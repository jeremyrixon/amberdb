package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import com.fasterxml.jackson.core.JsonProcessingException;

public class AmberLookupsTest {
    private static final String listFldsPattern = "\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                                               // pattern:
                                                                               // "name",
                                                                               // "value"
    private static final String mapsFldsPattern = "\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                                               // pattern:
                                                                               // "id",
                                                                               // "parent_id"
    private static final String singleValueLookupFldsPattern = "\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                                                                         // pattern:
                                                                                                         // "id",
                                                                                                         // "name",
                                                                                                         // "value"
    private static final String singleValueWithCodeLookupFldsPattern = "\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                                                                                              // pattern:
                                                                                                                              // "id",
                                                                                                                              // "name",
                                                                                                                              // "code",
                                                                                                                              // "value"
    private static final String lookupWithAttributesFldsPattern = "\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\"),\\s*(\".*\")"; // flds
                                                                                                                         // pattern:
                                                                                                                         // "id",
                                                                                                                         // "name",
                                                                                                                         // "attribute",
                                                                                                                         // "value"
    public AmberGraph graph;

    @Before
    public void setup() throws MalformedURLException, IOException {

    }

    @After
    public void teardown() {
    }

    @Ignore
    public void testLookups() throws IOException {
        Path listSeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/listSeeds.txt");
        Path materialTypeSeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/materialTypeSeeds.txt");
        Path toolCategoryMapSeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/toolCategoryMapSeeds.txt");
        Path toolCategorySeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/toolCategorySeeds.txt");
        Path toolSeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/toolSeeds.txt");
        Path toolTypeMapSeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/toolTypeMapSeeds.txt");
        Path toolTypeMaterialTypeSeedsFile = Paths
                .get("/Users/szhou/git/amberdb-170414/test_data/toolTypeMaterialTypeSeeds.txt");
        Path toolTypeSeedsFile = Paths.get("/Users/szhou/git/amberdb-170414/test_data/toolTypeSeeds.txt");
        // String listFldsPattern = "\\s*(\".*\"),\\s*(\".*\")";
        List<List<String>> nameValueLookups = parseLookupData(listSeedsFile, listFldsPattern, 2);
        String listData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(nameValueLookups);
        System.out.println("lookup data is : ");
        System.out.println(listData);

        List<List<String>> materialTypeLookups = parseLookupData(materialTypeSeedsFile, singleValueLookupFldsPattern, 3);
        String materialTypeData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(materialTypeLookups);
        System.out.println("materialType data is : ");
        System.out.println(materialTypeData);

        List<List<String>> toolCategoryLookups = parseLookupData(toolCategorySeedsFile,
                singleValueWithCodeLookupFldsPattern, 4);
        String toolCategoryData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(toolCategoryLookups);
        System.out.println("tool category data is : ");
        System.out.println(toolCategoryData);

        List<List<String>> toolSeedsLookups = parseLookupData(toolSeedsFile, lookupWithAttributesFldsPattern, 4);
        String toolsData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(toolSeedsLookups);
        System.out.println("tools data is : ");
        System.out.println(toolsData);

        List<List<String>> toolTypeLookups = parseLookupData(toolTypeSeedsFile, singleValueLookupFldsPattern, 3);
        String toolTypesData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(toolTypeLookups);
        System.out.println("tool type data is : ");
        System.out.println(toolTypesData);

        List<List<String>> toolCategoryMap = parseLookupData(toolCategoryMapSeedsFile, mapsFldsPattern, 2);
        String toolCategoryMapData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(toolCategoryMap);
        System.out.println("tool category map data is : ");
        System.out.println(toolCategoryMapData);

        List<List<String>> toolTypeMaterialTypeMap = parseLookupData(toolTypeMaterialTypeSeedsFile, mapsFldsPattern, 2);
        String toolTypeMaterialTypeData = new com.fasterxml.jackson.databind.ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(toolTypeMaterialTypeMap);
        System.out.println("tool type material type map data is : ");
        System.out.println(toolTypeMaterialTypeData);

        List<List<String>> toolTypeMap = parseLookupData(toolTypeMapSeedsFile, listFldsPattern, 2);
        String toolTypeMapData = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(toolTypeMap);
        System.out.println("tool type map data is : ");
        System.out.println(toolTypeMapData);
    }

    private List<List<String>> parseLookupData(Path dataFile, String fldsPattern, int fldsTotal) throws IOException {
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
    
    private void printLookupData(List<List<String>> lookupData) throws JsonProcessingException {
        String lookupDataJson = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(lookupData);
        System.out.println(lookupDataJson);
    }
}
