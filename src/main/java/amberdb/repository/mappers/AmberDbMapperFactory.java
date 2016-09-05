package amberdb.repository.mappers;

import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.persistence.Entity;

public class AmberDbMapperFactory implements ResultSetMapperFactory {
    @Override
    public boolean accepts(Class type, StatementContext ctx) {
        return type.isAnnotationPresent(Entity.class);
    }

    @Override
    public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
        return new AmberDbResultSetMapper(type);
    }
}
