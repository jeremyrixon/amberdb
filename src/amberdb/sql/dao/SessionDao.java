package amberdb.sql.dao;

import java.util.Iterator;
import java.util.List;

import  amberdb.sql.*;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.util.StringMapper;

public interface SessionDao extends Transactional<SessionDao> {

    static final String vertexFields = " id, txn_start, txn_end, state ";
//    static final String vertexFieldsV = " v.id, v.txn_start, v.txn_end, v.state ";
//    static final String vertexFieldSymbols = " :id, :txn_start, :txn_end, :state ";
    
    static final String edgeFields = " id, txn_start, txn_end, v_out, v_in, label, edge_order, state ";
//    static final String edgeFieldsE = " e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, e.state ";
//    static final String edgeFieldSymbols = " :id, :txn_start, :txn_end, :v_out, :v_in, :label, :edge_order, :state ";
    
    static final String propertyFields = " id, name, type, b_value, s_value, i_value, d_value ";
    static final String propertyFieldsP = " p.id, p.name, p.type, p.b_value, p.s_value, p.i_value, p.d_value ";
//    static final String propertyFieldSymbols = " :id, :name, :type, :b_value, :s_value, :i_value, :d_value ";
    
    /*
     *  DB creation operations (DDL)
     */
    @SqlUpdate(
    		"CREATE TABLE IF NOT EXISTS vertex (" +
    		"id         BIGINT UNIQUE, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"state      TINYINT(1))")
    void createVertexTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge (" +
    		"id         BIGINT UNIQUE, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"v_out      BIGINT, " +
    		"v_in       BIGINT, " +
    		"label      VARCHAR(100), " +
    		"edge_order BIGINT, " +
    		"state      TINYINT(1))")
    void createEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property (" +
    		"id      BIGINT, " +
    		"name    VARCHAR(100), " +
    		"type    CHAR(1), " +
            "b_value BOOLEAN, " +
            "s_value TEXT, " +
            "i_value BIGINT, " +
            "d_value DOUBLE)")
    void createPropertyTable();
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_prop " +
            "ON property(id, name)")
    void createPropertyIndex();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS id_generator (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT)")
    void createIdGeneratorTable();
    
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
     * DDL cleanup
     */
    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, property, id_generator")
    void dropTables();
    


    /*
     * Add new properties
     */
    @SqlUpdate(
            "INSERT INTO property (id, name, i_value) " +
            "VALUES (:id, :name, :value)")
    void createIntegerProperty(
            @Bind("id") long elementId, 
            @Bind("name") String name, 
            @Bind("value") Integer value);
    @SqlUpdate(
            "INSERT INTO property (id, name, b_value) " +
            "VALUES (:id, :name, :value)")
    void createBooleanProperty(
            @Bind("id") long elementId,
            @Bind("name") String name, 
            @Bind("value") Boolean value);
    @SqlUpdate(
            "INSERT INTO property (id, name, d_value) " +
            "VALUES (:id, :name, :value)")
    void createDoubleProperty(
            @Bind("id") long elementId, 
            @Bind("name") String name, 
            @Bind("value") Double value);
    @SqlUpdate(
            "INSERT INTO property (id, name, s_value) " +
            "VALUES (:id, :name, :value)")
    void createStringProperty(
            @Bind("id") long elementId, 
            @Bind("name") String name, 
            @Bind("value") String value);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET edge_order = :edgeOrder " +
            "WHERE id = :id")
    void updateEdgeOrder(
            @Bind("id") long edgeId, 
            @Bind("edgeOrder") Integer edgeOrder);
    
    /*
     * Deleting things 
     */
//    @SqlUpdate(
//            "DELETE FROM property " +
//            "WHERE e_id = :id " +
//            "AND name = :propertyName")
//    void removeProperty(
//            @Bind("id") long elementId, 
//            @Bind("propertyName") String propertyName);
    @SqlUpdate(
            "DELETE FROM edge " +
            "WHERE id = :id")
    void removeEdge(
            @Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM vertex " +
            "WHERE id = :id")
    void removeVertex(
            @Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE e_id = :id")
    void removeElementProperties(
            @Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM property_index " +
            "WHERE e_id IN " +
            "  (SELECT id FROM edge " +
            "   WHERE v_id = :id " +
            "   OR v_out = :id)")
    void removeIncidentEdgeProperties(
            @Bind("id") long id);
    @SqlUpdate(
            "DELETE FROM edge " +
            "WHERE v_id = :id " +
            "OR v_out = :id")
    void removeIncidentEdges(
            @Bind("id") long id);
    
    /*
     * Find Operations
     */
    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE id = :id")
    @Mapper(SessionVertexMapper.class)
    AmberVertex findVertexById(
            @Bind("id") long id);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE id = :id")
    @Mapper(SessionEdgeMapper.class)
    AmberEdge findEdge(
            @Bind("id") long id);

    
//    @SqlQuery(
//            "SELECT name, b_value, d_value, s_value, i_value " +
//            "FROM property_index " +
//            "WHERE e_id = :id")
//    @Mapper(SessionPropertyMapper.class)
//    Iterator<AmberProperty> findPropertiesByElementId(
//            @Bind("id") long id);

    /*
     * Finding edges incident to a vertex.
     */
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findOutEdges(
            @Bind("vertexId") long vertexId, 
            @Bind("label") String label);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findOutEdges(
            @Bind("vertexId") long vertexId);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findInEdges(
            @Bind("vertexId") long vertexId, 
            @Bind("label") String label);
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findInEdges(
            @Bind("vertexId") long vertexId);
  
    
    void close();

    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE state = :state")
    @Mapper(SessionVertexMapper.class)
    List<AmberVertex> findVerticesByState(
            @Bind("state") int state);
    
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE state = :state")
    @Mapper(SessionEdgeMapper.class)
    List<AmberEdge> findEdgesByState(
            @Bind("state") int state);

    @SqlQuery(
            "SELECT " + propertyFieldsP +
            "FROM property p, edge e " +
            "WHERE p.id = e.id " +
            "AND e.state = :state " +
            "UNION " +
            "SELECT " + propertyFieldsP +
            "FROM property p, vertex v " +
            "WHERE p.id = v.id " +
            "AND v.state = :state")
    @Mapper(SessionPropertyMapper.class)
    List<AmberProperty> findPropertiesByElementState(
            @Bind("state") int state);
}

