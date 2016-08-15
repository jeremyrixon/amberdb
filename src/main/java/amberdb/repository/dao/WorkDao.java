package amberdb.repository.dao;

import amberdb.repository.model.EADWork;
import amberdb.repository.model.Work;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import java.util.List;

public abstract class WorkDao implements Transactional<WorkDao>, GetHandle {

    @SqlQuery("select * from work where id = :id")
    public abstract EADWork getEADWork(@Bind("id") Long id);

    @SqlQuery("select * from work where localSystemNumber = :localSystemNumber")
    public abstract List<Work> getWorksInCollection(@Bind("localSystemNumber") String localSystemNumber);
}
