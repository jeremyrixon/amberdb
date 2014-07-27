package amberdb.version.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.version.TEdge;
import amberdb.version.TVertex;
import amberdb.version.TEdgeMapper;
import amberdb.version.TVertexMapper;


public interface VersionDao extends Transactional<VersionDao> {

    
    @SqlQuery("SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' "
            + "FROM transaction t, vertex v "
            + "WHERE t.id = :id "
            + "AND (v.txn_start = t.id OR e.txn_end = t.id) "
            + "ORDER BY t.id")
    @Mapper(TVertexMapper.class)
    List<TVertex> getVerticesByTransactionId(@Bind("id") Long id);
    

    @SqlQuery("SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' "
            + "FROM transaction t, edge e "
            + "WHERE t.id = :id "
            + "AND (e.txn_start = t.id OR e.txn_end = t.id) "
            + "ORDER BY t.id")
    @Mapper(TEdgeMapper.class)
    List<TEdge> getEdgesByTransactionId(@Bind("id") Long id);


    @SqlUpdate("")
    void endElements(
            @Bind("txnId") Long txnId);

    
    void close();
}

