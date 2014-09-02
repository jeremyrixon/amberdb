package amberdb.graph;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;

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

    
    public Map<Long, String> getModifiedObjectIds(Date when) {

        // Get the txns involved
        Map<Long, String> modifiedIds = new HashMap<Long, String>();
        List<Long> txnsSince = getTxnsSince(when);
        if (txnsSince.size() == 0) {
            return modifiedIds;
        }
        
        // set our transaction period
        Long txn1 = txnsSince.get(0) - 1;
        Long txn2 = txnsSince.get(txnsSince.size()-1) + 1; // > than last txn
        
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
            id = e.getVertex(Direction.IN).getId();
            if (!(TTransition.DELETED.toString().equals(modifiedIds.get(id)))) {
                modifiedIds.put(id, TTransition.MODIFIED.toString());
            }
            id = e.getVertex(Direction.OUT).getId();
            if (!(TTransition.DELETED.toString().equals(modifiedIds.get(id)))) {
                modifiedIds.put(id, TTransition.MODIFIED.toString());
            }
        }
        
        return modifiedIds;
    }
    
    
    public List<Long> getTxnsSince(Date time) {
        return vGraph.dao().getTransactionsSince(time.getTime());
    }

    
    public VersionedGraph getVersionedGraph() {
        return vGraph;
    }
}
