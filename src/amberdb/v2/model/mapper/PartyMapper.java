package amberdb.v2.model.mapper;

import amberdb.v2.model.Party;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PartyMapper implements ResultSetMapper<Party> {

    @Override
    public Party map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Party(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("name"),
                r.getBoolean("suppressed"),
                r.getString("orgUrl"),
                r.getString("logoUrl"));
    }
}
