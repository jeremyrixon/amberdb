package amberdb.enums;

public enum BibLevel {
    
    COMPONENT("Component"), 
    SET("Set"), 
    ITEM("Item"), 
    PART("Part");

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
}
