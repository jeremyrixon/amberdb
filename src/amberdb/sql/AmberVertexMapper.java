package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AmberVertexMapper implements ResultSetMapper<AmberVertex> {
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        return new AmberVertex(
                rs.getLong("id"),
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getBoolean("txn_open"),
                rs.getString("properties"),
                rs.getString("pi"));
    }
}