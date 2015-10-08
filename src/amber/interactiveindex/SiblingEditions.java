package amber.interactiveindex;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Represent a map grid with multiple PIs
 */
public class SiblingEditions {
    private List<String> objectIds = new ArrayList<>();

    public SiblingEditions() {
    }
    
    public SiblingEditions(List<String> objIdList) {
        objectIds.addAll(objIdList);
    }

    public List<String> getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(List<String> objectIds) {
        this.objectIds = objectIds;
    }

    public boolean contains(String objId) {
        return objectIds.contains(objId);
    }

    /**
     * Return all object Ids in the objectIds list which is not equal to the specified objId
     */
    @SuppressWarnings("unchecked")
    public List<String> getAllSiblingEditionObjectIdsBut(final String objId) {
        return (List<String>)CollectionUtils.selectRejected(objectIds, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object.equals(objId);
            }
        });
    }
}
