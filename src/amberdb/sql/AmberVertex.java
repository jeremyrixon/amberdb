package amberdb.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import amberdb.sql.dao.VertexDao;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;

public class AmberVertex implements Vertex {

    private long id;
    /**
     * Change the id of this vertex
     * @param newId
     */
    protected void changeId(long newId) {
        dao().begin();
        dao().changeVertexId(id, newId);
        dao().changeVertexPropertyIds(id, newId);
        dao().changeInVertexIds(id, newId);
        dao().changeOutVertexIds(id, newId);
        id = newId;
        dao().commit();
    }
    /**
     * Point this vertex object at a different vertex instance
     * @param id
     */
    public void addressId(long id) {
        this.id = id;
    }

    private AmberGraph graph;
    protected void setGraph(AmberGraph graph) {
        this.graph = graph;
    }
    public AmberGraph getGraph() {
        return graph;
    }
    private VertexDao dao() {
        return graph.vertexDao();
    }
    
    // this constructor for getting a vertex from amber
    public AmberVertex(AmberGraph graph, long id) {
        
        if (graph == null) throw new RuntimeException("graph cannot be null");
        
        setGraph(graph);
        this.id = id;
    }

    // this constructor for creating a new vertex in session
    public AmberVertex(AmberGraph graph) {
        
        if (graph == null) throw new RuntimeException("graph cannot be null");
        
        setGraph(graph);
        this.id = graph.newPersistentId();
        dao().insertVertex(id, null, null, State.NEW.toString());
    }

    public Long getTxnStart() {
        return dao().getVertexTxnStart(id);
    }
    
    public void setTxnStart(Long txnStart) {
        dao().setVertexTxnStart(id, txnStart);
    }
    
    public void setState(State state) { 
        dao().setVertexState(id, state.toString());
    }
    
    public State getState() { 
        return State.valueOf(dao().getVertexState(id));
    }
    
    @Override
    public Object getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String propertyName) {
        AmberProperty property = dao().getProperty(id, propertyName);
        if (property == null) return null;
        return (T) property.getValue();
    }

    @Override
    public Set<String> getPropertyKeys() {
        return dao().getPropertyKeys(id);
    }

    @Override
    public void remove() {
        
        for (Edge e: getEdges(Direction.BOTH)) {
            AmberEdge edge = (AmberEdge) e;
            edge.remove();
        }

        // remove the actual record from session if it is new
        if (dao().getVertexState(id).equals(State.NEW.toString())) {
            dao().removeVertex(id);
        } else {
            // Otherwise, mark as deleted so we can preserve this in amber
            dao().setVertexState(id, State.DEL.toString());
        }
        dao().removeVertexProperties(id);
        return;
    }

    @Override
    public <T> T removeProperty(String propertyName) {
        T prop = getProperty(propertyName);
        dao().removeProperty(id, propertyName);

        // set state as modified (MOD) if it was originally unaltered (AMB)
        if (dao().getVertexState(id).equals(State.AMB.toString())) {
            dao().setVertexState(id, State.MOD.toString());
        }

        return prop;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        if (!(value instanceof Integer || value instanceof String || 
              value instanceof Boolean || value instanceof Double ||
              value instanceof Long    || value instanceof Float)) {
            throw new IllegalArgumentException("Illegal property type [" + value.getClass() + "].");
        }
        
        dao().removeProperty(id, propertyName);
        dao().setProperty(id, propertyName, DataType.forObject(value), AmberProperty.encodeBlob(value));

        // set state as modified (MOD) if it was originally unaltered (AMB)
        if (dao().getVertexState(id).equals(State.AMB.toString())) {
            dao().setVertexState(id, State.MOD.toString());
        }
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex) {
        
        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");
        
        AmberEdge edge = new AmberEdge(graph, this.id, (long) inVertex.getId(), label);
        return edge;
    }

    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {

        List<AmberEdge> sessionEdges = Lists.newArrayList(graph.findSessionEdgesForVertex(id, direction, labels));

        // get ids for all edges found to exclude from persistence search
        List<Long> sessionEdgeIds = new ArrayList<Long>();
        for (AmberEdge e : sessionEdges) {
            sessionEdgeIds.add((Long) e.getId());
        }

        // load any remaining edges from persistent into session (shouldn't overwrite present edges)
        List<AmberEdge> persistentEdges = graph.loadPersistentEdges(this, sessionEdgeIds, direction, labels);
        List<Edge> edges = new ArrayList<Edge>();

        // remove deleted edges (they'll only be in the session)
        for (AmberEdge edge : sessionEdges) {
            if (edge.getState() != State.DEL) {
                edges.add(edge);
            }
        }
        edges.addAll(persistentEdges);
        return edges;
    }

    /* 
     * To conform to Blueprint test suite this method can now return the same vertex multiple times
     * in the returned iterable.
     *  
     * (non-Javadoc)
     * 
     * @see
     * com.tinkerpop.blueprints.Vertex#getVertices(com.tinkerpop.blueprints.
     * Direction, java.lang.String[])
     */
    
    @Override
    public Iterable<Vertex> getVertices(Direction direction, String... labels) {

        // get the edges
        Iterable<Edge> edges = getEdges(direction, labels);

        for (Edge e : edges) {
            AmberEdge ae = (AmberEdge) e;
        }
        
        List<Long> vertexIds = new ArrayList<Long>();
        for (Edge e : edges) {
            AmberEdge ae = (AmberEdge) e;
            if (ae.getInVertexId() == id) {
                vertexIds.add(ae.getOutVertexId());
            } else {
                vertexIds.add(ae.getInVertexId());
            }
        } 

        // pull all the vertices into the session
        List<AmberVertex> vs = graph.getVerticesById(vertexIds); 
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (AmberVertex v : vs) {
            if (v == null) continue;
            while (vertexIds.remove((Long) v.getId()) == true) {
                vertices.add(v);
            }
        }
        return vertices;
    }

    @Override
    public VertexQuery query() {
        return new DefaultVertexQuery(this);
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AmberVertex other = (AmberVertex) obj;
        if (id != (Long) other.getId())
            return false;
        return true;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("vertex id:").append(id)
        .append(" start:").append(dao().getVertexTxnStart(id))
        .append(" end:").append(dao().getVertexTxnEnd(id))
        .append(" state:").append(dao().getVertexState(id).toString());
        return sb.toString();
    }
    
    private void s(String s) {
        graph.log.info(s);
    }
    
    public Long getTxnEnd() {
        return dao().getVertexTxnEnd(id);
    }
}
