package amberdb.version;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skife.jdbi.v2.Handle;


public class TransactionQuery {

    
    Long firstTxn;
    Long lastTxn;
    private VersionedGraph graph;

    protected TransactionQuery(Long firstTxn, VersionedGraph graph) {

        // guard
        if (firstTxn == null) throw new IllegalArgumentException("Query must have transaction id");
        
        this.firstTxn = firstTxn;
        this.graph = graph;
    }

    
    protected TransactionQuery(Long firstTxn, Long lastTxn, VersionedGraph graph) {

        // guards
        if (firstTxn == null || lastTxn == null) 
            throw new IllegalArgumentException("Query must have first and last transaction id");
        if (firstTxn > lastTxn) 
            throw new IllegalArgumentException("First transaction id must be less than or equal to the last");
        
        this.firstTxn = firstTxn;
        this.lastTxn = lastTxn;
        this.graph = graph;
    }
    
    public List<VersionedVertex> execute() {

        List<VersionedVertex> vertices;
        try (Handle h = graph.dbi().open()) {

            // run the generated query
            h.begin();
            h.createStatement(generateTransactionQuery()).execute();
            h.commit();

            // and reap the rewards
            Map<TId, Map<String, Object>> propMaps = getElementPropertyMaps(h);
            vertices = getVertices(h, graph, propMaps);
            getEdges(h, graph, propMaps);
        }
        return vertices;
    }
    
    
    private Map<TId, Map<String, Object>> getElementPropertyMaps(Handle h) {
        
        List<TProperty> propList = h.createQuery(
                "SELECT p.id, p.txn_start, p.txn_end, p.name, p.type, p.value "
                + "FROM property p, v0 " 
                + "WHERE p.id = v0.vid OR p.id = v0.eid")
                .map(new TPropertyMapper()).list();

        Map<TId, Map<String, Object>> propertyMaps = new HashMap<>();
        for (TProperty prop : propList) {
            TId id = prop.getId();
            if (propertyMaps.get(id) == null) {
                propertyMaps.put(id, new HashMap<String, Object>());
            }
            propertyMaps.get(id).put(prop.getName(), prop.getValue());
        }
        return propertyMaps;
    }
    
    
    private List<VersionedVertex> getVertices(Handle h , VersionedGraph graph, Map<TId, Map<String, Object>> propMaps) {

        List<VersionedVertex> gotVertices = new ArrayList<>(); 
        
        List<TVertex> vertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end "
                + "FROM vertex v, v0 "
                + "WHERE v.id = v0.vid")
                .map(new TVertexMapper()).list();

        // add them to the graph
        Map<Long, Set<TVertex>> vertexSets = new HashMap<>();
        for (TVertex vertex : vertices) {
            Long versId = vertex.getId().id;
            if (vertexSets.get(versId) == null) 
                vertexSets.put(versId, new HashSet<TVertex>()); 
            vertexSets.get(versId).add(vertex);    
            vertex.replaceProperties(propMaps.get(vertex.getId()));
        }
        for (Set<TVertex> vSet : vertexSets.values()) {
            VersionedVertex v = new VersionedVertex(vSet, graph);
            graph.addVertexToGraph(v);
            gotVertices.add(v);
        }
        return gotVertices;
    }
    
    
    private void getEdges(Handle h , VersionedGraph graph, Map<TId, Map<String, Object>> propMaps) {
        
        List<TEdge> edges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order "
                + "FROM edge e, v0 "
                + "WHERE e.id = v0.eid ")
                .map(new TEdgeMapper(graph, true)).list();
        
        // add them to the graph
        Map<Long, Set<TEdge>> edgeSets = new HashMap<>();
        for (TEdge edge : edges) {
            if (edge == null) { // if either vertex doesn't exist 
                continue;
            }
            Long versId = edge.getId().id;
            if (edgeSets.get(versId) == null) 
                edgeSets.put(versId, new HashSet<TEdge>()); 
            edgeSets.get(versId).add(edge);    
            edge.replaceProperties(propMaps.get(edge.getId()));
        }            
        for (Set<TEdge> eSet : edgeSets.values()) {
            VersionedEdge e = new VersionedEdge(eSet, graph);
            graph.addEdgeToGraph(e);
        }
    }


    public String generateTransactionQuery() {

        String where = null;
        if (lastTxn == null) {
            where = "WHERE (txn_start = %1$d OR txn_end = %1$d);";
        } else {
            where = "WHERE ((txn_start >= %1$d AND txn_start <= %2$d) OR (txn_end >= %1$d AND txn_end <= %2$d));";
        }
        
        StringBuilder s = new StringBuilder();
        s.append("DROP TABLE IF EXISTS v0;\n");
        
        s.append("CREATE TEMPORARY TABLE v0 ("
                + "vid BIGINT, "
                + "eid BIGINT);\n");
        
        s.append(String.format(
                "INSERT INTO v0 (vid, eid) \n"
              + "SELECT id, 0 \n"
              + "FROM vertex \n"
              + where,
              firstTxn, lastTxn));
        
        s.append(String.format(
                "INSERT INTO v0 (vid, eid) \n"
              + "SELECT 0, id \n"
              + "FROM edge \n"
              + where,
              firstTxn, lastTxn));

        return s.toString();
    }
}
