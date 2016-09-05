package amberdb.repository.mappers;

import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultColumnMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Slight modification of JDBI's ReflectionBeanMapper to use <code>@Column</code>
 * annotations for property names.
 *
 * @param <T> The class to map
 */
public class AmberDbResultSetMapper<T> implements ResultSetMapper<T> {

    private final Class<T> type;
    private final Map<String, Field> properties = new HashMap<String, Field>();

    private static final Logger log = LoggerFactory.getLogger(AmberDbResultSetMapper.class);

    public AmberDbResultSetMapper(Class<T> type) {
        this.type = type;
        cacheAllFieldsIncludingSuperClass(type);
    }

    private void cacheAllFieldsIncludingSuperClass(Class<T> type) {
        Class aClass = type;
        while(aClass != null) {
            for (Field field : aClass.getDeclaredFields()) {
                String fieldName = field.getName().toLowerCase();
                if (field.isAnnotationPresent(Column.class)) {
                    Column col = field.getAnnotation(Column.class);
                    if (StringUtils.isNotBlank(col.name())) {
                        fieldName = col.name().toLowerCase();
                    }
                }
                properties.put(fieldName, field);
            }
            aClass = aClass.getSuperclass();
        }
    }

    @Override
    public T map(int row, ResultSet rs, StatementContext ctx) throws SQLException {
        T bean;

        try {
            bean = type.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("A bean, %s, was mapped " +
                    "which was not instantiable", type.getName()), e);
        }

        ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 1; i <= metadata.getColumnCount(); ++i) {
            String name = metadata.getColumnLabel(i).toLowerCase();

            Field field = properties.get(name);

            if (field != null) {
                Class type = field.getType();

                Object value;
                ResultColumnMapper mapper = ctx.columnMapperFor(type);
                if (mapper != null) {
                    value = mapper.mapColumn(rs, i, ctx);
                } else {
                    value = rs.getObject(i);
                }

                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(String.format("Unable to access " +
                            "property, %s", name), e);
                }
            }
        }

        return bean;
    }
}
