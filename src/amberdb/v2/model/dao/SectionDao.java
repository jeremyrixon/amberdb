package amberdb.v2.model.dao;

import amberdb.v2.model.Section;
import amberdb.v2.relation.model.WorkSection;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class SectionDao implements CrudDao<Section> {

    @Override
    @SqlQuery("select * from section where id = :id")
    public abstract Section get(@Bind("id") Long id);

    @Override
    public abstract Long insert(@BindBean("s") Section instance);

    @Override
    public abstract Section save(@BindBean("s") Section instance);

    @Override
    @SqlUpdate("delete from section where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from section_history where id = :id")
    public abstract List<Section> getHistory(@Bind("id") Long id);

    @Override
    public abstract Long insertHistory(@BindBean("s") Section instance);

    @Override
    public abstract Section saveHistory(@BindBean("s") Section instance);

    @Override
    @SqlUpdate("delete from section_history where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);

    @SqlQuery("select * from work_section where work_id = :id and subType = :type")
    public abstract List<WorkSection> getSectionByWorkId(@Bind("id") Long id, @Bind("type") String subType);
}
