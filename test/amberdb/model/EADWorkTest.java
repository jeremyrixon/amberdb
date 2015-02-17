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
    private static EADWork componentWork;
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
        
        componentWork.setRdsAcknowledgementType(expectedRdsType);
        componentWork.setRdsAcknowledgementReceiver(expectedRdsReceiver);
        componentWork.setEADUpdateReviewRequired(expectedEADReviewYN);
        componentWork.setFolder(expectedFolder);
        assertEquals(expectedRdsType, componentWork.getRdsAcknowledgementType());
        assertEquals(expectedRdsReceiver, componentWork.getRdsAcknowledgementReceiver());
        assertEquals(expectedEADReviewYN, componentWork.getEADUpdateReviewRequired());
        assertEquals(expectedFolder, componentWork.getFolder());
    }
    
    private static void setTestDataInH2(AmberSession db) {
        EADWork collectionWork = db.addWork().asEADWork();
        componentWork = collectionWork.addEADWork(); 
        componentWork.setSubType("series");
        componentWork.setTitle("Papers of Leslie Greener");
    }
}
