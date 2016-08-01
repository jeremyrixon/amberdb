package amberdb.v2.relation.dao;

import amberdb.v2.relation.model.WorkCopy;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class WorkCopyDao {

    @SqlQuery("select * from work_copy where work_id = :id")
    public abstract List<WorkCopy> getCopiesByWorkId(@Bind("id") Long id);

    @SqlQuery("select * from work_copy where work_id = :id and copy_role = :role")
    public abstract WorkCopy getCopyByWorkIdAndRole(@Bind("id") Long id, @Bind("role") String copyRole);

    @SqlQuery("select * from work_copy where copy_id = :id;")
    public abstract WorkCopy getWorkByCopyId(@Bind("id") Long id);

    @SqlQuery("select * from work_copy where work_id = :id order by copy_id;")
    public abstract List<WorkCopy> getOrderedCopyIds(@Bind("id") Long id);
}
