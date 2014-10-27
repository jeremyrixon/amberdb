package amberdb.graph;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import amberdb.graph.dao.AmberDao;
import amberdb.graph.dao.AmberDaoH2;
import amberdb.graph.dao.AmberDaoMySql;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraph extends BaseGraph 
        implements Graph, TransactionalGraph, IdGenerator, 
        ElementModifiedListener, EdgeFactory, VertexFactory {


    private static final Logger log = LoggerFactory.getLogger(AmberGraph.class);
    
    public static final DataSource DEFAULT_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:persist","pers","pers");

    protected DBI dbi;
    private AmberDao dao;

    String dbProduct;
    
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
        
        dao.createPropertyIndex();
        dao.createPropertyNameIndex();
        if (dbProduct.equals("MySQL")) {
            dao.createPropertyValueIndex();
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
        
        dao.createIdGeneratorTable();
        dao.createTransactionTable();
       
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
        removedVertices.put(v.getId(), v);
        super.removeVertex(v);
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
    
        // set up batch sql data structures
        Long sessId = newId();
        
        AmberEdgeBatch e = new AmberEdgeBatch();
        AmberVertexBatch v = new AmberVertexBatch();
        AmberPropertyBatch p = new AmberPropertyBatch();

        batchSuspendEdges(e, p);
        batchSuspendVertices(v, p);
        
        log.debug("batches -- vertices:{} edges:{} properties:{}", v.id.size(), e.id.size(), p.id.size() );
        
        dao.begin();
        
        dao.suspendEdges(sessId, e.id, e.txnStart, e.txnEnd, e.vertexOut, e.vertexIn, e.label, e.order, e.state);
        dao.suspendVertices(sessId, v.id, v.txnStart, v.txnEnd, v.state);
        dao.suspendProperties(sessId, p.id, p.name, p.type, p.value);
        
        dao.commit();
        
        return sessId;
    }

    
    private void batchSuspendEdges(AmberEdgeBatch edges, AmberPropertyBatch properties) {
        
        log.debug("suspending edges -- deleted:{} new:{} modified:{}", 
                removedEdges.size(), newEdges.size(), modifiedEdges.size());
        
        for (Edge e : removedEdges.values()) {
            modifiedEdges.remove(e);
            if (newEdges.remove(e)) continue;
            edges.add(new AmberEdgeWithState((AmberEdge) e, "DEL"));
        }
        
        for (Edge e : graphEdges.values()) {
            AmberEdge ae = (AmberEdge) e;
            if (newEdges.contains(e)) {
                modifiedEdges.remove(e); // a modified new edge is just a new edge
                edges.add(new AmberEdgeWithState(ae, "NEW"));
            } else if (modifiedEdges.contains(e)) {
                edges.add(new AmberEdgeWithState(ae, "MOD"));
            } else {
                edges.add(new AmberEdgeWithState(ae, "AMB"));
            }
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

        for (Vertex v : graphVertices.values()) {
            AmberVertex av = (AmberVertex) v;
            if (newVertices.contains(v)) {
                modifiedVertices.remove(v); // a modified new vertex is just a new vertex
                vertices.add(new AmberVertexWithState(av, "NEW"));
            } else if (modifiedVertices.contains(v)) {
                vertices.add(new AmberVertexWithState(av, "MOD"));
            } else {
                vertices.add(new AmberVertexWithState(av, "AMB"));
            }
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
        Map<Long, Map<String, Object>> propertyMaps = new HashMap<Long, Map<String, Object>>();
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
        List<AmberVertexWithState> vertices = new ArrayList<AmberVertexWithState>();
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
        List<AmberEdgeWithState> edges = new ArrayList<AmberEdgeWithState>();
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
    
    
    public Long commit(String user, String operation) {
        
        Long txnId = suspend();
        dao.begin();

        // End current elements where this transaction modifies or deletes them.
        // Additionally, end edges orphaned by this procedure.
        dao.endElements(txnId);
        
        // start new elements for new and modified transaction elements
        dao.startElements(txnId);
        
        // Refactor note: need to check when adding (modding?) edges that both ends exist
        dao.insertTransaction(txnId, new Date().getTime(), user, operation);
        dao.commit();
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
    

    /**
     * Currently, 'label' cannot be used as the key to return matching labeled
     * edges.
     */
    @Override
    public Iterable<Edge> getEdges() {
        if (!localMode) {
            AmberEdgeQuery avq = new AmberEdgeQuery(this); 
            avq.execute(); 
        }
        return super.getEdges(); 
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
            avq.execute(); 
        }
        return super.getEdges(key, value); 
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
    
    
    public List<AmberVertex> getVerticesByTransactionsId(Long id) {
        List<AmberVertex> vertices = new ArrayList<>();
        for (AmberVertexWithState vs : dao.getVerticesByTransactionId(id)) {
            vertices.add(vs.vertex); 
        }
        return vertices;
    }


    public List<AmberEdge> getEdgesByTransactionsId(Long id) {
        List<AmberEdge> edges = new ArrayList<>();
        for (AmberEdgeWithState es : dao.getEdgesByTransactionId(id)) {
            edges.add(es.edge); 
        }
        return edges;
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
}

