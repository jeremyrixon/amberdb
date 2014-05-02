package amberdb.sql;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import amberdb.AmberSession;
import amberdb.lookup.ListLu;
import amberdb.lookup.ToolsLu;

public class AmberLookupsTest {
    private AmberSession session;
    private LookupsMock lookups;
    
    @Before
    public void setup() {
        session = new AmberSession();
        lookups = session.getAmberGraph().dbi().onDemand(LookupsMock.class);
    }

    @After
    public void teardown() {
        session = null;
    }
    
    @Test
    public void testFindActiveDevices() {
        List<ToolsLu> devices = lookups.findActiveToolsFor("toolCategory", "Device");
        assertNotNull(devices);
        assertEquals(devices.size(), 10);
    }
    
    @Test
    public void testFindActiveScanners() {
        List<ToolsLu> scanners = lookups.findActiveToolsFor("toolType", "scanner");
        assertNotNull(scanners);
        System.out.println("devices size " + scanners.size());
        assertEquals(scanners.size(), 6);
    }
    
    @Test
    public void testFindActiveToolsForNikonInInitialSeeds() {
        List<ToolsLu> nikonEntries = lookups.findActiveToolsFor("name", "Nikon");
        assertNotNull(nikonEntries);
        assert(nikonEntries.size() == 2);
    }
    
    @Test
    public void testFindToolsEverRecordedForNikonInInitialSeeds() {
        List<ToolsLu> nikonEntries = lookups.findToolsEverRecordedFor("name", "Nikon");
        assertNotNull(nikonEntries);
        assert(nikonEntries.size() == 2);
    }
    
    @Test
    public void testFindActiveToolCannon20D() {
        long id = 9L;
        ToolsLu cannon20D = lookups.findActiveTool(id);
        assert(cannon20D.getName().equals("Canon 20D"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testExceptionForNonExistentTool() {
        Long id = 0L;
        lookups.findActiveTool(id);
    }
    
    @Test
    public void testUpdateToolCannon20DResolution() {
        long id = 9L;
        String lbl = "Cannon 20D";
        String code = "null";
        ToolsLu cannon20D = lookups.findActiveTool(id);
        assert(cannon20D.getName().equals("Canon 20D"));
        assert(cannon20D.getResolution().equals(""));
        lookups.updateLookupData(id, 
                                 "tools", 
                                 lbl, "resolution", "", "3504x2336");
        ToolsLu updedCannon = lookups.findActiveTool(id);
        assert(updedCannon.getName().equals("Canon 20D"));
        assert(updedCannon.getResolution().equals("3504x2336"));
    }
    
    @Test
    public void testUpdateToolCannon20DToolType() {
        long canonId = 9L;        
        long oldToolTypeId = 452L;
        long newToolTypeId = 451L;
        assert(lookups.hasAssociation(canonId, oldToolTypeId));
        lookups.updateLookupDataMap(canonId, oldToolTypeId, newToolTypeId);
        assert(lookups.hasAssociation(canonId, newToolTypeId));
    }
    
    @Test
    public void testUpdateToolCannon20DMaterialType() {
        long canonId = 9L;        
        long oldMaterialId = 501L;
        long newMaterialId = 502L;
        assert(lookups.hasAssociation(canonId, oldMaterialId));
        lookups.updateLookupDataMap(canonId, oldMaterialId, newMaterialId);
        assert(lookups.hasAssociation(canonId, newMaterialId));
    }
    
    @Test
    public void testUpdateToolCannon20DToolCategory() {
        long canonId = 9L;        
        long deviceId = 499L;
        long softwareId = 500L;
        assert(lookups.hasAssociation(canonId, deviceId));
        lookups.updateLookupDataMap(canonId, deviceId, softwareId);
        assert(lookups.hasAssociation(canonId, softwareId));
    }
    
    @Test
    public void testUpdateMaterialTypeTextToOcrText() {
        assert(activeLookupsInclude("carrier", "Online"));
        assertFalse(activeLookupsInclude("carrier", "Born Digital"));
        
        ListLu imageMT = lookups.findLookup("materialType", "Text");
        lookups.updateLookupData(imageMT.getId(), imageMT.getName(), imageMT.getValue(), "Ocr Text");
        assertFalse(activeLookupsInclude("materialType", "Text"));
        assert(activeLookupsInclude("materialType", "Ocr Text"));
    }
    
    private boolean activeLookupsInclude(String name, String value) {
        List<ListLu> values = lookups.findActiveLookupsFor(name);
        if (values == null || values.isEmpty()) return false;
        for (ListLu luValue : values) {
            if (luValue.getValue().equals(value))
                return true;
        }
        return false;
    }

    @Test
    public void testAddMaterialTypeWireless() {
        assertFalse(activeLookupsInclude("materialType", "wireless"));
        lookups.addLookupData(lookups.nextLookupId(), "materialType", "wireless");
        assert(activeLookupsInclude("materialType", "wireless"));
    }
    
    @Test
    public void testAddSoftwareAdobeIllustrator() {
        long id = lookups.nextLookupId();
        lookups.addLookupData(id, "tools", "", "", "name", "Adobe Illustrator CC", "N");
        lookups.addLookupData(id, "tools", "", "", "resolution", "", "N");
        lookups.addLookupData(id, "tools", "", "", "serialNumber", "11-220-284", "N");
        lookups.addLookupData(id, "tools", "", "", "notes", "Use to create clip art", "N");
        
        ToolsLu ai = lookups.findActiveTool(id);
        assert(ai.getName().equals("Adobe Illustrator CC"));
        assert(ai.getResolution().equals(""));
        assert(ai.getSerialNumber().equals("11-220-284"));
        assert(ai.getNotes().equals("Use to create clip art"));
    }
}
