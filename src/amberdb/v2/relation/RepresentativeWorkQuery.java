package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.model.Copy;
import amberdb.v2.relation.model.Representation;

import java.util.ArrayList;
import java.util.List;

public class RepresentativeWorkQuery extends AmberQueryBase {

    public RepresentativeWorkQuery(AmberSession session) {
        super(session);
    }

    public List<Copy> getRepresentations(Long workId) {
        List<Representation> rel = session.getRepresentativeWorkDao().getRepresentations(workId);
        List<Copy> result = new ArrayList();

        if (rel != null) {
            for (Representation r : rel) {
                result.add(session.getCopyDao().get(r.getCopyId()));
            }
        }

        return result;
    }

    public boolean isRepresented(Long workId) {
        return session.getRepresentativeWorkDao().isRepresented(workId);
    }
}
