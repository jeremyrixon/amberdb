package amberdb.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberSession;
import amberdb.TestUtils;
import amberdb.enums.CopyRole;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
        List<String> aliases = copy.getAlias();
        assertEquals(0, aliases.size());
        assertFalse(aliases.contains("testingc"));
        aliases.add("testing");
        aliases.add("testinga");
        aliases.add("testingb");
        aliases.add("testingc");
        aliases.add("testingd");
        copy.setAlias(aliases);
        aliases = copy.getAlias();
        assertEquals(5,aliases.size());
        assertTrue(aliases.contains("testingc"));
        aliases.add("octopus");
        copy.setAlias(aliases);
        aliases = copy.getAlias();
        assertEquals(6, aliases.size());
        assertTrue(aliases.contains("octopus"));
    }
    
    
    @Test
    public void testGetFiles() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        long sessId = amberDb.suspend();
        amberDb.recover(sessId);
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
    
    @Test
    public void testIntegerProperties() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        ImageFile imageFile = copy.addImageFile();
        imageFile.setImageLength(null);
        assertEquals(null, imageFile.getImageLength());
        imageFile.setImageLength(200);
        assertEquals((Integer)200, imageFile.getImageLength());
    }
    
    @Test
    public void testToEnsureDCMLegacyDataFieldsExist() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        
        Date date = new Date();
        
        copy.setDcmDateTimeCreated(date);
        assertEquals(date, copy.getDcmDateTimeCreated());
        
        copy.setDcmCopyPid("12345");
        assertEquals("12345", copy.getDcmCopyPid());
        
        copy.setDcmSourceCopy("12345");
        assertEquals("12345", copy.getDcmSourceCopy());
        
        copy.setDcmDateTimeUpdated(date);
        assertEquals(date, copy.getDcmDateTimeUpdated());
        
        copy.setDcmRecordCreator("creator");
        assertEquals("creator", copy.getDcmRecordCreator());
        
        copy.setDcmRecordUpdater("updater");
        assertEquals("updater", copy.getDcmRecordUpdater());                
        
    }

    /**
     * This test will:
     * 1 - Create a work, copy and imagefile. Commit change.
     * 2 - Retrieves the work from the objectId and then updates one property on the copy and imageFile. Commit change.
     * 3 - Retrieves the work and tests for the changed property on copy and imageFile.
     * 
     * This test will only try and change a property on Copy and ImageFile that was part of the original create.
     */
    @Test
    public void shouldBeAbleToCreateAndEditCopyAndImageFileMetadata() throws IOException {
        
        String DEVICE = "Canon";
        String DEVICE_UPDATED = "Nikon";
        
        String RECORDSOURCE = "VOYAGER";
        String RECORDSOURCE_UPDATED = "VUFIND";
        
        // Create the Work
        Work work = amberDb.addWork();        
        work.setTitle("cat");
        
        // Create the Copy
        Copy copy = work.addCopy();
        copy.setCopyRole(CopyRole.ACCESS_COPY.code());
        copy.setRecordSource(RECORDSOURCE);
        
        // Add a File
        ImageFile imageFile = copy.addImageFile();
        imageFile.setDevice(DEVICE);        
        
        // Double Check that the values have been set
        assertEquals(RECORDSOURCE, copy.getRecordSource());
        assertEquals(DEVICE, imageFile.getDevice());
        
        Long objectId = work.getId();
        
        amberDb.commit();        
        
        work = null;
        copy = null;
        imageFile = null;
        
        // Retrieve Work / Copy / ImageFile 
        // and make changes to ImageFile
        //amber = amberDbService.get();
        Work workB = amberDb.findWork(objectId);
        assertNotNull(workB);        
        Copy copyB = workB.getCopy(CopyRole.ACCESS_COPY);        
        assertNotNull(copyB);
        ImageFile imageFileB = copyB.getImageFile();
        assertNotNull(imageFileB);
        
        // Before Updating the values check that they haven't changed
        assertEquals(RECORDSOURCE, copyB.getRecordSource());
        assertEquals(DEVICE, imageFileB.getDevice());
        
       
        // Make a change to Copy and ImageFile
        copyB.setRecordSource(RECORDSOURCE_UPDATED);
        imageFileB.setDevice(DEVICE_UPDATED);
        
        // Check that the updated values have been set 
        assertEquals(RECORDSOURCE_UPDATED, copyB.getRecordSource());
        assertEquals(DEVICE_UPDATED, imageFileB.getDevice());
        
        amberDb.commit();        
        
        workB = null;
        copyB = null;
        imageFileB = null;
        
        // Retrieve Work / Copy / ImageFile                 
        Work workC = amberDb.findWork(objectId);
        assertNotNull(workC);        
        Copy copyC = workC.getCopy(CopyRole.ACCESS_COPY);        
        assertNotNull(copyC);
        ImageFile imageFileC = copyC.getImageFile();
        assertNotNull(imageFileC);
        
        // Check for changes against the updated values
        assertEquals("Expecting the Record Source on Copy to be updated", RECORDSOURCE_UPDATED, copyC.getRecordSource());
        assertEquals("Expecting the Device on ImageFile to be updated", DEVICE_UPDATED, imageFileC.getDevice());
    }

    @Test
    public void copyHasMultipleAccessCopies() {
        Work work = amberDb.addWork();
        Copy mCopy = work.addCopy();
        mCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy d1Copy = work.addCopy();
        Copy d2Copy = work.addCopy();

        d1Copy.setSourceCopy(mCopy);
        d2Copy.setSourceCopy(mCopy);

        Copy sourceCopy = d1Copy.getSourceCopy();
        assertThat(d1Copy.getSourceCopy().getCopyRole().equals(mCopy.getCopyRole()), is(true));
        assertThat(Iterables.size(mCopy.getDerivatives()), is(2));
    }

    @Test
    public void copyHasSingleComaster() {
        Work work = amberDb.addWork();
        Copy mCopy = work.addCopy();
        mCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy d1Copy = work.addCopy();
        d1Copy.setNotes("d1");
        Copy d2Copy = work.addCopy();
        d2Copy.setNotes("d2");

        mCopy.setComasterCopy(d1Copy);
        mCopy.setComasterCopy(d2Copy);
        assertThat(mCopy.getComasterCopy().getNotes(), is("d2"));
    }
}

