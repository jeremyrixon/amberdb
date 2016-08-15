package amberdb.repository.dao.associations;

import amberdb.repository.model.Work;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class DeliveryWorkAssociationDao {

    @SqlQuery("select * from work w where w.id in (select v_out from flatedge f where f.id = :id and f.type = 'deliveredOn' order by f.edge_order)")
    public abstract List<Work> getDeliveryWorks(@Bind("id") Long id);

    @SqlQuery("select * from work w where w.id = (select v_in from flatedge f where f.id = :id and f.type = 'deliveredOn')")
    public abstract Work getDeliveryWorkParent(@Bind("id") Long id);

    @SqlUpdate("update flatedge set edge_order = :position where v_out = :id and type = 'deliveredOn'")
    public abstract void setDeliveryWorkOrder(@Bind("id") Long id, @Bind("position") int position);
}
