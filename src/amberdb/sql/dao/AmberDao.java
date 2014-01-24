package amberdb.sql.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.sql.AmberProperty;
import amberdb.sql.map.PropertyMapper;
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
            "CREATE UNIQUE INDEX edge_in_idx "
            + "ON edge(v_in)")
    void createEdgeInVertexIndex();
    
    @SqlUpdate(
            "CREATE UNIQUE INDEX edge_out_idx "
            + "ON edge(v_out)")
    void createEdgeOutVertexIndex();

	@SqlQuery(
			"SELECT (COUNT(table_name) = 8) "
			+ "FROM information_schema.tables " 
			+ "WHERE table_name IN ("
			+ "  'VERTEX', 'EDGE', 'PROPERTY', "
			+ "  'SESS_VERTEX', 'SESS_EDGE', 'SESS_PROPERTY', "
			+ "  'ID_GENERATOR', 'TRANSACTION')")
    boolean schemaTablesExist();
    
	@SqlUpdate(
			"DROP TABLE IF EXISTS "
			+ "vertex, edge, property, "
			+ "sess_vertex, sess_edge, sess_property, "
			+ "transaction, id_generator")
    void dropTables();

    
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
    		@Bind("sessId")    Long         sessId,
    		@Bind("id")        List<Long>   id,
    		@Bind("txnStart")  List<Long>   txnStart,
    		@Bind("txnEnd")    List<Long>   txnEnd,
    		@Bind("outId")     List<Long>   outId,
    		@Bind("inId")      List<Long>   inId,
            @Bind("label")     List<String> label,
    		@Bind("edgeOrder") List<Long>   edgeOrder,
            @Bind("state")     List<String> state);

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
    
    /* Note: resume edge and vertex implemented in AmberGraph
    
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

    @SqlUpdate("SET @txn = :txnId;\n"
// == CHANGE FOR MYSQL ==    		
    		// edges    		
    		+ "UPDATE edge e "
    		+ "SET txn_end = @txn "
    		+ "WHERE e.txn_end = 0 "
    		+ "AND e.id IN ("
    		+ "  SELECT id "
    		+ "  FROM sess_edge "
    		+ "  WHERE s_id = @txn "
    		+ "  AND state <> 'NEW');\n"

    		// edge properties
			+ "UPDATE property p "
			+ "SET txn_end = @txn "
			+ "WHERE p.txn_end = 0 "
			+ "AND p.id IN ("
			+ "  SELECT id "
			+ "  FROM sess_edge "
			+ "  WHERE s_id = @txn "
			+ "  AND state <> 'NEW');\n"
    		
			// vertices
    		+ "UPDATE vertex v "
    		+ "SET txn_end = @txn "
    		+ "WHERE v.txn_end = 0 "
    		+ "AND v.id IN ("
    		+ "  SELECT id "
    		+ "  FROM sess_vertex "
    		+ "  WHERE s_id = @txn "
    		+ "  AND state <> 'NEW');\n"

    		// vertex properties
			+ "UPDATE property p "
			+ "SET txn_end = @txn "
			+ "WHERE p.txn_end = 0 "
			+ "AND p.id IN ("
			+ "  SELECT id "
			+ "  FROM sess_vertex "
			+ "  WHERE s_id = @txn "
			+ "  AND state <> 'NEW');\n"
			
			// orphan edges
			+ "UPDATE edge e "
			+ "SET txn_end = @txn "
			+ "WHERE e.txn_end = 0 "
			+ "AND e.v_in IN ("
			+ "  SELECT id "
			+ "  FROM sess_vertex "
			+ "  WHERE state = 'DEL' "
			+ "  AND s_id = @txn);\n"

			+ "UPDATE edge e "
			+ "SET txn_end = @txn "
			+ "WHERE e.txn_end = 0 "
			+ "AND e.v_out IN ("
			+ "  SELECT id "
			+ "  FROM sess_vertex "
			+ "  WHERE state = 'DEL' "
			+ "  AND s_id = @txn);\n")
    void endElements(
            @Bind("txnId") Long txnId);

    @SqlUpdate("SET @txn = :txnId;\n"
    		
    		// edges    		
    		+ "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
    		+ "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
    		+ "FROM sess_edge "
    		+ "WHERE s_id = @txn "
    		+ "AND (state = 'NEW' OR state = 'MOD');\n"

    		// vertices    		
    		+ "INSERT INTO vertex (id, txn_start, txn_end) "
    		+ "SELECT id, s_id, 0 "
    		+ "FROM sess_vertex "
    		+ "WHERE s_id = @txn "
    		+ "AND (state = 'NEW' OR state = 'MOD');\n"

    		// properties    		
    		+ "INSERT INTO property (id, txn_start, txn_end, name, type, value) "
    		+ "SELECT id, s_id, 0, name, type, value "
    		+ "FROM sess_property "
    		+ "WHERE s_id = @txn") 
    void startElements(
            @Bind("txnId") Long txnId);
    
//    @SqlUpdate(
//            "INSERT INTO stage_vertex (id, txn_start, state, txn_new) " +
//            "VALUES (:id, :txn_start, :state, :txn_new)")
//    long insertStageVertex(
//            @BindAmberVertex AmberVertex vertex, 
//            @Bind("txn_new") long txnId);
//
//    @SqlUpdate(
//            "INSERT INTO stage_edge (id, txn_start, txn_new, v_out, v_in, label, edge_order, state) " +
//            "VALUES (:id, :txn_start, :txn_new, :v_out, :v_in, :label, :edge_order, :state)")
//    long insertStageEdge(
//            @BindAmberEdge AmberEdge edge, 
//            @Bind("txn_new") long txnId);
//    
//    @SqlUpdate(
//            "INSERT INTO stage_property (id, name, type, value, txn_new) " +
//            "VALUES (:id, :name, :type, :value, :txn_new)")
//    long insertStageProperty(
//            @BindAmberProperty AmberProperty property, 
//            @Bind("txn_new") long txnId);
//    
//    @SqlQuery(
//            "SELECT v.id, v.txn_end " +
//            "FROM vertex v, stage_vertex s " +
//            "WHERE s.txn_new = :txnId " +
//            "AND s.id = v.id " +
//            "AND v.txn_end > 0 " +
//            "AND v.txn_end IS NOT NULL " +
//            "AND v.txn_end > s.txn_start " +  // this clause may not be needed
//            "UNION " +
//            "SELECT e.id, e.txn_end " +
//            "FROM edge e, stage_edge s " +
//            "WHERE s.txn_new = :txnId " +
//            "AND s.id = e.id " +
//            "AND e.txn_end > 0 " +
//            "AND e.txn_end IS NOT NULL " +
//            "AND e.txn_end > s.txn_start ")  // this clause may not be needed
//    @Mapper(LongArrayMapper.class)
//    List<Long[]> findDeletionMutations(
//            @Bind("txnId") long txnId);
//    
//    @SqlQuery(
//            "SELECT v.id, v.txn_start " +
//            "FROM vertex v, stage_vertex s " +
//            "WHERE s.txn_new = :txnId " +
//            "AND s.id = v.id " +
//            "AND v.txn_start > s.txn_start " +
//            "UNION " +
//            "SELECT e.id, e.txn_end " +
//            "FROM edge e, stage_edge s " +
//            "WHERE s.txn_new = :txnId " +
//            "AND s.id = e.id " +
//            "AND e.txn_start > s.txn_start ")
//    @Mapper(LongArrayMapper.class)
//    List<Long[]> findAlterationMutations(
//            @Bind("txnId") long txnId);
//    
//    @SqlUpdate(
//            "INSERT INTO vertex (id, txn_start) " +
//            "SELECT id, txn_new " +
//            "FROM stage_vertex " +
//            "WHERE state <> 'DEL' " +
//            "AND txn_new = :txnId")
//    int insertStagedVertices(
//            @Bind("txnId") long txnId);
//
//    @SqlUpdate(
//            "INSERT INTO edge (id, txn_start, v_out, v_in, label, edge_order) " +
//            "SELECT id, txn_new, v_out, v_in, label, edge_order " +
//            "FROM stage_edge " +
//            "WHERE state <> 'DEL' " +
//            "AND txn_new = :txnId")
//    int insertStagedEdges(
//            @Bind("txnId") long txnId);
//    
//    @SqlUpdate(
//            "UPDATE transaction " +
//            "SET commit = :commitMarker " +
//            "WHERE id = :txnId")
//    int commitTransaction(
//            @Bind("txnId") long txnId,
//            @Bind("commitMarker") long commitMarker);
//    
//    
//    @SqlQuery(
//            "SELECT id, name, type, value " + 
//            "FROM property " +
//            "WHERE id = :id " +
//            "AND (txn_end = 0 OR txn_end IS NULL)")
//    @Mapper(PersistentPropertyMapper.class)
//    List<AmberProperty>
//            getProperties(@Bind("id") long id);
//    
//    @SqlQuery(
//            "SELECT v.id, v.txn_start, v.txn_end " + 
//            "FROM vertex v, edge e " +
//            "WHERE v.id = e.v_in " +
//            "AND v_out = :vertexId " +
//            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
//            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberVertex> findOutVertices(
//            @Bind("vertexId") long vertexId);
//
//    @SqlQuery(
//            "SELECT v.id, v.txn_start, v.txn_end " + 
//            "FROM vertex v, edge e " +
//            "WHERE v.id = e.v_out " +
//            "AND v_in = :vertexId " +
//            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
//            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberVertex> findInVertices(
//            @Bind("vertexId") long vertexId);
//    
//    @SqlQuery(
//            "SELECT v.id, v.txn_start, v.txn_end " +
//            "FROM vertex v, edge e " +
//            "WHERE v.id = e.v_in " +
//            "AND v_out = :vertexId " +
//            "AND label = :label " +
//            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
//            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberVertex> findOutVertices(
//            @Bind("vertexId") long vertexId,
//            @Bind("label") String label);
//
//    @SqlQuery(
//            "SELECT v.id, v.txn_start, v.txn_end " +
//            "FROM vertex v, edge e " +
//            "WHERE v.id = e.v_out " +
//            "AND v_in = :vertexId " +
//            "AND label = :label " +
//            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
//            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberVertex> findInVertices(
//            @Bind("vertexId") long vertexId,
//            @Bind("label") String label);
//
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_out = :vertexId " +
//            "AND label = :label " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
//            "AND (txn_end = 0 OR txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberEdge>
//    findOutEdges(
//            @Bind("vertexId") long vertexId, 
//            @Bind("label") String label);
//
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_out = :vertexId " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
//            "AND (txn_end = 0 OR txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberEdge>
//    findOutEdges(
//            @Bind("vertexId") long vertexId);
//
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_in = :vertexId " +
//            "AND label = :label " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
//            "AND (txn_end = 0 OR txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberEdge>
//    findInEdges(
//            @Bind("vertexId") long vertexId, 
//            @Bind("label") String label);
//
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_in = :vertexId " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
//            "AND (txn_end = 0 OR txn_end IS NULL) " +
//            "ORDER BY edge_order")
//    Iterator<AmberEdge>
//    findInEdges(
//            @Bind("vertexId") long vertexId);
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE (txn_end = 0 OR txn_end IS NULL) " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
//    Iterator<AmberEdge> findEdges();
//    
//    @SqlQuery(
//            "SELECT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order " +
//            "FROM edge e, property p " +
//            "WHERE e.id = p.id " +
//            "AND e.txn_start = p.txn_start " +
//            "AND (e.txn_end = 0 OR e.txn_end IS NULL) " +
//            "AND (e.txn_start > 0 OR e.txn_start IS NOT NULL) " +
//            "AND p.name = :name " +
//            "AND p.value = :value " +
//            "ORDER BY e.edge_order")
//    Iterator<AmberEdge> findEdgesWithProperty(
//            @Bind("name") String name,
//            @Bind("value") byte[] value);
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end " +
//            "FROM vertex " +
//            "WHERE (txn_end = 0 OR txn_end IS NULL) " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
//    Iterator<AmberVertex> findVertices();
//
//    @SqlQuery(
//            "SELECT v.id, v.txn_start, v.txn_end " +
//            "FROM vertex v, property p " +
//            "WHERE v.id = p.id " +
//            "AND v.txn_start = p.txn_start " +
//            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
//            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
//            "AND p.name = :name " +
//            "AND p.value = :value")
//    Iterator<AmberVertex> findVerticesWithProperty(
//            @Bind("name") String name,
//            @Bind("value") byte[] value);
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE id = :id " +
//            "AND (txn_end = 0 OR txn_end IS NULL) " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
//    AmberEdge findEdge(
//            @Bind("id") long id);
//
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end " +
//            "FROM vertex " +
//            "WHERE id = :id " +
//            "AND (txn_end = 0 OR txn_end IS NULL) " +
//            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
//    AmberVertex findVertex(
//            @Bind("id") long id);
//    
//    /*
//     * methods overridden by db specific (MySql or H2) extending interfaces
//     */
//    @SqlUpdate("")
//    int insertStagedEdgeProperties(
//            @Bind("txnId") long txnId);
//
//    @SqlUpdate("")
//    int insertStagedVertexProperties(
//            @Bind("txnId") long txnId);
//
//    @SqlUpdate("")
//    int updateSupersededVertices(
//            @Bind("txnId") long txnId);
//    
//    @SqlUpdate("")
//    int updateSupersededEdges(
//            @Bind("txnId") long txnId);
//
//    @SqlUpdate("")
//    int updateSupersededEdgeProperties(@Bind("txnId") long txnId);
//    
//    @SqlUpdate("")
//    int updateSupersededVertexProperties(@Bind("txnId") long txnId);


    void close();


}

