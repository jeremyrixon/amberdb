package amberdb.v2.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public abstract class MovingImageFile extends File {
}
