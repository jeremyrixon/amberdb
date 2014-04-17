package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.lookup.ToolsLu;

public abstract class Tools {
    
    @RegisterMapper(Tools.ToolsLuMapper.class)
    @SqlQuery(
            "select s.id, n.name, r.resolution, s.serialNumber, t.notes, tt.toolType, tt.materialType, toolCategory "
                    + "from "
                    + "(select id, "
                    + "       convert(value using utf8) as name "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'name' "
                    + "and deleted = 'N') n, "
                    + "(select id, "
                    + "       convert(value using utf8) as resolution "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'resolution' "
                    + "and deleted = 'N') r, "
                    + "(select id, "
                    + "       convert(value using utf8) as serialNumber "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'serialNumber' "
                    + "and deleted = 'N') s, "
                    + "(select id, "
                    + "       convert(value using utf8) as notes "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'notes' "
                    + "and deleted = 'N') t, "
                    + "(select t.id, "
                    + "        convert(t.value using utf8) as toolType, "
                    + "        convert(m.value using utf8) as materialType "
                    + " from lookups t, lookups m, maps mp1 "
                    + " where t.name = 'toolType' "
                    + " and t.deleted = 'N' "
                    + " and m.name = 'materialType' "
                    + " and m.deleted = 'N' "
                    + " and t.id = mp1.id "
                    + " and mp1.parent_id = m.id "
                    + " and mp1.deleted = 'N') tt, "
                    + "(select distinct mp2.id, "
                    + "        convert(t1.value using utf8) as toolCategory "
                    + " from lookups t1, maps mp2 "
                    + " where t1.name = 'toolCategory' "
                    + " and t1.deleted = 'N' "
                    + " and mp2.parent_id = t1.id "
                    + " and mp2.deleted = 'N') tt1, "
                    + "maps mp "
                    + "where n.id = r.id "
                    + "and r.id = s.id "
                    + "and s.id = t.id "
                    + "and s.id = mp.id "
                    + "and tt.id = tt1.id "
                    + "and mp.parent_id = tt.id "
                    + "and mp.deleted = 'N' "
            )
    public abstract List<ToolsLu> findListFor(@Bind("name") String name);
    
    public static class ToolsLuMapper implements ResultSetMapper<ToolsLu> {
        public ToolsLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new ToolsLu(r.getLong("id"),
                    r.getString("name"),
                    r.getString("resolution"),
                    r.getString("serialNumber"),
                    r.getString("notes"),
                    r.getString("materialType"),
                    r.getString("toolType"),
                    r.getString("toolCategory"));
        }
    }
}
