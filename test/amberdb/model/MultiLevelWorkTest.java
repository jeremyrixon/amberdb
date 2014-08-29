package amberdb.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.enums.DigitalStatus;
import amberdb.enums.Form;
import amberdb.enums.SubType;

public class MultiLevelWorkTest {
    private static Work workCollection;
    private static AmberSession db;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void setup() throws IOException, InstantiationException, ParseException {
        db = new AmberSession();
        setTestDataInH2(db);
    }
    
    private void setTestDataInH2(AmberSession db) throws ParseException, JsonParseException, JsonMappingException, IOException {
        workCollection = db.addWork();
        workCollection.setSubType(SubType.COLLECTION.code());
        workCollection.setForm(Form.MANUSCRIPT.code());
        workCollection.setSubUnitType(db.getLookups().findLookup("subUnitType", "Collection").getCode());
        workCollection.setBibId("12345");
        workCollection.setBibLevel("Set");
        workCollection.setDigitalStatus(DigitalStatus.DIGITISED.code());
        workCollection.setCreator("Lord Gowrie");
        
        workCollection.setStartDate(dateFormat.parse("01/01/1835"));
        workCollection.setEndDate(dateFormat.parse("01/01/1987"));
        workCollection.setExtent("8.54 m. (61 boxes) + 8 folio boxes");
        workCollection.setChildRange("c01 aspace_series1 - c01 aspace_series21??");
        workCollection.setStartChild("c01 aspace_series1??");
        workCollection.setEndChild("c01 aspece_series21??");
        workCollection.setRecordSource("FA");
        workCollection.setLocalSystemNumber("top level does not have an AS number, set to null??");
        workCollection.setAccessConditions("restricted");  // scopecontent/@audience="internal"

        workCollection.setConstraint(Arrays.asList(new String[] { "Closed" }));
        workCollection.setRdsAcknowledgementType("Sponsor");
        workCollection.setRdsAcknowledgementReceiver("National Library of Australia");
        workCollection.setEADUpdateReviewRequired("Yes");
        
        workCollection.setCollection("nla.ms");
        workCollection.setTitle("Papers of Lord Gowrie v5");
        
        Path tmpEADFile = folder.newFile().toPath();
        Files.write(tmpEADFile, "EAD data".getBytes());
        Copy eadCopy = workCollection.addCopy(tmpEADFile, CopyRole.FINDING_AID_COPY, "application/xml");
        
        Work series1 = db.addWork();
        series1.setSubType(SubType.SERIES.code());
        workCollection.setForm(Form.MANUSCRIPT.code());
        workCollection.setSubUnitType(db.getLookups().findLookup("subUnitType", "Series").getCode());
        workCollection.setBibId("12345");
        workCollection.setBibLevel("Item");
        workCollection.setDigitalStatus(DigitalStatus.DIGITISED.code());
        workCollection.setCreator("Lord Gowrie");
        workCollection.addChild(series1);
    }
    
    @Test
    public void testLoadMultiLevelWork() {
        
    }
}
