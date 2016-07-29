package amberdb.v2.model.dao;

import amberdb.v2.model.MovingImageFile;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class MovingImageFileDao implements CrudDao<MovingImageFile> {
    @Override
    @SqlQuery("select * from movingimagefile where id = :id")
    public abstract MovingImageFile get(Long id);

    @Override
    public abstract Long insert(MovingImageFile instance);

    @Override
    public abstract MovingImageFile save(MovingImageFile instance);

    @Override
    @SqlUpdate("delete from movingimagefile where id = :id")
    public abstract void delete(Long id);

    @Override
    @SqlQuery("select * from movingimagefile_history where id = :id")
    public abstract List<MovingImageFile> getHistory(Long id);

    @Override
    public abstract Long insertHistory(MovingImageFile instance);

    @Override
    public abstract MovingImageFile saveHistory(MovingImageFile instance);

    @Override
    @SqlUpdate("delete from movingimagefile_history where id = :id")
    public abstract void deleteHistory(Long id);
}
