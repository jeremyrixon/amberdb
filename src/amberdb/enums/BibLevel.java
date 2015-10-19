package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum BibLevel{
    
    COMPONENT("Component", 5), 
    SET("Set", 3), 
    ITEM("Item", 2), 
    PART("Part", 1),
    SECTION("Section", 4),
    NO_BIB_LEVEL("", 0);

    private String code;
    private Integer order;

    private BibLevel(String code, Integer order) {
        this.code = code;
        this.order = order;
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
    
    public Integer order() {
        return order;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (BibLevel lu : BibLevel.values()) {
            list.add(lu.code());
        }
        return list;
    }
   
}
