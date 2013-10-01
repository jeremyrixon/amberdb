package amberdb.sql.dcm;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.skife.jdbi.v2.BeanMapper;
import org.skife.jdbi.v2.DefaultMapper;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

public interface DcmDao {
    
    @SqlQuery("select count(*) from work")
    long countWorks();

    void close();

    @SqlQuery("select * from work")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Iterable<DcmWork> allWorks();

}