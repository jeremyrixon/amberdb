package amberdb.v2.relation;

import amberdb.relation.Acknowledge;
import amberdb.v2.AmberSession;
import amberdb.v2.model.Acknowledgement;
import amberdb.v2.relation.model.WorkAcknowledgement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkAcknowledgementQuery extends AmberQueryBase {

    public WorkAcknowledgementQuery(AmberSession session) {
        super(session);
    }

    public List<Acknowledgement> getOrderedAcknowledgements(Long workId) {
        List<WorkAcknowledgement> rel = session.getWorkAcknowledgementDao().getOrderedAcknowledgements(workId);
        List<Acknowledgement> result = new ArrayList();

        if (rel != null) {
            for (WorkAcknowledgement r : rel) {
                result.add(session.getAcknowledgementDao().get(r.getAcknowledgementId()));
            }
        }

        if (result != null && !result.isEmpty()) {
            Collections.sort(result, new Comparator<Acknowledgement>() {
                public int compare(final Acknowledgement object1, final Acknowledgement object2) {
                    return object1.getWeighting().compareTo(object2.getWeighting());
                }
            });
        }

        return result;
    }
}
