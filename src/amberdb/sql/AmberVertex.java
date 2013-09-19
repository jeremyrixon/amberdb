package amberdb.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import amberdb.sql.AmberElement.State;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;

public class AmberVertex extends AmberElement implements Vertex {

    // this constructor for getting a vertex from the db
    // properties must be fetched separately 
    public AmberVertex(Long id, Long txnStart, Long txnEnd) {
        id(id);
        txnStart(txnStart);
        txnEnd(txnEnd);
    }

    // This constructor for creating a new vertex
    public AmberVertex(AmberGraph graph) {
        
        graph(graph);
        id(dao().newId());
        
        // add to transaction as NEW element
        this.txnState(State.NEW);
        graph().currentTxn().addVertex(this);
    }

    // This constructor for getting a vertex from the transaction
    public AmberVertex(long id, long txnStart, long txnEnd, int state) {
        id(id);
        txnStart(txnStart);
        txnEnd(txnEnd);
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
            ((AmberEdge) e).remove();
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
        
        return new AmberEdge(graph(), id(), (long) inVertex.getId(), label);
    }

    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        
        List<Edge> edges = new ArrayList<Edge>();
        if (labels.length == 0) {
            // get 'em all
            if (direction == Direction.IN || direction == Direction.BOTH) {
                edges.addAll(Lists.newArrayList(dao().findInEdgesByVertexId(id())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                edges.addAll(Lists.newArrayList(dao().findOutEdgesByVertexId(id())));
            }
            for (Edge je : edges) {
                ((AmberEdge) je).graph(graph());
            }
        } else {
            for (String label : labels) {
                edges.addAll(getEdges(direction, label));
            }
        }
        return edges;
    }

    public List<Edge> getEdges(Direction direction, String label) {
        List<Edge> edges = new ArrayList<Edge>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(Lists.newArrayList(dao().findInEdgesByVertexIdAndLabel(id(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(Lists.newArrayList(dao().findOutEdgesByVertexIdAndLabel(id(), label)));
        }
        for (Edge je: edges) {
            ((AmberEdge) je).graph(graph());
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
        List<Vertex> vertices = new ArrayList<Vertex>();
        if (labels.length == 0) {
            if (direction == Direction.IN || direction == Direction.BOTH) {
                vertices.addAll(Lists.newArrayList(dao().findVertexByOutEdgeToVertexId(id())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                vertices.addAll(Lists.newArrayList(dao().findVertexByInEdgeFromVertexId(id())));
            }
            for (Vertex jv : vertices) {
                ((AmberVertex) jv).graph(graph());
            }
        } else {
            for (String label : labels) {
                vertices.addAll(getVertices(direction, label));
            }
        }
        return vertices;
    }

    public List<Vertex> getVertices(Direction direction, String label) {
        List<Vertex> vertices = new ArrayList<Vertex>();

        if (direction == Direction.IN || direction == Direction.BOTH) {
            vertices.addAll(Lists.newArrayList(dao().findVertexByOutEdgeLabelToVertexId(id(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            vertices.addAll(Lists.newArrayList(dao().findVertexByInEdgeLabelFromVertexId(id(), label)));
        }
        for (Vertex jv: vertices) {
            ((AmberVertex) jv).graph(graph());
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
        result = prime * result + ((properties() == null) ? 0 : properties().hashCode());
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
        if (properties() == null) {
            if (other.properties() != null)
                return false;
        } else if (!properties().equals(other.properties()))
            return false;
        return true;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("vertex id:").append(id())
        .append(" pi:").append(pi)
        .append(" properties:").append(properties())
        .append(" start:").append(txnStart())
        .append(" end:").append(txnEnd())
        .append(" open:").append(txnOpen());
        return sb.toString();
    }
    
//    private void updatePropertyIndex(String propertyName, Object value) {
//
//        // argument guard
//        if (!(value instanceof Boolean || value instanceof Double || 
//                value instanceof String || value instanceof Integer)) {
//            throw new IllegalArgumentException("Vertex property type can only be one of Boolean, Double, " +
//                    "String or Integer. Supplied value was "+ value.getClass().getName());  
//        }
//        
//        dao().begin();
//        dao().removeVertexPropertyIndexEntry(id(), propertyName);
//        if (value instanceof Boolean) {
//            dao().setBooleanVertexPropertyIndexEntry(id(), propertyName, (Boolean) value);
//        } else if (value instanceof Double) {
//            dao().setDoubleVertexPropertyIndexEntry(id(), propertyName, (Double) value);
//        } else if (value instanceof String) {
//            dao().setStringVertexPropertyIndexEntry(id(), propertyName, (String) value);
//        } else if (value instanceof Integer) {
//            dao().setIntegerVertexPropertyIndexEntry(id(), propertyName, (Integer) value);
//        } 
//        dao().commit();
//    }
    
}
