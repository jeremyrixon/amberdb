package amberdb.sql.map;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberProperty;
import amberdb.sql.DataType;

public class SessionPropertyMapper implements ResultSetMapper<AmberProperty> {

    public AmberProperty map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {

        Blob b = rs.getBlob("value");
        DataType type = DataType.valueOf(rs.getString("type"));
        return new AmberProperty(
                rs.getLong("id"),
                rs.getString("name"),
                DataType.valueOf(rs.getString("type")),
                AmberProperty.decodeBlob(b.getBytes(1, (int) b.length()), type));
    }
}