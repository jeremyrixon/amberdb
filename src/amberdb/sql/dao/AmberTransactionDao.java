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

public interface AmberTransactionDao extends Transactional<AmberTransactionDao> {

    /*
     *  DB creation operations (DDL)
     */
    @SqlUpdate(
    		"CREATE TABLE IF NOT EXISTS vertex (" +
    		"id         BIGINT, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"state      TINYINT(1))")
    void createVertexTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge (" +
    		"id         BIGINT, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"v_out      BIGINT, " +
    		"v_in       BIGINT, " +
    		"label      VARCHAR(100), " +
    		"edge_order BIGINT, " +
    		"state      TINYINT(1))")
    void createEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property_index (" +
    		"e_id    BIGINT, " +
    		"name    VARCHAR(100), " +
            "b_value TINYINT(1), " +
            "s_value TEXT, " +
            "i_value BIGINT, " +
            "d_value DOUBLE)")
    void createPropertyTable();
    @SqlUpdate(
            "CREATE UNIQUE INDEX e_unique_prop " +
            "ON property_index(e_id, name)")
    void createPropertyTableIndex();
    
    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, property_index")
    void dropTables();
    
    /*
     * Update Operations
     */
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE e_id = :id " +
            "AND name = :propertyName")
    void removeProperty(@Bind("id") long elementId, @Bind("propertyName") String propertyName);

    @SqlUpdate(
            "UPDATE edge " +
            "SET state = :state " +
            "WHERE id = :id")
    void updateEdgeState(@Bind("id") long elementId, @Bind("state") Integer state);
    @SqlUpdate(
            "UPDATE vertex " +
            "SET state = :state " +
            "WHERE id = :id")
    void updateVertexState(@Bind("id") long elementId, @Bind("state") Integer state);

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

    @SqlUpdate(
            "INSERT INTO property_index (e_id, name, i_value) " +
            "VALUES (:id, :name, :value)")
    void createIntegerProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Integer value);
    @SqlUpdate(
            "INSERT INTO property_index (e_id, name, b_value) " +
            "VALUES (:id, :name, :value)")
    void createBooleanProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Boolean value);
    @SqlUpdate(
            "INSERT INTO property_index (e_id, name, d_value) " +
            "VALUES (:id, :name, :value)")
    void createDoubleProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Double value);
    @SqlUpdate(
            "INSERT INTO property_index (e_id, name, s_value) " +
            "VALUES (:id, :name, :value)")
    void createStringProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") String value);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET edge_order = :edgeOrder " +
            "WHERE id = :id")
    void updateEdgeOrder(@Bind("id") long edgeId, @Bind("edgeOrder") Integer edgeOrder);
    
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
            "WHERE e_id = :id")
    void removeElementProperties(@Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE e_id IN " +
            "  (SELECT id FROM edge " +
            "   WHERE v_id = :id " +
            "   OR v_out = :id)")
    void removeIncidentEdgeProperties(@Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM edge " +
            "WHERE v_id = :id " +
            "OR v_out = :id")
    void removeIncidentEdges(@Bind("id") long id);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET state = :state " +
            "WHERE v_in = :vertexId " +
            "OR v_out = :vertexId")
    void updateIncidentEdgeStates(@Bind("vertexId") long vertexId, @Bind("state") Integer state);

    /*
     * Find Operations
     */

    @SqlQuery(
            "SELECT id, txn_start, txn_end, state " +
            "FROM vertex " +
            "WHERE id = :id")
    @Mapper(AmberVertexTxnMapper.class)
    AmberVertex
            findVertexById(@Bind("id") long id);
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " +
            "FROM edge " +
            "WHERE id = :id")
    @Mapper(AmberEdgeTxnMapper.class)
    AmberEdge
            findEdgeById(@Bind("id") long id);

    @SqlQuery(
            "SELECT name, b_value, d_value, s_value, i_value " +
            "FROM property_index " +
            "WHERE e_id = :id")
    @Mapper(AmberPropertyTxnMapper.class)
    Iterator<AmberProperty>
            findPropertiesByElementId(@Bind("id") long id);
    
    
    /*
     * Create Operations
     */
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order, state) " +
            "VALUES (:id, :txnStart, :txnEnd, :outId, :inId, :label, :edgeOrder, :state)")
    long 
            createEdge(@Bind("id") long id, @Bind("txnStart") long txnStart, @Bind("txnEnd") long txnEnd,
                    @Bind("outId") long outId, @Bind("inId") long inId, @Bind("label") String label, 
                    @Bind("edgeOrder") int edgeOrder, @Bind("state") int state);
    
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO vertex (id, txn_start, txn_end, state) " +
            "VALUES (:id, :txnStart, :txnEnd, :state)")
    long 
            createVertex(@Bind("id") long id, @Bind("txnStart") long txnStart, 
                    @Bind("txnEnd") long txnEnd, @Bind("state") int state);
    
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
//            "INSERT INTO edge_property_idx (e_id, name, b_value) " +
//            "VALUES (:id, :name, :value)")
//    long setBooleanEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Boolean value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (e_id, name, d_value) " +
//            "VALUES (:id, :name, :value)")
//    long setDoubleEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Double value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (e_id, name, s_value) " +
//            "VALUES (:id, :name, :value)")
//    long setStringEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") String value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "INSERT INTO edge_property_idx (e_id, name, i_value) " +
//            "VALUES (:id, :name, :value)")
//    long setIntegerEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Integer value);
//    
//    @SqlUpdate(
//            "DELETE FROM edge_property_idx " +
//            "WHERE e_id = :id " +
//            "AND name = :name")
//    void removeEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name);
//
//    @SqlUpdate(
//            "DELETE FROM edge_property_idx " +
//    		"WHERE e_id = :id")
//    void removeEdgePropertyIndexEntries(@Bind("id") long id);
//    
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.e_id " +
//            "AND epi.name = :name " +
//            "AND epi.b_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByBooleanProperty(@Bind("name") String name, @Bind("value") Boolean value);
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.e_id " +
//            "AND epi.name = :name " +
//            "AND epi.d_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByDoubleProperty(@Bind("name") String name, @Bind("value") Double value);
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.e_id " +
//            "AND epi.name = :name " +
//            "AND epi.s_value = :value " +
//            "ORDER BY edge_order")
//    @Mapper(AmberEdgeMapper.class)
//    Iterator<AmberEdge> 
//        findEdgesByStringProperty(@Bind("name") String name, @Bind("value") String value);
//    @SqlQuery(
//            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
//            "FROM edge e, edge_property_idx epi " +
//            "WHERE e.id = epi.e_id " +
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

