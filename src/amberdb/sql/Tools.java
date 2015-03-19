package amberdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.graph.dao.AmberDao;


public abstract class Tools implements Transactional<Tools>{   

    @RegisterMapper(Tools.ToolsLuMapper.class)
    @SqlQuery(
            "select distinct t.*, tl.value as toolType, tcl.value as toolCategory, mtl.value as materialType "
            + "from tools t left join (select id, value from lookups where deleted = 'N') tl " 
            + "on t.toolTypeId = tl.id "        
            + "left join  (select id, value from lookups where deleted = 'N') tcl "
            + "on t.toolCategoryId = tcl.id "
            + "left join  (select id, value from lookups where deleted = 'N') mtl "
            + "on t.materialTypeId = mtl.id "
            + "where t.deleted = :deleted "
            + "order by t.name ")
    public abstract List<ToolsLu> findTools(@Bind("deleted") String deleted);   
    
    @RegisterMapper(Tools.ToolsLuMapper.class)
    @SqlQuery(
            "select distinct t.*, tl.value as toolType, tcl.value as toolCategory, mtl.value as materialType "
            + "from tools t left join (select id, value from lookups where deleted = 'N') tl " 
            + "on t.toolTypeId = tl.id "        
            + "left join  (select id, value from lookups where deleted = 'N') tcl "
            + "on t.toolCategoryId = tcl.id "
            + "left join  (select id, value from lookups where deleted = 'N') mtl "
            + "on t.materialTypeId = mtl.id "
            + "where (t.deleted = :deleted "
            + "or t.id = :inclId) "
            + "order by t.name ")
    public abstract List<ToolsLu> findTools(@Bind("deleted") String deleted, @Bind("inclId") Long inclId);   
    
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
            Long commitTime = null;
            if (r.getString("commitTime") != null && !r.getString("commitTime").isEmpty()) {
                try {
                    commitTime = Long.parseLong(r.getString("commitTime"));        
                } catch (java.lang.NumberFormatException e) {
                    commitTime = null;
                }
            }
            return new ToolsLu(r.getLong("id"),
                    r.getString("name"),
                    r.getString("resolution"),
                    r.getString("serialNumber"),
                    r.getString("notes"),
                    r.getLong("materialTypeId"),
                    r.getString("materialType"),
                    r.getLong("toolTypeId"),
                    r.getString("toolType"),
                    r.getLong("toolCategoryId"),
                    r.getString("toolCategory"),
                    commitTime,
                    r.getString("commitUser"),
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
        return filterTools(filterFldName, filterFldValue, tools);
    }
    
    public List<ToolsLu> findActiveToolsFor(String filterFldName, String filterFldValue, Long includeId) {
        List<ToolsLu> tools = findTools("N", includeId);
        if (tools == null) tools = new ArrayList<>();
        return filterTools(filterFldName, filterFldValue, tools);
    }
    
    public List<ToolsLu> findActiveToolsFor(Map<String, String> criteria, Long includeId) {
        List<ToolsLu> tools = (includeId == null)? findTools("N") : findTools("N", includeId);
        for (String fldName : criteria.keySet()) {
            tools = filterTools(fldName, criteria.get(fldName), tools, includeId);
        }
        return tools;
    }
    
    public List<ToolsLu> findToolsEverRecordedFor(String filterFldName, String filterFldValue) {
        List<ToolsLu> tools = findAllToolsEverRecorded();
        return filterTools(filterFldName, filterFldValue, tools);
    }
    
    public List<ToolsLu> findActiveSoftware() {
        return findActiveToolsFor("toolCategory", "software");
    }
    
    public List<ToolsLu> findActiveSoftware(Long includeId) {
        return findActiveToolsFor("toolCategory", "software", includeId);
    }
    
    public List<ToolsLu> findSoftwareEverRecorded() {
        return findToolsEverRecordedFor("toolCategory", "software");
    }
    
    public List<ToolsLu> findActiveDevices() {
        return findActiveToolsFor("toolCategory", "device");
    }
    
    public List<ToolsLu> findActiveDevices(Long includeId) {
        return findActiveToolsFor("toolCategory", "device", includeId);
    }
        
    public List<ToolsLu> findDevicesEverRecorded() {
        return findToolsEverRecordedFor("toolCategory", "device");
    }
     
    private List<ToolsLu> filterTools(String filterFldName, String filterFldValue, List<ToolsLu> tools) {
        return filterTools(filterFldName, filterFldValue, tools, null);
    }
    
    private List<ToolsLu> filterTools(String filterFldName, String filterFldValue, List<ToolsLu> tools, Long includeId) {
        List<ToolsLu> filteredTools = new ArrayList<>();
        for (ToolsLu tool : tools) {
                filterRow(filterFldName, filterFldValue, filteredTools, tool, includeId);  
        }
        return filteredTools;
    }

