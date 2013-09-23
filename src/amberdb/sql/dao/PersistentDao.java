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
    @SqlUpdate(
            "CREATE UNIQUE INDEX e_unique_prop " +
            "ON property(id, txn_start, name)")
    void createPropertyIndex();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS id_generator (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT)")
    void createIdGeneratorTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS transaction (" +
            "id       BIGINT, " +
            "commit   TIMESTAMP, " +
            "user     VARCHAR(100), " +
            "operaton TEXT)")
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
    
    /*
     * Update existing properties
     */
    @SqlUpdate(
            "UPDATE property_index " +
            "SET i_value = :value " +
            "WHERE id = :id " +
            "AND name = :name")
    
    void updateIntegerProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Integer value);
    @SqlUpdate(
            "UPDATE property_index " +
            "SET b_value = :value " +
            "WHERE id = :id " +
            "AND name = :name")
    void updateBooleanProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Boolean value);
    @SqlUpdate(
            "UPDATE property_index " +
            "SET d_value = :value " +
            "WHERE id = :id " +
            "AND name = :name")
    void updateDoubleProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Double value);
    @SqlUpdate(
            "UPDATE property_index " +
            "SET s_value = :value " +
            "WHERE id = :id " +
            "AND name = :name")
    void updateStringProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") String value);

    /*
     * Add new properties
     */
    @SqlUpdate(
            "INSERT INTO property_index (id, name, i_value) " +
            "VALUES (:id, :name, :value)")
    void createIntegerProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Integer value);
    @SqlUpdate(
            "INSERT INTO property_index (id, name, b_value) " +
            "VALUES (:id, :name, :value)")
    void createBooleanProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Boolean value);
    @SqlUpdate(
            "INSERT INTO property_index (id, name, d_value) " +
            "VALUES (:id, :name, :value)")
    void createDoubleProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Double value);
    @SqlUpdate(
            "INSERT INTO property_index (id, name, s_value) " +
            "VALUES (:id, :name, :value)")
    void createStringProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") String value);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET edge_order = :edgeOrder " +
            "WHERE id = :id")
    void updateEdgeOrder(@Bind("id") long edgeId, @Bind("edgeOrder") Integer edgeOrder);
    
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
    @Mapper(SessionVertexMapper.class)
    AmberVertex
            findVertex(@Bind("id") long id);

    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE txn_end IS NULL " +
            "AND txn_start IS NOT NULL")
    @Mapper(SessionVertexMapper.class)
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
    long createEdge(
            @Bind("id") long id, 
            @Bind("txnStart") long txnStart, 
            @Bind("txnEnd") long txnEnd,
            @Bind("outId") long outId, 
            @Bind("inId") long inId, 
            @Bind("label") String label, 
            @Bind("edgeOrder") int edgeOrder);
    
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO vertex (" + vertexFields + ") " +
            "VALUES (" + edgeFieldSymbols + ")")
    long createVertex(
            @Bind("id") long id, 
            @Bind("txnStart") long txnStart, 
            @Bind("txnEnd") long txnEnd);

    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_in = :vertexId " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//            findInEdgesByVertexId(@Bind("vertexId") long vertexId);

