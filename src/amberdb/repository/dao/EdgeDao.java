package amberdb.repository.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public abstract class EdgeDao {

    @SqlUpdate("update flatedge set edge_order = :order where v_in = :id and type = :type")
    public abstract void setOrderIn(@Bind("id") Long id, @Bind("type") String type, @Bind("order") int edgeOrder);

    @SqlUpdate("update flatedge set edge_order = :order where v_out = :id and type = :type")
    public abstract void setOrderOut(@Bind("id") Long id, @Bind("type") String type, @Bind("order") int edgeOrder);

    @SqlQuery("select edge_order from flatedge where v_in = :id and type = :type")
    public abstract int getOrderIn(@Bind("id") Long id, @Bind("type") String type);

    @SqlQuery("select edge_order from flatedge where v_out = :id and type = :type")
    public abstract int getOrderOut(@Bind("id") Long id, @Bind("type") String type);

    @SqlUpdate("update flatedge set edge_order = :position where id = :id")
    public abstract void setOrder(@Bind("id") Long id, @Bind("position") int position);

    @SqlQuery("select count(id) from flatedge where v_out = :id and type = 'existsOn'")
    public abstract int countExistsOn(@Bind("id") Long id);

}
