package amberdb;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import amberdb.enums.CopyRole;
import amberdb.model.Page;
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

            Work auto1 = amberDb.addWork();
            samplePI = auto1.getObjId();
            
            auto1.setBibId(12345L);
            auto1.addPage(job.files.get(6)).setOrderInWork(auto1, 1);
            
            Page page = auto1.addPage();
            page.setOrderInWork(auto1, 2);
            page.addCopy(job.files.get(2), CopyRole.MASTER_COPY);
            page.addCopy(job.files.get(4), CopyRole.OCR_METS_COPY);

            auto1.addPage(job.files.get(3)).setOrderInWork(auto1, 3);
            
            auto1.setTitle("Blinky Bill");
            bookIds.add(auto1.getId());


            Work auto2 = amberDb.addWork();
            auto2.setBibId(55555);
            auto2.addPage(job.files.get(5)).setOrderInWork(auto2, 1);
            auto2.setTitle("James and the giant peach");

            bookIds.add(auto2.getId());


            // user manually creates a work out of the first two pages

            Work manual = amberDb.addWork();
            manual.addPage(job.files.get(0)).setOrderInWork(manual, 1);
            manual.addPage(job.files.get(1)).setOrderInWork(manual, 2);
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
    
    @Test
    public void testDescribeWorks() {        
        // recover existing transaction if any
        if (job.getAmberTxId() != null) {
            dao.recover(job.getAmberTxId());
        }

        for (Long workId: job.getWorks()) {
            describeWork(job, dao.findWork(workId));
        }

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(dao.suspend());
    }
    
    @Test
    public void testFixLabel() {
        Work work = dao.findWorkByVn(12345L);
        
        try { 
            int count = work.countParts();
            if (count > 0) {
                Page page7 = work.getPage(1);
                if (page7 != null)
                    page7.setTitle("III");
            }
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
   
    private void describeWork(JobMockup job, Work work) {
        // fill in default values
        for (Page p : work.getPages()) {
            p.setDevice(job.getDefaultDevice());
            p.setSoftware(job.getDefaultSoftware());
        }

        // show the qa form
        try {
            if (work.countParts() > 1) {
                // swap page 1 and 2
                work.getPage(1).setOrderInWork(work, 2);
                work.getPage(2).setOrderInWork(work, 1);
            }
            
            if (work.countParts() > 0) {
                Page page = work.getPage(1);
                page.rotate(10);
                page.crop(100, 100, 200, 200);
                page.setTitle("IV");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
