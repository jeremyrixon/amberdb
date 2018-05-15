package amberdb.enums;

/**
 * This enum forms a subset of the PUBLICATION_CATEGORY lookup list. This enum is primarily used to determine whether a work
 * represents a government deposit from NED, and if so the level of government (local, state, commonwealth).
 *
 * The publication category lookup may be used for other purposes as well, so the full list is not necessarily represented here.
 */
public enum NEDPublicationCategory {
    ACADEMIC("Academic", "ACADEMIC"),
    SELF_PUBLISHER("Self-publisher", "SELF_PUBLISHER"),
    ORGANISATION("Organisation", "ORGANISATION"),
    COMMONWEALTH_GOVERNMENT("Commonwealth Government", "COMMONWEALTH_GOVERNMENT"),
    STATE_GOVERNMENT("State or Territory Government", "STATE_GOVERNMENT"),
    LOCAL_GOVERNMENT("Local Government", "LOCAL_GOVERNMENT"),
    @Deprecated
    GOVERNMENT("Government", "GOVERNMENT"), // deprecated - this was used in edeposit works: NED separates the different government levels
    @Deprecated
    PUBLISHER("Publisher", "PUBLISHER"); // deprecated - this was used in edeposit works, but has been replaced with the list above

    private String code;
    private String nedCode;

    NEDPublicationCategory(String code, String nedCode) {
        this.code = code;
        this.nedCode = nedCode;
    }

    public String getCode() {
        return code;
    }

    public String getNedCode() {
        return nedCode;
    }

    public static NEDPublicationCategory fromCode(String code) {
        for (NEDPublicationCategory cat : values()) {
            if (cat.code.equalsIgnoreCase(code)) {
                return cat;
            }
        }

        return null;
    }

    public static NEDPublicationCategory fromNEDCode(String nedCode) {
        for (NEDPublicationCategory cat : values()) {
            if (cat.nedCode.equalsIgnoreCase(nedCode)) {
                return cat;
            }
        }

        return null;
    }
}
