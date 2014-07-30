package amberdb.version;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TElementDiff {

    TElement element1, element2;
    TTransition transition; 
    Map<String, Object[]> diffs;
    
    public TElementDiff(Long txn1, Long txn2, TElement e1, TElement e2) {

        element1 = e1;
        element2 = e2;
        
        if (element1 == null && element2 == null) {
            transition = TTransition.UNCHANGED;
            return;
        }
        if (element1 == null) {
            if (element2.id.end > 0L && element2.id.end == txn2) {
                transition = TTransition.UNCHANGED;
            } else {
                transition = TTransition.NEW;
            }
            return;
        }
        if (element2 == null) {
            if (element1.id.end > 0L && ((element1.id.end == txn2 && element1.id.start != txn1) || (element1.id.end == txn1))) { 
                transition = TTransition.UNCHANGED;
            } else {
                transition = TTransition.DELETED;
            }
            return;
        }
        if (element1.equals(element2)) {
            if (element1.id.start == txn1 && element1.id.end == txn2) {
                transition = TTransition.DELETED;
            } else {
                transition = TTransition.UNCHANGED;
            }
            return;
        }
        transition = TTransition.MODIFIED;
        diffs = new HashMap<>();
        diffs.put("id", (element1.id.equals(element2.id)) ? new Object[] { element1.id } : new Object[] { element1.id, element2.id });
        
        // now go through all the properties for both e1 and e2
        Set<String> propNames = element1.getPropertyKeys();
        propNames.addAll(element2.getPropertyKeys());
        for (String propName : propNames) {
            
            Object obj1 = element1.getProperty(propName);
            Object obj2 = element2.getProperty(propName);
            if (obj1 == null) {
                diffs.put(propName, new Object[] { null, obj2 });
            } else {
                diffs.put(propName, (obj1.equals(obj2)) ? new Object[] { obj1 } : new Object[] { obj1, obj2 });
            }    
        }
    }
    
    
    public Object[] getDiff(String propertyName) throws TDiffException {
        if (transition == TTransition.MODIFIED) {
            return diffs.get(propertyName);
        }
        throw new TDiffException("Cannot get difference. TElementDiff state is " + transition);
    }
    

    public Object getProperty(String propertyName) throws TDiffException {
        switch (transition) {
        case NEW: return element2.getProperty(propertyName);
        case DELETED: return element1.getProperty(propertyName);
        case MODIFIED: return diffs.get(propertyName);
        case UNCHANGED: return element1.getProperty(propertyName);
        }
        throw new TDiffException("Cannot get property. Unknown Transition state: " + transition);
    }
    
    
    public String toString() {

        StringBuilder sb = new StringBuilder(transition + ": " );
        switch (transition) {
        case NEW: 
            sb.append(element2);
            break;
        case UNCHANGED:
            if (element1 == null) {
                sb.append(element2);
            } else {
                sb.append(element1);
            }
            break;
        case DELETED:
            if (element1 == null) {
                sb.append(element2);
            } else {
                sb.append(element1);
            }
            break;
        case MODIFIED: 
            sb.append(printDiff());
            break;
        default:
            throw new TDiffException("Unexpected transition: " + transition);
        }
        return sb.toString();
    }
    
    
    private String printDiff() {
        StringBuilder sb = new StringBuilder();
        for (String propName : diffs.keySet()) {
            sb.append(propName + ": ");
            Object[] objArr = diffs.get(propName);
            sb.append((objArr[0] != null) ? objArr[0].toString() : "<null>"); 
            if (objArr.length > 1) {
                sb.append(" -> ");
                sb.append((objArr[1] != null) ? objArr[1].toString() : "<null>"); 
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
    public TTransition getTransition() {
        return transition;
    }
}
