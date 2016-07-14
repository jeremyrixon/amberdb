package amberdb.v1.version;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.v1.graph.DataType;


public class TPropertyMapper implements ResultSetMapper<TProperty> {

    
    public TProperty map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        Long id = rs.getLong("id"); 
        Long start = rs.getLong("txn_start"); 
        Long end = rs.getLong("txn_end"); 
        TId vId = new TId(id, start, end);
        
        String name =rs.getString("name");
        DataType type = DataType.valueOf(rs.getString("type"));
        Blob b = rs.getBlob("value");
        Object value = TProperty.decode(b.getBytes(1, (int) b.length()), type);

        return new TProperty(vId, name, value);
    }
}
