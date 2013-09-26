package amberdb.sql.dao;

import java.util.Iterator;

import  amberdb.sql.*;
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

    /*
     *  DB creation operations (DDL)
     */
    @SqlUpdate(
    		"CREATE TABLE IF NOT EXISTS vertex (" +
    		"id         BIGINT, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT)")
    void createVertexTable();
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_vert " +
            "ON vertex(id, txn_start)")
    void createVertexIndex();
    
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
            "CREATE UNIQUE INDEX unique_edge " +
            "ON edge(id, txn_start)")
    void createEdgeIndex();

    
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
//    @SqlUpdate(
//            "CREATE UNIQUE INDEX e_unique_prop " +
//            "ON property(id, txn_start, name)")
//    void createPropertyIndex();
    
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

    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, property, transaction")
    void dropTables();

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
     * New elements
     */
    // vertices
    // edges
    // properties
    
//    /*
//     * Update existing properties
//     */
//    @SqlUpdate(
//            "UPDATE property_index " +
//            "SET i_value = :value " +
//            "WHERE id = :id " +
//            "AND name = :name")
//    
//    void updateIntegerProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Integer value);
//    @SqlUpdate(
//            "UPDATE property_index " +
//            "SET b_value = :value " +
//            "WHERE id = :id " +
//            "AND name = :name")
//    void updateBooleanProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Boolean value);
//    @SqlUpdate(
//            "UPDATE property_index " +
//            "SET d_value = :value " +
//            "WHERE id = :id " +
//            "AND name = :name")
//    void updateDoubleProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Double value);
//    @SqlUpdate(
//            "UPDATE property_index " +
//            "SET s_value = :value " +
//            "WHERE id = :id " +
//            "AND name = :name")
//    void updateStringProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") String value);

    /*
     * Add new properties
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
    
//    @SqlUpdate(
//            "UPDATE edge " +
//            "SET edge_order = :edgeOrder " +
//            "WHERE id = :id")
//    void updateEdgeOrder(@Bind("id") long edgeId, @Bind("edgeOrder") Integer edgeOrder);
    
    /*
     * Deleting things 
     */
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE id = :id " +
            "AND name = :propertyName")
    void removeProperty(@Bind("id") long elementId, @Bind("propertyName") String propertyName);
    @SqlUpdate(
            "DELETE FROM edge " +
            "WHERE id = :id")
    void removeEdge(@Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM vertex " +
            "WHERE id = :id")
    void removeVertex(@Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE id = :id")
    void removeElementProperties(@Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE id IN " +
            "  (SELECT id FROM edge " +
            "   WHERE v_id = :id " +
            "   OR v_out = :id)")
    void removeIncidentEdgeProperties(@Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM edge " +
            "WHERE v_id = :id " +
            "OR v_out = :id")
    void removeIncidentEdges(@Bind("id") long id);
    
    /*
     * Find Operations
     */
    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE id = :id " +
            "AND txn_end IS NULL " +
            "AND txn_start IS NOT NULL")
    @Mapper(PersistentVertexMapper.class)
    AmberVertex
            findVertex(@Bind("id") long id);

    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE txn_end IS NULL " +
            "AND txn_start IS NOT NULL")
    @Mapper(PersistentVertexMapper.class)
    Iterator<AmberVertex>
            findVertices();
    
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE id = :id " +
            "AND txn_end IS NULL " +
            "AND txn_start IS NOT NULL")
    @Mapper(PersistentEdgeMapper.class)
    AmberEdge
            findEdge(@Bind("id") long id);

    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE txn_end IS NULL " +
            "AND txn_start IS NOT NULL")
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
            "AND txn_start IS NOT NULL " +
            "AND txn_end IS NULL " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findOutEdges(@Bind("vertexId") long vertexId, @Bind("label") String label);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND txn_start IS NOT NULL " +
            "AND txn_end IS NULL " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findOutEdges(@Bind("vertexId") long vertexId);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "AND txn_start IS NOT NULL " +
            "AND txn_end IS NULL " +
            "ORDER BY edge_order")
    @Mapper(PersistentEdgeMapper.class)
    Iterator<AmberEdge>
            findInEdges(@Bind("vertexId") long vertexId, @Bind("label") String label);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND txn_start IS NOT NULL " +
            "AND txn_end IS NULL " +
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
            "AND v.txn_start IS NOT NULL " +
            "AND v.txn_end IS NULL " +
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
            "AND v.txn_start IS NOT NULL " +
            "AND v.txn_end IS NULL " +
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
            "AND v.txn_start IS NOT NULL " +
            "AND v.txn_end IS NULL " +
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
            "AND v.txn_start IS NOT NULL " +
            "AND v.txn_end IS NULL " +
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
            "AND e.txn_end IS NULL " +
            "AND e.txn_start IS NOT NULL " +
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
            "AND e.txn_end IS NULL " +
            "AND e.txn_start IS NOT NULL " +
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
            "AND e.txn_end IS NULL " +
            "AND e.txn_start IS NOT NULL " +
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
            "AND e.txn_end IS NULL " +
            "AND e.txn_start IS NOT NULL " +
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
            "AND v.txn_end IS NULL " +
            "AND v.txn_start IS NOT NULL " +
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
            "AND v.txn_end IS NULL " +
            "AND v.txn_start IS NOT NULL " +
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
            "AND v.txn_end IS NULL " +
            "AND v.txn_start IS NOT NULL " +
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
            "AND v.txn_end IS NULL " +
            "AND v.txn_start IS NOT NULL " +
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
            "AND txn_start IS NOT NULL " +
            "AND txn_end IS NULL")
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

    
    void close();
}