//    
//    /*
//     *  Edge related operations
//     */
//    
//    @SqlUpdate(
//            "DELETE FROM edge " +
//    		"WHERE id = :id")
//    void removeEdge(@Bind("id") long id);
//
//    
//    @SqlUpdate(
//            "UPDATE edge " +
//            "SET properties = :properties " +
//            "WHERE id = :id")
//    void updateEdgeProperties(@Bind("id") long id, @Bind("properties") String properties);
//
//    
//    @SqlUpdate(
//            "UPDATE edge " +
//    		"SET edge_order = :edgeOrder " +
//    		"WHERE id = :id")
//    void updateEdgeOrder(@Bind("id") long id, @Bind("edgeOrder") Integer edgeOrder);
//
//    
//    @SqlUpdate(
//            "UPDATE edge " +
//            "SET state = :state " +
//            "WHERE id = :id")
//    void updateEdgeState(@Bind("id") long id, @Bind("state") Integer state);
//
//    
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge (id, txn_start, txn_end, properties, v_out, v_in, label, edge_order, state) " +
//            "VALUES (:id, :txnStart, :txnEnd, :properties, :outId, :inId, :label, :edgeOrder, :state)")
//    long addEdge(@Bind("id") long id, @Bind("txnStart") long txnStart, @Bind("txnEnd") long txnEnd,
//            @Bind("properties") String properties, @Bind("outId") long outId, @Bind("inId") long inId, 
//            @Bind("label") String label, @Bind("edgeOrder") int edgeOrder, @Bind("state") int state);
//    
////    @GetGeneratedKeys
////    @SqlUpdate(
////            "INSERT INTO edge (txn_start, txn_open, properties, v_out, v_in, label, edge_order) " +
////    		"VALUES (:txnStart, true, :properties, :outId, :inId, :label, 0)")
////    long createEdge(@Bind("txnStart") long txnStart, @Bind("properties") String properties, @Bind("outId") long outId, @Bind("inId") long inId, @Bind("label") String label);
//
//    
//    @SqlQuery( // txn
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//    		"FROM edge " +
//    		"WHERE id = :id " +
//    		"AND txn_end IS NULL")
//    @Mapper(AmberEdgeMapper.class)
//    AmberEdge 
//        findEdgeById(@Bind("id") long id);
//    
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_out = :vertexId " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findOutEdgesByVertexId(@Bind("vertexId") long vertexId);
//
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE v_in = :vertexId " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findInEdgesByVertexId(@Bind("vertexId") long vertexId);
//
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//    		"FROM edge " +
//    		"WHERE v_out = :vertexId " +
//    		"AND label in (:labels) " +
//    		"ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findOutEdgesByVertexIdAndLabel(@Bind("vertexId") long vertexId, @Bind("labels") String labels);
//
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//    		"FROM edge " +
//    		"WHERE v_in = :vertexId AND label in (:labels) " +
//    		"ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findInEdgesByVertexIdAndLabel(@Bind("vertexId") long vertexId, @Bind("labels") String labels);
//    
//    
//    @SqlQuery( // txn
//            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge " +
//            "WHERE txn_end IS NULL")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findAllEdges();
//    
//    
//    /*
//     *  Vertex related operations
//     */
//    
//    @SqlUpdate("DELETE FROM vertex WHERE id = :id")
//    void removeVertex(@Bind("id") long id);
//    
//    
//    @SqlUpdate("UPDATE vertex SET properties = :properties WHERE id = :id")
//    void updateVertex(@Bind("id") long id, @Bind("properties") String properties);
//
//    @SqlUpdate("UPDATE vertex SET properties = :properties WHERE id = :id")
//    void updateVertexProperties(@Bind("id") long id, @Bind("properties") String properties);
//
//    
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO vertex (txn_start, txn_open, properties, pi) " +
//    		"VALUES (:txnStart, true, :properties, :pi)")
//    long createVertex(@Bind("txnStart") Long txnStart, @Bind("properties") String properties, @Bind("pi") String pi);
//
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, pi " +
//            "FROM vertex " +
//            "WHERE id = :id")
//    @Mapper(AmberVertexMapper.class)
//    AmberVertex 
//        findVertexById(@Bind("id") long id);
//    
//
//    // allow multiple of the same vertex to be returned to conform
//    // to the Blueprint VertexTestSuite gettingEdgesAndVertices
//    @SqlQuery(
//            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
//            "       v.properties properties, v.pi pi " +
//    		"FROM vertex v, edge e " +
//    		"WHERE v.id = e.v_in " +
//    		"AND e.v_out = :srcVertexId " +
//    		"ORDER BY e.edge_order")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVertexByInEdgeFromVertexId(@Bind("srcVertexId") long vertexId);
//    
//    
//    // allow multiple of the same vertex to be returned to conform
//    // to the Blueprint VertexTestSuite gettingEdgesAndVertices
//    @SqlQuery(
//            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
//            "       v.properties properties, v.pi pi " +
//            "FROM vertex v, edge e " +
//            "WHERE v.id = e.v_out " +
//            "AND e.v_in = :srcVertexId " +
//            "ORDER BY e.edge_order")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVertexByOutEdgeToVertexId(@Bind("srcVertexId") long vertexId);
//    
//    
//    @SqlQuery(
//            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
//            "       v.properties properties, v.pi pi " +
//            "FROM vertex v, edge e " +
//    		"WHERE v.id = e.v_in " +
//    		"AND e.v_out = :srcVertexId " +
//    		"AND e.label = :label " +
//    		"ORDER BY e.edge_order")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVertexByInEdgeLabelFromVertexId(@Bind("srcVertexId") long vertexId, @Bind("label") String label);
//    
//    
//    @SqlQuery(
//            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
//            "       v.properties properties, v.pi pi " +
//            "FROM vertex v, edge e " +
//            "WHERE v.id = e.v_out " +
//            "AND e.v_in = :srcVertexId " +
//            "AND e.label = :label " +
//            "ORDER BY e.edge_order")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVertexByOutEdgeLabelToVertexId(@Bind("srcVertexId") long vertexId, @Bind("label") String label);
//
//    
//    @SqlQuery(
//            "SELECT id, txn_start, txn_end, txn_open, properties, pi " +
//            "FROM vertex")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findAllVertices();
//
//    
//    @SqlQuery(
//            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
//            "       v.properties properties, v.pi pi " +
//    		"FROM vertex v, edge e " +
//    		"WHERE e.id = :edgeId " +
//    		"AND v.id = e.v_out")
//    @Mapper(AmberVertexMapper.class)
//    AmberVertex 
//        findVertexByOutEdge(@Bind("edgeId") long edgeId);
//
//    
//    @SqlQuery(
//            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
//            "       v.properties properties, v.pi pi " +
//    		"FROM vertex v, edge e " +
//    		"WHERE e.id = :edgeId " +
//    		"AND v.id = e.v_in")
//    @Mapper(AmberVertexMapper.class)
//    AmberVertex 
//        findVertexByInEdge(@Bind("edgeId") long edgeId);
//
//    
//    
//    /*
//     * Property index related operations
//     */
//
//    // Vertex indexes
//    
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO vertex_property_idx (v_id, name, b_value) " +
//    		"VALUES (:id, :name, :value)")
//    long setBooleanVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Boolean value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO vertex_property_idx (v_id, name, d_value) " +
//            "VALUES (:id, :name, :value)")
//    long setDoubleVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Double value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO vertex_property_idx (v_id, name, s_value) " +
//            "VALUES (:id, :name, :value)")
//    long setStringVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") String value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO vertex_property_idx (v_id, name, i_value) " +
//            "VALUES (:id, :name, :value)")
//    long setIntegerVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Integer value);
//    
//    @SqlUpdate(
//            "DELETE FROM vertex_property_idx " +
//    		"WHERE v_id = :id " +
//    		"AND name = :name")
//    void removeVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name);
//
//    @SqlUpdate(
//            "DELETE FROM vertex_property_idx " +
//    		"WHERE v_id = :id")
//    void removeVertexPropertyIndexEntries(@Bind("id") long id);
//    
//    @SqlQuery(
//            "SELECT v.id id, txn_start, txn_end, txn_open, properties, pi " +
//            "FROM vertex v, vertex_property_idx vpi " +
//            "WHERE v.id = vpi.v_id " +
//            "AND vpi.name = :name " +
//            "AND vpi.b_value = :value")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVerticesByBooleanProperty(@Bind("name") String name, @Bind("value") Boolean value);
//    @SqlQuery(
//            "SELECT v.id id, txn_start, txn_end, txn_open, properties, pi " +
//            "FROM vertex v, vertex_property_idx vpi " +
//            "WHERE v.id = vpi.v_id " +
//            "AND vpi.name = :name " +
//            "AND vpi.d_value = :value")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVerticesByDoubleProperty(@Bind("name") String name, @Bind("value") Double value);
//    @SqlQuery(
//            "SELECT v.id id, txn_start, txn_end, txn_open, properties, pi " +
//            "FROM vertex v, vertex_property_idx vpi " +
//            "WHERE v.id = vpi.v_id " +
//            "AND vpi.name = :name " +
//            "AND vpi.s_value = :value")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVerticesByStringProperty(@Bind("name") String name, @Bind("value") String value);
//    @SqlQuery(
//            "SELECT v.id id, txn_start, txn_end, txn_open, properties, pi " +
//            "FROM vertex v, vertex_property_idx vpi " +
//            "WHERE v.id = vpi.v_id " +
//            "AND vpi.name = :name " +
//            "AND vpi.i_value = :value")
//    @Mapper(AmberVertexMapper.class)
//    Iterator<AmberVertex> 
//        findVerticesByIntegerProperty(@Bind("name") String name, @Bind("value") Integer value);
//
//    // Edge indexes
//    
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (id, name, b_value) " +
//            "VALUES (:id, :name, :value)")
//    long setBooleanEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Boolean value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (id, name, d_value) " +
//            "VALUES (:id, :name, :value)")
//    long setDoubleEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Double value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (id, name, s_value) " +
//            "VALUES (:id, :name, :value)")
//    long setStringEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") String value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (id, name, i_value) " +
//            "VALUES (:id, :name, :value)")
//    long setIntegerEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Integer value);
//    
//    @SqlUpdate(
//            "DELETE FROM edge_property_idx " +
//            "WHERE id = :id " +
//            "AND name = :name")
//    void removeEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name);
//
//    @SqlUpdate(
//            "DELETE FROM edge_property_idx " +
//    		"WHERE id = :id")
//    void removeEdgePropertyIndexEntries(@Bind("id") long id);
//    
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.id " +
//            "AND epi.name = :name " +
//            "AND epi.b_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByBooleanProperty(@Bind("name") String name, @Bind("value") Boolean value);
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.id " +
//            "AND epi.name = :name " +
//            "AND epi.d_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByDoubleProperty(@Bind("name") String name, @Bind("value") Double value);
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.id " +
//            "AND epi.name = :name " +
//            "AND epi.s_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByStringProperty(@Bind("name") String name, @Bind("value") String value);
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.id " +
//            "AND epi.name = :name " +
//            "AND epi.i_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByIntegerProperty(@Bind("name") String name, @Bind("value") Integer value);
//
//    
//    /*
//     *  Transaction related operations
//     */
//    
//    //@SqlUpdate("DELETE FROM transaction WHERE id = :id")
//    //void removeTxn(@Bind("id") long id);
//    // Note: will we record failed transactions ?
//    
//    @SqlUpdate(
//            "UPDATE transaction " +
//    		"SET commit = :commit " +
//    		"WHERE id = :id")
//    void updateTxn(@Bind("id") long id, @Bind("commit") Date commit);

    
    void close();
}

