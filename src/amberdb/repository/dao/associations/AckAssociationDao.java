package amberdb.repository.dao.associations;

import amberdb.repository.model.Acknowledge;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class AckAssociationDao {

    @SqlQuery("select * from acknowledge a where a.id in (select f.v_out from flatedge f where f.id = :id and f.type = 'acknowledge')")
    public abstract List<Acknowledge> getAcknowledgements(Long id);

    @SqlQuery("select * from acknowledge a where a.id in (select f.v_out from flatedge f where f.id = :id and f.type = 'acknowledge') order by a.weighting")
    public abstract List<Acknowledge> getOrderedAcknowledgements(Long id);
}
