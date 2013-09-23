package amberdb;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.Work;

public class IngestTest {
    private static AmberDb dao;
    private static JobMockup job;
    private static String samplePI;
    
    @BeforeClass
    public static void setup() throws IOException {
        job = uploadFiles();
        dao = identifyWorks();
    }
    
    @AfterClass
    public static void teardown() {
        job = null;
        dao = null;
    }
    
    private static JobMockup uploadFiles() {
        job = new JobMockup();
        
        // Note: banjo upload files step does not interact with amberDb at all.
        List<File> list = new ArrayList<File>();
        list.add(new File("/tmp/a.tiff"));
        list.add(new File("/tmp/b.tiff"));

        list.add(new File("/tmp/nla.aus-vn12345-1.tiff"));
        list.add(new File("/tmp/nla.aus-vn12345-2.tiff"));
        list.add(new File("/tmp/nla.aus-vn12345.xml"));

        list.add(new File("/tmp/nla.aus-vn643643-1.tiff"));
        list.add(new File("/tmp/nla.aus-vn643643-2.tiff"));

        job.files = list;
        return job;
    }
    
    private static AmberDb identifyWorks() throws IOException {
        try (AmberDb amberDb = new AmberDb()) {

            // recover existing transaction if any
            // NOTE: this recover and the corresponding suspend
            // could be done in a common pre-action and post-action
            // controller method
            if (job.getAmberTxId() != null) {
                amberDb.recover(job.getAmberTxId());
            }

            List<Long> bookIds = new ArrayList<Long>();
            // try to detect works based on filenames,

            Section auto1 = amberDb.addSection();
            samplePI = auto1.getObjId();
            
            auto1.setBibId(12345L);
            amberDb.addPageTo(auto1, job.files.get(6));

            Page page = amberDb.addPageTo(auto1);

            amberDb.addImageTiffCopyTo(page, job.files.get(2));
            amberDb.addOCRMETSCopyTo(page, job.files.get(4));

            amberDb.addPageTo(auto1, job.files.get(3));

            auto1.setTitle("Blinky Bill");
            bookIds.add(auto1.getId());


            Section auto2 = amberDb.addSection();
            auto2.setBibId(55555);
            amberDb.addPageTo(auto2, job.files.get(5));
            auto2.setTitle("James and the giant peach");

            bookIds.add(auto2.getId());


            // user manually creates a work out of the first two pages

            Section manual = amberDb.addSection();
            amberDb.addPageTo(manual, job.files.get(0));
            amberDb.addPageTo(manual, job.files.get(1));
            manual.setTitle("Little red riding hood");

            bookIds.add(manual.getId());

            job.workIds = bookIds;

            // save this transaction without committing it
            // so we can continue manipulating these objects
            // in another web request
            job.setAmberTxId(amberDb.suspend());
            
            return amberDb;
        }
    }
    
    @Test
    public void testFindWorkByPI() {
        Work sample = dao.findWork(PIUtil.objId(samplePI));
        assertEquals("Blinky Bill", sample.getTitle());
        
        String resultPI = sample.getObjId();
        assertEquals(samplePI, resultPI);
    }
    
    // @Test
    @Ignore
    public void testDescribeWorks() {        
        // recover existing transaction if any
        if (job.getAmberTxId() != null) {
            dao.recover(job.getAmberTxId());
        }

        for (Long workId: job.getWorks()) {
            describeWork(job, dao.findSection(workId));
        }

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(dao.suspend());
    }
    
    @Test
    // @Ignore
    public void testFixLabel() {
        Section section = dao.findSectionByVn(12345L);
        
        try {
        // TODO: need to check why section.getAddedPage(1) return null?    
        Page page7 = section.getAddedPage(2);
        page7.setTitle("III");
        } catch(Exception e) {
            e.printStackTrace();
        }

        dao.commit();
    }
    
    @Test
    public void testCompleteJob() {
        // recover existing transaction if any
        dao.recover(job.getAmberTxId());
        dao.commit();
    }
    
    @Test
    public void testCancelJob() {
        dao.rollback();
    }
    
    @Test
    public void testSerializeToJson() throws JsonGenerationException, JsonMappingException, IOException {
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dao.serializeToJson()));
    }
   
    private void describeWork(JobMockup job, Section section) {
        // fill in default values
        for (Page p: section.getAddedPages()) {
            p.setDevice(job.getDefaultDevice());
            p.setSoftware(job.getDefaultSoftware());
        }

        // show the qa form
        try {
        if (section.countParts() > 1) {
            section.swapPages(1, 2);
        } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        Page page = section.getAddedPage(1);
        page.rotate(10);
        page.crop(100,100,200,200);
        page.setTitle("IV");
    }
}
