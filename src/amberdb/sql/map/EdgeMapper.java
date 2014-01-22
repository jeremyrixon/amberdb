package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberEdge;
import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;


public class EdgeMapper implements ResultSetMapper<AmberEdge> {
    
    private AmberGraph graph;
    
    public EdgeMapper(AmberGraph graph) {
        this.graph = graph;
    }
    
    public AmberEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

    	return new AmberEdge(
                rs.getLong("id"), 
                rs.getString("label"),
                (AmberVertex) graph.getVertex(rs.getLong("v_in")),
                (AmberVertex) graph.getVertex(rs.getLong("v_out")),
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getLong("edge_order"), 
                rs.getString("status"));
    }
}
