package amberdb.sql.map;

import amberdb.sql.AmberGraph;
import amberdb.sql.AmberEdge;
import amberdb.sql.AmberProperty;
import amberdb.sql.InSessionException;
import amberdb.sql.State;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PersistentEdgeMapper implements ResultSetMapper<AmberEdge> {
    
    private AmberGraph graph;
    
    public PersistentEdgeMapper(AmberGraph graph) {
        this.graph = graph;
    }
    
    public AmberEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {
        
        long id =  rs.getLong("id");
        
        AmberEdge edge = new AmberEdge(graph, id);
        try {
            graph.edgeDao().insertEdge(
                    id, 
                    rs.getLong("txn_start"),
                    rs.getLong("txn_end"),
                    rs.getLong("v_out"),
                    rs.getLong("v_in"),
                    rs.getString("label"),
                    rs.getInt("edge_order"), 
                    State.AMB.toString());
            
            // load properties from the list of id's later
            graph.addLoadPropertyId(id);

        } catch (Exception e) {
            // Need to log as expected exception when an edge is already present in the session
            s("edge already in session:"+id);
            return null;
        }
        
        return edge;
    }
    
    private void s(String s) {
        graph.log.info(s);
    }
}
