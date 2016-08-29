package amberdb.v2.relation.dao;

import amberdb.v2.relation.model.CopyFile;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public abstract class CopyFileDao {

    @SqlQuery("select * from copy_file where copy_id = :id")
    public abstract CopyFile getFileByCopyId(@Bind("id") Long id);

    @SqlQuery("select * from copy_file where copy_id = :id and file_type = :type")
    public abstract CopyFile getFileByType(@Bind("id") Long id, @Bind("type") String fileType);

    @SqlQuery("select * from copy_file where file_id = :id")
    public abstract CopyFile getCopyByFileId(@Bind("id") Long id);
}
