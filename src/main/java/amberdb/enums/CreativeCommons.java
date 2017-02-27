package amberdb.enums;

public enum CreativeCommons {
    ATTRIBUTION("CC BY", "Attribution"),
    ATTRIBUTION_SHARE_ALIKE("CC BY-SA", "Attribution-Share Alike"),
    ATTRIBUTION_NO_DERIVATIVES("CC BY-ND", "Attribution-No Derivatives"),
    ATTRIBUTION_NONCOMMERCIAL("CC BY-NC", "Attribution-NonCommercial"),
    ATTRIBUTION_NONCOMMERCIAL_SHARE_ALIKE("CC BY-NC-SA", "Attribution-NonCommercial Share Alike"),
    ATTRIBUTION_NONCOMMERCIAL_NO_DERIVATIVES("CC BY-NC-ND", "Attribution-NonCommercial-No Derivatives");

    private final String code;
    private final String display;

    CreativeCommons(String code, String display) {
        this.code = code;
        this.display = display;
    }
}
