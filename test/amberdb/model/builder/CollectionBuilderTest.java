package amberdb.model.builder;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
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
    
    @Before
    public void setUp() throws JsonProcessingException, IOException {
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
            collectionWork.setCollection("nla.ms");
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
    public void test() {
        
    }
    
    // @Test
    public void testCreateCollection() throws IOException, ValidityException, ParsingException {
        // Verify before creating collection, collectionWork does not have any sub-work attached
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            assertNull(collectionWork.getPartsOf(new ArrayList<String>()));
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
    
    // @Test
    public void testGenerateJson() throws IOException, ValidityException, ParsingException {
        createCollection();
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            boolean storeCopy = false;
            Document doc = CollectionBuilder.generateJson(collectionWork, storeCopy);
            JsonNode content = doc.getContent();
            Iterator<String> it = content.getFieldNames();
            assertTrue(it.hasNext());
            int i = 0;
            while (it.hasNext()) {
                i++;
            }
            assertEquals(8, i);
        }
    }
    
    private void createCollection() throws IOException, ValidityException, ParsingException {
        try (AmberSession as = db.begin()) {
            Work collectionWork = as.findWork(collectionWorkId);
            InputStream in = new FileInputStream(testEADPath.toFile());
            boolean validateXml = false;
            CollectionBuilder.createCollection(collectionWork, in, collectCfg, new EADParser(), validateXml);
            as.commit();
        }
    }
}
