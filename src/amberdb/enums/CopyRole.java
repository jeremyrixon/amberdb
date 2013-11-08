package amberdb.enums;

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
}
