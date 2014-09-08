package amberdb.graph.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.graph.AmberEdgeWithState;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberTransaction;
import amberdb.graph.AmberVertexWithState;
import amberdb.graph.EdgeMapper;
import amberdb.graph.VertexMapper;
import amberdb.graph.PropertyMapper;
import amberdb.graph.TransactionMapper;


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
            "CREATE INDEX edge_in_idx "
            + "ON edge(v_in)")
    void createEdgeInVertexIndex();

    
    @SqlUpdate(
            "CREATE INDEX edge_out_idx "
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
            + "ORDER BY t.id")
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
            + "ORDER BY t.id")
    @Mapper(TransactionMapper.class)
    List<AmberTransaction> getTransactionsByEdgeId(@Bind("id") Long id);
    
    
    @SqlQuery("(SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' "
            + "FROM transaction t, vertex v "
            + "WHERE t.id = :id "
            + "AND v.txn_start = t.id) "
            + "UNION "
            + "(SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' "
            + "FROM transaction t, vertex v "
            + "WHERE t.id = :id "
            + "AND v.txn_end = t.id) "
            + "ORDER BY t.id")
    @Mapper(VertexMapper.class)
    List<AmberVertexWithState> getVerticesByTransactionId(@Bind("id") Long id);
    

    @SqlQuery("(SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' "
            + "FROM transaction t, edge e "
            + "WHERE t.id = :id "
            + "AND e.txn_start = t.id) "
            + "UNION "
            + "(SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' "
            + "FROM transaction t, edge e "
            + "WHERE t.id = :id "
            + "AND e.txn_end = t.id) "
            + "ORDER BY t.id")
    @Mapper(EdgeMapper.class)
    List<AmberEdgeWithState> getEdgesByTransactionId(@Bind("id") Long id);


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
            + "SELECT id, s_id, 0, name, type, value "
            + "FROM sess_property "
            + "WHERE s_id = @txn") 
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

}

