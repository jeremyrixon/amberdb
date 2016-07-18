package amberdb.v2.model.mapper;

import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AmberDbMapperFactory implements ResultSetMapperFactory {
    @Override
    public boolean accepts(Class type, StatementContext ctx) {
        return type.isAnnotationPresent(MapWith.class);
    }

    @Override
    public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
        MapWith rm = (MapWith) type.getAnnotation(MapWith.class);
        try {
            return rm.value().newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
