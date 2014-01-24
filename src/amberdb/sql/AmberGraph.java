package amberdb.sql;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import amberdb.sql.dao.AmberDao;
import amberdb.sql.map.EdgeMapper;
import amberdb.sql.map.PropertyMapper;
import amberdb.sql.map.VertexMapper;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraph extends BaseGraph 
		implements Graph, TransactionalGraph, IdGenerator, ElementModifiedListener, EdgeFactory, VertexFactory {

    public Logger log = Logger.getLogger(AmberGraph.class.getName());
    
    public static final DataSource DEFAULT_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:persist","pers","pers");

    private DBI dbi;
    private AmberDao dao;

    private Set<Edge> removedEdges = new HashSet<Edge>();
    private Set<Vertex> removedVertices = new HashSet<Vertex>();

    private Set<Edge> newEdges = new HashSet<Edge>();
    private Set<Vertex> newVertices = new HashSet<Vertex>();

    private Set<Edge> modifiedEdges = new HashSet<Edge>();
    private Set<Vertex> modifiedVertices = new HashSet<Vertex>();
    
    /** Identify this graph instance */
    private String user;
    
    
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
    	dao = dbi.onDemand(AmberDao.class);
        if (!dao.schemaTablesExist()) {
            log.info("Schema doesn't exist - creating ...");
            createAmberSchema();
        }
    }
    
    
    /**
     * WARNING: This method should only ever be used for testing. It will 
     * destroy and rebuild the persistent database sans data. This will 
     * likely make people very angry if run against production.
     *
     * REFACTOR NOTE: prevent this method from being inadvertently called
     */
    public void createAmberSchema() {
        
    	dao.createVertexTable();
        dao.createEdgeTable();
        dao.createPropertyTable();
        
        dao.createSessionVertexTable();
        dao.createSessionEdgeTable();
        dao.createSessionPropertyTable();
        
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

    
	public String toString() {
		return ("ambergraph");
	}    
    
	
    @Override
    public void removeEdge(Edge e) {
    	removedEdges.add(e);
        super.removeEdge(e);
    }

    
    @Override
    public void removeVertex(Vertex v) {
    	removedVertices.add(v);
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
    	
    	log.info("vertices   in batch: " + v.id.size());
    	log.info("edges      in batch: " + e.id.size());
    	log.info("properties in batch: " + p.id.size());
    	
    	dao.suspendEdges(sessId, e.id, e.txnStart, e.txnEnd, e.vertexOut, e.vertexIn, e.label, e.order, e.state);
    	dao.suspendVertices(sessId, v.id, v.txnStart, v.txnEnd, v.state);
    	dao.suspendProperties(sessId, p.id, p.name, p.type, p.value);
    	
    	return sessId;
    }

    
    private void batchSuspendEdges(AmberEdgeBatch edges, AmberPropertyBatch properties) {
    	
    	log.info("suspending edges -- r:" + removedEdges.size() + " n:" + newEdges.size() + " m:" + modifiedEdges.size());
    	
    	for (Edge e : removedEdges) {
    		modifiedEdges.remove(e);
    		if (newEdges.remove(e)) continue;
    		edges.add(new AmberEdgeWithState((AmberEdge) e, "DEL"));
    	}
    	for (Edge e : newEdges) {
    		modifiedEdges.remove(e);
    		AmberEdge ae = (AmberEdge) e;
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

    	log.info("suspending verts -- r:" + removedVertices.size() + " n:" + newVertices.size() + " m:" + modifiedVertices.size());
    	
    	for (Vertex v : removedVertices) {
    		modifiedVertices.remove(v);
    		if (newVertices.remove(v)) continue;
    		vertices.add(new AmberVertexWithState((AmberVertex) v, "DEL"));
    	}
    	for (Vertex v : newVertices) {
    		modifiedVertices.remove(v);
    		AmberVertex av = (AmberVertex) v;
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
	public Edge newEdge(Object id, String label, Vertex inVertex, Vertex outVertex, Map<String, Object> properties, Graph graph) {
		return new AmberEdge((Long) id, label, (AmberVertex) inVertex, (AmberVertex) outVertex, properties, (AmberGraph) graph, 0L, 0L, 0L);
	}
    
	
	public void clear() {
		
		super.clear();
		
		removedEdges.clear();
		removedVertices.clear();

		newEdges.clear();
		newVertices.clear();

		modifiedEdges.clear();
		modifiedVertices.clear();
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
    	log.info("property maps resumed: " + propertyMaps.size());
    	
    	// Restore vertices to the graph before edges because 
    	// edge construction depends on vertex existence
    	List<AmberVertexWithState> vertexStateWrappers = resumeVertices(sessId);
    	for (AmberVertexWithState wrapper : vertexStateWrappers) {
    		AmberVertex vertex = wrapper.vertex; 
    		
    		String state = wrapper.state;
    		if (state.equals("DEL")) {
    			removedVertices.add(vertex);
    			continue;
    		} 
    		
    		graphVertices.add(vertex);
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
    			removedEdges.add(edge);
    			continue;
    		} 
    		
    		graphEdges.add(edge);
    		edge.replaceProperties(propertyMaps.get((Long) edge.getId()));
    		
    		if (state.equals("NEW")) {
    			newEdges.add(edge);
    		} else if (state.equals("MOD")) {
    			modifiedEdges.add(edge);
    		}
    	}
    	
    	log.info("resuming vr:" + removedVertices.size()
    			+ " vn:" + newVertices.size()
    			+ " vm:" + modifiedVertices.size()
    			+ " er:" + removedEdges.size()
    			+ " en:" + newEdges.size()
    			+ " em:" + modifiedEdges.size());
    }
    
    private List<AmberVertexWithState> resumeVertices(Long sessId) {
        Handle h = dbi.open();
        List<AmberVertexWithState> vertices = h.createQuery(
        		"SELECT id, txn_start, txn_end, state " + 
        	    "FROM sess_vertex " +
        	    "WHERE s_id = :sessId")
                .bind("sessId", sessId)
                .map(new VertexMapper(this)).list();
        h.close();
        return vertices;
    }
    
    private List<AmberEdgeWithState> resumeEdges(Long sessId) {
        Handle h = dbi.open();
        List<AmberEdgeWithState> edges = h.createQuery(
        		"SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " + 
        	    "FROM sess_edge " +
   	            "WHERE s_id = :sessId")
                .bind("sessId", sessId)
                .map(new EdgeMapper(this)).list();
        h.close();
        return edges;
    }
    
    
    public Long commit(String user, String operation) {
    	
    	Long txnId = suspend();
    	
    	// end current elements where this transaction modifies or deletes it.
    	// additionally, end edges orphaned by this procedure.
    	dao.endElements(txnId);
    	
    	// start new elements for new and modified transaction elements
    	dao.startElements(txnId);
    	
    	// *** note: need to check when adding (modding?) edges that both ends exist
    	
    	dao.insertTransaction(txnId, new Date().getTime(), user, operation);
    	
    	clear();
    	
    	// refresh local graph or something :-)
    	
    	return txnId;
    }
    
    
    public Vertex getVertex(Object id) {
    	
    	Vertex vertex = super.getVertex(id);
    	if (vertex != null) return vertex;
    	
    	if (parseId(id) == null) return null; // super may have returned null because the id didn't parse
    	
    	Handle h = dbi.open();
		AmberVertexWithState vs = h.createQuery(
				"SELECT id, txn_start, txn_end, 'AMB' state "
				+ "FROM vertex " 
				+ "WHERE id = :id "
				+ "AND txn_end = 0")
                .bind("id", parseId(id))
                .map(new VertexMapper(this)).first();

		if (vs == null) return null;
        vertex = vs.vertex;
        if (removedVertices.contains(vertex)) return null;

        AmberVertex v = (AmberVertex) vertex;
        v.replaceProperties(getElementPropertyMap((Long) v.getId(), v.txnStart, h));
        h.close();
        
        return vertex;
    } 
    
    public Edge getEdge(Object id) {
    	
    	Edge edge = super.getEdge(id);
    	if (edge != null) return edge;

    	if (parseId(id) == null) return null; // super may have returned null because the id didn't parse
    	
    	Handle h = dbi.open();
		AmberEdgeWithState es = h.createQuery(
				"SELECT id, txn_start, txn_end, label, v_in, v_out, edge_order, 'AMB' state "
				+ "FROM edge " 
				+ "WHERE id = :id "
				+ "AND txn_end = 0")
                .bind("id", parseId(id))
                .map(new EdgeMapper(this)).first();

		if (es == null) return null;
        edge = es.edge;
        if (removedEdges.contains(edge)) return null;
        
        AmberEdge e = (AmberEdge) edge;
        e.replaceProperties(getElementPropertyMap((Long) e.getId(), e.txnStart, h));
        h.close();

        return e;
    } 
    
    private Map<String, Object> getElementPropertyMap(Long elementId, Long txnStart, Handle h) {

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
        h.close();
        if (propList == null || propList.size() == 0) return propMap;
        
        for (AmberProperty p : propList) {
        	propMap.put(p.getName(), p.getValue());
        }
    	return propMap;
    }
}

