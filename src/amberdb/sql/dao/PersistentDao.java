package amberdb.sql.dao;

import java.util.Iterator;
import java.util.List;

import  amberdb.sql.*;
import amberdb.sql.bind.BindAmberEdge;
import amberdb.sql.bind.BindAmberProperty;
import amberdb.sql.bind.BindAmberVertex;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

public interface PersistentDao extends Transactional<PersistentDao> {

    static final String edgeFields = " id, txn_start, txn_end, v_out, v_in, label, edge_order ";
    static final String edgeFieldsE = " e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order ";
    static final String edgeFieldSymbols = " :id, :txn_start, :txn_end, :v_out, :v_in, :label, :edge_order ";

    static final String vertexFields = " id, txn_start, txn_end ";
    static final String vertexFieldsV = " v.id, v.txn_start, v.txn_end ";
    static final String vertexFieldSymbols = " :id, :txn_start, :txn_end ";
    
    static final String propFields = " id, txn_start, txn_end, name, type, b_value, s_value, i_value, d_value ";
    static final String propFieldsP = " p.id, p.txn_start, p.txn_end, p.name, p.type, p.b_value, p.s_value, p.i_value, p.d_value ";
    static final String propFieldSymbols = " :id, :txn_start, :txn_end, :name, :type, :b_value, :s_value, :i_value, :d_value ";

    // Staging table fields
    static final String stageVertexFields = " id, txn_start, txn_new, state ";
    static final String stageVertexFieldsV = " v.id, v.txn_start, v.txn_new, v.state ";
    static final String stageVertexFieldSymbols = " :id, :txn_start, :txn_new, :state ";

    static final String stageEdgeFields = " id, txn_start, txn_new, v_out, v_in, label, edge_order, state ";
    static final String stageEdgeFieldsE = " e.id, e.txn_start, e.txn_new, e.v_out, e.v_in, e.label, e.edge_order, e.state ";
    static final String stageEdgeFieldSymbols = " :id, :txn_start, :txn_new, :v_out, :v_in, :label, :edge_order, :state ";
 
