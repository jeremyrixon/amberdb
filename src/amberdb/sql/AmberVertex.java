package amberdb.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


import amberdb.sql.dao.VertexDao;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;

public class AmberVertex extends AmberElement implements Vertex {

    private VertexDao dao() { return graph().vertexDao(); }   
    
    // this constructor for getting a vertex from the db
    public AmberVertex(Long id, Long txnStart, Long txnEnd) {

        // check if it's already in session
        if (dao().findVertex(id) != null) {
            throw new InSessionException("Edge with id already exists: " + id);
        }
        
        id(id);
        txnStart(txnStart);
        txnEnd(txnEnd);
        
    }

    // This constructor for creating a new vertex
    public AmberVertex(long id) {
        id(id);
    }

    // This constructor for getting a vertex from the session
    public AmberVertex(long id, Long txnStart, Long txnEnd, int state) {
        id(id);
        txnStart(txnStart);
        txnEnd(txnEnd);
    }

    public void addToSession(AmberGraph graph, State state, boolean getPersistentProperties) {
        graph(graph);
        sessionState(state);
        dao().insertVertex(id(), txnStart(), txnEnd(), sessionState().ordinal());

        // get properties
        if (getPersistentProperties) {
            graph().loadPersistentProperties(id());
        }
    }    
    
    @Override
    public Object getId() {
        return id();
    }

    @Override
    public <T> T getProperty(String propertyName) {
        return super.getProperty(propertyName);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return super.getPropertyKeys();
    }

    @Override
    public void remove() {
        for (Edge e: getEdges(Direction.BOTH)) {
            AmberEdge edge = (AmberEdge) e;
            edge.graph(graph());
            edge.remove();
        }
        super.remove();
    }

    @Override
    public <T> T removeProperty(String propertyName) {
        T prop = super.removeProperty(propertyName);
        return prop;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        if (!(value instanceof Integer || value instanceof String || 
              value instanceof Boolean || value instanceof Double)) {
            throw new IllegalArgumentException("Illegal property type [" + value.getClass() + "].");
        }
        
        super.setProperty(propertyName, value);
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex) {
        
        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");
        
        AmberEdge edge = new AmberEdge(graph().newId(), id(), (long) inVertex.getId(), label);
        edge.addToSession(graph, State.NEW, false);
        return edge;
    }

    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {

        // load edges from persistent into session (shouldn't overwrite present edges)
        graph().loadPersistentEdges(this, direction, labels);

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
                unfilteredEdges.addAll(Lists.newArrayList(dao().findInEdges(id())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                unfilteredEdges.addAll(Lists.newArrayList(dao().findOutEdges(id())));
            }
            for (AmberEdge edge : unfilteredEdges) {
                edge.graph(graph());
                if (edge.sessionState() != State.DELETED) {
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
            unfilteredEdges.addAll(Lists.newArrayList(dao().findInEdges(id(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            unfilteredEdges.addAll(Lists.newArrayList(dao().findOutEdges(id(), label)));
        }
        for (AmberEdge edge: unfilteredEdges) {
            edge.graph(graph());
            if (edge.sessionState() != State.DELETED) {
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
        List<AmberVertex> persistedVertices = graph.loadPersistentVertices(this, direction, labels);

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
                unfilteredVertices.addAll(Lists.newArrayList(dao().findInVertices(id())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                unfilteredVertices.addAll(Lists.newArrayList(dao().findOutVertices(id())));
            }
            
            for (AmberVertex vertex : unfilteredVertices) {
                vertex.graph(graph());
                if (vertex.sessionState() != State.DELETED) {
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
            unfilteredVertices.addAll(Lists.newArrayList(dao().findInVertices(id(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            unfilteredVertices.addAll(Lists.newArrayList(dao().findOutVertices(id(), label)));
        }
        unfilteredVertices.removeAll(Collections.singleton(null));
        for (AmberVertex vertex: unfilteredVertices) {
            vertex.graph(graph());
            if (vertex.sessionState() != State.DELETED) {
                vertices.add(vertex);
            }
        }
        return vertices;    
    }
    
    
    @Override
    public VertexQuery query() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Long) id()).hashCode();
        result = prime * result + ((Long) txnStart()).hashCode();
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
        if (id() != other.id())
            return false;
        return true;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("vertex id:").append(id())
        .append(" properties:").append(super.toString())
        .append(" start:").append(txnStart())
        .append(" end:").append(txnEnd())
        .append(" state:").append(sessionState().toString());
        return sb.toString();
    }
}
