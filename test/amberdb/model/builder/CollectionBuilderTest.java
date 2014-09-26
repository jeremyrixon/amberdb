package amberdb.model.builder;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Test;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.model.Work;

public class CollectionBuilderTest {
    private AmberDb db;
    private String collectionWorkId;
    private ObjectMapper objectMapper;
    private JsonNode collectCfg;
    private Path testEADPath;
    private String[] testEADFiles = { "test/resources/1941original_AS_export-2.xml", 
            "test/resources/5925.xml", "test/resources/6442.xml",
            "test/resources/9982.xml",
            "test/resources/nla.ms-ms2852_LordGowriev5_ead.xml" };
    private List<String> tagsList;
    private String[] tags = {"abstract",
            "accessrestrict",
            "acqinfo",
            "altformavail",
            "archdesc",
            "archref",
            "arrangement",
            "author",
            "bibliography"   ,
            "bibref",
            "bioghist",
            "change",
            "chronitem",
            "chronlist",
            "colspec",
            "controlaccess",
            "corpname",
            "creation",
            "date",
            "defitem",
            "descgrp",
            "descrules",
            "did",
            "dimensions",
            "dsc",
            "ead",
            "eadheader",
            "eadid",
            "emph",
            "entry",
            "event",
            "eventgrp",
            "extent",
            "extref",
            "filedesc",
            "frontmatter",
            "genreform",
            "geogname",
            "head",
            "head01",
            "head02",
            "index",
            "indexentry",
            "item",
            "label",
            "langmaterial",
            "language",
            "langusage",
            "lb",
            "list",
            "listhead",
            "name",
            "note",
            "num",
            "odd",
            "originalsloc",
            "origination",
            "p",
            "persname",
            "physdesc",
            "physfacet",
            "physloc",
            "prefercite",
            "profiledesc",
            "publicationstmt",
            "publisher",
            "ref",
            "relatedmaterial",
            "repository",
            "revisiondesc",
            "row",
            "scopecontent",
            "separatedmaterial",
            "table",
            "tbody",
            "tgroup",
            "thead",
            "title",
            "titlepage",
            "titleproper",
            "titlestmt",
            "unitdate",
            "unitid",
            "unittitle",
            "userestrict"};
    
    @Before
    public void setUp() throws JsonProcessingException, IOException {
        tagsList = Arrays.asList(tags);
        testEADPath = Paths.get("test/resources/6442.xml");
      
        objectMapper = new ObjectMapper();
        collectCfg = objectMapper.readTree(new File("test/resources/ead.json"));

        // create the top-level work
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:cache", "store", "collection");
        db = new AmberDb(ds, Paths.get("."));
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.addWork(); 
            collectionWork.setSubType("Work");
            collectionWork.setSubUnitType("Collection");
            collectionWork.setForm("Manuscript");
            collectionWork.setBibLevel("Set");
            collectionWork.setCollection("nla.ms-ms6442");
            collectionWork.setRecordSource("FA");
            collectionWork.asEADWork().setRdsAcknowledgementType("Sponsor");
            collectionWork.asEADWork().setRdsAcknowledgementReceiver("NLA");
            collectionWork.asEADWork().setEADUpdateReviewRequired("Y");   
            collectionWork.asEADWork().setAccessConditions("Restricted");
            collectionWorkId = collectionWork.getObjId();
            as.commit();
        }
    }
    
    @Test
    public void testCreateCollection() throws IOException, ValidityException, ParsingException {
        // Verify before creating collection, collectionWork does not have any sub-work attached
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            assertTrue(collectionWork.getPartsOf(new ArrayList<String>()).isEmpty());
        }
        
        createCollection();
        
        // Verify after creating collection, collectionWork has 8 sub-works attached
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            List<Work> subWorks = collectionWork.getPartsOf(new ArrayList<String>());
            assertNotNull(subWorks);
            assertEquals(8, subWorks.size());
        }
    }
    
    @Test
    public void testGenerateJson() throws IOException, ValidityException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            boolean storeCopy = false;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            System.out.println("doc: " + doc.toJson());
            JsonNode content = doc.getContent();
            Iterator<String> it = content.getFieldNames();
            assertTrue(it.hasNext());
            int i = 0;
            while (it.hasNext()) {
                it.next();
                i++;
            }
            assertEquals(9, i);
        }
    }
    
    @Test
    public void testFindXPathsForEADWork1() throws IOException, ValidityException, ParsingException {
        Map<String, List<String>> tagMaps = Collections.synchronizedSortedMap(new TreeMap<String, List<String>>());
        for (String testEADFile : testEADFiles) {
            System.out.println("parsing file " + testEADFile);
            Path testEADPath = Paths.get(testEADFile);
            InputStream in = new FileInputStream(testEADPath.toFile());
            traverseDoc(tagMaps, in);    
        }
        
        for (String key : tagMaps.keySet()) {
            List<String> paths = tagMaps.get(key);
            if (paths == null)
                System.out.println(key + ": not found");
            else {
                System.out.println("\"" + key + "\": ");
                for (String path : paths) {
                    System.out.println("\t\t" + "\"" + path + "\"");
                }
            }
        }
    }
    
    private void traverseDoc(Map<String, List<String>> tagMaps, InputStream in) throws ValidityException, ParsingException, IOException {
        EADParser eadParser = new EADParser();
        boolean validateXML = false;
        eadParser.init(in, collectCfg, validateXML);
        Element rootElement = eadParser.doc.getRootElement();
        String qualifiedName = eadParser.qualifiedName;
        String xpath = "//" + qualifiedName + ":" + qualifiedName;
        System.out.println("xpath: " + xpath);
        Elements childElements = rootElement.getChildElements();
        traverseChildElements(tagMaps, xpath, qualifiedName, childElements);       
    }
    
    private void traverseChildElements(Map<String, List<String>> tagMaps, String basePath, String qualifiedName, Elements childElements) {
        if (childElements == null) return;
        for (int i = 0; i < childElements.size(); i++) {
            Element e = childElements.get(i);
            String localName = e.getLocalName();
            String xpath = basePath + "/" + qualifiedName + ":" + localName;
            if (tagsList.contains(localName)) {
                if (tagMaps.get(localName) == null) {
                    List<String> paths = new ArrayList<>();
                    paths.add(xpath);
                    tagMaps.put(localName, paths);
                } else {
                    List<String> paths = tagMaps.get(localName);
                    if (!paths.contains(xpath))
                        paths.add(xpath);
                }
            }       
            traverseChildElements(tagMaps, xpath, qualifiedName, e.getChildElements());
        }
    }
    
    private void createCollection() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            InputStream in = new FileInputStream(testEADPath.toFile());
            boolean validateXml = false;
            String collectionName = testEADPath.getFileName().toString();
            CollectionBuilder.createCollection(collectionWork, collectionName, in, collectCfg, new EADParser(), validateXml);
            as.commit();
        }
    }
}
