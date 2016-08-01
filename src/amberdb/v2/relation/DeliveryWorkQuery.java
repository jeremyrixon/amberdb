package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.model.Work;
import amberdb.v2.relation.model.WorkDeliveryWork;

import java.util.ArrayList;
import java.util.List;

public class DeliveryWorkQuery extends AmberQueryBase {

    public DeliveryWorkQuery(AmberSession session) {
        super(session);
    }

    public Work getDeliveryWorkParent(Long deliveryWorkId) {
        WorkDeliveryWork rel = session.getDeliveryWorkDao().getDeliveryWorkParent(deliveryWorkId);

        if (rel != null) {
            return session.getWorkDao().get(rel.getWorkId());
        }

        return null;
    }

    public Work getDeliveryWorkParentForSoundFile(Long soundFileId) {
        FileQuery fq = new FileQuery(session);
        Work work = fq.getWorkForSoundFile(soundFileId);

        return getDeliveryWorkParent(work.getId());
    }

    public List<Work> getDeliveryWorks(Long workId) {
        List<WorkDeliveryWork> rel = session.getDeliveryWorkDao().getDeliveryWorks(workId);
        List<Work> deliveryWorks = new ArrayList();

        if (rel != null) {
            for (WorkDeliveryWork r : rel) {
                Work dw = session.getWorkDao().get(r.getDeliveryWorkId());
                if (dw != null) {
                    deliveryWorks.add(dw);
                }
            }
        }

        return deliveryWorks;
    }

    public boolean isDeliveryWork(Long deliveryWorkId) {
        return session.getDeliveryWorkDao().getDeliveryWork(deliveryWorkId) == null ? false : true;
    }
}
