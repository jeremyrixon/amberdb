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
                    "select * from\n" + 
                    "(\n" + 
                    "select distinct article.id as article_id,\n" + 
                    "       @from_transaction,\n" + 
                    "       article.txn_start,\n" + 
                    "       article.txn_end,\n" + 
                    "       article.accessConditions as a_access,\n" + 
                    "       parent.accessConditions as p_access,\n" + 
                    "       page.accessConditions as page_access,\n" + 
                    "       case\n" + 
                    "         when article.accessConditions = 'Restricted' then 'Restricted'\n" + 
                    "         when parent.accessConditions is null or parent.accessConditions = 'Restricted' then 'Restricted'\n" + 
                    "         when page.accessConditions is null or page.accessConditions = 'Restricted' then 'Restricted'\n" + 
                    "         when article.accessConditions is null then parent.accessConditions\n" + 
                    "         else article.accessConditions\n" + 
                    "       end as accessConditions,\n" + 
                    "       case\n" + 
                    "         when article.txn_end >= @from_transaction then 'DELETED,NEW_MODIFIED_DELETED'\n" + 
                    "         when parent.accessConditions is null or parent.accessConditions = 'Restricted' then 'DELETED,PARENT_JOURNAL_RESTRICTED'\n" + 
                    "         when page.accessConditions is null or page.accessConditions = 'Restricted' then 'DELETED,RESTRICTED_PAGE'\n" + 
                    "         when article.accessConditions = 'Restricted' then 'DELETED,RESTRICTED'\n" + 
                    "         when (article.txn_start >= @from_transaction and (select count(*) from work_history where id = article.id) > 1) then 'MODIFIED,NEW_MODIFIED_DELETED'\n" + 
                    "         when (article.txn_start >= @from_transaction and (select count(*) from work_history where id = article.id) = 1) then 'NEW,NEW_MODIFIED_DELETED'\n" + 
                    "         when (parent.txn_start >= @from_transaction and (select count(*) from work_history where id = parent.id) > 1) then 'MODIFIED,PARENT_MODIFIED'\n" + 
                    "         when (parent.txn_start >= @from_transaction and (select count(*) from work_history where id = parent.id) = 1) then 'NEW,PARENT_NEW'\n" + 
                    "         when (page.txn_start >= @from_transaction and (select count(*) from work_history where id = page.id) > 1) then 'MODIFIED,PAGE_MODIFIED'\n" + 
                    "         when (page.txn_start >= @from_transaction and (select count(*) from work_history where id = page.id) = 1) then 'NEW,PAGE_NEW'\n" + 
                    "         else 'UNKNOWN'\n" + 
                    "       end as article_status\n" + 
                    "from\n" + 
                    "  work_history as article\n" + 
                    "left join edge as article_parent_edge on article.id = article_parent_edge.v_out\n" + 
                    "left join work as parent on parent.id = article_parent_edge.v_in\n" + 
                    "left join edge as article_page_edge on article.id = article_page_edge.v_out\n" + 
                    "left join work as page on page.id = article_parent_edge.v_in\n" + 
                    "where\n" + 
                    " article.subType = 'article' and\n" + 
                    " article_parent_edge.label = 'isPartOf' and -- parent join\n" + 
                    " article_page_edge.label = 'existsOn' and -- page join\n" + 
                    " (\n" + 
                    "    article.accessConditions = 'Restricted' or -- deleted\n" + 
                    "    (parent.accessConditions is null or parent.accessConditions = 'Restricted') or -- deleted\n" + 
                    "    (page.accessConditions is null or page.accessConditions = 'Restricted') or -- deleted\n" + 
                    "    article.txn_start >= @from_transaction or -- new/modified\n" + 
                    "    parent.txn_start >= @from_transaction or -- parent new/modified\n" + 
                    "    page.txn_start >= @from_transaction or -- page new/modified\n" + 
                    "    article.txn_end = 0 or -- current\n" + 
                    "    article.txn_end >= @from_transaction -- deleted\n" + 
                    " )\n" + 
                    ") as articles\n" + 
                    "where article_status like 'DELETED%' or article_status in ('NEW,NEW_MODIFIED_DELETED', 'MODIFIED,NEW_MODIFIED_DELETED')\n" + 
                    "order by article_id limit :skip, :take;");

            q.bind("skip", request.getSkip());
            q.bind("take", request.getTake());
            
            for (Map<String, Object> row : q.list()) {
                Long id = (Long)row.get("article_id");
                String[] status = ((String)row.get("article_status")).split(",");
                String accessConditions = (String)row.get("accessConditions");
                
                String transition = status[0];
                String reason = status.length > 1 ? status[1] : "";

                modifiedObjects.put(id, new ModifiedObjectsQueryResponse.ModifiedObject(transition, reason, accessConditions));
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
