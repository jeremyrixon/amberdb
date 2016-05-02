package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum DepositMethod {
	ONLINELEGALDEPOSIT("Online legal deposit"),
	ONLINEGOVERNMENTDEPOSIT("Online government deposit"),
	ONLINEVOLUNTARYDEPOSIT("Online voluntary deposit"),
	OFFLINEVOLUNTARYDEPOSIT("Offline voluntary deposit");
	
    
    private String code;
    
    private DepositMethod(String code) {
        this.code = code;
    }
    
    public String code() {
        return code;
    }
    
    public static DepositMethod fromString(String code) {
        if (code != null) {
            for (DepositMethod dm : DepositMethod.values()) {
                if (code.equalsIgnoreCase(dm.code)) {
                    return dm;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (DepositMethod dm  : DepositMethod.values()) {
            list.add(dm.code);
        }
        return list;
    }
}
