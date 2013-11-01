package amberdb.sql.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface PersistentDaoH2 extends PersistentDao {

    @SqlUpdate(
            "INSERT INTO property (id, txn_start, name, type, value) " +
            "SELECT id, txn_new, name, type, value " +
            "FROM stage_property " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_edge " +
            "  WHERE state <> 'DEL' " +
            "  AND txn_new = :txnId) " +
            "AND txn_new = :txnId")
    int insertStagedEdgeProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "INSERT INTO property (id, txn_start, name, type, value) " +
            "SELECT id, txn_new, name, type, value " +
            "FROM stage_property " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_vertex " +
            "  WHERE state <> 'DEL' " +
            "  AND txn_new = :txnId) " +
            "AND txn_new = :txnId")
    int insertStagedVertexProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "UPDATE vertex " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_vertex " +
            "  WHERE state <> 'NEW' " +
            "  AND txn_new = :txnId) " +
            "AND (txn_end = 0 OR txn_end IS NULL)")
    int updateSupercededVertices(
            @Bind("txnId") long txnId);
    
    @SqlUpdate(
            "UPDATE edge " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_edge " +
            "  WHERE state <> 'NEW' " +
            "  AND txn_new = :txnId) " +
            "AND (txn_end = 0 OR txn_end IS NULL)")
    int updateSupercededEdges(
            @Bind("txnId") long txnId);

    @SqlUpdate(            
            "UPDATE property p " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_edge " +
            "  WHERE state <> 'NEW' " +
            "  AND txn_new = :txnId) " +
            "AND (p.txn_end = 0 OR p.txn_end IS NULL)")
    int updateSupercededEdgeProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate(            
            "UPDATE property p " +
            "SET txn_end = :txnId " +
            "WHERE id IN (" +
            "  SELECT id " +
            "  FROM stage_vertex " +
            "  WHERE state <> 'NEW' " +
            "  AND txn_new = :txnId) " +
            "AND (p.txn_end = 0 OR p.txn_end IS NULL)")
    int updateSupercededVertexProperties(
            @Bind("txnId") long txnId);

    @SqlQuery(            
            "SELECT (COUNT(table_name) = 8) " +
            "FROM information_schema.tables " + 
            "WHERE table_name IN (" +
            "  'VERTEX', 'EDGE', 'PROPERTY', " +
            "  'STAGE_EDGE', 'STAGE_VERTEX', 'STAGE_PROPERTY', " +
            "  'ID_GENERATOR', 'TRANSACTION')")
    boolean schemaTablesExist();
    
}

