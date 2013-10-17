package amberdb.sql;

import amberdb.sql.State;
import amberdb.sql.dao.*;
import amberdb.sql.map.PersistentEdgeMapperFactory;
import amberdb.sql.map.PersistentPropertyMapper;
import amberdb.sql.map.PersistentVertexMapperFactory;
import amberdb.sql.map.SessionEdgeMapperFactory;
import amberdb.sql.map.SessionVertexMapperFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import com.google.common.collect.Lists;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.DefaultGraphQuery;

public class AmberGraph implements Graph, TransactionalGraph {

    public static final String DEFAULT_USER = "anon";
    public static final DataSource DEFAULT_SESSION_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:session","sess","sess");

    public static final DataSource DEFAULT_PERSIST_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:persist","pers","pers");

    private DBI sessionDbi;
    private DBI persistentDbi;
    
    private PersistentDao persistentDao;
    private TransactionDao transactionDao;
    
    private SessionDao sessionDao;
    private VertexDao vertexDao;
    private EdgeDao edgeDao;

    /** Identify this graph instance */
    private String user;
    
    boolean autoCommit = false;
    
    /* 
     * Constructors
     */
    
    /**
     * Constructor with default user, and in-memory session and persistence
     * databases. Essentially a stand alone session.
     */
    public AmberGraph() {
        initGraph(DEFAULT_SESSION_DATASOURCE, DEFAULT_PERSIST_DATASOURCE, DEFAULT_USER);
    }

    /**
     * Constructor with given user, and in-memory session and persistence
     * databases. Essentially a stand alone session.
     * 
     * @param the
     *            user identifying this instance of AmberGraph
     */
    public AmberGraph(String user) {
        initGraph(DEFAULT_SESSION_DATASOURCE, null, user);
    }

    /**
     * Constructor with default user, a given session database and 
     * a default in-memory persistence database. Essentially a stand alone session.
     * 
     * @param sessionDs 
     *                  the datasource to use for the session
     */
    public AmberGraph(DataSource sessionDs) {
        initGraph(sessionDs, DEFAULT_PERSIST_DATASOURCE, DEFAULT_USER);
    }

    /**
     * Constructor for default user, and specified session and persistence data sources
     * 
     * @param sessionDs if null, then the default session data source is used
     * @param persistentDs if null, this session becomes stand alone
     * @param user
     */
    public AmberGraph(DataSource sessionDs, DataSource persistentDs) {
        initGraph(sessionDs, persistentDs, user);
    }
    
    /**
     * Constructor specifying the user and the session and persistence data sources
     * 
     * @param sessionDs if null, then the default session data source is used
     * @param persistentDs if null, this session becomes stand alone
     * @param user
     */
    public AmberGraph(DataSource sessionDs, DataSource persistentDs, String user) {
        initGraph(sessionDs, persistentDs, user);
    }

    /**
     * The graph initialiser called by all constructors
     * @param sessionDs
     * @param persistentDs
     * @param user
     */
    private void initGraph(DataSource sessionDs, DataSource persistentDs, String user) {

        this.user = user;
        
        initPersistence(persistentDs);
        initSession(sessionDs);
    }

    private void initPersistence(DataSource ds) {
        if (ds == null) ds = DEFAULT_PERSIST_DATASOURCE;
        persistentDbi = new DBI(ds);

        // register mapper factories for passing this graph to elements
        // instantiated via the persistent datastore
        PersistentVertexMapperFactory pvFactory = new PersistentVertexMapperFactory();
        persistentDbi.registerMapper(pvFactory);
        pvFactory.setGraph(this);

        PersistentEdgeMapperFactory peFactory = new PersistentEdgeMapperFactory();
        persistentDbi.registerMapper(peFactory);
        peFactory.setGraph(this);

        // set up required data access objects
        if (ds instanceof MysqlDataSource) {
            persistentDao = persistentDbi.onDemand(PersistentDaoMYSQL.class);
        } else {
            persistentDao = persistentDbi.onDemand(PersistentDaoH2.class);
        }
        transactionDao = persistentDbi.onDemand(TransactionDao.class);
        
        if (ds.equals(DEFAULT_PERSIST_DATASOURCE)) {
            createPersistentSchema();
        }
    }
    
