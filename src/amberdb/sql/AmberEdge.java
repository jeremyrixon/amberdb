package amberdb.sql;

import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class AmberEdge extends AmberElement implements Edge {

    String label; 
    long inVertexId;
    long outVertexId;
    int edgeOrder = 0;

    public static final String SORT_ORDER_PROPERTY_NAME = "edge-order";
    
    // This constructor for access
    public AmberEdge(long id, Long txnStart, Long txnEnd, Boolean txnOpen, 
            String properties, long outVertexId, long inVertexId, String label, int edgeOrder) {
        
        id(id);
        properties(properties);
        this.label = label;
        this.inVertexId = inVertexId;
        this.outVertexId = outVertexId;
        this.edgeOrder = edgeOrder;
        
        txnStart(txnStart);
        txnEnd(txnEnd);
        txnOpen(txnOpen);
    }    

    // This constructor for creation
    public AmberEdge(AmberGraph graph, String properties, long outVertexId, long inVertexId, String label) {

        graph(graph);
        properties(properties);
        this.label = label;
        this.inVertexId = inVertexId;
        this.outVertexId = outVertexId;
        
        txnStart(graph.currentTxnId());
        txnOpen(true);
        
        long id = graph.getDao().createEdge(txnStart(), properties, outVertexId, inVertexId, label);
        id(id);
    }    
    
    @Override
    public Object getId() {
        return id();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String propertyName) {
        
        // get special sorting property 
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) (Integer) edgeOrder;
        }
        
        return super.getProperty(propertyName);
    }

    @Override
    public Set<String> getPropertyKeys() {
        
        // The property keys returned do not include the
        // special sorting property name as it breaks 
        // the edge test suite.
        Set<String> properties = super.getPropertyKeys();
        //properties.add(SORT_ORDER_PROPERTY_NAME);
        
        return properties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T removeProperty(String propertyName) {
        
        // you cannot remove the special sorting
        // property this method just returns it
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) (Integer) edgeOrder;
        }
        
        T prop = super.removeProperty(propertyName);
        graph().getDao().updateEdgeProperties(id(), properties());
        
        // update index
        graph().getDao().removeEdgePropertyIndexEntry(id(), propertyName);
        
        return prop;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*|label")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        if (!(value instanceof String || value instanceof Integer || value instanceof Boolean || value instanceof Double)) {
            throw new IllegalArgumentException("Illegal property type [" + value.getClass() + "].");
        }
        if (!(value instanceof Integer) && propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            throw new IllegalArgumentException(SORT_ORDER_PROPERTY_NAME + " property type must be Integer, was [" + value.getClass() + "].");
        }
        
        // set special sorting property
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            edgeOrder = (Integer) value;
            graph().getDao().updateEdgeOrder(id(), edgeOrder);
            return;
        }
        
        super.setProperty(propertyName, value);
        graph().getDao().updateEdgeProperties(id(), properties());
        
        // update index
        graph().getDao().setEdgePropertyIndexEntry(id(), propertyName, value);
        
        return;
    }    

    @Override
    public void remove() {
        graph().getDao().removeEdge(id());
        graph().getDao().removeEdgePropertyIndexEntries(id());
    }

    @Override
    public String getLabel() {
        return label.toString();
    }

    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        
        // argument guard
        if (Direction.BOTH == direction) {
            throw new IllegalArgumentException("Can only get a vertex from a single direction");
        }
        AmberVertex jv = null;
        if (direction == Direction.IN) {
            jv = graph().getDao().findVertexByInEdge(id());
        } else if (direction == Direction.OUT) {
            jv = graph().getDao().findVertexByOutEdge(id());
        }
        
        if (jv == null) return null;
        jv.graph(graph());
        return jv;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Long) id()).hashCode();
        result = prime * result + (int) (inVertexId ^ (inVertexId >>> 32));
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((properties() == null) ? 0 : properties().hashCode());
        result = prime * result + (int) (outVertexId ^ (outVertexId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        AmberEdge other = (AmberEdge) obj;
        if (id() != other.id()) return false;
        if (inVertexId != other.inVertexId) return false;
        if (outVertexId != other.outVertexId) return false;
        
        if (label == null) {
            if (other.label != null) return false;
        } else if (!label.equals(other.label)) return false;

        if (properties() == null) {
            if (other.properties() != null) return false;
        } else if (!properties().equals(other.properties())) return false;
        
        return true;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("edge id:").append(id())
        .append(" label:").append(label)
        .append(" out:").append(outVertexId)
        .append(" in:").append(inVertexId)
        .append(" properties:").append(properties())
        .append(" start:").append(txnStart())
        .append(" end:").append(txnEnd())
        .append(" open:").append(txnOpen())
        .append(" order:").append(edgeOrder);
        return sb.toString();
    }    
}
