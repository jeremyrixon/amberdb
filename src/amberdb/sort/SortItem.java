package amberdb.sort;

import java.util.Comparator;
import java.util.List;

import amberdb.model.Work;

public class SortItem {
    private final SortField sortField;
    private final SortOrder sortOrder;
    public SortItem(SortField sortField, SortOrder sortOrder) {
        super();
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
    public SortItem(String fieldName, boolean desc){
        this.sortField = SortField.fromString(fieldName);
        if (sortField == null){
            throw new IllegalArgumentException("The field "+fieldName+" is not defined in SortField class");
        }
        this.sortOrder = desc ? SortOrder.DESC : SortOrder.ASC; 
    }
    
    public boolean desc() {
        return sortOrder == SortOrder.DESC;
    }
    public String fieldName() {
        return sortField.fieldName;
    }
    public Comparator<Work> compartor() {
        return desc() ? sortField.descComparator : sortField.ascComparator;
    }
    
    public static List<String> allSortFields() {
        return SortField.allSortFields();
    }
}
