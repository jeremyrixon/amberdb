package amberdb.sql.map;

import amberdb.sql.AmberProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PersistentPropertyMapper implements ResultSetMapper<AmberProperty> {
    public AmberProperty map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        return new AmberProperty(
                rs.getLong("id"),
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getBoolean("b_value"),
                rs.getString("s_value"),
                rs.getInt("i_value"),
                rs.getDouble("d_value"));
    }
}