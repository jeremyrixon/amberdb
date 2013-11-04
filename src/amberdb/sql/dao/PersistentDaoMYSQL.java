package amberdb.sql.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface PersistentDaoMYSQL extends PersistentDao {
    
    @SqlUpdate(
            "INSERT INTO property (id, txn_start, name, type, value) " +
            "SELECT p.id, p.txn_new, p.name, p.type, p.value " +
            "FROM stage_property p, stage_edge e " +
            "WHERE p.id = e.id " +
            "AND e.state <> 'DEL' " +
            "AND e.txn_new = :txnId " +
            "AND p.txn_new = :txnId")
    int insertStagedEdgeProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "INSERT INTO property (id, txn_start, name, type, value) " +
            "SELECT p.id, p.txn_new, p.name, p.type, p.value " +
            "FROM stage_property p, stage_vertex v " +
            "WHERE p.id = v.id " +
            "AND v.state <> 'DEL' " +
            "AND v.txn_new = :txnId " +
            "AND p.txn_new = :txnId")
    int insertStagedVertexProperties(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "UPDATE vertex v ,stage_vertex s " +
            "SET v.txn_end = :txnId " +
            "WHERE v.id = s.id " +
            "AND s.state <> 'NEW' " +
            "AND s.txn_new = :txnId " +
            "AND (v.txn_end = 0 OR v.txn_end IS NULL)")
    int updateSupercededVertices(
            @Bind("txnId") long txnId);
    
    @SqlUpdate(
            "UPDATE edge e, stage_edge s " +
            "SET e.txn_end = :txnId " +
            "WHERE e.id = s.id " +
            "AND s.state <> 'NEW' " +
            "AND s.txn_new = :txnId " +
            "AND (e.txn_end = 0 OR e.txn_end IS NULL)")
    int updateSupercededEdges(
            @Bind("txnId") long txnId);

    @SqlUpdate(
            "UPDATE property p, stage_edge e " + 
            "SET p.txn_end = :txnId " + 
            "WHERE p.id = e.id " + 
            "AND e.state <> 'NEW' " + 
            "AND e.txn_new = :txnId " +
            "AND (p.txn_end = 0 OR p.txn_end IS NULL)")
    int updateSupercededEdgeProperties(@Bind("txnId") long txnId);
    
    @SqlUpdate(
            "UPDATE property p, stage_vertex v " + 
            "SET p.txn_end = :txnId " + 
            "WHERE p.id = v.id " + 
            "AND v.state <> 'NEW' " + 
            "AND v.txn_new = :txnId " +
            "AND (p.txn_end = 0 OR p.txn_end IS NULL)")
    int updateSupercededVertexProperties(@Bind("txnId") long txnId);
    
    @SqlQuery(            
            "SELECT (COUNT(table_name) = 8) " +
            "FROM information_schema.tables " + 
            "WHERE table_name IN (" +
            "  'VERTEX', 'EDGE', 'PROPERTY', " +
            "  'STAGE_EDGE', 'STAGE_VERTEX', 'STAGE_PROPERTY', " +
            "  'ID_GENERATOR', 'TRANSACTION')")
    boolean schemaTablesExist();

}

