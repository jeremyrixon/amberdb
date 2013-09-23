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

public interface VertexDao extends Transactional<VertexDao> {

    static final String vertexFields = " id, txn_start, txn_end, state ";
    static final String vertexFieldsV = " v.id, v.txn_start, v.txn_end, v.state ";
    static final String vertexFieldSymbols = " :id, :txn_start, :txn_end, :state ";
    
    static final String edgeFields = " id, txn_start, txn_end, v_out, v_in, label, edge_order, state ";
    static final String edgeFieldSymbols = " :id, :txn_start, :txn_end, :v_out, :v_in, :label, :edge_order, :state ";
    
 
    
    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :id " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findInEdges(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_in = :id " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findInEdges(
            @Bind("id")    long id,
            @Bind("label") String label);

    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :id " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findOutEdges(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT " + edgeFields +
            "FROM edge " +
            "WHERE v_out = :id " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionEdgeMapper.class)
    Iterator<AmberEdge> findOutEdges(
            @Bind("id")    long id,
            @Bind("label") String label);


    @SqlQuery(
            "SELECT " + vertexFieldsV + 
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND v_in = :id " +
            "ORDER BY edge_order")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findInVertices(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT " + vertexFieldsV + 
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND v_in = :id " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findInVertices(
            @Bind("id")    long id,
            @Bind("label") String label);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV + 
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_in " +
            "AND v_out = :id " +
            "ORDER BY edge_order")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findOutVertices(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT " + vertexFieldsV + 
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_in " +
            "AND v_out = :id " +
            "AND label = :label " +
            "ORDER BY edge_order")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findOutVertices(
            @Bind("id")    long id,
            @Bind("label") String label);
    
     
    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex " +
            "WHERE id = :id ")
    @Mapper(SessionVertexMapper.class)
    AmberVertex findVertex(
            @Bind("id") long id);
    
    @SqlQuery(
            "SELECT " + vertexFields +
            "FROM vertex ")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findVertices();
    
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND p.name = :name " +
            "AND p.s_value = :value " +
            "ORDER BY v.id")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithStringProperty(
            @Bind("name") String name,
            @Bind("value") String value);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND p.name = :name " +
            "AND p.b_value = :value " +
            "ORDER BY v.id")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithBooleanProperty(
            @Bind("name") String name,
            @Bind("value") Boolean value);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND p.name = :name " +
            "AND p.i_value = :value " +
            "ORDER BY v.id")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithIntProperty(
            @Bind("name") String name,
            @Bind("value") Integer value);
    
    @SqlQuery(
            "SELECT " + vertexFieldsV +
            "FROM vertex v, property p " +
            "WHERE v.id = p.id " +
            "AND p.name = :name " +
            "AND p.d_value = :value " +
            "ORDER BY v.id")
    @Mapper(SessionVertexMapper.class)
    Iterator<AmberVertex> findVerticesWithDoubleProperty(
            @Bind("name") String name,
            @Bind("value") Double value);
    
  
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO vertex (" + vertexFields + ") " +
            "VALUES (" + vertexFieldSymbols + ")")
    long insertVertex(
            @Bind("id") long id,
            @Bind("txn_start") Long txnStart,
            @Bind("txn_end") Long txnEnd,
            @Bind("state") int state);
    
    
    void close();
}

