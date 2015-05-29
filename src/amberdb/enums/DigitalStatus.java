package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum DigitalStatus {
    
    DIGITISED("Captured"), 
    NON_DIGITISED("Not Captured"), 
    CAPTURED("Captured"),
    NOT_CAPTURED("Not Captured"),
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
