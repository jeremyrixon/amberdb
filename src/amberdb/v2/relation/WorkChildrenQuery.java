package amberdb.v2.relation;

import amberdb.v2.AmberSession;
import amberdb.v2.enums.BibLevel;
import amberdb.v2.enums.CopyRole;
import amberdb.v2.model.Work;
import amberdb.v2.relation.model.ParentChild;
import amberdb.v2.relation.model.WorkCopy;
import amberdb.v2.util.AmberProperty;
import amberdb.v2.util.QueryUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkChildrenQuery extends AmberQueryBase {

    private AmberSession session;

    static Map<String, String> hex = new HashMap<>();
    private static String[] types = {
            "Work", "Page", "EADWork", "Section",
            "Copy",
            "File", "ImageFile", "SoundFile", "MovingImageFile",
            "Description", "CameraData", "EADEntity", "EADFeature", "GeoCoding", "IPTC"
    };
    static String workNotSectionInList;
    static String fileInList;
    static String descInList;
    static {
        for (String t : types) {
            hex.put(t, Hex.encodeHexString(AmberProperty.encode(t)));
        }
        workNotSectionInList = "(X'" + StringUtils.join(new String[] {hex.get("Work"), hex.get("Page"), hex.get("EADWork")}, "', X'") + "')";
        fileInList = "(X'" + StringUtils.join(new String[] {hex.get("File"), hex.get("ImageFile"), hex.get("SoundFile"), hex.get("MovingImageFile")}, "', X'") + "')";
        descInList = "(X'" + StringUtils.join(new String[] {hex.get("Description"), hex.get("CameraData"), hex.get("EADEntity"),
                hex.get("EADFeature"), hex.get("GeoCoding"), hex.get("IPTC")}, "', X'") + "')";
    }

    public WorkChildrenQuery(AmberSession session) {
        super(session);
    }

    public List<Work> getChildren(Long workId) {
        List<ParentChild> rel = session.getParentChildDao().getChildren(workId);
        List<Work> result = new ArrayList();

        if (rel != null) {
            for (ParentChild r : rel) {
                result.add(session.getWorkDao().get(r.getChildId()));
            }
        }

        return result;
    }

    public Work getFirstChild(Long workId) {
        return getChildRange(workId, 0, 1).get(0);
    }

    public List<Work> getChildRange(Long workId, int start, int num){
        List<ParentChild> rel = session.getParentChildDao().getChildren(workId, start, num);
        List<Work> result = new ArrayList();

        if (rel != null) {
            for (ParentChild r :rel) {
                result.add(session.getWorkDao().get(r.getChildId()));
            }
        }

        return result;
    }

    /**
     * Retrieve children Ids of a list of parent Ids by the specified bib level and specified sub type
     * @param parentIds a list of parent Ids
     * @param bibLevel bibLevel of the child
     * @param subTypes subtypes of the child, can be null
     * @return a list of children Ids with the specified bib level and specified sub type
     */
    public List<Long> getChildrenIdsByBibLevelSubType(List<Long> parentIds, BibLevel bibLevel, List<String> subTypes){
        String subTypeString = "";
        if (subTypes != null) {
            subTypeString = QueryUtil.quotedCommaSeperatedStrings(subTypes);
        }
        if (CollectionUtils.isNotEmpty(parentIds)){
            for (Long pid : parentIds) {
                if (StringUtils.isBlank(subTypeString)) {
                    session.getParentChildDao().getChildrenIdsByBibLevel(pid, bibLevel.code());
                } else {
                    session.getParentChildDao().getChildrenIdsByBibLevelSubType(pid, bibLevel.code(), subTypeString);
                }
            }
        }
        return new ArrayList<>();
    }

    public int getTotalChildCount(Long workId) {
        return session.getParentChildDao().getTotalChildCount(workId).intValue();
    }

    public List<CopyRole> getAllChildCopyRoles(Long workId) {
        List<ParentChild> rel = session.getParentChildDao().getChildren(workId);
        List<CopyRole> result = new ArrayList();

        if (rel != null) {
            for (ParentChild r : rel) {
                List<WorkCopy> wcRel = session.getWorkCopyDao().getCopiesByWorkId(r.getChildId());
                if (wcRel != null) {
                    for (WorkCopy wc : wcRel) {
                        result.add(CopyRole.fromString(wc.getCopyRole()));
                    }
                }
            }
        }

        return result;
    }
}
