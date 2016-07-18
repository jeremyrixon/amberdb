package amberdb.v2.model.mapper;

import amberdb.v2.model.ImageFile;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageFileMapper implements ResultSetMapper<ImageFile> {
    @Override
    public ImageFile map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new ImageFile(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getString("extent"),
                r.getString("fileName"),
                r.getString("localSystemNumber"),
                r.getString("software"),
                r.getString("encodingLevel"),
                r.getString("language"),
                r.getString("mimeType"),
                r.getString("resolution"),
                r.getString("manufacturerSerialNumber"),
                r.getString("holdingId"),
                r.getString("resolutionUnit"),
                r.getInt("imageWidth"),
                r.getString("manufacturerMake"),
                r.getString("manufacturerModelName"),
                r.getString("encoding"),
                r.getString("deviceSerialNumber"),
                r.getInt("fileSize"),
                r.getString("bitDepth"),
                r.getString("publisher"),
                r.getString("compression"),
                r.getString("device"),
                r.getInt("imageLength"),
                r.getString("colourSpace"),
                r.getString("standardId"),
                r.getString("title"),
                r.getBoolean("australianContent"),
                r.getString("contributor"),
                r.getString("checksum"),
                r.getString("recordSource"),
                r.getString("bibId"),
                r.getString("coverage"),
                r.getString("orientation"),
                r.getString("creator"),
                r.getString("colourProfile"),
                r.getDate("checksumGenerationDate"),
                r.getString("applicationDateCreated"),
                r.getString("coordinates"),
                r.getString("creatorStatement"),
                r.getString("fileFormatVersion"),
                r.getString("dateDigitised"),
                r.getString("holdingNumber"),
                r.getString("application"),
                r.getString("series"),
                r.getInt("blobId"),
                r.getString("softwareSerialNumber"),
                r.getString("checksumType"),
                r.getString("location"),
                r.getString("fileFormat"));
    }
}
