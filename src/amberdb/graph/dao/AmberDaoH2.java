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
    
    
    @SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE work_history h "
    		 + "SET txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id IN (SELECT id FROM sess_work WHERE s_id = @txn AND STATE <> 'AMB');"
    		 + " "
    		 + "DELETE FROM work c "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id IN (SELECT id FROM sess_work WHERE s_id = @txn AND STATE = 'DEL');")
    		public abstract void endWorks(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE file_history h "
    		 + "SET txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id IN (SELECT id FROM sess_file WHERE s_id = @txn AND STATE <> 'AMB');"
    		 + " "
    		 + "DELETE FROM file c "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id IN (SELECT id FROM sess_file WHERE s_id = @txn AND STATE = 'DEL');")
    		public abstract void endFiles(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE description_history h "
    		 + "SET txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id IN (SELECT id FROM sess_description WHERE s_id = @txn AND STATE <> 'AMB');"
    		 + " "
    		 + "DELETE FROM description c "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id IN (SELECT id FROM sess_description WHERE s_id = @txn AND STATE = 'DEL');")
    		public abstract void endDescriptions(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE party_history h "
    		 + "SET txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id IN (SELECT id FROM sess_party WHERE s_id = @txn AND STATE <> 'AMB');"
    		 + " "
    		 + "DELETE FROM party c "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id IN (SELECT id FROM sess_party WHERE s_id = @txn AND STATE = 'DEL');")
    		public abstract void endParties(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE tag_history h "
    		 + "SET txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id IN (SELECT id FROM sess_tag WHERE s_id = @txn AND STATE <> 'AMB');"
    		 + " "
    		 + "DELETE FROM tag c "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id IN (SELECT id FROM sess_tag WHERE s_id = @txn AND STATE = 'DEL');")
    		public abstract void endTags(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    				 + "UPDATE flatedge_history h "
    				 + "SET txn_end = @txn "
    				 + "WHERE h.txn_end = 0 "
    				 + "AND h.id IN (SELECT id FROM sess_flatedge WHERE s_id = @txn AND STATE <> 'AMB');"
    				 + " "
    				 + "DELETE FROM flatedge c "
    				 + "WHERE c.txn_end = 0 "
    				 + "AND c.id IN (SELECT id FROM sess_flatedge WHERE s_id = @txn AND STATE = 'DEL');")
    				public abstract void endFlatedges(
    				@Bind("txnId") Long txnId);   
}

