package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum BibLevel {
    
    COMPONENT("Component"), 
    SET("Set"), 
    ITEM("Item"), 
    PART("Part"),
    SECTION("Section");

    private String code;

    private BibLevel(String code) {
        this.code = code;
    }

    public static BibLevel fromString(String code) {
        if (code != null) {
            for (BibLevel e : BibLevel.values()) {
                if (code.equalsIgnoreCase(e.code)) {
                    return e;
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
        for (BibLevel lu : BibLevel.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
