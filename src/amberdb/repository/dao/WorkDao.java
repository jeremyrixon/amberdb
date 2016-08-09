package amberdb.repository.dao;

import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

public abstract class WorkDao implements Transactional<WorkDao>, GetHandle {

}
