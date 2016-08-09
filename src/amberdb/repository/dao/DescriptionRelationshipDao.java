package amberdb.repository.dao;

import amberdb.repository.model.Description;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class DescriptionRelationshipDao {

    @SqlQuery("select * from description where id in (select v_out from flatedge where v_in = :id and type = 'descriptionOf'")
    public abstract List<Description> getDescriptions(@Bind("id") Long id);
}
