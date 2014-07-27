package amberdb.version.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.version.TEdge;
import amberdb.version.TVertex;
import amberdb.version.AmberTransaction;
import amberdb.version.TEdgeMapper;
import amberdb.version.TVertexMapper;
import amberdb.version.TransactionMapper;


public interface VersionDao extends Transactional<VersionDao> {

    /*
     * DB creation operations (DDL)
     */
    
    /*
     * Main tables
     */
    /* Transaction operations */
    
    
    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = :id")
    @Mapper(TransactionMapper.class)
    AmberTransaction getTransaction(@Bind("id") Long id);
    
    
    @SqlQuery("SELECT DISTINCT t.id, t.time, t.user, t.operation "
            + "FROM transaction t, vertex v "
            + "WHERE v.id = :id "
            + "AND (t.id = v.txn_start OR t.id = v.txn_end) "
            + "ORDER BY t.id")
    @Mapper(TransactionMapper.class)
    List<AmberTransaction> getTransactionsByVertexId(@Bind("id") Long id);
    
    
    @SqlQuery("SELECT DISTINCT t.id, t.time, t.user, t.operation "
            + "FROM transaction t, edge v "
            + "WHERE e.id = :id "
            + "AND (t.id = e.txn_start OR t.id = e.txn_end) "
            + "ORDER BY t.id")
    @Mapper(TransactionMapper.class)
    List<AmberTransaction> getTransactionsByEdgeId(@Bind("id") Long id);
    
    
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


    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = (" 
    		+ "  SELECT MIN(t.id) "
            + "  FROM transaction t, vertex v "
            + "  WHERE v.id = :id "
            + "  AND t.id = v.txn_start)")
    @Mapper(TransactionMapper.class)
    AmberTransaction getFirstTransactionForVertexId(@Bind("id") Long id);
    
    
    @SqlQuery("SELECT id, time, user, operation "
            + "FROM transaction "
            + "WHERE id = (" 
            + "  SELECT MIN(t.id) "
            + "  FROM transaction t, edge e "
            + "  WHERE e.id = :id "
            + "  AND t.id = e.txn_start)")
    @Mapper(TransactionMapper.class)
    AmberTransaction getFirstTransactionForEdgeId(@Bind("id") Long id);

    
    @SqlUpdate("")
    void endElements(
            @Bind("txnId") Long txnId);

    
    void close();
}

