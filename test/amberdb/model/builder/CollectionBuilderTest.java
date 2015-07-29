package amberdb.model.builder;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.AccessCondition;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.EADEntity;
import amberdb.model.EADFeature;
import amberdb.model.Work;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import nu.xom.*;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class CollectionBuilderTest {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilderTest.class);
    AmberDb db;
    String collectionWorkId;
    ObjectMapper objectMapper;
    JsonNode collectCfg;
    Path testEADPath;
    Path testUpdedEADPath;
    String[] expectedUuids = {
            "aspace_d1ac0117fdba1b9dc09b68e8bb125948",
            "aspace_7275d12ba178fcbb7cf926d0b7bf68cc",
            "aspace_5c65cd1a0dd35517ba04da03d95ffac2",
            "aspace_ff977d51fa95c2d3a318fc7d6fb14451",
            "aspace_563116915a27063fb67dc5de82e2f848",
            "aspace_140a75e0f3a47eb5fbb735c7fba957ae", 
            "aspace_1012d592eedcfdbdc6175b91db070e2d",
            "aspace_3c0c615f787a41d4dc4c4104505e55a7"
    };
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private Path testDupEadPath;

    @Before
    public void setUp() throws JsonProcessingException, IOException {
        testEADPath = Paths.get("test/resources/6442.xml");
        testUpdedEADPath = Paths.get("test/resources/6442_updated.xml");
        testDupEadPath = Paths.get("test/resources/6442_dup.xml");
        
        objectMapper = new ObjectMapper();
        // collectCfg = objectMapper.readTree(new File("test/resources/ead.json"));
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
            
            int i = 0;
            for (Work subWork : subWorks) {
                String asId = subWork.getLocalSystemNumber();
                String creator = subWork.getCreator();
                String expectedCreator = expectedCreatorMap.get(asId);
                assertEquals(expectedCreator, creator);
                assertEquals(expectedUuids[i], asId);
                i++;
            }
        }
    }
    
    @Test
    public void testFindExistingEADWork() throws ValidityException, IOException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            List<Work> subWorks = collectionWork.getPartsOf(new ArrayList<String>());
            Map<String, String> idMap = new HashMap<>();
            for (Work subWork : subWorks) {
                idMap.put(subWork.getLocalSystemNumber(), subWork.getObjId());
            }
            for (String inputLocalSystemNumber : idMap.keySet()) {
                String expectedObjId = idMap.get(inputLocalSystemNumber);
                assertEquals(expectedObjId, collectionWork.asEADWork().checkEADWorkInCollectionByLocalSystemNumber(inputLocalSystemNumber).getObjId());
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
        assertNotNull(cfg.get(XmlDocumentParser.CFG_COLLECTION_ELEMENT).get(XmlDocumentParser.CFG_SUB_ELEMENTS));
        
        XmlDocumentParser parser = CollectionBuilder.getDefaultXmlDocumentParser();
        // Verify that the standard parsing configuration have validateXML and storeCopy set as true.
        parser.parsingCfg = cfg;
        assertFalse(parser.validateXML());

        // Verify that the parsing configuration (collectCfg) used for other tests in this unit test class has
        // validateXML and storeCopy set as false.
        parser.parsingCfg = collectCfg;
        assertFalse(parser.validateXML());
    }
    
    @Test
    public void testReloadEADPreChecks() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
