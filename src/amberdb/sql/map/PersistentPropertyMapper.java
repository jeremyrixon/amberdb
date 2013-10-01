package amberdb.sql.map;

import amberdb.sql.AmberProperty;
import amberdb.sql.DataType;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PersistentPropertyMapper implements ResultSetMapper<AmberProperty> {

    public AmberProperty map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        DataType type = DataType.valueOf(rs.getString("type"));
        Blob b = rs.getBlob("value");
        Object value = AmberProperty.decodeBlob(b.getBytes(1, (int) b.length()), type);
        
        return new AmberProperty(id, name, type, value);
    }
}