package amberdb.sql.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public abstract class AmberDaoMySql implements AmberDao {

    @SqlUpdate("UPDATE edge e, sess_edge se "
                + "SET e.txn_end = :txnId "
                + "WHERE e.txn_end = 0 "
                + "AND e.id = se.id "
                + "AND se.s_id = :txnId "
                + "AND se.state <> 'AMB'")
    public abstract void updateEdge(@Bind("txnId") Long txnId);
    
    @SqlUpdate("UPDATE property p, sess_edge se "
                + "SET p.txn_end = :txnId "
                + "WHERE p.txn_end = 0 "
                + "AND p.id = se.id "
                + "AND se.s_id = :txnId "
                + "AND se.state <> 'AMB'")
    public abstract void updateEdgeProperties(@Bind("txnId") Long txnId);
    
    @SqlUpdate("UPDATE vertex v, sess_vertex sv "
                + "SET v.txn_end = :txnId "
                + "WHERE v.txn_end = 0 "
                + "AND v.id = sv.id "
                + "AND sv.s_id = :txnId "
                + "AND sv.state <> 'AMB'")
    public abstract void updateVertex(@Bind("txnId") Long txnId);
    
    @SqlUpdate("UPDATE property p, sess_vertex sv "
                + "SET p.txn_end = :txnId "
                + "WHERE p.txn_end = 0 "
                + "AND p.id = sv.id "
                + "AND sv.s_id = :txnId "
                + "AND sv.state <> 'AMB'")
    public abstract void updateVertexProperties(@Bind("txnId") Long txnId);
    
    @SqlUpdate("UPDATE edge e, sess_vertex sv "
                + "SET e.txn_end = :txnId "
                + "WHERE e.txn_end = 0 "
                + "AND e.v_in = sv.id "
                + "AND sv.state = 'DEL' "
                + "AND sv.s_id = :txnId")
    public abstract void updateOrphanEdges(@Bind("txnId") Long txnId);
    
    @SqlUpdate("UPDATE edge e, sess_vertex sv "
            + "SET e.txn_end = :txnId "
            + "WHERE e.txn_end = 0 "
            + "AND e.v_out = sv.id "
            + "AND sv.state = 'DEL' "
            + "AND sv.s_id = :txnId")
    public abstract void updateEdgeFromSessionVertex (@Bind("txnId") Long txnId);
    
    @Override
    public void endElements(
          @Bind("txnId") Long txnId) {
        // edge
        updateEdge(txnId);
        
        // edge properties
        updateEdgeProperties(txnId);
        
        // vertex
        updateVertex(txnId);
        // vertex properties
        updateVertexProperties(txnId);
        // orphan edges
        updateOrphanEdges(txnId);
        //
        updateEdgeFromSessionVertex(txnId);
    }
    
    @SqlUpdate("INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
            + "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
            + "FROM sess_edge "
            + "WHERE s_id = :txnId "
            + "AND (state = 'NEW' OR state = 'MOD')")
    public abstract void insertIntoEdge(@Bind("txnId") Long txnId);
    
    
    @SqlUpdate("INSERT INTO vertex (id, txn_start, txn_end) "
            + "SELECT id, s_id, 0 "
            + "FROM sess_vertex "
            + "WHERE s_id = :txnId "
            + "AND (state = 'NEW' OR state = 'MOD')")
    public abstract void insertIntoVertex(@Bind("txnId") Long txnId);
    
    @SqlUpdate("INSERT INTO property (id, txn_start, txn_end, name, type, value) "
            + "SELECT id, s_id, 0, name, type, value "
            + "FROM sess_property "
            + "WHERE s_id = :txnId")
    public abstract void insertIntoProperty(@Bind("txnId") Long txnId);
    
    @Override
    public void startElements(
            @Bind("txnId") Long txnId) {
        insertIntoEdge(txnId);
        insertIntoVertex(txnId);
        insertIntoProperty(txnId);
    }

    @SqlUpdate("DELETE FROM sess_vertex " +
            "WHERE s_id = :sessId")
    public abstract void deleteFromSessionVertex(@Bind("sessId") Long sessId);
    
    @SqlUpdate("DELETE FROM sess_edge " +
            "WHERE s_id = :sessId")
    public abstract void deleteFromSessionEdge(@Bind("sessId") Long sessId);
    
    @SqlUpdate("DELETE FROM sess_property " +
            "WHERE s_id = :sessId")
    public abstract void deleteFromSessionProperty(@Bind("sessId") Long sessId);
    
    @Override
    public void clearSession(
            @Bind("sessId") Long sessId) {
        deleteFromSessionVertex(sessId);
        deleteFromSessionEdge(sessId);
        deleteFromSessionProperty(sessId);
    }
}

