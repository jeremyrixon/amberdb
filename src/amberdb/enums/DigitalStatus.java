package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum DigitalStatus {
    
    DIGITISED("Digitised"), 
    NON_DIGITISED("Non-digitised"), 
    PARTIALLY_DIGITISED("Partially digitised"), 
    PRESERVED_ANALOGUE("Preserved analogue");

    private String code;

    private DigitalStatus(String code) {
        this.code = code;
    }

    public static DigitalStatus fromString(String code) {
        if (code != null) {
            for (DigitalStatus e : DigitalStatus.values()) {
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
        for (DigitalStatus lu : DigitalStatus.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
