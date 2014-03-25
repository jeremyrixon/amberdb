package amberdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberSession;
import amberdb.TestUtils;

public class CopyTest {
    
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();    
    
    private AmberSession amberDb;
    
    @Before
    public void startup() {
        amberDb = new AmberSession();           
    }
    
    @After
    public void teardown() throws IOException {
        if (amberDb != null) {
            amberDb.close();
        }       
    }

    @Test
    public void shouldCreateAnImageFileWhenMimeTypeStartsWithImage() throws Exception {                     
        Work work = amberDb.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.tiff"), "image/tiff").setOrder(1);

        File file = work.getPage(1).getCopies().iterator().next().getFile();        
        assertTrue(file instanceof ImageFile);        
    }
    
    @Test
    public void shouldCreateAFile() throws Exception {                     
        Work work = amberDb.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.xml"), "text/html").setOrder(1);

        File file = work.getPage(1).getCopies().iterator().next().getFile();        
        assertFalse(file instanceof ImageFile);
        assertTrue(file instanceof File);        
    }

    @Test
    public void testGetSetAllOtherNumbers() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        Map<String, String> otherNumbers = copy.getAllOtherNumbers();
        assertEquals(0, otherNumbers.size());
        otherNumbers.put("Voyager", "voyagerNumber");
        otherNumbers.put("State Library of Victoria", "slvNumber");
        otherNumbers.put("Jon's Cookbook", "1");
        copy.setAllOtherNumbers(otherNumbers);
        otherNumbers = copy.getAllOtherNumbers();
        assertEquals(3, otherNumbers.size());
        assertEquals("slvNumber", otherNumbers.get("State Library of Victoria"));
        otherNumbers.put("fruitNumber", "23");
        copy.setAllOtherNumbers(otherNumbers);
        otherNumbers = copy.getAllOtherNumbers();
        assertEquals(4, otherNumbers.size());
        assertEquals("23", otherNumbers.get("fruitNumber"));
    }
    
    @Test
    public void testGetSetAlias() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        List<String> aliases = copy.getAliases();
        assertEquals(0, aliases.size());
        assertFalse(aliases.contains("testingc"));
        aliases.add("testing");
        aliases.add("testinga");
        aliases.add("testingb");
        aliases.add("testingc");
        aliases.add("testingd");
        copy.setAliases(aliases);
        aliases = copy.getAliases();
        assertEquals(5,aliases.size());
        assertTrue(aliases.contains("testingc"));
        aliases.add("octopus");
        copy.setAliases(aliases);
        aliases = copy.getAliases();
        assertEquals(6, aliases.size());
        assertTrue(aliases.contains("octopus"));
    }
    
    
    @Test
    public void testGetFiles() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        assertEquals(null, copy.getImageFile());
        ImageFile imageFile = copy.addImageFile();
        assertEquals(null, copy.getSoundFile());
        SoundFile soundFile = copy.addSoundFile();
        imageFile.setDevice("frog");
        soundFile.setBitrate("amsterdam");
        ImageFile otherImageFile = copy.getImageFile();
        SoundFile otherSoundFile = copy.getSoundFile();
        assertEquals("ImageFile", otherImageFile.getType());
        assertEquals("frog", otherImageFile.getDevice());
        assertEquals("SoundFile", otherSoundFile.getType());
        assertEquals("amsterdam", otherSoundFile.getBitrate());
    }
    
    
    
    @Test
    public void testDateProperties() throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("12/12/1987");
        Copy copy = amberDb.addWork().addCopy();
        copy.setDateCreated(date);
        assertEquals(date, copy.getDateCreated());
    }
    
    

}
