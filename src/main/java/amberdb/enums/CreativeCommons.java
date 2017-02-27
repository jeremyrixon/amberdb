package amberdb.enums;

public enum CreativeCommons {
    ATTRIBUTION("CC BY", "Attribution (CC BY)"),
    ATTRIBUTION_SHARE_ALIKE("CC BY-SA", "Attribution-Share Alike (CC BY-SA)"),
    ATTRIBUTION_NO_DERIVATIVES("CC BY-ND", "Attribution-No Derivatives (CC BY-ND)"),
    ATTRIBUTION_NONCOMMERCIAL("CC BY-NC", "Attribution-NonCommercial (CC BY-NC)"),
    ATTRIBUTION_NONCOMMERCIAL_SHARE_ALIKE("CC BY-NC-SA", "Attribution-NonCommercial Share Alike (CC BY-NC-SA)"),
    ATTRIBUTION_NONCOMMERCIAL_NO_DERIVATIVES("CC BY-NC-ND", "Attribution-NonCommercial-No Derivatives (CC BY-NC-ND)");

    private final String code;
    private final String display;

    CreativeCommons(String code, String display) {
        this.code = code;
        this.display = display;
    }
}
