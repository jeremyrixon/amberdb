package amberdb.query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

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
            h.execute("DROP " + graph.getTempTableDrop() + " TABLE IF EXISTS v0; CREATE TEMPORARY TABLE v0 (id BIGINT) " + graph.getTempTableEngine() + ";");
            h.execute("SET @start_transaction = ?", txns.get(0));
            h.execute("SET @end_transaction = ?", txns.get(txns.size() - 1));
            
            h.execute(
                    "INSERT INTO v0 (id)\n" + 
                    "SELECT DISTINCT id FROM (" +
                    "SELECT id\n" + 
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
                    ") AS results ORDER BY id ASC LIMIT ?,?;"
                    , skip, take);

            Query<Map<String, Object>> q = h.createQuery(
                            "SELECT DISTINCT id,\n" + 
                            "       (CASE WHEN (txn_start < @start_transaction AND txn_end <= @end_transaction AND txn_end <> 0) THEN 'DELETED' ELSE\n" + 
                            "         (CASE WHEN (v_count_before = 0) THEN 'NEW' ELSE 'MODIFIED' END)\n" + 
                            "        END) AS transition\n" + 
                            "FROM\n" + 
                            "(\n" + 
                            "    SELECT a.id,\n" + 
                            "    (\n" + 
                            "      SELECT b.txn_start FROM vertex b\n" + 
                            "      WHERE a.id = b.id\n" + 
                            "      ORDER BY b.txn_start DESC\n" + 
                            "      LIMIT 1\n" + 
                            "    ) as txn_start,\n" + 
                            "    (\n" + 
                            "      SELECT b.txn_end FROM vertex b\n" + 
                            "      WHERE a.id = b.id\n" + 
                            "      ORDER BY b.txn_start DESC\n" + 
                            "      LIMIT 1\n" + 
                            "    ) as txn_end,\n" + 
                            "    (\n" + 
                            "      SELECT count(id) FROM vertex b\n" + 
                            "      WHERE a.id = b.id AND b.txn_start < @start_transaction\n" + 
                            "    ) as v_count_before\n" + 
                            "    FROM v0 a\n" + 
                            "    ORDER BY id\n" + 
                            ") AS vertices_with_transition;"
                            );

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
            
            boolean hasMore = (q.list().size() >= take);
            return new ModifiedObjectsQueryResponse(modifiedObjects, hasMore, hasMore ? skip + take : -1);
        }
    }
}
