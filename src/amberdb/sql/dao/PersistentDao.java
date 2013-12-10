package amberdb.sql.dao;

import java.util.Iterator;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.sql.AmberEdge;
import amberdb.sql.AmberProperty;
import amberdb.sql.AmberVertex;
import amberdb.sql.bind.BindAmberEdge;
import amberdb.sql.bind.BindAmberProperty;
import amberdb.sql.bind.BindAmberVertex;
import amberdb.sql.map.LongArrayMapper;
import amberdb.sql.map.PersistentPropertyMapper;

public interface PersistentDao extends Transactional<PersistentDao> {

    /*
     * DB creation operations (DDL)
     */
    
    /*
     * Main tables
     */
    @SqlUpdate(
    		"CREATE TABLE IF NOT EXISTS vertex (" +
    		"id         BIGINT, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT)")
    void createVertexTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge (" +
    		"id         BIGINT, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"v_out      BIGINT, " +
    		"v_in       BIGINT, " +
    		"label      VARCHAR(100), " +
    		"edge_order BIGINT)")
    void createEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property (" +
    		"id        BIGINT, " +
            "txn_start BIGINT, " +
            "txn_end   BIGINT, " +
    		"name      VARCHAR(100), " +
    		"type      CHAR(3), " +
            "value     BLOB)")
    void createPropertyTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS id_generator (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT)")
    void createIdGeneratorTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS transaction (" +
            "id       BIGINT UNIQUE, " +
            "commit   BIGINT UNIQUE, " +
            "user     VARCHAR(100), " +
            "operation TEXT)")
    void createTransactionTable();