    static final String stagePropFields = " id, txn_new, name, type, b_value, s_value, i_value, d_value ";
    static final String stagePropFieldsP = " p.id, p.txn_new, p.name, p.type, p.b_value, p.s_value, p.i_value, p.d_value ";
    static final String stagePropFieldSymbols = " :id, :txn_new, :name, :type, :b_value, :s_value, :i_value, :d_value ";

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
    		"type      CHAR(1), " +
            "b_value   TINYINT(1), " +
            "s_value   TEXT, " +
            "i_value   BIGINT, " +
            "d_value   DOUBLE)")
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
            "state      TINYINT(1))")
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
            "state      TINYINT(1))")
    void createStagingEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS stage_property (" +
            "id        BIGINT, " +
            "txn_new   BIGINT, " +
            "name      VARCHAR(100), " +
            "type      CHAR(1), " +
            "b_value   TINYINT(1), " +
            "s_value   TEXT, " +
            "i_value   BIGINT, " +
            "d_value   DOUBLE)")
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
    
    /*
     * stage properties
     */
    @SqlUpdate(
            "INSERT INTO property (id, txn_start, txn_end, name, type, i_value) " +
            "VALUES (:id, :txn_start, :txn_end, :name, :type, :value)")
    void createIntegerProperty(
            @Bind("id")        long elementId, 
            @Bind("txn_start") long txnStart, 
            @Bind("txn_end")   long txnEnd, 
            @Bind("name")      String name,
            @Bind("type")      String type,
            @Bind("value")     Integer value);
    
    @SqlUpdate(
            "INSERT INTO property (id, txn_start, txn_end, name, type, b_value) " +
            "VALUES (:id, :txn_start, :txn_end, :name, :type, :value)")
    void createBooleanProperty(
            @Bind("id")        long elementId, 
            @Bind("txn_start") long txnStart, 
            @Bind("txn_end")   long txnEnd, 
            @Bind("name")      String name, 
            @Bind("type")      String type,
            @Bind("value")     Boolean value);
    
    @SqlUpdate(
            "INSERT INTO property (id, txn_start, txn_end, name, type, d_value) " +
            "VALUES (:id, :txn_start, :txn_end, :name, :type, :value)")
    void createDoubleProperty(
            @Bind("id")        long elementId, 
            @Bind("txn_start") long txnStart, 
            @Bind("txn_end")   long txnEnd, 
            @Bind("name")      String name, 
            @Bind("type")      String type,
            @Bind("value")     Double value);
    
    @SqlUpdate(
            "INSERT INTO property (id, txn_start, txn_end, name, type, s_value) " +
            "VALUES (:id, :txn_start, :txn_end, :name, :type, :value)")
    void createStringProperty(
            @Bind("id")        long elementId, 
            @Bind("txn_start") long txnStart, 
            @Bind("txn_end")   long txnEnd, 
            @Bind("name")      String name, 
            @Bind("type")      String type,
            @Bind("value")     String value);
    
 
    /*
     * Deleting things - cleaning up staging tables to go here 
     */
    
    /*
     * Find Operations
     */
    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE id = :id " +
            "AND txn_end = 0 " +
            "txn_start > 0")
    @Mapper(PersistentVertexMapper.class)
    AmberVertex
            findVertex(@Bind("id") long id);

    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE txn_end = 0 " +
            "txn_start > 0")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex>
            findVertices();
    
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE id = :id " +
            "AND txn_end = 0 " +
            "txn_start > 0")
    @Mapper(PersistentEdgeMapper.class)
    AmberEdge
            findEdge(@Bind("id") long id);

    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE txn_end = 0 " +
            "txn_start > 0")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findEdges();
    
    @SqlQuery(
            "SELECT " + propFields +
            "FROM property_index " +
            "WHERE id = :id")
    @Mapper(SessionPropertyMapper.class)
    Iterator<AmberProperty>
            findPropertiesByElementId(@Bind("id") long id);

    /*
     * Finding edges incident to a vertex.
     */
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND label = :label " +
            "txn_start > 0 " +
            "AND txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findOutEdges(@Bind("vertexId") long vertexId, @Bind("label") String label);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "txn_start > 0 " +
            "AND txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findOutEdges(@Bind("vertexId") long vertexId);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "txn_start > 0 " +
            "AND txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findInEdges(@Bind("vertexId") long vertexId, @Bind("label") String label);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "txn_start > 0 " +
            "AND txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findInEdges(@Bind("vertexId") long vertexId);

    /*
     * Finding vertices attached to edges incident to a vertex.
     */
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, edge e " +
            "WHERE v.id = v_in " +
            "AND v_out = :vertexId " +
            "AND label = :label " +
            "AND v.txn_start > 0 " +
            "AND v.txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findOutVertices(
            @Bind("vertexId") long vertexId, 
            @Bind("label") String label);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_in " +
            "AND v_out = :vertexId " +
            "AND v.txn_start > 0 " +
            "AND v.txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findOutVertices(
            @Bind("vertexId") long vertexId);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND v_in = :vertexId " +
            "AND label = :label " +
            "AND v.txn_start > 0 " +
            "AND v.txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findInVertices(
            @Bind("vertexId") long vertexId, 
            @Bind("label") String label);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND v_in = :vertexId " +
            "AND v.txn_start > 0 " +
            "AND v.txn_end = 0 " +
            "ORDER BY edge_order")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findInVertices(
            @Bind("vertexId") long vertexId);

    

    /*
     * Finding edges by property values
     */
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND e.txn_start = p.txn_start " +
            "AND e.txn_end = 0 " +
            "AND e.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.s_value = :value " +
            "ORDER BY e.id")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithStringProperty(
            @Bind("name") String name,
            @Bind("value") String value);
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND e.txn_start = p.txn_start " +
            "AND e.txn_end = 0 " +
            "AND e.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.b_value = :value " +
            "ORDER BY e.id")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithBooleanProperty(
            @Bind("name") String name,
            @Bind("value") Boolean value);
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND e.txn_start = p.txn_start " +
            "AND e.txn_end = 0 " +
            "AND e.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.i_value = :value " +
            "ORDER BY e.id")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithIntProperty(
            @Bind("name") String name,
            @Bind("value") Integer value);
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND e.txn_start = p.txn_start " +
            "AND e.txn_end = 0 " +
            "AND e.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.d_value = :value " +
            "ORDER BY e.id")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithDoubleProperty(
            @Bind("name") String name,
            @Bind("value") Double value);

    
    /*
     * Finding vertices by property values
     */
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND v.txn_start = p.txn_start " +
            "AND v.txn_end = 0 " +
            "AND v.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.s_value = :value " +
            "ORDER BY v.id")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithStringProperty(
            @Bind("name") String name,
            @Bind("value") String value);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND v.txn_start = p.txn_start " +
            "AND v.txn_end = 0 " +
            "AND v.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.b_value = :value " +
            "ORDER BY v.id")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithBooleanProperty(
            @Bind("name") String name,
            @Bind("value") Boolean value);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND v.txn_start = p.txn_start " +
            "AND v.txn_end = 0 " +
            "AND v.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.i_value = :value " +
            "ORDER BY v.id")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithIntProperty(
            @Bind("name") String name,
            @Bind("value") Integer value);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND v.txn_start = p.txn_start " +
            "AND v.txn_end = 0 " +
            "AND v.txn_start > 0 " +
            "AND p.name = :name " +
            "AND p.d_value = :value " +
            "ORDER BY v.id")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithDoubleProperty(
            @Bind("name") String name,
            @Bind("value") Double value);

    
    /*
     * Find all properties of an element
     */
    @SqlQuery(
            "SELECT " + propFields +
            "FROM property " +
            "WHERE id = :id " +
            "txn_start > 0 " +
            "AND txn_end = 0")
    @Mapper(PersistentPropertyMapper.class)
    Iterator<AmberProperty> findProperties(@Bind("id") long id);

    
    
    /*
     * Create Operations
     */
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO edge (" + edgeFields + ") " +
            "VALUES (" + edgeFieldSymbols + ")")
    long insertEdge(
            @Bind("id") long id, 
            @Bind("txn_start") long txnStart, 
            @Bind("txn_end") long txnEnd,
            @Bind("v_out") long outId, 
            @Bind("v_in") long inId, 
            @Bind("label") String label, 
            @Bind("edge_order") int edgeOrder);
    
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO vertex (" + vertexFields + ") " +
            "VALUES (" + vertexFieldSymbols + ")")
    long insertVertex(
            @Bind("id") long id, 
            @Bind("txn_start") long txnStart, 
            @Bind("txn_end") long txnEnd);


    @SqlUpdate(
            "INSERT INTO stage_vertex (" + stageVertexFields + ") " +
            "VALUES (" + stageVertexFieldSymbols + ")")
    long insertStageVertex(
            @BindAmberVertex AmberVertex vertex, 
            @Bind("txn_new") long txnId);

    @SqlUpdate(
            "INSERT INTO stage_edge (" + stageEdgeFields + ") " +
            "VALUES (" + stageEdgeFieldSymbols + ")")
    long insertStageEdge(
            @BindAmberEdge AmberEdge edge, 
            @Bind("txn_new") long txnId);
    
    @SqlUpdate(
            "INSERT INTO stage_property ( id, name, type, s_value, txn_new ) " +
            "VALUES ( :id, :name, :type, :s_value, :txn_new )")
    long insertStageStringProperty(
            @BindAmberProperty AmberProperty property, 
            @Bind("txn_new") long txnId);
    @SqlUpdate(
            "INSERT INTO stage_property ( id, name, type, i_value, txn_new ) " +
            "VALUES ( :id, :name, :type, :i_value, :txn_new )")
    long insertStageIntegerProperty(
            @BindAmberProperty AmberProperty property, 
            @Bind("txn_new") long txnId);
    @SqlUpdate(
            "INSERT INTO stage_property ( id, name, type, b_value, txn_new ) " +
            "VALUES ( :id, :name, :type, :b_value, :txn_new )")
    long insertStageBooleanProperty(
            @BindAmberProperty AmberProperty property, 
            @Bind("txn_new") long txnId);
    @SqlUpdate(
            "INSERT INTO stage_property ( id, name, type, d_value, txn_new ) " +
            "VALUES ( :id, :name, :type, :d_value, :txn_new )")
    long insertStageDoubleProperty(
            @BindAmberProperty AmberProperty property, 
            @Bind("txn_new")   long txnId);
    
    @SqlQuery(
            "SELECT v.id, v.end_txn " +
            "FROM vertex v, stage_vertex s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = v.id " +
            "AND v.txn_end > 0 " +
            "AND v.txn_end > s.txn_start " +  // this clause may not be needed
            "UNION " +
            "SELECT e.id, e.end_txn " +
            "FROM edge e, stage_edge s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = e.id " +
            "AND e.txn_end > 0 " +
            "AND e.txn_end > s.txn_start ")  // this clause may not be needed
    List<Long[]> findDeletionMutations(
            @Bind("txnId") long txnId);
    
    @SqlQuery(
            "SELECT v.id, v.start_txn " +
            "FROM vertex v, stage_vertex s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = v.id " +
            "AND v.txn_start > s.txn_start " +
            "UNION " +
            "SELECT e.id, e.end_txn " +
            "FROM edge e, stage_edge s " +
            "WHERE s.txn_new = :txnId " +
            "AND s.id = e.id " +
            "AND e.txn_start > s.txn_start ")
    List<Long[]> findAlterationMutations(
            @Bind("txnId") long txnId);
    
    @SqlQuery(
            "INSERT INTO vertex (id, txn_start) " +
            "SELECT id, txn_new " +
            "FROM stage_vertex " +
            "WHERE state <> :deletedState")
    int insertStagedVertices(
            @Bind("txnId")        long txnId,
            @Bind("deletedState") int deletedState);

    @SqlQuery(
            "INSERT INTO edge (id, txn_start, v_out, v_in, label, edge_order) " +
            "SELECT id, txn_new, v_out, v_in, label, edge_order " +
            "FROM stage_vertex " +
            "WHERE state <> :deletedState")
    int insertStagedEdges(
            @Bind("txnId")        long txnId,
            @Bind("deletedState") int deletedState);
    
    @SqlQuery(
            "INSERT INTO property (id, txn_start, name, type, s_value, b_value, i_value, d_value) " +
            "SELECT id, txn_new, name, type, s_value, b_value, i_value, d_value " +
            "FROM stage_property " +
            "WHERE state <> :deletedState")
    int insertStagedProperties(
            @Bind("txnId") long txnId);

    @SqlQuery(
            "UPDATE vertex " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_vertex " +
            "  WHERE state <> :newState) " +
            "AND txn_end = 0")
    int updateSupercededVertices(
            @Bind("txnId")    long txnId,
            @Bind("newState") int newState);

    @SqlQuery(
            "UPDATE edge " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_edge " +
            "  WHERE state <> :newState) " +
            "AND txn_end = 0")
    int updateSupercededEdges(
            @Bind("txnId")    long txnId,
            @Bind("newState") int newState);
    
    @SqlQuery(
            "UPDATE property " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_property " +
            "  WHERE state <> :newState) " +
            "AND txn_end = 0")
    int updateSupercededProperties(
            @Bind("txnId") long txnId,
            @Bind("newState") int newState);

    @SqlQuery(
            "UPDATE transaction " +
            "SET commit = :commitMarker " +
            "WHERE id = :txnId")
    int commitTransaction(
            @Bind("txnId") long txnId,
            @Bind("commitMarker") long commitMarker);
    
    void close();
}

