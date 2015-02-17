package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum MaterialType {
    IMAGE("Image"),
    SOUND("Sound"),
    TEXT("Text"),
    MOVINGIMAGE("Moving Image");
    
    String code;
    
    private MaterialType(String code) {
        this.code = code;
    }
    
    public String code() {
        return this.code;
    }
    
    public static MaterialType fromString(String code) {
        if (code != null) {
            for (MaterialType mt : MaterialType.values()) {
                if (code.equalsIgnoreCase(mt.code)) {
                    return mt;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (MaterialType mt : MaterialType.values()) {
            list.add(mt.code);
        }
        return list;
    }
}
