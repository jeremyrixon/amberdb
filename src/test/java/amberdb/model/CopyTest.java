package amberdb.model;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.TestUtils;
import amberdb.enums.CopyRole;
import com.google.common.collect.Iterables;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CopyTest extends AbstractDatabaseIntegrationTest {
    
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();    
    
    @Test
    public void shouldCreateAnImageFileWhenMimeTypeStartsWithImage() throws Exception {                     
        Work work = amberSession.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.tiff"), "image/tiff").setOrder(1);

        File file = work.getPage(1).getCopies().iterator().next().getFile();
        Assert.assertTrue(file instanceof ImageFile);
    }
    
    @Test
    public void shouldCreateAFile() throws Exception {                     
        Work work = amberSession.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.xml"), "text/html").setOrder(1);

        File file = work.getPage(1).getCopies().iterator().next().getFile();        
        Assert.assertFalse(file instanceof ImageFile);
        Assert.assertTrue(file instanceof File);
    }

    @Test
    public void testGetSetAllOtherNumbers() throws IOException {
        Copy copy = amberSession.addWork().addCopy();
        Map<String, String> otherNumbers = copy.getAllOtherNumbers();
        Assert.assertEquals(0, otherNumbers.size());
        otherNumbers.put("Voyager", "voyagerNumber");
        otherNumbers.put("State Library of Victoria", "slvNumber");
        otherNumbers.put("Jon's Cookbook", "1");
        copy.setAllOtherNumbers(otherNumbers);
        otherNumbers = copy.getAllOtherNumbers();
        Assert.assertEquals(3, otherNumbers.size());
        Assert.assertEquals("slvNumber", otherNumbers.get("State Library of Victoria"));
        otherNumbers.put("fruitNumber", "23");
        copy.setAllOtherNumbers(otherNumbers);
        otherNumbers = copy.getAllOtherNumbers();
        Assert.assertEquals(4, otherNumbers.size());
        Assert.assertEquals("23", otherNumbers.get("fruitNumber"));
    }
    
    @Test
    public void testGetSetAlias() throws IOException {
        Copy copy = amberSession.addWork().addCopy();
        List<String> aliases = copy.getAlias();
        Assert.assertEquals(0, aliases.size());
        Assert.assertFalse(aliases.contains("testingc"));
        aliases.add("testing");
        aliases.add("testinga");
        aliases.add("testingb");
        aliases.add("testingc");
        aliases.add("testingd");
        copy.setAlias(aliases);
        aliases = copy.getAlias();
        Assert.assertEquals(5,aliases.size());
        Assert.assertTrue(aliases.contains("testingc"));
        aliases.add("octopus");
        copy.setAlias(aliases);
        aliases = copy.getAlias();
        Assert.assertEquals(6, aliases.size());
        Assert.assertTrue(aliases.contains("octopus"));
    }

    @Test
    public void testGetFiles() throws IOException {
        Copy copy = amberSession.addWork().addCopy();
        long sessId = amberSession.suspend();
        amberSession.recover(sessId);
        Assert.assertEquals(null, copy.getImageFile());
        ImageFile imageFile = copy.addImageFile();
        Assert.assertEquals(null, copy.getSoundFile());
        Assert.assertEquals(null, copy.getMovingImageFile());
        SoundFile soundFile = copy.addSoundFile();
        MovingImageFile movingImageFile = copy.addMovingImageFile();
        imageFile.setDevice("frog");
        soundFile.setBitrate("amsterdam");
        movingImageFile.setBitDepth("12");
        ImageFile otherImageFile = copy.getImageFile();
        SoundFile otherSoundFile = copy.getSoundFile();
        MovingImageFile otherMovingImageFile = copy.getMovingImageFile();
        Assert.assertEquals("ImageFile", otherImageFile.getType());
        Assert.assertEquals("frog", otherImageFile.getDevice());
        Assert.assertEquals("SoundFile", otherSoundFile.getType());
        Assert.assertEquals("amsterdam", otherSoundFile.getBitrate());
        Assert.assertEquals("MovingImageFile", otherMovingImageFile.getType());
        Assert.assertEquals("12", otherMovingImageFile.getBitDepth());
    }

    @Test
    public void testDateProperties() throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("12/12/1987");
        Copy copy = amberSession.addWork().addCopy();
        copy.setDateCreated(date);
        Assert.assertEquals(date, copy.getDateCreated());
    }

    @Test
    public void testIntegerProperties() throws IOException {
        Copy copy = amberSession.addWork().addCopy();
        ImageFile imageFile = copy.addImageFile();
        imageFile.setImageLength(null);
        Assert.assertEquals(null, imageFile.getImageLength());
        imageFile.setImageLength(200);
        Assert.assertEquals((Integer)200, imageFile.getImageLength());
    }
    
    @Test
    public void testToEnsureDCMLegacyDataFieldsExist() throws IOException {
        Copy copy = amberSession.addWork().addCopy();
        
        Date date = new Date();
        
        copy.setDcmDateTimeCreated(date);
        Assert.assertEquals(date, copy.getDcmDateTimeCreated());
        
        copy.setDcmCopyPid("12345");
        Assert.assertEquals("12345", copy.getDcmCopyPid());
        
        copy.setDcmSourceCopy("12345");
        Assert.assertEquals("12345", copy.getDcmSourceCopy());
        
        copy.setDcmDateTimeUpdated(date);
        Assert.assertEquals(date, copy.getDcmDateTimeUpdated());
        
        copy.setDcmRecordCreator("creator");
        Assert.assertEquals("creator", copy.getDcmRecordCreator());
        
        copy.setDcmRecordUpdater("updater");
        Assert.assertEquals("updater", copy.getDcmRecordUpdater());
        
    }
    
    @Test
    public void testGetDerivedCopiesWithCopyRole() {
        Work work = amberSession.addWork();
        Copy sourceCopy = work.addCopy();
        sourceCopy.setCopyRole(CopyRole.ORIGINAL_COPY.code());
        Copy accessCopy1 = work.addCopy();
        accessCopy1.setCopyRole(CopyRole.ACCESS_COPY.code());
        accessCopy1.setSourceCopy(sourceCopy);
        Iterable<Copy> actualAccessCopies = sourceCopy.getDerivatives(CopyRole.ACCESS_COPY);
        Assert.assertEquals(1, Iterables.size(actualAccessCopies));
        Assert.assertEquals(accessCopy1, actualAccessCopies.iterator().next());
        Copy accessCopy2 = work.addCopy();
        accessCopy2.setCopyRole(CopyRole.ACCESS_COPY.code());
        accessCopy2.setSourceCopy(sourceCopy);
        actualAccessCopies = sourceCopy.getDerivatives(CopyRole.ACCESS_COPY);
        Assert.assertEquals(2, Iterables.size(actualAccessCopies));

        List<Copy> copies = new ArrayList<>();
        for (Copy c : actualAccessCopies) {
            copies.add(c);
        }
        Assert.assertTrue(copies.remove(accessCopy1));
        Assert.assertTrue(copies.remove(accessCopy2));
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
        Work work = amberSession.addWork();
        work.setTitle("cat");
        
        // Create the Copy
        Copy copy = work.addCopy();
        copy.setCopyRole(CopyRole.ACCESS_COPY.code());
        copy.setRecordSource(RECORDSOURCE);
        
        // Add a File
        ImageFile imageFile = copy.addImageFile();
        imageFile.setDevice(DEVICE);        
        
        // Double Check that the values have been set
        Assert.assertEquals(RECORDSOURCE, copy.getRecordSource());
        Assert.assertEquals(DEVICE, imageFile.getDevice());
        
        Long objectId = work.getId();
        
        amberSession.commit();
        
        work = null;
        copy = null;
        imageFile = null;
        
        // Retrieve Work / Copy / ImageFile 
        // and make changes to ImageFile
        //amber = amberSessionService.get();
        Work workB = amberSession.findWork(objectId);
        Assert.assertNotNull(workB);
        Copy copyB = workB.getCopy(CopyRole.ACCESS_COPY);        
        Assert.assertNotNull(copyB);
        ImageFile imageFileB = copyB.getImageFile();
        Assert.assertNotNull(imageFileB);
        
        // Before Updating the values check that they haven't changed
        Assert.assertEquals(RECORDSOURCE, copyB.getRecordSource());
        Assert.assertEquals(DEVICE, imageFileB.getDevice());
        
       
        // Make a change to Copy and ImageFile
        copyB.setRecordSource(RECORDSOURCE_UPDATED);
        imageFileB.setDevice(DEVICE_UPDATED);
        
        // Check that the updated values have been set 
        Assert.assertEquals(RECORDSOURCE_UPDATED, copyB.getRecordSource());
        Assert.assertEquals(DEVICE_UPDATED, imageFileB.getDevice());
        
        amberSession.commit();
        
        workB = null;
        copyB = null;
        imageFileB = null;
        
        // Retrieve Work / Copy / ImageFile                 
        Work workC = amberSession.findWork(objectId);
        Assert.assertNotNull(workC);
        Copy copyC = workC.getCopy(CopyRole.ACCESS_COPY);        
        Assert.assertNotNull(copyC);
        ImageFile imageFileC = copyC.getImageFile();
        Assert.assertNotNull(imageFileC);
        
        // Check for changes against the updated values
        Assert.assertEquals("Expecting the Record Source on Copy to be updated", RECORDSOURCE_UPDATED, copyC.getRecordSource());
        Assert.assertEquals("Expecting the Device on ImageFile to be updated", DEVICE_UPDATED, imageFileC.getDevice());
    }

    @Test
    public void copyHasMultipleAccessCopies() {
        Work work = amberSession.addWork();
        Copy mCopy = work.addCopy();
        mCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy d1Copy = work.addCopy();
        Copy d2Copy = work.addCopy();

        d1Copy.setSourceCopy(mCopy);
        d2Copy.setSourceCopy(mCopy);

        Copy sourceCopy = d1Copy.getSourceCopy();
        Assert.assertThat(d1Copy.getSourceCopy().getCopyRole().equals(mCopy.getCopyRole()), Is.is(true));
        Assert.assertThat(Iterables.size(mCopy.getDerivatives()), Is.is(2));
    }

    @Test
    public void copyCanRemoveSourceCopyRelationship() {
        Work work = amberSession.addWork();
        Copy mCopy = work.addCopy();
        mCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Long mCopyId = mCopy.getId();
        Copy d1Copy = work.addCopy();

        d1Copy.setSourceCopy(mCopy);
        Assert.assertThat(mCopy.equals(d1Copy.getSourceCopy()), Is.is(true));

        d1Copy.removeSourceCopy(mCopy);
        Assert.assertThat(!mCopy.equals(d1Copy.getSourceCopy()), Is.is(true));
        Assert.assertThat(d1Copy.getSourceCopy() == null, Is.is(true));
        Assert.assertThat(mCopy.equals(amberSession.findModelObjectById(mCopyId, Copy.class)), Is.is(true));
    }

    @Test
    public void copyHasSingleComaster() {
        Work work = amberSession.addWork();
        Copy mCopy = work.addCopy();
        mCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy d1Copy = work.addCopy();
        d1Copy.setNotes("d1");
        Copy d2Copy = work.addCopy();
        d2Copy.setNotes("d2");

        mCopy.setComasterCopy(d1Copy);
        mCopy.setComasterCopy(d2Copy);
        Assert.assertThat(mCopy.getComasterCopy().getNotes(), Is.is("d2"));
    }
    
    @Test
    public void testGetSoundFile() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        copy.setCopyRole(CopyRole.LISTENING_1_COPY.code());
        File f = copy.addFile();
        f.setMimeType("audio");

        File f2 = copy.getSoundFile();
        
        Assert.assertEquals(f, f2);
    }
    
    @Test
    public void testGetOrderedCopies() {
        Long[] cpIds = new Long[11];
        Work work = amberSession.addWork();
        Copy o = work.addCopy();
        cpIds[0] = o.getId();
        o.setCopyRole(CopyRole.ORIGINAL_COPY.code());
        Copy m = work.addCopy();
        cpIds[1] = m.getId();
        m.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy d = work.addCopy();
        cpIds[2] = d.getId();
        d.setCopyRole(CopyRole.DIGITAL_DISTRIBUTION_COPY.code());
        Copy rm = work.addCopy();
        cpIds[3] = rm.getId();
        rm.setCopyRole(CopyRole.RELATED_METADATA_COPY.code());
        Copy s = work.addCopy();
        cpIds[4] = s.getId();
        rm.setCopyRole(CopyRole.SUMMARY_COPY.code());
        Copy tr = work.addCopy();
        cpIds[5] = tr.getId();
        tr.setCopyRole(CopyRole.TRANSCRIPT_COPY.code());
        Copy l1 = work.addCopy();
        cpIds[6] = l1.getId();
        l1.setCopyRole(CopyRole.LISTENING_1_COPY.code());
        Copy l2 = work.addCopy();
        cpIds[7] = l2.getId();
        l2.setCopyRole(CopyRole.LISTENING_2_COPY.code());
        Copy l3 = work.addCopy();
        cpIds[8] = l3.getId();
        l3.setCopyRole(CopyRole.LISTENING_3_COPY.code());
        Copy w = work.addCopy();
        cpIds[9] = w.getId();
        w.setCopyRole(CopyRole.WORKING_COPY.code());
        Copy ad = work.addCopy();
        cpIds[10] = ad.getId();
        ad.setCopyRole(CopyRole.ANALOGUE_DISTRIBUTION_COPY.code());
        
        Iterable<Copy> copies = work.getOrderedCopies();
        int i = 0;
        for (Copy copy : copies) {
            Assert.assertTrue(cpIds[i] == copy.getId());
            i++;
        }
    }
}

