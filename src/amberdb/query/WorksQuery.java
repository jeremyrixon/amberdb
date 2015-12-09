package amberdb.query;

import java.util.*;

import amberdb.graph.*;
import amberdb.relation.*;
import org.apache.commons.collections.CollectionUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.ByteArrayMapper;

import com.google.common.base.Joiner;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import amberdb.AmberSession;
import amberdb.enums.BibLevel;
import amberdb.model.Copy;
import amberdb.model.Work;

public class WorksQuery {

    public static List<Work> getWorks(AmberSession sess, List<Long> ids) {
        List<Work> works = new ArrayList<>();
        List<Vertex> verts = sess.getAmberGraph().newQuery(ids).execute();
        for (Vertex v : verts) {
            works.add(sess.getGraph().frame(v, Work.class));
        }
        return works;
    }
    
    public static Map<Long, Work> getWorksMap(AmberSession sess, List<Long> ids) {
        Map<Long, Work> works = new HashMap();
        AmberQuery query = sess.getAmberGraph().newQuery(ids);
        query.branch(BranchType.BRANCH_FROM_PREVIOUS, Arrays.asList(IsPartOf.label), Direction.OUT);
        query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(DeliveredOn.label), Direction.IN, Arrays.asList(0));
        Map<Long, AmberTransaction> firstTransactionMap = getFirstTransactions(sess, ids);
        Map<Long, AmberTransaction> lastTransactionMap = getLastTransactions(sess, ids);
        List<Vertex> verts = query.execute();
        for (Vertex v : verts) {
            Work work = sess.getGraph().frame(v, Work.class);
            populateFirstTransactionDetails(work, firstTransactionMap);
            populateLastTransactionDetails(work, lastTransactionMap);
            works.put(Long.valueOf(work.getId()), work);
        }
        return works;
    }

    private static void populateFirstTransactionDetails(Work work, Map<Long, AmberTransaction> map) {
        AmberTransaction transaction = map.get(work.getId());
        if (transaction != null) {
            work.asVertex().setProperty("createdOn", new Date(transaction.getTime()));
            work.asVertex().setProperty("createdBy", transaction.getUser());
        }
    }

    private static void populateLastTransactionDetails(Work work, Map<Long, AmberTransaction> map) {
        AmberTransaction transaction = map.get(work.getId());
        if (transaction != null) {
            work.asVertex().setProperty("modifiedOn", new Date(transaction.getTime()));
            work.asVertex().setProperty("modifiedBy", transaction.getUser());
        }
    }

    public static List<Copy> getCopiesWithWorks(AmberSession sess, List<Long> copyIds) {
        List<Copy> copies = new ArrayList<>();
        AmberQuery query = sess.getAmberGraph().newQuery(copyIds);
        query.branch(BranchType.BRANCH_FROM_ALL, new String[] { "isCopyOf" }, Direction.OUT);
        List<Vertex> vertices = query.execute();
        for (Vertex v : vertices) {
            if (v.getProperty("type").equals("Copy")) {
                copies.add(sess.getGraph().frame(v, Copy.class));
            }
        }
        return copies;
    }

    public static Map<Long, Copy> getCopiesWithWorksMap(AmberSession sess, List<Long> copyIds) {
        Map<Long, Copy> copies = new HashMap<Long, Copy>();
        AmberQuery query = sess.getAmberGraph().newQuery(copyIds);
        query.branch(BranchType.BRANCH_FROM_ALL, new String[] {IsCopyOf.label}, Direction.OUT);
        query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(IsSourceCopyOf.label), Direction.OUT, Arrays.asList(0));
        query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(IsFileOf.label), Direction.IN, Arrays.asList(0));
        List<Vertex> vertices = query.execute();
        for (Vertex v : vertices) {
            if (v.getProperty("type").equals("Copy")) {
                Copy copy = sess.getGraph().frame(v, Copy.class);
                copies.put(Long.valueOf(copy.getId()), copy);
            }
        }
        return copies;
    }
    
    /**
     * Find the distinct bib levels of all the parents of the specified list of work ids
     */
    public static Set<BibLevel> getDistinctParentBibLevels(AmberSession sess, List<Long> workIds){
        String sql = "SELECT DISTINCT p.value from property p, edge e where e.v_in=p.id "
                + "and e.txn_end = 0 and e.label = 'isPartOf' "
                + "and p.name = 'bibLevel' and p.txn_end = 0 and e.v_out in ("+Joiner.on(",").join(workIds)+"); ";
        return getDistinctBibLevels(sess, sql);
    }
    
    /**
     * Find the distinct bib levels of all the children of the specified list of work ids
     */
    public static Set<BibLevel> getDistinctChildrenBibLevels(AmberSession sess, List<Long> workIds){
        String sql = "SELECT DISTINCT p.value from property p, edge e where e.v_out=p.id "
                + "and e.txn_end = 0 and e.label = 'isPartOf' "
                + "and p.name = 'bibLevel' and p.txn_end = 0 and e.v_in in ("+Joiner.on(",").join(workIds)+"); ";
        return getDistinctBibLevels(sess, sql);
    }
    
    private static Set<BibLevel> getDistinctBibLevels(AmberSession sess, final String sql){
        List<byte[]> bibLevelCodes = new ArrayList<>();
        try (Handle h = sess.getAmberGraph().dbi().open()) {
            bibLevelCodes = h.createQuery(sql).map(ByteArrayMapper.FIRST).list();
        }
        Set<BibLevel> bibLevels = new HashSet<>();
        for (byte[] bytes : bibLevelCodes) {
            String code = (String) AmberProperty.decode(bytes, DataType.STR);
            BibLevel bibLevel = BibLevel.fromString(code);
            if (bibLevel != null){
                bibLevels.add(bibLevel);    
            }
        }
        return bibLevels;
    }
    
    private static Map<Long, AmberTransaction> getFirstTransactions(AmberSession sess, List<Long> workIds){
        if (CollectionUtils.isNotEmpty(workIds)) {
            String sql = "select t1.time, t1.user, t1.operation, t2.transaction_id, t2.vertex_id " +
                    "from transaction t1, (select min(t.id) transaction_id, v.id vertex_id from transaction t, vertex v " +
                    "where t.id = v.txn_start and v.id in (" + Joiner.on(",").join(workIds) + ") group by v.id) " +
                    "as t2 where t1.id = t2.transaction_id";
            return getTransactions(sess, sql);
        }
        return new HashMap<>();
    }

    private static Map<Long, AmberTransaction> getLastTransactions(AmberSession sess, List<Long> workIds){
        if (CollectionUtils.isNotEmpty(workIds)) {
            String sql = "select t1.time, t1.user, t1.operation, t2.transaction_id, t2.vertex_id " +
                    "from transaction t1, (select max(t.id) transaction_id, v.id vertex_id from transaction t, vertex v " +
                    "where t.id = v.txn_start and v.id in (" + Joiner.on(",").join(workIds) + ") group by v.id) " +
                    "as t2 where t1.id = t2.transaction_id";
            return getTransactions(sess, sql);
        }
        return new HashMap<>();
    }

    private static Map<Long, AmberTransaction> getTransactions(AmberSession sess, String sql){
        Map<Long, AmberTransaction> map = new HashMap<>();
        List<AmberVertexTransaction> list;
        try (Handle h = sess.getAmberGraph().dbi().open()) {
            list = h.createQuery(sql).map(new VertexTransactionMapper()).list();
        }
        for (AmberVertexTransaction transaction : list) {
            map.put(transaction.getVertexId(), transaction.getTransaction());
        }
        return map;
    }
}
