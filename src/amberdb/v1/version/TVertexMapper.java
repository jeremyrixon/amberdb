package amberdb.v1.version;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class TVertexMapper implements ResultSetMapper<TVertex> {

    
    public TVertex map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        
        TVertex vertex = new TVertex(
                new TId(
                        rs.getLong("id"), 
                        rs.getLong("txn_start"), 
                        rs.getLong("txn_end")), 
                null);
        return vertex;
    }
}
