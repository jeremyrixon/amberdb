package amberdb.v1.version;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;


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
        
        this.firstTxn = (firstTxn == 0L) ? 1L : firstTxn; // A first Transaction of 0 will include unwanted quantum foam :-)
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
            vertices = getVertices(h, graph, getPropertyMaps(h, "v0"), "v0");
            getEdges(h, graph, getPropertyMaps(h, "e0"), "e0");
        }
        return vertices;
    }
    

    private Map<TId, Map<String, Object>> getPropertyMaps(Handle h, String idTable) {
        
        try (ResultIterator<TProperty> iter = h.createQuery(
                "SELECT p.id, p.txn_start, p.txn_end, p.name, p.type, p.value "
                + "FROM property p, " + idTable + " " 
                + "WHERE p.id = " + idTable + ".id")
                .map(new TPropertyMapper()).iterator()) {

	        Map<TId, Map<String, Object>> propertyMaps = new HashMap<>();
	        Map<String, String> internedStrings = new HashMap<>();
	        
	        while (iter.hasNext()) {
	        	TProperty prop = iter.next();
	            TId id = prop.getId();
	            if (propertyMaps.get(id) == null) {
	                propertyMaps.put(id, new HashMap<String, Object>());
	            }
	            Object value = prop.getValue();
	            if (value instanceof String) {
	            	String s = (String) value;
	            	String interned = internedStrings.get(s);
	            	if (interned == null) {
	            		internedStrings.put(s, s);
	            		value = s;
	            	} else {
	            		value = interned;
	            	}
	            }
	            propertyMaps.get(id).put(prop.getName().intern(), value);
	        }
	        
	        return propertyMaps;
        }
    }

    
    private List<VersionedVertex> getVertices(Handle h , VersionedGraph graph, Map<TId, Map<String, Object>> propMaps, String tableId) {

    	String query =  "SELECT v.id, v.txn_start, v.txn_end "
                      + "FROM vertex v, " + tableId + " "
                      + "WHERE v.id = " + tableId + ".id";
    	try (ResultIterator<TVertex> vertexIter = h.createQuery(query).map(new TVertexMapper()).iterator()) {

	        List<VersionedVertex> gotVertices = new ArrayList<>(); 
	        
	        // add them to the graph
	        Map<Long, Set<TVertex>> vertexSets = new HashMap<>();
	        while (vertexIter.hasNext()) {
	        	TVertex vertex = vertexIter.next();
	            Long versId = vertex.getId().id;
	            if (vertexSets.get(versId) == null) 
	                vertexSets.put(versId, new HashSet<TVertex>()); 
	            vertexSets.get(versId).add(vertex); 
	            Map<String, Object> props = propMaps.get(vertex.getId());
	            if (props != null) {
	                vertex.replaceProperties(props);
	            }
	        }
	        for (Set<TVertex> vSet : vertexSets.values()) {
	            VersionedVertex v = new VersionedVertex(vSet, graph);
	            graph.addVertexToGraph(v);
	            gotVertices.add(v);
	        }
	        return gotVertices;
    	}
    }
    
    
    private void getEdges(Handle h , VersionedGraph graph, Map<TId, Map<String, Object>> propMaps, String tableId) {
    	String query = "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order "
    			     + "FROM edge e, " + tableId + " "
                     + "WHERE e.id = " + tableId + ".id ";
        
         try (ResultIterator<TEdge> iter = h.createQuery(query).map(new TEdgeMapper()).iterator()) {
        
	        // add them to the graph
	        Map<Long, Set<TEdge>> edgeSets = new HashMap<>();
	        
	        while(iter.hasNext()) {
	        	TEdge edge = iter.next();
	        	
	            if (edge == null) { // if either vertex doesn't exist 
	                continue;
	            }
	            Long versId = edge.getId().id;
	            if (edgeSets.get(versId) == null) 
	                edgeSets.put(versId, new HashSet<TEdge>()); 
	            edgeSets.get(versId).add(edge);
	            Map<String, Object> props = propMaps.get(edge.getId());
	            if (props != null) {
	                edge.replaceProperties(props);
	            }
	        }
	        for (Set<TEdge> eSet : edgeSets.values()) {
	            VersionedEdge e = new VersionedEdge(eSet, graph);
	            graph.addEdgeToGraph(e);
	        }
         }
    }


    public String generateTransactionQuery() {

        String txnWhereClause1 = null;
        String txnWhereClause2 = null;

        if (lastTxn == null) {
            txnWhereClause1 = "WHERE (txn_start = "+firstTxn+") \n";
            txnWhereClause2 = "WHERE (txn_end = "+firstTxn+") \n";
        } else {
            txnWhereClause1 = "WHERE (txn_start >= "+firstTxn+" AND txn_start <= "+lastTxn+") \n";
            txnWhereClause2 = "WHERE (txn_end >= "+firstTxn+" AND txn_end <= "+lastTxn+") \n";
        }
        
        StringBuilder s = new StringBuilder();
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v0;\n");
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS e0;\n");
        
        s.append("CREATE TEMPORARY TABLE v0 (id BIGINT) " + graph.tempTableEngine + ";\n");
        s.append("CREATE TEMPORARY TABLE e0 (id BIGINT) " + graph.tempTableEngine + ";\n");
        
        s.append("INSERT INTO v0 (id) \n"
               + "SELECT DISTINCT id \n"
               + "FROM vertex \n"
               + txnWhereClause1 + ";\n");
        s.append("INSERT INTO v0 (id) \n"
               + "SELECT DISTINCT id \n"
               + "FROM vertex \n"
               + txnWhereClause2 + ";\n");
        
        s.append("INSERT INTO e0 (id) \n"
               + "SELECT DISTINCT id \n"
               + "FROM edge \n" 
               + txnWhereClause1 + ";\n");
        s.append("INSERT INTO e0 (id) \n"
               + "SELECT DISTINCT id \n"
               + "FROM edge \n" 
               + txnWhereClause2 + ";\n");

        return s.toString();
    }
    

    public String makeTempTablesQuery() {
        StringBuilder s = new StringBuilder();
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS c0;\n");
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS f0;\n");
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS d0;\n");

        s.append("CREATE TEMPORARY TABLE c0 (id BIGINT) " + graph.tempTableEngine + ";\n"); // Copies
        s.append("CREATE TEMPORARY TABLE f0 (id BIGINT) " + graph.tempTableEngine + ";\n"); // Files
        s.append("CREATE TEMPORARY TABLE d0 (id BIGINT) " + graph.tempTableEngine + ";\n"); // Descriptions
        
        return s.toString();
    }

    
    public String makeDescriptionsQuery() {

        String txnWhereClause1 = null;
        String txnWhereClause2 = null;
        
        if (lastTxn == null) {
            txnWhereClause1 = "WHERE (v.txn_start = "+firstTxn+") \n";
            txnWhereClause2 = "WHERE (v.txn_end <= "+firstTxn+") \n";
        } else {
            txnWhereClause1 = "WHERE (v.txn_start >= "+firstTxn+" AND v.txn_start <= "+lastTxn+") \n";
            txnWhereClause2 = "WHERE (v.txn_end >= "+firstTxn+" AND v.txn_end <= "+lastTxn+") \n";
        }
        
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO d0 (id) \n"
               + "SELECT DISTINCT v.id \n"
               + "FROM vertex v, property p \n"
               +  txnWhereClause1 
               + "AND v.id = p.id \n"
               + "AND p.name = 'type' \n"
               + "AND p.value IN (" 
               + toHex("Description") + ", " 
               + toHex("IPTC") + ", " 
               + toHex("GeoCoding") + ", " 
               + toHex("CameraData") + "); \n");
        
        s.append("INSERT INTO d0 (id) \n"
               + "SELECT DISTINCT v.id \n"
               + "FROM vertex v, property p \n"
               +  txnWhereClause2 
               + "AND v.id = p.id \n"
               + "AND p.name = 'type' \n"
               + "AND p.value IN (" 
               + toHex("Description") + ", " 
               + toHex("IPTC") + ", " 
               + toHex("GeoCoding") + ", " 
               + toHex("CameraData") +"); \n");
        return s.toString();
    }

    
    public String makeFilesQuery() {

        String txnWhereClause1 = null;
        String txnWhereClause2 = null;

        if (lastTxn == null) {
            txnWhereClause1 = "WHERE (v.txn_start = "+firstTxn+") \n";
            txnWhereClause2 = "WHERE (v.txn_end <= "+firstTxn+") \n";
        } else {
            txnWhereClause1 = "WHERE (v.txn_start >= "+firstTxn+" AND v.txn_start <= "+lastTxn+") \n";
            txnWhereClause2 = "WHERE (v.txn_end >= "+firstTxn+" AND v.txn_end <= "+lastTxn+") \n";
        }

        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO f0 (id) \n"
               + "SELECT DISTINCT v.id \n"
               + "FROM vertex v, property p \n"
               +  txnWhereClause1
               + "AND v.id = p.id \n"
               + "AND p.name = 'type' \n"
               + "AND cast(p.value as char(100)) IN ("
               + toHex("File") + ", "
               + toHex("ImageFile") + ", "
               + toHex("SoundFile") + ", "
               + toHex("MovingImageFile") + "); \n");
        
        s.append("INSERT INTO f0 (id) \n"
               + "SELECT DISTINCT v.id \n"
               + "FROM vertex v, property p \n"
               +  txnWhereClause2
               + "AND v.id = p.id \n"
               + "AND p.name = 'type' \n"
               + "AND cast(p.value as char(100)) IN ("
               + toHex("File") + ", "
               + toHex("ImageFile") + ", "
               + toHex("SoundFile") + ", "
               + toHex("MovingImageFile") + "); \n");
        return s.toString();
    }

    
    public String makeDescriptFilesCopiesQuery() {
        StringBuilder s = new StringBuilder();
        // get Files from Descriptions
        s.append(
                "INSERT INTO f0 (id) \n"
              + "SELECT DISTINCT e.v_in \n"
              + "FROM edge e, d0 \n"
              + "WHERE d0.id = e.v_out \n"
              + "AND e.label = 'descriptionOf'; \n");
        // get copies from files and add to 
        s.append(
                "INSERT INTO c0 (id) \n"
              + "SELECT DISTINCT e.v_in \n"
              + "FROM edge e, f0 \n"
              + "WHERE f0.id = e.v_out \n"
              + "AND e.label = 'isFileOf'; \n");
        return s.toString();
    }
    

    public List<VersionedVertex> getCopiesByTxnFileAndDescription() {
        List<VersionedVertex> copyVertices = null;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute(makeTempTablesQuery());
            h.createStatement(makeDescriptionsQuery()).execute();
            h.createStatement(makeFilesQuery()).execute();
            h.createStatement(makeDescriptFilesCopiesQuery()).execute();
            h.commit();            
            // and reap the rewards
            copyVertices = getVertices(h, graph, getPropertyMaps(h, "c0"), "c0");
        }
        return copyVertices;
    }
    
    
    private String toHex(String s) {
        StringBuilder sb = new StringBuilder("x'");
        for (byte b : s.getBytes()) {
            sb.append(String.format("%02X", b));
        }
        return sb.append('\'').toString();
    }
}
