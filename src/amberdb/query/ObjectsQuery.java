package amberdb.query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.LongMapper;

import com.google.common.base.Predicate;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQueryBase;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;

public class ObjectsQuery extends AmberQueryBase {
    
    public ObjectsQuery(AmberGraph graph) {
        super(graph);
    }
    
    public ModifiedObjectsQueryResponse getModifiedObjectIds(List<Long> txns, Predicate<VersionedVertex> filterPredicate, long skip, long take) {
        LinkedHashMap<Long, String> modifiedObjects = new LinkedHashMap<Long, String>();
        
        if (txns == null || txns.size() == 0) {
            return new ModifiedObjectsQueryResponse();
        }
        
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP TEMPORARY TABLE IF EXISTS v0; CREATE TEMPORARY TABLE v0 (id BIGINT) ENGINE=memory;");
            h.execute("SET @start_transaction = ?", txns.get(0) - 1);
            h.execute("SET @end_transaction = ?", txns.get(txns.size() - 1) + 1);

            System.out.println("start_txn: " + (txns.get(0) - 1) + ", end_txn: " + (txns.get(txns.size() - 1) + 1));
            
            h.execute(
                    "REPLACE INTO v0 (id)\n" + 
                    "SELECT SQL_CALC_FOUND_ROWS id\n" + 
                    "FROM vertex\n" + 
                    "WHERE (txn_start >= @start_transaction\n" + 
                    "       AND txn_start <= @end_transaction)\n" + 
                    "  AND (txn_end > @end_transaction\n" + 
                    "       OR txn_end = 0)\n" + 
                    "UNION\n" + 
                    "SELECT id\n" + 
                    "FROM vertex\n" + 
                    "WHERE (txn_end >= @start_transaction\n" + 
                    "       AND txn_end <= @end_transaction)\n" + 
                    "  AND (txn_start < @start_transaction)\n" + 
                    "UNION\n" + 
                    "SELECT v_in\n" + 
                    "FROM edge\n" + 
                    "WHERE (txn_start >= @start_transaction\n" + 
                    "       AND txn_start <= @end_transaction)\n" + 
                    "  AND (txn_end > @end_transaction\n" + 
                    "       OR txn_end = 0)\n" + 
                    "UNION\n" + 
                    "SELECT v_in\n" + 
                    "FROM edge\n" + 
                    "WHERE (txn_end >= @start_transaction\n" + 
                    "       AND txn_end <= @end_transaction)\n" + 
                    "  AND (txn_start < @start_transaction)\n" + 
                    "UNION\n" + 
                    "SELECT v_out\n" + 
                    "FROM edge\n" + 
                    "WHERE (txn_start >= @start_transaction\n" + 
                    "       AND txn_start <= @end_transaction)\n" + 
                    "  AND (txn_end > @end_transaction\n" + 
                    "       OR txn_end = 0)\n" + 
                    "UNION\n" + 
                    "SELECT v_out\n" + 
                    "FROM edge\n" + 
                    "WHERE (txn_end >= @start_transaction\n" + 
                    "       AND txn_end <= @end_transaction)\n" + 
                    "  AND (txn_start < @start_transaction)\n" + 
                    "ORDER BY id ASC\n" +
                    "LIMIT ?,?;", skip, take);

            Query<Map<String, Object>> q = h.createQuery(
                    "SELECT id,\n" + 
                    "       (CASE\n" + 
                    "            WHEN (txn_start < @start_transaction\n" + 
                    "                  AND txn_end <= @end_transaction\n" + 
                    "                  AND txn_end <> 0) THEN 'DELETED'\n" + 
                    "            ELSE (CASE WHEN (v_count_before = 0) THEN 'NEW' ELSE 'MODIFIED' END)\n" + 
                    "        END) AS transition\n" + 
                    "FROM\n" + 
                    "  ( SELECT a.id,\n" + 
                    "\n" + 
                    "     ( SELECT b.txn_start\n" + 
                    "      FROM vertex b\n" + 
                    "      WHERE a.id = b.id\n" + 
                    "      ORDER BY b.txn_start DESC LIMIT 1 ) AS txn_start,\n" + 
                    "\n" + 
                    "     ( SELECT b.txn_end\n" + 
                    "      FROM vertex b\n" + 
                    "      WHERE a.id = b.id\n" + 
                    "      ORDER BY b.txn_start DESC LIMIT 1 ) AS txn_end,\n" + 
                    "\n" + 
                    "     ( SELECT count(*)\n" + 
                    "      FROM vertex b\n" + 
                    "      WHERE a.id = b.id\n" + 
                    "      ORDER BY b.txn_start DESC LIMIT 1 ) AS v_count,\n" + 
                    "\n" + 
                    "     ( SELECT count(*)\n" + 
                    "      FROM vertex b\n" + 
                    "      WHERE a.id = b.id\n" + 
                    "        AND b.txn_start < @start_transaction\n" + 
                    "      ORDER BY b.txn_start DESC LIMIT 1 ) AS v_count_before\n" + 
                    "   FROM v0 a\n" + 
                    "   ORDER BY id) AS vertices_with_transition;");

            VersionedGraph vGraph = new VersionedGraph(graph.dbi());
            
            for (Map<String, Object> row : q.list()) {
                Long id = (Long)row.get("id");
                String transition = (String)row.get("transition");

                vGraph.clear();
                VersionedVertex vv = vGraph.getVertex(id);
                
                if (filterPredicate.apply(vv)) {
                    modifiedObjects.put(id, transition);
                }
            }
            
            boolean hasMore = (q.list().size() >= take); // && q.list().size < totalResultSize;
            System.out.println("qlistsize: " + q.list().size() + ", take: " + take + ", hasMore: " + hasMore);
            return new ModifiedObjectsQueryResponse(modifiedObjects, hasMore, hasMore ? skip + take : -1);
        }
    }
}
