package amberdb.sql;

import amberdb.sql.Stateful.State;
import amberdb.sql.dao.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;

public class AmberGraph implements Graph {

    public static final String DEFAULT_USER = "anon";
    public static final String sessionDsUrl = "jdbc:h2:mem:session";
    DataSource sessionDs = JdbcConnectionPool.create(sessionDsUrl,"session","session");
    
    private DBI sessionDbi;
    
    private PersistentDao persistentDao;
    private TransactionDao transactionDao;
    
    private SessionDao sessionDao;
    private StatefulDao statefulDao;
    private ElementDao elementDao;
    private VertexDao vertexDao;
    private EdgeDao edgeDao;

    boolean autoCommit = false;
    
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
        
        persistentDao = dbi.onDemand(PersistentDao.class);
        transactionDao = dbi.onDemand(TransactionDao.class);
        
        sessionDbi = new DBI(sessionDs);
        sessionDao = sessionDbi.onDemand(SessionDao.class);
         
        sessionDao.begin();
        sessionDao.dropTables();
        sessionDao.createVertexTable();
        sessionDao.createEdgeTable();
        sessionDao.createPropertyTable();
        sessionDao.createPropertyIndex();
        sessionDao.createIdGeneratorTable();
        sessionDao.commit();
        
        statefulDao = sessionDbi.onDemand(StatefulDao.class);
        elementDao = sessionDbi.onDemand(ElementDao.class);
        vertexDao = sessionDbi.onDemand(VertexDao.class);
        edgeDao = sessionDbi.onDemand(EdgeDao.class);
    }

    /*
     * Jelly specific methods
     */

    /**
     * Generate a new id unique within this session. It is also guaranteed not to 
     * clash with the persistent data store's ids by virtue of being a negative number.
     *  
     * @return A unique session id
     */
    protected Long newId() {
        sessionDao.begin();
        Long newPi = -(sessionDao.newId());

        // occasionally clean up the pi generation table (every 1000 pis or so)
        if (newPi % 1000 == 995) {
            sessionDao.garbageCollectIds(newPi);
        }
        sessionDao.commit();
        return newPi;
    }
    
    protected TransactionDao transactionDao() {
        return transactionDao;
    }
    protected SessionDao sessionDao() {
        return sessionDao;
    }
    protected StatefulDao statefulDao() {
        return statefulDao;
    }
    protected ElementDao elementDao() {
        return elementDao;
    }
    protected EdgeDao edgeDao() {
        return edgeDao;
    }
    protected VertexDao vertexDao() {
        return vertexDao;
    }

 
    protected List<AmberEdge> loadPersistentEdges(AmberVertex vertex, Direction direction, String... labels) {
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        if (labels.length == 0) {

            if (direction == Direction.IN || direction == Direction.BOTH) {
                edges.addAll(Lists.newArrayList(persistentDao.findInEdges(vertex.id())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                edges.addAll(Lists.newArrayList(persistentDao.findOutEdges(vertex.id())));
            }
            edges.removeAll(Collections.singleton(null));
            for (AmberEdge edge : edges) {
                edge.addToSession(this, State.READ, true);
            }
        } else {
            for (String label : labels) {
                edges.addAll(loadPersistentEdges(vertex, direction, label));
            }
        }
        return edges;
    }

    protected List<AmberEdge> loadPersistentEdges(AmberVertex vertex, Direction direction, String label) {
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(Lists.newArrayList(persistentDao.findInEdges(vertex.id(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(Lists.newArrayList(persistentDao.findOutEdges(vertex.id(), label)));
        }
        edges.removeAll(Collections.singleton(null));
        for (AmberEdge edge: edges) {
            edge.addToSession(this, State.READ, true);
        }
        return edges;
    }
 
    protected List<AmberEdge> loadEdges() {
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        edges.addAll(Lists.newArrayList(persistentDao.findEdges()));
        edges.removeAll(Collections.singleton(null));
        for (AmberEdge edge: edges) {
            edge.addToSession(this, State.READ, true);
        }
        return edges;
    }
    
    protected List<AmberEdge> loadEdgesWithProperty(String key, Object value) {
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        
        if (value instanceof String) {
            edges.addAll(Lists.newArrayList(persistentDao.findEdgesWithStringProperty(key, (String) value)));
        } else if (value instanceof Boolean) {
            edges.addAll(Lists.newArrayList(persistentDao.findEdgesWithBooleanProperty(key, (Boolean) value)));
        } else if (value instanceof Integer) {
            edges.addAll(Lists.newArrayList(persistentDao.findEdgesWithIntProperty(key, (Integer) value)));
        } else if (value instanceof Double) {
            edges.addAll(Lists.newArrayList(persistentDao.findEdgesWithDoubleProperty(key, (Double) value)));
        }    
        edges.removeAll(Collections.singleton(null));
        for (AmberEdge edge: edges) {
            edge.addToSession(this, State.READ, true);
        }
        return edges;
    }
    
    protected List<AmberVertex> loadPersistentVertices(AmberVertex vertex, Direction direction, String... labels) {
        
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        if (labels.length == 0) {

            if (direction == Direction.IN || direction == Direction.BOTH) {
                vertices.addAll(Lists.newArrayList(persistentDao.findInVertices(vertex.id())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                vertices.addAll(Lists.newArrayList(persistentDao.findOutVertices(vertex.id())));
            }
            vertices.removeAll(Collections.singleton(null));
            for (AmberVertex v : vertices) {
                v.addToSession(this, State.READ, true);
            }
        } else {
            for (String label : labels) {
                vertices.addAll(loadPersistentVertices(vertex, direction, label));
            }
        }
        return vertices;
    }

    protected List<AmberVertex> loadPersistentVertices(AmberVertex vertex, Direction direction, String label) {
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            vertices.addAll(Lists.newArrayList(persistentDao.findInVertices(vertex.id(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            vertices.addAll(Lists.newArrayList(persistentDao.findOutVertices(vertex.id(), label)));
        }
        vertices.removeAll(Collections.singleton(null));
        for (AmberVertex v: vertices) {
            v.addToSession(this, State.READ, true);
        }
        return vertices;
    }

    protected List<AmberVertex> loadVertices() {
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        
        vertices.addAll(Lists.newArrayList(persistentDao.findVertices()));
        vertices.removeAll(Collections.singleton(null));
        for (AmberVertex v: vertices) {
            v.addToSession(this, State.READ, true);
        }
        return vertices;
    }

    protected List<AmberVertex> loadVerticesWithProperty(String key, Object value) {
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        
        if (value instanceof String) {
            vertices.addAll(Lists.newArrayList(persistentDao.findVerticesWithStringProperty(key, (String) value)));
        } else if (value instanceof Boolean) {
            vertices.addAll(Lists.newArrayList(persistentDao.findVerticesWithBooleanProperty(key, (Boolean) value)));
        } else if (value instanceof Integer) {
            vertices.addAll(Lists.newArrayList(persistentDao.findVerticesWithIntProperty(key, (Integer) value)));
        } else if (value instanceof Double) {
            vertices.addAll(Lists.newArrayList(persistentDao.findVerticesWithDoubleProperty(key, (Double) value)));
        }    
        vertices.removeAll(Collections.singleton(null));
        for (AmberVertex vertex: vertices) {
            vertex.addToSession(this, State.READ, true);
        }
        return vertices;
    }
    
    protected void loadPersistentProperties(long id) {
        
        Iterator<AmberProperty> properties = persistentDao.findProperties(id);
        while (properties.hasNext()) {
            AmberProperty property = properties.next();
            if (property.getType().equals("s")) {
                elementDao.addStringProperty(property.id(), property.name, (String) property.getValue());
            } else if (property.getType().equals("b")) {
                elementDao.addBooleanProperty(property.id(), property.name, (Boolean) property.getValue());
            } else if (property.getType().equals("i")) {
                elementDao.addIntProperty(property.id(), property.name, (Integer) property.getValue());
            } else if (property.getType().equals("d")) {
                elementDao.addDoubleProperty(property.id(), property.name, (Double) property.getValue());
            }
        }
    }
 
    
    /*
     * Tinkerpop blueprints graph interface implementation
     */
    
    @Override
    public Edge addEdge(Object id, Vertex out, Vertex in, String label) {

        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");

        long newId = newId();
        AmberEdge edge = new AmberEdge(newId, (long) out.getId(), (long) in.getId(), label);
        edge.addToSession(this, State.NEW, false);
        
        if (autoCommit) commitToPersistent("addEdge");
        return edge;
    }

    @Override
    public Vertex addVertex(Object id) {
        
        long newId = newId();
        AmberVertex vertex = new AmberVertex(newId);
        vertex.addToSession(this, State.NEW, false);

        if (autoCommit) commitToPersistent("addVertex");
        return vertex;
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

        // is the edge in the session ? 
        AmberEdge edge = edgeDao.findEdge(id);
        if (edge != null ) {
            return (Edge) nullIfDeleted(edge);
        }
        
        // get from persistent
        edge = persistentDao.findEdge(id);
        if (edge != null) {
            edge.addToSession(this, State.READ, true);
        }
        return edge;
    }

    /**
     * This will blow up if the number of edges in the graph is big. 
     */
    @Override
    public Iterable<Edge> getEdges() {

        loadEdges();
        List<Edge> edges = new ArrayList<Edge>();
        Iterator<AmberEdge> iter = edgeDao.findEdges();
        while (iter.hasNext()) {
            addUndeletedElements(iter.next(), edges);
        }
        return edges;
    }

    @Override
    public Iterable<Edge> getEdges(String key, Object value) {

        loadEdgesWithProperty(key, value);
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        
        if (value instanceof String) {
            edges.addAll(Lists.newArrayList(edgeDao.findEdgesWithStringProperty(key, (String) value)));
        } else if (value instanceof Boolean) {
            edges.addAll(Lists.newArrayList(edgeDao.findEdgesWithBooleanProperty(key, (Boolean) value)));
        } else if (value instanceof Integer) {
            edges.addAll(Lists.newArrayList(edgeDao.findEdgesWithIntProperty(key, (Integer) value)));
        } else if (value instanceof Double) {
            edges.addAll(Lists.newArrayList(edgeDao.findEdgesWithDoubleProperty(key, (Double) value)));
        }
        
        List<Edge> filteredEdges = new ArrayList<Edge>();
        for (AmberEdge e: edges) {
            addUndeletedElements(e, filteredEdges);
        }
        return filteredEdges;
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
        features.supportsEdgeRetrieval = true;
        
        
        //features.supportsMixedListProperty= true;
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

        // is the vertex in the session ? 
        AmberVertex vertex = vertexDao.findVertex(id);
        if (vertex != null ) {
            return (Vertex) nullIfDeleted(vertex);
        }
        
        // get from persistent
        vertex = persistentDao.findVertex(id);
        if (vertex != null) {
            vertex.addToSession(this, State.READ, true);
        }
        return vertex;
    }

    
    
    @Override
    public Iterable<Vertex> getVertices() {
        loadVertices();
        List<Vertex> vertices = new ArrayList<Vertex>();
        Iterator<AmberVertex> iter = vertexDao.findVertices();
        while (iter.hasNext()) {
            addUndeletedElements(iter.next(), vertices);
        }
        return vertices;
    }


    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        loadVerticesWithProperty(key, value);
        
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        
        if (value instanceof String) {
            vertices.addAll(Lists.newArrayList(vertexDao.findVerticesWithStringProperty(key, (String) value)));
        } else if (value instanceof Boolean) {
            vertices.addAll(Lists.newArrayList(vertexDao.findVerticesWithBooleanProperty(key, (Boolean) value)));
        } else if (value instanceof Integer) {
            vertices.addAll(Lists.newArrayList(vertexDao.findVerticesWithIntProperty(key, (Integer) value)));
        } else if (value instanceof Double) {
            vertices.addAll(Lists.newArrayList(vertexDao.findVerticesWithDoubleProperty(key, (Double) value)));
        } 
        
        List<Vertex> filteredVertices = new ArrayList<Vertex>();
        for (AmberVertex v: vertices) {
            addUndeletedElements(v, filteredVertices);
        }
        
        return filteredVertices;
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
        if (autoCommit) commitToPersistent("removeEdge");
    }

    @Override
    public void removeVertex(Vertex v) {
        v.remove();
        if (autoCommit) commitToPersistent("removeVertex");
    }

    @Override
    public void shutdown() {
        sessionDao.dropTables();
        
        persistentDao.close();
        sessionDao.close();
        elementDao.close();
        vertexDao.close();
        edgeDao.close();
    }
    
    public String toString() {
        return ("ambergraph");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addUndeletedElements(AmberElement element, List elementList) {
        if (element != null) {
            element.graph(this);
            if (element.sessionState() != State.DELETED) {
                elementList.add(element);
            }
        }
    }

    private AmberElement nullIfDeleted(AmberElement element) {
        element.graph(this);
        if (element.sessionState() == State.DELETED) {
            return null;
        }
        return element;
    }

    protected void commitToPersistent(String operation) {
        
        s("committing transaction : " + operation);
        
        // Get a fresh transaction
        AmberTransaction txn = new AmberTransaction(this, DEFAULT_USER, operation);
        
        // start by preparing the new elements
        prepareNewElements(txn);

        // next the modified elements
        prepareModifiedElements(txn);
        
        // deleted e & v
        // --------------
        // end date w props
        
        // new e & v
        // ---------
        // write over
        // with props
        
        // modified e & v
        // --------------
        // check modified
        
    }
    
    private void prepareNewElements(AmberTransaction txn) {
        
        List<Persistent> newElements = new ArrayList<Persistent>();
        newElements.addAll(sessionDao().findVerticesByState(State.NEW.ordinal()));  
        newElements.addAll(sessionDao().findEdgesByState(State.NEW.ordinal()));
        newElements.addAll(sessionDao().findPropertiesByElementState(State.NEW.ordinal()));
        
        // add them all to the new transaction. Set the txn_end also,
        // so they are ignored until they are committed
        for (Persistent p : newElements) {
            p.graph(this);
            p.txnStart(txn.id());
            p.txnEnd(txn.id());
            
            // prepare by writing them to persistent store
            if (p instanceof AmberVertex) {
                s("persisting new vertex :"+((AmberVertex) p).toString());
                persistentDao.insertVertex(p.id(), p.txnStart(), p.txnEnd());
            } else if (p instanceof AmberEdge) {
                AmberEdge e = (AmberEdge) p;
                s("persisting new edge :"+e.toString());
                persistentDao.insertEdge(e.id(), e.txnStart(), e.txnEnd(), 
                        e.outVertexId, e.outVertexId, e.label, e.edgeOrder);
            } else if (p instanceof AmberProperty) {
                s("persisting new prop :"+((AmberProperty) p).toString());
                persistProperty((AmberProperty) p);
            } else {
                s("it finally happened: encountered class "+ p.getClass().getName());
                // throw UnknownPersistentClassError ( need to decide what to do )
            }
        }
    }

    private void prepareModifiedElements(AmberTransaction txn) {
        
        List<Persistent> modElements = new ArrayList<Persistent>();
        modElements.addAll(sessionDao().findVerticesByState(State.MODIFIED.ordinal()));  
        modElements.addAll(sessionDao().findEdgesByState(State.MODIFIED.ordinal()));
        modElements.addAll(sessionDao().findPropertiesByElementState(State.MODIFIED.ordinal()));

        // here here here here here
        
        // add them all to the new transaction. Set the txn_end also,
        // so they are ignored until they are committed
        for (Persistent p : modElements) {
            p.graph(this);
            p.txnStart(txn.id());
            p.txnEnd(txn.id());
            
            // prepare by writing them to persistent store
            if (p instanceof AmberVertex) {
                persistentDao.insertVertex(p.id(), p.txnStart(), p.txnEnd());
            } else if (p instanceof AmberEdge) {
                AmberEdge e = (AmberEdge) p;
                persistentDao.insertEdge(e.id(), e.txnStart(), e.txnEnd(), 
                        e.outVertexId, e.outVertexId, e.label, e.edgeOrder);
            } else if (p instanceof AmberProperty) {
                persistProperty((AmberProperty) p);
            }
        }
    }
    
    
    private void persistProperty(AmberProperty p) {
        if (p.getType().equals("s")) {
            persistentDao.createStringProperty(p.id(), p.txnStart(), p.txnEnd(), p.name, p.getType(), (String) p.getValue());
        } else if (p.getType().equals("b")) {
            persistentDao.createBooleanProperty(p.id(), p.txnStart(), p.txnEnd(), p.name, p.getType(), (Boolean) p.getValue());
        } else if (p.getType().equals("i")) {
            persistentDao.createIntegerProperty(p.id(), p.txnStart(), p.txnEnd(), p.name, p.getType(), (Integer) p.getValue());
        } else if (p.getType().equals("d")) {
            persistentDao.createDoubleProperty(p.id(), p.txnStart(), p.txnEnd(), p.name, p.getType(), (Double) p.getValue());
        }
    }
    
    // Convenience for debugging
    private void s(String s) {
        System.out.println(s);
    }
    
    
}