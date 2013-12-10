package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;

public class SessionVertexMapper implements ResultSetMapper<AmberVertex> {
    
    private AmberGraph graph;
    
    public SessionVertexMapper(AmberGraph graph) {
        this.graph = graph;
    }
    
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {

        return new AmberVertex(graph, rs.getLong("id"));
        
    }
}