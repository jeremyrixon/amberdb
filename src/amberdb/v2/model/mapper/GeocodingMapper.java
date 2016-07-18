package amberdb.v2.model.mapper;

import amberdb.v2.model.Geocoding;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GeocodingMapper implements ResultSetMapper<Geocoding> {
    @Override
    public Geocoding map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Geocoding(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("mapDatum"),
                r.getString("latitude"),
                r.getDate("timestamp"),
                r.getString("longitude"));
    }
}
