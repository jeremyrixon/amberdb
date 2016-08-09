package amberdb.graph.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public abstract class AmberDaoMySql extends AmberDao {


    @SqlUpdate("SET @txn = :txnId;\n"

            // edges
            + "UPDATE edge e, sess_edge se "
            + "SET e.txn_end = @txn "
            + "WHERE e.txn_end = 0 "
            + "AND e.id = se.id "
            + "AND se.s_id = @txn "
            + "AND se.state <> 'AMB';\n"

            // edge properties
            + "UPDATE property p, sess_edge se "
            + "SET p.txn_end = @txn "
            + "WHERE p.txn_end = 0 "
            + "AND p.id = se.id "
            + "AND se.s_id = @txn "
            + "AND se.state <> 'AMB';\n"

            // vertices
            + "UPDATE vertex v, sess_vertex sv "
            + "SET v.txn_end = @txn "
            + "WHERE v.txn_end = 0 "
            + "AND v.id = sv.id "
            + "AND sv.s_id = @txn "
            + "AND sv.state <> 'AMB';\n"

            // vertex properties
            + "UPDATE property p, sess_vertex sv "
            + "SET p.txn_end = @txn "
            + "WHERE p.txn_end = 0 "
            + "AND p.id = sv.id "
            + "AND sv.s_id = @txn "
            + "AND sv.state <> 'AMB';\n"

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
    public abstract void endElements(
          @Bind("txnId") Long txnId);
    
    
    @SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE work_history h, sess_work s "
    		 + "SET h.txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state <> 'AMB'; "
    		 + " "
    		 + "DELETE c "
    		 + "FROM work c, sess_work s "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state = 'DEL';")
    		public abstract void endWorks(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE file_history h, sess_file s "
    		 + "SET h.txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state <> 'AMB'; "
    		 + " "
    		 + "DELETE c "
    		 + "FROM file c, sess_file s "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state = 'DEL';")
    		public abstract void endFiles(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE description_history h, sess_description s "
    		 + "SET h.txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state <> 'AMB'; "
    		 + " "
    		 + "DELETE c "
    		 + "FROM description c, sess_description s "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state = 'DEL';")
    		public abstract void endDescriptions(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE party_history h, sess_party s "
    		 + "SET h.txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state <> 'AMB'; "
    		 + " "
    		 + "DELETE c "
    		 + "FROM party c, sess_party s "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state = 'DEL';")
    		public abstract void endParties(
    		@Bind("txnId") Long txnId);

    		@SqlUpdate("SET @txn = :txnId;"
    		 + "UPDATE tag_history h, sess_tag s "
    		 + "SET h.txn_end = @txn "
    		 + "WHERE h.txn_end = 0 "
    		 + "AND h.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state <> 'AMB'; "
    		 + " "
    		 + "DELETE c "
    		 + "FROM tag c, sess_tag s "
    		 + "WHERE c.txn_end = 0 "
    		 + "AND c.id = s.id "
    		 + "AND s.s_id = @txn "
    		 + "AND s.state = 'DEL';")
    		public abstract void endTags(
    		@Bind("txnId") Long txnId);
    

    		@SqlUpdate("SET @txn = :txnId;"
    				 + "UPDATE flatedge_history h, sess_flatedge s "
    				 + "SET h.txn_end = @txn "
    				 + "WHERE h.txn_end = 0 "
    				 + "AND h.id = s.id "
    				 + "AND s.s_id = @txn "
    				 + "AND s.state <> 'AMB'; "
    				 + " "
    				 + "DELETE c "
    				 + "FROM flatedge c, sess_flatedge s "
    				 + "WHERE c.txn_end = 0 "
    				 + "AND c.id = s.id "
    				 + "AND s.s_id = @txn "
    				 + "AND s.state = 'DEL';")
    				public abstract void endFlatedges(
    				@Bind("txnId") Long txnId);
}

