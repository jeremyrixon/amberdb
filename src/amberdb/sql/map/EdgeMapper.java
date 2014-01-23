package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberEdge;
import amberdb.sql.AmberEdgeWithState;
import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;


public class EdgeMapper implements ResultSetMapper<AmberEdgeWithState> {
    
    private AmberGraph graph;
    
    public EdgeMapper(AmberGraph graph) {
        this.graph = graph;
    }
    
    public AmberEdgeWithState map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

    	AmberEdge edge = new AmberEdge(
                rs.getLong("id"), 
                rs.getString("label"),
                (AmberVertex) graph.getVertex(rs.getLong("v_in")),
                (AmberVertex) graph.getVertex(rs.getLong("v_out")),
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getLong("edge_order"));
    	
    	return new AmberEdgeWithState(edge, rs.getString("state"));
    }
}
