package amberdb.v2.model.mapper;

import amberdb.v2.model.CameraData;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CameraDataMapper implements ResultSetMapper<CameraData> {

    @Override
    public CameraData map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new CameraData(r.getInt("id"), r.getInt("txn_start"), r.getInt("txn_end"), r.getString("extent"),
                r.getString("localSystemNumber"), r.getString("encodingLevel"), r.getString("standardId"),
                r.getString("language"), r.getString("title"), r.getString("otherTitle"), r.getString("holdingId"),
                r.getString("holdingNumber"), r.getBoolean("australianContent"), r.getString("contributor"),
                r.getString("publisher"), r.getString("recordSource"), r.getString("coverage"), r.getString("bibId"),
                r.getString("creator"), r.getString("coordinates"), r.getString("scaleEtc"), r.getString("exposureTime"),
                r.getString("exposureFNumber"), r.getString("exposureMode"), r.getString("exposureProgram"),
                r.getString("isoSpeedRating"), r.getString("focalLength"), r.getString("lens"),
                r.getString("meteringMode"), r.getString("whiteBalance"), r.getString("fileSource"));
    }
}
