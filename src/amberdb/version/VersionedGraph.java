package amberdb.version;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import amberdb.version.dao.VersionDao;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;


public class VersionedGraph {

    
    Map<Long, VersionedEdge>   graphEdges = new HashMap<>();
    Map<Long, VersionedVertex> graphVertices = new HashMap<>();
    
    protected Map<Long, Set<VersionedEdge>> inEdgeSets = new HashMap<>();
    protected Map<Long, Set<VersionedEdge>> outEdgeSets = new HashMap<>();

    public Logger log = Logger.getLogger(VersionedGraph.class.getName());
    
    public static final DataSource DEFAULT_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:persist","pers","pers");

    protected DBI dbi;
    private VersionDao dao;

    private boolean localMode = false;
    public void setLocalMode(boolean localModeOn) {
        localMode = localModeOn;
    }
    public boolean inLocalMode() {
        return localMode;
    }
    

    public VersionedVertex removeVertex(Long id) {
        
        VersionedVertex v = graphVertices.remove(id);
        if (v != null) {
            Set<VersionedEdge> edges;
            edges = inEdgeSets.remove(id);
            if (edges != null) {
                for (VersionedEdge edge : edges) {
                    graphEdges.remove(edge.getId());
                }
            }
            edges = outEdgeSets.remove(id);
            if (edges != null) {
                for (VersionedEdge edge : edges) {
                    graphEdges.remove(edge.getId());
                }
            }
        }
        return v;
    }

    
    public VersionedEdge removeEdge(Long id) {
        
        VersionedEdge e = graphEdges.remove(id);
        if (e != null) {
            for (Set<VersionedEdge> set : inEdgeSets.values()) {
                set.remove(e);
            }
            for (Set<VersionedEdge> set : outEdgeSets.values()) {
                set.remove(e);
            }
        }
        return e;
    }

    
    public VersionedGraph() {
        initGraph(DEFAULT_DATASOURCE);
    }

    
    public VersionedGraph(DataSource dataSource) {
        initGraph(dataSource);
    }

    
    public VersionedGraph(DBI dbi) {
        this.dbi = dbi;
        dao = this.dbi.onDemand(VersionDao.class);
    }
    
    
    private void initGraph(DataSource dataSource) {
        dbi = new DBI(dataSource);
        dao = dbi.onDemand(VersionDao.class);
    }
    
    
    public VersionDao dao() {
        return dao;
    }

    
    public DBI dbi() {
        return dbi;
    }

    
    protected void addEdgeToGraph(VersionedEdge e) {
        graphEdges.put(e.getId(), e);
        for (TEdge te : e.edges) {
            
            Set<VersionedEdge> inEdgeSet = inEdgeSets.get(te.outId);
            if (inEdgeSet == null) {
                getVertex(te.outId); // load the missing vertex
            }
            inEdgeSets.get(te.outId).add(e);
            
            Set<VersionedEdge> outEdgeSet = outEdgeSets.get(te.inId);
            if (outEdgeSet == null) {
                getVertex(te.inId); // load the missing vertex
            }
            outEdgeSets.get(te.inId).add(e);
        }
    }

    
    protected void addVertexToGraph(VersionedVertex v) {
        Long id = v.getId();
        graphVertices.put(id, v);
        
        if (inEdgeSets.get(id) == null) inEdgeSets.put(id, new HashSet<VersionedEdge>());
        if (outEdgeSets.get(id) == null) outEdgeSets.put(id, new HashSet<VersionedEdge>());
    }
    
    
    public VersionedEdge getEdge(Long id) {
        return getEdge(id, localMode);
    } 
    

