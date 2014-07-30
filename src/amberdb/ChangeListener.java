package amberdb;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.tinkerpop.blueprints.Direction;

import amberdb.graph.AmberGraph;
import amberdb.version.TElementDiff;
import amberdb.version.TTransition;
import amberdb.version.TVertexDiff;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;


public class ChangeListener {

    private VersionedGraph graph;
    Logger log = Logger.getLogger(this.getClass().getName());
    
    
    public ChangeListener(AmberGraph graph) {
        this.graph = new VersionedGraph(graph.dbi());
    }
    
    
    public Set<VersionedVertex> changedWorks(Long firstTxn, Long lastTxn) {
        
        log.info("loading graph for txn-" + firstTxn + " to txn-" + lastTxn);
        graph.loadTransactionGraph(firstTxn, lastTxn);
        
        Set<VersionedVertex> changedWorks = new HashSet<>();
        
        int vNum = 0;
        for (VersionedVertex v : graph.getVertices()) {

            vNum++;
            
            TVertexDiff tvd = v.getDiff(firstTxn, lastTxn);
            TTransition change = tvd.getTransition(); 
            if (change != TTransition.UNCHANGED) {
                
                // The type attribute of an NLA vertex shouldn't change
                String type = (String) getUnchangedProperty(tvd, "type");
                if (type == null) {
                    log.warning("Skipping Object. Type has changed :" + tvd.toString());
                    continue;
                }
                
                // walk up the tree to a work with an Item bibLevel
                VersionedVertex travel = v; // this vertex travels to the item level work 
                
                // object is a file description
                switch (type) {
                case "Description":
                case "IPTC":
                case "GeoCoding":
                case "CameraData":
                    // Get the file
                    Iterator<VersionedVertex> files = travel.getVertices(Direction.OUT, "descriptionOf").iterator();
                    if (!files.hasNext()) {
                        log.warning("Skipping Object. Description has no file :" + tvd.toString());
                        continue;
                    }
                    travel = files.next(); // travel should be a file
                    type = "File";
                }
                
                // travel object is a file
                switch (type) {
                case "File":
                case "ImageFile":
                case "SoundFile":
                    // Get the copy
                    Iterator<VersionedVertex> copies = travel.getVertices(Direction.OUT, "isFileOf").iterator();
                    if (!copies.hasNext()) {
                        log.warning("Skipping Object. File has no copy :" + tvd.toString());
                        continue;
                    }
                    travel = copies.next(); // travel should be a copy
                    type = "Copy";
                }
                
                // travel object is a copy
                switch (type) {
                case "Copy":
                    // Get the work
                    Iterator<VersionedVertex> works = travel.getVertices(Direction.OUT, "isCopyOf").iterator();
                    if (!works.hasNext()) {
                        log.warning("Skipping Object. Copy has no work :" + tvd.toString());
                        continue;
                    }
                    travel = works.next(); // travel should be a copy
                }
                
                // travel object is now a work of some sort (Work, Page, Section)
                
                // travel up using bibLevel and isPartOf relationship
                TVertexDiff tDiff = travel.getDiff(firstTxn, lastTxn);
                String bibLevel = (String) getUnchangedProperty(tDiff, "bibLevel");
                if (bibLevel == null) {
                    log.warning("Skipping Object. Work bibLevel has changed :" + tDiff.toString());
                    continue;
                }
                
                int loopCount = 0;
                while (loopCount < 5 && !bibLevel.equals("Item")) {
                    loopCount++;
                    
                    Iterator<VersionedVertex> works = travel.getVertices(Direction.OUT, "isPartOf").iterator();
                    if (!works.hasNext()) {
                        log.warning("Skipping Object. Work is not a part of anything :" + tvd.toString());
                        break;
                    }
                    travel = works.next(); 
                    // @TODO only going up one parent path currently - will need to fix to go up to multiple parents eventually

                    bibLevel = (String) getUnchangedProperty(travel.getDiff(firstTxn, lastTxn), "bibLevel");
                    if (bibLevel == null) {
                        log.warning("Skipping Object. Work bibLevel has changed :" + tDiff.toString());
                        break;
                    }
                }
                
                if (bibLevel != null && bibLevel.equals("Item") && travel != null) {
                    changedWorks.add(travel);
                }
            }
        }
        
        return changedWorks;
    }
    
    /**
     * Return a property from a TDiff if it hasn't changed between versions. The
     * element can have been deleted or created, but the property cannot have 2
     * different values.
     * 
     * @param diff
     *            the element diff to get the property from
     * @param propertyName
     *            the property name
     * @return The property if it hasn't changed, null otherwise
     */
    private Object getUnchangedProperty(TElementDiff diff, String propertyName) {
        Object obj = diff.getProperty(propertyName);
        if (obj instanceof Object[]) {
            return null;
        }
        return obj;
    }
        
}
