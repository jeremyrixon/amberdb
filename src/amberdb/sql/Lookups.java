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

public abstract class Lookups extends Tools {        
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and deleted = :deleted order by name, value")
    public abstract List<ListLu> findLookupsFor(@Bind("name") String name, @Bind("deleted") String deleted);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and (code = :code or value = :code) and (deleted is null or deleted = 'N') order by value")
    public abstract List<ListLu> findActiveLookup(@Bind("name") String name, @Bind("code") String code);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select id, name, value, code, deleted from lookups where name = :name and (code = :code or value = :code) and (deleted = 'D' or deleted = 'Y') order by deleted, value")
    public abstract List<ListLu> findDeletedLookup(@Bind("name")String name, @Bind("code") String code);
    
    public ListLu findLookup(String name, String code) {
        List<ListLu> activeLookups = findActiveLookup(name, code);
        if (activeLookups != null && activeLookups.size() > 0)
            return activeLookups.get(0);
        List<ListLu> deletedLookups = findDeletedLookup(name, code);
        if (deletedLookups != null && deletedLookups.size() > 0)
            return deletedLookups.get(0);
        return null;
    }
    
    public List<ListLu> findActiveLookupsFor(String name) {
        List<ListLu> activeLookups = findLookupsFor(name, "N");
        if (activeLookups == null) return new ArrayList<>();
        return activeLookups;
    }
    
    public List<ListLu> findLatestDeletedLookupsFor(String name) {
        List<ListLu> latestDeleted = findLookupsFor(name, "D");
        if (latestDeleted == null) return new ArrayList<>();
        return latestDeleted;
    }
    
    public List<ListLu> findAllDeletedLookupsFor(String name) {
        List<ListLu> deletedLookups = findLookupsFor(name, "Y");
        List<ListLu> latestDeleted = findLookupsFor(name, "D");
        if (deletedLookups == null && latestDeleted == null) return new ArrayList<>();
        List<ListLu> allDeletedLookups = new ArrayList<>();
        if (latestDeleted != null) allDeletedLookups.addAll(latestDeleted);
        if (deletedLookups != null) allDeletedLookups.addAll(deletedLookups);
        return allDeletedLookups;
    }
    
    public List<ListLu> findAllLookupsEverRecordedFor(String name) {
        List<ListLu> lookups = new ArrayList<>();
        lookups.addAll(findActiveLookupsFor(name));
        lookups.addAll(findAllDeletedLookupsFor(name));
        return lookups;
    }
    
    public ListLu findLookup(long id, String name) {
        List<ListLu> lookups = findAllLookupsEverRecordedFor(name);
        for (ListLu lookup : lookups) {
            if (lookup.getId() != null && lookup.getId() == id) {
                return lookup;
            }
        }
        throw new RuntimeException("Lookup of id " + id + " is not found.");
    }
    
    public static class ListLuMapper implements ResultSetMapper<ListLu> {
        public ListLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            String code = r.getString("code");
            if (code == null || code.isEmpty()) {
                String name = r.getString("name");
                if (name != null && name.equals("tools"))
                    code = r.getString("id");
                else
                    code = r.getString("value");
            }
            return new ListLu(r.getLong("id"), r.getString("name"), r.getString("value"), code, r.getString("deleted"));
        }
    }
    
    public void addLookupData(ListLu lu) {
        // Validate that name, code combination does not already exist and is active.
        String code = (lu.getCode() == null || lu.getCode().isEmpty())? lu.getValue() : lu.getCode();
        ListLu _lu = findLookup(lu.getName(), code);
        if (_lu != null && !_lu.isDeleted())
            throw new IllegalArgumentException("The lookup entry: (" + lu.getName() + ", " + code + ") already exist.");
        
        Long id = nextLookupId();
        if (id == null) id = 1L;
        addLookupData(id, lu.getName(), code, lu.getValue());
    }
    
    public void deleteLookup(ListLu lu) {
        archiveDeletedLookup(lu.getId(), lu.getName(), lu.getCode());
        markLookupDeleted(lu.getId(), lu.getName(), lu.getCode());
    }
    
    public void updateLookup(ListLu lu) {
        // check the list lu is in the database with its id, otherwise, throw an exception.
        String LuNotFoundErr = "Fail to update lookup (" + lu.name + ", " + lu.code + "," + lu.value + "), can not find this entry in the database.";
        if (lu.getId() == null)
            throw new IllegalArgumentException(LuNotFoundErr);
        ListLu persistedLu = findLookup(lu.getId(), lu.getName());
        if (persistedLu == null)
            throw new IllegalArgumentException(LuNotFoundErr);
        
        deleteLookup(lu);
        String code = (lu.getCode() == null || lu.getCode().isEmpty())? lu.getValue() : lu.getCode();
        addLookupData(lu.getId(), lu.getName(), code, lu.getValue());
    }
    
    @SqlQuery("select max(id) + 1 from lookups")
    protected abstract Long nextLookupId();
    
    @SqlUpdate("INSERT INTO lookups (id, name, code, value, deleted) VALUES"
            + "(:id, :name, :code, :value, 'N')")
    protected abstract void addLookupData(@Bind("id") Long id,
                                              @Bind("name") String name,
                                              @Bind("code") String code,
                                              @Bind("value") String value);
    
    @SqlUpdate("UPDATE lookups SET deleted = 'D' "
            + "WHERE id = :id and name = :name and (code is null or code = :code) and deleted = 'N'")
    protected abstract void markLookupDeleted(@Bind("id") Long id,
                                              @Bind("name") String name,
                                              @Bind("code") String code);
    
    @SqlUpdate("UPDATE lookups SET deleted = 'Y' "
            + "WHERE id = :id and name = :name and (code = :code or value = :code) and deleted = 'D'")
    protected abstract void archiveDeletedLookup(@Bind("id") Long id,
                                                          @Bind("name") String name,
                                                          @Bind("code") String code);
    
    @SqlUpdate("INSERT INTO lookups (id, name, code, value, deleted) VALUES"
            + "(:id, :name, :code, :value, :deleted)")
    public abstract void seedLookupTable(@Bind("id") List<Long> id,
                                              @Bind("name") List<String> name,
                                              @Bind("code") List<String> code,
                                              @Bind("value") List<String> value,
                                              @Bind("deleted") List<String> deleted);
    
    public void seedInitialLookups() throws IOException {
        List<Path> lookupPaths = getLookupFilePaths("amberdb.lookups.", ".file");
        for (Path lookupPath : lookupPaths) {
            Map<String,List<String>> lookupData = parseLookupData(lookupPath);
            if (!lookupData.isEmpty()) {
                List<Long> ids = parseLong(lookupData.get("id"));
                List<String> names = padStringArry(lookupData.get("name"), ids.size());
                List<String> codes = padStringArry(lookupData.get("code"), ids.size());
                List<String> values = padStringArry(lookupData.get("value"), ids.size());
                List<String> deleted = padStringArry(lookupData.get("deleted"), ids.size());
                seedLookupTable(ids, names, codes, values, deleted);
            }
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
