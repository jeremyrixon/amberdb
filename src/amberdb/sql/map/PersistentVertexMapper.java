package amberdb.sql.map;

import amberdb.sql.AmberGraph;
import amberdb.sql.AmberProperty;
import amberdb.sql.AmberVertex;
import amberdb.sql.InSessionException;
import amberdb.sql.State;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PersistentVertexMapper implements ResultSetMapper<AmberVertex> {
    
    public static AmberGraph graph;
    
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
            for (AmberProperty p : graph.persistentDao().getProperties(id)) {
                graph.vertexDao().setProperty(p.getId(), p.getName(), 
                        p.getType().toString(), AmberProperty.encodeBlob(p.getValue()));
            }            
            
        } catch (Exception e) {
            // Need to log as expected exception when a vertex is already present in the session
            return null;
        }
        
        return vertex;
    }
}