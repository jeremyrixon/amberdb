package amberdb.v2.util;

import amberdb.v2.AmberSession;
import amberdb.v2.enums.CopyRole;
import amberdb.v2.model.Copy;
import amberdb.v2.model.Work;
import amberdb.v2.relation.RepresentativeWorkQuery;
import amberdb.v2.relation.WorkChildrenQuery;
import amberdb.v2.relation.WorkCopyQuery;

import java.util.Iterator;
import java.util.List;

public class RepresentativeImageHelper {

    /**
     * Returns the work that contains the access copy to be used for this work's representative image.
     *
     * @return The work with the image copy that should be used to represent this work, or null if no image is
     * specified.
     */
    public static Work getRepresentativeImageWork(Work work, AmberSession amberSession){
        Work repImageOrAccessCopy = getRepImageOrAccessCopy(work, amberSession);
        if (repImageOrAccessCopy != null) {
            return repImageOrAccessCopy;
        }
        WorkChildrenQuery query = new WorkChildrenQuery(amberSession);
        List<Work> children = query.getChildRange(work.getId(), 0, 1);
        if (!children.isEmpty()) {
            Work child = children.get(0);
            if (WorkUtils.checkCanReturnRepImage(child)) {
                return getRepImageOrAccessCopy(child, amberSession);
            }
        }
        return null;
    }
    
    public static void setRepresentativeImageWorkProperty(Work work, AmberSession amberSession){
        Work representativeImageWork = getRepresentativeImageWork(work, amberSession);
        if (representativeImageWork != null){
            work.setRepresentativeImageWork(representativeImageWork.getId());
        }
    }

    private static Work getRepImageOrAccessCopy(Work work, AmberSession session) {
        RepresentativeWorkQuery rq = new RepresentativeWorkQuery(session);
        Iterator<Copy> representations = rq.getRepresentations(work.getId()).iterator();
        WorkCopyQuery query = new WorkCopyQuery(session);
        if (representations.hasNext()) {
            Copy copy = representations.next();
            Work repWork = query.getWork(copy.getId());
            if (!WorkUtils.checkCanReturnRepImage(repWork)) {
                return null;
            }
            return repWork;
        }
        Copy accessCopy = query.getCopy(work.getId(), CopyRole.ACCESS_COPY);
        if (accessCopy != null && accessCopy.getImageFile() != null) {
            return work;
        }
        return null;
    }
}