    private void initSession(DataSource ds) {
        if (ds == null) ds = DEFAULT_SESSION_DATASOURCE;
        sessionDbi = new DBI(ds);
        
        // register mapper factories for passing this graph to elements 
        // instantiated via the session datastore
        SessionVertexMapperFactory svFactory = new SessionVertexMapperFactory();
        sessionDbi.registerMapper(svFactory);
        svFactory.setGraph(this);
        
        SessionEdgeMapperFactory seFactory = new SessionEdgeMapperFactory();
        sessionDbi.registerMapper(seFactory);
        seFactory.setGraph(this);
        
        sessionDao = sessionDbi.onDemand(SessionDao.class);

        // create the session database schema
        // REFACTOR NOTE: in future need to allow re-attachment to suspended sessions
        initSessionSchema(sessionDao);
        
        // set up required data access objects
        vertexDao = sessionDbi.onDemand(VertexDao.class);
        edgeDao = sessionDbi.onDemand(EdgeDao.class);
    }
    
    /**
     * Set up a fresh database for sessions to run on
     * 
     * @param dao
     *            the session data access object
     */
    protected void initSessionSchema(SessionDao dao) {
        dao.begin();
        dao.createVertexTable();
        dao.createEdgeTable();
        dao.createPropertyTable();
        try { 
            dao.createPropertyIndex();
        } catch (Exception e) {
            s("Exception expected: Property index already exists ? exception thrown was: " + e.getMessage());
        }
        dao.commit();
    }

    /**
     * WARNING: This method should only ever be used for testing. It will 
     * destroy and rebuild the persistent database sans data. This will 
     * likely make people very angry if run against production.
     *
     * REFACTOR NOTE: prevent this method from being inadvertently called
     *  
     * @param dao
     *            the persistent data access object
     */
    public void createPersistentSchema() {

        persistentDao.createVertexTable();
        persistentDao.createEdgeTable();
        persistentDao.createPropertyTable();
        persistentDao.createIdGeneratorTable();
        persistentDao.createTransactionTable();
        persistentDao.createStagingVertexTable();
        persistentDao.createStagingEdgeTable();
        persistentDao.createStagingPropertyTable();
        newPersistentId(); // seed generator with id > 0
    }

    private List<Long> loadPropertyIds = new ArrayList<Long>();
    public void addLoadPropertyId(Long id) {
        loadPropertyIds.add(id);
    }
    
    /*
     * Amber specific methods
     */

    /**
     * Generate a new id unique within the persistent data store. Not available for stand alone sessions
     *  
     * @return A unique persistent id
     */
    protected Long newPersistentId() {
        
        persistentDao.begin();
        Long newId = persistentDao.newId();

        // occasionally clean up the id generation table (every 1000 pis or so)
        if (newId % 1000 == 995) {
            persistentDao.garbageCollectIds(newId);
        }
        persistentDao.commit();
        return newId;
    }    
    
    public PersistentDao persistentDao() {
        return persistentDao;
    }
    public TransactionDao transactionDao() {
        return transactionDao;
    }
    public SessionDao sessionDao() {
        return sessionDao;
    }
    public EdgeDao edgeDao() {
        return edgeDao;
    }
    public VertexDao vertexDao() {
        return vertexDao;
    }

