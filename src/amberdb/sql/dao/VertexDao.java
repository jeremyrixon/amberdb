package amberdb.sql.dao;

import java.util.List;
import java.util.Set;

import  amberdb.sql.*;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.util.StringMapper;

public interface VertexDao extends Transactional<VertexDao> {
    
    void close();

    
    @SqlUpdate(
            "UPDATE vertex " +
            "SET id = :newId " +
            "WHERE id = :id")
    void changeVertexId(
            @Bind("id") long id, 
            @Bind("newId") long newId);
    
    @SqlUpdate(
            "UPDATE property " +
            "SET id = :newId " +
            "WHERE id = :id")
    void changeVertexPropertyIds(
            @Bind("id") long id, 
            @Bind("newId") long newId);
    
    @SqlUpdate(
            "INSERT INTO vertex (id, txn_start, txn_end, state) " +
            "VALUES (:id, :txn_start, :txn_end, :state)")
    void insertVertex(
            @Bind("id") long id,
            @Bind("txn_start") Long txnStart,
            @Bind("txn_end") Long txnEnd,
            @Bind("state") String state);

    @SqlUpdate(
            "UPDATE vertex " +
            "SET state = :newState " +
            "WHERE id = :id")
    void setVertexState(
            @Bind("id") long id,
            @Bind("newState") String newState);

    @SqlQuery(
            "SELECT state " +
            "FROM vertex " +
            "WHERE id = :id")
    @Mapper(StringMapper.class)
    String getVertexState(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT id, name, type, value " +
            "FROM property " +
            "WHERE name = :propertyName " +
            "AND id = :id")
    @Mapper(SessionPropertyMapper.class)
    AmberProperty getProperty(
            @Bind("id") long id, 
            @Bind("propertyName") String propertyName);

    @SqlQuery(
            "SELECT name " +
            "FROM property " +
            "WHERE id = :id")
    @Mapper(StringMapper.class)
    Set<String> getPropertyKeys(
            @Bind("id") long id);

    @SqlUpdate(
            "DELETE FROM property " +
            "WHERE id = :id " +
            "AND name = :propertyName")
    void removeProperty(
            @Bind("id") long id, 
            @Bind("propertyName") String propertyName);

    @SqlUpdate(
            "INSERT INTO property (id, name, type, value) " +
            "VALUES (:id, :name, :type, :value)")
    void setProperty(
            @Bind("id")    long id,
            @Bind("name")  String propertyName, 
            @Bind("type")  String type, 
            @Bind("value") byte[] value);
    
    @SqlUpdate(
            "DELETE FROM vertex " +
            "WHERE id = :id")
    void removeVertex(
            @Bind("id") long id);
    
    @SqlUpdate(
            "DELETE FROM property " +
            "WHERE id = :id")
    void removeVertexProperties(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT txn_start " +
            "FROM vertex " +
            "WHERE id = :id")
    Long getVertexTxnStart(
            @Bind("id") long id);
    
    @SqlUpdate(
            "UPDATE vertex " +
            "SET txn_start = :txnStart " +
            "WHERE id = :id")
    void setVertexTxnStart(
            @Bind("id") long id,
            @Bind("txnStart") Long txnStart);
    
    @SqlQuery(
            "SELECT txn_end " +
            "FROM vertex " +
            "WHERE id = :id")
    Long getVertexTxnEnd(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "ORDER BY edge_order")
    List<AmberEdge> findInEdges(
            @Bind("vertexId") long id);
    
    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "ORDER BY edge_order")
    List<AmberEdge> findOutEdges(
            @Bind("vertexId") long id);

    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    List<AmberEdge> findInEdges(
            @Bind("vertexId") long id,
            @Bind("label") String label);
    
    @SqlQuery(
            "SELECT id " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    List<AmberEdge> findOutEdges(
            @Bind("vertexId") long vertexId,
            @Bind("label") String label);

    @SqlQuery(
            "SELECT v_out id " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "ORDER BY edge_order")
    List<AmberVertex> findInVertices(
            @Bind("vertexId") long vertexId);
    
    @SqlQuery(
            "SELECT v_in id " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "ORDER BY edge_order")
    List<AmberVertex> findOutVertices(
            @Bind("vertexId") long vertexId);

    @SqlQuery(
            "SELECT v_out id " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    List<AmberVertex> findInVertices(
            @Bind("vertexId") long vertexId,
            @Bind("label") String label);
    
    @SqlQuery(
            "SELECT v_in id " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "AND label = :label " +
            "ORDER BY edge_order")
    List<AmberVertex> findOutVertices(
            @Bind("vertexId") long id,
            @Bind("label") String label);

    @SqlUpdate(
            "UPDATE edge " +
            "SET v_in = :newId " +
            "WHERE v_in = :id")
    void changeInVertexIds(
            @Bind("id") long id, 
            @Bind("newId") long newId);

    @SqlUpdate(
            "UPDATE edge " +
            "SET v_out = :newId " +
            "WHERE v_out = :id")
    void changeOutVertexIds(
            @Bind("id") long id, 
            @Bind("newId") long newId);

}

