package amberdb.graph.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.PreparedBatchPart;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.graph.AmberEdge;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberTransaction;
import amberdb.graph.AmberVertex;
import amberdb.graph.AmberVertex;
import amberdb.graph.BaseElement;
import amberdb.graph.PropertyMapper;
import amberdb.graph.TransactionMapper;


public abstract class AmberDao implements Transactional<AmberDao>, GetHandle {

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
        "CREATE TABLE cameradata" +
        "(" +
        "    id BIGINT(20)," +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    exposureTime VARCHAR(17), " +
        "    localSystemNumber VARCHAR(39), " +
        "    encodingLevel VARCHAR(47), " +
        "    whiteBalance VARCHAR(18), " +
        "    standardId TEXT, " +
        "    language VARCHAR(35), " +
        "    lens VARCHAR(27), " +
        "    title TEXT, " +
        "    focalLenth VARCHAR(8), " +
        "    holdingId VARCHAR(7), " +
        "    australianContent TINYINT(1), " +
        "    contributor TEXT, " +
        "    isoSpeedRating VARCHAR(5), " +
        "    recordSource VARCHAR(8), " +
        "    coverage TEXT, " +
        "    bibId VARCHAR(9), " +
        "    meteringMode VARCHAR(23), " +
        "    creator TEXT, " +
        "    coordinates TEXT, " +
        "    fileSource VARCHAR(26), " +
        "    otherTitle TEXT, " +
        "    holdingNumber TEXT, " +
        "    exposureProgram VARCHAR(19), " +
        "    exposureFNumber VARCHAR(5), " +
        "    publisher TEXT, " +
        "    scaleEtc TEXT, " +
        "    exposureMode VARCHAR(15), " +
        "    focalLength VARCHAR(8) " +
        "); " +
        "CREATE INDEX cameradata_id ON cameradata (id); " +
        "CREATE TABLE cameradata_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    exposureTime VARCHAR(17), " +
        "    localSystemNumber VARCHAR(39), " +
        "    encodingLevel VARCHAR(47), " +
        "    whiteBalance VARCHAR(18), " +
        "    standardId TEXT, " +
        "    language VARCHAR(35), " +
        "    lens VARCHAR(27), " +
        "    title TEXT, " +
        "    focalLenth VARCHAR(8), " +
        "    holdingId VARCHAR(7), " +
        "    australianContent TINYINT(1), " +
        "    contributor TEXT, " +
        "    isoSpeedRating VARCHAR(5), " +
        "    recordSource VARCHAR(8), " +
        "    coverage TEXT, " +
        "    bibId VARCHAR(9), " +
        "    meteringMode VARCHAR(23), " +
        "    creator TEXT, " +
        "    coordinates TEXT, " +
        "    fileSource VARCHAR(26), " +
        "    otherTitle TEXT, " +
        "    holdingNumber TEXT, " +
        "    exposureProgram VARCHAR(19), " +
        "    exposureFNumber VARCHAR(5), " +
        "    publisher TEXT, " +
        "    scaleEtc TEXT, " +
        "    exposureMode VARCHAR(15), " +
        "    focalLength VARCHAR(8) " +
        "); " +
        "CREATE INDEX cameradata_history_id ON cameradata_history (id); " +
        "CREATE INDEX cameradata_history_txn_id ON cameradata_history (id, txn_start, txn_end); " +
        "CREATE TABLE copy " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    extent TEXT, " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    localSystemNumber VARCHAR(39), " +
        "    encodingLevel VARCHAR(47), " +
        "    standardId TEXT, " +
        "    language VARCHAR(35), " +
        "    title TEXT, " +
        "    holdingId VARCHAR(7), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    australianContent TINYINT(1), " +
        "    dateCreated DATETIME, " +
        "    contributor TEXT, " +
        "    timedStatus VARCHAR(9), " +
        "    copyType VARCHAR(9), " +
        "    alias TEXT, " +
        "    copyStatus VARCHAR(4), " +
        "    copyRole VARCHAR(3), " +
        "    manipulation TEXT, " +
        "    recordSource VARCHAR(8), " +
        "    algorithm VARCHAR(8), " +
        "    bibId VARCHAR(9), " +
        "    creator TEXT, " +
        "    otherNumbers VARCHAR(2), " +
        "    dcmDateTimeCreated DATETIME, " +
        "    materialType VARCHAR(12), " +
        "    commentsExternal TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    classification TEXT, " +
        "    currentVersion VARCHAR(3), " +
        "    commentsInternal TEXT, " +
        "    bestCopy VARCHAR(1), " +
        "    carrier VARCHAR(17), " +
        "    holdingNumber TEXT, " +
        "    series TEXT, " +
        "    publisher TEXT, " +
        "    dcmRecordCreator VARCHAR(14), " +
        "    dcmCopyPid VARCHAR(37) " +
        "); " +
        "CREATE INDEX copy_id ON copy (id); " +
        "CREATE TABLE copy_desc " +
        "( " +
        "    id BIGINT(20) PRIMARY KEY NOT NULL, " +
        "    copy_id BIGINT(20), " +
        "    created_at DATETIME, " +
        "    created_by VARCHAR(200), " +
        "    updated_at DATETIME, " +
        "    updated_by VARCHAR(200), " +
        "    exposure_program TEXT, " +
        "    exposure_time TEXT, " +
        "    focal_lenth TEXT, " +
        "    iso_speed_rating TEXT, " +
        "    lens TEXT, " +
        "    metering_mode TEXT, " +
        "    type TEXT, " +
        "    exposure_mode TEXT, " +
        "    white_balance TEXT, " +
        "    exposure_fnumber TEXT, " +
        "    file_source TEXT, " +
        "    focal_length TEXT, " +
        "    bib_id TEXT, " +
        "    coordinates TEXT, " +
        "    coverage TEXT, " +
        "    encoding_level TEXT, " +
        "    extent TEXT, " +
        "    holding_id TEXT, " +
        "    holding_number TEXT, " +
        "    language TEXT, " +
        "    local_system_number TEXT, " +
        "    other_title TEXT, " +
        "    publisher TEXT, " +
        "    record_source TEXT, " +
        "    scale_etc TEXT, " +
        "    standard_id TEXT, " +
        "    title TEXT " +
        "); " +
        "CREATE TABLE copy_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    extent TEXT, " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    localSystemNumber VARCHAR(39), " +
        "    encodingLevel VARCHAR(47), " +
        "    standardId TEXT, " +
        "    language VARCHAR(35), " +
        "    title TEXT, " +
        "    holdingId VARCHAR(7), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    australianContent TINYINT(1), " +
        "    dateCreated DATETIME, " +
        "    contributor TEXT, " +
        "    timedStatus VARCHAR(9), " +
        "    copyType VARCHAR(9), " +
        "    alias TEXT, " +
        "    copyStatus VARCHAR(4), " +
        "    copyRole VARCHAR(3), " +
        "    manipulation TEXT, " +
        "    recordSource VARCHAR(8), " +
        "    algorithm VARCHAR(8), " +
        "    bibId VARCHAR(9), " +
        "    creator TEXT, " +
        "    otherNumbers VARCHAR(2), " +
        "    dcmDateTimeCreated DATETIME, " +
        "    materialType VARCHAR(12), " +
        "    commentsExternal TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    classification TEXT, " +
        "    currentVersion VARCHAR(3), " +
        "    commentsInternal TEXT, " +
        "    bestCopy VARCHAR(1), " +
        "    carrier VARCHAR(17), " +
        "    holdingNumber TEXT, " +
        "    series TEXT, " +
        "    publisher TEXT, " +
        "    dcmRecordCreator VARCHAR(14), " +
        "    dcmCopyPid VARCHAR(37) " +
        "); " +
        "CREATE INDEX copy_history_id ON copy_history (id); " +
        "CREATE INDEX copy_history_txn_id ON copy_history (id, txn_start, txn_end); " +
        "CREATE TABLE dup_edge_to_be_removed " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    v_out BIGINT(20), " +
        "    v_in BIGINT(20), " +
        "    label VARCHAR(100), " +
        "    edge_order BIGINT(20) " +
        "); " +
        "CREATE TABLE dup_property_to_be_removed " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE dup_vertex_to_be_removed " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE durations " +
        "( " +
        "    id BIGINT(20), " +
        "    copy_id BIGINT(20), " +
        "    role CHAR(5), " +
        "    duration_secs BIGINT(20), " +
        "    carrier VARCHAR(200) " +
        "); " +
        "CREATE TABLE eadfeature " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    records TEXT, " +
        "    featureType VARCHAR(15), " +
        "    fields VARCHAR(19), " +
        "    featureId VARCHAR(39) " +
        "); " +
        "CREATE INDEX eadfeature_id ON eadfeature (id); " +
        "CREATE TABLE eadfeature_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    records TEXT, " +
        "    featureType VARCHAR(15), " +
        "    fields VARCHAR(19), " +
        "    featureId VARCHAR(39) " +
        "); " +
        "CREATE INDEX eadfeature_history_id ON eadfeature_history (id); " +
        "CREATE INDEX eadfeature_history_txn_id ON eadfeature_history (id, txn_start, txn_end); " +
        "CREATE TABLE eadwork " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    localSystemNumber VARCHAR(39), " +
        "    occupation TEXT, " +
        "    materialFromMultipleSources TINYINT(1), " +
        "    encodingLevel VARCHAR(47), " +
        "    endDate DATETIME, " +
        "    displayTitlePage TINYINT(1), " +
        "    subject TEXT, " +
        "    sendToIlms TINYINT(1), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    language VARCHAR(35), " +
        "    sensitiveMaterial VARCHAR(3), " +
        "    repository VARCHAR(30), " +
        "    holdingId VARCHAR(7), " +
        "    arrangement TEXT, " +
        "    dcmAltPi VARCHAR(52), " +
        "    folderNumber TEXT, " +
        "    collectionNumber VARCHAR(49), " +
        "    west VARCHAR(1), " +
        "    totalDuration VARCHAR(10), " +
        "    workCreatedDuringMigration TINYINT(1), " +
        "    relatedMaterial TEXT, " +
        "    dcmDateTimeCreated DATETIME, " +
        "    findingAidNote TEXT, " +
        "    collection VARCHAR(7), " +
        "    dcmWorkPid VARCHAR(31), " +
        "    otherTitle TEXT, " +
        "    classification TEXT, " +
        "    commentsInternal TEXT, " +
        "    immutable VARCHAR(12), " +
        "    folder TEXT, " +
        "    copyrightPolicy VARCHAR(31), " +
        "    nextStep VARCHAR(4), " +
        "    publisher TEXT, " +
        "    subType VARCHAR(7), " +
        "    copyingPublishing TEXT, " +
        "    scaleEtc TEXT, " +
        "    startDate DATETIME, " +
        "    tempHolding VARCHAR(2), " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    access TEXT, " +
        "    allowHighResdownload TINYINT(1), " +
        "    south VARCHAR(1), " +
        "    isMissingPage TINYINT(1), " +
        "    restrictionsOnAccess TEXT, " +
        "    north VARCHAR(1), " +
        "    scopeContent TEXT, " +
        "    representativeId VARCHAR(26), " +
        "    standardId TEXT, " +
        "    accessConditions VARCHAR(13), " +
        "    title TEXT, " +
        "    internalAccessConditions VARCHAR(10), " +
        "    eadUpdateReviewRequired VARCHAR(1), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    australianContent TINYINT(1), " +
        "    digitalStatusDate DATETIME, " +
        "    east VARCHAR(1), " +
        "    bibliography TEXT, " +
        "    contributor TEXT, " +
        "    provenance TEXT, " +
        "    moreIlmsDetailsRequired TINYINT(1), " +
        "    subUnitType VARCHAR(23), " +
        "    rights TEXT, " +
        "    uniformTitle TEXT, " +
        "    rdsAcknowledgementType VARCHAR(7), " +
        "    alias TEXT, " +
        "    recordSource VARCHAR(8), " +
        "    dateRangeInAS VARCHAR(9), " +
        "    coverage TEXT, " +
        "    bibId VARCHAR(9), " +
        "    summary TEXT, " +
        "    creator TEXT, " +
        "    preferredCitation TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    folderType VARCHAR(31), " +
        "    bibLevel VARCHAR(9), " +
        "    carrier VARCHAR(17), " +
        "    holdingNumber TEXT, " +
        "    form VARCHAR(19), " +
        "    series TEXT, " +
        "    rdsAcknowledgementReceiver TEXT, " +
        "    constraint1 TEXT, " +
        "    digitalStatus VARCHAR(18), " +
        "    dcmRecordCreator VARCHAR(14) " +
        "); " +
        "CREATE INDEX eadwork_id ON eadwork (id); " +
        "CREATE TABLE eadwork_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    localSystemNumber VARCHAR(39), " +
        "    occupation TEXT, " +
        "    materialFromMultipleSources TINYINT(1), " +
        "    encodingLevel VARCHAR(47), " +
        "    endDate DATETIME, " +
        "    displayTitlePage TINYINT(1), " +
        "    subject TEXT, " +
        "    sendToIlms TINYINT(1), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    language VARCHAR(35), " +
        "    sensitiveMaterial VARCHAR(3), " +
        "    repository VARCHAR(30), " +
        "    holdingId VARCHAR(7), " +
        "    arrangement TEXT, " +
        "    dcmAltPi VARCHAR(52), " +
        "    folderNumber TEXT, " +
        "    collectionNumber VARCHAR(49), " +
        "    west VARCHAR(1), " +
        "    totalDuration VARCHAR(10), " +
        "    workCreatedDuringMigration TINYINT(1), " +
        "    relatedMaterial TEXT, " +
        "    dcmDateTimeCreated DATETIME, " +
        "    findingAidNote TEXT, " +
        "    collection VARCHAR(7), " +
        "    dcmWorkPid VARCHAR(31), " +
        "    otherTitle TEXT, " +
        "    classification TEXT, " +
        "    commentsInternal TEXT, " +
        "    immutable VARCHAR(12), " +
        "    folder TEXT, " +
        "    copyrightPolicy VARCHAR(31), " +
        "    nextStep VARCHAR(4), " +
        "    publisher TEXT, " +
        "    subType VARCHAR(7), " +
        "    copyingPublishing TEXT, " +
        "    scaleEtc TEXT, " +
        "    startDate DATETIME, " +
        "    tempHolding VARCHAR(2), " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    access TEXT, " +
        "    allowHighResdownload TINYINT(1), " +
        "    south VARCHAR(1), " +
        "    isMissingPage TINYINT(1), " +
        "    restrictionsOnAccess TEXT, " +
        "    north VARCHAR(1), " +
        "    scopeContent TEXT, " +
        "    representativeId VARCHAR(26), " +
        "    standardId TEXT, " +
        "    accessConditions VARCHAR(13), " +
        "    title TEXT, " +
        "    internalAccessConditions VARCHAR(10), " +
        "    eadUpdateReviewRequired VARCHAR(1), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    australianContent TINYINT(1), " +
        "    digitalStatusDate DATETIME, " +
        "    east VARCHAR(1), " +
        "    bibliography TEXT, " +
        "    contributor TEXT, " +
        "    provenance TEXT, " +
        "    moreIlmsDetailsRequired TINYINT(1), " +
        "    subUnitType VARCHAR(23), " +
        "    rights TEXT, " +
        "    uniformTitle TEXT, " +
        "    rdsAcknowledgementType VARCHAR(7), " +
        "    alias TEXT, " +
        "    recordSource VARCHAR(8), " +
        "    dateRangeInAS VARCHAR(9), " +
        "    coverage TEXT, " +
        "    bibId VARCHAR(9), " +
        "    summary TEXT, " +
        "    creator TEXT, " +
        "    preferredCitation TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    folderType VARCHAR(31), " +
        "    bibLevel VARCHAR(9), " +
        "    carrier VARCHAR(17), " +
        "    holdingNumber TEXT, " +
        "    form VARCHAR(19), " +
        "    series TEXT, " +
        "    rdsAcknowledgementReceiver TEXT, " +
        "    constraint1 TEXT, " +
        "    digitalStatus VARCHAR(18), " +
        "    dcmRecordCreator VARCHAR(14) " +
        "); " +
        "CREATE INDEX eadwork_history_id ON eadwork_history (id); " +
        "CREATE INDEX eadwork_history_txn_id ON eadwork_history (id, txn_start, txn_end); " +
        "CREATE TABLE file " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    fileName TEXT, " +
        "    localSystemNumber VARCHAR(39), " +
        "    software VARCHAR(33), " +
        "    encodingLevel VARCHAR(47), " +
        "    standardId TEXT, " +
        "    language VARCHAR(35), " +
        "    mimeType TEXT, " +
        "    title TEXT, " +
        "    holdingId VARCHAR(7), " +
        "    australianContent TINYINT(1), " +
        "    contributor TEXT, " +
        "    checksum VARCHAR(40), " +
        "    recordSource VARCHAR(8), " +
        "    coverage TEXT, " +
        "    bibId VARCHAR(9), " +
        "    creator TEXT, " +
        "    checksumGenerationDate DATETIME, " +
        "    coordinates TEXT, " +
        "    encoding VARCHAR(10), " +
        "    holdingNumber TEXT, " +
        "    fileSize BIGINT(20), " +
        "    blobId BIGINT(20), " +
        "    checksumType VARCHAR(4), " +
        "    publisher TEXT, " +
        "    compression VARCHAR(9), " +
        "    device VARCHAR(44), " +
        "    fileFormat VARCHAR(20) " +
        "); " +
        "CREATE INDEX file_id ON file (id); " +
        "CREATE TABLE file_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    fileName TEXT, " +
        "    localSystemNumber VARCHAR(39), " +
        "    software VARCHAR(33), " +
        "    encodingLevel VARCHAR(47), " +
        "    standardId TEXT, " +
        "    language VARCHAR(35), " +
        "    mimeType TEXT, " +
        "    title TEXT, " +
        "    holdingId VARCHAR(7), " +
        "    australianContent TINYINT(1), " +
        "    contributor TEXT, " +
        "    checksum VARCHAR(40), " +
        "    recordSource VARCHAR(8), " +
        "    coverage TEXT, " +
        "    bibId VARCHAR(9), " +
        "    creator TEXT, " +
        "    checksumGenerationDate DATETIME, " +
        "    coordinates TEXT, " +
        "    encoding VARCHAR(10), " +
        "    holdingNumber TEXT, " +
        "    fileSize BIGINT(20), " +
        "    blobId BIGINT(20), " +
        "    checksumType VARCHAR(4), " +
        "    publisher TEXT, " +
        "    compression VARCHAR(9), " +
        "    device VARCHAR(44), " +
        "    fileFormat VARCHAR(20) " +
        "); " +
        "CREATE INDEX file_history_id ON file_history (id); " +
        "CREATE INDEX file_history_txn_id ON file_history (id, txn_start, txn_end); " +
        "CREATE TABLE imagefile " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    fileName TEXT, " +
        "    localSystemNumber VARCHAR(39), " +
        "    software VARCHAR(33), " +
        "    encodingLevel VARCHAR(47), " +
        "    language VARCHAR(35), " +
        "    mimeType TEXT, " +
        "    resolution VARCHAR(37), " +
        "    manufacturerSerialNumber VARCHAR(12), " +
        "    holdingId VARCHAR(7), " +
        "    resolutionUnit VARCHAR(4), " +
        "    imageWidth INT(11), " +
        "    manufacturerMake VARCHAR(27), " +
        "    manufacturerModelName VARCHAR(41), " +
        "    encoding VARCHAR(10), " +
        "    deviceSerialNumber VARCHAR(20), " +
        "    fileSize BIGINT(20), " +
        "    bitDepth VARCHAR(8), " +
        "    publisher TEXT, " +
        "    compression VARCHAR(9), " +
        "    device VARCHAR(44), " +
        "    imageLength INT(11), " +
        "    colourSpace VARCHAR(15), " +
        "    standardId TEXT, " +
        "    title TEXT, " +
        "    australianContent TINYINT(1), " +
        "    contributor TEXT, " +
        "    checksum VARCHAR(40), " +
        "    recordSource VARCHAR(8), " +
        "    bibId VARCHAR(9), " +
        "    coverage TEXT, " +
        "    orientation VARCHAR(36), " +
        "    creator TEXT, " +
        "    colourProfile VARCHAR(9), " +
        "    checksumGenerationDate DATETIME, " +
        "    applicationDateCreated VARCHAR(19), " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    fileFormatVersion VARCHAR(3), " +
        "    dateDigitised VARCHAR(19), " +
        "    holdingNumber TEXT, " +
        "    application TEXT, " +
        "    series TEXT, " +
        "    blobId BIGINT(20), " +
        "    softwareSerialNumber VARCHAR(10), " +
        "    checksumType VARCHAR(4), " +
        "    location TEXT, " +
        "    fileFormat VARCHAR(20) " +
        "); " +
        "CREATE INDEX imagefile_id ON imagefile (id); " +
        "CREATE TABLE imagefile_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    fileName TEXT, " +
        "    localSystemNumber VARCHAR(39), " +
        "    software VARCHAR(33), " +
        "    encodingLevel VARCHAR(47), " +
        "    language VARCHAR(35), " +
        "    mimeType TEXT, " +
        "    resolution VARCHAR(37), " +
        "    manufacturerSerialNumber VARCHAR(12), " +
        "    holdingId VARCHAR(7), " +
        "    resolutionUnit VARCHAR(4), " +
        "    imageWidth INT(11), " +
        "    manufacturerMake VARCHAR(27), " +
        "    manufacturerModelName VARCHAR(41), " +
        "    encoding VARCHAR(10), " +
        "    deviceSerialNumber VARCHAR(20), " +
        "    fileSize BIGINT(20), " +
        "    bitDepth VARCHAR(8), " +
        "    publisher TEXT, " +
        "    compression VARCHAR(9), " +
        "    device VARCHAR(44), " +
        "    imageLength INT(11), " +
        "    colourSpace VARCHAR(15), " +
        "    standardId TEXT, " +
        "    title TEXT, " +
        "    australianContent TINYINT(1), " +
        "    contributor TEXT, " +
        "    checksum VARCHAR(40), " +
        "    recordSource VARCHAR(8), " +
        "    bibId VARCHAR(9), " +
        "    coverage TEXT, " +
        "    orientation VARCHAR(36), " +
        "    creator TEXT, " +
        "    colourProfile VARCHAR(9), " +
        "    checksumGenerationDate DATETIME, " +
        "    applicationDateCreated VARCHAR(19), " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    fileFormatVersion VARCHAR(3), " +
        "    dateDigitised VARCHAR(19), " +
        "    holdingNumber TEXT, " +
        "    application TEXT, " +
        "    series TEXT, " +
        "    blobId BIGINT(20), " +
        "    softwareSerialNumber VARCHAR(10), " +
        "    checksumType VARCHAR(4), " +
        "    location TEXT, " +
        "    fileFormat VARCHAR(20) " +
        "); " +
        "CREATE INDEX imagefile_history_id ON imagefile_history (id); " +
        "CREATE INDEX imagefile_history_txn_id ON imagefile_history (id, txn_start, txn_end); " +
        "CREATE TABLE indexed_txns " +
        "( " +
        "    txn BIGINT(11) " +
        "); " +
        "CREATE INDEX indexed_txns_idx ON indexed_txns (txn); " +
        "CREATE TABLE list " +
        "( " +
        "    name VARCHAR(100), " +
        "    value VARCHAR(100), " +
        "    deleted VARCHAR(1) " +
        "); " +
        "CREATE TABLE page " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    extent TEXT, " +
        "    notes VARCHAR(30), " +
        "    localSystemNumber VARCHAR(39), " +
        "    occupation TEXT, " +
        "    encodingLevel VARCHAR(47), " +
        "    materialFromMultipleSources TINYINT(1), " +
        "    displayTitlePage TINYINT(1), " +
        "    endDate DATETIME, " +
        "    subject TEXT, " +
        "    sendToIlms TINYINT(1), " +
        "    vendorId VARCHAR(7), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    language VARCHAR(35), " +
        "    sensitiveMaterial VARCHAR(3), " +
        "    repository VARCHAR(30), " +
        "    holdingId VARCHAR(7), " +
        "    dcmAltPi VARCHAR(52), " +
        "    west VARCHAR(1), " +
        "    workCreatedDuringMigration TINYINT(1), " +
        "    dcmDateTimeCreated DATETIME, " +
        "    commentsExternal TEXT, " +
        "    firstPart VARCHAR(27), " +
        "    findingAidNote TEXT, " +
        "    collection VARCHAR(7), " +
        "    dcmWorkPid VARCHAR(31), " +
        "    otherTitle TEXT, " +
        "    classification TEXT, " +
        "    localSystemno VARCHAR(7), " +
        "    commentsInternal TEXT, " +
        "    acquisitionStatus VARCHAR(7), " +
        "    immutable VARCHAR(12), " +
        "    restrictionType VARCHAR(0), " +
        "    copyrightPolicy VARCHAR(31), " +
        "    ilmsSentDateTime DATETIME, " +
        "    publisher TEXT, " +
        "    nextStep VARCHAR(4), " +
        "    subType VARCHAR(7), " +
        "    scaleEtc TEXT, " +
        "    startDate DATETIME, " +
        "    tempHolding VARCHAR(2), " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    tilePosition TEXT, " +
        "    sortIndex VARCHAR(28), " +
        "    allowHighResdownload TINYINT(1), " +
        "    south VARCHAR(1), " +
        "    restrictionsOnAccess TEXT, " +
        "    isMissingPage TINYINT(1), " +
        "    north VARCHAR(1), " +
        "    standardId TEXT, " +
        "    representativeId VARCHAR(26), " +
        "    scopeContent TEXT, " +
        "    accessConditions VARCHAR(13), " +
        "    edition TEXT, " +
        "    alternativeTitle VARCHAR(20), " +
        "    title TEXT, " +
        "    acquisitionCategory VARCHAR(19), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    eadUpdateReviewRequired VARCHAR(1), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    australianContent TINYINT(1), " +
        "    digitalStatusDate DATETIME, " +
        "    east VARCHAR(1), " +
        "    contributor TEXT, " +
        "    moreIlmsDetailsRequired TINYINT(1), " +
        "    subUnitType VARCHAR(23), " +
        "    uniformTitle TEXT, " +
        "    rights TEXT, " +
        "    alias TEXT, " +
        "    rdsAcknowledgementType VARCHAR(7), " +
        "    issueDate DATETIME, " +
        "    recordSource VARCHAR(8), " +
        "    bibId VARCHAR(16), " +
        "    coverage TEXT, " +
        "    summary TEXT, " +
        "    creator TEXT, " +
        "    sensitiveReason TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    interactiveIndexAvailable TINYINT(1), " +
        "    bibLevel VARCHAR(9), " +
        "    carrier VARCHAR(17), " +
        "    holdingNumber TEXT, " +
        "    form VARCHAR(19), " +
        "    series TEXT, " +
        "    rdsAcknowledgementReceiver TEXT, " +
        "    constraint1 TEXT, " +
        "    digitalStatus VARCHAR(18), " +
        "    dcmRecordCreator VARCHAR(14), " +
        "    depositType TEXT, " +
        "    parentConstraint VARCHAR(35) " +
        "); " +
        "CREATE INDEX page_id ON page (id); " +
        "CREATE TABLE page_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    extent TEXT, " +
        "    notes VARCHAR(30), " +
        "    localSystemNumber VARCHAR(39), " +
        "    occupation TEXT, " +
        "    encodingLevel VARCHAR(47), " +
        "    materialFromMultipleSources TINYINT(1), " +
        "    displayTitlePage TINYINT(1), " +
        "    endDate DATETIME, " +
        "    subject TEXT, " +
        "    sendToIlms TINYINT(1), " +
        "    vendorId VARCHAR(7), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    language VARCHAR(35), " +
        "    sensitiveMaterial VARCHAR(3), " +
        "    repository VARCHAR(30), " +
        "    holdingId VARCHAR(7), " +
        "    dcmAltPi VARCHAR(52), " +
        "    west VARCHAR(1), " +
        "    workCreatedDuringMigration TINYINT(1), " +
        "    dcmDateTimeCreated DATETIME, " +
        "    commentsExternal TEXT, " +
        "    firstPart VARCHAR(27), " +
        "    findingAidNote TEXT, " +
        "    collection VARCHAR(7), " +
        "    dcmWorkPid VARCHAR(31), " +
        "    otherTitle TEXT, " +
        "    classification TEXT, " +
        "    localSystemno VARCHAR(7), " +
        "    commentsInternal TEXT, " +
        "    acquisitionStatus VARCHAR(7), " +
        "    immutable VARCHAR(12), " +
        "    restrictionType VARCHAR(0), " +
        "    copyrightPolicy VARCHAR(31), " +
        "    ilmsSentDateTime DATETIME, " +
        "    publisher TEXT, " +
        "    nextStep VARCHAR(4), " +
        "    subType VARCHAR(7), " +
        "    scaleEtc TEXT, " +
        "    startDate DATETIME, " +
        "    tempHolding VARCHAR(2), " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    tilePosition TEXT, " +
        "    sortIndex VARCHAR(28), " +
        "    allowHighResdownload TINYINT(1), " +
        "    south VARCHAR(1), " +
        "    restrictionsOnAccess TEXT, " +
        "    isMissingPage TINYINT(1), " +
        "    north VARCHAR(1), " +
        "    standardId TEXT, " +
        "    representativeId VARCHAR(26), " +
        "    scopeContent TEXT, " +
        "    accessConditions VARCHAR(13), " +
        "    edition TEXT, " +
        "    alternativeTitle VARCHAR(20), " +
        "    title TEXT, " +
        "    acquisitionCategory VARCHAR(19), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    eadUpdateReviewRequired VARCHAR(1), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    australianContent TINYINT(1), " +
        "    digitalStatusDate DATETIME, " +
        "    east VARCHAR(1), " +
        "    contributor TEXT, " +
        "    moreIlmsDetailsRequired TINYINT(1), " +
        "    subUnitType VARCHAR(23), " +
        "    uniformTitle TEXT, " +
        "    rights TEXT, " +
        "    alias TEXT, " +
        "    rdsAcknowledgementType VARCHAR(7), " +
        "    issueDate DATETIME, " +
        "    recordSource VARCHAR(8), " +
        "    bibId VARCHAR(16), " +
        "    coverage TEXT, " +
        "    summary TEXT, " +
        "    creator TEXT, " +
        "    sensitiveReason TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    interactiveIndexAvailable TINYINT(1), " +
        "    bibLevel VARCHAR(9), " +
        "    carrier VARCHAR(17), " +
        "    holdingNumber TEXT, " +
        "    form VARCHAR(19), " +
        "    series TEXT, " +
        "    rdsAcknowledgementReceiver TEXT, " +
        "    constraint1 TEXT, " +
        "    digitalStatus VARCHAR(18), " +
        "    dcmRecordCreator VARCHAR(14), " +
        "    depositType TEXT, " +
        "    parentConstraint VARCHAR(35) " +
        "); " +
        "CREATE INDEX page_history_id ON page_history (id); " +
        "CREATE INDEX page_history_txn_id ON page_history (id, txn_start, txn_end); " +
        "CREATE TABLE party_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    name VARCHAR(47), " +
        "    suppressed TINYINT(1), " +
        "    orgUrl TEXT, " +
        "    logoUrl VARCHAR(17) " +
        "); " +
        "CREATE INDEX party_history_id ON party_history (id); " +
        "CREATE INDEX party_history_txn_id ON party_history (id, txn_start, txn_end); " +
        "CREATE TABLE property_all_cameradata " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_cameradata_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_cameradata_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_copy " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_copy_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_copy_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_eadfeature " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_eadfeature_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_eadfeature_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_eadwork " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_eadwork_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_eadwork_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_file " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_file_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_file_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_geocoding " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_geocoding_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_geocoding_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_imagefile " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_imagefile_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_imagefile_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_iptc " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_iptc_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_iptc_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_page " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_page_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_page_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_party " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_party_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_party_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_section " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_section_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_section_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_soundfile " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_soundfile_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_soundfile_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_tag " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_tag_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_tag_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_work " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE property_all_work_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_all_work_txns " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_cameradata " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_cameradata_type ON property_current_cameradata (name); " +
        "CREATE TABLE property_current_cameradata_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_copy " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_copy_type ON property_current_copy (name); " +
        "CREATE TABLE property_current_copy_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_eadfeature " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_eadfeature_type ON property_current_eadfeature (name); " +
        "CREATE TABLE property_current_eadfeature_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_eadwork " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_eadwork_type ON property_current_eadwork (name); " +
        "CREATE TABLE property_current_eadwork_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_file " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_file_type ON property_current_file (name); " +
        "CREATE TABLE property_current_file_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_geocoding " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_geocoding_type ON property_current_geocoding (name); " +
        "CREATE TABLE property_current_geocoding_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_imagefile " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_imagefile_type ON property_current_imagefile (name); " +
        "CREATE TABLE property_current_imagefile_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_iptc " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_iptc_type ON property_current_iptc (name); " +
        "CREATE TABLE property_current_iptc_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_page " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_page_type ON property_current_page (name); " +
        "CREATE TABLE property_current_page_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_party " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_party_type ON property_current_party (name); " +
        "CREATE TABLE property_current_party_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_section " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_section_type ON property_current_section (name); " +
        "CREATE TABLE property_current_section_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_soundfile " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_soundfile_type ON property_current_soundfile (name); " +
        "CREATE TABLE property_current_soundfile_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_tag " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_tag_type ON property_current_tag (name); " +
        "CREATE TABLE property_current_tag_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE property_current_work " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE INDEX property_current_work_type ON property_current_work (name); " +
        "CREATE TABLE property_current_work_ids " +
        "( " +
        "    id BIGINT(20) " +
        "); " +
        "CREATE TABLE restrictions_on_access " +
        "( " +
        "    id BIGINT(20), " +
        "    value TEXT " +
        "); " +
        "CREATE TABLE section " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    creator TEXT, " +
        "    accessConditions VARCHAR(13), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    abstract TEXT, " +
        "    advertising TINYINT(1), " +
        "    title TEXT, " +
        "    printedPageNumber VARCHAR(14), " +
        "    captions VARCHAR(255), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    bibLevel VARCHAR(9), " +
        "    illustrated TINYINT(1), " +
        "    copyrightPolicy VARCHAR(31), " +
        "    metsId VARCHAR(8), " +
        "    subType VARCHAR(7), " +
        "    constraint1 TEXT " +
        "); " +
        "CREATE INDEX section_id ON section (id); " +
        "CREATE TABLE section_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    creator TEXT, " +
        "    accessConditions VARCHAR(13), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    abstract TEXT, " +
        "    advertising TINYINT(1), " +
        "    title TEXT, " +
        "    printedPageNumber VARCHAR(14), " +
        "    captions VARCHAR(255), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    bibLevel VARCHAR(9), " +
        "    illustrated TINYINT(1), " +
        "    copyrightPolicy VARCHAR(31), " +
        "    metsId VARCHAR(8), " +
        "    subType VARCHAR(7), " +
        "    constraint1 TEXT " +
        "); " +
        "CREATE INDEX section_history_id ON section_history (id); " +
        "CREATE INDEX section_history_txn_id ON section_history (id, txn_start, txn_end); " +
        "CREATE TABLE series " +
        "( " +
        "    id BIGINT(20), " +
        "    value TEXT " +
        "); " +
        "CREATE TABLE soundfile " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    fileName TEXT, " +
        "    software VARCHAR(33), " +
        "    thickness VARCHAR(11), " +
        "    channel VARCHAR(3), " +
        "    bitrate VARCHAR(3), " +
        "    mimeType TEXT, " +
        "    durationType VARCHAR(7), " +
        "    speed VARCHAR(10), " +
        "    duration VARCHAR(12), " +
        "    toolId VARCHAR(13), " +
        "    checksum VARCHAR(40), " +
        "    soundField VARCHAR(9), " +
        "    fileContainer VARCHAR(3), " +
        "    brand VARCHAR(27), " +
        "    surface VARCHAR(20), " +
        "    equalisation VARCHAR(4), " +
        "    encoding VARCHAR(10), " +
        "    codec VARCHAR(4), " +
        "    fileSize BIGINT(20), " +
        "    reelSize VARCHAR(12), " +
        "    carrierCapacity VARCHAR(7), " +
        "    bitDepth VARCHAR(8), " +
        "    blobId BIGINT(20), " +
        "    checksumType VARCHAR(4), " +
        "    samplingRate VARCHAR(5), " +
        "    compression VARCHAR(9), " +
        "    fileFormat VARCHAR(20) " +
        "); " +
        "CREATE INDEX soundfile_id ON soundfile (id); " +
        "CREATE TABLE soundfile_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    fileName TEXT, " +
        "    software VARCHAR(33), " +
        "    thickness VARCHAR(11), " +
        "    channel VARCHAR(3), " +
        "    bitrate VARCHAR(3), " +
        "    mimeType TEXT, " +
        "    durationType VARCHAR(7), " +
        "    speed VARCHAR(10), " +
        "    duration VARCHAR(12), " +
        "    toolId VARCHAR(13), " +
        "    checksum VARCHAR(40), " +
        "    soundField VARCHAR(9), " +
        "    fileContainer VARCHAR(3), " +
        "    brand VARCHAR(27), " +
        "    surface VARCHAR(20), " +
        "    equalisation VARCHAR(4), " +
        "    encoding VARCHAR(10), " +
        "    codec VARCHAR(4), " +
        "    fileSize BIGINT(20), " +
        "    reelSize VARCHAR(12), " +
        "    carrierCapacity VARCHAR(7), " +
        "    bitDepth VARCHAR(8), " +
        "    blobId BIGINT(20), " +
        "    checksumType VARCHAR(4), " +
        "    samplingRate VARCHAR(5), " +
        "    compression VARCHAR(9), " +
        "    fileFormat VARCHAR(20) " +
        "); " +
        "CREATE INDEX soundfile_history_id ON soundfile_history (id); " +
        "CREATE INDEX soundfile_history_txn_id ON soundfile_history (id, txn_start, txn_end); " +
        "CREATE TABLE stage_edge " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_new BIGINT(20), " +
        "    v_out BIGINT(20), " +
        "    v_in BIGINT(20), " +
        "    label VARCHAR(100), " +
        "    edge_order BIGINT(20), " +
        "    state CHAR(3) " +
        "); " +
        "CREATE INDEX stage_edge_combined_state_new_idx ON stage_edge (txn_new, state); " +
        "CREATE INDEX stage_edge_id_idx ON stage_edge (id); " +
        "CREATE INDEX stage_edge_stage_idx ON stage_edge (state); " +
        "CREATE INDEX stage_edge_txn_new_idx ON stage_edge (txn_new); " +
        "CREATE TABLE stage_property " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_new BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE stage_vertex " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_new BIGINT(20), " +
        "    state CHAR(3) " +
        "); " +
        "CREATE INDEX stage_vertex_combined_state_new_idx ON stage_vertex (txn_new, state); " +
        "CREATE INDEX stage_vertex_combined_txn_new_state_idx ON stage_vertex (txn_new, state); " +
        "CREATE TABLE tag " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    name VARCHAR(47) " +
        "); " +
        "CREATE INDEX tag_id ON tag (id); " +
        "CREATE TABLE tag_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    name VARCHAR(47) " +
        "); " +
        "CREATE INDEX tag_history_id ON tag_history (id); " +
        "CREATE INDEX tag_history_txn_id ON tag_history (id, txn_start, txn_end); " +
        "CREATE TABLE work " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    localSystemNumber VARCHAR(39), " +
        "    occupation TEXT, " +
        "    endDate DATETIME, " +
        "    displayTitlePage TINYINT(1), " +
        "    holdingId VARCHAR(7), " +
        "    hasRepresentation VARCHAR(1), " +
        "    totalDuration VARCHAR(10), " +
        "    dcmDateTimeCreated DATETIME, " +
        "    firstPart VARCHAR(27), " +
        "    additionalTitle TEXT, " +
        "    dcmWorkPid VARCHAR(31), " +
        "    classification TEXT, " +
        "    commentsInternal TEXT, " +
        "    restrictionType VARCHAR(0), " +
        "    ilmsSentDateTime DATETIME, " +
        "    subType VARCHAR(7), " +
        "    scaleEtc TEXT, " +
        "    startDate DATETIME, " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    tilePosition TEXT, " +
        "    allowHighResdownload TINYINT(1), " +
        "    south VARCHAR(1), " +
        "    restrictionsOnAccess TEXT, " +
        "    preservicaType TEXT, " +
        "    north VARCHAR(1), " +
        "    accessConditions VARCHAR(13), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    eadUpdateReviewRequired VARCHAR(1), " +
        "    australianContent TINYINT(1), " +
        "    moreIlmsDetailsRequired TINYINT(1), " +
        "    rights TEXT, " +
        "    genre VARCHAR(11), " +
        "    deliveryUrl VARCHAR(25), " +
        "    recordSource VARCHAR(8), " +
        "    sheetCreationDate TEXT, " +
        "    creator TEXT, " +
        "    sheetName TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    additionalCreator TEXT, " +
        "    folderType VARCHAR(31), " +
        "    eventNote TEXT, " +
        "    interactiveIndexAvailable TINYINT(1), " +
        "    startChild VARCHAR(17), " +
        "    bibLevel VARCHAR(9), " +
        "    holdingNumber TEXT, " +
        "    publicNotes TEXT, " +
        "    series TEXT, " +
        "    constraint1 TEXT, " +
        "    notes VARCHAR(30), " +
        "    catalogueUrl VARCHAR(35), " +
        "    encodingLevel VARCHAR(47), " +
        "    materialFromMultipleSources TINYINT(1), " +
        "    subject TEXT, " +
        "    sendToIlms TINYINT(1), " +
        "    vendorId VARCHAR(7), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    language VARCHAR(35), " +
        "    sensitiveMaterial VARCHAR(3), " +
        "    dcmAltPi VARCHAR(52), " +
        "    folderNumber VARCHAR(50), " +
        "    west VARCHAR(1), " +
        "    html TEXT, " +
        "    preservicaId TEXT, " +
        "    redocworksReason VARCHAR(20), " +
        "    workCreatedDuringMigration TINYINT(1), " +
        "    author TEXT, " +
        "    commentsExternal TEXT, " +
        "    findingAidNote TEXT, " +
        "    collection VARCHAR(7), " +
        "    otherTitle TEXT, " +
        "    imageServerUrl VARCHAR(48), " +
        "    localSystemno VARCHAR(7), " +
        "    acquisitionStatus VARCHAR(7), " +
        "    reorderType VARCHAR(8), " +
        "    immutable VARCHAR(12), " +
        "    copyrightPolicy VARCHAR(31), " +
        "    nextStep VARCHAR(4), " +
        "    publisher TEXT, " +
        "    additionalSeries TEXT, " +
        "    tempHolding VARCHAR(2), " +
        "    sortIndex VARCHAR(28), " +
        "    isMissingPage TINYINT(1), " +
        "    standardId TEXT, " +
        "    representativeId VARCHAR(26), " +
        "    edition TEXT, " +
        "    reorder VARCHAR(1), " +
        "    title TEXT, " +
        "    acquisitionCategory VARCHAR(19), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    digitalStatusDate DATETIME, " +
        "    east VARCHAR(1), " +
        "    contributor TEXT, " +
        "    publicationCategory VARCHAR(11), " +
        "    ingestJobId BIGINT(20), " +
        "    subUnitType VARCHAR(23), " +
        "    uniformTitle TEXT, " +
        "    alias TEXT, " +
        "    rdsAcknowledgementType VARCHAR(7), " +
        "    issueDate DATETIME, " +
        "    bibId VARCHAR(9), " +
        "    coverage TEXT, " +
        "    summary TEXT, " +
        "    additionalContributor TEXT, " +
        "    sendToIlmsDateTime VARCHAR(10), " +
        "    sensitiveReason TEXT, " +
        "    carrier VARCHAR(17), " +
        "    form VARCHAR(19), " +
        "    rdsAcknowledgementReceiver TEXT, " +
        "    digitalStatus VARCHAR(18), " +
        "    dcmRecordCreator VARCHAR(14), " +
        "    sprightlyUrl VARCHAR(39), " +
        "    depositType TEXT, " +
        "    parentConstraint VARCHAR(35), " +
        "    additionalSeriesStatement TEXT " +
        "); " +
        "CREATE INDEX work_id ON work (id); " +
        "CREATE TABLE work_902502_backup " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    type CHAR(3), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE work_desc " +
        "( " +
        "    id BIGINT(20) PRIMARY KEY NOT NULL, " +
        "    work_id BIGINT(20), " +
        "    created_at DATETIME, " +
        "    created_by VARCHAR(200), " +
        "    updated_at DATETIME, " +
        "    updated_by VARCHAR(200), " +
        "    feature_id TEXT, " +
        "    feature_type TEXT, " +
        "    fields TEXT, " +
        "    records TEXT, " +
        "    type TEXT, " +
        "    latitude TEXT, " +
        "    longitude TEXT, " +
        "    map_datum TEXT, " +
        "    timestamp DATETIME, " +
        "    city TEXT, " +
        "    province TEXT " +
        "); " +
        "CREATE TABLE work_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    extent TEXT, " +
        "    dcmDateTimeUpdated DATETIME, " +
        "    localSystemNumber VARCHAR(39), " +
        "    occupation TEXT, " +
        "    endDate DATETIME, " +
        "    displayTitlePage TINYINT(1), " +
        "    holdingId VARCHAR(7), " +
        "    hasRepresentation VARCHAR(1), " +
        "    totalDuration VARCHAR(10), " +
        "    dcmDateTimeCreated DATETIME, " +
        "    firstPart VARCHAR(27), " +
        "    additionalTitle TEXT, " +
        "    dcmWorkPid VARCHAR(31), " +
        "    classification TEXT, " +
        "    commentsInternal TEXT, " +
        "    restrictionType VARCHAR(0), " +
        "    ilmsSentDateTime DATETIME, " +
        "    subType VARCHAR(7), " +
        "    scaleEtc TEXT, " +
        "    startDate DATETIME, " +
        "    dcmRecordUpdater VARCHAR(26), " +
        "    tilePosition TEXT, " +
        "    allowHighResdownload TINYINT(1), " +
        "    south VARCHAR(1), " +
        "    restrictionsOnAccess TEXT, " +
        "    preservicaType TEXT, " +
        "    north VARCHAR(1), " +
        "    accessConditions VARCHAR(13), " +
        "    internalAccessConditions VARCHAR(10), " +
        "    eadUpdateReviewRequired VARCHAR(1), " +
        "    australianContent TINYINT(1), " +
        "    moreIlmsDetailsRequired TINYINT(1), " +
        "    rights TEXT, " +
        "    genre VARCHAR(11), " +
        "    deliveryUrl VARCHAR(25), " +
        "    recordSource VARCHAR(8), " +
        "    sheetCreationDate TEXT, " +
        "    creator TEXT, " +
        "    sheetName TEXT, " +
        "    coordinates TEXT, " +
        "    creatorStatement TEXT, " +
        "    additionalCreator TEXT, " +
        "    folderType VARCHAR(31), " +
        "    eventNote TEXT, " +
        "    interactiveIndexAvailable TINYINT(1), " +
        "    startChild VARCHAR(17), " +
        "    bibLevel VARCHAR(9), " +
        "    holdingNumber TEXT, " +
        "    publicNotes TEXT, " +
        "    series TEXT, " +
        "    constraint1 TEXT, " +
        "    notes VARCHAR(30), " +
        "    catalogueUrl VARCHAR(35), " +
        "    encodingLevel VARCHAR(47), " +
        "    materialFromMultipleSources TINYINT(1), " +
        "    subject TEXT, " +
        "    sendToIlms TINYINT(1), " +
        "    vendorId VARCHAR(7), " +
        "    allowOnsiteAccess TINYINT(1), " +
        "    language VARCHAR(35), " +
        "    sensitiveMaterial VARCHAR(3), " +
        "    dcmAltPi VARCHAR(52), " +
        "    folderNumber VARCHAR(50), " +
        "    west VARCHAR(1), " +
        "    html TEXT, " +
        "    preservicaId TEXT, " +
        "    redocworksReason VARCHAR(20), " +
        "    workCreatedDuringMigration TINYINT(1), " +
        "    author TEXT, " +
        "    commentsExternal TEXT, " +
        "    findingAidNote TEXT, " +
        "    collection VARCHAR(7), " +
        "    otherTitle TEXT, " +
        "    imageServerUrl VARCHAR(48), " +
        "    localSystemno VARCHAR(7), " +
        "    acquisitionStatus VARCHAR(7), " +
        "    reorderType VARCHAR(8), " +
        "    immutable VARCHAR(12), " +
        "    copyrightPolicy VARCHAR(31), " +
        "    nextStep VARCHAR(4), " +
        "    publisher TEXT, " +
        "    additionalSeries TEXT, " +
        "    tempHolding VARCHAR(2), " +
        "    sortIndex VARCHAR(28), " +
        "    isMissingPage TINYINT(1), " +
        "    standardId TEXT, " +
        "    representativeId VARCHAR(26), " +
        "    edition TEXT, " +
        "    reorder VARCHAR(1), " +
        "    title TEXT, " +
        "    acquisitionCategory VARCHAR(19), " +
        "    subUnitNo TEXT, " +
        "    expiryDate DATETIME, " +
        "    digitalStatusDate DATETIME, " +
        "    east VARCHAR(1), " +
        "    contributor TEXT, " +
        "    publicationCategory VARCHAR(11), " +
        "    ingestJobId BIGINT(20), " +
        "    subUnitType VARCHAR(23), " +
        "    uniformTitle TEXT, " +
        "    alias TEXT, " +
        "    rdsAcknowledgementType VARCHAR(7), " +
        "    issueDate DATETIME, " +
        "    bibId VARCHAR(32), " +
        "    coverage TEXT, " +
        "    summary TEXT, " +
        "    additionalContributor TEXT, " +
        "    sendToIlmsDateTime VARCHAR(10), " +
        "    sensitiveReason TEXT, " +
        "    carrier VARCHAR(17), " +
        "    form VARCHAR(19), " +
        "    rdsAcknowledgementReceiver TEXT, " +
        "    digitalStatus VARCHAR(18), " +
        "    dcmRecordCreator VARCHAR(14), " +
        "    sprightlyUrl VARCHAR(39), " +
        "    depositType TEXT, " +
        "    parentConstraint VARCHAR(35) " +
        "); " +
        "CREATE INDEX work_history_id ON work_history (id); " +
        "CREATE INDEX work_history_txn_id ON work_history (id, txn_start, txn_end); " +
        "CREATE TABLE work_copy " +
        "( " +
        "    work_id BIGINT(20) NOT NULL, " +
        "    copy_id BIGINT(20) NOT NULL, " +
        "    edge_order INT(11) NOT NULL, " +
        "    work_type VARCHAR(20) NOT NULL, " +
        "    copy_role VARCHAR(3) NOT NULL " +
        "); " +
        "CREATE INDEX wc_copy_id_index ON work_copy (copy_id); " +
        "CREATE INDEX wc_edge_order_index ON work_copy (edge_order); " +
        "CREATE INDEX wc_work_id_index ON work_copy (work_id); " +
        "CREATE INDEX wc_work_id_type_index ON work_copy (work_id, work_type); " +
        "CREATE TABLE copy_file " +
        "( " +
        "    copy_id BIGINT(20) NOT NULL, " +
        "    file_id BIGINT(20) NOT NULL, " +
        "    file_type VARCHAR(20) NOT NULL " +
        "); " +
        "CREATE INDEX cf_copy_id_file_type_index ON copy_file (copy_id, file_type); " +
        "CREATE INDEX cf_copy_id_index ON copy_file (copy_id); " +
        "CREATE INDEX cf_file_id_type_index ON copy_file (file_id, file_type); " +
        "CREATE TABLE work_deliverywork " +
        "( " +
        "    work_id BIGINT(20) NOT NULL, " +
        "    deliverywork_id BIGINT(20) NOT NULL, " +
        "    edge_order INT(11) NOT NULL " +
        "); " +
        "CREATE INDEX wd_deliverywork_id_work_id_index ON work_deliverywork (deliverywork_id, work_id); " +
        "CREATE INDEX wd_work_id_edge_order_index ON work_deliverywork (work_id, edge_order); " +
        "CREATE INDEX wd_work_id_index ON work_deliverywork (work_id); " +
        "CREATE TABLE work_section " +
        "( " +
        "    work_id BIGINT(20) NOT NULL, " +
        "    section_id BIGINT(20) NOT NULL, " +
        "    subtype VARCHAR(10) NOT NULL " +
        "); " +
        "CREATE INDEX ws_section_id_index ON work_section (section_id); " +
        "CREATE INDEX ws_work_id_index ON work_section (work_id); " +
        "CREATE INDEX ws_work_id_subtype_index ON work_section (work_id, subtype); " +
        "CREATE TABLE parent_child " +
        "( " +
        "    parent_id BIGINT(20) NOT NULL, " +
        "    child_id BIGINT(20) NOT NULL, " +
        "    child_biblevel VARCHAR(10) NOT NULL, " +
        "    child_subtype VARCHAR(10) NOT NULL " +
        "); " +
        "CREATE INDEX pc_child_id_biblevel_subtype ON parent_child (child_id, child_biblevel, child_subtype); " +
        "CREATE INDEX pc_child_id_index ON parent_child (child_id); " +
        "CREATE INDEX pc_parent_id_child_biblevel_subtype ON parent_child (parent_id, child_biblevel, child_subtype); " +
        "CREATE INDEX pc_parent_id_index ON parent_child (parent_id); " +
        "CREATE TABLE work_alias " +
        "( " +
        "    work_id BIGINT(20) NOT NULL, " +
        "    alias VARCHAR(100) NOT NULL " +
        "); " +
        "CREATE INDEX wa_alias_work_id_index ON work_alias (alias, work_id); " +
        "CREATE INDEX wa_work_id_index ON work_alias (work_id); " +
        "CREATE TABLE party " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    name VARCHAR(47), " +
        "    suppressed TINYINT(1), " +
        "    orgUrl TEXT, " +
        "    logoUrl VARCHAR(17) " +
        "); " +
        "CREATE TABLE temp_ack " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE temp_ack_history " +
        "( " +
        "    id BIGINT(20), " +
        "    txn_start BIGINT(20), " +
        "    txn_end BIGINT(20), " +
        "    name VARCHAR(100), " +
        "    value BLOB " +
        "); " +
        "CREATE TABLE acknowledgement_history " +
        "( " +
        "    id BIGINT(20) NOT NULL, " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    ack_type VARCHAR(100) DEFAULT '' NOT NULL, " +
        "    kind_of_support VARCHAR(100) DEFAULT '' NOT NULL, " +
        "    weighting DECIMAL(5,2) DEFAULT '0.00' NOT NULL, " +
        "    url_to_original TEXT, " +
        "    date DATETIME " +
        "); " +
        "CREATE INDEX ack_history_id_index ON acknowledgement_history (id); " +
        "CREATE INDEX ack_history_id_weighting_index ON acknowledgement_history (id, weighting); " +
        "CREATE TABLE acknowledgement " +
        "( " +
        "    id BIGINT(20) NOT NULL, " +
        "    txn_start BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    txn_end BIGINT(20) DEFAULT '0' NOT NULL, " +
        "    ack_type VARCHAR(100) DEFAULT '' NOT NULL, " +
        "    kind_of_support VARCHAR(100) DEFAULT '' NOT NULL, " +
        "    weighting DECIMAL(5,2) DEFAULT '0.00' NOT NULL, " +
        "    url_to_original TEXT, " +
        "    date DATETIME " +
        "); " +
        "CREATE INDEX ack_id_index ON acknowledgement (id); " +
        "CREATE INDEX ack_id_weighting_index ON acknowledgement (id, weighting); " +
        "CREATE TABLE acknowledgement_party " +
        "( " +
        "    acknowledgement_id BIGINT(20) NOT NULL, " +
        "    party_id BIGINT(20) " +
        "); " +
        "CREATE INDEX ap_acknowledgement_id_index ON acknowledgement_party (acknowledgement_id); " +
        "CREATE INDEX ap_party_id_index ON acknowledgement_party (party_id); " +
        "CREATE TABLE work_acknowledgement " +
        "( " +
        "    work_id BIGINT(20), " +
        "    acknowledgement_id BIGINT(20) NOT NULL, " +
        "    weighting DECIMAL(5,2) DEFAULT '0.00' NOT NULL " +
        "); " +
        "CREATE INDEX wack_ack_id_index ON work_acknowledgement (acknowledgement_id); " +
        "CREATE INDEX wack_ack_id_weighting_index ON work_acknowledgement (acknowledgement_id, weighting); " +
        "CREATE INDEX wack_work_id_index ON work_acknowledgement (work_id); " +
        "CREATE TABLE representative_work " +
        "( " +
        "    copy_id BIGINT(20), " +
        "    work_id BIGINT(20), " +
        "    edge_order INT(11), " +
        "    work_type VARCHAR(20) " +
        "); " +
        "CREATE INDEX rw_copy_id_index ON representative_work (copy_id); " +
        "CREATE INDEX rw_copy_id_order ON representative_work (copy_id, edge_order); " +
        "CREATE INDEX rw_work_id_type_index ON representative_work (work_id, work_type);")
    public abstract void createV2Tables();

    @SqlQuery(
            "SELECT (COUNT(table_name) >= 8) "
            + "FROM information_schema.tables "
            + "WHERE table_name IN ("
            + "  'VERTEX', 'EDGE', 'PROPERTY', "
            + "  'SESS_VERTEX', 'SESS_EDGE', 'SESS_PROPERTY', "
            + "  'ID_GENERATOR', 'TRANSACTION')")
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
            @Bind("state")     List<String>  state);


    @SqlBatch("INSERT INTO sess_vertex (s_id, id, txn_start, txn_end, state) "
            + "VALUES (:sessId, :id, :txnStart, :txnEnd, :state)")
    public abstract void suspendVertices(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("txnStart")  List<Long>   txnStart,
            @Bind("txnEnd")    List<Long>   txnEnd,
            @Bind("state")     List<String> state);


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

	public void suspendIntoFlatVertexTable(Long sessId, String state, String table, Set<AmberVertex> set) {
		Set<String> fields = getFields(set);
		String sql = String.format("INSERT INTO %s (s_id, state, id, txn_start, txn_end, %s) values (:s_id, :state, :id, :txn_start, :txn_end, %s)",
				table,
				StringUtils.join(fields, ','),
				StringUtils.join(format(fields, ":%s"), ','));

		Handle h = getHandle();
		PreparedBatch preparedBatch = h.prepareBatch(sql);
		for (AmberVertex v: set) {
			PreparedBatchPart preparedBatchPart = preparedBatch.add();
			preparedBatchPart.bind("s_id",       sessId);
			preparedBatchPart.bind("state",      state);
			preparedBatchPart.bind("id",         v.getId());
			preparedBatchPart.bind("txn_start",  v.getTxnStart());
			preparedBatchPart.bind("txn_end",    v.getTxnEnd());
			if (!"DEL".equals(state)) {
				for (String field: fields) {
					preparedBatchPart.bind(field,    v.getProperty(field));
				}
			}
		}
		preparedBatch.execute();
		
	}

	public void suspendIntoFlatEdgeTable(Long sessId, String state, String table, Set<AmberEdge> set) {
		Set<String> fields = getFields(set);
		String sql = String.format("INSERT INTO %s (s_id, state, id, txn_start, txn_end, %s) values (:s_id, :state, :id, :txn_start, :txn_end, %s)",
				table,
				StringUtils.join(fields, ','),
				StringUtils.join(format(fields, ":%s"), ','));

		Handle h = getHandle();
		PreparedBatch preparedBatch = h.prepareBatch(sql);
		for (AmberEdge v: set) {
			PreparedBatchPart preparedBatchPart = preparedBatch.add();
			preparedBatchPart.bind("s_id",       sessId);
			preparedBatchPart.bind("state",      state);
			preparedBatchPart.bind("id",         v.getId());
			preparedBatchPart.bind("txn_start",  v.getTxnStart());
			preparedBatchPart.bind("txn_end",    v.getTxnEnd());
			if (!"DEL".equals(state)) {
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

	private Set<String> getFields(Set<? extends BaseElement> set) {
		Set<String> allFields = new HashSet<>();
		for (BaseElement element: set) {
			Set<String> fields = element.getPropertyKeys();
			fields.remove("type");
			allFields.addAll(fields);
		}
		return allFields;
	}


    // The following query intentionally left blank. It's implemented in the db specific AmberDao sub classes (h2 or MySql)
    @SqlUpdate("")
    public abstract void endWorks(
            @Bind("txnId") Long txnId);
    

    @SqlUpdate("SET @txn = :txnId;\n"
            + "INSERT INTO work_history (id, txn_start, txn_end, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint) "
            + "SELECT id, s_id, 0, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint "
            + "FROM sess_work "
            + "WHERE s_id = @txn "
            + "AND state = 'NEW';\n"

            + "INSERT INTO work_history (id, txn_start, txn_end, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint) "
            + "SELECT id, s_id, 0, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint "
            + "FROM sess_work "
            + "WHERE s_id = @txn "
            + "AND state = 'MOD';\n"
            
            + "INSERT INTO work (id, txn_start, txn_end, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint) "
            + "SELECT id, s_id, 0, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint "
            + "FROM sess_work "
            + "WHERE s_id = @txn "
            + "AND state = 'NEW';\n"

            + "UPDATE work w, sess_work sw SET w.txn_start = sw.txn_start, w.txn_end = sw.txn_end, w.extent = sw.extent, w.dcmDateTimeUpdated = sw.dcmDateTimeUpdated, w.localSystemNumber = sw.localSystemNumber, w.occupation = sw.occupation, w.endDate = sw.endDate, w.displayTitlePage = sw.displayTitlePage, w.holdingId = sw.holdingId, w.hasRepresentation = sw.hasRepresentation, w.totalDuration = sw.totalDuration, w.dcmDateTimeCreated = sw.dcmDateTimeCreated, w.firstPart = sw.firstPart, w.additionalTitle = sw.additionalTitle, w.dcmWorkPid = sw.dcmWorkPid, w.classification = sw.classification, w.commentsInternal = sw.commentsInternal, w.restrictionType = sw.restrictionType, w.ilmsSentDateTime = sw.ilmsSentDateTime, w.subType = sw.subType, w.scaleEtc = sw.scaleEtc, w.startDate = sw.startDate, w.dcmRecordUpdater = sw.dcmRecordUpdater, w.tilePosition = sw.tilePosition, w.allowHighResdownload = sw.allowHighResdownload, w.south = sw.south, w.restrictionsOnAccess = sw.restrictionsOnAccess, w.preservicaType = sw.preservicaType, w.north = sw.north, w.accessConditions = sw.accessConditions, w.internalAccessConditions = sw.internalAccessConditions, w.eadUpdateReviewRequired = sw.eadUpdateReviewRequired, w.australianContent = sw.australianContent, w.moreIlmsDetailsRequired = sw.moreIlmsDetailsRequired, w.rights = sw.rights, w.genre = sw.genre, w.deliveryUrl = sw.deliveryUrl, w.recordSource = sw.recordSource, w.sheetCreationDate = sw.sheetCreationDate, w.creator = sw.creator, w.sheetName = sw.sheetName, w.coordinates = sw.coordinates, w.creatorStatement = sw.creatorStatement, w.additionalCreator = sw.additionalCreator, w.folderType = sw.folderType, w.eventNote = sw.eventNote, w.interactiveIndexAvailable = sw.interactiveIndexAvailable, w.startChild = sw.startChild, w.bibLevel = sw.bibLevel, w.holdingNumber = sw.holdingNumber, w.publicNotes = sw.publicNotes, w.series = sw.series, w.constraint1 = sw.constraint1, w.notes = sw.notes, w.catalogueUrl = sw.catalogueUrl, w.encodingLevel = sw.encodingLevel, w.materialFromMultipleSources = sw.materialFromMultipleSources, w.subject = sw.subject, w.sendToIlms = sw.sendToIlms, w.vendorId = sw.vendorId, w.allowOnsiteAccess = sw.allowOnsiteAccess, w.language = sw.language, w.sensitiveMaterial = sw.sensitiveMaterial, w.dcmAltPi = sw.dcmAltPi, w.folderNumber = sw.folderNumber, w.west = sw.west, w.html = sw.html, w.preservicaId = sw.preservicaId, w.redocworksReason = sw.redocworksReason, w.workCreatedDuringMigration = sw.workCreatedDuringMigration, w.author = sw.author, w.commentsExternal = sw.commentsExternal, w.findingAidNote = sw.findingAidNote, w.collection = sw.collection, w.otherTitle = sw.otherTitle, w.imageServerUrl = sw.imageServerUrl, w.localSystemNo = sw.localSystemNo, w.acquisitionStatus = sw.acquisitionStatus, w.reorderType = sw.reorderType, w.immutable = sw.immutable, w.copyrightPolicy = sw.copyrightPolicy, w.nextStep = sw.nextStep, w.publisher = sw.publisher, w.additionalSeries = sw.additionalSeries, w.tempHolding = sw.tempHolding, w.sortIndex = sw.sortIndex, w.isMissingPage = sw.isMissingPage, w.standardId = sw.standardId, w.representativeId = sw.representativeId, w.edition = sw.edition, w.reorder = sw.reorder, w.title = sw.title, w.acquisitionCategory = sw.acquisitionCategory, w.subUnitNo = sw.subUnitNo, w.expiryDate = sw.expiryDate, w.digitalStatusDate = sw.digitalStatusDate, w.east = sw.east, w.contributor = sw.contributor, w.publicationCategory = sw.publicationCategory, w.ingestJobId = sw.ingestJobId, w.subUnitType = sw.subUnitType, w.uniformTitle = sw.uniformTitle, w.alias = sw.alias, w.rdsAcknowledgementType = sw.rdsAcknowledgementType, w.issueDate = sw.issueDate, w.bibId = sw.bibId, w.coverage = sw.coverage, w.summary = sw.summary, w.additionalContributor = sw.additionalContributor, w.sendToIlmsDateTime = sw.sendToIlmsDateTime, w.sensitiveReason = sw.sensitiveReason, w.carrier = sw.carrier, w.form = sw.form, w.rdsAcknowledgementReceiver = sw.rdsAcknowledgementReceiver, w.digitalStatus = sw.digitalStatus, w.dcmRecordCreator = sw.dcmRecordCreator, w.sprightlyUrl = sw.sprightlyUrl, w.depositType = sw.depositType, w.parentConstraint = sw.parentConstraint "
    		+ "WHERE w.id = sw.id "
            + "AND s_id = @txn "
            + "AND state = 'MOD';\n")

    public abstract void startWorks(
            @Bind("txnId") Long txnId);
	
}

