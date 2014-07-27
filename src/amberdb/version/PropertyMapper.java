package amberdb.version;


import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class PropertyMapper implements ResultSetMapper<VersionProperty> {

    
    public VersionProperty map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        Long id = rs.getLong("id"); 
        Long start = rs.getLong("txn_start"); 
        Long end = rs.getLong("txn_end"); 
        TId vId = new TId(id, start, end);
        
        String name =rs.getString("name");
        DataType type = DataType.valueOf(rs.getString("type"));
        Blob b = rs.getBlob("value");
        Object value = VersionProperty.decode(b.getBytes(1, (int) b.length()), type);

        return new VersionProperty(vId, name, value);
    }
}
