package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AmberEdgeMapper implements ResultSetMapper<AmberEdge> {
    public AmberEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {
        return new AmberEdge(
                rs.getLong("id"),
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getString("properties"),
                rs.getLong("v_out"), 
                rs.getLong("v_in"), 
                rs.getString("label"),
                rs.getInt("edge_order"),
                rs.getLong("pi"));
    }
}
