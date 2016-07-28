package amberdb.graph.dao;


import java.util.List;

import amberdb.graph.AmberProperty;
import amberdb.graph.TransactionMapper;
import amberdb.v2.model.Work;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.graph.AmberTransaction;
import amberdb.graph.PropertyMapper;


public interface AmberDao extends Transactional<AmberDao> {

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
    void createVertexTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge ("
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, "
            + "v_out      BIGINT, "
            + "v_in       BIGINT, "
            + "label      VARCHAR(100), "
            + "edge_order BIGINT)")
    void createEdgeTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property ("
            + "id        BIGINT, "
            + "txn_start BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end   BIGINT DEFAULT 0 NOT NULL, "
            + "name      VARCHAR(100), "
            + "type      CHAR(3), "
            + "value     BLOB)")
    void createPropertyTable();


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
    void createSessionVertexTable();


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
    void createSessionEdgeTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_property ("
            + "s_id      BIGINT, "
            + "id        BIGINT, "
            + "name      VARCHAR(100), "
            + "type      CHAR(3), "
            + "value     BLOB)")
    void createSessionPropertyTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS id_generator ("
            + "id BIGINT PRIMARY KEY AUTO_INCREMENT)")
    void createIdGeneratorTable();


    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS transaction ("
            + "id        BIGINT UNIQUE, "
            + "time      BIGINT, "
            + "user      VARCHAR(100), "
            + "operation TEXT)")
    void createTransactionTable();


