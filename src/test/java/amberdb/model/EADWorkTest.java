package amberdb.model;

import amberdb.AmberSession;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EADWorkTest {
    private EADWork componentWork;
    private AmberSession db;

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
        Assert.assertEquals(expectedRdsType, componentWork.getRdsAcknowledgementType());
        Assert.assertEquals(expectedRdsReceiver, componentWork.getRdsAcknowledgementReceiver());
        Assert.assertEquals(expectedEADReviewYN, componentWork.getEADUpdateReviewRequired());
        Assert.assertEquals(expectedFolder, componentWork.getFolder());
    }

    private void setTestDataInH2(AmberSession db) {
        EADWork collectionWork = db.addWork().asEADWork();
        componentWork = collectionWork.addEADWork();
        componentWork.setSubType("series");
        componentWork.setTitle("Papers of Leslie Greener");
    }
}
