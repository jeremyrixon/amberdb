package amberdb.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.ResultIterator;
import org.skife.jdbi.v2.Update;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQueryBase;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;
import amberdb.query.ModifiedObjectsQueryResponse.ModifiedObject;

public class ObjectsQuery extends AmberQueryBase {
    
    private VersionedGraph vGraph;
    
    public ObjectsQuery(AmberGraph graph) {
        super(graph);
        this.vGraph = new VersionedGraph(graph.dbi());
        this.vGraph.clear();
    }

    public ModifiedObjectsQueryResponse getArticlesForIndexing(ModifiedObjectsBetweenTransactionsQueryRequest request) {

        LinkedHashMap<Long, ModifiedObjectsQueryResponse.ModifiedObject> modifiedObjects = new LinkedHashMap<Long, ModifiedObjectsQueryResponse.ModifiedObject>();

        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("SET @from_transaction = ?", request.getFromTxn());

            Query<Map<String, Object>> q = h.createQuery(
                    "SELECT *\n" + 
                    "FROM\n" + 
                    "  (SELECT DISTINCT article.id AS article_id,\n" + 
                    "                   CASE\n" + 
                    "                       WHEN article.accessConditions = 'Restricted' THEN 'Restricted'\n" + 
                    "                       WHEN parent.accessConditions IS NULL\n" + 
                    "                            OR parent.accessConditions = 'Restricted' THEN 'Restricted'\n" + 
                    "                       WHEN page.accessConditions IS NULL\n" + 
                    "                            OR page.accessConditions = 'Restricted' THEN 'Restricted'\n" + 
                    "                       ELSE article.accessConditions\n" + 
                    "                   END AS accessConditions,\n" + 
                    "                   CASE\n" + 
                    "                       WHEN article.txn_end >= @from_transaction THEN 'DELETED'\n" + 
                    "                       WHEN parent.accessConditions IS NULL\n" + 
                    "                            OR parent.accessConditions = 'Restricted' THEN 'DELETED,PARENT_JOURNAL_RESTRICTED'\n" + 
                    "                       WHEN page.accessConditions IS NULL\n" + 
                    "                            OR page.accessConditions = 'Restricted' THEN 'DELETED,RESTRICTED_PAGE'\n" + 
                    "                       WHEN article.accessConditions = 'Restricted' THEN 'DELETED,RESTRICTED'\n" + 
                    "                       WHEN (parent.txn_start >= @from_transaction\n" + 
                    "                             AND\n" + 
                    "                               (SELECT count(*)\n" + 
                    "                                FROM work_history\n" + 
                    "                                WHERE id = parent.id) > 1) THEN 'MODIFIED,PARENT_MODIFIED'\n" + 
                    "                       WHEN parent.txn_start >= @from_transaction THEN 'NEW,PARENT_NEW'\n" + 
                    "                       WHEN (page.txn_start >= @from_transaction\n" + 
                    "                             AND\n" + 
                    "                               (SELECT count(*)\n" + 
                    "                                FROM work_history\n" + 
                    "                                WHERE id = page.id) > 1) THEN 'MODIFIED,PAGE_MODIFIED'\n" + 
                    "                       WHEN page.txn_start >= @from_transaction THEN 'NEW,PAGE_NEW'\n" + 
                    "                       WHEN\n" + 
                    "                              (SELECT count(*)\n" + 
                    "                               FROM work_history\n" + 
                    "                               WHERE id = article.id) > 1 THEN 'MODIFIED'\n" + 
                    "                       ELSE 'NEW'\n" + 
                    "                   END AS article_status\n" + 
                    "   FROM work_history AS article,\n" + 
                    "        edge AS article_parent_edge,\n" + 
                    "   work AS parent,\n" + 
                    "           edge AS article_page_edge,\n" + 
                    "   work AS page\n" + 
                    "   WHERE (article.txn_end = 0\n" + 
                    "          OR -- current\n" + 
                    " article.txn_end >= @from_transaction)\n" + 
                    "     AND -- deleted\n" + 
                    "article.subType = 'article'\n" + 
                    "     AND article_parent_edge.label = 'isPartOf'\n" + 
                    "     AND -- parent join\n" + 
                    "article.id = article_parent_edge.v_out\n" + 
                    "     AND parent.id = article_parent_edge.v_in\n" + 
                    "     AND article_page_edge.label = 'existsOn'\n" + 
                    "     AND -- page join\n" + 
                    "article.id = article_page_edge.v_out\n" + 
                    "     AND page.id = article_page_edge.v_in\n" + 
                    "     AND page.subType = 'page'\n" + 
                    "     AND ( article.txn_start >= @from_transaction\n" + 
                    "          OR -- new/modified\n" + 
                    " article.accessConditions = 'Restricted'\n" + 
                    "          OR -- deleted\n" + 
                    " (parent.accessConditions IS NULL\n" + 
                    "  OR parent.accessConditions = 'Restricted')\n" + 
                    "          OR -- deleted\n" + 
                    " (page.accessConditions IS NULL\n" + 
                    "  OR page.accessConditions = 'Restricted')\n" + 
                    "          OR -- deleted\n" + 
                    " parent.txn_start >= @from_transaction\n" + 
                    "          OR -- parent new/modified\n" + 
                    " page.txn_start >= @from_transaction -- page new/modified\n" + 
                    ") ) AS articles\n" + 
                    "WHERE article_status LIKE 'DELETED%'\n" + 
                    "  OR article_status IN ('NEW',\n" + 
                    "                        'MODIFIED')\n" + 
                    "ORDER BY article_id LIMIT :skip,\n" + 
                    "                          :take;\n");

            q.bind("skip", request.getSkip());
            q.bind("take", request.getTake());
            
            for (Map<String, Object> row : q.list()) {
                Long id = (Long)row.get("article_id");
                String[] status = ((String)row.get("article_status")).split(",");
                String accessConditions = (String)row.get("accessCOnditions");
                
                String transition = status[0];
                String reason = status.length > 1 ? status[1] : "";

                if (!modifiedObjects.containsKey(id) || !"DELETED".equals(modifiedObjects.get(id).transition)) {
                    modifiedObjects.put(id, new ModifiedObjectsQueryResponse.ModifiedObject(transition, reason, accessConditions));
                }
            }

            boolean hasMore = (q.list().size() >= request.getTake());

            return new ModifiedObjectsQueryResponse(modifiedObjects, hasMore, hasMore ? request.getSkip() + request.getTake() : -1);
        } 
    }

    public ModifiedObjectsQueryResponse getModifiedObjectIds(ModifiedObjectsBetweenTransactionsQueryRequest request) {
        LinkedHashMap<Long, ModifiedObjectsQueryResponse.ModifiedObject> modifiedObjects = new LinkedHashMap<Long, ModifiedObjectsQueryResponse.ModifiedObject>();
        
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

            
            try (ResultIterator<Map<String, Object>> iter = q.iterator()) {
            	while(iter.hasNext()) {
            		Map<String, Object> row = iter.next();
	                Long id = (Long)row.get("id");
	                String transition = ((String)row.get("transition")).intern();
	                
	                if (request.hasFilterPredicate()) {
	                    vGraph.clear();
	                    VersionedVertex vv = vGraph.getVertex(id);
	                    if (vv != null && request.getFilterPredicate().apply(vv)) {
	                        modifiedObjects.put(id, new ModifiedObjectsQueryResponse.ModifiedObject(transition));
	                    } 
	                } else {
	                    modifiedObjects.put(id, new ModifiedObjectsQueryResponse.ModifiedObject(transition));
	                }
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
