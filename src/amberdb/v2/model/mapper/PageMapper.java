package amberdb.v2.model.mapper;

import amberdb.v2.model.Page;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PageMapper implements ResultSetMapper<Page> {
    @Override
    public Page map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Page(r.getInt("id"), r.getInt("txn_start"), r.getInt("txn_end"), r.getDate("dcmDateTimeUpdated"), r.getString("extent"), r.getString("notes"),
                r.getString("localSystemNumber"), r.getString("occupation"), r.getString("encodingLevel"), r.getBoolean("materialFromMultipleSources"),
                r.getBoolean("displayTitlePage"), r.getDate("endDate"), r.getString("subject"), r.getBoolean("sendToIlms"), r.getString("vendorId"),
                r.getBoolean("allowOnsiteAccess"), r.getString("language"), r.getString("sensitiveMaterial"), r.getString("repository"),
                r.getString("holdingId"), r.getString("dcmAltPi"), r.getString("west"), r.getBoolean("workCreatedDuringMigration"),
                r.getDate("dcmDateTimeCreated"), r.getString("commentsExternal"), r.getString("firstPart"), r.getString("findingAidNote"),
                r.getString("collection"), r.getString("dcmWorkPid"), r.getString("otherTitle"), r.getString("classification"), r.getString("localSystemno"),
                r.getString("commentsInternal"), r.getString("acquisitionStatus"), r.getString("immutable"), r.getString("restrictionType"),
                r.getString("copyrightPolicy"), r.getDate("ilmsSentDateTime"), r.getString("publisher"), r.getString("nextStep"), r.getString("subType"),
                r.getString("scaleEtc"), r.getDate("startDate"), r.getString("tempHolding"), r.getString("dcmRecordUpdater"), r.getString("tilePosition"),
                r.getString("sortIndex"), r.getBoolean("allowHighResdownload"), r.getString("south"), r.getString("restrictionsOnAccess"),
                r.getBoolean("isMissingPage"), r.getString("north"), r.getString("standardId"), r.getString("representativeId"), r.getString("scopeContent"),
                r.getString("accessConditions"), r.getString("edition"), r.getString("alternativeTitle"), r.getString("title"), r.getString("acquisitionCategory"),
                r.getString("internalAccessConditions"), r.getString("eadUpdateReviewRequired"), r.getString("subUnitNo"), r.getDate("expiryDate"),
                r.getBoolean("australianContent"), r.getDate("digitalStatusDate"), r.getString("east"), r.getString("contributor"),
                r.getBoolean("moreIlmsDetailsRequired"), r.getString("subUnitType"), r.getString("uniformTitle"), r.getString("rights"), r.getString("alias"),
                r.getString("rdsAcknowledgementType"), r.getDate("issueDate"), r.getString("recordSource"), r.getString("bibId"), r.getString("coverage"),
                r.getString("summary"), r.getString("creator"), r.getString("sensitiveReason"), r.getString("coordinates"), r.getString("creatorStatement"),
                r.getBoolean("interactiveIndexAvailable"), r.getString("bibLevel"), r.getString("carrier"), r.getString("holdingNumber"), r.getString("form"),
                r.getString("series"), r.getString("rdsAcknowledgementReceiver"), r.getString("constraint1"), r.getString("digitalStatus"),
                r.getString("dcmRecordCreator"), r.getString("depositType"), r.getString("parentConstraint"));
    }
}
