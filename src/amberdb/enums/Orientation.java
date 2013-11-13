package amberdb.enums;

public enum Orientation {
    
    LANDSCAPE("Landscape"), 
    Portrait("Portrait");

    private String code;

    private Orientation(String code) {
        this.code = code;
    }

    public static Orientation fromString(String code) {
        if (code != null) {
            for (Orientation e : Orientation.values()) {
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
