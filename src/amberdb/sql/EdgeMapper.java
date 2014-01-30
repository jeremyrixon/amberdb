package amberdb.sql;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class EdgeMapper implements ResultSetMapper<AmberEdgeWithState> {
    
    
    private AmberGraph graph;
    private boolean localOnly; 
    
    
    public EdgeMapper(AmberGraph graph, boolean localOnly) {
        this.graph = graph;
        this.localOnly = localOnly;
    }
    
    
    public AmberEdgeWithState map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        AmberVertex in = (AmberVertex) graph.getVertex(rs.getLong("v_in"), localOnly);
        AmberVertex out = (AmberVertex) graph.getVertex(rs.getLong("v_out"), localOnly);
        if (in == null || out == null) return null;
        
        AmberEdge edge = new AmberEdge(
                rs.getLong("id"), 
                rs.getString("label"),
                out,
                in,
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getInt("edge_order"));

        return new AmberEdgeWithState(edge, rs.getString("state"));
    }
}
