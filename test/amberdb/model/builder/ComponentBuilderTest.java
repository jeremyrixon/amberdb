package amberdb.model.builder;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.model.Work;

public class ComponentBuilderTest {
    static final Logger log = LoggerFactory.getLogger(ComponentBuilderTest.class);
    AmberDb db;
    String collectionWorkId;
    ObjectMapper objectMapper;
    JsonNode collectCfg;
    Path testEADPath;
    String[] testEADFiles = { "test/resources/6442.xml" };
    
    @Before
    public void setUp() throws IOException {
        testEADPath = Paths.get("test/resources/6442.xml");
        objectMapper = new ObjectMapper();
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
    public void testPurgePreChecks() {
        
    }
    
    @Test
    public void testPurgeComponents() {
        
    }
    
    @Test
    public void testUpdateComponentData() {
        
    }
    
    @Test
    public void testUpdateComponentPath() {
        
    }
    
    @Test
    public void testMergeComponent() {
        
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
