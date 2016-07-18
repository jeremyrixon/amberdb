package amberdb.v2.model.mapper;

import amberdb.v2.model.Section;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionMapper implements ResultSetMapper<Section> {
    @Override
    public Section map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Section(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("creator"),
                r.getString("accessConditions"),
                r.getBoolean("allowOnsiteAccess"),
                r.getString("abstract"),
                r.getString("advertising"),
                r.getString("title"),
                r.getString("printedPageNumber"),
                r.getString("captions"),
                r.getString("internalAccessConditions"),
                r.getString("subUnitNo"),
                r.getDate("expiryDate"),
                r.getString("bibLevel"),
                r.getBoolean("illustrated"),
                r.getString("copyrightPolicy"),
                r.getString("metsId"),
                r.getString("subType"),
                r.getString("constraint1"));
    }
}
