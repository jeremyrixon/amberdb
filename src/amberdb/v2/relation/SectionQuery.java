package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.model.Work;
import amberdb.v2.relation.model.WorkSection;

import java.util.ArrayList;
import java.util.List;

public class SectionQuery extends AmberQueryBase {

    public SectionQuery(AmberSession session) {
        super(session);
    }

    public List<Work> getPartsOf(Long workId, String subType) {
        List<WorkSection> rel = session.getWorkSectionDao().getSectionByWorkId(workId, subType);
        List<Work> result = new ArrayList();

        if (rel != null) {
            for (WorkSection r : rel) {
                result.add(session.getSectionDao().get(r.getSectionId()));
            }
        }

        return result;
    }
}
