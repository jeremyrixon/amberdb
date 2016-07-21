package amberdb.util;

import amberdb.model.Work;

public class WorkUtils {

    public static boolean checkCanReturnRepImage(Work work) {
        String internalAccesscondition = work.getInternalAccessConditions();
        if ("closed".equalsIgnoreCase(internalAccesscondition) || "restricted".equalsIgnoreCase(internalAccesscondition)) {
            return false;
        }

        String sensitiveMaterial = work.getSensitiveMaterial();
        return !"yes".equalsIgnoreCase(sensitiveMaterial);
    }
}
