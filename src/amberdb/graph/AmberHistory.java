package amberdb.graph;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;


public class AmberHistory {

    
    private AmberGraph graph;
    
    
    public AmberHistory(AmberGraph graph) {
        this.graph = graph;
    }
    
    
    /**
     * Retrieve a map with keys of ids for vertices added, changed or deleted since a given time.
     * The map value is what was done to the vertex. Note: if a vertex was both added then modified 
     * since the given time it appears as modified even though it may not have existed before the
     * time given. 
     * 
     * @param time the time after which altered vertices should be selected
     * 
     * @return a list of the modified vertices along with how they were modified
     */
    public Map<Long, String> modifiedSince(Date time) {
        
        Long millisTime = time.getTime();

        StringBuilder s = new StringBuilder();
        s.append(String.format(
                
            "DROP TABLE IF EXISTS vt;\n" 
                
            + "CREATE TEMPORARY TABLE vt (id BIGINT, txn_start BIGINT, txn_end BIGINT, state CHAR(3));\n"

            + "INSERT INTO vt (id, txn_start, txn_end) \n"
            + "SELECT v.id, max(txn_start), max(txn_end) \n"
            + "FROM vertex v, transaction t \n"
            + "WHERE t.time >= (%1$d) \n"
            + "AND (t.id = v.txn_end OR t.id = v.txn_start) \n"
            + "GROUP BY v.id; \n"
            
//            + "INSERT INTO vt (id, txn_start, txn_end, state) \n"
//            + "SELECT e.v_in, max(txn_start), max(txn_end), 'REL' \n"
//            + "FROM edge e, transaction t \n"
//            + "WHERE t.time >= (%1$d) \n"
//            + "AND (t.id = e.txn_end OR t.id = e.txn_start) \n"
//            + "GROUP BY e.v_in; \n"
//
//            + "INSERT INTO vt (id, txn_start, txn_end, state) \n"
//            + "SELECT e.v_out, max(txn_start), max(txn_end), 'REL' \n"
//            + "FROM edge e, transaction t \n"
//            + "WHERE t.time >= (%1$d) \n"
//            + "AND (t.id = e.txn_end OR t.id = e.txn_start) \n"
//            + "GROUP BY e.v_out; \n"
            
            + "UPDATE vt \n"
            + "SET state = 'DEL' \n"
            + "WHERE txn_end > txn_start "
            + "AND state IS NULL; \n"

            + "UPDATE vt \n"
            + "SET state = 'NEW' \n"
            + "WHERE txn_end = 0 " 
            + "AND state IS NULL; \n"
            
            + "UPDATE vt \n"
            + "SET state = 'MOD' \n"
            + "WHERE txn_end <= txn_start "
            + "AND state IS NULL; \n"
            
            ,millisTime));

        Map<Long, String> verticesWithState = new HashMap<Long, String>();
        try (Handle h = graph.dbi().open()) {
            // run the generated query
            h.begin();
            h.createStatement(s.toString()).execute();
            h.commit();

            // get results
            List<Map<String, Object>> rs = h.select("SELECT id, state FROM vt ");
            for (Map<String, Object> r : rs) {
                verticesWithState.put((Long) r.get("id"), (String) r.get("state"));
            }
        }
        return verticesWithState;
    }

    
    /**
     * Get the smallest transaction id associated with a given time stamp
     * 
     * @param time the time stamp
     * 
     * @return the transaction id
     */
    public Long getFirstTransactionIdAfter(Date time) {
        
        Long txnId;

        try (Handle h = graph.dbi().open()) {
            txnId = h.createQuery("SELECT min(id) FROM transaction WHERE time = (SELECT min(time) FROM transaction WHERE time >= :time")
                    .bind("time", time.getTime())
                    .first(Long.class);
        }
        return txnId;
    }
    
    
    /**
     * Return the latest incarnation of a vertex even if it has been deleted. The 
     * vertex is not associated with a graph, so it lacks most normal functionality. 
     */
    public AmberVertex getLastVertex(Long id) {

        AmberVertex vertex;
        
        AmberVertexWithState vs;
        try (Handle h = graph.dbi().open()) {
            vs = h.createQuery(
                "SELECT id, txn_start, txn_end, 'AMB' state "
                + "FROM vertex " 
                + "WHERE id = :id "
                + "AND txn_start = (SELECT max(txn_start) FROM vertex WHERE id = :id)")
                .bind("id", id)
                .map(new VertexMapper(this.graph)).first();

            if (vs == null) return null;
            vertex = vs.vertex;

            AmberVertex v = (AmberVertex) vertex;
            v.replaceProperties(getElementPropertyMap((Long) v.getId(), v.txnStart, h));
        }
        return vertex;
    }

    
    /**
     * This version of getting an element's properties will return property maps based on
     * the creation date of an element - even if the element has subsequently been deleted.
     * For use with history only.
     */
    protected Map<String, Object> getElementPropertyMap(Long elementId, Long txnStart, Handle h) {

        Map<String, Object> propMap = new HashMap<String, Object>();

        List<AmberProperty> propList = h.createQuery(
                "SELECT id, name, type, value "
                + "FROM property " 
                + "WHERE id = :id "
                + "AND txn_start = :txnStart")
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
     * Return all incarnations of a vertex. The list progresses in reverse time order
     */
    public List<AmberVertex> getHistory(Long vertexId) {

        List<AmberVertex> vHistory = new ArrayList<AmberVertex>();
        
        List<AmberVertexWithState> vsList;
        try (Handle h = graph.dbi().open()) {
            vsList = h.createQuery(
                "SELECT id, txn_start, txn_end, 'AMB' state "
                + "FROM vertex " 
                + "WHERE id = :vertexId "
                + "ORDER BY txn_start DESC")
                .bind("vertexId", vertexId)
                .map(new VertexMapper(this.graph)).list();

            for (AmberVertexWithState vs : vsList) {
                if (vs == null) continue;
                AmberVertex v = vs.vertex;
                v.replaceProperties(getElementPropertyMap((Long) v.getId(), v.txnStart, h));
                vHistory.add(v);
            }
        }
        return vHistory;
    }
    
    
    /**
     * Return the ids of vertices arrived at by following the latest edges from 
     * a vertex even if it and its edges have been deleted. This routine is completely dodgey 
     * at best and not to be trusted - it will be removed when proper history handling is added
     * to amberdb.
     */
    public List<Long> followLastEdges(Long vertexId, String label, Direction direction) {

        String src, dest;
        if (direction == Direction.IN) {
            src  = "v_in";
            dest = "v_out";
        } else {
            src  = "v_out";
            dest = "v_in";
        }
        
        StringBuilder s = new StringBuilder();
        s.append(String.format(
            "DROP TABLE IF EXISTS le; \n"
            + "CREATE TEMPORARY TABLE le ("
            + "id BIGINT, "
            + "txn_start BIGINT);\n"                        
            
            + "INSERT INTO le (id, txn_start) "
            + "SELECT e.id,  max(e.txn_start) "
            + "FROM edge e " 
            + "WHERE e.%1$s = %2$d " 
            + "AND e.label = '%3$s' "
            + "GROUP BY e.id; \n"
            , src, vertexId, label));

        List<Long> vertices = new ArrayList<Long>();
        try (Handle h = graph.dbi().open()) {
            // run the generated query
            h.begin();
            h.createStatement(s.toString()).execute();
            h.commit();

            // get results
            s = new StringBuilder();
            s.append(String.format(
                "SELECT e.%1$s vid "
                + "FROM edge e, le " 
                + "WHERE e.id = le.id " 
                + "AND e.txn_start = le.txn_start\n"
                , dest));            
            
            List<Map<String, Object>> rs = h.select(s.toString());
            for (Map<String, Object> r : rs) {
                vertices.add((Long) r.get("vid"));
            }
        }
        return vertices;
    }
}
