package amberdb.sql;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;


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
            + "WHERE t.time >= (%d) \n"
            + "AND (t.id = v.txn_end OR t.id = v.txn_start) \n"
            + "GROUP BY v.id; \n"
            
            + "UPDATE vt \n"
            + "SET state = 'DEL' \n"
            + "WHERE txn_end > txn_start; \n"

            + "UPDATE vt \n"
            + "SET state = 'MOD' \n"
            + "WHERE txn_end <= txn_start; \n"
            
            + "UPDATE vt \n"
            + "SET state = 'NEW' \n"
            + "WHERE txn_end = 0; \n", 
        
            millisTime));

        Handle h = graph.dbi().open();

        // run the generated query
        h.begin();
        h.createStatement(s.toString()).execute();
        h.commit();
            
        // get results
        List<Map<String, Object>> rs = h.select("SELECT id, state FROM vt ");
        Map<Long, String> verticesWithState = new HashMap<Long, String>();
        for (Map<String, Object> r : rs) {
            verticesWithState.put((Long) r.get("id"), (String) r.get("state"));
        }
        return verticesWithState;
    }
}
