package amberdb.version;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class TEdgeMapper implements ResultSetMapper<TEdge> {
    
    
    private VersionedGraph graph;
    private boolean localOnly; 
    
    
    public TEdgeMapper(VersionedGraph graph, boolean localOnly) {
        this.graph = graph;
        this.localOnly = localOnly;
    }
    
    
    public TEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        VersionedVertex in = graph.getVertex(rs.getLong("v_in"), localOnly);
        VersionedVertex out = graph.getVertex(rs.getLong("v_out"), localOnly);
        if (in == null || out == null) return null;
        
        TEdge edge = new TEdge(
                new TId(
                        rs.getLong("id"),
                        rs.getLong("txn_start"), 
                        rs.getLong("txn_end")),
                rs.getString("label"), 
                out.getId(), 
                in.getId(), 
                null, 
                rs.getInt("edge_order"));

        return edge;
    }
}
