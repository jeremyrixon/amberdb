package amberdb.query;

import amberdb.AmberSession;
import amberdb.graph.AmberQuery;
import amberdb.graph.BranchType;
import amberdb.model.Copy;
import amberdb.model.Work;
import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

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
}
