package amberdb.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.ByteArrayMapper;

import com.google.common.base.Joiner;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import amberdb.AmberSession;
import amberdb.enums.BibLevel;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberQuery;
import amberdb.graph.BranchType;
import amberdb.graph.DataType;
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
    
    /**
     * Find the distinct bib levels of all the children of the specified list of work ids
     */
    public static Set<BibLevel> getDistinctChildrenBibLevels(AmberSession sess, List<Long> workIds){
        List<byte[]> bibLevelCodes = new ArrayList<>();
        try (Handle h = sess.getAmberGraph().dbi().open()) {
            bibLevelCodes = h.createQuery(
                    "SELECT DISTINCT p.value from property p, edge e where e.v_out=p.id \n"
                            + "and e.txn_end = 0 and e.label = 'isPartOf' \n"
                            + "and p.name = 'bibLevel' and p.txn_end = 0 and e.v_in in (:workIds); \n")
                    .bind("workIds", Joiner.on(",").join(workIds))
                    .map(ByteArrayMapper.FIRST).list();
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
}
