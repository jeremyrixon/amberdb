package amberdb.sql.map;

import amberdb.sql.AmberVertex;
import amberdb.sql.InSessionException;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PersistentVertexMapper implements ResultSetMapper<AmberVertex> {
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {
        AmberVertex vertex;
        try {
            vertex = new AmberVertex(
                    rs.getLong("id"),
                    rs.getLong("txn_start"),
                    rs.getLong("txn_end"));
        } catch (InSessionException ise) {
            return null;
        }
        return vertex;
    }
}