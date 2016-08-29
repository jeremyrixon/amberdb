package amberdb.v2.model.mapper;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExistsMapper implements ResultSetMapper<Boolean> {

    public Boolean map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return rs.first();
    }
}