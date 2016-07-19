package amberdb.v2.model.mapper;

import com.google.common.base.CaseFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses annotations and reflection to map ResultSet to POJO.
 *
 * @param <T> The class to map
 */
public class AmberDbResultSetMapper<T> implements ResultSetMapper<T> {

    private final Class<T> type;

    public AmberDbResultSetMapper(Class<T> type) {
        this.type = type;
    }

    @Override
    public T map(int row, ResultSet rs, StatementContext ctx) throws SQLException {
        T bean = null;

        try {
            if (rs != null) {
                if (type.isAnnotationPresent(Entity.class)) {
                    ResultSetMetaData md = rs.getMetaData();
                    Field[] fields = type.getSuperclass().getDeclaredFields();
                    ArrayUtils.addAll(fields, type.getDeclaredFields());

                    while (rs.next()) {
                        bean = type.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); ++i) {
                            String colName = md.getColumnLabel(i);
                            Object colValue = rs.getObject(i);

                            for (Field f : fields) {
                                if (f.isAnnotationPresent(Column.class)) {
                                    Column col = f.getAnnotation(Column.class);
                                    if (col.name().equalsIgnoreCase(colName)
                                            && colValue != null) {
                                        BeanUtils.setProperty(bean, f.getName(), colValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return bean;
    }
}
