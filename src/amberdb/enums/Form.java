package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum Form {
    
    BOOK("Book"), 
    JOURNAL("Journal");

    private String code;

    private Form(String code) {
        this.code = code;
    }

    public static Form fromString(String code) {
        if (code != null) {
            for (Form e : Form.values()) {
                if (code.equalsIgnoreCase(e.code)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (Form lu : Form.values()) {
            list.add(lu.code());
        }
        return list;
    }

    public String code() {
        return code;
    }
}