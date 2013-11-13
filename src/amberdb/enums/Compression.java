package amberdb.enums;

public enum Compression {
    
    JPEG("JPEG"), 
    NONE("None"),
    PDF("PDF");

    private String code;

    private Compression(String code) {
        this.code = code;
    }

    public static Compression fromString(String code) {
        if (code != null) {
            for (Compression e : Compression.values()) {
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
}
