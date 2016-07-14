package amberdb.v1.version;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class TEdgeMapper implements ResultSetMapper<TEdge> {
    
    
    public TEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        TEdge edge = new TEdge(
                new TId(
                        rs.getLong("id"),
                        rs.getLong("txn_start"), 
                        rs.getLong("txn_end")),
                rs.getString("label"), 
                rs.getLong("v_out"), 
                rs.getLong("v_in"), 
                null, 
                rs.getInt("edge_order"));

        return edge;
    }
}