    private void filterRow(String filterFldName, String filterFldValue, List<ToolsLu> activeToolsFor, ToolsLu tool, Long includeId) {
        // The following assignments are to cater for user trying to add a new tool, 
        // but doesn't go through with it, which will leave a blank record in the db
        // currently.
        String toolName = (tool.getName() == null)?"":tool.getName();
        String toolType = (tool.getToolType() == null)?"":tool.getToolType();
        String resolution = (tool.getResolution() == null)?"":tool.getResolution();
        String serialNumber = (tool.getSerialNumber() == null)?"":tool.getSerialNumber();
        String notes = (tool.getNotes() == null)?"":tool.getNotes();
        String materialType = (tool.getMaterialType() == null)?"":tool.getMaterialType();
        String toolCategory = (tool.getToolCategory() == null)?"":tool.getToolCategory();
        
        if (filterFldName.equalsIgnoreCase("name") && toolName.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("toolType") && toolType.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("resolution") && resolution.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("serialNumber") && serialNumber.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("notes") && notes.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("materialType") && materialType.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (filterFldName.equalsIgnoreCase("toolCategory") && toolCategory.toUpperCase().contains(filterFldValue.toUpperCase()))
            activeToolsFor.add(tool);
        else if (includeId != null && tool.getId().equals(includeId))
            activeToolsFor.add(tool);
    }    
    
    public synchronized Long addTool(ToolsLu toolsLu) {
        Long id = nextToolId();
        if (id == null) id = 1L;
        insertTool(id, toolsLu.getName(), toolsLu.getResolution(), toolsLu.getNotes(), toolsLu.getSerialNumber(), toolsLu.getToolTypeId(),
                toolsLu.getToolCategoryId(), toolsLu.getMaterialTypeId(), toolsLu.getCommitUser(), new Date().getTime());
        return id;
    }
    
    public synchronized void deleteTool(Long id) {
        // archive previously deleted tool entry
        archiveDeletedTool(id);
        
        // mark the current tool entry deleted.
        // Note: this is so that if a tool entry is marked as deleted,
        // all the dlir app records referencing the tool entry
        // can still display the referenced tool.
        markToolDeleted(id);
    }
    
    public synchronized void undeleteTool(Long id) {
        ToolsLu tool = findTool(id);
        if (tool.isDeleted()) {
          markToolUnDeleted(id);  
        } 
    }
    
    public synchronized void updTool(ToolsLu toolsLu) {
        // check the toolsLu is in the database with its id, otherwise, throw an exception.
        String LuNotFoundErr = "Fail to update tool " + toolsLu.name + ", can not find this entry in the database.";
        if (toolsLu.getId() == null)
            throw new IllegalArgumentException(LuNotFoundErr);
        ToolsLu persistedTool = findTool(toolsLu.getId());
        if (persistedTool == null)
            throw new IllegalArgumentException(LuNotFoundErr);
        
        if (persistedTool.isEmptyRecord()) {
            deleteEmptyToolRecord(toolsLu.getId());
        } else {
            deleteTool(toolsLu.getId());
        }
        insertTool(toolsLu.getId(), toolsLu.getName(), toolsLu.getResolution(), toolsLu.getNotes(), toolsLu.getSerialNumber(), toolsLu.getToolTypeId(),
                toolsLu.getToolCategoryId(), toolsLu.getMaterialTypeId(), toolsLu.getCommitUser(), new Date().getTime());
    }

    @SqlQuery("select max(id) + 1 from tools;")
    protected abstract Long nextToolId();
    
    @SqlUpdate("INSERT INTO tools(id, name, resolution, notes, serialNumber, toolTypeId, toolCategoryId, materialTypeId, commitUser, commitTime) VALUES"
            + "(:id, :name, :resolution, :notes, :serialNumber, :toolTypeId, :toolCategoryId, :materialTypeId, :commitUser, :commitTime)")
    protected abstract void insertTool(@Bind("id") Long id, @Bind("name") String name,
            @Bind("resolution") String resolution, @Bind("notes") String notes,
            @Bind("serialNumber") String serialNumber, @Bind("toolTypeId") Long toolTypeId,
            @Bind("toolCategoryId") Long toolCategoryId, @Bind("materialTypeId") Long materialTypeId,
            @Bind("commitUser") String commitUser, @Bind("commitTime") Long commitTime);
    
    @SqlUpdate("DELETE FROM tools where id = :id and deleted = 'N'")
    protected abstract void deleteEmptyToolRecord(@Bind("id") Long id);
    
    @SqlUpdate("UPDATE tools SET deleted = 'D' where id = :id and deleted = 'N'")
    protected abstract void markToolDeleted(@Bind("id") Long id);
    
    @SqlUpdate("UPDATE tools SET deleted = 'Y' where id = :id and deleted = 'D'")
    protected abstract void archiveDeletedTool(@Bind("id") Long id);
    
    @SqlUpdate("UPDATE tools SET deleted = 'N' where id = :id and deleted = 'D'")
    protected abstract void markToolUnDeleted(@Bind("id") Long id);
}

