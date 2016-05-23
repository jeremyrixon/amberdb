package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum DepositType {
	ONLINELEGALDEPOSIT("Online legal deposit"),
	ONLINEGOVERNMENTDEPOSIT("Online government deposit"),
	ONLINEVOLUNTARYDEPOSIT("Online voluntary deposit"),
	OFFLINEVOLUNTARYDEPOSIT("Offline voluntary deposit");
	
    
    private String code;
    
    private DepositType(String code) {
        this.code = code;
    }
    
    public String code() {
        return code;
    }
    
    public static DepositType fromString(String code) {
        if (code != null) {
            for (DepositType dm : DepositType.values()) {
                if (code.equalsIgnoreCase(dm.code)) {
                    return dm;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (DepositType dm  : DepositType.values()) {
            list.add(dm.code);
        }
        return list;
    }
}
