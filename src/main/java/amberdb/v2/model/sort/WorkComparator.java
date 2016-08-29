package amberdb.v2.model.sort;

import amberdb.util.NaturalSort;
import amberdb.v2.model.Work;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

public class WorkComparator implements Comparator<Work> {

    private String sortPropertyName;
    private boolean sortForward;

    public WorkComparator(String sortPropertyName, boolean sortForward) {
        this.sortPropertyName = sortPropertyName;
        this.sortForward = sortForward;
    }

    @Override
    public int compare(Work w1, Work w2) {
        if (w1 == w2) {
            return 0;
        }

        // We want the order to always be deterministic to avoid UI behaviour of works showing in different orders
        // So we fall back to ordering by object id if the return value would be 0
        int fallbackOrder = w1.getObjId().compareTo(w2.getObjId()) * (sortForward ? 1 : -1);

        Object o1 = null;
        Object o2 = null;
        try {
            o1 = BeanUtils.getProperty(w1, sortPropertyName);
            o2 = BeanUtils.getProperty(w2, sortPropertyName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return 0;
        }

        // null guards
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        // easy comparisons
        if (o1.equals(o2)) return fallbackOrder;

        // check for a json encoded list and treat empty lists same as null
        if (o1 instanceof String && ((String) o1).startsWith("[")) {
            if (o1.equals("[]")) return 1;
        }

        // easy comparisons for Strings, Booleans, Dates and a variety of number formats
        if (o1 instanceof String){
            int i = NaturalSort.compareNatural((String)o1, (String)o2);
            return sortForward ? i : -i;
        }else if (o1 instanceof Comparable) {
            int i = ((Comparable) o1).compareTo(o2);
            return sortForward ? i : -i;
        }

        // can't decide how to compare - call them equal
        return fallbackOrder;
    }
}
