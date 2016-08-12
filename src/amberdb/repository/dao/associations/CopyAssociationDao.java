package amberdb.repository.dao.associations;

import amberdb.repository.model.Copy;
import amberdb.repository.model.File;
import amberdb.repository.model.Work;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class CopyAssociationDao {
    @SqlQuery("select * from work where id = (select v_out from flatedge where v_in = :id and type = 'isCopyOf' limit 1) and copyRole = :role ")
    public abstract Copy getCopy(@Bind("id") Long id, @Bind("role") String role);

    @SqlQuery("select * from work where id in (select v_out from flatedge where v_in = :id and type = 'isCopyOf' order by edge_order)")
    public abstract List<Copy> getCopies(@Bind("id") Long id);

    @SqlQuery("select * from work where id in (select v_out from flatedge where v_in = :id and type = 'isCopyOf' order by edge_order) and copyRole = :role")
    public abstract List<Copy> getCopies(@Bind("id") Long id, @Bind("role") String role);

    @SqlQuery("select * from work where id = (select v_in from flatedge where v_out = :id and type = 'isDerivativeOf')")
    public abstract Copy getSourceCopy(@Bind("id") Long id);

    @SqlQuery("select * from work where id in (select v_out from flatedge where v_in = :id and type = 'isDerivativeOf' order by edge_order)")
    public abstract List<Copy> getDerivatives(@Bind("id") Long id);

    @SqlQuery("select * from work where id in (select v_out from flatedge where v_in = :id and type = 'isDerivativeOf' order by edge_order) and copyRole = :role")
    public abstract List<Copy> getDerivatives(@Bind("id") Long id, @Bind("role") String role);

    @SqlQuery("select * from work where id = (select v_in from flatedge where v_out = :id and type = 'isComasterOf')")
    public abstract Copy getComaster(@Bind("id") Long id);

    @SqlQuery("select * from work where id in (select v_out from flatedge where v_in = :id and type = 'isFileOf')")
    public abstract List<File> getFiles(@Bind("id") Long id);

    @SqlQuery("select * from work where id = (select v_in from flatedge where v_out = :id and type = 'isCopyOf')")
    public abstract Work getWork(@Bind("id") Long id);

    @SqlQuery("select * from work where id in (select v_in from flatedge where v_out = :id and type = 'represents')")
    public abstract List<Work> getRepresentedWorks(@Bind("id") Long id);

    @SqlQuery("select * from work where id in (select v_out from flatedge where v_in = :id and type = 'represents')")
    public abstract List<Copy> getRepresentations(@Bind("id") Long id);
}
