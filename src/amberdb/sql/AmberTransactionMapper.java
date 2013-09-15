package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AmberTransactionMapper implements ResultSetMapper<AmberTransaction> {
    public AmberTransaction map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {
        return new AmberTransaction(
                rs.getLong("id"), 
                rs.getString("user"),
                rs.getDate("commit"));
    }
}