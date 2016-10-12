package amberdb.graph.dao;


import static amberdb.graph.State.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.PreparedBatchPart;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.graph.AmberEdge;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberTransaction;
import amberdb.graph.AmberVertex;
import amberdb.graph.BaseElement;
import amberdb.graph.PropertyMapper;
import amberdb.graph.State;
import amberdb.graph.TransactionMapper;


public abstract class AmberDao implements Transactional<AmberDao>, GetHandle {
	
    protected static final Map<String, String> fieldMapping        = new HashMap<>();
    public static final Map<String, String> fieldMappingReverse = new HashMap<>();
    static {
    	fieldMapping.put("constraint", "availabilityConstraint");
    	fieldMapping.put("condition",  "copyCondition");
    	
    	for (Entry<String, String> entry: fieldMapping.entrySet()) {
    		fieldMappingReverse.put(entry.getValue(), entry.getKey());
    	}
    }
    public static final Set<String> nodeFields = new TreeSet<>();
    static {
        nodeFields.add("type");
        nodeFields.add("accessConditions");
        nodeFields.add("alias");
        nodeFields.add("commentsExternal");
        nodeFields.add("commentsInternal");
        nodeFields.add("expiryDate");
        nodeFields.add("internalAccessConditions");
        nodeFields.add("localSystemNumber");
        nodeFields.add("name");
        nodeFields.add("notes");
        nodeFields.add("recordSource");
        nodeFields.add("restrictionType");
    }

    
    /*
     * DB creation operations (DDL)
     */

