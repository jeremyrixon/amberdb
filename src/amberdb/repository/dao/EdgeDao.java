package amberdb.repository.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public abstract class EdgeDao {

    public void setOrder(Long id, Long txnStart, Long txnEnd, String direction, int edgeOrder) {
        if ("in".equalsIgnoreCase(direction)) {
            setOrderIn(id, txnStart, txnEnd, edgeOrder);
        } else {
            setOrderOut(id, txnStart, txnEnd, edgeOrder);
        }
    }

    @SqlUpdate("update flatedge set txn_start = :txnStart, txn_end = :txnEnd, edge_order = :edgeOrder where v_in = :id")
    public abstract void setOrderIn(@Bind("id") Long id, @Bind("txnStart") Long txnStart, @Bind("txnEnd") Long txnEnd, @Bind("edgeOrder") int edgeOrder);

    @SqlUpdate("update flatedge set txn_start = :txnStart, txn_end = :txnEnd, edge_order = :edgeOrder where v_out = :id")
    public abstract void setOrderOut(@Bind("id") Long id, @Bind("txnStart") Long txnStart, @Bind("txnEnd") Long txnEnd, @Bind("edgeOrder") int edgeOrder);

    @SqlQuery("select edge_order from flatedge where type = :type and v_in = :id")
    public abstract int getOrderIn(@Bind("id") Long id, @Bind("type") String type);

    @SqlQuery("select edge_order from flatedge where type = :type and v_out = :id")
    public abstract int getOrderOut(@Bind("id") Long id, @Bind("type") String type);

}
