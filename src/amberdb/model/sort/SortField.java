package amberdb.model.sort;

import java.util.List;
import com.google.common.collect.Lists;

enum SortField {

    ALIAS ("alias"),
    SHEET_NAME ("sheetName"),
    ISSUE_DATE ("issueDate");

    private SortField(String fieldName) {
        this.fieldName = fieldName;
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

    String fieldName;
}