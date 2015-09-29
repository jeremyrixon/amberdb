package amberdb.query;

import amberdb.AmberSession;
import amberdb.model.Work;
import java.util.ArrayList;
import java.util.List;
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
}