    /*
     * Main table indexes - these require review.
     */
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_vert " +
            "ON vertex(id, txn_start)")
    void createVertexIndex();
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_edge " +
            "ON edge(id, txn_start)")
    void createEdgeIndex();
    @SqlUpdate(
            "CREATE UNIQUE INDEX e_unique_prop " +
            "ON property(id, txn_start, name)")
    void createPropertyIndex();

    // Clean up DDL
    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, property, transaction")
    void dropTables();

    /*
     * Element staging tables (no indexes defined yet - needs review)
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS stage_vertex (" +
            "id         BIGINT, " +
            "txn_start  BIGINT, " +
            "txn_new    BIGINT, " +
            "state      CHAR(3))")
    void createStagingVertexTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS stage_edge (" +
            "id         BIGINT, " +
            "txn_start  BIGINT, " +
            "txn_new    BIGINT, " +
            "v_out      BIGINT, " +
            "v_in       BIGINT, " +
            "label      VARCHAR(100), " +
            "edge_order BIGINT, " +
            "state      CHAR(3))")
    void createStagingEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS stage_property (" +
            "id        BIGINT, " +
            "txn_new   BIGINT, " +
            "name      VARCHAR(100), " +
            "type      CHAR(3), " +
            "value     BLOB)")
    void createStagingPropertyTable();
    
    /*
     * id generation operations
     */
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO id_generator () " + 
            "VALUES ()")
    long newId();

    @SqlUpdate(
            "DELETE " +
            "FROM id_generator " +
            "WHERE id < :id")
    void garbageCollectIds(
            @Bind("id") long id);
    
    @SqlUpdate(
            "INSERT INTO stage_vertex (id, txn_start, state, txn_new) " +
            "VALUES (:id, :txn_start, :state, :txn_new)")
    long insertStageVertex(
            @BindAmberVertex AmberVertex vertex, 
            @Bind("txn_new") long txnId);

    @SqlUpdate(
            "INSERT INTO stage_edge (id, txn_start, txn_new, v_out, v_in, label, edge_order, state) " +
            "VALUES (:id, :txn_start, :txn_new, :v_out, :v_in, :label, :edge_order, :state)")
    long insertStageEdge(
            @BindAmberEdge AmberEdge edge, 
            @Bind("txn_new") long txnId);
    
    @SqlUpdate(
            "INSERT INTO stage_property (id, name, type, value, txn_new) " +
            "VALUES (:id, :name, :type, :value, :txn_new)")
    long insertStageProperty(
            @BindAmberProperty AmberProperty property, 
            @Bind("txn_new") long txnId);
    
    @SqlQuery(
            "SELECT v.id, v.txn_end " +
            "FROM vertex v, stage_vertex s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = v.id " +
            "AND v.txn_end > 0 " +
            "AND v.txn_end IS NOT NULL " +
            "AND v.txn_end > s.txn_start " +  // this clause may not be needed
            "UNION " +
            "SELECT e.id, e.txn_end " +
            "FROM edge e, stage_edge s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = e.id " +
            "AND e.txn_end > 0 " +
            "AND e.txn_end IS NOT NULL " +
            "AND e.txn_end > s.txn_start ")  // this clause may not be needed
    @Mapper(LongArrayMapper.class)
    List<Long[]> findDeletionMutations(
            @Bind("txnId") long txnId);
    
    @SqlQuery(
            "SELECT v.id, v.txn_start " +
            "FROM vertex v, stage_vertex s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = v.id " +
            "AND v.txn_start > s.txn_start " +
            "UNION " +
            "SELECT e.id, e.txn_end " +
            "FROM edge e, stage_edge s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = e.id " +
            "AND e.txn_start > s.txn_start ")
    @Mapper(LongArrayMapper.class)
    List<Long[]> findAlterationMutations(
            @Bind("txnId") long txnId);
    
    @SqlUpdate(
            "INSERT INTO vertex (id, txn_start) " +
            "SELECT id, txn_new " +
            "FROM stage_vertex " +
            "WHERE state <> 'DEL' " +
            "AND txn_new = :txnId")
    int insertStagedVertices(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "INSERT INTO edge (id, txn_start, v_out, v_in, label, edge_order) " +
            "SELECT id, txn_new, v_out, v_in, label, edge_order " +
            "FROM stage_edge " +
            "WHERE state <> 'DEL' " +
            "AND txn_new = :txnId")
    int insertStagedEdges(
            @Bind("txnId") long txnId);
    
    @SqlUpdate(
            "UPDATE transaction " +
            "SET commit = :commitMarker " +
            "WHERE id = :txnId")
    int commitTransaction(
            @Bind("txnId") long txnId,
            @Bind("commitMarker") long commitMarker);
    
    
    @SqlQuery(
            "SELECT id, name, type, value " + 
            "FROM property " +
            "WHERE id = :id " +
            "AND (txn_end = 0 OR txn_end IS NULL)")
    @Mapper(PersistentPropertyMapper.class)
    List<AmberProperty>
            getProperties(@Bind("id") long id);
    
    @SqlQuery(
            "SELECT v.id, v.txn_start, v.txn_end " + 
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_in " +
            "AND v_out = :vertexId " +
            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberVertex> findOutVertices(
            @Bind("vertexId") long vertexId);

    @SqlQuery(
            "SELECT v.id, v.txn_start, v.txn_end " + 
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND v_in = :vertexId " +
            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberVertex> findInVertices(
            @Bind("vertexId") long vertexId);
    
    @SqlQuery(
            "SELECT v.id, v.txn_start, v.txn_end " +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_in " +
            "AND v_out = :vertexId " +
            "AND label = :label " +
            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberVertex> findOutVertices(
            @Bind("vertexId") long vertexId,
            @Bind("label") String label);

    @SqlQuery(
            "SELECT v.id, v.txn_start, v.txn_end " +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND v_in = :vertexId " +
            "AND label = :label " +
            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberVertex> findInVertices(
            @Bind("vertexId") long vertexId,
            @Bind("label") String label);

    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND label = :label " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
            "AND (txn_end = 0 OR txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberEdge>
    findOutEdges(
            @Bind("vertexId") long vertexId, 
            @Bind("label") String label);

    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
            "AND (txn_end = 0 OR txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberEdge>
    findOutEdges(
            @Bind("vertexId") long vertexId);

    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
            "AND (txn_end = 0 OR txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberEdge>
    findInEdges(
            @Bind("vertexId") long vertexId, 
            @Bind("label") String label);

    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL) " +
            "AND (txn_end = 0 OR txn_end IS NULL) " +
            "ORDER BY edge_order")
    Iterator<AmberEdge>
    findInEdges(
            @Bind("vertexId") long vertexId);
    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE (txn_end = 0 OR txn_end IS NULL) " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
    Iterator<AmberEdge> findEdges();
    
    @SqlQuery(
            "SELECT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order " +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND e.txn_start = p.txn_start " +
            "AND (e.txn_end = 0 OR e.txn_end IS NULL) " +
            "AND (e.txn_start > 0 OR e.txn_start IS NOT NULL) " +
            "AND p.name = :name " +
            "AND p.value = :value " +
            "ORDER BY e.edge_order")
    Iterator<AmberEdge> findEdgesWithProperty(
            @Bind("name") String name,
            @Bind("value") byte[] value);
    
    @SqlQuery(
            "SELECT id, txn_start, txn_end " +
            "FROM vertex " +
            "WHERE (txn_end = 0 OR txn_end IS NULL) " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
    Iterator<AmberVertex> findVertices();

    @SqlQuery(
            "SELECT v.id, v.txn_start, v.txn_end " +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND v.txn_start = p.txn_start " +
            "AND (v.txn_end = 0 OR v.txn_end IS NULL) " +
            "AND (v.txn_start > 0 OR v.txn_start IS NOT NULL) " +
            "AND p.name = :name " +
            "AND p.value = :value")
    Iterator<AmberVertex> findVerticesWithProperty(
            @Bind("name") String name,
            @Bind("value") byte[] value);
    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE id = :id " +
            "AND (txn_end = 0 OR txn_end IS NULL) " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
    AmberEdge findEdge(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT id, txn_start, txn_end " +
            "FROM vertex " +
            "WHERE id = :id " +
            "AND (txn_end = 0 OR txn_end IS NULL) " +
            "AND (txn_start > 0 OR txn_start IS NOT NULL)")
    AmberVertex findVertex(
            @Bind("id") long id);
    
    /*
     * methods overridden by db specific (MySql or H2) extending interfaces
     */
    @SqlUpdate("")
    int insertStagedEdgeProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate("")
    int insertStagedVertexProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate("")
    int updateSupercededVertices(
            @Bind("txnId") long txnId);
    
    @SqlUpdate("")
    int updateSupercededEdges(
            @Bind("txnId") long txnId);

    @SqlUpdate("")
    int updateSupercededEdgeProperties(@Bind("txnId") long txnId);
    
    @SqlUpdate("")
    int updateSupercededVertexProperties(@Bind("txnId") long txnId);

    @SqlQuery("")
    boolean schemaTablesExist();

    void close();
}

