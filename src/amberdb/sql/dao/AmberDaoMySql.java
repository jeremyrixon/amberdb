package amberdb.sql.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public interface AmberDaoMySql extends AmberDao {


    @SqlUpdate("SET @txn = :txnId;\n"

            // edges
            + "UPDATE edge e, sess_edge se "
            + "SET e.txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.id = se.id "
            + "AND se.s_id = @txn "
            + "AND se.state <> 'NEW';\n"

            // edge properties
            + "UPDATE property p, sess_edge se "
            + "SET p.txn_end = @txn "
            + "WHERE p.txn_end = 0 "
            + "AND p.id = se.id "
            + "AND se.s_id = @txn "
            + "AND se.state <> 'NEW';\n"

            // vertices
            + "UPDATE vertex v, sess_vertex sv "
            + "SET v.txn_end = @txn "
            + "WHERE v.txn_end = 0 "
            + "AND v.id = sv.id "
            + "AND sv.s_id = @txn "
            + "AND sv.state <> 'NEW';\n"

            // vertex properties
            + "UPDATE property p, sess_vertex sv "
            + "SET p.txn_end = @txn "
            + "WHERE p.txn_end = 0 "
            + "AND p.id = sv.id "
            + "AND sv.s_id = @txn "
            + "AND sv.state <> 'NEW';\n"

            // orphan edges
            + "UPDATE edge e, sess_vertex sv "
            + "SET e.txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.v_in = sv.id "
            + "AND sv.state = 'DEL' "
            + "AND sv.s_id = @txn;\n"

            + "UPDATE edge e, sess_vertex sv "
            + "SET e.txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.v_out = sv.id "
            + "AND sv.state = 'DEL' "
            + "AND sv.s_id = @txn;\n")
    void endElements(
          @Bind("txnId") Long txnId);
}

