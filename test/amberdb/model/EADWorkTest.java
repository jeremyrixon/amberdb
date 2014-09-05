package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import amberdb.AmberSession;

public class EADWorkTest {
    private static EADWork greenerCollection;
    private static AmberSession db;
    
    @Before
    public void setup() throws IOException, InstantiationException {
        db = new AmberSession();
        setTestDataInH2(db);
    }
    
    @Test
    public void testSetEADProperties() throws JsonParseException, JsonMappingException, IOException {
        String expectedRdsType = "Sponsor";
        String expectedRdsReceiver = "NLA";
        String expectedEADReviewYN = "Y";
        String[] items = { "box-6" };
        List<String> expectedFolder = Arrays.asList(items);
        
        greenerCollection.setRdsAcknowledgementType(expectedRdsType);
        greenerCollection.setRdsAcknowledgementReceiver(expectedRdsReceiver);
        greenerCollection.setEADUpdateReviewRequired(expectedEADReviewYN);
        greenerCollection.setFolder(expectedFolder);
        assertEquals(expectedRdsType, greenerCollection.getRdsAcknowledgementType());
        assertEquals(expectedRdsReceiver, greenerCollection.getRdsAcknowledgementReceiver());
        assertEquals(expectedEADReviewYN, greenerCollection.getEADUpdateReviewRequired());
        assertEquals(expectedFolder, greenerCollection.getFolder());
    }
    
    private static void setTestDataInH2(AmberSession db) {
        greenerCollection = db.addWork().asEADWork();
        greenerCollection.setSubType("series");
        greenerCollection.setTitle("Papers of Leslie Greener");
    }
}
