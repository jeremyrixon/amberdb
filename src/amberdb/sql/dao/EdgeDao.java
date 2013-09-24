package amberdb.sql.dao;

import java.util.Iterator;
import java.util.List;

import  amberdb.sql.*;
import amberdb.sql.Stateful.State;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.util.StringMapper;

public interface EdgeDao extends Transactional<EdgeDao> {

    static final String vertexFields = " id, txn_start, txn_end, state ";
    static final String vertexFieldSymbols = " :id, :txn_start, :txn_end, :state ";
    
    static final String edgeFields = " id, txn_start, txn_end, v_out, v_in, label, edge_order, state ";
    static final String edgeFieldsE = " e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, e.state ";
    static final String edgeFieldSymbols = " :id, :txn_start, :txn_end, :v_out, :v_in, :label, :edge_order, :state ";

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
    
    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE id = :id ")
    @Mapper(SessionVertexMapper.class)
    AmberVertex findVertex(
            @Bind("id") long id);
    
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE id = :id ")
    @Mapper(SessionEdgeMapper.class)
    AmberEdge findEdge(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge ")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findEdges();
    
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO edge (" + edgeFields + ") " +
            "VALUES (" + edgeFieldSymbols + ")")
    long insertEdge(
            @Bind("id") long id,
            @Bind("txn_start") Long txnStart,
            @Bind("txn_end") Long txnEnd,
            @Bind("v_out") long outId,
            @Bind("v_in") long inId,
            @Bind("label") String label,
            @Bind("edge_order") int edgeOrder,
            @Bind("state") int state);
    
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND p.name = :name " +
            "AND p.s_value = :value " +
            "ORDER BY e.id")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithStringProperty(
            @Bind("name") String name,
            @Bind("value") String value);
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND p.name = :name " +
            "AND p.b_value = :value " +
            "ORDER BY e.id")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithBooleanProperty(
            @Bind("name") String name,
            @Bind("value") Boolean value);
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND p.name = :name " +
            "AND p.i_value = :value " +
            "ORDER BY e.id")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithIntProperty(
            @Bind("name") String name,
            @Bind("value") Integer value);
    
    @SqlQuery(
            "SELECT " + edgeFieldsE +
            "FROM edge e, property p " +
            "WHERE e.id = p.id " +
            "AND p.name = :name " +
            "AND p.d_value = :value " +
            "ORDER BY e.id")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findEdgesWithDoubleProperty(
            @Bind("name") String name,
            @Bind("value") Double value);
    
    @SqlQuery(
            "SELECT name " +
            "FROM property " +
            "WHERE id = :elementId")
    @Mapper(StringMapper.class)
    List<String> getPropertyKeys(
            @Bind("elementId") long elementId);
    
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET edge_order = :edgeOrder " +
            "WHERE id = :id ")
    void updateEdgeOrder(
            @Bind("id") long id,
            @Bind("edgeOrder") int edgeOrder);
    
    @SqlQuery(
            "SELECT edge_order " +
            "FROM edge " +
            "WHERE id = :id ")
    int getEdgeOrder(
            @Bind("id") long id);
    
    
    
    void close();
}

