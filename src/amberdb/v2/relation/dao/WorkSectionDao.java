package amberdb.v2.relation.dao;

import amberdb.v2.relation.model.WorkSection;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class WorkSectionDao {
    @SqlQuery("select * from work_section where work_id = :id and subType = :type")
    public abstract List<WorkSection> getSectionByWorkId(@Bind("id") Long id, @Bind("type") String subType);
}
