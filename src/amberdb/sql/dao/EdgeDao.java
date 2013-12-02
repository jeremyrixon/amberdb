package amberdb.sql.dao;

import java.util.Set;

import  amberdb.sql.*;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.StringMapper;

public interface EdgeDao extends Transactional<EdgeDao> {
    
    void close();
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET id = :newId " +
            "WHERE id = :id")
    void changeEdgeId(
            @Bind("id") long id, 
            @Bind("newId") long newId);
    
    @SqlUpdate(
            "UPDATE property " +
            "SET id = :newId " +
            "WHERE id = :id")
    void changeEdgePropertyIds(
            @Bind("id") long id, 
            @Bind("newId") long newId);
    
    @SqlUpdate(
            "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order, state) " +
            "VALUES (:id, :txn_start, :txn_end, :v_out, :v_in, :label, :edge_order, :state)")
    void insertEdge(
            @Bind("id") long id,
            @Bind("txn_start") Long txnStart,
            @Bind("txn_end") Long txnEnd,
            @Bind("v_out") Long outVertexId,
            @Bind("v_in") Long inVertexId,
            @Bind("label") String label,
            @Bind("edge_order") Integer edgeOrder,
            @Bind("state") String state);

    @SqlUpdate(
            "UPDATE edge " +
            "SET state = :newState " +
            "WHERE id = :id")
    void setEdgeState(
            @Bind("id") long id,
            @Bind("newState") String newState);

    @SqlQuery(
            "SELECT state " +
            "FROM edge " +
            "WHERE id = :id")
//    @Mapper(StringMapper.class)
    String getEdgeState(
            @Bind("id") long id);

    @SqlUpdate(
            "UPDATE edge " +
            "SET edge_order = :edgeOrder " +
            "WHERE id = :id")
    void setEdgeOrder(
            @Bind("id") long id,
            @Bind("edgeOrder") Integer edgeOrder);

    @SqlQuery(
            "SELECT edge_order " +
            "FROM edge " +
            "WHERE id = :id")
    @Mapper(IntegerMapper.class)
    Integer getEdgeOrder(
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
            "DELETE FROM edge " +
            "WHERE id = :id")
    void removeEdge(
            @Bind("id") long id);
    
    @SqlUpdate(
            "DELETE FROM property " +
            "WHERE id = :id")
    void removeEdgeProperties(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT label " +
            "FROM edge " +
            "WHERE id = :id")
    String getEdgeLabel(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT v_out " +
            "FROM edge " +
            "WHERE id = :id")
    long getOutVertexId(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT v_in " +
            "FROM edge " +
            "WHERE id = :id")
    long getInVertexId(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT state " +
            "FROM vertex " +
            "WHERE id = :id")
    @Mapper(StringMapper.class)
    String getVertexState(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT txn_start " +
            "FROM edge " +
            "WHERE id = :id")
    Long getEdgeTxnStart(
            @Bind("id") long id);

    @SqlUpdate(
            "UPDATE edge " +
            "SET txn_start = :txnStart " +
            "WHERE id = :id")
    void setEdgeTxnStart(
            @Bind("id") long id,
            @Bind("txnStart") Long txnStart);
    
    @SqlQuery(
            "SELECT txn_end " +
            "FROM edge " +
            "WHERE id = :id")
    Long getEdgeTxnEnd(
            @Bind("id") long id);

}

