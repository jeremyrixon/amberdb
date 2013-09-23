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
import org.skife.jdbi.v2.util.StringMapper;

public interface SessionDao extends Transactional<SessionDao> {

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
    
    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, property")
    void dropTables();
    


    /*
     * Add new properties
     */
    @SqlUpdate(
            "INSERT INTO property (id, name, i_value) " +
            "VALUES (:id, :name, :value)")
    void createIntegerProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Integer value);
    @SqlUpdate(
            "INSERT INTO property (id, name, b_value) " +
            "VALUES (:id, :name, :value)")
    void createBooleanProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Boolean value);
    @SqlUpdate(
            "INSERT INTO property (id, name, d_value) " +
            "VALUES (:id, :name, :value)")
    void createDoubleProperty(@Bind("id") long elementId, @Bind("name") String name, @Bind("value") Double value);
    @SqlUpdate(
            "INSERT INTO property (id, name, s_value) " +
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
            "WHERE e_id = :id " +
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
    
    /*
     * Find Operations
     */
    @SqlQuery(
            "SELECT id, txn_start, txn_end, state " +
            "FROM vertex " +
            "WHERE id = :id")
    @Mapper(SessionVertexMapper.class)
    AmberVertex
            findVertexById(@Bind("id") long id);
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " +
            "FROM edge " +
            "WHERE id = :id")
    @Mapper(SessionEdgeMapper.class)
    AmberEdge
            findEdge(@Bind("id") long id);

    
    @SqlQuery(
            "SELECT name, b_value, d_value, s_value, i_value " +
            "FROM property_index " +
            "WHERE e_id = :id")
    @Mapper(SessionPropertyMapper.class)
    Iterator<AmberProperty>
            findPropertiesByElementId(@Bind("id") long id);

    /*
     * Finding edges incident to a vertex.
     */
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge>
            findOutEdges(@Bind("vertexId") long vertexId, @Bind("label") String label);
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge>
            findOutEdges(@Bind("vertexId") long vertexId);
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge>
            findInEdges(@Bind("vertexId") long vertexId, @Bind("label") String label);
    @SqlQuery(
            "SELECT id, txn_start, txn_end, v_out, v_in, label, edge_order, state " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge>
            findInEdges(@Bind("vertexId") long vertexId);
  
    
    void close();
}

