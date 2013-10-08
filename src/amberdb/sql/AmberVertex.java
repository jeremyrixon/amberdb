package amberdb.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import amberdb.sql.State;
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
        this.id = graph.newSessionId();
        dao().insertVertex(id, null, null, State.NEW.toString());
        graph.addToNewVertices(this);
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
        
        // remove the actual record if it is new
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

        if (graph.autoCommit) graph.commitToPersistent("vertex removeProperty");
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

        if (graph.autoCommit) graph.commitToPersistent("vertex setProperty");
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

        // load edges from persistent into session (shouldn't overwrite present edges)
        if (graph.persistence) graph.loadPersistentEdges(this, direction, labels);

        // now just get from session - DELETED edges have been removed
        List<AmberEdge> sessionEdges = getSessionEdges(direction, labels);
        
        List<Edge> edges = new ArrayList<Edge>();
        edges.addAll(sessionEdges);
        
        return edges;
    }


    protected List<AmberEdge> getSessionEdges(Direction direction, String... labels) {

        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        List<AmberEdge> unfilteredEdges = new ArrayList<AmberEdge>();

        if (labels.length == 0) {

            if (direction == Direction.IN || direction == Direction.BOTH) {
                unfilteredEdges.addAll(Lists.newArrayList(dao().findInEdges(id)));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                unfilteredEdges.addAll(Lists.newArrayList(dao().findOutEdges(id)));
            }
            for (AmberEdge edge : unfilteredEdges) {
                if (edge.getState() != State.DEL) {
                    edges.add(edge);
                }
            }

        } else {
            for (String label : labels) {
                edges.addAll(getSessionEdges(direction, label));
            }
        }
        return edges;
    }

    public List<AmberEdge> getSessionEdges(Direction direction, String label) {

        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        List<AmberEdge> unfilteredEdges = new ArrayList<AmberEdge>();
        
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            unfilteredEdges.addAll(Lists.newArrayList(dao().findInEdges(id, label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            unfilteredEdges.addAll(Lists.newArrayList(dao().findOutEdges(id, label)));
        }
        for (AmberEdge edge: unfilteredEdges) {
            if (edge.getState() != State.DEL) {
                edges.add(edge);
            }
        }
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
        
        // load vertices from persistent into session (shouldn't overwrite present vertices)
        if (graph.persistence) graph.loadPersistentVertices(this, direction, labels);

        // now just get from session (contains DELETED edges)
        List<AmberVertex> sessionVertices = getSessionVertices(direction, labels);
        
        List<Vertex> vertices = new ArrayList<Vertex>();
        vertices.addAll(sessionVertices);
        
        return vertices;
    }

    protected List<AmberVertex> getSessionVertices(Direction direction, String... labels) {
        
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        List<AmberVertex> unfilteredVertices = new ArrayList<AmberVertex>();
        
        if (labels.length == 0) {

            if (direction == Direction.IN || direction == Direction.BOTH) {
                unfilteredVertices.addAll(Lists.newArrayList(dao().findInVertices(id)));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                unfilteredVertices.addAll(Lists.newArrayList(dao().findOutVertices(id)));
            }
            
            for (AmberVertex vertex : unfilteredVertices) {
                if (vertex.getState() != State.DEL) {
                    vertices.add(vertex);
                }
            }
            
        } else {
            for (String label : labels) {
                vertices.addAll(getSessionVertices(direction, label));
            }
        }
        return vertices;
    }

    public List<AmberVertex> getSessionVertices(Direction direction, String label) {
        
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        List<AmberVertex> unfilteredVertices = new ArrayList<AmberVertex>();

        if (direction == Direction.IN || direction == Direction.BOTH) {
            unfilteredVertices.addAll(Lists.newArrayList(dao().findInVertices(id, label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            unfilteredVertices.addAll(Lists.newArrayList(dao().findOutVertices(id, label)));
        }
        unfilteredVertices.removeAll(Collections.singleton(null));
        for (AmberVertex vertex: unfilteredVertices) {
            if (vertex.getState() != State.DEL) {
                vertices.add(vertex);
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
        System.out.println(s);
    }
}
