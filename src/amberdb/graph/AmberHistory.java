package amberdb.graph;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;

import amberdb.util.AmberModelTypes;
import amberdb.version.TEdgeDiff;
import amberdb.version.TId;
import amberdb.version.TTransition;
import amberdb.version.TVertexDiff;
import amberdb.version.VersionedEdge;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;


public class AmberHistory {

    
    private VersionedGraph vGraph;
    
    
    public AmberHistory(AmberGraph graph) {
        vGraph = new VersionedGraph(graph.dbi());
    }

    public VersionedGraph loadChangedGraphForPeriod(Date startDate, Date endDate) {
        List<Long> txnsBetween = getTxnsBetween(startDate, endDate);
        return loadChangedGraphForPeriod(txnsBetween.get(0), txnsBetween.get(txnsBetween.size()-1));
    }
    
    public VersionedGraph loadChangedGraphForPeriod(Long txn1, Long txn2) {
        vGraph.loadTransactionGraph(txn1, txn2, true);
        return vGraph;
    }


    /**
     * @return the set of versioned vertices present in the given versioned
     *         graph that have changed over the txn period
     */
    public Set<VersionedVertex> getChangedVertices(VersionedGraph graph, Long txn1, Long txn2) {
        Set<VersionedVertex> changedVertices = new HashSet<>();
        for (VersionedVertex v : vGraph.getVertices()) {
            TVertexDiff diff = v.getDiff(txn1, txn2);
            if (diff.getTransition() != TTransition.UNCHANGED) {
                changedVertices.add(v);
            }
        }
        return changedVertices;
    }    

    
    /**
     * @return the set of versioned edges present in the given versioned
     *         graph that have changed over the txn period
     */
    public Set<VersionedEdge> getChangedEdges(VersionedGraph graph, Long txn1, Long txn2) {
        Set<VersionedEdge> changedEdges = new HashSet<>();
        for (VersionedEdge e : vGraph.getEdges()) {
            TEdgeDiff diff = e.getDiff(txn1, txn2);
            if (diff.getTransition() != TTransition.UNCHANGED) {
                changedEdges.add(e);
            }
        }
        return changedEdges;
    }    

    
    protected Map<Long, String> getModifiedObjectIds(List<Long> txns) {

        // Get the txns involved
        Map<Long, String> modifiedIds = new HashMap<>();
        if (txns.size() == 0) {
            return modifiedIds;
        }
        
        // set our transaction period
        Long txn1 = txns.get(0) - 1;
        Long txn2 = txns.get(txns.size()-1) + 1; // > than last txn
        
        // load the graph
        VersionedGraph vGraph = loadChangedGraphForPeriod(txn1, txn2);
        Set<VersionedVertex> vertices = getChangedVertices(vGraph, txn1, txn2);
        Set<VersionedEdge> edges = getChangedEdges(vGraph, txn1, txn2);

        for (VersionedVertex v : vertices) {
            TVertexDiff diff = v.getDiff(txn1, txn2);
            TTransition change = diff.getTransition();
            TId id = diff.getId()[0];
            
            // Deletion trumps all changes - don't replace
            if (modifiedIds.get(id) != null
                    && modifiedIds.get(id).equals(TTransition.DELETED.toString())) {
                continue;
            }
            modifiedIds.put(id.getId(), change.toString());
        }
        
        // find and return the vertices for any changed edge
        Long id;
        for (VersionedEdge e : edges) {
            id = (Long) e.getVertex(Direction.IN).getId();
            if (!modifiedIds.containsKey(id) && !(TTransition.DELETED.toString().equals(modifiedIds.get(id)))) {
                modifiedIds.put(id, TTransition.MODIFIED.toString());
            }
            id = (Long) e.getVertex(Direction.OUT).getId();
            if (!modifiedIds.containsKey(id) && !(TTransition.DELETED.toString().equals(modifiedIds.get(id)))) {
                modifiedIds.put(id, TTransition.MODIFIED.toString());
            }
        }
        
        return modifiedIds;
    }
    
    
    protected Map<Long, String> getModifiedWorkIds(List<Long> transactions) {
        Map<Long, String> modifiedWorks = new HashMap<>();
        Map<Long, String> modifiedObjs = getModifiedObjectIds(transactions);

        for (Long id : modifiedObjs.keySet()) {
            Set<VersionedVertex> works = getWorksForObject(id, new HashSet<VersionedVertex>());
            String changeToObj = modifiedObjs.get(id);
            for (VersionedVertex v : works) {
                Long workId = v.getId();
                String changeToWork = modifiedWorks.get(workId);
                
                // don't overwrite a delete with anything else 
                if (TTransition.DELETED.toString().equals(changeToWork)) { 
                    continue;
                }

                // only write as a delete if it's the work itself that has been deleted
                if (workId.equals(id) && changeToObj.equals(TTransition.DELETED.toString())) {
                    changeToWork = TTransition.DELETED.toString();
                } else {
                    changeToWork = TTransition.MODIFIED.toString();
                }
                
                modifiedWorks.put(workId, changeToWork);
            }
        }
        return modifiedWorks;
    }

    public Map<Long, String> getModifiedWorkIds(Date from) {
        return getModifiedWorkIds(getTxnsSince(from));
    }

    public Map<Long, String> getModifiedWorkIds(Date from, Date to) {
        return getModifiedWorkIds(getTxnsBetween(from, to));
    }

    public Map<Long, String> getModifiedObjectIds(Date from) {
        return getModifiedObjectIds(getTxnsSince(from));
    }

    public Map<Long, String> getModifiedOBjectIds(Date from, Date to) {
        return getModifiedObjectIds(getTxnsBetween(from, to));
    }
    

    private Set<VersionedVertex> getWorksForObject(Long id, Set<VersionedVertex> works) {
        
        VersionedVertex v = vGraph.getVertex(id);
        String type = v.getAtTxnOrLast(Long.MAX_VALUE).getProperty("type");
        
        if (AmberModelTypes.isDescription(type)) {
            for (VersionedVertex parent : v.getVertices(Direction.OUT, "descriptionOf")) {
                getWorksForObject(parent.getId(), works);
            }        
        } else if (AmberModelTypes.isFile(type)) {
            for (VersionedVertex parent : v.getVertices(Direction.OUT, "isFileOf")) {
                getWorksForObject(parent.getId(), works);
            }        
        } else if (AmberModelTypes.isCopy(type)) {
            for (VersionedVertex parent : v.getVertices(Direction.OUT, "isCopyOf")) {
                getWorksForObject(parent.getId(), works);
            }        
        } else if (AmberModelTypes.isWork(type)) {
            works.add(v);
        } 
        return works;
    }
    
    
    public List<Long> getTxnsSince(Date time) {
        return vGraph.dao().getTransactionsSince(time.getTime());
    }

    public List<Long> getTxnsBetween(Date startTime, Date endTime) {
        return vGraph.dao().getTransactionsBetween(startTime.getTime(), endTime.getTime());
    }

    
    public VersionedGraph getVersionedGraph() {
        return vGraph;
    }
}
