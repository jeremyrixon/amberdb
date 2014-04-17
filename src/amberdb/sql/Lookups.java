package amberdb.sql;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.util.Properties;

import amberdb.lookup.ListLu;

public abstract class Lookups {
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select DISTINCT(name) from list")
    public abstract List<ListLu> findAllLists();

    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list where name = :name and deleted is null")
    public abstract List<ListLu> findListFor(@Bind("name") String name);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("select * from list where name = :name orderBy name")
    public abstract List<ListLu> findUnabridgedListFor(@Bind("name") String name);
    
    @RegisterMapper(Lookups.ListLuMapper.class)
    @SqlQuery("update list set deleted = 'y' where name = :name and value = :value" )
    public abstract List<ListLu> removeListItem(@Bind("name") String name,@Bind("value") String value );
    
    @SqlQuery("select distinct name from list order by name")
    public abstract List<String> findListNames();
    
    public static class ListLuMapper implements ResultSetMapper<ListLu> {
        public ListLu map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new ListLu(r.getString("name"), r.getString("value"));
        }
    }
    
    public void seedInitialLookups() throws IOException {
        String listSeedsFile = config(System.getProperty("lookups.list.seeds.file"), "./listSeeds.txt");
        String materialTypeSeedsFile = config(System.getProperty("lookups.material.types.file"), "./materialTypeSeeds.txt");
        String toolCategorySeedsFile = config(System.getProperty("lookups.material."), "./toolCategorySeedsFile.txt");
        String listFldsPattern = "\\s*(\".*\"),\\s*(\".*\")"; // flds
        // pattern:
        // "name",
        // "value"
        String mapsFldsPattern = "\\s*(\".*\"),\\s*(\".*\")"; // flds
        // pattern:
        // "id",
        // "parent_id"
        String singleValueLookupFldsPattern = "\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\")"; // flds
                                  // pattern:
                                  // "id",
                                  // "name",
                                  // "value"
        String singleValueWithCodeLookupFldsPattern = "\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                       // pattern:
                                                       // "id",
                                                       // "name",
                                                       // "code",
                                                       // "value"
        String lookupWithAttributesFldsPattern = "\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                  // pattern:
                                                  // "id",
                                                  // "name",
                                                  // "attribute",
                                                  // "value"

        List<List<String>> nameValueLookups = parseLookupData(Paths.get(listSeedsFile), listFldsPattern, 2);
        
    }
    
    protected String config(String actualProp, String defaultProp) {
        if (actualProp == null || actualProp.isEmpty())
            return defaultProp;
        return actualProp;
    }
    
    protected List<List<String>> parseLookupData(Path dataFile, String fldsPattern, int fldsTotal) throws IOException {
        List<List<String>> lookupData = new ArrayList<>();

        List<String> lines = Files.readAllLines(dataFile, Charset.forName("utf8"));
        Pattern pattern = Pattern.compile(fldsPattern);
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            List<String> flds = new ArrayList<>();
            if (matcher.matches()) {
                for (int i = 1; i <= fldsTotal; i++) {
                    String fld = matcher.group(i).replace("\"", "");
                    flds.add(fld);
                }
                lookupData.add(flds);
            }
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