    /*
     * Main table indexes - these require review as they might need indexes.
     */
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_vert "
            + "ON vertex(id, txn_start)")
    void createVertexIndex();


    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_edge "
            + "ON edge(id, txn_start)")
    void createEdgeIndex();


    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_prop "
            + "ON property(id, txn_start, name)")
    void createPropertyIndex();


    @SqlUpdate(
            "CREATE INDEX vert_txn_end_idx "
            + "ON vertex(txn_end)")
    void createVertexTxnEndIndex();


    @SqlUpdate(
            "CREATE INDEX edge_txn_end_idx "
            + "ON edge(txn_end)")
    void createEdgeTxnEndIndex();


    @SqlUpdate(
            "CREATE INDEX prop_name_idx "
            + "ON property(name)")
    void createPropertyNameIndex();


    @SqlUpdate(
            "CREATE INDEX property_value_idx "
            + "ON property(value(512))")
    void createPropertyValueIndex();


    @SqlUpdate(
            "CREATE INDEX property_name_val_idx "
            + "ON property(name, value(512))")
    void createPropertyNameValueIndex();


    @SqlUpdate(
            "CREATE INDEX prop_txn_end_idx "
            + "ON property(txn_end)")
    void createPropertyTxnEndIndex();


    @SqlUpdate(
            "CREATE INDEX edge_label_idx "
            + "ON edge(label)")
    void createEdgeLabelIndex();


    @SqlUpdate(
            "CREATE INDEX edge_in_idx "
            + "ON edge(v_in)")
    void createEdgeInVertexIndex();


    @SqlUpdate(
            "CREATE INDEX edge_out_idx "
            + "ON edge(v_out)")
    void createEdgeOutVertexIndex();

	/*
	 * Put all the columns we need for traversal into one index to save index
	 * merging which is costing us 100ms+ in AmberQuery when a vertex has
	 * a lot of outgoing edges.
	 */
	@SqlUpdate(
			"CREATE INDEX edge_in_traversal_idx "
			+ "ON edge(txn_end, v_in, label, edge_order, v_out)")
	void createEdgeInTraversalIndex();


	@SqlUpdate(
			"CREATE INDEX edge_out_traversal_idx "
			+ "ON edge(txn_end, v_out, label, edge_order, v_in)")
	void createEdgeOutTraversalIndex();


    @SqlUpdate(
            "CREATE INDEX sess_edge_idx "
            + "ON sess_edge(s_id)")
    void createSessionEdgeIndex();


    @SqlUpdate(
            "CREATE INDEX sess_vertex_idx "
            + "ON sess_vertex(s_id)")
    void createSessionVertexIndex();


    @SqlUpdate(
            "CREATE INDEX sess_property_idx "
            + "ON sess_property(s_id)")
    void createSessionPropertyIndex();


    @SqlUpdate(
            "CREATE INDEX sess_edge_sis_idx "
            + "ON sess_edge(s_id, id, state)")
    void createSessionEdgeIdStateIndex();


    @SqlUpdate(
            "CREATE INDEX sess_vertex_sis_idx "
            + "ON sess_vertex(s_id, id, state)")
    void createSessionVertexIdStateIndex();


    @SqlUpdate(
            "CREATE INDEX sess_property_sis_idx "
            + "ON sess_property(s_id, id)")
    void createSessionPropertyIdStateIndex();


    @SqlQuery(
            "SELECT (COUNT(table_name) >= 8) "
            + "FROM information_schema.tables "
            + "WHERE table_name IN ("
            + "  'VERTEX', 'EDGE', 'PROPERTY', "
            + "  'SESS_VERTEX', 'SESS_EDGE', 'SESS_PROPERTY', "
            + "  'ID_GENERATOR', 'TRANSACTION')")
    boolean schemaTablesExist();


    /*
     * id generation operations
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO id_generator () "
            + "VALUES ()")
    long newId();


    @SqlUpdate("DELETE "
            + "FROM id_generator "
            + "WHERE id < :id")
    void garbageCollectIds(
            @Bind("id") long id);


    /*
     * suspend/resume operations
     */
    @SqlBatch("INSERT INTO sess_edge (s_id, id, txn_start, txn_end, v_out, v_in, label, edge_order, state) "
            + "VALUES (:sessId, :id, :txnStart, :txnEnd, :outId, :inId, :label, :edgeOrder, :state)")
    void suspendEdges(
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
    void suspendVertices(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("txnStart")  List<Long>   txnStart,
            @Bind("txnEnd")    List<Long>   txnEnd,
            @Bind("state")     List<String> state);


    @SqlBatch("INSERT INTO sess_property (s_id, id, name, type, value) "
            + "VALUES (:sessId, :id, :name, :type, :value)")
    void suspendProperties(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("name")      List<String> name,
            @Bind("type")      List<String> type,
            @Bind("value")     List<byte[]> value);


    @SqlQuery("SELECT id, name, type, value "
            + "FROM sess_property "
            + "WHERE s_id = :sessId")
    @Mapper(PropertyMapper.class)
    List<AmberProperty> resumeProperties(@Bind("sessId") Long sessId);


    /* Transaction operations */


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = :id")
    @Mapper(TransactionMapper.class)
    AmberTransaction getTransaction(@Bind("id") Long id);


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
    List<AmberTransaction> getTransactionsByVertexId(@Bind("id") Long id);


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
    List<AmberTransaction> getTransactionsByEdgeId(@Bind("id") Long id);


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = ("
    		+ "  SELECT MIN(t.id) "
            + "  FROM transaction t, vertex v "
            + "  WHERE v.id = :id "
            + "  AND t.id = v.txn_start)")
    @Mapper(TransactionMapper.class)
    AmberTransaction getFirstTransactionForVertexId(@Bind("id") Long id);


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = ("
            + "  SELECT MIN(t.id) "
            + "  FROM transaction t, edge e "
            + "  WHERE e.id = :id "
            + "  AND t.id = e.txn_start)")
    @Mapper(TransactionMapper.class)
    AmberTransaction getFirstTransactionForEdgeId(@Bind("id") Long id);


    /* Note: resume edge and vertex implemented in AmberGraph */


    /*
     * commit operations
     */
    @SqlUpdate(
            "INSERT INTO transaction (id, time, user, operation)" +
            "VALUES (:id, :time, :user, :operation)")
    void insertTransaction(
            @Bind("id") long id,
            @Bind("time") long time,
            @Bind("user") String user,
            @Bind("operation") String operation);


    // The following query intentionally left blank. It's implemented in the db specific AmberDao sub classes (h2 or MySql)
    @SqlUpdate("")
    void endElements(
            @Bind("txnId") Long txnId);


    @SqlUpdate("SET @txn = :txnId;\n"

            // edges
            + "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
            + "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
            + "FROM sess_edge "
            + "WHERE s_id = @txn "
            + "AND state = 'NEW';\n"

            + "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
            + "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
            + "FROM sess_edge "
            + "WHERE s_id = @txn "
            + "AND state = 'MOD';\n"

            // vertices
            + "INSERT INTO vertex (id, txn_start, txn_end) "
            + "SELECT id, s_id, 0 "
            + "FROM sess_vertex "
            + "WHERE s_id = @txn "
            + "AND state = 'NEW';\n"

            + "INSERT INTO vertex (id, txn_start, txn_end) "
            + "SELECT id, s_id, 0 "
            + "FROM sess_vertex "
            + "WHERE s_id = @txn "
            + "AND state = 'MOD';\n"

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
    void startElements(
            @Bind("txnId") Long txnId);


    void close();


    @SqlUpdate("SET @sessId = :sessId;\n" +

            "DELETE FROM sess_vertex " +
            "WHERE s_id = @sessId;\n" +

            "DELETE FROM sess_edge " +
            "WHERE s_id = @sessId;\n" +

            "DELETE FROM sess_property " +
            "WHERE s_id = @sessId;\n")
    void clearSession(
            @Bind("sessId") Long sessId);


    @SqlBatch("INSERT INTO work (id, txn_start, txn_end, extent, dcmDateTimeUpdated, localSystemNumber, occupation, endDate, displayTitlePage, holdingId, hasRepresentation, totalDuration, dcmDateTimeCreated, firstPart, additionalTitle, dcmWorkPid, classification, commentsInternal, restrictionType, ilmsSentDateTime, subType, scaleEtc, startDate, dcmRecordUpdater, tilePosition, allowHighResdownload, south, restrictionsOnAccess, preservicaType, north, accessConditions, internalAccessConditions, eadUpdateReviewRequired, australianContent, moreIlmsDetailsRequired, rights, genre, deliveryUrl, recordSource, sheetCreationDate, creator, sheetName, coordinates, creatorStatement, additionalCreator, folderType, eventNote, interactiveIndexAvailable, startChild, bibLevel, holdingNumber, publicNotes, series, constraint1, notes, catalogueUrl, encodingLevel, materialFromMultipleSources, subject, sendToIlms, vendorId, allowOnsiteAccess, language, sensitiveMaterial, dcmAltPi, folderNumber, west, html, preservicaId, redocworksReason, workCreatedDuringMigration, author, commentsExternal, findingAidNote, collection, otherTitle, imageServerUrl, localSystemNo, acquisitionStatus, reorderType, immutable, copyrightPolicy, nextStep, publisher, additionalSeries, tempHolding, sortIndex, isMissingPage, standardId, representativeId, edition, reorder, title, acquisitionCategory, subUnitNo, expiryDate, digitalStatusDate, east, contributor, publicationCategory, ingestJobId, subUnitType, uniformTitle, alias, rdsAcknowledgementType, issueDate, bibId, coverage, summary, additionalContributor, sendToIlmsDateTime, sensitiveReason, carrier, form, rdsAcknowledgementReceiver, digitalStatus, dcmRecordCreator, sprightlyUrl, depositType, parentConstraint) "
            + "VALUES (:id, :txn_start, :txn_end, :extent, :dcmDateTimeUpdated, :localSystemNumber, :occupation, :endDate, :displayTitlePage, :holdingId, :hasRepresentation, :totalDuration, :dcmDateTimeCreated, :firstPart, :additionalTitle, :dcmWorkPid, :classification, :commentsInternal, :restrictionType, :ilmsSentDateTime, :subType, :scaleEtc, :startDate, :dcmRecordUpdater, :tilePosition, :allowHighResdownload, :south, :restrictionsOnAccess, :preservicaType, :north, :accessConditions, :internalAccessConditions, :eadUpdateReviewRequired, :australianContent, :moreIlmsDetailsRequired, :rights, :genre, :deliveryUrl, :recordSource, :sheetCreationDate, :creator, :sheetName, :coordinates, :creatorStatement, :additionalCreator, :folderType, :eventNote, :interactiveIndexAvailable, :startChild, :bibLevel, :holdingNumber, :publicNotes, :series, :constraint1, :notes, :catalogueUrl, :encodingLevel, :materialFromMultipleSources, :subject, :sendToIlms, :vendorId, :allowOnsiteAccess, :language, :sensitiveMaterial, :dcmAltPi, :folderNumber, :west, :html, :preservicaId, :redocworksReason, :workCreatedDuringMigration, :author, :commentsExternal, :findingAidNote, :collection, :otherTitle, :imageServerUrl, :localSystemNo, :acquisitionStatus, :reorderType, :immutable, :copyrightPolicy, :nextStep, :publisher, :additionalSeries, :tempHolding, :sortIndex, :isMissingPage, :standardId, :representativeId, :edition, :reorder, :title, :acquisitionCategory, :subUnitNo, :expiryDate, :digitalStatusDate, :east, :contributor, :publicationCategory, :ingestJobId, :subUnitType, :uniformTitle, :alias, :rdsAcknowledgementType, :issueDate, :bibId, :coverage, :summary, :additionalContributor, :sendToIlmsDateTime, :sensitiveReason, :carrier, :form, :rdsAcknowledgementReceiver, :digitalStatus, :dcmRecordCreator, :sprightlyUrl, :depositType, :parentConstraint)")
	void createWorks(@BindBean List<Work> work);

    @SqlBatch("DELETE FROM work WHERE id = :id")
    void deleteWorks(@BindBean List<Work> work);

    @SqlBatch("UPDATE work set  txn_start = :txn_start, txn_end = :txn_end, extent = :extent, dcmDateTimeUpdated = :dcmDateTimeUpdated, localSystemNumber = :localSystemNumber, occupation = :occupation, endDate = :endDate, displayTitlePage = :displayTitlePage, holdingId = :holdingId, hasRepresentation = :hasRepresentation, totalDuration = :totalDuration, dcmDateTimeCreated = :dcmDateTimeCreated, firstPart = :firstPart, additionalTitle = :additionalTitle, dcmWorkPid = :dcmWorkPid, classification = :classification, commentsInternal = :commentsInternal, restrictionType = :restrictionType, ilmsSentDateTime = :ilmsSentDateTime, subType = :subType, scaleEtc = :scaleEtc, startDate = :startDate, dcmRecordUpdater = :dcmRecordUpdater, tilePosition = :tilePosition, allowHighResdownload = :allowHighResdownload, south = :south, restrictionsOnAccess = :restrictionsOnAccess, preservicaType = :preservicaType, north = :north, accessConditions = :accessConditions, internalAccessConditions = :internalAccessConditions, eadUpdateReviewRequired = :eadUpdateReviewRequired, australianContent = :australianContent, moreIlmsDetailsRequired = :moreIlmsDetailsRequired, rights = :rights, genre = :genre, deliveryUrl = :deliveryUrl, recordSource = :recordSource, sheetCreationDate = :sheetCreationDate, creator = :creator, sheetName = :sheetName, coordinates = :coordinates, creatorStatement = :creatorStatement, additionalCreator = :additionalCreator, folderType = :folderType, eventNote = :eventNote, interactiveIndexAvailable = :interactiveIndexAvailable, startChild = :startChild, bibLevel = :bibLevel, holdingNumber = :holdingNumber, publicNotes = :publicNotes, series = :series, constraint1 = :constraint1, notes = :notes, catalogueUrl = :catalogueUrl, encodingLevel = :encodingLevel, materialFromMultipleSources = :materialFromMultipleSources, subject = :subject, sendToIlms = :sendToIlms, vendorId = :vendorId, allowOnsiteAccess = :allowOnsiteAccess, language = :language, sensitiveMaterial = :sensitiveMaterial, dcmAltPi = :dcmAltPi, folderNumber = :folderNumber, west = :west, html = :html, preservicaId = :preservicaId, redocworksReason = :redocworksReason, workCreatedDuringMigration = :workCreatedDuringMigration, author = :author, commentsExternal = :commentsExternal, findingAidNote = :findingAidNote, collection = :collection, otherTitle = :otherTitle, imageServerUrl = :imageServerUrl, localSystemNo = :localSystemNo, acquisitionStatus = :acquisitionStatus, reorderType = :reorderType, immutable = :immutable, copyrightPolicy = :copyrightPolicy, nextStep = :nextStep, publisher = :publisher, additionalSeries = :additionalSeries, tempHolding = :tempHolding, sortIndex = :sortIndex, isMissingPage = :isMissingPage, standardId = :standardId, representativeId = :representativeId, edition = :edition, reorder = :reorder, title = :title, acquisitionCategory = :acquisitionCategory, subUnitNo = :subUnitNo, expiryDate = :expiryDate, digitalStatusDate = :digitalStatusDate, east = :east, contributor = :contributor, publicationCategory = :publicationCategory, ingestJobId = :ingestJobId, subUnitType = :subUnitType, uniformTitle = :uniformTitle, alias = :alias, rdsAcknowledgementType = :rdsAcknowledgementType, issueDate = :issueDate, bibId = :bibId, coverage = :coverage, summary = :summary, additionalContributor = :additionalContributor, sendToIlmsDateTime = :sendToIlmsDateTime, sensitiveReason = :sensitiveReason, carrier = :carrier, form = :form, rdsAcknowledgementReceiver = :rdsAcknowledgementReceiver, digitalStatus = :digitalStatus, dcmRecordCreator = :dcmRecordCreator, sprightlyUrl = :sprightlyUrl, depositType = :depositType, parentConstraint = :parentConstraint " +
    		  "WHERE id = :id")
	void updateWorks(@BindBean List<Work> work);

}

