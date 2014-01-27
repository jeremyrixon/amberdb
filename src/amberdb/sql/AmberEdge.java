package amberdb.sql;

import java.util.Map;

public class AmberEdge extends BaseEdge {

    
    Long txnStart;
    Long txnEnd;
    Integer order;

    public static final String SORT_ORDER_PROPERTY_NAME = "edge-order";
    
    
    public AmberEdge(Long id, String label, AmberVertex inVertex, 
            AmberVertex outVertex, Map<String, Object> properties, 
            AmberGraph graph, Long txnStart, Long txnEnd, Integer order) {
        
        super(id, label, (BaseVertex) inVertex, (BaseVertex) outVertex, properties, (BaseGraph) graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
        this.order = order;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd)
          .append(" order:" ).append(order);
        return super.toString() + sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String propertyName) {
        
        // get special sorting property
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) order;
        }
        return super.getProperty(propertyName);
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T removeProperty(String propertyName) {
        
        // you cannot remove the special sorting
        // property this method just returns it
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) order;
        }
        return super.removeProperty(propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        if (!(value instanceof Integer) && propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            throw new IllegalArgumentException(SORT_ORDER_PROPERTY_NAME +
                    " property type must be Integer, was [" + value.getClass() + "].");
        }
        
        // set special sorting property
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            order = (Integer) value;
            return;
        }     
        super.setProperty(propertyName, value);
    }     
}
