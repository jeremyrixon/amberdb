package amberdb.v2.relation.dao;

import amberdb.v2.relation.model.WorkAcknowledgement;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class WorkAcknowledgementDao {

    @SqlQuery("select * from work_acknowledgement where id = :id")
    public abstract List<WorkAcknowledgement> getOrderedAcknowledgements(Long id);
}
