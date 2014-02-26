package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.lookup.ListLu;

public abstract class Lookups {

    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select DISTINCT(name) from list")
    public abstract List<ListLu> findAllLists();

    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list where name = :name and deleted is null")
    public abstract List<ListLu> findListFor(@Bind("name") String name);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list where name = :name")
    public abstract List<ListLu> findUnabridgedListFor(@Bind("name") String name);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("update list set deleted = 'y' where name = :name and value = :value" )
    public abstract List<ListLu> removeListItem(@Bind("name") String name,@Bind("value") String value );
    
    public static class ListLuMapper implements ResultSetMapper<ListLu> {
        public ListLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new ListLu(r.getString("name"), r.getString("value"));
        }
    }

}
