package amberdb.sql.map;

import amberdb.sql.AmberVertex;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class SessionVertexMapper implements ResultSetMapper<AmberVertex> {
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        return new AmberVertex(
                rs.getLong("id"),
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getInt("state"));
    }
}