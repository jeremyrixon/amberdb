package amberdb.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQueryBase;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;

public class ObjectsQuery extends AmberQueryBase {
    
    private VersionedGraph vGraph;
    
    public ObjectsQuery(AmberGraph graph) {
        super(graph);
        this.vGraph = new VersionedGraph(graph.dbi());
        this.vGraph.clear();
    }

    public ModifiedObjectsQueryResponse getModifiedObjectIds(ModifiedObjectsBetweenTransactionsQueryRequest request) {
        LinkedHashMap<Long, String> modifiedObjects = new LinkedHashMap<Long, String>();
        
        if (!request.hasTransactions()) {
            return new ModifiedObjectsQueryResponse();
        }
        
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP " + graph.getTempTableDrop() + " TABLE IF EXISTS v0; CREATE TEMPORARY TABLE v0 (id BIGINT) " + graph.getTempTableEngine() + ";");
            h.execute("SET @start_transaction = ?", request.getFromTxn());
            h.execute("SET @end_transaction = ?", request.getToTxn());

            Update insert = createInsertStatement(h, request.getPropertyFilters(), request.isOnlyPropertiesWithinTransactionRange(), request.getSkip(), request.getTake());
            insert.execute();

            Query<Map<String, Object>> q = h.createQuery(
                            "SELECT DISTINCT id,\n" + 
                            "       (CASE WHEN (txn_end <= @end_transaction AND txn_end <> 0) THEN 'DELETED' ELSE\n" + 
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

            for (Map<String, Object> row : q.list()) {
                Long id = (Long)row.get("id");
                String transition = (String)row.get("transition");
                
                if (request.hasFilterPredicate()) {
                    vGraph.clear();
                    VersionedVertex vv = vGraph.getVertex(id);
                    
                    if (request.getFilterPredicate().apply(vv)) {
                        modifiedObjects.put(id, transition);
                    }
                } else {
                    modifiedObjects.put(id, transition);
                }
            }

            boolean hasMore = (q.list().size() >= request.getTake());
            return new ModifiedObjectsQueryResponse(modifiedObjects, hasMore, hasMore ? request.getSkip() + request.getTake() : -1);
        }
    }

    private Update createInsertStatement(Handle h, List<WorkProperty> propertyFilters, boolean onlyPropertiesWithinTransactionRange, long skip, long take) {
        if (propertyFilters == null) {
            propertyFilters = new ArrayList<WorkProperty>();
        }

        String insert =
                "INSERT INTO v0 (id)\n" + 
                "SELECT DISTINCT v.id\n" + 
                "FROM\n" + 
                "  ( SELECT id\n" + 
                "   FROM vertex\n" + 
                "   WHERE (txn_start >= @start_transaction\n" + 
                "          AND txn_start <= @end_transaction)\n" + 
                "     AND (txn_end > @end_transaction\n" + 
                "          OR txn_end = 0)\n" + 
                "   UNION SELECT id\n" + 
                "   FROM vertex\n" + 
                "   WHERE (txn_end >= @start_transaction\n" + 
                "          AND txn_end <= @end_transaction)\n" + 
                "     AND (txn_start < @start_transaction)\n" + 
                "   UNION SELECT v_in AS id\n" + 
                "   FROM edge\n" + 
                "   WHERE (txn_start >= @start_transaction\n" + 
                "          AND txn_start <= @end_transaction)\n" + 
                "     AND (txn_end > @end_transaction\n" + 
                "          OR txn_end = 0)\n" + 
                "   UNION SELECT v_in AS id\n" + 
                "   FROM edge\n" + 
                "   WHERE (txn_end >= @start_transaction\n" + 
                "          AND txn_end <= @end_transaction)\n" + 
                "     AND (txn_start < @start_transaction)\n" + 
                "   UNION SELECT v_out AS id\n" + 
                "   FROM edge\n" + 
                "   WHERE (txn_start >= @start_transaction\n" + 
                "          AND txn_start <= @end_transaction)\n" + 
                "     AND (txn_end > @end_transaction\n" + 
                "          OR txn_end = 0)\n" + 
                "   UNION SELECT v_out AS id\n" + 
                "   FROM edge\n" + 
                "   WHERE (txn_end >= @start_transaction\n" + 
                "          AND txn_end <= @end_transaction)\n" + 
                "     AND (txn_start < @start_transaction)) AS v\n";

        if (propertyFilters.size() > 0) {
            insert +=
                "    , property p\n" + 
                "    WHERE v.id = p.id\n";
                        if (onlyPropertiesWithinTransactionRange) {
                            insert += "      AND p.txn_end >= @start_transaction AND (p.txn_end <= @end_transaction OR p.txn_end = 0)\n";
                        }
            insert +=
                "      AND ( \n";

            for (int i = 0; i < propertyFilters.size(); i++) {
                insert += "        (p.name = :name_" + i + " AND CAST(p.value AS char) = :value_" + i + ")\n";
                if (i < propertyFilters.size() - 1) {
                    insert += "        OR\n";
                }
            }
            insert += "    )\n";
        }

        insert += "ORDER BY v.id ASC LIMIT :skip,:take;";

        Update u = h.createStatement(insert);
        
        for (int i = 0; i < propertyFilters.size(); i++) {
            u.bind("name_" + i, propertyFilters.get(i).getName());
            u.bind("value_" + i, propertyFilters.get(i).getValue());
        }

        u.bind("skip", skip);
        u.bind("take", take);

        return u;
    }
}
