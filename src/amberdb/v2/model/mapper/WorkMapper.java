package amberdb.v2.model.mapper;

import amberdb.v2.model.Work;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class WorkMapper implements ResultSetMapper<Work> {
    @Override
    public Work map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Work(r.getInt("id"), r.getInt("txn_start"), r.getInt("txn_end"), r.getDate("dcmDateTimeCreated"), r.getDate("dcmDateTimeUpdated"),
                r.getString("dcmRecordUpdater"), r.getString("dcmWorkPid"), r.getString("dcmAltPi"), 
                r.getString("dcmRecordCreator"), r.getBoolean("displayTitlePage"), r.getString("title"),
                r.getString("addtionalTitle"),  r.getString("otherTitle"), r.getString("uniformTitle"), 
                r.getString("alias"), r.getString("author"), r.getString("publisher"), r.getString("contributor"), 
                r.getString("additionalContributor"), r.getString("localSystemNumber"), r.getString("localSystemNo"), 
                r.getString("classification"), r.getString("collection"), r.getString("extent"), r.getString("subType"), 
                r.getString("subUnitNo"), r.getString("subUnitType"), r.getString("occupation"), r.getDate("startDate"),
                r.getDate("endDate"), r.getDate("expiryDate"), r.getDate("digitalStatusDate"),
                r.getString("acquisitionStatus"), r.getString("acquisitionCategory"), r.getString("publicationCategory"),
                r.getString("reorderType"), r.getString("immutable"), r.getString("copyrightPolicy"), r.getString("hasRepresentation"), r.getString("totalDuration"), r.getString("scaleEtc"), r.getString("firstPart"),
                r.getString("commentsInternal"), r.getString("commentsExternal"), r.getString("restrictionType"), r.getDate("ilmsSentDateTime"),
                r.getBoolean("moreIlmsDetailsRequired"), r.getBoolean("sendToIlms"), r.getDate("sendToIlmsDateTime"), r.getString("tilePosition"),
                r.getBoolean("allowHighResdownload"), r.getString("accessConditions"), r.getString("internalAccessConditions"),
                r.getString("EADUpdateReviewRequired"), r.getBoolean("australianContent"), r.getString("rights"), r.getString("genre"),
                r.getString("deliveryUrl"), r.getString("imageServerUrl"), r.getString("recordSource"), r.getString("sheetCreationDate"),
                r.getString("sheetName"), r.getString("creator"), r.getString("creatorStatement"), r.getString("additionalCreator"), r.getString("folderType"),
                r.getString("folderNumber"), r.getString("eventNote"), r.getString("notes"), r.getString("publicNotes"), r.getString("findingAidNote"),
                r.getBoolean("interactiveIndexAvailable"), r.getString("startChild"), r.getString("bibLevel"), r.getString("standardId"),
                r.getString("representativeId"), r.getString("holdingId"), r.getString("holdingNumber"), r.getString("tempHolding"), r.getString("sortIndex"),
                r.getBoolean("isMissingPage"), r.getString("series"), r.getString("edition"), r.getString("reorder"), r.getString("additionalSeries"),
                r.getString("constraint1"), r.getString("parentConstraint"), r.getString("catalogueUrl"), r.getString("encodingLevel"),
                r.getBoolean("materialFromMultipleSources"), r.getString("subject"), r.getString("vendorId"), r.getBoolean("allowOnsiteAccess"),
                r.getString("language"), r.getString("sensitiveMaterial"), r.getString("html"), r.getString("preservicaId"), r.getString("redocworksReason"),
                r.getBoolean("workCreatedDuringMigration"), r.getString("nextStep"), r.getInt("ingestJobId"), r.getString("rdsAcknowledgementType"),
                r.getString("rdsAcknowledgementReceiver"), r.getDate("issueDate"), r.getString("bibId"), r.getString("coverage"), r.getString("summary"),
                r.getString("sensitiveReason"), r.getString("carrier"), r.getString("form"), r.getString("digitalStatus"), r.getString("sprightlyUrl"),
                r.getString("depositType"), r.getString("coordinates"), r.getString("north"), r.getString("south"), r.getString("east"), r.getString("west"));
    }
}
