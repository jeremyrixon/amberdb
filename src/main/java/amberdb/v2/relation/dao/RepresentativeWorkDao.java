package amberdb.v2.relation.dao;

import amberdb.v2.model.Copy;
import amberdb.v2.model.mapper.ExistsMapper;
import amberdb.v2.relation.model.Representation;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

public abstract class RepresentativeWorkDao {

    @SqlQuery("select * from representative_work where work_id = :id")
    public abstract List<Representation> getRepresentations(@Bind("id") Long id);

    @SqlQuery("select * from representative_work where work_id = :id")
    @RegisterMapper(ExistsMapper.class)
    public abstract boolean isRepresented(@Bind("id") Long id);
}
