package amberdb.model.builder;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.AccessCondition;
import amberdb.enums.CopyRole;
import amberdb.model.EADWork;
import amberdb.model.Work;

import static amberdb.model.builder.XmlDocumentParser.CFG_COLLECTION_ELEMENT;
import static amberdb.model.builder.XmlDocumentParser.CFG_SUB_ELEMENTS;
import static amberdb.model.builder.XmlDocumentParser.CFG_BASE;
import static amberdb.model.builder.XmlDocumentParser.CFG_REPEATABLE_ELEMENTS;

public class ComponentBuilderTest {
    AmberDb db;
    String collectionWorkId;
    ObjectMapper objectMapper;
    EADParser parser;
    JsonNode collectCfg;
    Path testEADPath;
    String[] testEADFiles = { "test/resources/6442.xml" };
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void setUp() throws IOException, ValidityException, ParsingException {
        testEADPath = Paths.get("test/resources/6442.xml");
        objectMapper = new ObjectMapper();
        collectCfg = CollectionBuilder.getDefaultCollectionCfg();
        ((ObjectNode) collectCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("validateXML", "no");
        ((ObjectNode) collectCfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT)).put("storeCopy", "no");

        // create the top-level work
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:cache", "store", "collection");
        db = new AmberDb(ds, folder.getRoot().toPath());
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.addWork(); 
            collectionWork.setSubType("Work");
            collectionWork.setSubUnitType("Collection");
            collectionWork.setForm("Manuscript");
            collectionWork.setBibLevel("Set");
            collectionWork.setCollection("nla.ms");
            collectionWork.setRecordSource("FA");
            collectionWork.asEADWork().setRdsAcknowledgementType("Sponsor");
            collectionWork.asEADWork().setRdsAcknowledgementReceiver("NLA");  
            collectionWork.asEADWork().setAccessConditions(AccessCondition.RESTRICTED.code());
            collectionWorkId = collectionWork.getObjId();
            collectionWork.addCopy(Paths.get("test/resources/6442.xml"), CopyRole.FINDING_AID_COPY, "application/xml");
            as.commit();
        }        
        createCollection();
    }
    
    @Test
    public void testMakeComponent() {
        String expectedASId = "aspace_d1ac0117fdba1b9dc09b68e8bb125948";
        JsonNode firstComp = makeComponent(0);
        assertEquals(expectedASId, firstComp.get("uuid").getTextValue());
    }
    
    @Test
    public void testUpdateComponentData() throws IOException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            collectionWork.setCollection("nla.ms");
            collectionWork.setAccessConditions(AccessCondition.UNRESTRICTED.code());
            EADWork componentWork = collectionWork.asEADWork().addEADWork();
            assertNull(componentWork.getSubType());
            assertNull(componentWork.getSubUnitType());
            assertNull(componentWork.getForm());
            assertNull(componentWork.getBibLevel());
            assertNull(componentWork.getCollection());
            assertNull(componentWork.getRecordSource());
            assertNull(componentWork.getLocalSystemNumber());
            assertNull(componentWork.getRdsAcknowledgementType());
            assertNull(componentWork.getRdsAcknowledgementType());
            assertNull(componentWork.getEADUpdateReviewRequired());
            assertNull(componentWork.getAccessConditions());

            JsonNode firstComp = makeComponent(0);
            Map<String, String> map = new ConcurrentHashMap<>();
            map.put("component-level", "Series");
            ComponentBuilder.mapWorkMD(componentWork, firstComp.get("uuid").getTextValue(), map);
            String expectedSubType = "Work";
            String expectedSubUnitType = "Series";
            String expectedForm = "Manuscript";
            String expectedBibLevel = "Set";
            String expectedCollection = "nla.ms";
            String expectedRecordSource = "FA";
            String expectedLocalSystemNumber = "aspace_d1ac0117fdba1b9dc09b68e8bb125948";
            String expectedRdsAcknowledgementType = "Sponsor";
            String expectedRdsAcknowledgementReceiver = "NLA";
            String expectedEADUpdateReviewRequired = "N";
            String expectedAccessConditions = AccessCondition.UNRESTRICTED.code();
            
            assertEquals(expectedSubType, componentWork.getSubType());
            assertEquals(expectedSubUnitType, componentWork.getSubUnitType());
            assertEquals(expectedForm, componentWork.getForm());
            assertEquals(expectedBibLevel, componentWork.getBibLevel());
            assertEquals(expectedCollection, componentWork.getCollection());
            assertEquals(expectedRecordSource, componentWork.getRecordSource());
            assertEquals(expectedLocalSystemNumber, componentWork.getLocalSystemNumber());
            assertEquals(expectedRdsAcknowledgementType, componentWork.getRdsAcknowledgementType());
            assertEquals(expectedRdsAcknowledgementReceiver, componentWork.getRdsAcknowledgementReceiver());
            assertEquals(expectedEADUpdateReviewRequired, componentWork.getEADUpdateReviewRequired());
            assertEquals(expectedAccessConditions, componentWork.getAccessConditions());
        }
    }
    
    @Test
    public void testBibLevel() throws IOException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            EADWork component1 = collectionWork.asEADWork().addEADWork();
            EADWork component2 = component1.addEADWork();
            EADWork component3 = component2.addEADWork();
            EADWork component4 = component3.addEADWork();
            Map<String, String> map = new ConcurrentHashMap<>();
            map.put("component-level", "collection");
            
            Map<String, String> map1 = new ConcurrentHashMap<>();
            map1.put("component-level", "series");
            String localSystemNumber1 = "aspace_1";
            ComponentBuilder.mapWorkMD(component1, localSystemNumber1, map1);
            assertEquals("Set", component1.getBibLevel());
            
            Map<String, String> map2 = new ConcurrentHashMap<>();
            map2.put("component-level", "subseries");
            String localSystemNumber2 = "aspace_2";
            ComponentBuilder.mapWorkMD(component2, localSystemNumber2, map2);
            assertEquals("Set", component2.getBibLevel());
            
            Map<String, String> map3 = new ConcurrentHashMap<>();
            map3.put("component-level", "file");
            String localSystemNumber3 = "aspace_3";
            ComponentBuilder.mapWorkMD(component3, localSystemNumber3, map3);
            assertEquals("Set", component3.getBibLevel());
            
            Map<String, String> map4 = new ConcurrentHashMap<>();
            map4.put("component-level", "item");
            String localSystemNumber4 = "aspace_4";
            ComponentBuilder.mapWorkMD(component4, localSystemNumber4, map4);
            assertEquals("Item", component4.getBibLevel());
        }
    }
    
    @Test
    public void testUpdateComponentPath() throws IOException {
        JsonNode[] comps = new JsonNode[2];
        comps[0] = makeComponent(0);
        comps[1] = makeComponent(1);
        Map<String, String> componentWorksMap = new HashMap<>();
        
        // create 2 components for the first time
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            EADWork componentWork1 = collectionWork.asEADWork().addEADWork();
            EADWork componentWork2 = collectionWork.asEADWork().addEADWork();
            ComponentBuilder.mapWorkMD(componentWork1, comps[0].get("uuid").getTextValue(), new ConcurrentHashMap<String, String>());
            ComponentBuilder.mapWorkMD(componentWork2, comps[1].get("uuid").getTextValue(), new ConcurrentHashMap<String, String>());
            componentWorksMap.put(componentWork1.getLocalSystemNumber(), componentWork1.getObjId());
            componentWorksMap.put(componentWork2.getLocalSystemNumber(), componentWork2.getObjId());
            as.commit();
        }
        
        // verify the update of component path of comps[1]
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            EADWork[] componentWorks = new EADWork[2];
            int i = 0;
            for (String key : componentWorksMap.keySet()) {
                String objId = componentWorksMap.get(key);
                EADWork componentWork = as.findWork(objId).asEADWork();
                ((ObjectNode) comps[i]).put("nlaObjId", componentWork.getObjId());
                componentWorks[i] = componentWork;
                i++;
            }
            
            // verify the parent of both component works is currently the collection work.
            assertEquals(collectionWork, componentWorks[0].getParent());
            assertEquals(collectionWork, componentWorks[1].getParent());
            
            // update the component work 2's path through merge component
            componentWorks[1] = ComponentBuilder.mergeComponent(collectionWork.asEADWork(), componentWorks[0], comps[1]);
            
            // verify the parent of component work 1 is still the collection work.
            assertEquals(collectionWork, componentWorks[0].getParent());
            
            // verify the parent of component work 2 is now component work 1 after the update of its path
            assertNotEquals(collectionWork, componentWorks[1].getParent());
            assertEquals(componentWorks[0], componentWorks[1].getParent());
        }
    }
    
    private void createCollection() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            InputStream in = new FileInputStream(testEADPath.toFile());
            parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            CollectionBuilder.processCollection(collectionWork, collectCfg, parser);
            as.commit();
        }
    }
    
    private JsonNode makeComponent(int ord) {
        nu.xom.Document doc = parser.doc;
        JsonNode subElementsCfg = collectCfg.get(CFG_COLLECTION_ELEMENT).get(CFG_SUB_ELEMENTS);
        String basePath = subElementsCfg.get(CFG_BASE).getTextValue();
        String repeatablePath = subElementsCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
        Nodes nodes = parser.getElementsByXPath(doc, basePath); 
        if (nodes == null)
            fail("No components found from the test EAD file " + testEADFiles[0] + ", please check test EAD file.");
        Nodes components = parser.findXmlSubElementFromDoc(doc, nodes.get(0), repeatablePath);
        if (components == null)
            fail("No components found from the test EAD file " + testEADFiles[0] + ", please check test EAD file.");
        return ComponentBuilder.makeComponent(components.get(ord), subElementsCfg, parser);
    }
}
