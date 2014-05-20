package amberdb.sql;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import amberdb.AmberSession;

public class AmberLookupsTest {
    private AmberSession session;
    private Lookups lookups;

    @Before
    public void setup() {
        session = new AmberSession();
        lookups = session.getAmberGraph().dbi().onDemand(Lookups.class);
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
        assertEquals(scanners.size(), 10);
    }

    @Test
    public void testFindActiveToolsForNikonInInitialSeeds() {
        List<ToolsLu> nikonEntries = lookups.findActiveToolsFor("name", "Nikon");
        assertNotNull(nikonEntries);
        assert (nikonEntries.size() == 2);
    }

    @Test
    public void testFindToolsEverRecordedForNikonInInitialSeeds() {
        List<ToolsLu> nikonEntries = lookups.findToolsEverRecordedFor("name", "Nikon");
        assertNotNull(nikonEntries);
        assert (nikonEntries.size() == 2);
    }

    @Test
    public void testFindActiveToolCannon20D() {
        long id = 9L;
        ToolsLu cannon20D = lookups.findActiveTool(id);
        assert (cannon20D.getName().equals("Canon 20D"));
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionForNonExistentTool() {
        Long id = 0L;
        lookups.findActiveTool(id);
    }

    @Test
    public void testUpdateToolCannon20DResolution() {
        long id = 9L;
        ToolsLu cannon20D = lookups.findActiveTool(id);
        assert (cannon20D.getName().equals("Canon 20D"));
        assertNull(cannon20D.getResolution());
        cannon20D.setResolution("3504x2336");
        lookups.updTool(cannon20D);
        session.commit();
        ToolsLu updedCannon = lookups.findActiveTool(id);
        assert (updedCannon.getName().equals("Canon 20D"));
        assert (updedCannon.getResolution().equals("3504x2336"));
    }

    @Test
    public void testUpdateToolCannon20DToolType() {
        long canonId = 9L;
        long oldToolTypeId = 451L;
        long newToolTypeId = 452L;

        ToolsLu canon = lookups.findTool(canonId);
        assertEquals(oldToolTypeId, canon.getToolTypeId().longValue());
        canon.setToolTypeId(newToolTypeId);
        lookups.updTool(canon);
        ToolsLu newCanon = lookups.findTool(canonId);
        assertEquals(newToolTypeId, newCanon.getToolTypeId().longValue());
    }

    @Test
    public void testUpdateToolCannon20DMaterialType() {
        long canonId = 9L;
        long oldMaterialId = 501L;
        long newMaterialId = 502L;

        ToolsLu canon = lookups.findTool(canonId);
        assert (canon.getMaterialTypeId() == oldMaterialId);
        canon.setMaterialTypeId(newMaterialId);
        lookups.updTool(canon);
        ToolsLu newCanon = lookups.findTool(canonId);
        assert (newCanon.getMaterialTypeId() == newMaterialId);
    }

    @Test
    public void testUpdateToolCannon20DToolCategory() {
        long canonId = 9L;
        long deviceId = 499L;
        long softwareId = 500L;

        ToolsLu canon = lookups.findTool(canonId);
        assert (canon.getToolCategoryId() == deviceId);
        canon.setToolCategoryId(softwareId);
        lookups.updTool(canon);
        ToolsLu newCanon = lookups.findTool(canonId);
        assert (newCanon.getToolCategoryId() == softwareId);
    }

    @Test
    public void testUpdateMaterialTypeTextToOcrText() {
        ListLu imageMT = lookups.findLookup("materialType", "Text");
        imageMT.setValue("Ocr Text");
        lookups.updateLookup(imageMT);
        assertFalse(activeLookupsInclude("materialType", "Text"));
        assert (activeLookupsInclude("materialType", "Ocr Text"));
    }

    private boolean activeLookupsInclude(String name, String value) {
        List<ListLu> values = lookups.findActiveLookupsFor(name);
        if (values == null || values.isEmpty())
            return false;
        for (ListLu luValue : values) {
            if (luValue.getValue() != null && luValue.getValue().equals(value))
                return true;
        }
        return false;
    }

    @Test
    public void testAddMaterialTypeWireless() {
        assertFalse(activeLookupsInclude("materialType", "wireless"));
        ListLu wireless = new ListLu("materialType", "wireless");
        lookups.addLookup(wireless);
        assert (activeLookupsInclude("materialType", "wireless"));
    }

    @Test
    public void testAddSoftwareAdobeIllustrator() {
        ToolsLu ai = new ToolsLu("apaterso");
        ai.setName("Adobe Illustrator CC");
        ai.setSerialNumber("11-220-284");
        ai.setNotes("Use to create clip art");
        lookups.addTool(ai);

        List<ToolsLu> ais = lookups.findActiveToolsFor("name", ai.getName());
        ToolsLu newAI = ais.get(0);
        assert (newAI.getName().equals("Adobe Illustrator CC"));
        assert (newAI.getSerialNumber().equals("11-220-284"));
        assert (newAI.getNotes().equals("Use to create clip art"));
    }
}
