package amberdb.sql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;
import amberdb.sql.State;

public class PersistentVertexMapper implements ResultSetMapper<AmberVertex> {
    
    private AmberGraph graph;
    
    public PersistentVertexMapper(AmberGraph graph) {
        this.graph = graph;
    }
    
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {
        
        long id = rs.getLong("id");
        AmberVertex vertex = new AmberVertex(graph, id);
     
        try {
            graph.vertexDao().insertVertex(
                    id, 
                    rs.getLong("txn_start"),
                    rs.getLong("txn_end"), 
                    State.AMB.toString());

            // load properties also
            graph.addLoadPropertyId(id);
            
        } catch (Exception e) {
            // Need to log as expected exception when a vertex is already present in the session
            s("vertex already in session:"+id);
            return null;
        }
        
        return vertex;
    }
    
    private void s(String s) {
        graph.log.info(s);
    }
}