//            Document doc = CollectionBuilder.generateJson(collectionWork);
            InputStream in = new FileInputStream(testEADPath.toFile());
            EADParser parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            Set<String> componentsNotInEAD = CollectionBuilder.reloadEADPreChecks(collectionWork.asEADWork(), parser);
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
            assertEquals(CollectionBuilder.getWorksRequiringReview(collectionWork).size(), 0);
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
            assertEquals(1, CollectionBuilder.getWorksRequiringReview(collectionWork).size());
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

    @Test(expected = EADValidationException.class)
    public void testDupInEad() throws ValidityException, IOException, ParsingException {

        // create collection from EAD
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            collectionWork.setCollection("nla.ms");

            Copy ead = collectionWork.getCopy(CopyRole.FINDING_AID_COPY);
            collectionWork.removeCopy(ead);
            collectionWork.addCopy(testDupEadPath, CopyRole.FINDING_AID_COPY, "application/xml");

            // step 2: reload collection from updated EAD:
            //         - add new component works from the updated EAD.
            //         - update existing component works from the updated EAD.
            CollectionBuilder.reloadCollection(collectionWork);
        }
    }
    
    @Test
    public void testEADCollectionWithGeneralNoteContainerList() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            List<EADFeature> features = collectionWork.asEADWork().getEADFeatures();
            assertEquals("features extracted", 2, features.size());
            ImmutableMap<String, EADFeature> stringEADFeatureImmutableMap =
                    Maps.uniqueIndex(features, new Function<EADFeature, String>() {
                        @Override
                        public String apply(EADFeature eadFeature) {
                            return eadFeature.getFeatureType();
                        }
                    });
            assertTrue("first feature heading", stringEADFeatureImmutableMap.containsKey("General"));
            assertTrue("second feature heading", stringEADFeatureImmutableMap.containsKey("Container List"));
            
            // verify extracted general note
            EADFeature generalFeature = stringEADFeatureImmutableMap.get("General");
            EADFeature containerFeature = stringEADFeatureImmutableMap.get("Container List");
            List<String> generalFields = generalFeature.getFields();
            List<List<String>> generalRecords = generalFeature.getRecords();
            assertEquals("general notes size", 2, generalRecords.size());
            assertEquals("no. of fields for general notes", 1, generalFields.size());
            assertEquals("no. of records for general notes", 2, generalRecords.size());
            
            // verify extracted container list
            List<String> containerListFields = containerFeature.getFields();
            List<List<String>> containerListRecords = containerFeature.getRecords();
            assertEquals("container list size", 176, containerListRecords.size());
            assertEquals("no. of fields for container record", 4, containerListFields.size());
            assertEquals("first container record: Series", "1", containerListRecords.get(0).get(0));
            assertEquals("first container record: Series", "1-9", containerListRecords.get(0).get(1));
            assertEquals("first container record: Series", "1-1029", containerListRecords.get(0).get(2));
            assertEquals("first container record: Series", "1", containerListRecords.get(0).get(3));
        }
    }
    
    @Test
    public void testEADCollectionWithCorrespondenceIndex() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            String correspondenceId = collectionWork.asEADWork().getCorrespondenceId();
            String correspondenceHeader = collectionWork.asEADWork().getCorrespondenceHeader();
            List<EADEntity> entities = collectionWork.asEADWork().getEADEntities();
            assertEquals("correspondence id", "aspace_3866e149682cb863686b1c74fe987122", correspondenceId);
            String expectedCorrespondenceHeader = "An index of correspondence items in Series 1.1 (letters to Patrick White), Series 1.2 (letters by Patrick White), and Series 26.1 (letters to Manoly Lascaris).  Each correspondent is listed alphabetically by surname. The numbers refer to the series, subseries and folder in which the correspondence is held.";
            assertEquals("correspondence header",  expectedCorrespondenceHeader, correspondenceHeader);
            assertEquals("correspondence list size",  215, entities.size());
        }
    }
    
    @Test
    public void testComponentWorksMap() throws ValidityException, IOException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
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
            Set<String> currentDOs = CollectionBuilder.digitisedItemList(collectionWork);
            assertTrue(currentDOs.isEmpty());
        }
    }
    
    @Test
    public void testParsingArchiveSpaceIDs() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            InputStream in = new FileInputStream(testEADPath.toFile());
            EADParser parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            Set<String> uuids = parser.listUUIDs(10);
            assertTrue(!uuids.isEmpty());
            assertEquals(uuids.size(), 8);
        }
    }
        
    private void createCollection() throws IOException, ValidityException, ParsingException {            
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            InputStream in = new FileInputStream(testEADPath.toFile());
            EADParser parser = new EADParser();
            parser.init(collectionWorkId, in, collectCfg);
            CollectionBuilder.processCollection(collectionWork, collectCfg, parser);
            as.commit();
        }
    }
    
    private void log(String msg) {
        log.info(msg);
    }
}
