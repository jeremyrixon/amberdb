package amberdb.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.DefaultGraphQuery;


public class BaseGraph implements Graph, TransactionalGraph {

    
    List<Edge> graphEdges = new ArrayList<Edge>();
    List<Vertex> graphVertices = new ArrayList<Vertex>();
    
    Set<BaseElement> modifiedElements = new HashSet<BaseElement>();
    
    
    
    // id generation handling - overridden in subclass AmberGraph
    class IdGeneratorImpl implements IdGenerator {
        private long id = -1;
        public Long newId() { return id--; }
    }
    protected IdGenerator idGen = new IdGeneratorImpl();
    
    
    
    // element modification handling - overridden in subclass AmberGraph
    class ElementModifiedListenerImpl implements ElementModifiedListener {
    	public void elementModified(Object element) {} // do nothing
    }
    protected ElementModifiedListener elementModListener = new ElementModifiedListenerImpl();

    
    
    // edge factory - overridden in subclass AmberGraph
    class BaseEdgeFactory implements EdgeFactory {
    	public Edge newEdge(Object id, String label, Vertex in, Vertex out, Map<String, Object> properties, Graph graph) {
    		return new BaseEdge((Long) id, label, (BaseVertex) in, (BaseVertex) out, properties, (BaseGraph) graph);
    	} 
    }
    protected EdgeFactory edgeFactory = new BaseEdgeFactory();
    
    
    
    // vertex factory - overridden in subclass AmberGraph
    class BaseVertexFactory implements VertexFactory {
    	public Vertex newVertex(Object id, Map<String, Object> properties, Graph graph) {
    		return new BaseVertex((Long) id, properties, (BaseGraph) graph);
    	} 
    }
    protected VertexFactory vertexFactory = new BaseVertexFactory();
    
    
    
    /* 
     * Constructors
     */
    public BaseGraph() {}
    
    /*
     * 
     * Tinkerpop blueprints graph interface implementation
     *
     */
    
    @Override
    public Edge addEdge(Object id, Vertex out, Vertex in, String label) {
        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");
        Long newId = idGen.newId();
        Edge edge = edgeFactory.newEdge(newId, label, (BaseVertex) in, (BaseVertex) out, null, this);
        graphEdges.add(edge);
        return edge;
    }

    
    @Override
    public Vertex addVertex(Object id) {
        long newId = idGen.newId();
        Vertex vertex = vertexFactory.newVertex(newId, null, this);
        graphVertices.add(vertex);
        return vertex;
    }

    
    @Override
    public Edge getEdge (Object edgeId) {
        // argument guards
        if (edgeId == null) throw new IllegalArgumentException("edge id is null");
        Long id = parseId(edgeId);
        if (id == null) return null;
        
        for (Edge e : graphEdges) {
            if (id.equals(e.getId())) return e;
        }
        return null;
    }

    
    @Override
    public Iterable<Edge> getEdges() {
        List<Edge> edges = Lists.newArrayList(graphEdges);
        return edges;
    }

    
    @Override
    public Iterable<Edge> getEdges(String key, Object value) {
        List<Edge> edges = new ArrayList<Edge>();
        for (Edge edge : graphEdges) {
            if (edge.getProperty(key).equals(value)) {
                edges.add(edge);
            }
        }
        return edges;        
    }


    protected Long parseId(Object eId) {
        // argument guards
        if (!(eId instanceof Long || eId instanceof String || eId instanceof Integer) || eId == null) {
            return null;
        }
        long id = 0;
        try {
            if (eId instanceof String)
                id = Long.parseLong((String) eId);
        } catch (NumberFormatException e) {
            return null;
        }
        if (eId instanceof Long)
            id = (Long) eId;
        if (eId instanceof Integer)
            id = ((Integer) eId).longValue();

        return id;
    }
    
    @Override
    public Vertex getVertex(Object vertexId) {
        // argument guards
        if (vertexId == null) throw new IllegalArgumentException("vertex id is null");
        Long id = parseId(vertexId);
        if (id == null) return null;
        
        for (Vertex v : graphVertices) {
            if (id.equals(v.getId())) return v;
        }
        return null;
    }

    
    @Override
    public Iterable<Vertex> getVertices() {
        List<Vertex> vertices = Lists.newArrayList(graphVertices);
        return vertices;        
    }

    
    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (Vertex vertex : graphVertices) {
            if (vertex.getProperty(key).equals(value)) {
                vertices.add(vertex);
            }
        }
        return vertices;        
    }
    
    /**
     * Implement later if necessary 
     */
    @Override
    public GraphQuery query() {
        return new DefaultGraphQuery(this);
    }

    
    @Override
    public void removeEdge(Edge e) {
        BaseEdge ae = (BaseEdge) e;
System.out.println("removing :" + ae);

if ((Long) ae.getId() == 128L) {
	System.out.println("----check :" + ae);
}

        ae.inVertex.inEdges.remove(e);
        ae.outVertex.outEdges.remove(e);
        
        ae.inVertex = null;
        ae.outVertex = null;
        graphEdges.remove(e);
    }

    
    @Override
    public void removeVertex(Vertex v) {
        BaseVertex av = (BaseVertex) v;
        
        for (Edge e : av.inEdges) {
            BaseEdge ae = (BaseEdge) e;
            ae.outVertex.outEdges.remove(e);
            ae.inVertex = null;
            ae.outVertex = null;
            graphEdges.remove(e);
        }
        
        for (Edge e : av.outEdges) {
            BaseEdge ae = (BaseEdge) e;
            ae.inVertex.inEdges.remove(e);
            ae.inVertex = null;
            ae.outVertex = null;
            graphEdges.remove(e);
        }
        
        av.inEdges = null;
        av.outEdges = null;
        graphVertices.remove(v);
    }

    
    /**
     * REFACTOR NOTE: This method needs to cater for session suspension. Dropping the session tables is an interim
     * measure.
     */
    @Override
    public void shutdown() {
        commit();
    }

    
    public String toString() {
        return ("basegraph");
    }

    
    @Override
    public Features getFeatures() {
        Features features = new Features();
        for (final Field field : features.getClass().getFields()) {
            try {
                field.set(features, false);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        //features.isPersistent = true;
        features.supportsDuplicateEdges = true;
        features.supportsStringProperty = true;
        features.supportsIntegerProperty = true;
        features.supportsBooleanProperty = true;
        features.supportsDoubleProperty = true;
        features.supportsLongProperty = true;
        features.supportsFloatProperty = true;
        features.supportsSerializableObjectProperty = true;
        features.supportsUniformListProperty = true;
        features.supportsMixedListProperty = true;
        features.supportsPrimitiveArrayProperty = true;
        features.supportsMapProperty = true;
        features.ignoresSuppliedIds = true;
        features.supportsVertexProperties = true;
        features.supportsVertexIteration = true;
        features.supportsEdgeProperties = true;
        features.supportsEdgeIteration = true;
        features.supportsEdgeRetrieval = true;
        features.supportsTransactions = true;
        features.checkCompliance();
        return features;
    }

    
    @Override
    @Deprecated
    public void stopTransaction(Conclusion conclusion) {}

    
    @Override
    public void commit() {
    }

    
    @Override
    public void rollback() {
    }
}

