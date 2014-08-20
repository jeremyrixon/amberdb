package amberdb.graph;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amberdb.version.TTransition;
import amberdb.version.TVertexDiff;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;


public class AmberHistory {

    
    private VersionedGraph vGraph;
    
    
    public AmberHistory(AmberGraph graph) {
        vGraph = new VersionedGraph(graph.dbi());
    }
    
    
    /**
     * 
     * @param time the time after which altered vertices and edges should be selected
     * 
     * @return a graph holding modified vertices and edges
     */
    public Set<TVertexDiff> getModifiedVerticesSince(Date time) {
        vGraph.clear();
        Set<TVertexDiff> changedVertices = new HashSet<>();
        
        List<Long> txnsSince = getTxnsSince(time);
        if (txnsSince.size() == 0) return changedVertices;
        
        Long txn1 = txnsSince.get(0) - 1;
        s("" + txn1);
        Long txn2 = txnsSince.get(txnsSince.size() - 1) + 1; // greater than
                                                             // latest txn
        s("" + txn2);

        vGraph.loadTransactionGraph(txn1, txn2, true);
        for (VersionedVertex v : vGraph.getVertices()) {
            s("" + v);
            TVertexDiff diff = v.getDiff(txn1, txn2);
            if (diff.getTransition() != TTransition.UNCHANGED) {
                s("yep");
                changedVertices.add(diff);
            }
        }

        return changedVertices;
    }


    public List<Long> getTxnsSince(Date time) {
        return vGraph.dao().getTransactionsSince(time.getTime());
    }

    
    public VersionedGraph getVersionedGraph() {
        return vGraph;
    }
    
    
    private void s(String s) {
        System.out.println(s);
    }
}
