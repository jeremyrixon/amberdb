package amberdb.v2.relation.dao;

import amberdb.v2.relation.model.WorkDeliveryWork;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class DeliveryWorkDao {
    @SqlQuery("select * from work_deliverywork where deliverywork_id = :id")
    public abstract WorkDeliveryWork getDeliveryWorkParent(@Bind("id") Long id);

    @SqlQuery("select * from work_deliverywork where work_id = :id")
    public abstract List<WorkDeliveryWork> getDeliveryWorks(@Bind("id") Long id);

    @SqlQuery("select * from work_deliverywork where deliverywork_id = :id")
    public abstract WorkDeliveryWork getDeliveryWork(@Bind("id") Long id);
}
