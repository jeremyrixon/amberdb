package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberEdge;
import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;


public class VertexMapper implements ResultSetMapper<AmberVertex> {
    
    private AmberGraph graph;
    
    public VertexMapper(AmberGraph graph) {
        this.graph = graph;
    }
    
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

    	return new AmberVertex(
                rs.getLong("id"), 
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getString("status"));
    }
}
