package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.lookup.ToolsLu;

public abstract class Tools {    
    @SqlUpdate("INSERT INTO lookups (id, name, lbl, code, attribute, value, deleted) VALUES"
            + "(:id, :name, :lbl, :code, :attribute, :value, :deleted)")
    public abstract void seedToolsLookupTable(@Bind("id") List<Long> id,
                                              @Bind("name") List<String> name,
                                              @Bind("lbl") List<String> lbl,
                                              @Bind("code") List<String> code,
                                              @Bind("attribute") List<String> attribute,
                                              @Bind("value") List<String> value,
                                              @Bind("deleted") List<String> deleted);
        
    @SqlUpdate("INSERT INTO maps (id, parent_id, deleted) VALUES"
            + "(:id,:parentId,:deleted)")
    public abstract void seedToolsMapsTable(@Bind("id") List<Long> id,
                                            @Bind("parentId") List<Long> parentId,
                                            @Bind("deleted") List<String> deleted);
    
    @RegisterMapper(Tools.ToolsLuMapper.class)
    @SqlQuery(
            "select s.id, n.name, r.resolution, s.serialNumber, t.notes, tt.toolType, tt.materialType, toolCategory, n.deleted "
                    + "from "
                    + "(select id, "
                    + "        value as name, "
                    + "        deleted "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'name' "
                    + "and deleted = :deleted) n, "
                    + "(select id, "
                    + "        value as resolution "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'resolution' "
                    + "and deleted = :deleted) r, "
                    + "(select id, "
                    + "        value as serialNumber "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'serialNumber' "
                    + "and deleted = :deleted) s, "
                    + "(select id, "
                    + "        value as notes "
                    + "from lookups "
                    + "where name = 'tools' "
                    + "and attribute = 'notes' "
                    + "and deleted = :deleted) t, "
                    + "(select t.id, "
                    + "        t.value as toolType, "
                    + "        m.value as materialType "
                    + " from lookups t, lookups m, maps mp1 "
                    + " where t.name = 'toolType' "
                    + " and t.deleted = :deleted "
                    + " and m.name = 'materialType' "
                    + " and m.deleted = :deleted "
                    + " and t.id = mp1.id "
                    + " and mp1.parent_id = m.id "
                    + " and mp1.deleted = :deleted) tt, "
                    + "(select distinct mp2.id, "
                    + "        t1.value as toolCategory "
                    + " from lookups t1, maps mp2 "
                    + " where t1.name = 'toolCategory' "
                    + " and t1.deleted = :deleted "
                    + " and mp2.parent_id = t1.id "
                    + " and mp2.deleted = :deleted) tt1, "
                    + "maps mp "
                    + "where n.id = r.id "
                    + "and r.id = s.id "
                    + "and s.id = t.id "
                    + "and s.id = mp.id "
                    + "and tt.id = tt1.id "
                    + "and mp.parent_id = tt.id "
                    + "and mp.deleted = :deleted "
            )
    public abstract List<ToolsLu> findTools(@Bind("deleted") String deleted);   

    @SqlQuery("select count(id) from maps where id = :toolId and parent_id = :mapToId")
    public abstract int findAssociation(@Bind("toolId") Long toolId, @Bind("mapToId") Long mapToId);

    public boolean hasAssociation(Long toolId, Long mapToId) {
        return findAssociation(toolId, mapToId) > 0;
    }
    
    public List<ToolsLu> findAllToolsEverRecorded() {
        List<ToolsLu> allTools = new ArrayList<>();
        allTools.addAll(findAllActiveTools());
        allTools.addAll(findAllDeletedTools());
        return allTools;
    }
    
    public List<ToolsLu> findAllActiveTools() {
        List<ToolsLu> activeTools = findTools("N");
        if (activeTools == null) return new ArrayList<>();
        return activeTools;
    }
    
    public List<ToolsLu> findAllDeletedTools() {
        List<ToolsLu> deletedTools = findTools("Y");
        List<ToolsLu> latestDeletedTools = findTools("D");
        if (deletedTools == null && latestDeletedTools == null) return new ArrayList<>();
        List<ToolsLu> allDeletedTools = new ArrayList<>();
        if (latestDeletedTools != null) allDeletedTools.addAll(latestDeletedTools);
        if (deletedTools != null) allDeletedTools.addAll(deletedTools);
        return allDeletedTools;
    }
    
    public static class ToolsLuMapper implements ResultSetMapper<ToolsLu> {
        public ToolsLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new ToolsLu(r.getLong("id"),
                    r.getString("name"),
                    r.getString("resolution"),
                    r.getString("serialNumber"),
                    r.getString("notes"),
                    r.getString("materialType"),
                    r.getString("toolType"),
                    r.getString("toolCategory"),
                    r.getString("deleted"));
        }
    }
    
    public ToolsLu findTool(long id) {
        List<ToolsLu> tools = findAllToolsEverRecorded();
        for (ToolsLu  tool : tools) {
            if (tool.getId() == id)
                return tool;
        }
        throw new RuntimeException("Tool of id " + id + " is not found.");
    }
    
    public ToolsLu findActiveTool(long id) {
        List<ToolsLu> tools = findAllActiveTools();
        for (ToolsLu  tool : tools) {
            if (tool.getId() == id)
                return tool;
        }
        throw new RuntimeException("Tool of id " + id + " is not found.");
    }
    
    public List<ToolsLu> findActiveToolsFor(String filterFldName, String filterFldValue) {
        List<ToolsLu> tools = findAllActiveTools();
        List<ToolsLu> activeToolsFor = new ArrayList<>();
        for (ToolsLu tool : tools) {
            if (!tool.getDeleted()) { 
                filterRow(filterFldName, filterFldValue, activeToolsFor, tool);  
            }
        }
        return activeToolsFor;
    }
    
    public List<ToolsLu> findToolsEverRecordedFor(String filterFldName, String filterFldValue) {
        List<ToolsLu> tools = findAllToolsEverRecorded();
        List<ToolsLu> toolsFor = new ArrayList<>();
        for (ToolsLu tool : tools) {
                filterRow(filterFldName, filterFldValue, toolsFor, tool);  
        }
        return toolsFor;
    }
    
    public List<ToolsLu> findActiveSoftware() {
        return findActiveToolsFor("toolCategory", "software");
    }
    
    public List<ToolsLu> findSoftwareEverRecorded() {
        return findToolsEverRecordedFor("toolCategory", "software");
    }
    
    public List<ToolsLu> findActiveDevices() {
        return findActiveToolsFor("toolCategory", "device");
    }
    
    public List<ToolsLu> findDevicesEverRecorded() {
        return findToolsEverRecordedFor("toolCategory", "device");
    }
     
    private void filterRow(String filterFldName, String filterFldValue, List<ToolsLu> activeToolsFor, ToolsLu tool) {
        if (filterFldName.equalsIgnoreCase("name") && tool.getName().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("toolType") && tool.getToolType().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("resolution") && tool.getResolution().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("serialNumber") && tool.getSerialNumber().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("notes") && tool.getNotes().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("materialType") && tool.getMaterialType().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("toolCategory") && tool.getToolCategory().toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
    }    
}
