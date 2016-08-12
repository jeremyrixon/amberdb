package amberdb.repository.dao.associations;

import amberdb.repository.model.Node;
import amberdb.repository.model.Tag;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

import java.util.List;
import java.util.Map;

public abstract class TagAssociationDao implements GetHandle {

    public List<Node> getTaggedObjects(Long id) {
        // TODO - figure out which table to pull the tagged object from

        return null;
    }

    // TODO - need to generate txn_start/txn_end values before insertion
    @SqlUpdate("")
    public abstract void addNode(@Bind("tagId") Long tagId, @Bind("objId") Long objId);

    @SqlUpdate("delete from flatedge where v_in = :tagId and v_out = :objId")
    public abstract void removeNode(@Bind("tagId") Long tagId, @Bind("objId") Long objId);

    @SqlQuery("select * from tags where id in (select v_in from flatedge where v_out = :id and type = 'tags')")
    public abstract List<Tag> getTags(@Bind("id") Long id);

    // TODO - need to generate txn_start/txn_end value before insertion
    @SqlUpdate("")
    public abstract void addTag(@Bind("tagId") Long tagId, @Bind("objId") Long objId);

    @SqlUpdate("delete from flatedge where v_in = :tagId and v_out = :objId")
    public abstract void removeTag(@Bind("tagId") Long tagId, @Bind("objId") Long objId);
}
