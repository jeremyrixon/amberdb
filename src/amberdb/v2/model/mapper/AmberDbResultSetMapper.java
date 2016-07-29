package amberdb.v2.model.mapper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Uses annotations and reflection to map ResultSet to POJO.
 *
 * @param <T> The class to map
 */
public class AmberDbResultSetMapper<T> implements ResultSetMapper<T> {

    private final Class<T> type;

    private static final Logger log = LoggerFactory.getLogger(AmberDbResultSetMapper.class);

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
                    // Models inherit from AmberModel, which has the ID, transaction start and end properties
                    // but also have to take into account models inheriting from other models.
                    // eg. Section => Work => AmberModel.
                    // Adjust for level of inheritance when needed.
                    Field[] fields = type.getSuperclass().getSuperclass().getDeclaredFields();
                    ArrayUtils.addAll(fields, type.getSuperclass().getDeclaredFields());
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

        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            log.error("Bean of type {} could not be mapped.", type);
        }

        return bean;
    }
}
