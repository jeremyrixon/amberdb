package amberdb.enums;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.lookup.AgentLu;

public abstract class Lookups {

    @RegisterMapper(Lookups.AgentLuMapper.class)
    @SqlQuery("select * from agentlu order by agent")
    public abstract List<AgentLu> findAllAgents();

    public static class AgentLuMapper implements ResultSetMapper<AgentLu> {
        public AgentLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new AgentLu(r.getLong("id"), r.getString("agent"), r.getString("workingDirectory"));
        }
    }


}