    /*
     * Main tables
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS vertex ("
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end    BIGINT DEFAULT 0 NOT NULL)")
    public abstract void createVertexTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge ("
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, "
            + "v_out      BIGINT, "
            + "v_in       BIGINT, "
            + "label      VARCHAR(100), "
            + "edge_order BIGINT)")
    public abstract void createEdgeTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property ("
            + "id        BIGINT, "
            + "txn_start BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end   BIGINT DEFAULT 0 NOT NULL, "
            + "name      VARCHAR(100), "
            + "type      CHAR(3), "
            + "value     BLOB)")
    public abstract void createPropertyTable();


    /*
     * Session tables
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_vertex ("
            + "s_id       BIGINT, "
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, "
            + "state      CHAR(3))")
    public abstract void createSessionVertexTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_edge ("
            + "s_id       BIGINT, "
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, "
            + "v_out      BIGINT, "
            + "v_in       BIGINT, "
            + "label      VARCHAR(100), "
            + "edge_order BIGINT, "
            + "state      CHAR(3))")
    public abstract void createSessionEdgeTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_property ("
            + "s_id      BIGINT, "
            + "id        BIGINT, "
            + "name      VARCHAR(100), "
            + "type      CHAR(3), "
            + "value     BLOB)")
    public abstract void createSessionPropertyTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS id_generator ("
            + "id BIGINT PRIMARY KEY AUTO_INCREMENT)")
    public abstract void createIdGeneratorTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS transaction ("
            + "id        BIGINT UNIQUE, "
            + "time      BIGINT, "
            + "user      VARCHAR(100), "
            + "operation TEXT)")
    public abstract void createTransactionTable();


    /*
     * Main table indexes - these require review as they might need indexes.
     */
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_vert "
            + "ON vertex(id, txn_start)")
    public abstract void createVertexIndex();


    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_edge "
            + "ON edge(id, txn_start)")
    public abstract void createEdgeIndex();


    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_prop "
            + "ON property(id, txn_start, name)")
    public abstract void createPropertyIndex();


    @SqlUpdate(
            "CREATE INDEX vert_txn_end_idx "
            + "ON vertex(txn_end)")
    public abstract void createVertexTxnEndIndex();


    @SqlUpdate(
            "CREATE INDEX edge_txn_end_idx "
            + "ON edge(txn_end)")
    public abstract void createEdgeTxnEndIndex();


    @SqlUpdate(
            "CREATE INDEX prop_name_idx "
            + "ON property(name)")
    public abstract  void createPropertyNameIndex();


    @SqlUpdate(
            "CREATE INDEX property_value_idx "
            + "ON property(value(512))")
    public abstract void createPropertyValueIndex();


    @SqlUpdate(
            "CREATE INDEX property_name_val_idx "
            + "ON property(name, value(512))")
    public abstract void createPropertyNameValueIndex();


    @SqlUpdate(
            "CREATE INDEX prop_txn_end_idx "
            + "ON property(txn_end)")
    public abstract void createPropertyTxnEndIndex();


    @SqlUpdate(
            "CREATE INDEX edge_label_idx "
            + "ON edge(label)")
    public abstract void createEdgeLabelIndex();


    @SqlUpdate(
            "CREATE INDEX edge_in_idx "
            + "ON edge(v_in)")
    public abstract void createEdgeInVertexIndex();


    @SqlUpdate(
            "CREATE INDEX edge_out_idx "
            + "ON edge(v_out)")
    public abstract void createEdgeOutVertexIndex();

	/*
	 * Put all the columns we need for traversal into one index to save index
	 * merging which is costing us 100ms+ in AmberQuery when a vertex has
	 * a lot of outgoing edges.
	 */
	@SqlUpdate(
			"CREATE INDEX edge_in_traversal_idx "
			+ "ON edge(txn_end, v_in, label, edge_order, v_out)")
	public abstract void createEdgeInTraversalIndex();


	@SqlUpdate(
			"CREATE INDEX edge_out_traversal_idx "
			+ "ON edge(txn_end, v_out, label, edge_order, v_in)")
	public abstract void createEdgeOutTraversalIndex();


    @SqlUpdate(
            "CREATE INDEX sess_edge_idx "
            + "ON sess_edge(s_id)")
    public abstract void createSessionEdgeIndex();


    @SqlUpdate(
            "CREATE INDEX sess_vertex_idx "
            + "ON sess_vertex(s_id)")
    public abstract void createSessionVertexIndex();


    @SqlUpdate(
            "CREATE INDEX sess_property_idx "
            + "ON sess_property(s_id)")
    public abstract void createSessionPropertyIndex();


    @SqlUpdate(
            "CREATE INDEX sess_edge_sis_idx "
            + "ON sess_edge(s_id, id, state)")
    public abstract void createSessionEdgeIdStateIndex();


    @SqlUpdate(
            "CREATE INDEX sess_vertex_sis_idx "
            + "ON sess_vertex(s_id, id, state)")
    public abstract void createSessionVertexIdStateIndex();


    @SqlUpdate(
            "CREATE INDEX sess_property_sis_idx "
            + "ON sess_property(s_id, id)")
    public abstract void createSessionPropertyIdStateIndex();

    @SqlUpdate(
    		 "DROP TABLE IF EXISTS node;"
			+"CREATE TABLE IF NOT EXISTS node ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" accessConditions VARCHAR(63),"
			+" alias VARCHAR(255),"
			+" commentsExternal TEXT,"
			+" commentsInternal TEXT,"
			+" expiryDate DATETIME,"
			+" internalAccessConditions TEXT,"
			+" localSystemNumber VARCHAR(63),"
            +" name VARCHAR(255),"
			+" notes VARCHAR(255),"
			+" recordSource VARCHAR(63),"
			+" restrictionType VARCHAR(255));"
			+"CREATE INDEX node_id ON node (id);"
			+"CREATE INDEX node_txn_id ON node (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS node_history;"
			+"CREATE TABLE IF NOT EXISTS node_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" accessConditions VARCHAR(63),"
			+" alias VARCHAR(255),"
			+" commentsExternal TEXT,"
			+" commentsInternal TEXT,"
			+" expiryDate DATETIME,"
			+" internalAccessConditions TEXT,"
			+" localSystemNumber VARCHAR(63),"
            +" name VARCHAR(255),"
			+" notes VARCHAR(255),"
			+" recordSource VARCHAR(63),"
			+" restrictionType VARCHAR(255));"
			+"CREATE INDEX node_history_id ON node_history (id);"
			+"CREATE INDEX node_history_txn_id ON node_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_node;"
			+"CREATE TABLE IF NOT EXISTS sess_node ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" accessConditions VARCHAR(63),"
			+" alias VARCHAR(255),"
			+" commentsExternal TEXT,"
			+" commentsInternal TEXT,"
			+" expiryDate DATETIME,"
			+" internalAccessConditions TEXT,"
			+" localSystemNumber VARCHAR(63),"
            +" name VARCHAR(255),"
			+" notes VARCHAR(255),"
			+" recordSource VARCHAR(63),"
			+" restrictionType VARCHAR(255));"
			+"CREATE INDEX sess_node_id ON sess_node (id);"
			+"CREATE INDEX sess_node_txn_id ON sess_node (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS work;"
			+"CREATE TABLE IF NOT EXISTS work ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" abstract TEXT,"
			+" access TEXT,"
			+" accessConditions VARCHAR(63),"
			+" acquisitionCategory VARCHAR(63),"
			+" acquisitionStatus VARCHAR(63),"
			+" additionalContributor TEXT,"
			+" additionalCreator VARCHAR(255),"
			+" additionalSeries VARCHAR(255),"
			+" additionalSeriesStatement VARCHAR(255),"
			+" additionalTitle TEXT,"
			+" addressee VARCHAR(255),"
			+" adminInfo VARCHAR(255),"
			+" advertising BOOLEAN,"
			+" algorithm VARCHAR(63),"
			+" alias VARCHAR(255),"
			+" allowHighResdownload BOOLEAN,"
			+" allowOnsiteAccess BOOLEAN,"
			+" alternativeTitle VARCHAR(63),"
			+" altform VARCHAR(255),"
			+" arrangement TEXT,"
			+" australianContent BOOLEAN,"
			+" bestCopy VARCHAR(1),"
			+" bibId VARCHAR(63),"
			+" bibLevel VARCHAR(63),"
			+" bibliography TEXT,"
			+" captions TEXT,"
			+" carrier VARCHAR(63),"
			+" category VARCHAR(255),"
			+" childRange VARCHAR(255),"
			+" classification VARCHAR(255),"
			+" collection VARCHAR(63),"
			+" collectionNumber VARCHAR(63),"
			+" commentsExternal TEXT,"
			+" commentsInternal TEXT,"
			+" commercialStatus VARCHAR(63),"
			+" copyCondition TEXT,"
			+" availabilityConstraint TEXT,"
			+" contributor TEXT,"
			+" coordinates VARCHAR(255),"
			+" copyingPublishing TEXT,"
			+" copyrightPolicy VARCHAR(63),"
			+" copyRole VARCHAR(63),"
			+" copyStatus VARCHAR(63),"
			+" copyType VARCHAR(63),"
			+" correspondenceHeader VARCHAR(255),"
			+" correspondenceId VARCHAR(255),"
			+" correspondenceIndex VARCHAR(255),"
			+" coverage VARCHAR(255),"
			+" creator TEXT,"
			+" creatorStatement TEXT,"
			+" currentVersion VARCHAR(63),"
			+" dateCreated DATETIME,"
			+" dateRangeInAS VARCHAR(63),"
			+" dcmAltPi VARCHAR(63),"
			+" dcmCopyPid VARCHAR(63),"
			+" dcmDateTimeCreated DATETIME,"
			+" dcmDateTimeUpdated DATETIME,"
			+" dcmRecordCreator VARCHAR(63),"
			+" dcmRecordUpdater VARCHAR(63),"
			+" dcmSourceCopy VARCHAR(255),"
			+" dcmWorkPid VARCHAR(63),"
			+" depositType VARCHAR(63),"
			+" digitalStatus VARCHAR(63),"
			+" digitalStatusDate DATETIME,"
			+" displayTitlePage BOOLEAN,"
			+" eadUpdateReviewRequired VARCHAR(1),"
			+" edition VARCHAR(255),"
			+" encodingLevel VARCHAR(63),"
			+" endChild VARCHAR(255),"
			+" endDate DATETIME,"
			+" eventNote VARCHAR(255),"
			+" exhibition VARCHAR(255),"
			+" expiryDate DATETIME,"
			+" extent TEXT,"
			+" findingAidNote TEXT,"
			+" firstPart VARCHAR(63),"
			+" folder TEXT,"
			+" folderNumber VARCHAR(63),"
			+" folderType VARCHAR(63),"
			+" form VARCHAR(63),"
			+" genre VARCHAR(63),"
			+" heading VARCHAR(255),"
			+" holdingId VARCHAR(63),"
			+" holdingNumber TEXT,"
			+" html TEXT,"
			+" illustrated BOOLEAN,"
			+" ilmsSentDateTime DATETIME,"
			+" immutable VARCHAR(63),"
			+" ingestJobId BIGINT,"
			+" interactiveIndexAvailable BOOLEAN,"
			+" internalAccessConditions TEXT,"
			+" isMissingPage BOOLEAN,"
			+" issn VARCHAR(255),"
			+" issueDate DATETIME,"
			+" language VARCHAR(63),"
            +" localSystemNumber VARCHAR(63),"
			+" manipulation VARCHAR(63),"
			+" materialFromMultipleSources BOOLEAN,"
			+" materialType VARCHAR(63),"
			+" metsId VARCHAR(63),"
			+" moreIlmsDetailsRequired BOOLEAN,"
			+" notes VARCHAR(255),"
			+" occupation VARCHAR(255),"
			+" otherNumbers VARCHAR(63),"
			+" otherTitle TEXT,"
			+" preferredCitation TEXT,"
            +" parentConstraint VARCHAR(63),"
			+" preservicaId VARCHAR(63),"
			+" preservicaType VARCHAR(63),"
			+" printedPageNumber VARCHAR(63),"
			+" provenance TEXT,"
			+" publicationCategory VARCHAR(63),"
			+" publicationLevel VARCHAR(255),"
			+" publicNotes VARCHAR(63),"
			+" publisher TEXT,"
			+" rdsAcknowledgementReceiver VARCHAR(255),"
			+" rdsAcknowledgementType VARCHAR(63),"
			+" recordSource VARCHAR(63),"
			+" relatedMaterial TEXT,"
			+" repository VARCHAR(255),"
            +" representativeId VARCHAR(63),"
			+" restrictionsOnAccess TEXT,"
			+" restrictionType VARCHAR(255),"
			+" rights TEXT,"
			+" scaleEtc TEXT,"
			+" scopeContent TEXT,"
			+" segmentIndicator VARCHAR(255),"
			+" sendToIlms BOOLEAN,"
            +" sendToIlmsDateTime VARCHAR(63),"
			+" sensitiveMaterial VARCHAR(63),"
			+" sensitiveReason VARCHAR(63),"
			+" series TEXT,"
			+" sheetCreationDate VARCHAR(255),"
			+" sheetName VARCHAR(63),"
			+" standardId TEXT,"
			+" startChild VARCHAR(63),"
			+" startDate DATETIME,"
			+" subHeadings VARCHAR(255),"
			+" subject TEXT,"
			+" subType VARCHAR(63),"
			+" subUnitNo TEXT,"
			+" subUnitType VARCHAR(63),"
			+" summary TEXT,"
			+" tempHolding VARCHAR(63),"
			+" tilePosition TEXT,"
			+" timedStatus VARCHAR(63),"
			+" title TEXT,"
			+" totalDuration VARCHAR(63),"
			+" uniformTitle VARCHAR(255),"
			+" vendorId VARCHAR(63),"
			+" versionNumber VARCHAR(1),"
			+" workCreatedDuringMigration BOOLEAN,"
			+" workPid VARCHAR(255));"
			+"CREATE INDEX work_id ON work (id);"
			+"CREATE INDEX work_txn_id ON work (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS work_history;"
			+"CREATE TABLE IF NOT EXISTS work_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" abstract TEXT,"
			+" access TEXT,"
			+" accessConditions VARCHAR(63),"
			+" acquisitionCategory VARCHAR(63),"
			+" acquisitionStatus VARCHAR(63),"
			+" additionalContributor TEXT,"
			+" additionalCreator VARCHAR(255),"
			+" additionalSeries VARCHAR(255),"
			+" additionalSeriesStatement VARCHAR(255),"
			+" additionalTitle TEXT,"
			+" addressee VARCHAR(255),"
			+" adminInfo VARCHAR(255),"
			+" advertising BOOLEAN,"
			+" algorithm VARCHAR(63),"
			+" alias VARCHAR(255),"
			+" allowHighResdownload BOOLEAN,"
			+" allowOnsiteAccess BOOLEAN,"
			+" alternativeTitle VARCHAR(63),"
			+" altform VARCHAR(255),"
			+" arrangement TEXT,"
			+" australianContent BOOLEAN,"
			+" bestCopy VARCHAR(1),"
			+" bibId VARCHAR(63),"
			+" bibLevel VARCHAR(63),"
			+" bibliography TEXT,"
			+" captions TEXT,"
			+" carrier VARCHAR(63),"
			+" category VARCHAR(255),"
			+" childRange VARCHAR(255),"
			+" classification VARCHAR(255),"
			+" collection VARCHAR(63),"
			+" collectionNumber VARCHAR(63),"
			+" commentsExternal TEXT,"
			+" commentsInternal TEXT,"
			+" commercialStatus VARCHAR(63),"
			+" copyCondition TEXT,"
			+" availabilityConstraint TEXT,"
			+" contributor TEXT,"
			+" coordinates VARCHAR(255),"
			+" copyingPublishing TEXT,"
			+" copyrightPolicy VARCHAR(63),"
			+" copyRole VARCHAR(63),"
			+" copyStatus VARCHAR(63),"
			+" copyType VARCHAR(63),"
			+" correspondenceHeader VARCHAR(255),"
			+" correspondenceId VARCHAR(255),"
			+" correspondenceIndex VARCHAR(255),"
			+" coverage VARCHAR(255),"
			+" creator TEXT,"
			+" creatorStatement TEXT,"
			+" currentVersion VARCHAR(63),"
			+" dateCreated DATETIME,"
			+" dateRangeInAS VARCHAR(63),"
			+" dcmAltPi VARCHAR(63),"
			+" dcmCopyPid VARCHAR(63),"
			+" dcmDateTimeCreated DATETIME,"
			+" dcmDateTimeUpdated DATETIME,"
			+" dcmRecordCreator VARCHAR(63),"
			+" dcmRecordUpdater VARCHAR(63),"
			+" dcmSourceCopy VARCHAR(255),"
			+" dcmWorkPid VARCHAR(63),"
			+" depositType VARCHAR(63),"
			+" digitalStatus VARCHAR(63),"
			+" digitalStatusDate DATETIME,"
			+" displayTitlePage BOOLEAN,"
			+" eadUpdateReviewRequired VARCHAR(1),"
			+" edition VARCHAR(255),"
			+" encodingLevel VARCHAR(63),"
			+" endChild VARCHAR(255),"
			+" endDate DATETIME,"
			+" eventNote VARCHAR(255),"
			+" exhibition VARCHAR(255),"
			+" expiryDate DATETIME,"
			+" extent TEXT,"
			+" findingAidNote TEXT,"
			+" firstPart VARCHAR(63),"
			+" folder TEXT,"
			+" folderNumber VARCHAR(63),"
			+" folderType VARCHAR(63),"
			+" form VARCHAR(63),"
			+" genre VARCHAR(63),"
			+" heading VARCHAR(255),"
			+" holdingId VARCHAR(63),"
			+" holdingNumber TEXT,"
			+" html TEXT,"
			+" illustrated BOOLEAN,"
			+" ilmsSentDateTime DATETIME,"
			+" immutable VARCHAR(63),"
			+" ingestJobId BIGINT,"
			+" interactiveIndexAvailable BOOLEAN,"
			+" internalAccessConditions TEXT,"
			+" isMissingPage BOOLEAN,"
			+" issn VARCHAR(255),"
			+" issueDate DATETIME,"
			+" language VARCHAR(63),"
            +" localSystemNumber VARCHAR(63),"
			+" manipulation VARCHAR(63),"
			+" materialFromMultipleSources BOOLEAN,"
			+" materialType VARCHAR(63),"
			+" metsId VARCHAR(63),"
			+" moreIlmsDetailsRequired BOOLEAN,"
			+" notes VARCHAR(255),"
			+" occupation VARCHAR(255),"
			+" otherNumbers VARCHAR(63),"
			+" otherTitle TEXT,"
            +" parentConstraint VARCHAR(63),"
			+" preferredCitation TEXT,"
			+" preservicaId VARCHAR(63),"
			+" preservicaType VARCHAR(63),"
			+" printedPageNumber VARCHAR(63),"
			+" provenance TEXT,"
			+" publicationCategory VARCHAR(63),"
			+" publicationLevel VARCHAR(255),"
			+" publicNotes VARCHAR(63),"
			+" publisher TEXT,"
			+" rdsAcknowledgementReceiver VARCHAR(255),"
			+" rdsAcknowledgementType VARCHAR(63),"
			+" recordSource VARCHAR(63),"
			+" relatedMaterial TEXT,"
			+" repository VARCHAR(255),"
            +" representativeId VARCHAR(63),"
			+" restrictionsOnAccess TEXT,"
			+" restrictionType VARCHAR(255),"
			+" rights TEXT,"
			+" scaleEtc TEXT,"
			+" scopeContent TEXT,"
			+" segmentIndicator VARCHAR(255),"
			+" sendToIlms BOOLEAN,"
            +" sendToIlmsDateTime VARCHAR(63),"
			+" sensitiveMaterial VARCHAR(63),"
			+" sensitiveReason VARCHAR(63),"
			+" series TEXT,"
			+" sheetCreationDate VARCHAR(255),"
			+" sheetName VARCHAR(63),"
			+" standardId TEXT,"
			+" startChild VARCHAR(63),"
			+" startDate DATETIME,"
			+" subHeadings VARCHAR(255),"
			+" subject TEXT,"
			+" subType VARCHAR(63),"
			+" subUnitNo TEXT,"
			+" subUnitType VARCHAR(63),"
			+" summary TEXT,"
			+" tempHolding VARCHAR(63),"
			+" tilePosition TEXT,"
			+" timedStatus VARCHAR(63),"
			+" title TEXT,"
			+" totalDuration VARCHAR(63),"
			+" uniformTitle VARCHAR(255),"
			+" vendorId VARCHAR(63),"
			+" versionNumber VARCHAR(1),"
			+" workCreatedDuringMigration BOOLEAN,"
			+" workPid VARCHAR(255));"
			+"CREATE INDEX work_history_id ON work_history (id);"
			+"CREATE INDEX work_history_txn_id ON work_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_work;"
			+"CREATE TABLE IF NOT EXISTS sess_work ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" abstract TEXT,"
			+" access TEXT,"
			+" accessConditions VARCHAR(63),"
			+" acquisitionCategory VARCHAR(63),"
			+" acquisitionStatus VARCHAR(63),"
			+" additionalContributor TEXT,"
			+" additionalCreator VARCHAR(255),"
			+" additionalSeries VARCHAR(255),"
			+" additionalSeriesStatement VARCHAR(255),"
			+" additionalTitle TEXT,"
			+" addressee VARCHAR(255),"
			+" adminInfo VARCHAR(255),"
			+" advertising BOOLEAN,"
			+" algorithm VARCHAR(63),"
			+" alias VARCHAR(255),"
			+" allowHighResdownload BOOLEAN,"
			+" allowOnsiteAccess BOOLEAN,"
			+" alternativeTitle VARCHAR(63),"
			+" altform VARCHAR(255),"
			+" arrangement TEXT,"
			+" australianContent BOOLEAN,"
			+" bestCopy VARCHAR(1),"
			+" bibId VARCHAR(63),"
			+" bibLevel VARCHAR(63),"
			+" bibliography TEXT,"
			+" captions TEXT,"
			+" carrier VARCHAR(63),"
			+" category VARCHAR(255),"
			+" childRange VARCHAR(255),"
			+" classification VARCHAR(255),"
			+" collection VARCHAR(63),"
			+" collectionNumber VARCHAR(63),"
			+" commentsExternal TEXT,"
			+" commentsInternal TEXT,"
			+" commercialStatus VARCHAR(63),"
			+" copyCondition TEXT,"
			+" availabilityConstraint TEXT,"
			+" contributor TEXT,"
			+" coordinates VARCHAR(255),"
			+" copyingPublishing TEXT,"
			+" copyrightPolicy VARCHAR(63),"
			+" copyRole VARCHAR(63),"
			+" copyStatus VARCHAR(63),"
			+" copyType VARCHAR(63),"
			+" correspondenceHeader VARCHAR(255),"
			+" correspondenceId VARCHAR(255),"
			+" correspondenceIndex VARCHAR(255),"
			+" coverage VARCHAR(255),"
			+" creator TEXT,"
			+" creatorStatement TEXT,"
			+" currentVersion VARCHAR(63),"
			+" dateCreated DATETIME,"
			+" dateRangeInAS VARCHAR(63),"
			+" dcmAltPi VARCHAR(63),"
			+" dcmCopyPid VARCHAR(63),"
			+" dcmDateTimeCreated DATETIME,"
			+" dcmDateTimeUpdated DATETIME,"
			+" dcmRecordCreator VARCHAR(63),"
			+" dcmRecordUpdater VARCHAR(63),"
			+" dcmSourceCopy VARCHAR(255),"
			+" dcmWorkPid VARCHAR(63),"
			+" depositType VARCHAR(63),"
			+" digitalStatus VARCHAR(63),"
			+" digitalStatusDate DATETIME,"
			+" displayTitlePage BOOLEAN,"
			+" eadUpdateReviewRequired VARCHAR(1),"
			+" edition VARCHAR(255),"
			+" encodingLevel VARCHAR(63),"
			+" endChild VARCHAR(255),"
			+" endDate DATETIME,"
			+" eventNote VARCHAR(255),"
			+" exhibition VARCHAR(255),"
			+" expiryDate DATETIME,"
			+" extent TEXT,"
			+" findingAidNote TEXT,"
			+" firstPart VARCHAR(63),"
			+" folder TEXT,"
			+" folderNumber VARCHAR(63),"
			+" folderType VARCHAR(63),"
			+" form VARCHAR(63),"
			+" genre VARCHAR(63),"
			+" heading VARCHAR(255),"
			+" holdingId VARCHAR(63),"
			+" holdingNumber TEXT,"
			+" html TEXT,"
			+" illustrated BOOLEAN,"
			+" ilmsSentDateTime DATETIME,"
			+" immutable VARCHAR(63),"
			+" ingestJobId BIGINT,"
			+" interactiveIndexAvailable BOOLEAN,"
			+" internalAccessConditions TEXT,"
			+" isMissingPage BOOLEAN,"
			+" issn VARCHAR(255),"
			+" issueDate DATETIME,"
			+" language VARCHAR(63),"
            +" localSystemNumber VARCHAR(63),"
			+" manipulation VARCHAR(63),"
			+" materialFromMultipleSources BOOLEAN,"
			+" materialType VARCHAR(63),"
			+" metsId VARCHAR(63),"
			+" moreIlmsDetailsRequired BOOLEAN,"
			+" notes VARCHAR(255),"
			+" occupation VARCHAR(255),"
			+" otherNumbers VARCHAR(63),"
			+" otherTitle TEXT,"
            +" parentConstraint VARCHAR(63),"
			+" preferredCitation TEXT,"
			+" preservicaId VARCHAR(63),"
			+" preservicaType VARCHAR(63),"
			+" printedPageNumber VARCHAR(63),"
			+" provenance TEXT,"
			+" publicationCategory VARCHAR(63),"
			+" publicationLevel VARCHAR(255),"
			+" publicNotes VARCHAR(63),"
			+" publisher TEXT,"
			+" rdsAcknowledgementReceiver VARCHAR(255),"
			+" rdsAcknowledgementType VARCHAR(63),"
			+" recordSource VARCHAR(63),"
			+" relatedMaterial TEXT,"
			+" repository VARCHAR(255),"
            +" representativeId VARCHAR(63),"
			+" restrictionsOnAccess TEXT,"
			+" restrictionType VARCHAR(255),"
			+" rights TEXT,"
			+" scaleEtc TEXT,"
			+" scopeContent TEXT,"
			+" segmentIndicator VARCHAR(255),"
			+" sendToIlms BOOLEAN,"
            +" sendToIlmsDateTime VARCHAR(63),"
			+" sensitiveMaterial VARCHAR(63),"
			+" sensitiveReason VARCHAR(63),"
			+" series TEXT,"
			+" sheetCreationDate VARCHAR(255),"
			+" sheetName VARCHAR(63),"
			+" standardId TEXT,"
			+" startChild VARCHAR(63),"
			+" startDate DATETIME,"
			+" subHeadings VARCHAR(255),"
			+" subject TEXT,"
			+" subType VARCHAR(63),"
			+" subUnitNo TEXT,"
			+" subUnitType VARCHAR(63),"
			+" summary TEXT,"
			+" tempHolding VARCHAR(63),"
			+" tilePosition TEXT,"
			+" timedStatus VARCHAR(63),"
			+" title TEXT,"
			+" totalDuration VARCHAR(63),"
			+" uniformTitle VARCHAR(255),"
			+" vendorId VARCHAR(63),"
			+" versionNumber VARCHAR(1),"
			+" workCreatedDuringMigration BOOLEAN,"
			+" workPid VARCHAR(255));"
			+"CREATE INDEX sess_work_id ON sess_work (id);"
			+"CREATE INDEX sess_work_txn_id ON sess_work (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS file;"
			+"CREATE TABLE IF NOT EXISTS file ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" application TEXT,"
			+" applicationDateCreated VARCHAR(63),"
			+" bitDepth VARCHAR(63),"
			+" bitrate VARCHAR(63),"
			+" blobId BIGINT,"
			+" blockAlign INTEGER,"
			+" brand VARCHAR(63),"
			+" carrierCapacity VARCHAR(63),"
			+" channel VARCHAR(63),"
			+" checksum VARCHAR(63),"
			+" checksumGenerationDate DATETIME,"
			+" checksumType VARCHAR(63),"
			+" codec VARCHAR(63),"
			+" colourProfile VARCHAR(63),"
			+" colourSpace VARCHAR(63),"
			+" compression VARCHAR(63),"
			+" cpLocation VARCHAR(255),"
			+" dateDigitised VARCHAR(63),"
			+" dcmCopyPid VARCHAR(63),"
			+" device VARCHAR(63),"
			+" deviceSerialNumber VARCHAR(63),"
			+" duration VARCHAR(63),"
			+" durationType VARCHAR(63),"
			+" encoding VARCHAR(63),"
			+" equalisation VARCHAR(63),"
			+" fileContainer VARCHAR(63),"
			+" fileFormat VARCHAR(63),"
			+" fileFormatVersion VARCHAR(63),"
			+" fileName VARCHAR(255),"
			+" fileSize BIGINT,"
			+" framerate INTEGER,"
			+" imageLength INTEGER,"
			+" imageWidth INTEGER,"
			+" location VARCHAR(255),"
			+" manufacturerMake VARCHAR(63),"
			+" manufacturerModelName VARCHAR(63),"
			+" manufacturerSerialNumber VARCHAR(63),"
			+" mimeType VARCHAR(255),"
			+" notes VARCHAR(255),"
			+" orientation VARCHAR(63),"
			+" photometric VARCHAR(255),"
			+" reelSize VARCHAR(63),"
			+" resolution VARCHAR(63),"
			+" resolutionUnit VARCHAR(63),"
			+" samplesPerPixel VARCHAR(255),"
			+" samplingRate VARCHAR(63),"
			+" software VARCHAR(63),"
			+" softwareSerialNumber VARCHAR(63),"
			+" soundField VARCHAR(63),"
			+" speed VARCHAR(63),"
			+" surface VARCHAR(63),"
			+" thickness VARCHAR(63),"
			+" toolId VARCHAR(63),"
			+" zoomLevel VARCHAR(255));"
			+"CREATE INDEX file_id ON file (id);"
			+"CREATE INDEX file_txn_id ON file (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS file_history;"
			+"CREATE TABLE IF NOT EXISTS file_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" application TEXT,"
			+" applicationDateCreated VARCHAR(63),"
			+" bitDepth VARCHAR(63),"
			+" bitrate VARCHAR(63),"
			+" blobId BIGINT,"
			+" blockAlign INTEGER,"
			+" brand VARCHAR(63),"
			+" carrierCapacity VARCHAR(63),"
			+" channel VARCHAR(63),"
			+" checksum VARCHAR(63),"
			+" checksumGenerationDate DATETIME,"
			+" checksumType VARCHAR(63),"
			+" codec VARCHAR(63),"
			+" colourProfile VARCHAR(63),"
			+" colourSpace VARCHAR(63),"
			+" compression VARCHAR(63),"
			+" cpLocation VARCHAR(255),"
			+" dateDigitised VARCHAR(63),"
			+" dcmCopyPid VARCHAR(63),"
			+" device VARCHAR(63),"
			+" deviceSerialNumber VARCHAR(63),"
			+" duration VARCHAR(63),"
			+" durationType VARCHAR(63),"
			+" encoding VARCHAR(63),"
			+" equalisation VARCHAR(63),"
			+" fileContainer VARCHAR(63),"
			+" fileFormat VARCHAR(63),"
			+" fileFormatVersion VARCHAR(63),"
			+" fileName VARCHAR(255),"
			+" fileSize BIGINT,"
			+" framerate INTEGER,"
			+" imageLength INTEGER,"
			+" imageWidth INTEGER,"
			+" location VARCHAR(255),"
			+" manufacturerMake VARCHAR(63),"
			+" manufacturerModelName VARCHAR(63),"
			+" manufacturerSerialNumber VARCHAR(63),"
			+" mimeType VARCHAR(255),"
			+" notes VARCHAR(255),"
			+" orientation VARCHAR(63),"
			+" photometric VARCHAR(255),"
			+" reelSize VARCHAR(63),"
			+" resolution VARCHAR(63),"
			+" resolutionUnit VARCHAR(63),"
			+" samplesPerPixel VARCHAR(255),"
			+" samplingRate VARCHAR(63),"
			+" software VARCHAR(63),"
			+" softwareSerialNumber VARCHAR(63),"
			+" soundField VARCHAR(63),"
			+" speed VARCHAR(63),"
			+" surface VARCHAR(63),"
			+" thickness VARCHAR(63),"
			+" toolId VARCHAR(63),"
			+" zoomLevel VARCHAR(255));"
			+"CREATE INDEX file_history_id ON file_history (id);"
			+"CREATE INDEX file_history_txn_id ON file_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_file;"
			+"CREATE TABLE IF NOT EXISTS sess_file ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" application TEXT,"
			+" applicationDateCreated VARCHAR(63),"
			+" bitDepth VARCHAR(63),"
			+" bitrate VARCHAR(63),"
			+" blobId BIGINT,"
			+" blockAlign INTEGER,"
			+" brand VARCHAR(63),"
			+" carrierCapacity VARCHAR(63),"
			+" channel VARCHAR(63),"
			+" checksum VARCHAR(63),"
			+" checksumGenerationDate DATETIME,"
			+" checksumType VARCHAR(63),"
			+" codec VARCHAR(63),"
			+" colourProfile VARCHAR(63),"
			+" colourSpace VARCHAR(63),"
			+" compression VARCHAR(63),"
			+" cpLocation VARCHAR(255),"
			+" dateDigitised VARCHAR(63),"
			+" dcmCopyPid VARCHAR(63),"
			+" device VARCHAR(63),"
			+" deviceSerialNumber VARCHAR(63),"
			+" duration VARCHAR(63),"
			+" durationType VARCHAR(63),"
			+" encoding VARCHAR(63),"
			+" equalisation VARCHAR(63),"
			+" fileContainer VARCHAR(63),"
			+" fileFormat VARCHAR(63),"
			+" fileFormatVersion VARCHAR(63),"
			+" fileName VARCHAR(255),"
			+" fileSize BIGINT,"
			+" framerate INTEGER,"
			+" imageLength INTEGER,"
			+" imageWidth INTEGER,"
			+" location VARCHAR(255),"
			+" manufacturerMake VARCHAR(63),"
			+" manufacturerModelName VARCHAR(63),"
			+" manufacturerSerialNumber VARCHAR(63),"
			+" mimeType VARCHAR(255),"
			+" notes VARCHAR(255),"
			+" orientation VARCHAR(63),"
			+" photometric VARCHAR(255),"
			+" reelSize VARCHAR(63),"
			+" resolution VARCHAR(63),"
			+" resolutionUnit VARCHAR(63),"
			+" samplesPerPixel VARCHAR(255),"
			+" samplingRate VARCHAR(63),"
			+" software VARCHAR(63),"
			+" softwareSerialNumber VARCHAR(63),"
			+" soundField VARCHAR(63),"
			+" speed VARCHAR(63),"
			+" surface VARCHAR(63),"
			+" thickness VARCHAR(63),"
			+" toolId VARCHAR(63),"
			+" zoomLevel VARCHAR(255));"
			+"CREATE INDEX sess_file_id ON sess_file (id);"
			+"CREATE INDEX sess_file_txn_id ON sess_file (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS description;"
			+"CREATE TABLE IF NOT EXISTS description ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" alternativeTitle VARCHAR(63),"
			+" city VARCHAR(63),"
			+" country VARCHAR(255),"
			+" digitalSourceType VARCHAR(255),"
			+" event VARCHAR(255),"
			+" exposureFNumber VARCHAR(63),"
			+" exposureMode VARCHAR(63),"
			+" exposureProgram VARCHAR(63),"
			+" exposureTime VARCHAR(63),"
			+" fileFormat VARCHAR(63),"
			+" fileSource VARCHAR(63),"
			+" focalLength VARCHAR(63),"
			+" gpsVersion VARCHAR(255),"
			+" isoCountryCode VARCHAR(255),"
			+" isoSpeedRating VARCHAR(63),"
			+" latitude VARCHAR(63),"
			+" latitudeRef VARCHAR(255),"
			+" lens VARCHAR(63),"
			+" longitude VARCHAR(63),"
			+" longitudeRef VARCHAR(255),"
			+" mapDatum VARCHAR(63),"
			+" meteringMode VARCHAR(63),"
			+" province VARCHAR(63),"
			+" subLocation VARCHAR(255),"
			+" timestamp DATETIME,"
			+" whiteBalance VARCHAR(63),"
			+" worldRegion VARCHAR(255));"
			+"CREATE INDEX description_id ON description (id);"
			+"CREATE INDEX description_txn_id ON description (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS description_history;"
			+"CREATE TABLE IF NOT EXISTS description_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" alternativeTitle VARCHAR(63),"
			+" city VARCHAR(63),"
			+" country VARCHAR(255),"
			+" digitalSourceType VARCHAR(255),"
			+" event VARCHAR(255),"
			+" exposureFNumber VARCHAR(63),"
			+" exposureMode VARCHAR(63),"
			+" exposureProgram VARCHAR(63),"
			+" exposureTime VARCHAR(63),"
			+" fileFormat VARCHAR(63),"
			+" fileSource VARCHAR(63),"
			+" focalLength VARCHAR(63),"
			+" gpsVersion VARCHAR(255),"
			+" isoCountryCode VARCHAR(255),"
			+" isoSpeedRating VARCHAR(63),"
			+" latitude VARCHAR(63),"
			+" latitudeRef VARCHAR(255),"
			+" lens VARCHAR(63),"
			+" longitude VARCHAR(63),"
			+" longitudeRef VARCHAR(255),"
			+" mapDatum VARCHAR(63),"
			+" meteringMode VARCHAR(63),"
			+" province VARCHAR(63),"
			+" subLocation VARCHAR(255),"
			+" timestamp DATETIME,"
			+" whiteBalance VARCHAR(63),"
			+" worldRegion VARCHAR(255));"
			+"CREATE INDEX description_history_id ON description_history (id);"
			+"CREATE INDEX description_history_txn_id ON description_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_description;"
			+"CREATE TABLE IF NOT EXISTS sess_description ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
			+" alternativeTitle VARCHAR(63),"
			+" city VARCHAR(63),"
			+" country VARCHAR(255),"
			+" digitalSourceType VARCHAR(255),"
			+" event VARCHAR(255),"
			+" exposureFNumber VARCHAR(63),"
			+" exposureMode VARCHAR(63),"
			+" exposureProgram VARCHAR(63),"
			+" exposureTime VARCHAR(63),"
			+" fileFormat VARCHAR(63),"
			+" fileSource VARCHAR(63),"
			+" focalLength VARCHAR(63),"
			+" gpsVersion VARCHAR(255),"
			+" isoCountryCode VARCHAR(255),"
			+" isoSpeedRating VARCHAR(63),"
			+" latitude VARCHAR(63),"
			+" latitudeRef VARCHAR(255),"
			+" lens VARCHAR(63),"
			+" longitude VARCHAR(63),"
			+" longitudeRef VARCHAR(255),"
			+" mapDatum VARCHAR(63),"
			+" meteringMode VARCHAR(63),"
			+" province VARCHAR(63),"
			+" subLocation VARCHAR(255),"
			+" timestamp DATETIME,"
			+" whiteBalance VARCHAR(63),"
			+" worldRegion VARCHAR(255));"
			+"CREATE INDEX sess_description_id ON sess_description (id);"
			+"CREATE INDEX sess_description_txn_id ON sess_description (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS party;"
			+"CREATE TABLE IF NOT EXISTS party ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
            +" name VARCHAR(255),"
			+" orgUrl VARCHAR(255),"
			+" suppressed BOOLEAN,"
			+" logoUrl VARCHAR(255));"
			+"CREATE INDEX party_id ON party (id);"
			+"CREATE INDEX party_txn_id ON party (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS party_history;"
			+"CREATE TABLE IF NOT EXISTS party_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
            +" name VARCHAR(255),"
			+" orgUrl VARCHAR(255),"
			+" suppressed BOOLEAN,"
			+" logoUrl VARCHAR(255));"
			+"CREATE INDEX party_history_id ON party_history (id);"
			+"CREATE INDEX party_history_txn_id ON party_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_party;"
			+"CREATE TABLE IF NOT EXISTS sess_party ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
            +" name VARCHAR(255),"
			+" orgUrl VARCHAR(255),"
			+" suppressed BOOLEAN,"
			+" logoUrl VARCHAR(255));"
			+"CREATE INDEX sess_party_id ON sess_party (id);"
			+"CREATE INDEX sess_party_txn_id ON sess_party (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS tag;"
			+"CREATE TABLE IF NOT EXISTS tag ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
            +" name VARCHAR(255),"
			+" description VARCHAR(255));"
			+"CREATE INDEX tag_id ON tag (id);"
			+"CREATE INDEX tag_txn_id ON tag (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS tag_history;"
			+"CREATE TABLE IF NOT EXISTS tag_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
            +" name VARCHAR(255),"
			+" description VARCHAR(255));"
			+"CREATE INDEX tag_history_id ON tag_history (id);"
			+"CREATE INDEX tag_history_txn_id ON tag_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_tag;"
			+"CREATE TABLE IF NOT EXISTS sess_tag ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
			+" type VARCHAR(15),"
			+""
            +" name VARCHAR(255),"
			+" description VARCHAR(255));"
			+"CREATE INDEX sess_tag_id ON sess_tag (id);"
			+"CREATE INDEX sess_tag_txn_id ON sess_tag (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS flatedge;"
			+"CREATE TABLE IF NOT EXISTS flatedge ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
    		+" label VARCHAR(15), "
			+""
			+" v_out BIGINT,"
			+" v_in BIGINT,"
    		+" edge_order BIGINT); "
			+"CREATE INDEX flatedge_id ON flatedge (id);"
			+"CREATE INDEX flatedge_txn_id ON flatedge (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS flatedge_history;"
			+"CREATE TABLE IF NOT EXISTS flatedge_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
    		+" label VARCHAR(15), "
			+""
			+" v_out BIGINT,"
			+" v_in BIGINT,"
    		+" edge_order BIGINT); "
			+"CREATE INDEX flatedge_history_id ON flatedge_history (id);"
			+"CREATE INDEX flatedge_history_txn_id ON flatedge_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_flatedge;"
			+"CREATE TABLE IF NOT EXISTS sess_flatedge ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
    		+" label VARCHAR(15), "
			+""
			+" v_out BIGINT,"
			+" v_in BIGINT,"
    		+" edge_order BIGINT); "
			+"CREATE INDEX sess_flatedge_id ON sess_flatedge (id);"
			+"CREATE INDEX sess_flatedge_txn_id ON sess_flatedge (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS acknowledge;"
			+"CREATE TABLE IF NOT EXISTS acknowledge ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
    		+" label VARCHAR(15), "
			+""
			+" v_out BIGINT,"
			+" v_in BIGINT,"
			+" edge_order BIGINT,"
			+" ackType TEXT,"
			+" date DATETIME,"
			+" kindOfSupport TEXT,"
			+" weighting DOUBLE,"
			+" urlToOriginal VARCHAR(255));"
			+"CREATE INDEX acknowledge_id ON acknowledge (id);"
			+"CREATE INDEX acknowledge_txn_id ON acknowledge (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS acknowledge_history;"
			+"CREATE TABLE IF NOT EXISTS acknowledge_history ("
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
    		+" label VARCHAR(15), "
			+""
			+" v_out BIGINT,"
			+" v_in BIGINT,"
			+" edge_order BIGINT,"
			+" ackType TEXT,"
			+" date DATETIME,"
			+" kindOfSupport TEXT,"
			+" weighting DOUBLE,"
			+" urlToOriginal VARCHAR(255));"
			+"CREATE INDEX acknowledge_history_id ON acknowledge_history (id);"
			+"CREATE INDEX acknowledge_history_txn_id ON acknowledge_history (id, txn_start, txn_end);"
			+"DROP TABLE IF EXISTS sess_acknowledge;"
			+"CREATE TABLE IF NOT EXISTS sess_acknowledge ("
			+" s_id BIGINT,"
			+" state CHAR(3),"
			+" id BIGINT,"
			+" txn_start BIGINT DEFAULT 0 NOT NULL,"
			+" txn_end BIGINT DEFAULT 0 NOT NULL,"
    		+" label VARCHAR(15), "
			+""
			+" v_out BIGINT,"
			+" v_in BIGINT,"
			+" edge_order BIGINT,"
			+" ackType TEXT,"
			+" date DATETIME,"
			+" kindOfSupport TEXT,"
			+" weighting DOUBLE,"
			+" urlToOriginal VARCHAR(255));"
			+"CREATE INDEX sess_acknowledge_id ON sess_acknowledge (id);"
			+"CREATE INDEX sess_acknowledge_txn_id ON sess_acknowledge (id, txn_start, txn_end);")
    public abstract void createV2Tables();

    @SqlQuery(
            "SELECT (COUNT(table_name) >= 8) "
            + "FROM INFORMATION_SCHEMA.TABLES "
            + "WHERE table_name IN ("
            + "  'vertex', 'edge', 'property', "
            + "  'sess_vertex', 'sess_edge', 'sess_property', "
            + "  'id_generator', 'transaction')")
	public abstract boolean schemaTablesExist();


    /*
     * id generation operations
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO id_generator () "
            + "VALUES ()")
    public abstract long newId();


    @SqlUpdate("DELETE "
            + "FROM id_generator "
            + "WHERE id < :id")
    public abstract void garbageCollectIds(
            @Bind("id") long id);


    /*
     * suspend/resume operations
     */
    @SqlBatch("INSERT INTO sess_edge (s_id, id, txn_start, txn_end, v_out, v_in, label, edge_order, state) "
            + "VALUES (:sessId, :id, :txnStart, :txnEnd, :outId, :inId, :label, :edgeOrder, :state)")
    public abstract void suspendEdges(
            @Bind("sessId")    Long          sessId,
            @Bind("id")        List<Long>    id,
            @Bind("txnStart")  List<Long>    txnStart,
            @Bind("txnEnd")    List<Long>    txnEnd,
            @Bind("outId")     List<Long>    outId,
            @Bind("inId")      List<Long>    inId,
            @Bind("label")     List<String>  label,
            @Bind("edgeOrder") List<Integer> edgeOrder,
            @Bind("state")     List<String>   state);


    @SqlBatch("INSERT INTO sess_vertex (s_id, id, txn_start, txn_end, state) "
            + "VALUES (:sessId, :id, :txnStart, :txnEnd, :state)")
    public abstract void suspendVertices(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("txnStart")  List<Long>   txnStart,
            @Bind("txnEnd")    List<Long>   txnEnd,
            @Bind("state")     List<String>  state);


    @SqlBatch("INSERT INTO sess_property (s_id, id, name, type, value) "
            + "VALUES (:sessId, :id, :name, :type, :value)")
    public abstract  void suspendProperties(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("name")      List<String> name,
            @Bind("type")      List<String> type,
            @Bind("value")     List<byte[]> value);


    @SqlQuery("SELECT id, name, type, value "
            + "FROM sess_property "
            + "WHERE s_id = :sessId")
    @Mapper(PropertyMapper.class)
    public abstract  List<AmberProperty> resumeProperties(@Bind("sessId") Long sessId);


    /* Transaction operations */


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = :id")
    @Mapper(TransactionMapper.class)
    public abstract AmberTransaction getTransaction(@Bind("id") Long id);


    @SqlQuery("(SELECT DISTINCT t.id, t.time, t.user, t.operation "
            + "FROM transaction t, vertex v "
            + "WHERE v.id = :id "
            + "AND t.id = v.txn_start) "
            + "UNION "
            + "(SELECT DISTINCT t.id, t.time, t.user, t.operation "
            + "FROM transaction t, vertex v "
            + "WHERE v.id = :id "
            + "AND t.id = v.txn_end) "
            + "ORDER BY id")
    @Mapper(TransactionMapper.class)
    public abstract List<AmberTransaction> getTransactionsByVertexId(@Bind("id") Long id);


    @SqlQuery("(SELECT DISTINCT t.id, t.time, t.user, t.operation "
            + "FROM transaction t, edge e "
            + "WHERE e.id = :id "
            + "AND t.id = e.txn_start) "
            + "UNION "
            + "(SELECT DISTINCT t.id, t.time, t.user, t.operation "
            + "FROM transaction t, edge e "
            + "WHERE e.id = :id "
            + "AND t.id = e.txn_end) "
            + "ORDER BY id")
    @Mapper(TransactionMapper.class)
    public abstract List<AmberTransaction> getTransactionsByEdgeId(@Bind("id") Long id);


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = ("
    		+ "  SELECT MIN(t.id) "
            + "  FROM transaction t, vertex v "
            + "  WHERE v.id = :id "
            + "  AND t.id = v.txn_start)")
    @Mapper(TransactionMapper.class)
    public abstract AmberTransaction getFirstTransactionForVertexId(@Bind("id") Long id);


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = ("
            + "  SELECT MIN(t.id) "
            + "  FROM transaction t, edge e "
            + "  WHERE e.id = :id "
            + "  AND t.id = e.txn_start)")
    @Mapper(TransactionMapper.class)
    public abstract AmberTransaction getFirstTransactionForEdgeId(@Bind("id") Long id);


    /* Note: resume edge and vertex implemented in AmberGraph */


    /*
     * commit operations
     */
    @SqlUpdate(
            "INSERT INTO transaction (id, time, user, operation)" +
            "VALUES (:id, :time, :user, :operation)")
    public abstract void insertTransaction(
            @Bind("id") long id,
            @Bind("time") long time,
            @Bind("user") String user,
            @Bind("operation") String operation);


    // The following query intentionally left blank. It's implemented in the db specific AmberDao sub classes (h2 or MySql)
    @SqlUpdate("")
    public abstract void endElements(
            @Bind("txnId") Long txnId);


    @SqlUpdate("SET @txn = :txnId; "

            // edges
            + "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
            + "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
            + "FROM sess_edge "
            + "WHERE s_id = @txn "
            + "AND state = 'NEW'; "

            + "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
            + "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
            + "FROM sess_edge "
            + "WHERE s_id = @txn "
            + "AND state = 'MOD'; "

            // vertices
            + "INSERT INTO vertex (id, txn_start, txn_end) "
            + "SELECT id, s_id, 0 "
            + "FROM sess_vertex "
            + "WHERE s_id = @txn "
            + "AND state = 'NEW'; "

            + "INSERT INTO vertex (id, txn_start, txn_end) "
            + "SELECT id, s_id, 0 "
            + "FROM sess_vertex "
            + "WHERE s_id = @txn "
            + "AND state = 'MOD'; "

            // properties
            + "INSERT INTO property (id, txn_start, txn_end, name, type, value) "
            + "SELECT p.id, p.s_id, 0, p.name, p.type, p.value "
            + "FROM sess_property p, sess_vertex v "
            + "WHERE p.s_id = @txn "
            + "AND v.s_id = @txn "
            + "AND v.id = p.id "
            + "AND v.state = 'NEW'; "

            + "INSERT INTO property (id, txn_start, txn_end, name, type, value) "
            + "SELECT p.id, p.s_id, 0, p.name, p.type, p.value "
            + "FROM sess_property p, sess_vertex v "
            + "WHERE p.s_id = @txn "
            + "AND v.s_id = @txn "
            + "AND v.id = p.id "
            + "AND v.state = 'MOD'; "

            + "INSERT INTO property (id, txn_start, txn_end, name, type, value) "
            + "SELECT p.id, p.s_id, 0, p.name, p.type, p.value "
            + "FROM sess_property p, sess_edge e "
            + "WHERE p.s_id = @txn "
            + "AND e.s_id = @txn "
            + "AND e.id = p.id "
            + "AND e.state = 'NEW'; "

            + "INSERT INTO property (id, txn_start, txn_end, name, type, value) "
            + "SELECT p.id, p.s_id, 0, p.name, p.type, p.value "
            + "FROM sess_property p, sess_edge e "
            + "WHERE p.s_id = @txn "
            + "AND e.s_id = @txn "
            + "AND e.id = p.id "
            + "AND e.state = 'MOD'; ")
    public abstract void startElements(
            @Bind("txnId") Long txnId);


    public abstract void close();


    @SqlUpdate("SET @sessId = :sessId; " +

            "DELETE FROM sess_vertex " +
            "WHERE s_id = @sessId; " +

            "DELETE FROM sess_edge " +
            "WHERE s_id = @sessId; " +

            "DELETE FROM sess_property " +
            "WHERE s_id = @sessId; ")
    public abstract void clearSession(
            @Bind("sessId") Long sessId);

	public void suspendIntoFlatVertexTable(Long sessId, State state, String table, Set<AmberVertex> set) {
		Set<String> fields = getFields(set, state);
		String sql = String.format("INSERT INTO %s (s_id, state, id, txn_start, txn_end %s) values (:s_id, :state, :id, :txn_start, :txn_end %s)",
				table,
				StringUtils.join(format(fields, ", %s"), ' '),
				StringUtils.join(format(fields, ", :%s"), ' '));

		Handle h = getHandle();
		PreparedBatch preparedBatch = h.prepareBatch(sql);
		for (AmberVertex v: set) {
			PreparedBatchPart preparedBatchPart = preparedBatch.add();
			preparedBatchPart.bind("s_id",       sessId);
			preparedBatchPart.bind("state",      state.name());
			preparedBatchPart.bind("id",         v.getId());
			preparedBatchPart.bind("txn_start",  v.getTxnStart());
			preparedBatchPart.bind("txn_end",    v.getTxnEnd());
			if (state != DEL) {
				for (String field: fields) {
                    bindField(v, preparedBatchPart, field);
				}
			}
		}
		preparedBatch.execute();
		
	}

	public void suspendIntoNodeTable(Long sessId, State state, Set<AmberVertex> set) {
		String sql = "INSERT INTO sess_node ( s_id,  state,  id,  txn_start,  txn_end,  type,  accessConditions,  alias,  commentsExternal,  commentsInternal,  expiryDate,  internalAccessConditions,  localSystemNumber,  name,  notes,  recordSource,  restrictionType) "
				                  + "VALUES (:s_id, :state, :id, :txn_start, :txn_end, :type, :accessConditions, :alias, :commentsExternal, :commentsInternal, :expiryDate, :internalAccessConditions, :localSystemNumber, :name, :notes, :recordSource, :restrictionType)";
		if (state == DEL) { 
			sql = "INSERT INTO sess_node ( s_id,  state,  id,  txn_start,  txn_end,  type) "
	                           + "VALUES (:s_id, :state, :id, :txn_start, :txn_end, :type)";
		}

		Handle h = getHandle();
		PreparedBatch preparedBatch = h.prepareBatch(sql);
		for (AmberVertex v: set) {
			PreparedBatchPart preparedBatchPart = preparedBatch.add();
			preparedBatchPart.bind("s_id",       sessId);
			preparedBatchPart.bind("state",      state.name());
			preparedBatchPart.bind("id",         v.getId());
			preparedBatchPart.bind("txn_start",  v.getTxnStart());
			preparedBatchPart.bind("txn_end",    v.getTxnEnd());
			preparedBatchPart.bind("type",       v.getProperty("type"));
			if (state != DEL) {
				for (String field: nodeFields) {
					bindField(v, preparedBatchPart, field);
				}

			}
		}
		preparedBatch.execute();
		
	}

    private void bindField(AmberVertex v, PreparedBatchPart preparedBatchPart, String field) {
        String mappedField = field;
        if (fieldMappingReverse.containsKey(field)) {
            mappedField = fieldMappingReverse.get(field);
        }
        preparedBatchPart.bind(field, v.getProperty(mappedField));
    }
	
	public void suspendIntoFlatEdgeTable(Long sessId, State state, Set<AmberEdge> set) {
		String sql = "INSERT INTO sess_flatedge (s_id, state, id, txn_start, txn_end, v_out, v_in, edge_order, label) values (:s_id, :state, :id, :txn_start, :txn_end, :v_out, :v_in, :edge_order, :label)";

		Handle h = getHandle();
		PreparedBatch preparedBatch = h.prepareBatch(sql);
		for (AmberEdge v: set) {
			PreparedBatchPart preparedBatchPart = preparedBatch.add();
			preparedBatchPart.bind("s_id",       sessId);
			preparedBatchPart.bind("state",      state.name());
			preparedBatchPart.bind("id",         v.getId());
			preparedBatchPart.bind("txn_start",  v.getTxnStart());
			preparedBatchPart.bind("txn_end",    v.getTxnEnd());
			preparedBatchPart.bind("v_out",      v.getOutId());
			preparedBatchPart.bind("v_in",       v.getInId());
			preparedBatchPart.bind("edge_order", v.getOrder());
			preparedBatchPart.bind("label",      v.getLabel());
		}
		preparedBatch.execute();
		
	}

	public void suspendIntoFlatEdgeSpecificTable(Long sessId, State state, String table, Set<AmberEdge> set) {
		Set<String> fields = getFields(set, state);
		String sql = String.format("INSERT INTO %s (s_id, state, id, txn_start, txn_end, v_out, v_in, edge_order, label %s) values (:s_id, :state, :id, :txn_start, :txn_end, :v_out, :v_in, :edge_order, :label %s)",
				table,
				StringUtils.join(format(fields, ", %s"), ' '),
				StringUtils.join(format(fields, ", :%s"), ' '));

		Handle h = getHandle();
		PreparedBatch preparedBatch = h.prepareBatch(sql);
		for (AmberEdge v: set) {
			PreparedBatchPart preparedBatchPart = preparedBatch.add();
			preparedBatchPart.bind("s_id",       sessId);
			preparedBatchPart.bind("state",      state.name());
			preparedBatchPart.bind("id",         v.getId());
			preparedBatchPart.bind("txn_start",  v.getTxnStart());
			preparedBatchPart.bind("txn_end",    v.getTxnEnd());
			preparedBatchPart.bind("v_out",      v.getOutId());
			preparedBatchPart.bind("v_in",       v.getInId());
			preparedBatchPart.bind("edge_order", v.getOrder());
			preparedBatchPart.bind("label",       v.getLabel());
			if (state != DEL) {
				for (String field: fields) {
					preparedBatchPart.bind(field,    v.getProperty(field));
				}
			}
		}
		preparedBatch.execute();
		
	}

	private List<String> format(Collection<String> strings, String format) {
		List<String> r = new ArrayList<>(strings.size());
		for (String s: strings) {
			r.add(String.format(format, s));
		}
		return r;
	}

	private Set<String> getFields(Set<? extends BaseElement> set, State state) {
		Set<String> allFields = new HashSet<>();
		if (state != DEL) {
			for (BaseElement element: set) {
			    for (String field: element.getPropertyKeys()) {
			        if (fieldMapping.containsKey(field)) {
			            field = fieldMapping.get(field);
			        }
			        allFields.add(field);
			    }
			}
		}
		allFields.remove("nextStep");
		return allFields;
	}


	// The following queries intentionally left blank. They are implemented in the db specific AmberDao sub classes (h2 or MySql)
	@SqlUpdate("")
	public abstract void endNodes(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startNodes(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void endWorks(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startWorks(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void endFiles(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startFiles(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void endDescriptions(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startDescriptions(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void endParties(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startParties(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void endTags(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startTags(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void endFlatedges(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startFlatedges(
	@Bind("txnId") Long txnId);
	
	@SqlUpdate("")
	public abstract void endAcknowledgements(
	@Bind("txnId") Long txnId);

	@SqlUpdate("")
	public abstract void startAcknowledgements(
	@Bind("txnId") Long txnId);

}

