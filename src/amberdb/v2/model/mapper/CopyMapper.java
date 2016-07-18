package amberdb.v2.model.mapper;

import amberdb.v2.model.Copy;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CopyMapper implements ResultSetMapper<Copy> {
    @Override
    public Copy map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Copy(r.getInt("id"),
                r.getInt("txn_start"),
                r.getInt("txn_end"),
                r.getDate("dcmDateTimeUpdated"),
                r.getString("extent"),
                r.getString("dcmRecordUpdater"),
                r.getString("localSystemNumber"),
                r.getString("encodingLevel"),
                r.getString("standardId"),
                r.getString("language"),
                r.getString("title"),
                r.getString("holdingId"),
                r.getString("internalAccessConditions"),
                r.getBoolean("australianContent"),
                r.getDate("dateCreated"),
                r.getString("contributor"),
                r.getString("timedStatus"),
                r.getString("copyType"),
                r.getString("alias"),
                r.getString("copyStatus"),
                r.getString("copyRole"),
                r.getString("manipulation"),
                r.getString("recordSource"),
                r.getString("algorithm"),
                r.getString("bibId"),
                r.getString("creator"),
                r.getString("otherNumbers"),
                r.getDate("dcmDateTimeCreated"),
                r.getString("materialType"),
                r.getString("commentsExternal"),
                r.getString("coordinates"),
                r.getString("creatorStatement"),
                r.getString("classification"),
                r.getString("currentVersion"),
                r.getString("commentsInternal"),
                r.getString("bestCopy"),
                r.getString("carrier"),
                r.getString("holdingNumber"),
                r.getString("series"),
                r.getString("publisher"),
                r.getString("dcmRecordCreator"),
                r.getString("dcmCopyPid"));
    }
}
