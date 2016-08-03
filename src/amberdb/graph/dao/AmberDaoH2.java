package amberdb.graph.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public abstract class AmberDaoH2 extends AmberDao {


    @SqlUpdate("SET @txn = :txnId;\n"

            // edges            
            + "UPDATE edge e "
            + "SET txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.id IN ("
            + "  SELECT id "
            + "  FROM sess_edge "
            + "  WHERE s_id = @txn "
            + "  AND state <> 'AMB');\n"

            // edge properties
            + "UPDATE property p "
            + "SET txn_end = @txn "
            + "WHERE p.txn_end = 0 "
            + "AND p.id IN ("
            + "  SELECT id "
            + "  FROM sess_edge "
            + "  WHERE s_id = @txn "
            + "  AND state <> 'AMB');\n"
            
            // vertices
            + "UPDATE vertex v "
            + "SET txn_end = @txn "
            + "WHERE v.txn_end = 0 "
            + "AND v.id IN ("
            + "  SELECT id "
            + "  FROM sess_vertex "
            + "  WHERE s_id = @txn "
            + "  AND state <> 'AMB');\n"

            // vertex properties
            + "UPDATE property p "
            + "SET txn_end = @txn "
            + "WHERE p.txn_end = 0 "
            + "AND p.id IN ("
            + "  SELECT id "
            + "  FROM sess_vertex "
            + "  WHERE s_id = @txn "
            + "  AND state <> 'AMB');\n"
            
            // orphan edges
            + "UPDATE edge e "
            + "SET txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.v_in IN ("
            + "  SELECT id "
            + "  FROM sess_vertex "
            + "  WHERE state = 'DEL' "
            + "  AND s_id = @txn);\n"

            + "UPDATE edge e "
            + "SET txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.v_out IN ("
            + "  SELECT id "
            + "  FROM sess_vertex "
            + "  WHERE state = 'DEL' "
            + "  AND s_id = @txn);\n")
    public abstract void endElements(
            @Bind("txnId") Long txnId);
    
    @SqlUpdate("SET @txn = :txnId;\n"
            + "UPDATE work_history w "
            + "SET txn_end = @txn "
            + "WHERE w.txn_end = 0 "
            + "AND w.id IN ("
            + "  SELECT id "
            + "  FROM sess_work "
            + "  WHERE s_id = @txn "
            + "  AND state <> 'AMB');\n"
            
    		+ "DELETE FROM work w "
            + "WHERE w.txn_end = 0 "
            + "AND w.id IN ("
            + "  SELECT id "
            + "  FROM sess_work "
            + "  WHERE s_id = @txn "
            + "  AND state = 'DEL');\n")
    public abstract void endWorks(
            @Bind("txnId") Long txnId);
}

