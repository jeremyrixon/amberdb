package amberdb.sort;

import java.util.Comparator;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.model.Work;

public enum SortField {
    ALIAS ("alias", new Comparator<Work>(){
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
    SHEET_NUMBER ("sheetNumber", new Comparator<Work>(){
        @Override
        public int compare(Work w1, Work w2) {
            //Null value always last
            if (w1.getSheetName() == null){
                return 1;
            }
            if (w2.getSheetName() == null){
                return -1;
            }
            return w1.getSheetName().compareTo(w2.getSheetName());    
            
        }}, new Comparator<Work>(){
        @Override
        public int compare(Work w1, Work w2) {
            //Null value always last
            if (w1.getSheetName() == null){
                return 1;
            }
            if (w2.getSheetName() == null){
                return -1;
            }
            return w2.getSheetName().compareTo(w1.getSheetName()); 
        }
    }),
    ISSUE_DATE ("issueDate", new Comparator<Work>(){
        @Override
        public int compare(Work w1, Work w2) {
            //Null value always last
            if (w1.getIssueDate() == null){
                return 1;
            }
            if (w2.getIssueDate() == null){
                return -1;
            }
            return w1.getIssueDate().compareTo(w2.getIssueDate());    
            
        }}, new Comparator<Work>(){
        @Override
        public int compare(Work w1, Work w2) {
            //Null value always last
            if (w1.getIssueDate() == null){
                return 1;
            }
            if (w2.getIssueDate() == null){
                return -1;
            }
            return w2.getIssueDate().compareTo(w1.getIssueDate());
        }
    });
    
    private final static Logger log = LoggerFactory.getLogger(SortField.class);
    String fieldName;
    Comparator<Work> ascComparator;
    Comparator<Work> descComparator;
    
    private SortField(String fieldName, Comparator<Work> ascComparator, Comparator<Work> descComparator) {
        this.fieldName = fieldName;
        this.ascComparator = ascComparator;
        this.descComparator = descComparator;
    }
    
    public static SortField fromString(String fieldName) {
        for (SortField field : SortField.values()) {
            if (field.fieldName.equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null;
    }
    
}
