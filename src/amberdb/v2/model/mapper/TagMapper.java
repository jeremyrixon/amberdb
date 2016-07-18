package amberdb.v2.model.mapper;

import amberdb.v2.model.Tag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagMapper implements ResultSetMapper<Tag> {
    @Override
    public Tag map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Tag(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("name"));
    }
}
