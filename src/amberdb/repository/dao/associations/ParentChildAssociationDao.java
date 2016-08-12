package amberdb.repository.dao.associations;

import amberdb.repository.model.Section;
import amberdb.repository.model.Work;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class ParentChildAssociationDao {

    @SqlQuery("select * from work w where w.id = (select f.v_out from flatedge f where f.id = :id and f.type = :relation)")
    public abstract Work getParent(Long id);

    @SqlQuery("select * from work w where w.id in (select f.v_in from flatedge f where f.id = :id and f.type = :relation)")
    public abstract List<Work> getChildren(Long id);

}
