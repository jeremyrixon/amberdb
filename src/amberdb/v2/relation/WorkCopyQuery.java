package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.enums.CopyRole;
import amberdb.v2.model.Copy;
import amberdb.v2.model.Work;
import amberdb.v2.relation.model.WorkCopy;
import com.google.common.collect.LinkedListMultimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WorkCopyQuery extends AmberQueryBase {

    public WorkCopyQuery(AmberSession session) {
        super(session);
    }

    public boolean workHasImageAccessCopy(Long workId) {
        WorkCopy workCopy = session.getWorkCopyDao().getCopyByWorkIdAndRole(workId, CopyRole.ACCESS_COPY.code());

        return workCopy == null ? false : true;
    }

    public Copy getCopy(Long workId, CopyRole role) {
        WorkCopy workCopy = session.getWorkCopyDao().getCopyByWorkIdAndRole(workId, role.code());

        return session.getCopyDao().get(workCopy.getCopyId());
    }

    public Work getWork(Long copyId) {
        WorkCopy workCopyDetails = session.getWorkCopyDao().getWorkByCopyId(copyId);
        String type = workCopyDetails.getWorkType();
        Long workId = workCopyDetails.getWorkId();

        if ("Work".equalsIgnoreCase(type)) {
            return session.getWorkDao().get(workId);
        }

        return null;
    }

    public Map<String, Collection<Copy>> getOrderedCopyMap(Long workId) {
        LinkedListMultimap<String, Copy> orderedCopyMap = LinkedListMultimap.create();
        for (Copy copy : getOrderedCopies(workId)) {
            orderedCopyMap.put(copy.getCopyRole(), copy);
        }
        return orderedCopyMap.asMap();
    }

    public List<Copy> getOrderedCopies(Long workId) {
        List<Copy> result = new ArrayList();

        List<WorkCopy> copyList = session.getWorkCopyDao().getOrderedCopyIds(workId);
        for (WorkCopy wc : copyList) {
            Copy copy = session.getCopyDao().get(wc.getCopyId());
            if (copy != null) {
                result.add(copy);
            }
        }

        return result;
    }

    public List<CopyRole> getAllCopyRoles(List<Long> workIds) {
        List<CopyRole> result = new ArrayList();

        for (Long id : workIds) {
            List<WorkCopy> rel = session.getWorkCopyDao().getCopiesByWorkId(id);

            if (rel != null) {
                for (WorkCopy r : rel) {
                    result.add(CopyRole.fromString(r.getCopyRole()));
                }
            }
        }

        return result;
    }
}
