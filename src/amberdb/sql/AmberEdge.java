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

    
    // this constructor for getting an edge from the db
    // properties must be fetched separately 
    public AmberEdge(long id, Long txnStart, Long txnEnd, long outVertexId, 
            long inVertexId, String label, int edgeOrder) {
        
        id(id);
        txnStart(txnStart);
        txnEnd(txnEnd);
        
        this.label = label;
        this.inVertexId = inVertexId;
        this.outVertexId = outVertexId;
        this.edgeOrder = edgeOrder;
    }    

    // This constructor for creating a new edge
    public AmberEdge(AmberGraph graph, long outVertexId, long inVertexId, String label) {

        graph(graph);

        id(dao().newId());
        this.label = label;
        this.inVertexId = inVertexId;
        this.outVertexId = outVertexId;

        // add to transaction as NEW element
        this.txnState(State.NEW);
        graph().currentTxn().addEdge(this);
    }    
    
    // This constructor for getting an edge from the transaction
    // need to add properties and graph
    public AmberEdge(Long id, Long txnStart, Long txnEnd, Long outVertexId, 
            Long inVertexId, String label, int edgeOrder, int state) {
        
        id(id);
        txnStart(txnStart);
        txnEnd(txnEnd);
        this.txnState(State.forOrdinal(state));
        
        this.label = label;
        this.inVertexId = inVertexId;
        this.outVertexId = outVertexId;
        this.edgeOrder = edgeOrder;
        
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
        return prop;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*|label")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        if (!(value instanceof Integer || value instanceof String || 
              value instanceof Boolean || value instanceof Double)) {
            throw new IllegalArgumentException("Illegal property type [" + value.getClass() + "].");
        }
        if (!(value instanceof Integer) && propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            throw new IllegalArgumentException(SORT_ORDER_PROPERTY_NAME + 
                    " property type must be Integer, was [" + value.getClass() + "].");
        }
        
        // set special sorting property 
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            edgeOrder = (Integer) value;
            graph().currentTxn().updateEdgeOrder(this);
            return;
        }
        
        super.setProperty(propertyName, value);
    }    

    @Override
    public void remove() {
        super.remove();
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
        
        AmberVertex vertex = null;
        if (direction == Direction.IN) {
            vertex = findVertex(inVertexId);
        } else if (direction == Direction.OUT) {
            vertex = findVertex(outVertexId);
        }
        return vertex;
    }

    private AmberVertex findVertex(long id) {

        // check transaction first then permanent data store
        AmberVertex vertex = graph().currentTxn().getVertex(inVertexId);
        if (vertex != null) {
            State state = vertex.txnState();
            if (state == State.DELETED) return null;
            if (state == State.NEW || state == State.READ || state == State.MODIFIED) {
                return vertex;
            }    
        }
        
        // no, so return whatever is found in the permanent data store
        return (AmberVertex) graph().getVertex(inVertexId);
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
        .append(" order:").append(edgeOrder)
        .append(" txn state:").append(txnState().toString());
        return sb.toString();
    }
}
