package amberdb.util;

import amberdb.model.Work;

public class WorkUtils {

    public static boolean checkCanReturnRepImage(Work work) {
        String accessCondition = work.getAccessConditions();
        if (!"unrestricted".equalsIgnoreCase(accessCondition)) {
            return false;
        }

        String internalAccesscondition = work.getInternalAccessConditions();
        if ("closed".equalsIgnoreCase(internalAccesscondition) || "restricted".equalsIgnoreCase(internalAccesscondition)) {
            return false;
        }

        String sensitiveMaterial = work.getSensitiveMaterial();
        return !"yes".equalsIgnoreCase(sensitiveMaterial);
    }
}