    protected List<AmberEdge> loadPersistentEdges(AmberVertex vertex, Direction direction, String... labels) {
        
        // we load the properties for all persistent elements as a batch after we've 
        // loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        if (labels.length == 0) {

            if (direction == Direction.IN || direction == Direction.BOTH) {
                edges.addAll(Lists.newArrayList(persistentDao.findInEdges((Long) vertex.getId())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                edges.addAll(Lists.newArrayList(persistentDao.findOutEdges((Long) vertex.getId())));
            }
            edges.removeAll(Collections.singleton(null));
        } else {
            for (String label : labels) {
                edges.addAll(loadPersistentEdges(vertex, direction, label));
            }
        }

        if (edges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return edges;
    }

    protected List<AmberEdge> loadPersistentEdges(AmberVertex vertex, Direction direction, String label) {
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(Lists.newArrayList(persistentDao.findInEdges((Long) vertex.getId(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(Lists.newArrayList(persistentDao.findOutEdges((Long) vertex.getId(), label)));
        }
        edges.removeAll(Collections.singleton(null));
        return edges;
    }
 
    protected List<AmberEdge> loadEdges() {
        
        loadPropertyIds.clear();
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        edges.addAll(Lists.newArrayList(persistentDao.findEdges()));
        edges.removeAll(Collections.singleton(null));
        
        if (edges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return edges;
    }
    
    protected List<AmberEdge> loadEdgesWithProperty(String key, Object value) {
        
        loadPropertyIds.clear();
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();

        edges.addAll(Lists.newArrayList(persistentDao.findEdgesWithProperty(key, AmberProperty.encodeBlob(value))));
        edges.removeAll(Collections.singleton(null));
        
        if (edges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return edges;
    }
    
    protected List<AmberVertex> loadPersistentVertices(AmberVertex vertex, Direction direction, String... labels) {

        // we load the properties for all persistent elements as a batch after we've 
        // loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();

        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        if (labels.length == 0) {

            if (direction == Direction.IN || direction == Direction.BOTH) {
                vertices.addAll(Lists.newArrayList(persistentDao.findInVertices((Long) vertex.getId())));
            }
            if (direction == Direction.OUT || direction == Direction.BOTH) {
                vertices.addAll(Lists.newArrayList(persistentDao.findOutVertices((Long) vertex.getId())));
            }
            vertices.removeAll(Collections.singleton(null));
        } else {
            for (String label : labels) {
                vertices.addAll(loadPersistentVertices(vertex, direction, label));
            }
        }
        
        if (vertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return vertices;
    }

    protected List<AmberVertex> loadPersistentVertices(AmberVertex vertex, Direction direction, String label) {
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            vertices.addAll(Lists.newArrayList(persistentDao.findInVertices((Long) vertex.getId(), label)));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            vertices.addAll(Lists.newArrayList(persistentDao.findOutVertices((Long) vertex.getId(), label)));
        }
        vertices.removeAll(Collections.singleton(null));
        return vertices;
    }

    protected List<AmberVertex> loadVertices() {

        // we load the properties for all persistent elements as a batch after
        // we've loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();

        List<AmberVertex> vertices = new ArrayList<AmberVertex>();

        vertices.addAll(Lists.newArrayList(persistentDao.findVertices()));
        vertices.removeAll(Collections.singleton(null));

        if (vertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();

        return vertices;
    }

    protected List<AmberVertex> loadVerticesWithProperty(String key, Object value) {

        // we load the properties for all persistent elements as a batch after
        // we've loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();

        List<AmberVertex> vertices = new ArrayList<AmberVertex>();

        vertices.addAll(Lists.newArrayList(persistentDao.findVerticesWithProperty(key, AmberProperty.encodeBlob(value))));
        vertices.removeAll(Collections.singleton(null));

        if (vertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();

        return vertices;
    }
    
    protected void loadProperties(List<Long> ids) {

        // construct the in clause
        StringBuilder inClause;
        if (ids.size() > 0) {
            inClause = new StringBuilder("AND id IN (");
            for (Long id : ids) {
                inClause.append(id).append(",");
            }
            inClause.setLength(inClause.length() - 1);
            inClause.append(")");
        } else {
            inClause = new StringBuilder();
        }
        
        // run the query
        Handle h = persistentDbi.open();
        Iterator<AmberProperty> properties = h.createQuery(
                "SELECT id, name, type, value " +
                "FROM property " +
                "WHERE (txn_end = 0 OR txn_end IS NULL) " +        
                inClause.toString())
                .map(new PersistentPropertyMapper()).iterator();

        // put the properties found into the session db
        sessionDao.begin();

        AmberProperty property = null;
        while (properties.hasNext()) {
            try {
                property = properties.next();
                sessionDao.loadProperty(property);
            } catch (UnableToExecuteStatementException e) {
                s("Property in session - not reloaded: " + property.toString());
            }
        }
        sessionDao.commit();
        
        h.close();
    }
    

    
    /*
     * Tinkerpop blueprints graph interface implementation
     */
    
    @Override
    public Edge addEdge(Object id, Vertex out, Vertex in, String label) {

        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");

        AmberEdge edge = new AmberEdge(this, (long) out.getId(), (long) in.getId(), label);
        edge.setState(State.NEW);
        
        if (autoCommit) commitToPersistent("addEdge");
        return edge;
    }

    @Override
    public Vertex addVertex(Object id) {
        
        AmberVertex vertex = new AmberVertex(this);
        vertex.setState(State.NEW);
        
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
        AmberEdge edge = sessionDao.findEdge(id);
        if (edge != null ) {
            if (edge.getState() == State.DEL) {
                return null;
            }
            return edge;
        }
        
        // get from persistent
        edge = persistentDao.findEdge(id);

        if (edge != null) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();

        return edge;
    }

    /**
     * This will naturally blow up if the number of edges in the graph is big.
     * Can we refactor to be lazy ?  
     */
    @Override
    public Iterable<Edge> getEdges() {
        loadEdges();
        
        List<Edge> edges = new ArrayList<Edge>();
        Iterator<AmberEdge> iter = sessionDao.getEdges();
        while (iter.hasNext()) {
            Edge e = iter.next();
            if (e != null) {
                edges.add(e);
            }
        }
        return edges;
    }

    @Override
    public Iterable<Edge> getEdges(String key, Object value) {
        loadEdgesWithProperty(key, value);
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        edges.addAll(Lists.newArrayList(sessionDao.findEdgesWithProperty(key, AmberProperty.encodeBlob(value))));

        List<Edge> filteredEdges = new ArrayList<Edge>();
        for (AmberEdge e : edges) {
            if (e != null) {
                filteredEdges.add(e);
            }
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
        features.supportsLongProperty = true;
        features.supportsFloatProperty = true;
        features.ignoresSuppliedIds = true;
        features.supportsVertexProperties = true;
        features.supportsVertexIteration = true;
        features.supportsEdgeProperties = true;
        features.supportsEdgeIteration = true;
        features.supportsEdgeRetrieval = true;
        features.checkCompliance();
        return features;
    }

    @Override
    public Vertex getVertex(Object vertexId) {

        // argument guards
        if (vertexId == null)
            throw new IllegalArgumentException("vertex id is null");
        if (!(vertexId instanceof Long || vertexId instanceof String)) {
            return null;
        }
        long id = 0;
        try {
            if (vertexId instanceof String)
                id = Long.parseLong((String) vertexId);
        } catch (NumberFormatException e) {
            return null;
        }
        if (vertexId instanceof Long)
            id = (Long) vertexId;

        // is the vertex in the session ?
        AmberVertex vertex = sessionDao.findVertex(id);
        
        if (vertex != null) {
            // if we find it deleted in the session then don't return from persistent
            if (vertex.getState() == State.DEL) {
                return null;
            }
            return vertex;
        }

        // get from persistent
        vertex = persistentDao.findVertex(id);
        
        if (vertex != null) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return vertex;
    }
    
    
    @Override
    public Iterable<Vertex> getVertices() {
        loadVertices();

        List<Vertex> vertices = new ArrayList<Vertex>();
        Iterator<AmberVertex> iter = sessionDao.findVertices();
        while (iter.hasNext()) {
            vertices.add(iter.next());
        }
        return vertices;
    }


    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        loadVerticesWithProperty(key, value);
        
        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        vertices.addAll(Lists.newArrayList(sessionDao.findVerticesWithProperty(key, AmberProperty.encodeBlob(value))));

        List<Vertex> filteredVertices = new ArrayList<Vertex>();
        for (AmberVertex v : vertices) {
            if (v.getState() != State.DEL) {
                filteredVertices.add(v);
            }
        }

        return filteredVertices;
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
        e.remove();
    }

    @Override
    public void removeVertex(Vertex v) {
        v.remove();
    }

    /**
     * REFACTOR NOTE: This method needs to cater for session suspension. Dropping the session tables is an interim
     * measure.
     */
    @Override
    public void shutdown() {

        commit();
        persistentDao.close();
        transactionDao.close();
        sessionDao.close();
        vertexDao.close();
        edgeDao.close();
    }
    
    public String toString() {
        return ("ambergraph");
    }

    protected synchronized void commitToPersistent(String operation) {

        try {
            
            // Get a fresh transaction 
            AmberTransaction txn = new AmberTransaction(this, user, operation);
            s("committing transaction " + txn);

            stageElements(txn);

            List<Long[]> mutatedElements = checkForMutations(txn);

            if (mutatedElements.size() > 0) {
                s("mutations have occurred :");
                for (Long[] idToTxn : mutatedElements) {
                    s("id:" + idToTxn[0] + " in txn:" + idToTxn[1]);
                }
                // REFACTOR
                throw new TransactionException("Aborting transaction: in future we should do more than just this.");
            }

            // we'll hold a transaction now
            persistentDao.begin();

            // make the changes
            commitStaged(txn, persistentDao);

            // it is done
            persistentDao.commit();

            // clear staging tables
            // @TODO

            // IMPORTANT
            // refresh session - question should this clear session or just mark all read ?
            // I am going to implement mark as read, but this means externally persisted
            // updates to unmodified elements will not be seen, so we'll see what we have
            // to do to remedy this in future as it must be fixed somehow, just not right
            // now.
            s("refreshing session");

            // remove deleted elements
            sessionDao.begin();
            sessionDao.clearDeletedProperties();
            sessionDao.clearDeletedVertices();
            sessionDao.clearDeletedEdges();
            
            // Mark modified as amber (ie: fresh, unmodified)
            sessionDao.resetModifiedVertices(txn.getId());
            sessionDao.resetModifiedEdges(txn.getId());
            sessionDao.commit();

            s("end of commit");
            // IMPORTANT: will also get new session start marker, but not implemented yet

        // REFACTOR: Exception handling to be more specific  
        } catch (Exception e) {
            s("================= commit failed ===============");
            s("In future we'll do something about it here");
            e.printStackTrace();
            throw e;
        }
    }
    
    private void stageElements(AmberTransaction txn) {
        s("staging elements...");
        
        List<AmberEdge>     edges      = sessionDao.findAlteredEdges();
        List<AmberVertex>   vertices   = sessionDao.findAlteredVertices();
        List<AmberProperty> properties = sessionDao.findAlteredProperties();
        
        s("\tedges being staged: "      + edges.size());
        s("\tvertices being staged: "   + vertices.size());
        s("\tproperties being staged: " + properties.size());
        
        for (AmberVertex v: vertices) {
            persistentDao.insertStageVertex(v, txn.getId());
        }
        
        for (AmberEdge e: edges) {
            persistentDao.insertStageEdge(e, txn.getId());
        }
        
        for (AmberProperty p: properties) {
            persistentDao.insertStageProperty(p, txn.getId());
        }
    }

    private List<Long[]> checkForMutations(AmberTransaction txn) {
        s("checking for mutations...");
        
        List<Long[]> deletions = persistentDao.findDeletionMutations(txn.getId());
        s("\tdeletions: " + deletions.size());

        List<Long[]> alterations = persistentDao.findAlterationMutations(txn.getId());
        s("\talterations: " + alterations.size());

        // IMPORTANT: will need to check for new incident edges too. This will require
        // a refactor where starting a session acquires a session start id to compare 
        // with txn_start commit is of incident edges (or some such contrivance)
        
        deletions.addAll(alterations);
        
        return deletions;
    }
    
    private void commitStaged(AmberTransaction txn, PersistentDao dao) {
        s("committing staged changes...");
        
        long tId = txn.getId();
        
        // add an end transaction to superceded and deleted elements 
        int numEndedVertices = dao.updateSupercededVertices(tId);
        int numEndedEdges = dao.updateSupercededEdges(tId);

        // 
        int numEndedIncidentEdges = dao.updateSupercededVertexIncidentEdges(tId);
        
        // add an end transaction to superceded and deleted properties
        int numEndedEdgeProperties = dao.updateSupercededEdgeProperties(tId);
        int numEndedVertexProperties = dao.updateSupercededVertexProperties(tId);
        
        // IMPORTANT : will need to delete incident edges for deleted vertices too
        // They may not have been queried into the session, but are none the less
        // affected. This has not been implemented yet, but needs to be.
        
        // add new and modified elements
        int numInsertedVertices = dao.insertStagedVertices(tId);
        int numInsertedEdges = dao.insertStagedEdges(tId);

        // add their properties
        int numInsertedEdgeProperties = dao.insertStagedEdgeProperties(tId);
        int numInsertedVertexProperties = dao.insertStagedVertexProperties(tId);

        // just a bit of output - refactor should improve or remove this
        s("\tended vertices: "   + numEndedVertices);
        s("\tended edges: "      + numEndedEdges);
        s("\tended properties: " + numEndedEdgeProperties);
        s("\tended properties: " + numEndedVertexProperties);
        
        s("\tinserted vertices: "   + numInsertedVertices);
        s("\tinserted edges: "      + numInsertedEdges);
        s("\tinserted edge properties: " + numInsertedEdgeProperties);
        s("\tinserted vertex properties: " + numInsertedVertexProperties);
        
    }
    
    // Convenience for debugging
    private void s(String s) {
        System.out.println(s);
    }

    @Override
    public void stopTransaction(Conclusion conclusion) {
        // TODO Auto-generated method stub
    }

    @Override
    public void commit() {
        commitToPersistent(user);
    }

    @Override
    public void rollback() {
        clearSession();
    }
    
    public void clearSession() {
        // Simply clear all session tables
        sessionDao.clearVertices();
        sessionDao.clearEdges();
        sessionDao.clearProperty();
    }
}
