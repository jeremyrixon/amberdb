package amberdb.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.util.LongMapper;

import amberdb.sql.dao.EdgeDao;
import amberdb.sql.dao.PersistentDao;
import amberdb.sql.dao.PersistentDaoH2;
import amberdb.sql.dao.PersistentDaoMYSQL;
import amberdb.sql.dao.SessionDao;
import amberdb.sql.dao.TransactionDao;
import amberdb.sql.dao.VertexDao;
import amberdb.sql.map.PersistentEdgeMapper;
import amberdb.sql.map.PersistentEdgeMapperFactory;
import amberdb.sql.map.PersistentPropertyMapper;
import amberdb.sql.map.PersistentVertexMapper;
import amberdb.sql.map.PersistentVertexMapperFactory;
import amberdb.sql.map.SessionEdgeMapper;
import amberdb.sql.map.SessionEdgeMapperFactory;
import amberdb.sql.map.SessionVertexMapper;
import amberdb.sql.map.SessionVertexMapperFactory;

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

    public Logger log = Logger.getLogger(AmberGraph.class.getName());
    
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
        
        if (!persistentDao.schemaTablesExist()) {
            s("persistent schema doesn't exist - creating ...");
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
        if (!sessionDao.schemaTablesExist()) {
            s(user + " session schema doesn't exist - creating ...");
            initSessionSchema(sessionDao);
        }
        
        // set up required data access objects
        vertexDao = sessionDbi.onDemand(VertexDao.class);
        edgeDao = sessionDbi.onDemand(EdgeDao.class);
        
        // Synch mark records when this session was last fully compared with persistent ds
        sessionDao.updateSynchMark(newPersistentId());
    }
    
    /**
     * Set up a fresh database for sessions to run on
     * 
     * @param dao
     *            the session data access object
     */
    protected void initSessionSchema(SessionDao dao) {
        dao.createVertexTable();
        dao.createEdgeTable();
        dao.createPropertyTable();
        try { 
            dao.createPropertyIndex();
        } catch (Exception e) {
            s("Exception expected: Property index already exists ? exception thrown was: " + e.getMessage());
        }
        dao.createSynchTable();
        dao.initSynchMark();
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

    /**
     * Find all elements in persistent data store that exist in the session and
     * have been modified since this session was last synchronised.
     */
    public Map<String, List<Long>> getSynchLists() {
        
        Map<String, List<Long>> synchMap = new HashMap<String, List<Long>>(2); 

        Long synchMark = sessionDao.getSynchMark();
        s(user + " geting synch lists from txn " + synchMark);

        // get a list of vertex ids that contains all persistent vertices that 
        // have changes since last synch that are also current in this session
        // This will not include NEW session vertices of course 
        List<Long> sessionVertexIds = sessionDao.findNotNewVertexIds();
        sessionVertexIds.retainAll(findMutatedVertexIdsInListSinceTxn(synchMark, sessionVertexIds));
        
        // do the same for edges
        List<Long> sessionEdgeIds = sessionDao.findNotNewEdgeIds();
        sessionEdgeIds.retainAll(findMutatedEdgeIdsInListSinceTxn(synchMark, sessionEdgeIds));
        
        synchMap.put("vertex", sessionVertexIds);
        synchMap.put("edge", sessionEdgeIds);
        
        return synchMap;
    }

    /**
     * Convenience function to create an sql in clause from a list of numbers
     * 
     * @param inList a list of numbers a1, a2, a3 ...
     * @param clausePrefix a string to prefix the IN clause in the SQL statement
     *        eg: 'AND id ', 'AND id NOT ' 
     *
     * @return a string of the form "IN (a1, a2, a3 ...)"
     */
    private String constructLongInClause(List<Long> inList, String clausePrefix) {
        if (inList == null) return "";
        
        StringBuilder inClause;
        if (inList.size() > 0) {
            inClause = new StringBuilder(clausePrefix + " IN (");
            for (Long num : inList) {
                inClause.append(num).append(',');
            }
            inClause.setLength(inClause.length() - 1);
            inClause.append(')');
        } else {
            inClause = new StringBuilder();
        }
        return inClause.toString();
    }

    /**
     * Convenience function to create an sql in clause from a list of Strings. Any
     * string with a quote (') will just be ignored
     * 
     * @param inList a list of strings a1, a2, a3 ...
     * @param clausePrefix the string to connect the IN clause to the SQL statement
     *        eg: 'AND id ', 'AND id NOT '
     *
     * @return a string of the form "IN (a1, a2, a3 ...)"
     */
    protected String constructStringInClause(List<String> inList, String clausePrefix) {
        if (inList == null) return "";
        
        StringBuilder inClause;
        if (inList.size() > 0) {
            inClause = new StringBuilder(clausePrefix + " IN ('");
            for (String str : inList) {
                if (!str.contains("'")) {
                    inClause.append(str).append("','");
                }
            }
            inClause.setLength(inClause.length() - 3);
            inClause.append("') ");
            
            // replace with blank string if all strings had quotes
            if (inClause.length() == clausePrefix.length() + 8) {
                inClause = new StringBuilder();
            }
        } else {
            inClause = new StringBuilder();
        }
        return inClause.toString();
    }
   
    private List<Long> findMutatedVertexIdsInListSinceTxn(long synchMark, List<Long> vertexIdList) {
        
        String inClause = constructLongInClause(vertexIdList, "AND id ");

        Handle h = persistentDbi.open();
        List<Long> ids = h.createQuery(
                "SELECT id " + 
                "FROM vertex v " +
                "WHERE (v.txn_start > :txnId " +
                "OR v.txn_end > :txnId) " +
                inClause)
                .bind("txnId", synchMark)
                .map(new LongMapper()).list();
        h.close();
        return ids;
    }

    private List<Long> findMutatedEdgeIdsInListSinceTxn(long synchMark, List<Long> edgeIdList) {

        String inClause = constructLongInClause(edgeIdList, "AND id ");

        Handle h = persistentDbi.open();
        List<Long> ids = h.createQuery(
                "SELECT id " + 
                "FROM edge e " +
                "WHERE (e.txn_start > :txnId " +
                "OR e.txn_end > :txnId) " +
                inClause)
                .bind("txnId", synchMark)
                .map(new LongMapper()).list();
        h.close();
        return ids;
    }
    
    /**
     * Updates the synch marker to now (ie: latest txn)
     */
    public void updateSynchMark() {
        sessionDao.updateSynchMark(newPersistentId());
    }
    
    /**
     * Get the synch marker
     */
    public long getSynchMark() {
        return sessionDao.getSynchMark();
    }
    
    /**
     * Basic synchronisation between persistent and session graphs. Updates the session in the follow manner.
     * 
     * P(mod) S(amb) -> P(mod)
     * P(mod) S(mod) -> S(mod) [risky - is there a better way ?]
     * P(mod) S(del) -> P(mod)
     * 
     * P(del) S(amb) -> P(del)
     * P(del) S(mod) -> S(mod) as S(new)
     * P(del) S(del) -> P(del)
     * 
     * This method is currently pretty inefficient: it sure runs slowly against
     * MySql. Might have to remedy this sometime soon.
     */
    public void synch() {
        
        Map<String, List<Long>> synch = getSynchLists();
        
        // just update synch marker if nothing needs doing
        if (synch.get("vertex").size() == 0 && synch.get("edge").size() == 0) {
            s(user + " no mutations to session graph from amber since txn " + sessionDao.getSynchMark());
            updateSynchMark();
            s(user + " session synch marker updated to " + sessionDao.getSynchMark());
            return;
        }
        
        // so basically, we update the session from amber unless the element has been modified locally
        // in that case we will currently just use the local version. In future we might implement some 
        // kind of merge routine for modified elements.
        for (Long id : synch.get("vertex")) {
            String state = vertexDao.getVertexState(id);
            s(user + " -- state of vertex is: " + state);
            if (state.equals(State.MOD.toString())) {
                s(user + " synch retaining vertex as modified in session: " + id);
            } else {
                vertexDao.removeVertex(id);
                vertexDao.removeVertexProperties(id);
                Vertex v = getVertex(id);
                if (v == null) {
                    s("vertex deleted in amber: " + id);
                }
            }
        }

        for (Long id : synch.get("edge")) {
            String state = edgeDao.getEdgeState(id);
            s(user + " -- state of edge is: " + state);
            if (state.equals(State.MOD.toString())) {
                s(user + " synch retaining edge as modified in session: " + id);
            } else {
                edgeDao.removeEdge(id);
                edgeDao.removeEdgeProperties(id);
                Edge e = getEdge(id);
                if (e == null) {
                    s("edge deleted in amber " + id);
                }
            }
        }
        
        updateSynchMark();
        s(user + " session synch marker updated to " + sessionDao.getSynchMark());
        
        // Just a monologue : might have been better (cleaner and more generic) to create 
        // unique session index on id and txn_start and use that through out sessions, or
        // some such thing. It would make synchronisation simpler which would be a good 
        // thing. Oh well, can do for next refactor maybe (yeah right :-) Ie: use txns
        // ids in session to indicate new, modified, deleted and updated. Hmmmm it sounds 
        // so good now - wish i'd done it before time started running out and patching a
        // less than ideal system seemed like the better option. I'm so sorry.
    }
    
    
    
    protected List<AmberEdge> loadPersistentEdges(AmberVertex vertex, List<Long> excludeIds, Direction direction, String... labels) {
        
        // we load the properties for all persistent elements as a batch after we've 
        // loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        edges.addAll(Lists.newArrayList(findPersistentEdges((Long) vertex.getId(), direction, excludeIds, labels)));
        edges.removeAll(Collections.singleton(null));
        
        if (edges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return edges;
    }

    private List<AmberEdge> findPersistentEdges(Long vertexId, Direction direction, List<Long> exclusionIds, String... labels) {

        String inClause = constructLongInClause(exclusionIds, "AND id NOT ");
        String labelsClause = constructStringInClause(Lists.newArrayList(labels), "AND label ");

        List<AmberEdge> edges = new ArrayList<AmberEdge>();

        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(directedFindPersistentEdges(vertexId, "AND v_in = :vertexId ", labelsClause, inClause));
        } 
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(directedFindPersistentEdges(vertexId, "AND v_out = :vertexId ", labelsClause, inClause));
        }
        return edges;
    } 
    
    List<AmberEdge> directedFindPersistentEdges(Long vertexId, String directionClause, String labelsClause, String inClause) {
        Handle h = persistentDbi.open();
        List<AmberEdge> edges = h.createQuery(
                "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
                "FROM edge " +
                "WHERE (txn_start > 0 OR txn_start IS NOT NULL) " +
                "AND (txn_end = 0 OR txn_end IS NULL) " +
                directionClause +
                labelsClause +
                inClause +
                "ORDER BY edge_order")
                .bind("vertexId", vertexId)
                .map(new PersistentEdgeMapper(this)).list();
        h.close();
        return edges;
    }
 
    protected List<AmberEdge> loadEdges(List<Long> excludedIds) {
        
        loadPropertyIds.clear();
        
        List<AmberEdge> edges = new ArrayList<AmberEdge>();
        edges.addAll(Lists.newArrayList(findPersistentEdges(null, null, excludedIds, new String[] {})));
        edges.removeAll(Collections.singleton(null));
        
        if (edges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return edges;
    }
    
    protected List<AmberEdge> loadEdgesWithProperty(String key, Object value, List<Long> excludedIds) {
        
        loadPropertyIds.clear();
        
        String inClause = constructLongInClause(excludedIds, "AND e.id NOT ");
        
        Handle h = persistentDbi.open();
        List<AmberEdge> edges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order " +
                "FROM edge e, property p " +
                "WHERE e.id = p.id " +
                "AND e.txn_start = p.txn_start " +
                "AND (e.txn_end = 0 OR e.txn_end IS NULL) " +
                "AND (e.txn_start > 0 OR e.txn_start IS NOT NULL) " +
                "AND p.name = :name " +
                "AND p.value = :value " +
                inClause +
                "ORDER BY e.edge_order")
                .bind("name", key)
                .bind("value", AmberProperty.encodeBlob(value))
                .map(new PersistentEdgeMapper(this)).list();
        h.close();
        
        if (edges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return edges;
    }

    protected List<AmberVertex> loadVerticesWithProperty(String key, Object value, List<Long> excludedIds) {
        
        loadPropertyIds.clear();
        
        String inClause = constructLongInClause(excludedIds, "AND v.id NOT ");
        
        Handle h = persistentDbi.open();
        List<AmberVertex> vertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end " +
                "FROM vertex v, property p " +
                "WHERE v.id = p.id " +
                "AND v.txn_start = p.txn_start " +
                "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
                "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
                "AND p.name = :name " +
                "AND p.value = :value " +
                inClause)
                .bind("name", key)
                .bind("value", AmberProperty.encodeBlob(value))
                .map(new PersistentVertexMapper(this)).list();
        h.close();
        vertices.removeAll(Collections.singleton(null));
        
        if (vertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return vertices;
    }
    
    protected List<AmberVertex> loadVertices(List<Long> excludedIds) {

        
        // we load the properties for all persistent elements as a batch after
        // we've loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();

        List<AmberVertex> vertices = new ArrayList<AmberVertex>();
        vertices.addAll(Lists.newArrayList(findPersistentVertices(excludedIds)));
        vertices.removeAll(Collections.singleton(null));
        
        if (vertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        return vertices;
    }
    
    private List<AmberVertex> findPersistentVertices(List<Long> exclusionIds) {

        String inClause = constructLongInClause(exclusionIds, "AND id NOT ");

        Handle h = persistentDbi.open();
        List<AmberVertex> vertices = h.createQuery(
                "SELECT id, txn_start, txn_end " +
                "FROM vertex " +
                "WHERE (txn_start > 0 OR txn_start IS NOT NULL) " +
                "AND (txn_end = 0 OR txn_end IS NULL) " +
                inClause)
                .map(new PersistentVertexMapper(this)).list();
        h.close();
        vertices.removeAll(Collections.singleton(null));
        
        return vertices;
    }
    
    protected List<AmberVertex> loadVerticesWithProperty(String key, Object value) {

        // we load the properties for all persistent elements as a batch after
        // we've loaded the elements themselves. This list keeps track of them
        loadPropertyIds.clear();

        List<AmberVertex> vertices = Lists.newArrayList(persistentDao.findVerticesWithProperty(key, AmberProperty.encodeBlob(value)));
        vertices.removeAll(Collections.singleton(null));

        if (vertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();

        return vertices;
    }
    
    protected void loadProperties(List<Long> ids) {

        String inClause = constructLongInClause(ids, "AND id ");
        
        // run the query
        Handle h = persistentDbi.open();
        Iterator<AmberProperty> properties = h.createQuery(
                "SELECT id, name, type, value " +
                "FROM property " +
                "WHERE (txn_end = 0 OR txn_end IS NULL) " +        
                inClause)
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
        if (((AmberVertex)out).getState().equals(State.DEL)) throw new IllegalArgumentException("out vertex was deleted in session");
        if (((AmberVertex)in).getState().equals(State.DEL)) throw new IllegalArgumentException("in vertex was deleted in session");

        AmberEdge edge = new AmberEdge(this, (long) out.getId(), (long) in.getId(), label);
        edge.setState(State.NEW);
        
        return edge;
    }

    @Override
    public Vertex addVertex(Object id) {
        
        AmberVertex vertex = new AmberVertex(this);
        vertex.setState(State.NEW);
        
        return vertex;
    }

    @Override
    public Edge getEdge (Object edgeId) {
        
        // argument guards
        if (edgeId == null) throw new IllegalArgumentException("edge id is null");
        if (!(edgeId instanceof Long || edgeId instanceof String)) { return null; }
        
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
     * This method sucks all edges into memory. This is of course a bad thing as it will 
     * naturally blow up if the number of edges in the graph is big. 
     * 
     * Can we refactor to be lazy ?
     */
    @Override
    public Iterable<Edge> getEdges() {

        List<Edge> edges = new ArrayList<Edge>();

        // following list includes deleted session edges
        List<AmberEdge> sessionEdges = Lists.newArrayList(sessionDao.getEdges());
        
        List<Long> sessionEdgeIds = new ArrayList<Long>();
        for (AmberEdge e : sessionEdges) {
            //if (e == null) continue;
            sessionEdgeIds.add((Long) e.getId());
            if (e != null && e.getState() != State.DEL) {            
                edges.add(e);
            }
        }
        edges.addAll(loadEdges(sessionEdgeIds));
        return edges;        
    }

    @Override
    public Iterable<Edge> getEdges(String key, Object value) {

        List<Edge> edges = new ArrayList<Edge>();

        // Includes deleted edges
        List<AmberEdge> sessionEdges = Lists.newArrayList(sessionDao.findEdgesWithProperty(key, AmberProperty.encodeBlob(value)));

        List<Long> sessionEdgeIds = new ArrayList<Long>();
        for (AmberEdge e : sessionEdges) {
            //if (e == null) continue;
            sessionEdgeIds.add((Long) e.getId());
            if (e != null && e.getState() != State.DEL) {            
                edges.add(e);
            }
        }
        edges.addAll(loadEdgesWithProperty(key, value, sessionEdgeIds));
        return (Iterable<Edge>) edges;        
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
        features.supportsTransactions = true;
        features.checkCompliance();
        return features;
    }

    @Override
    public Vertex getVertex(Object vertexId) {

        // argument guards
        if (vertexId == null)
            throw new IllegalArgumentException("vertex id is null");
        if (!(vertexId instanceof Long || vertexId instanceof String || vertexId instanceof Integer)) {
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
        if (vertexId instanceof Integer)
            id = ((Integer) vertexId).longValue();

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
        
        List<Vertex> vertices = new ArrayList<Vertex>();

        // following list includes deleted session vertices
        List<AmberVertex> sessionVertices = Lists.newArrayList(sessionDao.findVertices());
        
        List<Long> sessionVertexIds = new ArrayList<Long>();
        for (AmberVertex v : sessionVertices) {
            //if (e == null) continue;
            sessionVertexIds.add((Long) v.getId());
            if (v != null && v.getState() != State.DEL) {            
                vertices.add(v);
            }
        }
        vertices.addAll(loadVertices(sessionVertexIds));
        return vertices;
    }

    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {

        List<Vertex> vertices = new ArrayList<Vertex>();

        // Includes deleted vertices
        List<AmberVertex> sessionVertices = Lists.newArrayList(sessionDao.findVerticesWithProperty(key, AmberProperty.encodeBlob(value)));

        List<Long> sessionVertexIds = new ArrayList<Long>();
        for (AmberVertex v : sessionVertices) {
            sessionVertexIds.add((Long) v.getId());
            if (v != null && v.getState() != State.DEL) {            
                vertices.add(v);
            }
        }
        vertices.addAll(loadVerticesWithProperty(key, value, sessionVertexIds));
        return (Iterable<Vertex>) vertices;             
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

        int retry = 0;
        final int MAX_RETRIES = 1;

        try {
            AmberTransaction txn = null;
            while (true) {
                txn = new AmberTransaction(this, user, operation);
                s("committing transaction: " + txn);
                s("\tuser " + user);

                if (stageElements(txn) == 0) {
                    s("no updates to commit. only synching session.");
                    synch();
                    return;
                }

                List<Long[]> mutatedElements = checkForMutations(txn);

                if (mutatedElements.size() > 0) {
                    s("mutations have occurred :");
                    for (Long[] idToTxn : mutatedElements) {
                        s("id:" + idToTxn[0] + " in txn:" + idToTxn[1]);
                    }
                    if (retry >= MAX_RETRIES) {
                        throw new TransactionException("aborting transaction: maximum retries performed.");
                    }
                    // REFACTOR
                    s("sychronising session then retrying commit");
                    txn.setOperation("aborted: mutations. retrying. ");
                    retry++;
                    synch();
                    continue;
                }
                break;
            }

            // we'll hold a transaction now
            persistentDao.begin();

            // make the changes
            commitStaged(txn, persistentDao);

            // it is done
            persistentDao.commit();

            // clear staging tables
            // TO DO, and maybe in some other manner. Not sure.

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
            
            // Update synch marker to now
            updateSynchMark();
            
            s("end of commit");
            
        // REFACTOR: Exception handling to be more specific  
        } catch (Exception e) {
            s("================= commit failed ===============");
            s("In future we'll do something about it here");
            e.printStackTrace();
            throw e;
        }
    }
    
    private int stageElements(AmberTransaction txn) {
        s("staging elements...");
        
        List<AmberEdge>     edges      = sessionDao.findAlteredEdges();
        List<AmberVertex>   vertices   = sessionDao.findAlteredVertices();
        List<AmberProperty> properties = sessionDao.findAlteredProperties();
        
        int numStaged = edges.size() + vertices.size() + properties.size();
        if (numStaged == 0) return 0; // short circuit exit
        
        s("\tedges being staged: "      + edges.size());
        s("\tvertices being staged: "   + vertices.size());
        s("\tproperties being staged: " + properties.size());
        
        persistentDao.begin();
        for (AmberVertex v: vertices)     { persistentDao.insertStageVertex(v, txn.getId());   }
        for (AmberEdge e: edges)          { persistentDao.insertStageEdge(e, txn.getId());     }
        for (AmberProperty p: properties) { persistentDao.insertStageProperty(p, txn.getId()); }
        persistentDao.commit();
        
        return numStaged;
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
        
        Long txnId = txn.getId();
        
        // add an end transaction to superceded and deleted elements 
        int numEndedVertices = dao.updateSupercededVertices(txnId);
        int numEndedEdges = dao.updateSupercededEdges(txnId);

        // add an end transaction to superceded and deleted properties
        int numEndedEdgeProperties = dao.updateSupercededEdgeProperties(txnId);
        int numEndedVertexProperties = dao.updateSupercededVertexProperties(txnId);
        
        // IMPORTANT : will need to delete incident edges for deleted vertices too
        // They may not have been queried into the session, but are none the less
        // affected. This has not been implemented yet, but needs to be.
        
        // add new and modified elements
        int numInsertedVertices = dao.insertStagedVertices(txnId);
        int numInsertedEdges = dao.insertStagedEdges(txnId);

        // add their properties
        int numInsertedEdgeProperties = dao.insertStagedEdgeProperties(txnId);
        int numInsertedVertexProperties = dao.insertStagedVertexProperties(txnId);

        // just a bit of output - refactor should improve or remove this
        s("\tended vertices: "   + numEndedVertices);
        s("\tended edges: "      + numEndedEdges);
        s("\tended edge properties: " + numEndedEdgeProperties);
        s("\tended vertex properties: " + numEndedVertexProperties);
        
        s("\tinserted vertices: "   + numInsertedVertices);
        s("\tinserted edges: "      + numInsertedEdges);
        s("\tinserted edge properties: " + numInsertedEdgeProperties);
        s("\tinserted vertex properties: " + numInsertedVertexProperties);
        
    }
    
    // Convenience for debugging
    private void s(String s) {
        log.info(s);
    }

    @Override
    @Deprecated
    public void stopTransaction(Conclusion conclusion) {}

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
    
    protected List<AmberEdge> findSessionEdgesForVertex(Long vertexId, Direction direction, String... labels) {
        String labelsClause = constructStringInClause(Lists.newArrayList(labels), "AND label ");
        List<AmberEdge> edges = new ArrayList<AmberEdge>();

        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(directedFindSessionEdgesForVertex(vertexId, "WHERE v_in = :vertexId ", labelsClause));
        } 
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(directedFindSessionEdgesForVertex(vertexId, "WHERE v_out = :vertexId ", labelsClause));
        }
        return edges;
    }

    protected List<AmberEdge> directedFindSessionEdgesForVertex(Long vertexId, String directionClause, String labelsClause) {

        Handle h = sessionDbi.open();
        List<AmberEdge> edges = h.createQuery(
                "SELECT id " +
                "FROM edge " +
                directionClause +
                labelsClause +
                "ORDER BY edge_order")
                .bind("vertexId", vertexId)
                .map(new SessionEdgeMapper(this)).list();
        h.close();
        return edges;
    }

    protected List<AmberVertex> findSessionVerticesInList(List<Long> vertexIds) {

        String idsClause = constructLongInClause(vertexIds, "WHERE id ");

        Handle h = sessionDbi.open();
        List<AmberVertex> vertices = h.createQuery(
                "SELECT id " +
                "FROM vertex " +
                idsClause)
                .map(new SessionVertexMapper(this)).list();
        h.close();
        return vertices;
    }

    protected List<AmberVertex> findPersistentVerticesInList(List<Long> vertexIds) {

        String idsClause = constructLongInClause(vertexIds, "AND id ");

        Handle h = persistentDbi.open();
        List<AmberVertex> vertices = h.createQuery(
                "SELECT id, txn_start, txn_end " +
                "FROM vertex " +
                "WHERE (txn_end = 0 OR txn_end IS NULL) " +
                idsClause)
                .map(new PersistentVertexMapper(this)).list();
        h.close();
        return vertices;
    }

    protected List<AmberVertex> getVerticesById(List<Long> vertexIds) {

        loadPropertyIds.clear();
        
        // get vertices in session first
        List<AmberVertex> vertices = findSessionVerticesInList(vertexIds);

        List<Long> sessionVertexIds = new ArrayList<Long>();
        for (AmberVertex v : vertices) {
            sessionVertexIds.add((Long) v.getId());
        }
        List<Long> persistentVertexIds = new ArrayList<Long>(vertexIds);
        persistentVertexIds.removeAll(sessionVertexIds);

        if (persistentVertexIds.size() > 0) {
            List<AmberVertex> persistentVertices = findPersistentVerticesInList(persistentVertexIds);
            vertices.addAll(persistentVertices);

            if (persistentVertices.size() > 0)
                loadProperties(loadPropertyIds);
        }
        loadPropertyIds.clear();
        
        return vertices;
    }    

    public List<AmberEdge> getEdgesByIncidentVertexId(List<Long> vertexIds, Direction direction, String... label) {

        loadPropertyIds.clear();
        
        List<AmberEdge> sessionInEdges = new ArrayList<AmberEdge>();
        List<AmberEdge> sessionOutEdges = new ArrayList<AmberEdge>();
        List<AmberEdge> persistentInEdges = new ArrayList<AmberEdge>();
        List<AmberEdge> persistentOutEdges = new ArrayList<AmberEdge>();
        
        // exclude edge id lists for subsequent persistent queries
        List<Long> sessionInEdgeIds = new ArrayList<Long>();
        List<Long> sessionOutEdgeIds = new ArrayList<Long>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            sessionInEdges = getSessionEdgesByIncidentVertexIds(vertexIds, "AND v_in ", label);
            for (AmberEdge e : sessionInEdges) {
                sessionInEdgeIds.add((Long) e.getId()); 
            }
            persistentInEdges = getPersistentEdgesByIncidentVertexIds(vertexIds, "AND v_in ", sessionInEdgeIds, label);
        }

        if (direction == Direction.OUT || direction == Direction.BOTH) {
            sessionOutEdges = getSessionEdgesByIncidentVertexIds(vertexIds, "AND v_out ", label); 
            for (AmberEdge e : sessionOutEdges) {
                sessionOutEdgeIds.add((Long) e.getId()); 
            }
            persistentOutEdges = getPersistentEdgesByIncidentVertexIds(vertexIds, "AND v_out ", sessionOutEdgeIds, label); 
        }

        List<AmberEdge> edges = sessionInEdges;
        edges.addAll(sessionOutEdges);
        
        List<AmberEdge> persistentEdges = persistentInEdges;
        persistentEdges.addAll(persistentOutEdges);
        
        if (persistentEdges.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        edges.addAll(persistentEdges);
        
        return edges;
    }   

    protected List<AmberEdge> getSessionEdgesByIncidentVertexIds(List<Long> vertexIds, String inClausePrefix, String... label) {
        
        String vertexIdsClause = constructLongInClause(vertexIds, inClausePrefix);
        String labelClause = constructStringInClause(Lists.newArrayList(label), "AND label ");
        
        Handle h = sessionDbi.open();
        List<AmberEdge> edges = h.createQuery(
                "SELECT id " +
                "FROM edge " +
                "WHERE 1=1 " +
                vertexIdsClause +
                labelClause +
                "ORDER BY edge_order")
                .map(new SessionEdgeMapper(this)).list();
        h.close();
        return edges;
    }

    protected List<AmberEdge> getPersistentEdgesByIncidentVertexIds(List<Long> vertexIds, String inClausePrefix, List<Long> excludedEdgeIds, String... label) {
        
        String vertexIdsClause = constructLongInClause(vertexIds, inClausePrefix); 
        String excludedEdgeIdsClause = constructLongInClause(excludedEdgeIds, "AND id NOT "); 
        String labelClause = constructStringInClause(Lists.newArrayList(label), "AND label ");
        
        Handle h = persistentDbi.open();
        List<AmberEdge> edges = h.createQuery(
                "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
                "FROM edge " +
                "WHERE 1=1 " +
                vertexIdsClause +
                excludedEdgeIdsClause +
                labelClause +
                "AND (txn_end IS NULL OR txn_end = 0) " +
                "ORDER BY edge_order")
                .map(new PersistentEdgeMapper(this)).list();
        h.close();
        edges.removeAll(Collections.singleton(null));
        return edges;
    }
    
    public List<AmberVertex> getVerticesByAdjacentVertexId(List<Long> vertexIds, Direction direction, String... label) {
        
        // need to pull the connecting edges into the session too
        List <AmberEdge> edges = getEdgesByIncidentVertexId(vertexIds, direction, label);
        
        loadPropertyIds.clear();
        
        List<AmberVertex> sessionInVertices = new ArrayList<AmberVertex>();
        List<AmberVertex> sessionOutVertices = new ArrayList<AmberVertex>();
        List<AmberVertex> persistentInVertices = new ArrayList<AmberVertex>();
        List<AmberVertex> persistentOutVertices = new ArrayList<AmberVertex>();
        
        // exclude edge id lists for subsequent persistent queries
        List<Long> sessionInVertexIds = new ArrayList<Long>();
        List<Long> sessionOutVertexIds = new ArrayList<Long>();
        
        if (direction == Direction.IN || direction == Direction.BOTH) {
            sessionInVertices = getSessionVerticesByAdjacentVertexIds(vertexIds, Direction.IN, label);
            for (AmberVertex v : sessionInVertices) {
                sessionInVertexIds.add((Long) v.getId()); 
            }
            persistentInVertices = getPersistentVerticesByAdjacentVertexIds(vertexIds, Direction.IN, sessionInVertexIds, label);
        }

        if (direction == Direction.OUT || direction == Direction.BOTH) {
            sessionOutVertices = getSessionVerticesByAdjacentVertexIds(vertexIds, Direction.OUT, label); 
            for (AmberVertex v : sessionOutVertices) {
                sessionOutVertexIds.add((Long) v.getId()); 
            }
            persistentOutVertices = getPersistentVerticesByAdjacentVertexIds(vertexIds, Direction.OUT, sessionOutVertexIds, label); 
        }

        List<AmberVertex> vertices = sessionInVertices;
        vertices.addAll(sessionOutVertices);
        
        List<AmberVertex> persistentVertices = persistentInVertices;
        persistentVertices.addAll(persistentOutVertices);
        
        if (persistentVertices.size() > 0) loadProperties(loadPropertyIds);
        loadPropertyIds.clear();
        
        vertices.addAll(persistentVertices);
        
        return vertices;
    }   
    
    protected List<AmberVertex> getSessionVerticesByAdjacentVertexIds(List<Long> vertexIds, Direction direction, String... label) {
        
        // argument guard
        if (Direction.BOTH == direction) { 
            throw new IllegalArgumentException("Can only get a vertex from a single direction");
        }    
        
        String srcVerticesPrefix;
        String destVerticesClause;
        
        if (Direction.IN == direction) {
            srcVerticesPrefix = "AND e.v_in ";
            destVerticesClause = "AND v.id = e.v_out ";
        } else {
            srcVerticesPrefix = "AND e.v_out ";
            destVerticesClause = "AND v.id = e.v_in ";
        }
        
        String vertexIdsClause = constructLongInClause(vertexIds, srcVerticesPrefix);
        String labelClause = constructStringInClause(Lists.newArrayList(label), "AND label ");
        
        Handle h = sessionDbi.open();
        List<AmberVertex> vertices = h.createQuery(
                "SELECT v.id " +
                "FROM edge e, vertex v " +
                "WHERE 1=1 " +
                vertexIdsClause +
                destVerticesClause +
                labelClause +
                "ORDER BY e.edge_order")
                .map(new SessionVertexMapper(this)).list();
        h.close();
        return vertices;
    }

    protected List<AmberVertex> getPersistentVerticesByAdjacentVertexIds(List<Long> vertexIds, Direction direction, List<Long> excludedVertexIds, String... label) {

        // argument guard
        if (Direction.BOTH == direction) { 
            throw new IllegalArgumentException("Can only get a vertex from a single direction");
        }    

        String srcVerticesPrefix;
        String destVerticesClause;
        
        if (Direction.IN == direction) {
            srcVerticesPrefix = "AND e.v_in ";
            destVerticesClause = "AND v.id = e.v_out ";
        } else {
            srcVerticesPrefix = "AND e.v_out ";
            destVerticesClause = "AND v.id = e.v_in ";
        }
        
        String vertexIdsClause = constructLongInClause(vertexIds, srcVerticesPrefix); 
        String excludedVertexIdsClause = constructLongInClause(excludedVertexIds, "AND v.id NOT "); 
        String labelClause = constructStringInClause(Lists.newArrayList(label), "AND label ");
        
        Handle h = persistentDbi.open();
        List<AmberVertex> vertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end " +
                "FROM edge e, vertex v " +
                "WHERE 1=1 " +
                vertexIdsClause +
                excludedVertexIdsClause +
                destVerticesClause +
                labelClause +
                "AND (v.txn_end IS NULL OR v.txn_end = 0) " +
                "AND (e.txn_end IS NULL OR e.txn_end = 0) " +
                "ORDER BY e.edge_order")
                .map(new PersistentVertexMapper(this)).list();
        h.close();
        vertices.removeAll(Collections.singleton(null));
        return vertices;
    }

}

