package amberdb.version;


import java.util.HashMap;
import java.util.Map;

import amberdb.graph.AmberEdge;


public class TEdgeDiff extends TElementDiff {

    Map<String, Object[]> edgeAttributes = new HashMap<>();
    
    public TEdgeDiff(Long txn1, Long txn2, TEdge e1, TEdge e2) {
        super(txn1, txn2, e1, e2);
        
        // edge specific attributes
        switch (transition) {
        case NEW:
            edgeAttributes.put("label", new Object[] { e2.getLabel() });
            edgeAttributes.put("inVertexId", new Object[] { e2.inId });
            edgeAttributes.put("outVertexId", new Object[] { e2.outId });
            edgeAttributes.put(AmberEdge.SORT_ORDER_PROPERTY_NAME, new Object[] { e2.order });
            break;
            
        case DELETED:
            edgeAttributes.put("label", new Object[] { e1.getLabel() });
            edgeAttributes.put("inVertexId", new Object[] { e1.inId });
            edgeAttributes.put("outVertexId", new Object[] { e1.outId });
            edgeAttributes.put(AmberEdge.SORT_ORDER_PROPERTY_NAME, new Object[] { e1.order });
            break;

        case MODIFIED:
            edgeAttributes.put("label", (e1.getLabel().equals(e2.getLabel())) ? new Object[] { e1.getLabel() } : new Object[] { e1.getLabel(), e2.getLabel() });
            edgeAttributes.put("inVertexId", (e1.inId.equals(e2.inId)) ? new Object[] { e1.inId } : new Object[] { e1.inId, e2.inId });
            edgeAttributes.put("outVertexId", (e1.outId.equals(e2.outId)) ? new Object[] { e1.outId } : new Object[] { e1.outId, e2.outId });
            edgeAttributes.put(AmberEdge.SORT_ORDER_PROPERTY_NAME, (e1.order.equals(e2.order)) ? new Object[] { e1.order } : new Object[] { e1.order, e2.order });
            break;

        case UNCHANGED:
            TEdge e = null;
            if (e1 != null) e = e1;
            if (e2 != null) e = e2;
            if (e != null) {
                edgeAttributes.put("label", new Object[] { e.getLabel() });
                edgeAttributes.put("inVertexId", new Object[] { e.inId });
                edgeAttributes.put("outVertexId", new Object[] { e.outId });
                edgeAttributes.put(AmberEdge.SORT_ORDER_PROPERTY_NAME, new Object[] { e.order });
            } else {
                edgeAttributes.put("label", new Object[] { null });
                edgeAttributes.put("inVertexId", new Object[] { null });
                edgeAttributes.put("outVertexId", new Object[] { null });
                edgeAttributes.put(AmberEdge.SORT_ORDER_PROPERTY_NAME, new Object[] { null });
            }
            break;
        }
    }
    
    
    @Override
    public Object getProperty(String propertyName) throws TDiffException {
        switch (propertyName) {
        case "label" : 
        case "inVertexId" : 
        case "outVertexId" : 
        case AmberEdge.SORT_ORDER_PROPERTY_NAME :
            return edgeAttributes.get(propertyName);
        }
        return super.getProperty(propertyName);
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString() + "\n");
        for (String propName : edgeAttributes.keySet()) {
            sb.append(propName + ": ");
            Object[] objArr = (Object[]) getProperty(propName);
            sb.append((objArr[0] != null) ? objArr[0].toString() : "<null>"); 
            if (objArr.length > 1) {
                sb.append(" -> ");
                sb.append((objArr[1] != null) ? objArr[1].toString() : "<null>"); 
            }
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
