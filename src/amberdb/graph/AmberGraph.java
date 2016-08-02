package amberdb.graph;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import amberdb.graph.dao.AmberDaoH2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Transaction;
import org.skife.jdbi.v2.TransactionStatus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import amberdb.graph.dao.AmberDao;
import amberdb.graph.dao.AmberDaoMySql;
import amberdb.util.WorkUtils;
import amberdb.v2.model.Work;


public class AmberGraph extends BaseGraph 
        implements Graph, TransactionalGraph, IdGenerator, 
        ElementModifiedListener, EdgeFactory, VertexFactory {

    private static final int COMMIT_BATCH_SIZE = 4000;
    private static final int BIG_COMMIT_THRESHOLD = 20000;
    private static final Logger log = LoggerFactory.getLogger(AmberGraph.class);
    
    public static final DataSource DEFAULT_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:persist","pers","pers");

    protected DBI dbi;
    private AmberDao dao;

    String dbProduct;
    protected String tempTableEngine = "";
    protected String tempTableDrop = "";
    
    protected Map<Object, Edge> removedEdges = new HashMap<>();
    protected Map<Object, Vertex> removedVertices = new HashMap<>();
    
    private Set<Edge> newEdges = new HashSet<Edge>();
    private Set<Vertex> newVertices = new HashSet<Vertex>();

    protected Set<Edge> modifiedEdges = new HashSet<Edge>();
    protected Set<Vertex> modifiedVertices = new HashSet<Vertex>();
    
    private boolean localMode = false;
    /**
     * Local mode puts the Amber Graph in a state where it will only query for
     * elements in the current session ie: it will not look for elements in
     * the Amber Graph's persistent data store. This can speed up queries 
     * significantly. When localMode is on, AmberQueries can still be used to 
     * populate the local graph and suspend, resume and commit should also 
     * work against the persistent data store. 
     * 
     * @param localModeOn if true sets local mode to on, off if false.
     */
    public void setLocalMode(boolean localModeOn) {
        localMode = localModeOn;
    }
    
    
    public boolean inLocalMode() {
        return localMode;
    }
    
    
    /* 
     * Constructors
     */
    
    public AmberGraph() {
        initGraph(DEFAULT_DATASOURCE);
    }

    
    public AmberGraph(DataSource dataSource) {
        initGraph(dataSource);
    }

    
    private void initGraph(DataSource dataSource) {

        idGen = this;
        edgeFactory = this;
        vertexFactory = this;
        elementModListener = this;

        dbi = new DBI(dataSource);
        dao = selectDao(dataSource);
        if (!dao.schemaTablesExist()) {
            log.trace("Graph schema doesn't exist - creating ...");
            createAmberSchema();
        }
    }
    
    
    private AmberDao selectDao(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            dbProduct = conn.getMetaData().getDatabaseProductName();
            log.debug("Amber database type is {}", dbProduct);
        } catch (SQLException e) {
            log.trace("could not determine the database type - assuming it is H2");
            log.trace(e.getMessage());
        }

        if (dbProduct.equals("MySQL")) {
            tempTableEngine = "ENGINE=memory";
            tempTableDrop = "TEMPORARY";
            return dbi.onDemand(AmberDaoMySql.class);
        } else if (dbProduct.equals("H2")) {
            return dbi.onDemand(AmberDaoH2.class);
        } else { // default to H2
            return dbi.onDemand(AmberDaoH2.class);
        }
    }
    
    
    public void createAmberSchema() {
        
        dao.createVertexTable();
        dao.createEdgeTable();
        dao.createPropertyTable();

        dao.createVertexIndex();
        
        dao.createEdgeIndex();
        dao.createEdgeInVertexIndex();
        dao.createEdgeOutVertexIndex();
        dao.createEdgeLabelIndex();
        dao.createEdgeInTraversalIndex();
        dao.createEdgeOutTraversalIndex();
        
        dao.createPropertyIndex();
        dao.createPropertyNameIndex();
        if (dbProduct.equals("MySQL")) {
            dao.createPropertyValueIndex();
            dao.createPropertyNameValueIndex();
        }
        dao.createVertexTxnEndIndex();
        dao.createEdgeTxnEndIndex();
        dao.createPropertyTxnEndIndex();

        dao.createSessionVertexTable();
        dao.createSessionEdgeTable();
        dao.createSessionPropertyTable();

        dao.createSessionVertexIndex();
        dao.createSessionEdgeIndex();
        dao.createSessionPropertyIndex();
        
        dao.createSessionEdgeIdStateIndex();
        dao.createSessionVertexIdStateIndex();
        dao.createSessionPropertyIdStateIndex();

        dao.createIdGeneratorTable();
        dao.createTransactionTable();
        dao.createV2Tables();
       
        newId(); // seed generator with id > 0
    }

    
    /**
     * Generate a new id unique within the persistent data store.
     *  
     * @return A unique persistent id
     */
    public Long newId() {
        dao.begin();
        Long newId = dao.newId();
        // occasionally clean up the id generation table (every 1000 pis or so)
        if (newId % 1000 == 995) {
            dao.garbageCollectIds(newId);
        }
        dao.commit();
        return newId;
    }    
    

    public void elementModified(Object element) {
        if (element instanceof Edge) {
            modifiedEdges.add((Edge) element);
        } else if (element instanceof Vertex) {
            modifiedVertices.add((Vertex) element);
        }
    }
    
    
    public AmberDao dao() {
        return dao;
    }

    
    public DBI dbi() {
        return dbi;
    }
    
    public String toString() {
        return ("ambergraph");
    }    
    
    
    @Override
    public void removeEdge(Edge e) {
        removedEdges.put(e.getId(), e);
        super.removeEdge(e);
    }

    
    @Override
    public void removeVertex(Vertex v) {
        
        // guard
        if (!graphVertices.containsKey(v.getId())) {
            throw new IllegalStateException("Cannot remove non-existent vertex : " + v.getId());
        }
        
        for (Edge e : v.getEdges(Direction.BOTH)) {
            removedEdges.put(e.getId(), e);
        }
        removedVertices.put(v.getId(), v);
        super.removeVertexWithoutGuard(v); // existence check already performed above
    }
    
    
    @Override
    public Edge addEdge(Object id, Vertex out, Vertex in, String label) {
        Edge edge = super.addEdge(id, out, in, label);
        newEdges.add(edge);
        return edge;
    }
    
    
    @Override
    public Vertex addVertex(Object id) {
        Vertex vertex = super.addVertex(id);
        newVertices.add(vertex);
        return vertex;
    }

    
    public Long suspend() {
        
        Long sessId = null;
        if (getModifiedElementCount() > BIG_COMMIT_THRESHOLD) {
            log.warn("Graph to be committed exceeds {} elements. Using big suspend to process.",
                    BIG_COMMIT_THRESHOLD);
       
            sessId = suspendBig();
            clear();
       
        } else {

            // set up batch sql data structures
            sessId = newId();

            AmberEdgeBatch e = new AmberEdgeBatch();
            AmberVertexBatch v = new AmberVertexBatch();
            AmberPropertyBatch p = new AmberPropertyBatch();

            batchSuspendEdges(e, p);
            batchSuspendVertices(v, p);

            log.debug("batches -- vertices:{} edges:{} properties:{}",
                    v.id.size(), e.id.size(), p.id.size());
            
            
            
            dao.begin();
            
            dao.createWorks(getNewWorks());
            dao.deleteWorks(getDeletedWorks());
            dao.updateWorks(getModifiedWorks());
            dao.suspendEdges(sessId, e.id, e.txnStart, e.txnEnd, e.vertexOut,
                    e.vertexIn, e.label, e.order, e.state);
            dao.suspendVertices(sessId, v.id, v.txnStart, v.txnEnd, v.state);
            dao.suspendProperties(sessId, p.id, p.name, p.type, p.value);
            
            dao.commit();
        }

        

        return sessId;
    }

    
	private List<Work> getNewWorks() {
		List<Work> works = new ArrayList<>();
		for (Vertex v : newVertices) {
			if ("Work".equals(v.getProperty("type"))) {
				works.add(WorkUtils.convert((AmberVertex) v));
			}
		}
		return works;
	}

	private List<Work> getDeletedWorks() {
		List<Work> works = new ArrayList<>();
		for (Vertex v : removedVertices.values()) {
			if ("Work".equals(v.getProperty("type"))) {
				works.add(WorkUtils.convert((AmberVertex) v));
			}
		}
		return works;
	}

	private List<Work> getModifiedWorks() {
		List<Work> works = new ArrayList<>();
		for (Vertex v : modifiedVertices) {
			if ("Work".equals(v.getProperty("type"))) {
				works.add(WorkUtils.convert((AmberVertex) v));
			}
		}
		return works;
	}
	
	
	private void batchSuspendEdges(AmberEdgeBatch edges, AmberPropertyBatch properties) {
        
        log.debug("suspending edges -- deleted:{} new:{} modified:{}",
                removedEdges.size(), newEdges.size(), modifiedEdges.size());
        
        for (Edge e : removedEdges.values()) {
            modifiedEdges.remove(e);
            if (newEdges.remove(e)) continue;
            edges.add(new AmberEdgeWithState((AmberEdge) e, "DEL"));
        }

        for (Edge e : newEdges) {
            AmberEdge ae = (AmberEdge) e;
            modifiedEdges.remove(e);
            edges.add(new AmberEdgeWithState(ae, "NEW"));
            properties.add((Long) ae.getId(), ae.getProperties());
        }

        for (Edge e : modifiedEdges) {
            AmberEdge ae = (AmberEdge) e;
            edges.add(new AmberEdgeWithState(ae, "MOD"));
            properties.add((Long) ae.getId(), ae.getProperties());
        }
    }
    
    
    private void batchSuspendVertices(AmberVertexBatch vertices, AmberPropertyBatch properties) {

        log.debug("suspending verts -- deleted:{} new:{} modified:{} ", 
                removedVertices.size(), newVertices.size(), modifiedVertices.size());
        
        for (Vertex v : removedVertices.values()) {
            modifiedVertices.remove(v);
            if (newVertices.remove(v)) continue;
            vertices.add(new AmberVertexWithState((AmberVertex) v, "DEL"));
        }

        for (Vertex v : newVertices) {
            AmberVertex av = (AmberVertex) v;
            modifiedVertices.remove(v);
            vertices.add(new AmberVertexWithState(av, "NEW"));
            properties.add((Long) av.getId(), av.getProperties());
        }

        for (Vertex v : modifiedVertices) {
            AmberVertex av = (AmberVertex) v;
            vertices.add(new AmberVertexWithState(av, "MOD"));
            properties.add((Long) av.getId(), av.getProperties());
        }
    }

    
    @Override
    public Vertex newVertex(Object id, Map<String, Object> properties, Graph graph) {
        return new AmberVertex((Long) id, properties, (AmberGraph) graph, 0L, 0L);
    }

    
    @Override
    public Edge newEdge(Object id, String label, Vertex outVertex, Vertex inVertex, 
            Map<String, Object> properties, Graph graph) {
        return new AmberEdge((Long) id, label, (AmberVertex) outVertex, (AmberVertex) inVertex, 
                properties, (AmberGraph) graph, 0L, 0L, 0);
    }
    
    
    public void clear() {
        super.clear();
        clearChangeSets();
    }
    
    
    private void clearChangeSets() {
        removedEdges.clear();
        removedVertices.clear();

        newEdges.clear();
        newVertices.clear();

        modifiedEdges.clear();
        modifiedVertices.clear();
    }
    

    public void destroySession(Long sessId) {
        log.debug("removing session {} from the session tables", sessId);
        dao.clearSession(sessId);
    }
    
    
    public void resume(Long sessId) {

        clear();
        
        // get, then separate the properties into the maps for their elements
        List<AmberProperty> properties = dao.resumeProperties(sessId);
        Map<Long, Map<String, Object>> propertyMaps = new HashMap<>();
        for (AmberProperty prop : properties) {
            Long id = prop.getId();
            if (propertyMaps.get(id) == null) {
                propertyMaps.put(id, new HashMap<String, Object>());
            }
            propertyMaps.get(id).put(prop.getName(), prop.getValue());
        }
        log.debug("property maps resumed: {}", propertyMaps.size());
        
        // Restore vertices to the graph before edges because 
        // edge construction depends on vertex existence
        List<AmberVertexWithState> vertexStateWrappers = resumeVertices(sessId);
        for (AmberVertexWithState wrapper : vertexStateWrappers) {
            AmberVertex vertex = wrapper.vertex; 
            
            String state = wrapper.state;
            if (state.equals("DEL")) {
                removedVertices.put(vertex.getId(), vertex);
                continue;
            } 
            
            addVertexToGraph(vertex);
            vertex.replaceProperties(propertyMaps.get((Long) vertex.getId()));
            
            if (state.equals("NEW")) {
                newVertices.add(vertex);
            } else if (state.equals("MOD")) {
                modifiedVertices.add(vertex);
            }
        }
        
        List<AmberEdgeWithState> edgeStateWrappers = resumeEdges(sessId);
        for (AmberEdgeWithState wrapper : edgeStateWrappers) {
            AmberEdge edge = wrapper.edge; 
            
            String state = wrapper.state;
            if (state.equals("DEL")) {
                removedEdges.put(edge.getId(), edge);
                continue;
            } 

            if (edge.inId == 0 || edge.outId == 0) {
                log.warn("Stale session resumed: Failed to restore [{}] from session {}", edge, sessId);
                log.warn("-- One or both of the incident vertices was deleted from the database after session suspension but before resumption.");
                continue;
            }

            addEdgeToGraph(edge);
            edge.replaceProperties(propertyMaps.get((Long) edge.getId()));
            
            if (state.equals("NEW")) {
                newEdges.add(edge);
            } else if (state.equals("MOD")) {
                modifiedEdges.add(edge);
            }
        }
        
        log.debug("resuming -- vertices - deleted:{} new:{} modified:{} -- edges - deleted:{} new:{} modified:{}", 
                removedVertices.size(), newVertices.size(), modifiedVertices.size(), 
                removedEdges.size(), newEdges.size(), modifiedEdges.size());
    }
    
    private List<AmberVertexWithState> resumeVertices(Long sessId) {
        List<AmberVertexWithState> vertices = new ArrayList<>();
        try (Handle h = dbi.open()) {
            vertices = h.createQuery(
                "SELECT id, txn_start, txn_end, state " + 
                "FROM sess_vertex " +
                "WHERE s_id = :sessId")
                .bind("sessId", sessId)
                .map(new VertexMapper(this)).list();
        }
        return vertices;
    }
    
    private List<AmberEdgeWithState> resumeEdges(Long sessId) {
        List<AmberEdgeWithState> edges = new ArrayList<>();
        try (Handle h = dbi.open()) {
            edges = h.createQuery(
                "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " + 
                "FROM sess_edge " +
                "WHERE s_id = :sessId")
                .bind("sessId", sessId)
                .map(new EdgeMapper(this, false)).list();
        }
        return edges;
    }
    
    private int getModifiedElementCount() {
        return newVertices.size() +
                modifiedVertices.size() +
                removedVertices.size() +
                newEdges.size() +
                modifiedEdges.size() +
                removedEdges.size();
    }

    public Long commit(String user, String operation) {

        if (getModifiedElementCount() > BIG_COMMIT_THRESHOLD) {
            log.warn("Graph to be committed exceeds {} elements. Using big commit to process.", BIG_COMMIT_THRESHOLD);
            return commitBig(user, operation);
        }
        
        Long txnId = suspend();
        commitSqlWrappedWithRetry(txnId, user, operation, 4, 300);
        clearChangeSets();
        return txnId;
    }

    
    @Override
    public void commit() {
        commit("amberdb", "commit");
    }

    
    @Override
    public Vertex getVertex(Object id) {
        return getVertex(id, localMode);
    } 
    

    @Override
    public Iterable<Edge> getEdges() {
        if (!localMode) {
            AmberEdgeQuery avq = new AmberEdgeQuery(this); 
            List<Edge> amberEdges = avq.execute();
            Set<Edge> edges = Sets.newHashSet(super.getEdges());
            for (Edge e : amberEdges) {
                edges.add(e);
            }
            return edges;
        } else {
            return super.getEdges();
        }
    }
    

    /**
     * Currently, 'label' cannot be used as the key to return matching labeled
     * edges.
     */
    @Override
    public Iterable<Edge> getEdges(String key, Object value) {
        if (!localMode) {
            AmberEdgeQuery avq = new AmberEdgeQuery(this); 
            avq.addCriteria(key, value);
            List<Edge> amberEdges = avq.execute();
            Set<Edge> edges = Sets.newHashSet(super.getEdges(key, value));
            for (Edge e : amberEdges) {
                edges.add(e);
            }
            return edges;
        } else {
            return super.getEdges(key, value);
        }    
    }

    
    protected Vertex getVertex(Object id, boolean localOnly) {
        
        Vertex vertex = super.getVertex(id);
        if (vertex != null) return vertex;
        if (localOnly) return null;
        
        // super may have returned null because the id didn't parse
        if (parseId(id) == null) return null;
       
        AmberVertexWithState vs;
        try (Handle h = dbi.open()) {
            vs = h.createQuery(
                "SELECT id, txn_start, txn_end, 'AMB' state "
                + "FROM vertex " 
                + "WHERE id = :id "
                + "AND txn_end = 0")
                .bind("id", parseId(id))
                .map(new VertexMapper(this)).first();

            if (vs == null) return null;
            vertex = vs.vertex;
            if (removedVertices.containsKey(vertex.getId())) return null;

            AmberVertex v = (AmberVertex) vertex;
            v.replaceProperties(getElementPropertyMap((Long) v.getId(), v.txnStart, h));
            addVertexToGraph(v);
        }
        
        return vertex;
    } 

    
    @Override
    public Edge getEdge(Object id) {
        return getEdge(id, localMode);
    } 
    
    
    protected Edge getEdge(Object id, boolean localOnly) {
        
        Edge edge = super.getEdge(id);
        if (edge != null) return edge;
        if (localOnly) return null;
        
        // super may have returned null because the id didn't parse
        if (parseId(id) == null) return null;
        
        AmberEdge e;
        try (Handle h = dbi.open()) {
            AmberEdgeWithState es = h.createQuery(
                "SELECT id, txn_start, txn_end, label, v_in, v_out, edge_order, 'AMB' state "
                + "FROM edge " 
                + "WHERE id = :id "
                + "AND txn_end = 0")
                .bind("id", parseId(id))
                .map(new EdgeMapper(this, false)).first();

            if (es == null) return null;
            edge = es.edge;
            if (removedEdges.containsKey(edge.getId())) return null;
        
            e = (AmberEdge) edge;
            e.replaceProperties(getElementPropertyMap((Long) e.getId(), e.txnStart, h));
            addEdgeToGraph(e);
        }

        return e;
    } 
    
    
    protected Map<String, Object> getElementPropertyMap(Long elementId, Long txnStart, Handle h) {

        Map<String, Object> propMap = new HashMap<String, Object>();

        List<AmberProperty> propList = h.createQuery(
                "SELECT id, name, type, value "
                + "FROM property " 
                + "WHERE id = :id "
                + "AND txn_start = :txnStart "
                + "AND txn_end = 0")
                .bind("id", elementId)
                .bind("txnStart", txnStart)
                .map(new PropertyMapper()).list();
        if (propList == null || propList.size() == 0) return propMap;
        
        for (AmberProperty p : propList) {
            propMap.put(p.getName(), p.getValue());
        }
        return propMap;
    }
    

    /**
     * Notes on Tinkerpop Graph interface method implementations for
     * 
     *    Iterable<Edge> getEdges()
     *    Iterable<Edge> getEdges(String key, Object value)
     *    Iterable<Vertex> getVertices()
     *    Iterable<Vertex> getVertices(String key, Object value)
     * 
     * To avoid crashing a large amber system these methods limit the number
     * of elements returned from persistent storage (currently 10000).
     */
    
    @Override
    public Iterable<Vertex> getVertices() {
        if (!localMode) {  
            new AmberVertexQuery(this).execute();
        }
        return super.getVertices();
    }
    
    
    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        if (!localMode) {
            AmberVertexQuery avq = new AmberVertexQuery(this); 
            avq.addCriteria(key, value);
            avq.execute(); 
        }
        return super.getVertices(key, value); 
    }

    
    /**
     * Required for searching on values in json encoded string lists. Needed in Banjo
     * @param key The name of the property containing a json encoded string list 
     * @param value The value to search for in the list
     * @return Any matching vertices
     */
    public Iterable<Vertex> getVerticesByJsonListValue(String key, String value) {

        if (!localMode) {
            AmberVertexQuery avq = new AmberVertexQuery(this); 
            avq.executeJsonValSearch(key, value);
        }
        
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (Vertex vertex : graphVertices.values()) {
            String s = vertex.getProperty(key);
            if (s != null && s.contains("\""+value+"\"")) {
                vertices.add(vertex);
            }
        }
        return vertices;        
    }    

    
    /**
     * Used by AmberVertex.
     */
    protected void getBranch(Long id, Direction direction, String[] labels) {
        AmberQuery q = new AmberQuery(id, this);
        q.branch(Lists.newArrayList(labels), direction);
        q.execute();
    }
    
    
    public AmberQuery newQuery(Long id) {
        return new AmberQuery(id, this);
    }


    public AmberQuery newQuery(List<Long> ids) {
        return new AmberQuery(ids, this);
    }
    

    public AmberMultipartQuery newMultipartQuery(List<Long> ids) {
        return new AmberMultipartQuery(this, ids);
    }
    
    
    public AmberMultipartQuery newMultipartQuery(Long... ids) {
        return new AmberMultipartQuery(this, ids);
    }
    
    
    public AmberVertexQuery newVertexQuery() {
        return new AmberVertexQuery(this);
    }
    
    
    @Override
    public void shutdown() {
        dao.close();
        super.shutdown();
    }
    
    
    public List<AmberTransaction> getTransactionsByVertexId(Long id) {
        return dao.getTransactionsByVertexId(id); 
    }


    public List<AmberTransaction> getTransactionsByEdgeId(Long id) {
        return dao.getTransactionsByEdgeId(id); 
    }
    
    
    public AmberTransaction getTransaction(Long id) {
        return dao.getTransaction(id);
    }
    
    
    public AmberTransaction getFirstTransactionForVertexId(Long id) {
        return dao.getFirstTransactionForVertexId(id);
    }


    public AmberTransaction getFirstTransactionForEdgeId(Long id) {
        return dao.getFirstTransactionForEdgeId(id);
    }


    private void commitSqlWrappedWithRetry(Long txnId, String user, String operation, int retries, int backoffDelay) {
        int tryCount = 0;
        int backoff = backoffDelay;
        retryLoop: while (true) {
            try {
                dao.begin();
                // End current elements where this transaction modifies or deletes them.
                // Additionally, end edges orphaned by this procedure.
                log.debug("ending elements");
                dao.endElements(txnId);
                // start new elements for new and modified transaction elements
                log.debug("starting elements");
                dao.startElements(txnId);
                // Refactor note: need to check when adding (modding?) edges that both ends exist
                dao.insertTransaction(txnId, new Date().getTime(), user, operation);
                dao.commit();
                log.debug("commit complete");
                break retryLoop;
            } catch (RuntimeException e) {
                if (tryCount < retries) {
                    log.warn("AmberDb commit failed: Reason: {}\n" +
                            "Retry after {} milliseconds", e.getMessage(), backoff);
                    tryCount++;
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        log.error("Backoff delay failed :", ie); // noted
                    }
                    backoff = backoff *2;
                } else {
                    log.error("AmberDb commit failed after {} retries: Reason:", retries, e);
                    throw e;
                }
            }
        }
    }


    public String getTempTableDrop() {
        return tempTableDrop;
    }


    public String getTempTableEngine() {
        return tempTableEngine;
    }


    private void bigSuspendVertices(Long sessId) {

        AmberVertexBatch vertices = new AmberVertexBatch();
        AmberPropertyBatch properties = new AmberPropertyBatch();

        log.debug("suspending verts -- deleted:{} new:{} modified:{} ", 
                removedVertices.size(), newVertices.size(), modifiedVertices.size());

        int batchLimit = 0;
        for (Vertex v : removedVertices.values()) {
            modifiedVertices.remove(v);
            if (newVertices.remove(v)) continue;
            vertices.add(new AmberVertexWithState((AmberVertex) v, "DEL"));

            batchLimit++;
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched vertex marshalling");
                dao.suspendVertices(sessId, vertices.id, vertices.txnStart, vertices.txnEnd, vertices.state);
                vertices.clear();
                batchLimit = 0;
            }
        }

        for (Vertex v : graphVertices.values()) {
            AmberVertex av = (AmberVertex) v;
            if (newVertices.contains(v)) {
                modifiedVertices.remove(v); // a modified new vertex is just a new vertex
                vertices.add(new AmberVertexWithState(av, "NEW"));
                properties.add((Long) av.getId(), av.getProperties());
            } else if (modifiedVertices.contains(v)) {
                vertices.add(new AmberVertexWithState(av, "MOD"));
                properties.add((Long) av.getId(), av.getProperties());
            }
            batchLimit++;
            batchLimit += (av.getProperties() == null) ? 0 : av.getProperties().size();
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched vertex marshalling");
                dao.suspendVertices(sessId, vertices.id, vertices.txnStart, vertices.txnEnd, vertices.state);
                dao.suspendProperties(sessId, properties.id, properties.name, properties.type, properties.value);
                vertices.clear();
                properties.clear();
                batchLimit = 0;
            }
        }
        dao.suspendVertices(sessId, vertices.id, vertices.txnStart, vertices.txnEnd, vertices.state);
        dao.suspendProperties(sessId, properties.id, properties.name, properties.type, properties.value);
    }


    private void bigSuspendEdges(Long sessId) {

        AmberEdgeBatch edges = new AmberEdgeBatch();
        AmberPropertyBatch properties = new AmberPropertyBatch();

        log.debug("suspending edges -- deleted:{} new:{} modified:{}", 
                removedEdges.size(), newEdges.size(), modifiedEdges.size());

        int batchLimit = 0;
        for (Edge e : removedEdges.values()) {
            modifiedEdges.remove(e);
            if (newEdges.remove(e)) continue;
            edges.add(new AmberEdgeWithState((AmberEdge) e, "DEL"));

            batchLimit++;
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched edge marshalling");
                dao.suspendEdges(sessId, edges.id, edges.txnStart, edges.txnEnd, 
                        edges.vertexOut, edges.vertexIn, edges.label, edges.order, edges.state);
                edges.clear();
                batchLimit = 0;
            }
        }

        for (Edge e : graphEdges.values()) {
            AmberEdge ae = (AmberEdge) e;
            if (newEdges.contains(e)) {
                modifiedEdges.remove(e); // a modified new edge is just a new edge
                edges.add(new AmberEdgeWithState(ae, "NEW"));
                properties.add((Long) ae.getId(), ae.getProperties());
            } else if (modifiedEdges.contains(e)) {
                edges.add(new AmberEdgeWithState(ae, "MOD"));
                properties.add((Long) ae.getId(), ae.getProperties());
            }
            batchLimit++;
            batchLimit += (ae.getProperties() == null) ? 0 : ae.getProperties().size();
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched edge marshalling");
                dao.suspendEdges(sessId, edges.id, edges.txnStart, edges.txnEnd, 
                        edges.vertexOut, edges.vertexIn, edges.label, edges.order, edges.state);
                dao.suspendProperties(sessId, properties.id, properties.name, properties.type, properties.value);
                edges.clear();
                properties.clear();
                batchLimit = 0;
            }
        }
        dao.suspendEdges(sessId, edges.id, edges.txnStart, edges.txnEnd, 
                edges.vertexOut, edges.vertexIn, edges.label, edges.order, edges.state);
        dao.suspendProperties(sessId, properties.id, properties.name, properties.type, properties.value);
    }


    public Long suspendBig() {

        // set up batch sql data structures
        final Long sessId = newId();
        dao.inTransaction(new Transaction<Long, AmberDao>() {
            @Override
            public Long inTransaction(AmberDao dao,
                    TransactionStatus transactionStatus) throws Exception {
                bigSuspendEdges(sessId);
                bigSuspendVertices(sessId);
                log.info("finished big suspend");
                return sessId;
            }
        });

        return sessId;
    }

    
    public Long commitBig(String user, String operation) {

        Long txnId = suspendBig();
        commitSqlWrappedWithRetry(txnId, user, operation, 4, 300);
        log.debug("Commence clearing session");
        dao.clearSession(txnId);
        log.debug("Finished clearing session");

        clearChangeSets();
        return txnId;
    }


    public void commitBig() {
        commitBig("amberdb", "commit");
    }


    public List<AmberVertex> getVerticesByTransactionId(Long id) {
        try (Handle h = dbi.open()) {
            List<AmberVertexWithState> vs = h.createQuery(
                "(SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM transaction t, vertex v "
                + "WHERE t.id = :id "
                + "AND v.txn_start = t.id) "
                + "UNION "
                + "(SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM transaction t, vertex v "
                + "WHERE t.id = :id "
                + "AND v.txn_end = t.id) "
                + "ORDER BY id")
                .bind("id", id)
                .map(new VertexMapper(this)).list();

            List<AmberVertex> vertices = new ArrayList<>();
            for (AmberVertexWithState v : vs) {
                vertices.add(v.vertex);
            }
            return vertices;
        }
    }


    public List<AmberEdge> getEdgesByTransactionId(Long id) {

        try (Handle h = dbi.open()) {
            List<AmberEdgeWithState> es = h.createQuery(
                "(SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' state "
                + "FROM transaction t, edge e "
                + "WHERE t.id = :id "
                + "AND e.txn_start = t.id) "
                + "UNION "
                + "(SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' state "
                + "FROM transaction t, edge e "
                + "WHERE t.id = :id "
                + "AND e.txn_end = t.id) "
                + "ORDER BY id")
                .bind("id", id)
                .map(new EdgeMapper(this, false)).list();

            List<AmberEdge> edges = new ArrayList<>();
            for (AmberEdgeWithState e : es) {
                edges.add(e.edge);
            }
            return edges;
        }
    }

    public List<Vertex> getRemovedVertices() {
        return new ArrayList(removedVertices.values());
    }

    public List<Vertex> getNewVertices() {
        return new ArrayList<>(newVertices);
    }

    public List<Vertex> getModifiedVertices() {
        return new ArrayList(modifiedVertices);
    }

    public List<Edge> getRemovedEdges() {
        return new ArrayList(removedEdges.values());
    }

    public List<Edge> getNewEdges() {
        return new ArrayList<>(newEdges);
    }

    public List<Edge> getModifiedEdges() {
        return new ArrayList(modifiedEdges);
    }
}

