package amberdb.repository.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Page extends Work {

    public Iterable<Section> getSections() {
        // TODO
        return null;
    }
}
