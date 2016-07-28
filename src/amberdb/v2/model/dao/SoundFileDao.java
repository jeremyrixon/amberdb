package amberdb.v2.model.dao;

import amberdb.v2.model.SoundFile;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class SoundFileDao implements CrudDao<SoundFile> {
    @Override
    @SqlQuery("select * from soundfile where id = :id")
    public abstract SoundFile get(@Bind("id") Long id);

    @Override
    public abstract Long insert(@BindBean("s") SoundFile instance);

    @Override
    public abstract SoundFile save(@BindBean("s") SoundFile instance);

    @Override
    @SqlUpdate("delete from soundfile where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from soundfile_history where id = :id")
    public abstract List<SoundFile> getHistory(@Bind("id") Long id);

    @Override
    public abstract Long insertHistory(@BindBean("s") SoundFile instance);

    @Override
    public abstract SoundFile saveHistory(@BindBean("s") SoundFile instance);

    @Override
    @SqlUpdate("select * from soundfile_history where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);
}
