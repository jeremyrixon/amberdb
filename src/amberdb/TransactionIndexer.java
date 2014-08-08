package amberdb;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.graph.AmberGraph;
import amberdb.version.TElementDiff;
import amberdb.version.TTransition;
import amberdb.version.TVertexDiff;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;


public class TransactionIndexer {

    private VersionedGraph graph;
    static final Logger log = LoggerFactory.getLogger(TransactionIndexer.class);
    
    
    public TransactionIndexer(AmberGraph graph) {
        this.graph = new VersionedGraph(graph.dbi());
    }
    
    public Set<Long>[] findObjectsToBeIndexed(Long startTxn, Long endTxn) {
        
        log.info("loading graph: Txn-" + startTxn + " -> Txn-" + endTxn);
        graph.loadTransactionGraph(startTxn, endTxn, true);
        
        // get our transaction copies (where their file or file descriptions have been affected)
        // we do this because it is really slow to find the copies for files and/or descriptions
        // by traversing the versioned graph :(
        log.info("Getting copies: Txn-" + startTxn + " -> Txn-" + endTxn);
        List<Long> additionalCopies = graph.getTransactionCopies(startTxn, endTxn);

        Set<Long> modifiedObjects = new HashSet<>();
        Set<Long> deletedObjects = new HashSet<>();
        
        Set<Long>[] changedObjectSets = new Set[] { modifiedObjects, deletedObjects}; 
        int numVerticesProcessed = 0;
        
        vertexLoop: for (VersionedVertex v : graph.getVertices()) {
            numVerticesProcessed++;

            TVertexDiff diff = v.getDiff(startTxn, endTxn);
            TTransition change = diff.getTransition();
            if (change == TTransition.UNCHANGED) continue vertexLoop;

            // The type attribute of an NLA vertex shouldn't change
            String type = (String) getUnchangedProperty(diff, "type");
            if (type == null) {
                log.warn("No type or type changed: " + diff);
                continue vertexLoop;
            }

            Long id = v.getId();
            
            // skip object if it is a file or file description as
            // we've collected those copies earlier
            switch (type) {
            case "Description":
            case "IPTC":
            case "GeoCoding":
            case "CameraData":
            case "File":
            case "ImageFile":
            case "SoundFile":
                continue vertexLoop;
            }

            // treat objects with a bibLevel of Item differently. For
            // deletions and access control changes we want to modify
            // the full item graph ie: delete all related records from
            // the index.
            String bibLevel = (String) getUnchangedProperty(diff, "bibLevel");
            if (bibLevel != null && bibLevel.equals("Item")) {
                // check a change in access conditions
                Object ac = diff.getProperty("internalAccessCondition");
                if (ac instanceof Object[]) {
                    Object[] cond = (Object[]) ac; 
                    log.info("Access condition has changed from " + cond[0] + " to " + cond[1]); 
                }
            }
            
            //log.info(change + " " + type + " " + id);
            switch (change) {
            case DELETED:
                deletedObjects.add(id);
                break;
            case NEW:
            case MODIFIED:
                modifiedObjects.add(id);
                break;
            }
        }
        log.info("Vertices processed: " + numVerticesProcessed);
        
        // merge the copy ids retrieved separately into the results
        for (Long id : additionalCopies) {
            modifiedObjects.add(id);
        } 

        // give deletion precedence over modification
        modifiedObjects.remove(deletedObjects);
        
        return changedObjectSets;
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
}
