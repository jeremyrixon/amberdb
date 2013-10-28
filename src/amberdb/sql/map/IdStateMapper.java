package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class IdStateMapper implements ResultSetMapper<IdState> {
    public IdState map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        return new IdState(rs.getLong("id"),rs.getString("state"));
    }
}