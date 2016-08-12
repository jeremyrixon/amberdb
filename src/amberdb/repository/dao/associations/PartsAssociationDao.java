package amberdb.repository.dao.associations;

import amberdb.repository.model.Page;
import amberdb.repository.model.Section;
import amberdb.repository.model.Work;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

public abstract class PartsAssociationDao {

    @SqlQuery("select * from work w where w.id in (select v_out from flatedge f where f.v_in = :id and f.type 'existsOn')")
    public abstract List<Page> existsOnPages(Long id);

    @SqlQuery("select * from work w where w.id = (select v_in from flatedge f where f.v_out = :id and f.type = 'existsOn' and f.edge_order = :position)")
    public abstract Page getPage(Long id, int position);

    @SqlQuery("select * from work w where w.id in (select f.v_in from flatedge f where f.id = :id and f.type = :relation) and w.subType = :subType")
    public abstract List<Work> getLeafs(Long id, String relation, String subType);

    @SqlQuery("select * from work w where w.id in (select f.v_in from flatedge f where f.id = :id and f.type = :relation) and w.subType = :subType")
    public abstract List<Section> getSections(Long id, String relation, String subType);

    @SqlQuery("select * from work w where w.id in (select f.v_out from flatedge f where f.id = :id and f.type = :relation")
    public abstract List<Work> getSubType(Long id, String relation);

    @SqlQuery("select count(v_out) from flatedge where v_in = :id and type = 'isPartOf'")
    public abstract Integer countParts(Long id);
}
