package amberdb.model.builder;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.AccessCondition;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.Work;

public class CollectionBuilderTest {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilderTest.class);
    AmberDb db;
    String collectionWorkId;
    ObjectMapper objectMapper;
    JsonNode collectCfg;
    Path testEADPath;
    Path testUpdedEADPath;
    String[] testEADFiles = { "test/resources/6442.xml" };

    @Before
    public void setUp() throws JsonProcessingException, IOException {
        testEADPath = Paths.get("test/resources/6442.xml");
        testUpdedEADPath = Paths.get("test/resources/6442_updated.xml");
        
        objectMapper = new ObjectMapper();
        // collectCfg = objectMapper.readTree(new File("test/resources/ead.json"));
        collectCfg = CollectionBuilder.getDefaultCollectionCfg();
        ((ObjectNode) collectCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("validateXML", "no");
        ((ObjectNode) collectCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("storeCopy", "no");

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
            collectionWork.asEADWork().setAccessConditions(AccessCondition.RESTRICTED.code());
            collectionWorkId = collectionWork.getObjId();
            collectionWork.addCopy(Paths.get("test/resources/6442.xml"), CopyRole.FINDING_AID_COPY, "application/xml");
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
        String[] expectedBibliography = {
          "Biographical note 1",
          "Biographical note 2",
          "Biographical note 3",
          "Biographical note 4"
        };
        Map<String, String> expectedCreatorMap = new HashMap<>();
        expectedCreatorMap.put("aspace_d1ac0117fdba1b9dc09b68e8bb125948", "First Creator");
        expectedCreatorMap.put("aspace_7275d12ba178fcbb7cf926d0b7bf68cc", "Second Creator");
        expectedCreatorMap.put("aspace_5c65cd1a0dd35517ba04da03d95ffac2", "Third Creator");
        expectedCreatorMap.put("aspace_563116915a27063fb67dc5de82e2f848", "Forth Creator");
        expectedCreatorMap.put("aspace_1012d592eedcfdbdc6175b91db070e2d", "Fifth Creator");
        expectedCreatorMap.put("aspace_3c0c615f787a41d4dc4c4104505e55a7", "Sixth Creator");
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            List<Work> subWorks = collectionWork.getPartsOf(new ArrayList<String>());
            List<String> bibliography = collectionWork.asEADWork().getBibliography();
            assertNotNull(subWorks);
            assertEquals(8, subWorks.size());
            assertEquals(4, bibliography.size());
            for (String expectedItem : expectedBibliography) {
                bibliography.contains(expectedItem);
            }
            for (Work subWork : subWorks) {
                String asId = subWork.getLocalSystemNumber();
                String creator = subWork.getCreator();
                String expectedCreator = expectedCreatorMap.get(asId);
                assertEquals(expectedCreator, creator);
            }
        }
    }
    
    @Test
    public void testGenerateJson() throws IOException, ValidityException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            boolean storeCopy = false;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            log("doc: " + doc.toJson());
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
    public void testFilterEAD() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
           Work collectionWork = as.findWork(collectionWorkId);
           XmlDocumentParser parser = CollectionBuilder.getDefaultXmlDocumentParser();
           String filteredEAD = filterSampleEAD(collectionWork, parser);
           InputStream eadIn = new ByteArrayInputStream(filteredEAD.getBytes());
           JsonNode parserCfg = CollectionBuilder.getDefaultCollectionCfg();
           ((ObjectNode) parserCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("validateXML", "no");
           ((ObjectNode) parserCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("storeCopy", "no");
           parser.init(collectionWorkId, eadIn, parserCfg);
           List<String> filteredElementCfg = parser.parseFiltersCfg();
           assertFalse(hasFilteredEADElement(parser.doc.getRootElement(), filteredElementCfg));
        }
    }
    
    private boolean hasFilteredEADElement(Node node, List<String> filterList) {
        Elements elements = ((Element) node).getChildElements();
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                if (filterList.contains(element.getLocalName().toUpperCase())) {
                    return true;
                } else {
                    return hasFilteredEADElement(element, filterList);
                }
            }
        }
        return false;
    }

    private String filterSampleEAD(Work collectionWork, XmlDocumentParser parser) throws FileNotFoundException, ValidityException,
            ParsingException, IOException {
        Path testEADPath = Paths.get("test/resources/6442.xml");
        InputStream eadData = new FileInputStream(testEADPath.toFile());
        JsonNode parserCfg = CollectionBuilder.getDefaultCollectionCfg();
        ((ObjectNode) parserCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("validateXML", "no");
        ((ObjectNode) parserCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("storeCopy", "no");
        parser.init(collectionWorkId, eadData, parserCfg);
        String filteredEAD = CollectionBuilder.filterEAD(collectionWork, parser);
        return filteredEAD;
    }
    
    @Test
    public void testExtractFirstEADComponent() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            Work componentWork = collectionWork.asEADWork().addEADWork();
            XmlDocumentParser parser = CollectionBuilder.getDefaultXmlDocumentParser();
            String filteredEAD = filterSampleEAD(collectionWork, parser);
            Elements components = ((Element) parser.getElementsByXPath(parser.doc, "//ead:ead/ead:archdesc/ead:dsc").get(0)).getChildElements();
            Element component = null;
            for (int i = 0; i < components.size(); i++) {
                log("component name is " + components.get(i).getLocalName());
                if (components.get(i).getLocalName().toUpperCase().startsWith("C")) {
                    component = components.get(i);
                    break;
                }
            }
            if (component != null) {
                String asId = component.getAttributeValue("id");
                String componentEAD = CollectionBuilder.extractEADComponent(collectionWork, asId, componentWork, component, parser);
                assertTrue(componentEAD != null && !componentEAD.isEmpty());
            }
        }
    }
    
    @Test
    public void testGetDefaultCollectionCfg() throws JsonGenerationException, JsonMappingException, IOException {
        // Verify the default collection configuration contains the expected configuration entries.
        JsonNode cfg = CollectionBuilder.getDefaultCollectionCfg();
        assertNotNull(cfg);
        assertNotNull(cfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT));
        assertNotNull(cfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT).get(XmlDocumentParser.CFG_VALIDATE_XML));
        assertNotNull(cfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT).get(XmlDocumentParser.CFG_STORE_COPY));
        assertNotNull(cfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT).get(XmlDocumentParser.CFG_SUB_ELEMENTS));
        
        XmlDocumentParser parser = CollectionBuilder.getDefaultXmlDocumentParser();
        // Verify that the standard parsing configuration have validateXML and storeCopy set as true.
        parser.parsingCfg = cfg;
        assertFalse(parser.validateXML());
        assertTrue(parser.storeCopy());
        
        // Verify that the parsing configuration (collectCfg) used for other tests in this unit test class has
        // validateXML and storeCopy set as false.
        parser.parsingCfg = collectCfg;
        assertFalse(parser.validateXML());
        assertFalse(parser.storeCopy());
    }
    
    @Test
    public void testReloadEADPreChecks() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            boolean storeCopy = true;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            InputStream in = new FileInputStream(testEADPath.toFile());
            EADParser parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            List<String> componentsNotInEAD = CollectionBuilder.reloadEADPreChecks(collectionWork.asEADWork(), parser);
            assertTrue(componentsNotInEAD.isEmpty());
        }
    }
    
    @Test
    public void testReloadCollection() throws ValidityException, IOException, ParsingException {
        String updedCompASId = "aspace_7275d12ba178fcbb7cf926d0b7bf68cc";
        
        // create collection from EAD
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            collectionWork.setCollection("nla.ms");
            boolean storeCopy = true;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            
            // verify the component of AS id (i.e updedCompASId) has collection work as its parent
            Map<String, String> uuidToPIMap = CollectionBuilder.componentWorksMap(collectionWork);
            Work componentToUpdate = as.findWork(uuidToPIMap.get(updedCompASId));
            assertEquals(collectionWork, componentToUpdate.getParent());
            
            //------------------------------------------------------------------------------------
            // The reload collection from updated EAD process
            //------------------------------------------------------------------------------------
            // step 1: delete existing finding aid copy, and attach new finding aid copy to collection work.
            Copy ead = collectionWork.getCopy(CopyRole.FINDING_AID_COPY);
            collectionWork.removeCopy(ead);
            collectionWork.addCopy(testUpdedEADPath, CopyRole.FINDING_AID_COPY, "application/xml");  
            
            // step 2: reload collection from updated EAD: 
            //         - add new component works from the updated EAD.
            //         - update existing component works from the updated EAD.
            CollectionBuilder.reloadCollection(collectionWork);
            as.commit();
            
            // verify the component of AS id (i.e. updatedCompAsId) is under the first component work 
            // within the collection as per specified by the EAD
            Map<String, String> newUUIDToPIMap = CollectionBuilder.componentWorksMap(collectionWork);
            String fistCompASId = "aspace_d1ac0117fdba1b9dc09b68e8bb125948";
            Work updatedComponent = as.findWork(newUUIDToPIMap.get(updedCompASId));
            assertNotEquals(collectionWork, updatedComponent.getParent());
            assertEquals(fistCompASId, updatedComponent.getParent().getLocalSystemNumber());
        }
    }
    
    @Test
    public void testComponentWorksMap() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            boolean storeCopy = true;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            Map<String, String> componentWorksMap = CollectionBuilder.componentWorksMap(collectionWork);
            assertTrue(!componentWorksMap.isEmpty());
            assertEquals(componentWorksMap.size(), 8);
        }
    }
    
    @Test
    public void testDigitalObjectsMap() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            boolean storeCopy = true;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            List<String> currentDOs = CollectionBuilder.digitisedItemList(collectionWork);
            assertTrue(currentDOs.isEmpty());
        }
    }
    
    @Test
    public void testParsingArchiveSpaceIDs() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            InputStream in = new FileInputStream(testEADPath.toFile());
            EADParser parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            List<String> uuids = parser.listUUIDs();
            assertTrue(!uuids.isEmpty());
            assertEquals(uuids.size(), 8);
        }
    }
        
    private void createCollection() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            InputStream in = new FileInputStream(testEADPath.toFile());
            String collectionName = testEADPath.getFileName().toString();
            EADParser parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            CollectionBuilder.processCollection(collectionWork, collectionName, in, collectCfg, parser);
            as.commit();
        }
    }
    
    private void log(String msg) {
        log.info(msg);
    }
}
