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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import amberdb.enums.CopyRole;
import amberdb.model.Work;

public class CollectionBuilderTest {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilderTest.class);
    AmberDb db;
    String collectionWorkId;
    ObjectMapper objectMapper;
    JsonNode collectCfg;
    Path testEADPath;
    String[] testEADFiles = { "test/resources/6442.xml" };

    @Before
    public void setUp() throws JsonProcessingException, IOException {
        testEADPath = Paths.get("test/resources/6442.xml");
      
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
            collectionWork.asEADWork().setEADUpdateReviewRequired("Y");   
            collectionWork.asEADWork().setAccessConditions("Restricted");
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
           parser.init(eadIn, parserCfg);
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
        parser.init(eadData, parserCfg);
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
        
    private void createCollection() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            InputStream in = new FileInputStream(testEADPath.toFile());
            String collectionName = testEADPath.getFileName().toString();
            CollectionBuilder.createCollection(collectionWork, collectionName, in, collectCfg, new EADParser());
            as.commit();
        }
    }
    
    private void log(String msg) {
        log.info(msg);
    }
}
