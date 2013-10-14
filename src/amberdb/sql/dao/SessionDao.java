package amberdb.sql.dao;

import java.util.Iterator;
import java.util.List;

import  amberdb.sql.*;
import amberdb.sql.bind.BindAmberEdge;
import amberdb.sql.bind.BindAmberProperty;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

public interface SessionDao extends Transactional<SessionDao> {

  
    /*
     *  DB creation operations (DDL)
     */
    @SqlUpdate(
    		"CREATE TABLE IF NOT EXISTS vertex (" +
    		"id         BIGINT UNIQUE, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"state      CHAR(3))")
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
    		"state      CHAR(3))")
    void createEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property (" +
    		"id      BIGINT, " +
    		"name    VARCHAR(100), " +
    		"type    CHAR(3), " +
            "value   BLOB)")
    void createPropertyTable();
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_prop " +
            "ON property(id, name)")
    void createPropertyIndex();
    
    /*
     * DDL cleanup
     */
    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, property, id_generator")
    void dropTables();
    

    @SqlUpdate(
            "UPDATE edge " +
            "SET edge_order = :edgeOrder " +
            "WHERE id = :id")
    void updateEdgeOrder(
            @Bind("id") long edgeId, 
            @Bind("edgeOrder") Integer edgeOrder);
    
    /*
     * Find Operations
     */
    @SqlQuery(
            "SELECT id " + 
            "FROM vertex " +
            "WHERE id = :id")
    AmberVertex findVertex(
            @Bind("id") long id);
    
    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE id = :id")
    AmberEdge findEdge(
            @Bind("id") long id);


    /*
     * find elements to be staged
     */
    @SqlQuery(
            "SELECT id " +
            "FROM vertex " +
            "WHERE state <> 'AMB'")
    List<AmberVertex> findAlteredVertices();
    
    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE state <> 'AMB'")
    List<AmberEdge> findAlteredEdges();
    
    @SqlQuery(
            "SELECT p.id, p.name, p.type, p.value " +
            "FROM property p, vertex v " +
            "WHERE p.id = v.id " +
            "AND v.state <> 'AMB' " +
            "AND v.state <> 'DEL' " +
            "UNION " +
            "SELECT p.id, p.name, p.type, p.value " +
            "FROM property p, edge e " +
            "WHERE p.id = e.id " +
            "AND e.state <> 'AMB' " +
            "AND e.state <> 'DEL'")
    @Mapper(SessionPropertyMapper.class)
    List<AmberProperty> findAlteredProperties();
    
    @SqlQuery(
            "SELECT id " +
            "FROM vertex " +
            "WHERE state = 'NEW' " +
            "AND id < 0 " +
            "UNION " +
            "SELECT id " +
            "FROM edge " +
            "WHERE state = 'NEW' " +
            "AND id < 0 " +
            "ORDER BY id")
    List<Long> findNewIds();

    @SqlQuery(
            "SELECT id " +
            "FROM vertex " +
            "WHERE state = 'NEW' " +
            "AND id < 0 " +
            "ORDER BY id")
    List<AmberVertex> findNewVertices();
    
    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE state = 'NEW' " +
            "AND id < 0 " +
            "ORDER BY id")
    List<AmberEdge> findNewEdges();
    
    @SqlUpdate(
            "DELETE FROM vertex " +
            "WHERE state = 'DEL'")
    void clearDeletedVertices();

    @SqlUpdate(
            "DELETE FROM edge " +
            "WHERE state = 'DEL'")
    void clearDeletedEdges();
    
    @SqlUpdate(
            "DELETE FROM property " +
            "WHERE id IN " +
            "  (SELECT id FROM edge " +
            "   WHERE state = 'DEL' " +
            "   UNION " +
            "   SELECT id FROM vertex " +
            "   WHERE state = 'DEL')")
    void clearDeletedProperties();
    
    @SqlUpdate(
            "UPDATE vertex " +
            "SET state = 'AMB', " +
            "txn_start = :txnId " +
            "WHERE (state = 'MOD' OR state = 'NEW')")
    void resetModifiedVertices(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "UPDATE edge " +
            "SET state = 'AMB', " +
            "txn_start = :txnId " +
            "WHERE (state = 'MOD' OR state = 'NEW')")
    void resetModifiedEdges(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "UPDATE vertex " +
            "SET id = :newId " +
            "WHERE id = :oldId")
    void updateVertexIds(
            @Bind("oldId") Long oldId, 
            @Bind("newId") Long newId);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET id = :newId " +
            "WHERE id = :oldId")
    void updateEdgeIds(
            @Bind("oldId") Long oldId, 
            @Bind("newId") Long newId);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET v_out = :newId " +
            "WHERE v_out = :oldId")
    void updateEdgeOutIds(
            @Bind("oldId") Long oldId, 
            @Bind("newId") Long newId);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET v_in = :newId " +
            "WHERE v_in = :oldId")
    void updateEdgeInIds(
            @Bind("oldId") Long oldId, 
            @Bind("newId") Long newId);
    
    @SqlUpdate(
            "UPDATE property " +
            "SET id = :newId " +
            "WHERE id = :oldId")
    void updatePropertyIds(
            @Bind("oldId") Long oldId, 
            @Bind("newId") Long newId);
    
    @SqlQuery(
            "SELECT id, name, type, value " +
            "FROM property")
    @Mapper(SessionPropertyMapper.class)
    List<AmberProperty> findProperties();


    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE state <> 'DEL' " +
            "ORDER BY edge_order")
    Iterator<AmberEdge> getEdges();

    @SqlQuery(
            "SELECT e.id " +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND p.name = :name " +
            "AND p.value = :value " +
            "ORDER BY e.edge_order")
    Iterator<AmberEdge> findEdgesWithProperty(
            @Bind("name") String name,
            @Bind("value") byte[] value);
    
    @SqlQuery(
            "SELECT id " +
            "FROM vertex " +
            "WHERE state <> 'DEL'")
    Iterator<AmberVertex> findVertices();

    @SqlQuery(
            "SELECT v.id " +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND p.name = :name " +
            "AND p.value = :value")
    Iterator<AmberVertex> findVerticesWithProperty(
            @Bind("name") String name,
            @Bind("value") byte[] value);

    @SqlBatch(
            "INSERT INTO property (id, name, type, value) " +
            "VALUES (:id, :name, :type, :value)")
    void loadProperties(
            @BindAmberProperty Iterator<AmberProperty> property);

    @SqlUpdate(
            "INSERT INTO property (id, name, type, value) " +
            "VALUES (:id, :name, :type, :value)")
    void loadProperty(
            @BindAmberProperty AmberProperty property);
    
    @SqlBatch(
            "INSERT INTO property (id, txn_start, txn_end, v_out, v_in, label, edge_order, state) " +
            "VALUES (:id, :txn_start, :txn_end, :v_out, :v_in, :label, :edge_order, :state)")
    void loadEdges(
            @BindAmberEdge Iterator<AmberEdge> a);

    @SqlUpdate(
            "DELETE FROM vertex")
    void clearVertices();
    
    @SqlUpdate(
            "DELETE FROM edge")
    void clearEdges();

    @SqlUpdate(
            "DELETE FROM property")
    void clearProperty();

    
    void close();

}

