package amberdb.v2.model.mapper;

import amberdb.v2.model.IPTC;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IPTCMapper implements ResultSetMapper<IPTC> {
    @Override
    public IPTC map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new IPTC(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("province"),
                r.getString("city"));
    }
}
