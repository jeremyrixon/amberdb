package amberdb.sql.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.sql.AmberProperty;
import amberdb.sql.Lookups;
import amberdb.sql.PropertyMapper;


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
     * Lookup table - stores lists of objects. for example types of tape
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS list ("
            + "name      VARCHAR(100), " 
            + "value     VARCHAR(100), " 
            + "deleted   VARCHAR(1) )")
    void createListTable();

    
    
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

        
    @SqlUpdate("")
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
    
    
    @CreateSqlObject
    public abstract Lookups lookups();
}

