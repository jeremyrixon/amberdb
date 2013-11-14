package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum CopyRole {
    ACCESS_COPY("ac"), MASTER_COPY("m"), OCR_JSON_COPY("oc"), OCR_ALTO_COPY("at"), OCR_METS_COPY("mt");

    private String code;

    private CopyRole(String code) {
        this.code = code;
    }

    public static CopyRole fromString(String code) {
        if (code != null) {
            for (CopyRole cr : CopyRole.values()) {
                if (code.equalsIgnoreCase(cr.code)) {
                    return cr;
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
        for (CopyRole lu : CopyRole.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
