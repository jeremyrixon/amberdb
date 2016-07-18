package amberdb.v2.model.mapper;

import amberdb.v2.model.File;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FileMapper implements ResultSetMapper<File> {
    @Override
    public File map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new File(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("extent"),
                r.getString("fileName"),
                r.getString("localSystemNumber"),
                r.getString("software"),
                r.getString("encodingLevel"),
                r.getString("standardId"),
                r.getString("language"),
                r.getString("mimeType"),
                r.getString("title"),
                r.getString("holdingId"),
                r.getBoolean("australianContent"),
                r.getString("contributor"),
                r.getString("checksum"),
                r.getString("recordSource"),
                r.getString("coverage"),
                r.getString("bibId"),
                r.getString("creator"),
                r.getDate("checksumGenerationDate"),
                r.getString("coordinates"),
                r.getString("encoding"),
                r.getString("holdingNumber"),
                r.getInt("fileSize"),
                r.getInt("blobId"),
                r.getString("checksumType"),
                r.getString("publisher"),
                r.getString("compression"),
                r.getString("device"),
                r.getString("fileFormat"));
    }
}
