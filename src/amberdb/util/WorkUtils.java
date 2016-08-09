package amberdb.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.persistence.Column;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.graph.AmberVertex;
import amberdb.model.Work;

public class WorkUtils {
	static final Logger log = LoggerFactory.getLogger(DateParser.class);    

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

	public static amberdb.v2.model.Work convert(AmberVertex v) {
		amberdb.v2.model.Work work = new amberdb.v2.model.Work();
		work.setId((Long) v.getId());
		work.setTxn_start(v.getTxnStart());
		work.setTxn_end(v.getTxnEnd());
		work.setTitle((String) v.getProperty("title"));
		
        Field[] fields = work.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                try {
                    Converter converter = new DateConverter(null);
                    ConvertUtils.register(converter, Date.class);
					BeanUtils.setProperty(work, f.getName(), v.getProperty(f.getName()));
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.warn("Error setting work field: " + f.getName(), e);
				}
            }
        }
		
		
		return work;
	}
}
