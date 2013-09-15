package amberdb.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sql.DataSource;

import org.skife.jdbi.v2.DBI;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;

public class AmberGraph implements Graph {

    public static final String DEFAULT_USER = "anon";
    
    private DBI dbi;
    private AmberGraphDao dao;
    private AmberTransaction currentTxn; 

    /*
     * Constructors
     */
    public AmberGraph(DataSource ds) {
        initGraph(new DBI(ds), DEFAULT_USER);
    }
    public AmberGraph(DataSource ds, String user) {
        initGraph(new DBI(ds), user);
    }
    public AmberGraph(DBI dbi) {
        initGraph(dbi, DEFAULT_USER);
    }
    public AmberGraph(DBI dbi, String user) {
        initGraph(dbi, user);
    }
    private void initGraph(DBI dbi, String user) {
        this.dbi = dbi;
        dao = dbi.onDemand(AmberGraphDao.class);
        currentTxn = initTransaction(user);
    }

    /*
     * Jelly specific methods
     */
    protected AmberGraphDao getDao() {
        return dao;
    }
    
    protected DBI getDBI() {
        return dbi;
    }

    protected AmberTransaction initTransaction(String user) {
        // argument guard
        if (user == null || user.trim().isEmpty()) { 
            throw new IllegalArgumentException("Must specify a user");
        }
        
        long txnId = dao.createTxn(user);
        return new AmberTransaction(txnId, user, null);
    }

    public Long currentTxnId() {
        return currentTxn.id;
    }
    
    /*
     * Tinkerpop blueprints graph interface implementation
     */
    
    @Override
    public Edge addEdge(Object id, Vertex out, Vertex in, String label) {
        
        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");

        return new AmberEdge(this, "{}", (long) out.getId(), (long) in.getId(), label);
    }

    @Override
    public Vertex addVertex(Object id) {
        return new AmberVertex(this, "{}", (String) id);
    }

    @Override
    public Edge getEdge (Object edgeId) {
        
        // argument guards
        if (edgeId == null) throw new IllegalArgumentException("edge id is null");
        if (!(edgeId instanceof Long || edgeId instanceof String)) {
            return null;
        }
        long id = 0;
        try {
            if (edgeId instanceof String) id = Long.parseLong((String) edgeId);
        } catch (NumberFormatException e) {
            return null;
        }
        
        if (edgeId instanceof Long) id = (Long) edgeId;

        AmberEdge je = dao.findEdgeById((long) id);
        if (je == null) return null;
        je.graph(this);
        return je;
    }

    /**
     * This will blow up if the number of edges in the graph is big. 
     */
    @Override
    public Iterable<Edge> getEdges() {
        List<Edge> edges = new ArrayList<Edge>();
        Iterator<AmberEdge> ie = dao.findAllEdges();
        while (ie.hasNext()) {
            AmberEdge je = ie.next();
            je.graph(this);
            edges.add(je);
        }
        return edges;
    }

    @Override
    public Iterable<Edge> getEdges(String key, Object value) {
        List<Edge> edges = new ArrayList<Edge>();
        Iterator<AmberEdge> ie = dao.findEdgesByProperty(key, value);
        while (ie.hasNext()) {
            edges.add(ie.next());
        }
        return edges;
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
        features.ignoresSuppliedIds = true;
        features.supportsVertexProperties = true;
        features.supportsVertexIteration = true;
        features.supportsEdgeProperties = true;
        features.supportsEdgeIteration = true;
        features.checkCompliance();
        return features;
    }

    @Override
    public Vertex getVertex(Object vertexId) {
        
        // argument guards
        if (vertexId == null) throw new IllegalArgumentException("vertex id is null");
        if (!(vertexId instanceof Long || vertexId instanceof String)) {
            return null;
        }
        long id = 0;
        try {
            if (vertexId instanceof String) id = Long.parseLong((String) vertexId);
        } catch (NumberFormatException e) {
            return null;
        } 
        if (vertexId instanceof Long) id = (Long) vertexId;

        AmberVertex jv = dao.findVertexById(id);
        if (jv == null) return null;
        jv.graph(this);
        return jv;
    }

    @Override
    public Iterable<Vertex> getVertices() {
        List<Vertex> vertices = new ArrayList<Vertex>();
        Iterator<AmberVertex> ij = dao.findAllVertices();
        while (ij.hasNext()) {
            AmberVertex je = ij.next();
            je.graph(this);
            vertices.add(je);
        }
        return vertices;
    }


    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        List<Vertex> vertices = new ArrayList<Vertex>();
        Iterator<AmberVertex> iv = dao.findVerticesByProperty(key, value);
        while (iv.hasNext()) {
            vertices.add(iv.next());
        }
        return vertices;
    }

    /**
     * Dunno what to do 
     */
    @Override
    public GraphQuery query() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeEdge(Edge e) {
        e.remove();
    }

    @Override
    public void removeVertex(Vertex v) {
        v.remove();
    }

    @Override
    public void shutdown() {
        dao.close();
    }
    
    public String toString() {
        return ("ambergraph");
    }
}