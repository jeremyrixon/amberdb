package amberdb.version;


import amberdb.graph.AmberEdge;


public class TEdgeDiff extends TElementDiff {

    
    public TEdgeDiff(Long txn1, Long txn2, TEdge e1, TEdge e2) {
        super(txn1, txn2, e1, e2);
        
        // edge specific attributes
        if (transition == TTransition.MODIFIED) {
            diffs.put("label", (e1.getLabel().equals(e2.getLabel())) ? new Object[] { e1.getLabel() } : new Object[] { e1.getLabel(), e2.getLabel() });
            diffs.put("inVertexId", (e1.inId.equals(e2.inId)) ? new Object[] { e1.inId } : new Object[] { e1.inId, e2.inId });
            diffs.put("outVertexId", (e1.outId.equals(e2.outId)) ? new Object[] { e1.outId } : new Object[] { e1.outId, e2.outId });
            diffs.put(AmberEdge.SORT_ORDER_PROPERTY_NAME, (e1.order.equals(e2.order)) ? new Object[] { e1.order } : new Object[] { e1.order, e2.order });
        }
    }
}
