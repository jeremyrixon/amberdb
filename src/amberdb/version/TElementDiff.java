package amberdb.version;


import java.util.Set;


public class TElementDiff {

    TElement elem1, elem2;
    TTransition transition; 
    
    public TElementDiff(Long txn1, Long txn2, TElement e1, TElement e2) {

        elem1 = e1;
        elem2 = e2;
        
        if (elem1 == null && elem2 == null) {
            transition = TTransition.UNCHANGED;
            return;
        }
        if (elem1 == null) {
            if (elem2.id.end > 0L && elem2.id.end == txn2) {
                transition = TTransition.UNCHANGED;
            } else {
                transition = TTransition.NEW;
            }
            return;
        }
        if (elem2 == null) {
            if (elem1.id.end > 0L && ((elem1.id.end == txn2 && elem1.id.start != txn1) || (elem1.id.end == txn1))) {
                transition = TTransition.UNCHANGED;
            } else {
                transition = TTransition.DELETED;
            }
            return;
        }
        if (elem1.equals(elem2)) {
            if (elem1.id.start == txn1 && elem1.id.end == txn2) {
                transition = TTransition.DELETED;
            } else {
                transition = TTransition.UNCHANGED;
            }
            return;
        }
        transition = TTransition.MODIFIED;
    }
    
    

    public Object getProperty(String propertyName) throws TDiffException {
        switch (transition) {
        case NEW: return elem2.getProperty(propertyName);
        case DELETED: return elem1.getProperty(propertyName);
        case MODIFIED: 
            Object obj1 = elem1.getProperty(propertyName);
            Object obj2 = elem2.getProperty(propertyName);
            if (obj1 == null) {
                return new Object[] { null, obj2 };
            } else {
                return (obj1.equals(obj2)) ? new Object[] { obj1 } : new Object[] { obj1, obj2 };
            }    

        case UNCHANGED: return (elem1 == null) ? ((elem2 == null) ? null : elem2.getProperty(propertyName)) : elem1.getProperty(propertyName);
        }
        throw new TDiffException("Cannot get property. Unknown Transition state: " + transition);
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder(transition + ": " );
        switch (transition) {
        case NEW: 
            sb.append(elem2);
            break;
        case UNCHANGED:
            if (elem1 == null) {
                if (elem2 == null) {
                    sb.append("null -> null (quantum foam)");
                } else {
                    sb.append(elem2);
                }
            } else {
                sb.append(elem1);
            }
            break;
        case DELETED:
            if (elem1 == null) {
                sb.append(elem2);
            } else {
                sb.append(elem1);
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
    
    
    public boolean isTransient() {
        if (elem1 == null && elem2 == null) {
            return true;
        }
        return false;
    }

    
    private String printDiff() {
        StringBuilder sb = new StringBuilder();
        Set<String> propNames = elem1.getPropertyKeys();
        propNames.addAll(elem2.getPropertyKeys());
        for (String propName : propNames) {
            sb.append(propName + ": ");
            Object[] objArr = (Object[]) getProperty(propName);
            sb.append((objArr[0] != null) ? objArr[0].toString() : "<null>"); 
            if (objArr.length > 1) {
                sb.append(" -> ");
                sb.append((objArr[1] != null) ? objArr[1].toString() : "<null>"); 
            }
            sb.append("\n");
        }
        if (sb.length() > 1) sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    
    public TTransition getTransition() {
        return transition;
    }
}
