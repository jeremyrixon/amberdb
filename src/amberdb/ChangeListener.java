package amberdb;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
    
    Set<Long> visitedVertices;
    
    public Map<Long, VersionedVertex> changedWorks(Long firstTxn, Long lastTxn) {
        
        visitedVertices = new HashSet<>();
        
        s("LOADING graph: Txn-" + firstTxn + " -> Txn-" + lastTxn);
        graph.loadTransactionGraph(firstTxn, lastTxn, true);

        Map<Long, VersionedVertex> changedItems = new HashMap<>();
        int numVerticesProcessed = 0;

        loop: for (VersionedVertex v : graph.getVertices()) {
            
            numVerticesProcessed++;

            if (hasBeenVisited(v)) continue;
            
            TVertexDiff tvd = v.getDiff(firstTxn, lastTxn);
            s("----------------------------\n" + tvd );
            if (tvd.isTransient()) {
                s("\t FROM " + v);                
            }
            
            if (tvd.getTransition() == TTransition.UNCHANGED) continue loop;
            
            // The type attribute of an NLA vertex shouldn't change
            String type = (String) getUnchangedProperty(tvd, "type");
            if (type == null) {
                log.warning("Skipping Object. Type has changed :" + tvd.toString());
                addToVisited(v);
                continue loop;
            }
                
            // walk up the tree to a work with an Item bibLevel
            VersionedVertex travel = v; // this vertex travels to the item level work 
                
            
            // object is a file description
            switch (type) { 
            case "Description": case "IPTC": case "GeoCoding": case "CameraData":
                s("DESCRIPTION");
                // Get the file
                Iterator<VersionedVertex> files = travel.getVertices(Direction.OUT, "descriptionOf").iterator();
                if (!files.hasNext()) {
                    log.warning("Skipping Object. Description has no file :" + tvd.toString());
                    continue loop;
                }
                travel = files.next(); // travel should be a file
                if (visited(travel)) continue;
                type = "File";
            }

            
            // travel object is a file
            switch (type) {
            case "File": case "ImageFile": case "SoundFile":
                s("FILE");
                // Get the copy
                Iterator<VersionedVertex> copies = travel.getVertices(Direction.OUT, "isFileOf").iterator();
                if (!copies.hasNext()) {
                    log.warning("Skipping Object. File has no copy :" + tvd.toString());
                    continue;
                }
                travel = copies.next(); // travel should be a copy
                if (visited(travel)) continue;
                type = "Copy";
            }
            
            
            // travel object is a copy
            switch (type) {
            case "Copy":
                s("COPY");
                // Get the work
                Iterator<VersionedVertex> works = travel.getVertices(Direction.OUT, "isCopyOf").iterator();
                if (!works.hasNext()) {
                    log.warning("Skipping Object. Copy has no work :" + tvd.toString());
                    continue;
                }
                travel = works.next(); // travel should be a copy
                if (visited(travel)) continue;
            }
               
            
            // travel object is now a work of some sort (Work, Page, Section)
            s("processing WORK " + travel);
                
            // travel up using bibLevel and isPartOf relationship
            TVertexDiff tDiff = travel.getDiff(firstTxn, lastTxn);
            String bibLevel = (String) getUnchangedProperty(tDiff, "bibLevel");
            if (bibLevel == null) {
                log.warning("Skipping Object. Work bibLevel has changed or is null:" + tDiff.toString() + "\n** " + travel.toString());
                addToVisited(travel);
                continue;
            }
                
            int loopCount = 0; // avoiding circular references
            while (loopCount < 5 && !bibLevel.equals("Item")) {
                loopCount++;
                s("climbing Work tree");    
                Iterator<VersionedVertex> works = travel.getVertices(Direction.OUT, "isPartOf").iterator();
                if (!works.hasNext()) {
                    log.warning("Skipping Object. Work is not a part of anything :" + travel.toString());
                    break;
                }
                travel = works.next(); 

                s("Checking Work " + travel);
                if (visited(travel)) continue loop;

                // @TODO only going up one parent path currently - will need to fix to go up to multiple parents eventually

                bibLevel = (String) getUnchangedProperty(travel.getDiff(firstTxn, lastTxn), "bibLevel");
                if (bibLevel == null) {
                    log.warning("Skipping Object. Work bibLevel has changed or is null:" + tDiff.toString());
                    break;
                }
            }
                
            if (bibLevel != null && bibLevel.equals("Item") && travel != null) {
                s("##### ADDING WORK ##### " + travel.getId());
                changedItems.put(travel.getId(), travel);
                addToVisited(travel);
            } else {
                s("***** Not adding work - hell knows why " + travel);
            }
        }

        log.info("---------- number of vertices processed was " + numVerticesProcessed);
        return changedItems;
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
            Object[] oArray = (Object[]) obj;
            if (oArray.length == 1) {
                return oArray[0];
            }
            return null;
        }
        return obj;
    }
        
    
    private boolean visited(VersionedVertex v) {
        if (visitedVertices.contains(v.getId())) {
            s("---- ALREADY PROCESSED ----\n");
            return true;
        } else {
            visitedVertices.add(v.getId());
            return false;
        }
    }
    
    
    private void addToVisited(VersionedVertex v) {
        visitedVertices.add(v.getId());
    }
    
    
    private boolean hasBeenVisited(VersionedVertex v) {
        return visitedVertices.contains(v.getId());
    }
    
    
    private void s(String s) {
        System.out.println(s);
    }
}
