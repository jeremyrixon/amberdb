package amberdb.sort;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import amberdb.model.Work;

enum SortField {
    ALIAS ("Alias", new Comparator<Work>(){
        @Override
        public int compare(Work w1, Work w2) {
            try {
                //Null value always last
                if (CollectionUtils.isEmpty(w1.getAlias())){
                    return 1;
                }
                if (CollectionUtils.isEmpty(w2.getAlias())){
                    return -1;
                }
                return w1.getAlias().get(0).compareTo(w2.getAlias().get(0));
            } catch (Exception e) {
                log.error("Bad alias found", e);
            }
            return 0;
        }}, new Comparator<Work>(){
        @Override
        public int compare(Work w1, Work w2) {
            try {
                //Null value always last
                if (CollectionUtils.isEmpty(w1.getAlias())){
                    return 1;
                }
                if (CollectionUtils.isEmpty(w2.getAlias())){
                    return -1;
                }
                return w2.getAlias().get(0).compareTo(w1.getAlias().get(0));
            } catch (Exception e) {
                log.error("Bad alias found", e);
            }
            return 0;
        }
    }), 
    SHEET_NAME ("Sheet Name", new SheetNameComparatorAsc(), new SheetNameComparatorDesc()),
    ISSUE_DATE ("Issue Date", new IssueDateComparatorAsc(), new IssueDateComparatorDesc());
    
    private final static Logger log = LoggerFactory.getLogger(SortField.class);
    String fieldName;
    Comparator<Work> ascComparator;
    Comparator<Work> descComparator;
    
    private SortField(String fieldName, Comparator<Work> ascComparator, Comparator<Work> descComparator) {
        this.fieldName = fieldName;
        this.ascComparator = ascComparator;
        this.descComparator = descComparator;
    }
    
    public static List<String> allSortFields() {
        List<String> list = Lists.newArrayList();
        for (SortField sortField : values()){
            list.add(sortField.fieldName);
        }
        return list;
    }
    
    public static SortField fromString(String fieldName) {
        for (SortField field : SortField.values()) {
            if (field.fieldName.equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null;
    }
    
    private static abstract class IssueDateComparator implements Comparator<Work>{
        @Override
        public int compare(Work w1, Work w2) {
            //Null value always last
            if (w1.getIssueDate() == null){
                return 1;
            }
            if (w2.getIssueDate() == null){
                return -1;
            }
            return 0;
        }
    }
    
    private static class IssueDateComparatorAsc extends IssueDateComparator{
        @Override
        public int compare(Work w1, Work w2) {
            int compare = super.compare(w1, w2);
            return compare == 0 ? w1.getIssueDate().compareTo(w2.getIssueDate()) : compare;
        }
    }
    
    private static class IssueDateComparatorDesc extends IssueDateComparator{
        @Override
        public int compare(Work w1, Work w2) {
            int compare = super.compare(w1, w2);
            return compare == 0 ? w2.getIssueDate().compareTo(w1.getIssueDate()) : compare;
        }
    }
    
    private static abstract class SheetNameComparator implements Comparator<Work>{
        @Override
        public int compare(Work w1, Work w2) {
            //Null value always last
            if (w1.getSheetName() == null){
                return 1;
            }
            if (w2.getSheetName() == null){
                return -1;
            }
            return 0;
        }
    }
    
    private static class SheetNameComparatorAsc extends SheetNameComparator{
        @Override
        public int compare(Work w1, Work w2) {
            int compare = super.compare(w1, w2);
            return compare == 0 ? w1.getSheetName().compareTo(w2.getSheetName()) : compare;
        }
    }
    
    private static class SheetNameComparatorDesc extends SheetNameComparator{
        @Override
        public int compare(Work w1, Work w2) {
            int compare = super.compare(w1, w2);
            return compare == 0 ? w2.getSheetName().compareTo(w1.getSheetName()) : compare;
        }
    }

    
    
}
