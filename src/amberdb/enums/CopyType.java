package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum CopyType {
    DIGITAL_COPY("d");
    
    private String code;
    
    private CopyType(String code) {
        this.code = code;
    }
    
    public static CopyType fromString(String code) {
        if (code != null) {
            for (CopyType ct : CopyType.values()) {
                if (code.equalsIgnoreCase(ct.code)) {
                    return ct;
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
        for (CopyType lu : CopyType.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
