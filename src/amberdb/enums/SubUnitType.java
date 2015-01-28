package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum SubUnitType {
    CLASS("Class"),
    COLLECTION("Collection"),
    FILE("File"),
    FONDS("Fonds"),
    ITEM("Item"),
    OTHERLEVEL("Otherlevel"),
    RECORDGRP("Recordgrp"),
    SERIES("Series"),
    SUBFONDS("Subfonds"),
    SUBGRP("Subgrp"),
    SUBSERIES("Subseries");
    
    private String code;
    
    private SubUnitType(String code) {
        this.code = code;
    }
    
    public static SubUnitType fromString(String code) {
        if (code != null) {
            for (SubUnitType t : SubUnitType.values()) {
                if (code.equalsIgnoreCase(t.code)) {
                    return t;
                }
            }
        }
        return null;
    }
    
    public String code() {
        return code;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (SubUnitType t : SubUnitType.values()) {
            list.add(t.code());
        }
        return list;
    }
}