    protected VersionedEdge getEdge(Long  eId, boolean localOnly) {

        // argument guards
        if (eId == null) throw new IllegalArgumentException("edge id is null");
        
        VersionedEdge edge = graphEdges.get(eId);
        if (edge != null) return edge;
        if (localOnly) return null;
        
        try (Handle h = dbi.open()) {
            List<TEdge> edges = h.createQuery(
                "SELECT id, txn_start, txn_end, label, v_in, v_out, edge_order, "
                + "FROM edge " 
                + "WHERE id = :id ")
                .bind("id", eId)
                .map(new TEdgeMapper()).list();

            if (edges == null) return null;
            
            Map<TId, Map<String, Object>> propMap = getElementPropertyMap(eId, h);
            for (TEdge te : edges) {
                Map<String, Object> props = propMap.get(te.id);
                if (props != null) {
                    te.replaceProperties(props);
                }
            }    
            Set<TEdge> eSet = new HashSet<>();
            eSet.addAll(edges);
            edge = new VersionedEdge(eSet, this);
            addEdgeToGraph(edge);
        }
        return edge;
    } 
    
    
    protected Map<TId, Map<String, Object>> getElementPropertyMap(Long pId, Handle h) {

        Map<TId, Map<String, Object>> propMap = new HashMap<>();

        List<TProperty> propList = h.createQuery(
                "SELECT id, txn_start, txn_end, name, type, value "
                + "FROM property " 
                + "WHERE id = :id")
                .bind("id", pId)
                .map(new TPropertyMapper()).list();
        if (propList == null || propList.size() == 0) return propMap;
        
        for (TProperty p : propList) {
            TId id = p.getId();
            if (propMap.get(id) == null) {
                propMap.put(id, new HashMap<String, Object>());
            }
            propMap.get(id).put(p.getName(), p.getValue());
        }
        return propMap;
    }
    
    
    public Iterable<VersionedEdge> getEdges() {
        List<VersionedEdge> edges = Lists.newArrayList(graphEdges.values());
        return edges;
    }

    
    public Iterable<VersionedEdge> getEdges(String key, Object value) {
        Set<VersionedEdge> edges = new HashSet<>();
        
        for (VersionedEdge ve : graphEdges.values()) {
            for (TEdge edge : ve.edges) {
                Object o = edge.getProperty(key); 
                if (o != null && o.equals(value)) {
                    edges.add(ve);
                }
            }
        }
        return edges;        
    }


    protected TId parseId(Object eId) {
        
        if (eId == null) return null;
        if (eId instanceof TId) return (TId) eId;

        try {
            if (eId instanceof Long) return new TId((Long) eId);
            if (eId instanceof Integer) return new TId(((Integer) eId).longValue());
            if (eId instanceof String) return TId.parse((String) eId);

        } catch (Exception e) {
            log.log(Level.WARNING, "Error parsing id:", e);
        }
        return null;
    }

    
    public VersionedVertex getVertex(Long id) {
        return getVertex(id, localMode);
    } 
    
    
    protected VersionedVertex getVertex(Long vId, boolean localOnly) {
        
        // argument guards
        if (vId == null) throw new IllegalArgumentException("vertex id is null");
        
        VersionedVertex vertex = graphVertices.get(vId);
        if (vertex != null) return vertex;
        if (localOnly) return null;
        
        try (Handle h = dbi.open()) {
            List<TVertex> vertices = h.createQuery(
                "SELECT id, txn_start, txn_end "
                + "FROM vertex " 
                + "WHERE id = :id ")
                .bind("id", vId)
                .map(new TVertexMapper()).list();

            if (vertices == null) return null;
            
            Map<TId, Map<String, Object>> propMap = getElementPropertyMap(vId, h);
            for (TVertex tv : vertices) {
                Map<String, Object> props = propMap.get(tv.id);
                if (props != null) {
                    tv.replaceProperties(props);
                }
            }
            Set<TVertex> vSet = new HashSet<>();
            vSet.addAll(vertices);
            vertex = new VersionedVertex(vSet, this);
            addVertexToGraph(vertex);
        }
        return vertex;
    } 
    
     
    public Iterable<VersionedVertex> getVertices() {
        List<VersionedVertex> vertices = Lists.newArrayList(graphVertices.values());
        return vertices;        
    }

    
    public Iterable<VersionedVertex> getVertices(String key, Object value) {
        Set<VersionedVertex> vertices = new HashSet<>();
        
        for (VersionedVertex vv : graphVertices.values()) {
            for (TVertex vertex : vv.vertices) {
                Object o = vertex.getProperty(key); 
                if (o != null && o.equals(value)) {
                    vertices.add(vv);
                }
            }
        }
        return vertices;        
    }
    
    
    public String toString() {
        return ("versiongraph");
    }

    
    public void clear() {
        graphEdges.clear();
        graphVertices.clear();
        inEdgeSets.clear();
        outEdgeSets.clear();
    }
    
    
    protected void getBranch(Long id, Direction direction, String[] labels) {
        VersionQuery q = new VersionQuery(id, this);
        q.branch(Lists.newArrayList(labels), direction);
        q.execute();
    }
    
    
    public VersionQuery newQuery(Long id) {
        return new VersionQuery(id, this);
    }


    public VersionQuery newQuery(List<Long> ids) {
        return new VersionQuery(ids, this);
    }
    
    
    public void loadTransactionGraph(Long txn, boolean clearGraph) {
        if (clearGraph) clear();
        new TransactionQuery(txn, this).execute();
    }


    public void loadTransactionGraph(Long firstTxn, Long lastTxn, boolean clearGraph) {
        if (clearGraph) clear();
        new TransactionQuery(firstTxn, lastTxn, this).execute();
    }
    
    
    public void getTransactionCopies(Long firstTxn, Long lastTxn) {
        new TransactionQuery(firstTxn, lastTxn, this).getCopiesByTxnFileAndDescription();
    }
}
