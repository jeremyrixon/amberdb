package amberdb.sql.map;

import amberdb.sql.AmberProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AmberPropertyTxnMapper implements ResultSetMapper<AmberProperty> {
    public AmberProperty map(int index, ResultSet rs, StatementContext ctx) 
            throws SQLException {
        return new AmberProperty(
                rs.getString("name"),
                rs.getBoolean("b_value"),
                rs.getDouble("d_value"),
                rs.getString("s_value"),
                rs.getInt("i_value"));
    }
}