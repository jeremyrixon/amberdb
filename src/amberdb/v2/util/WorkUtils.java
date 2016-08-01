package amberdb.v2.util;

import amberdb.v2.model.Work;
import amberdb.v2.util.DateParser;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class WorkUtils {
	static final Logger log = LoggerFactory.getLogger(DateParser.class);

    public static boolean checkCanReturnRepImage(Work work) {
        String internalAccesscondition = work.getInternalAccessConditions();
        if ("closed".equalsIgnoreCase(internalAccesscondition) || "restricted".equalsIgnoreCase(internalAccesscondition)) {
            return false;
        }

        String sensitiveMaterial = work.getSensitiveMaterial();
        return !"yes".equalsIgnoreCase(sensitiveMaterial);
    }
}
