package amberdb.v2.model.dao;

import amberdb.v2.model.File;
import amberdb.v2.relation.model.CopyFile;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class FileDao implements CrudDao<File> {
    @Override
    @SqlQuery("select * from file where id = :id")
    public abstract File get(@Bind("id") Long id);

    @Override
    public abstract Long insert(@BindBean("f") File instance);

    @Override
    public abstract File save(@BindBean("f") File instance);

    @Override
    @SqlUpdate("delete from file where id = :id")
    public abstract void delete(@Bind("id") Long id);

    @Override
    @SqlQuery("select * from file_history where id = :id")
    public abstract List<File> getHistory(@Bind("id") Long id);

    @Override
    public abstract Long insertHistory(@BindBean("f") File instance);

    @Override
    public abstract File saveHistory(@BindBean("f") File instance);

    @Override
    @SqlUpdate("delete from file_history where id = :id")
    public abstract void deleteHistory(@Bind("id") Long id);

    @SqlQuery("select * from copy_file where copy_id = :id")
    public abstract CopyFile getFileByCopyId(@Bind("id") Long id);

    @SqlQuery("select * from copy_file where copy_id = :id and file_type = :type")
    public abstract CopyFile getFileByType(@Bind("id") Long id, @Bind("type") String fileType);

    @SqlQuery("select * from copy_file where file_id = :id")
    public abstract CopyFile getCopyByFileId(@Bind("id") Long id);
}
