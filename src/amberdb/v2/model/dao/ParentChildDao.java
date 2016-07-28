package amberdb.v2.model.dao;

import amberdb.v2.model.Work;
import amberdb.v2.relation.model.ParentChild;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class ParentChildDao {

    @SqlQuery("select * from work where id = (select parent_id from parent_child where child_id = :id")
    public abstract Work getParent(@Bind("id") Long id);

    public abstract List<ParentChild> getChildren(@Bind("id") Long id);

    @SqlQuery("select count(child_id) from parent_child where work_id = :id")
    public abstract Integer getTotalChildCount(@Bind("id") Long workId);

    @SqlQuery("select child_id from parent_child where work_id = :id and chil_biblevel = :bibLevel")
    public abstract Long getChildrenIdsByBibLevel(@Bind("id") Long workId, @Bind("bibLevel") String bibLevel);

    @SqlQuery("select child_id from parent_child where work_id = :id and child_biblevel = :bibLevel and child_subtype in (:subTypes)")
    public abstract Long getChildrenIdsByBibLevelSubType(@Bind("id") Long workId, @Bind("bibLevel") String bibLevel, @Bind("subTypes") String subTypes);
}
