package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class LongArrayMapper implements ResultSetMapper<Long[]> {
    public Long[] map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        return new Long[] {
                rs.getLong(1),
                rs.getLong(2)};
    }
}