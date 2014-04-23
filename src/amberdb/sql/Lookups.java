package amberdb.sql;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.util.Properties;

import amberdb.lookup.ListLu;

public abstract class Lookups extends Tools {
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list order by name, value")
    public abstract List<ListLu> findAllLists();

    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list where name = :name and deleted is null order by name, value")
    public abstract List<ListLu> findListFor(@Bind("name") String name);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list where name = :name orderBy name, value")
    public abstract List<ListLu> findUnabridgedListFor(@Bind("name") String name);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("update list set deleted = 'y' where name = :name and value = :value order by value" )
    public abstract List<ListLu> removeListItem(@Bind("name") String name,@Bind("value") String value );
    
    @SqlQuery("select distinct name from list order by name")
    public abstract List<String> findListNames();
    
    public static class ListLuMapper implements ResultSetMapper<ListLu> {
        public ListLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new ListLu(r.getString("name"), r.getString("value"));
        }
    }
    
    @SqlUpdate("INSERT INTO list (name, value) VALUES"
            + "(:name,:value)")
    public abstract void addListData(@Bind("name") String name,
                                     @Bind("value") String value);
    
    @SqlUpdate("UPDATE list SET deleted = 'Y' WHERE name = :name and value = :value")
    public abstract void deleteListData(@Bind("name") String name,
                                        @Bind("value") String value);
    
    @SqlUpdate("INSERT INTO list (name, value) VALUES"
            + "(:name,:value,:deleted)")
    public abstract void seedListData(@Bind("name") List<String> name, 
                                      @Bind("value") List<String> value,
                                      @Bind("deleted") List<String> deleted);
        
        
    public void updateListData(String name, String oldValue, String newValue) {
        addListData(name, newValue);
        deleteListData(name, oldValue);
    }
    
    @SqlQuery("select max(id) + 1 from lookups")
    public abstract Long nextLookupId();
    
    @SqlUpdate("INSERT INTO lookups (id, name, lbl, code, attribute, value, deleted) VALUES"
            + "(:id, :name, :lbl, :code, :attribute, :value, :deleted)")
    public abstract void addLookupData(@Bind("id") Long id,
                                              @Bind("name") String name,
                                              @Bind("lbl") String lbl,
                                              @Bind("code") String code,
                                              @Bind("attribute") String attribute,
                                              @Bind("value") String value,
                                              @Bind("deleted") String deleted);
    
    @SqlUpdate("UPDATE lookups SET deleted = 'Y' "
            + "WHERE id = :id and name = :name and value = :value and code is null and attribute is null")
    public abstract void deleteLookupData(@Bind("id") Long id,
                                              @Bind("name") String name,
                                              @Bind("value") String value);
    
    @SqlUpdate("UPDATE lookups SET deleted = 'Y' "
            + "WHERE id = :id and name = :name and value = :value and code = :code and attribute is null")
    public abstract void deleteLookupData(@Bind("id") Long id,
                                              @Bind("name") String name,
                                              @Bind("code") String code,
                                              @Bind("value") String value);
    
    @SqlUpdate("UPDATE lookups SET deleted = 'Y' "
            + "WHERE id = :id and name = :name and value = :value and attribute = :attribute and code is null")
    public abstract void deleteLookupDataAttribute(@Bind("id") Long id,
                                              @Bind("name") String name,
                                              @Bind("attribute") String attribute,
                                              @Bind("value") String value);
    
    // TODO: maybe code and lbl should be an attribute for the tool lookup as well later?
    public void updateLookupData(Long id, String name, String lbl, String code, String attribute, String oldValue, String newValue) {
        addLookupData(id, name, lbl, code, attribute, newValue, "N");
        if ((code == null || code.isEmpty()) && (attribute == null || attribute.isEmpty()))
            deleteLookupData(id, name, oldValue);
        else if (attribute == null || attribute.isEmpty())
            deleteLookupData(id, name, code, oldValue);
        else if (code == null || code.isEmpty())
            deleteLookupDataAttribute(id, name, attribute, oldValue);
    }
    
    @SqlUpdate("INSERT INTO maps (id, parent_id, deleted) VALUES"
            + "(:id,:parentId,:deleted)")
    public abstract void addLookupDataMap(@Bind("id") Long id,
                                          @Bind("parentId") Long parentId,
                                          @Bind("deleted") String deleted);
    
    @SqlUpdate("UPDATE maps SET deleted = 'Y' WHERE id = :id and parent_id = :parentId")
    public abstract void deleteLookupDataMap(@Bind("id") Long id,
                                             @Bind("parentId") Long parentId);
    
    public void updateLookupDataMap(Long id, Long oldParentId, Long newParentId, String deleted) {
        addLookupDataMap(id, newParentId, "N");
        deleteLookupDataMap(id, oldParentId);
    }
    
    public void seedInitialLookups() throws IOException {
        List<Path> listPaths = getLookupFilePaths("amberdb.list.", ".file");
        for (Path listPath : listPaths) {
            Map<String,List<String>> listData = parseLookupData(listPath);
            if (!listData.isEmpty())
                seedListData(listData.get("name"), listData.get("value"), listData.get("deleted"));
        }
        List<Path> lookupPaths = getLookupFilePaths("amberdb.lookups.", ".file");
        for (Path lookupPath : lookupPaths) {
            Map<String,List<String>> lookupData = parseLookupData(lookupPath);
            if (!lookupData.isEmpty()) {
                List<Long> ids = parseLong(lookupData.get("id"));
                List<String> names = padStringArry(lookupData.get("name"), ids.size());
                List<String> lbls = padStringArry(lookupData.get("lbl"), ids.size());
                List<String> codes = padStringArry(lookupData.get("code"), ids.size());
                List<String> attributes = padStringArry(lookupData.get("attribute"), ids.size());
                List<String> values = padStringArry(lookupData.get("value"), ids.size());
                List<String> deleted = padStringArry(lookupData.get("deleted"), ids.size());
                seedToolsLookupTable(ids, names, lbls, codes, attributes, values, deleted);
            }
        }
        List<Path> lookupAssocPaths = getLookupFilePaths("amberdb.maps.", ".file");
        for (Path lookupAssocPath : lookupAssocPaths) {
            Map<String,List<String>> lookupAssocData = parseLookupData(lookupAssocPath);
            if (!lookupAssocData.isEmpty())
                seedToolsMapsTable(parseLong(lookupAssocData.get("id")), 
                        parseLong(lookupAssocData.get("parentId")), 
                        lookupAssocData.get("deleted"));
        }
    }
    
    protected List<String> padStringArry(List<String> strArry, int length) {
        if (strArry != null && !strArry.isEmpty()) return strArry;
        return new ArrayList<>(length);
    }
    
    protected List<Long> parseLong(List<String> values) {
        List<Long> longValues = new ArrayList<>();
        if (values == null || values.isEmpty()) return longValues;
        for (String value : values) {
            if (value == null || value.isEmpty()) longValues.add(null);
            else longValues.add(Long.parseLong(value));
        }
        return longValues;
    }
    
    public List<Path> getLookupFilePaths(String prefix, String suffix) {
        List<Path> lookupPaths = new ArrayList<>();
        Properties props = System.getProperties();
        for (Object key: props.keySet()) {
            if (key.toString().startsWith(prefix) && key.toString().endsWith(suffix)) {
                Path lookupFile = Paths.get(props.getProperty((String) key).toString());
                if (lookupFile.toFile().exists())
                    lookupPaths.add(lookupFile);
            }
                
        }
        return lookupPaths;
    }
    
    protected Map<String, List<String>> parseLookupData(Path dataFile) throws IOException {
        Map<String, List<String>> lookupData = new ConcurrentHashMap<>();

        // List<String> lines = Files.readAllLines(dataFile, Charset.forName("UTF-8"));
        List<String> lines = Files.readAllLines(dataFile, Charset.forName("utf8"));
        if (lines == null || lines.isEmpty()) return lookupData;
        String[] fldNames = lines.get(0).split(",");
        int fldsTotal = fldNames.length;
        if (fldsTotal == 0) return lookupData;
        
        String fldsPattern = "\\s*(\".*\")";
        if (fldsTotal > 1) {
            fldsPattern += ",\\s*(\".*\")";
        }
        
        Pattern pattern = Pattern.compile(fldsPattern);
        boolean firstLine = true;
        for (String line : lines) {
            if (!firstLine) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    for (int i = 1; i <= fldsTotal; i++) {
                        String fld = matcher.group(i).replace("\"", "");
                        if (lookupData.get(fldNames[i]) == null) {
                            lookupData.put(fldNames[i], new ArrayList<String>());
                        }
                        List<String> flds = lookupData.get(fldNames[i]);
                        flds.add(fld);
                    }
                }
            }
            firstLine = false;
        }
        return lookupData;
    }
    
    protected void printLookupData(OutputStream out, List<List<String>> lookupData) throws IOException {
        String lookupDataJson = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(lookupData);
        out.write(lookupDataJson.getBytes());
        out.write("\n".getBytes());
        out.flush();
    }

}
