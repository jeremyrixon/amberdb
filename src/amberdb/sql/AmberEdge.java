package amberdb.sql;

import java.util.Set;

import amberdb.sql.dao.EdgeDao;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class AmberEdge implements Edge {

    private long id;

    public static final String SORT_ORDER_PROPERTY_NAME = "edge-order";

    /**
     * Change the id of this edge
     * @param newId
     */
    protected void changeId(long newId) {
        dao().changeEdgeId(id, newId);
        dao().changeEdgePropertyIds(id, newId);
        id = newId;
    }
    /**
     * Point this edge object at a different edge instance
     * @param id
     */
    public void addressId(long id) {
        this.id = id;
    }

    private AmberGraph graph;
    protected void setGraph(AmberGraph graph) {
        this.graph = graph;
    }
    
    private EdgeDao dao() {
        return graph.edgeDao();
    }
    
    // this constructor for getting an edge from amber
    public AmberEdge(AmberGraph graph, long id) {
        
        if (graph == null) throw new RuntimeException("graph cannot be null");

        setGraph(graph);
        this.id = id;
    }
    
    // this constructor for creating a new edge in session
    public AmberEdge(AmberGraph graph, long outVertexId, long inVertexId, String label) {
        
        if (graph == null) throw new RuntimeException("graph cannot be null");

        setGraph(graph);
        this.id = graph.newPersistentId();
        dao().insertEdge(id, null, null, outVertexId, inVertexId, label, 0, State.NEW.toString());
    }

    public Long getTxnStart() {
        return dao().getEdgeTxnStart(id);
    }

    public void setTxnStart(Long txnStart) {
        dao().setEdgeTxnStart(id, txnStart);
    }
    
    public void setState(State state) { 
        dao().setEdgeState(id, state.toString());
    }
    
    public State getState() { 
        return State.valueOf(dao().getEdgeState(id));
    }

    public Long getInVertexId() {
        return dao().getInVertexId(id);
    }

    public Long getOutVertexId() {
        return dao().getOutVertexId(id);
    }

    public Integer getEdgeOrder() {
        return dao().getEdgeOrder(id);
    }
    
    @Override
    public Object getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String propertyName) {
        
        // get special sorting property 
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) (Integer) dao().getEdgeOrder(id);
        }
        
        AmberProperty property = dao().getProperty(id, propertyName);
        if (property == null) return null;
        return (T) property.getValue();
    }

    @Override
    public Set<String> getPropertyKeys() {
        
        // The property keys returned do not include the
        // special sorting property name as it breaks 
        // the edge test suite.
        
        //properties.add(SORT_ORDER_PROPERTY_NAME);
        
        return dao().getPropertyKeys(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T removeProperty(String propertyName) {
        
        // you cannot remove the special sorting
        // property this method just returns it
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) (Integer) dao().getEdgeOrder(id);
        }
        
        T prop = getProperty(propertyName);
        dao().removeProperty(id, propertyName);

        // set state as modified (MOD) if it was originally unaltered (AMB)
        if (dao().getEdgeState(id).equals(State.AMB.toString())) {
            dao().setEdgeState(id, State.MOD.toString());
        }
        
        if (graph.autoCommit) graph.commitToPersistent("edge removeProperty");
        return prop;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*|label")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        if (!(value instanceof Integer || value instanceof String || 
              value instanceof Boolean || value instanceof Double ||
              value instanceof Long    || value instanceof Float)) {
            throw new IllegalArgumentException("Illegal property type [" + value.getClass() + "].");
        }
        if (!(value instanceof Integer) && propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            throw new IllegalArgumentException(SORT_ORDER_PROPERTY_NAME + 
                    " property type must be Integer, was [" + value.getClass() + "].");
        }
        
        // set special sorting property 
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            dao().setEdgeOrder(id, (Integer) value);
        } else {
            dao().removeProperty(id, propertyName);
            dao().setProperty(id, propertyName, DataType.forObject(value), AmberProperty.encodeBlob(value));
        }

        // set state as modified (MOD) if it was originally unaltered (AMB)
        if (dao().getEdgeState(id).equals(State.AMB.toString())) {
            dao().setEdgeState(id, State.MOD.toString());
        }
        
        if (graph.autoCommit) graph.commitToPersistent("edge setProperty");
    }    

    @Override
    public void remove() {

        // remove the actual record if it is new
        if (dao().getEdgeState(id).equals(State.NEW.toString())) {
            dao().removeEdge(id);
        } else {
            // Otherwise, mark as deleted so we can preserve this in amber
            dao().setEdgeState(id, State.DEL.toString());
        }
        dao().removeEdgeProperties(id);
        return;
    }

    @Override
    public String getLabel() {
        return dao().getEdgeLabel(id);
    }

    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        
        // argument guard
        if (Direction.BOTH == direction) {
            throw new IllegalArgumentException("Can only get a vertex from a single direction");
        }
        
        AmberVertex vertex = null;
        if (direction == Direction.IN) {
            vertex = (AmberVertex) graph.getVertex(dao().getInVertexId(id));
        } else if (direction == Direction.OUT) {
            vertex = (AmberVertex) graph.getVertex(dao().getOutVertexId(id));
        }
        return vertex;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Long) id).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        AmberEdge other = (AmberEdge) obj;
        if (id != (Long) other.getId()) return false;

        return true;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("edge id:").append(id)
        .append(" label:").append(dao().getEdgeLabel(id))
        .append(" out:").append(dao().getOutVertexId(id))
        .append(" in:").append(dao().getInVertexId(id))
        .append(" start:").append(dao().getEdgeTxnStart(id))
        .append(" end:").append(dao().getEdgeTxnEnd(id))
        .append(" order:").append(dao().getEdgeOrder(id))
        .append(" state:").append(dao().getEdgeState(id));
        return sb.toString();
    }
    
    private void s(String s) {
        System.out.println(s);
    }
}
