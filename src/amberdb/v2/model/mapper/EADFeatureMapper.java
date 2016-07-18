package amberdb.v2.model.mapper;

import amberdb.v2.model.EADFeature;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EADFeatureMapper implements ResultSetMapper<EADFeature> {
    @Override
    public EADFeature map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new EADFeature(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("records"),
                r.getString("featureType"),
                r.getString("fields"),
                r.getString("featureId"));
    }
}
