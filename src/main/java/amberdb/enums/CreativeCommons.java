package amberdb.enums;

import org.apache.commons.lang.StringUtils;

public enum CreativeCommons {
    ATTRIBUTION("CC BY", "Attribution (CC BY)"),
    ATTRIBUTION_SHAREALIKE("CC BY-SA", "Attribution-ShareAlike (CC BY-SA)"),
    ATTRIBUTION_NODERIVATIVES("CC BY-ND", "Attribution-NoDerivatives (CC BY-ND)"),
    ATTRIBUTION_NONCOMMERCIAL("CC BY-NC", "Attribution-NonCommercial (CC BY-NC)"),
    ATTRIBUTION_NONCOMMERCIAL_SHAREALIKE("CC BY-NC-SA", "Attribution-NonCommercial-ShareAlike (CC BY-NC-SA)"),
    ATTRIBUTION_NONCOMMERCIAL_NODERIVATIVES("CC BY-NC-ND", "Attribution-NonCommercial-NoDerivatives (CC BY-NC-ND)");

    private final String code;
    private final String display;

    CreativeCommons(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public String display() {
        return this.display;
    }

    public static CreativeCommons fromString(String code) {
        if (StringUtils.isNotBlank(code)) {
            for (CreativeCommons creativeCommons : CreativeCommons.values()) {
                if (code.equalsIgnoreCase(creativeCommons.code)) {
                    return creativeCommons;
                }
            }
        }
        return null;
    }
}
