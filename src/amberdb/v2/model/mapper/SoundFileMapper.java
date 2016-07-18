package amberdb.v2.model.mapper;

import amberdb.v2.model.SoundFile;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SoundFileMapper implements ResultSetMapper<SoundFile> {
    @Override
    public SoundFile map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new SoundFile(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("fileName"),
                r.getString("software"),
                r.getString("thickness"),
                r.getString("channel"),
                r.getString("bitrate"),
                r.getString("mimeType"),
                r.getString("durationType"),
                r.getString("speed"),
                r.getString("duration"),
                r.getString("toolId"),
                r.getString("checksum"),
                r.getString("soundField"),
                r.getString("fileContainer"),
                r.getString("brand"),
                r.getString("surface"),
                r.getString("equalisation"),
                r.getString("encoding"),
                r.getString("codec"),
                r.getInt("fileSize"),
                r.getString("reelSize"),
                r.getString("carrierCapacity"),
                r.getString("bitDepth"),
                r.getInt("blobId"),
                r.getString("checksumType"),
                r.getString("samplingRate"),
                r.getString("compression"),
                r.getString("fileFormat"));
    }
}
