package amberdb.sql.map;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberProperty;
import amberdb.sql.DataType;
import amberdb.sql.PropertyCodec;


public class PropertyMapper implements ResultSetMapper<AmberProperty> {
    
    public AmberProperty map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        Long id = rs.getLong("id"); 
        String name =rs.getString("name");
        DataType type = DataType.valueOf(rs.getString("type"));
        Blob b = rs.getBlob("value");
        Object value = PropertyCodec.decode(b.getBytes(1, (int) b.length()), type);
    	
    	return new AmberProperty(id, name, value);
    }
}
