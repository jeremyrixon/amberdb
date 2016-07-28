package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.model.Section;
import amberdb.v2.model.Work;
import amberdb.v2.relation.model.WorkSection;

import java.util.ArrayList;
import java.util.List;

public class SectionQuery {

    private AmberSession session;

    public SectionQuery(AmberSession session) {
        this.session = session;
    }

    public List<Work> getPartsOf(Long workId, String subType) {
        List<WorkSection> rel = session.getSectionDao().getSectionByWorkId(workId, subType);
        List<Work> result = new ArrayList();

        if (rel != null) {
            for (WorkSection r : rel) {
                result.add(session.getSectionDao().get(r.getSectionId()));
            }
        }

        return result;
    }
}
