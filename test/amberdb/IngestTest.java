package amberdb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.enums.CopyRole;
import amberdb.model.Page;
import amberdb.model.Work;

public class IngestTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private static AmberDb db;
    private static JobMockup job;
    private static String samplePI;

    @BeforeClass
    public static void setup() throws IOException {
        job = uploadFiles();
        db = identifyWorks();
    }

    @AfterClass
    public static void teardown() throws IOException {
        job = null;
        if (db != null) {
            db.close();
        }
        db = null;
    }
    
    private static Path newDummyFile(String name) throws IOException {
        Path path = folder.newFile(name).toPath();
        Files.write(path, "Hello world\n".getBytes());
        return path;
    }

    private static JobMockup uploadFiles() throws IOException {
        job = new JobMockup();

        // Note: banjo upload files step does not interact with amberDb at all.
        List<Path> list = new ArrayList<Path>();
        list.add(newDummyFile("a.tiff"));
        list.add(newDummyFile("b.tiff"));

        list.add(newDummyFile("nla.aus-vn12345-1.tiff"));
        list.add(newDummyFile("nla.aus-vn12345-2.tiff"));
        list.add(newDummyFile("nla.aus-vn12345.xml"));

        list.add(newDummyFile("nla.aus-vn643643-1.tiff"));
        list.add(newDummyFile("nla.aus-vn643643-2.tiff"));

        job.files = list;
        return job;
    }

    private static AmberDb identifyWorks() throws IOException {
        AmberDb amberDb = new AmberDb();

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
        auto1.addPage(job.files.get(6)).setOrder(1);

        Page page = auto1.addPage();
        page.setOrder(2);
        page.addCopy(job.files.get(2), CopyRole.MASTER_COPY);
        page.addCopy(job.files.get(4), CopyRole.OCR_METS_COPY);

        auto1.addPage(job.files.get(3)).setOrder(3);

        auto1.setTitle("Blinky Bill");
        bookIds.add(auto1.getId());

        Work auto2 = amberDb.addWork();
        auto2.setBibId(55555);
        auto2.addPage(job.files.get(5)).setOrder(1);
        auto2.setTitle("James and the giant peach");

        bookIds.add(auto2.getId());

        // user manually creates a work out of the first two pages

        Work manual = amberDb.addWork();
        manual.addPage(job.files.get(0)).setOrder(1);
        manual.addPage(job.files.get(1)).setOrder(2);
        manual.setTitle("Little red riding hood");

        bookIds.add(manual.getId());
        job.workIds = bookIds;

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(amberDb.suspend());

        return amberDb;
    }

    @Test
    public void testFindWorkByPI() {
        Work sample = db.findWork(PIUtil.parse(samplePI));
        assertEquals("Blinky Bill", sample.getTitle());

        String resultPI = sample.getObjId();
        assertEquals(samplePI, resultPI);
    }

    @Test
    public void testDescribeWorks() {
        // recover existing transaction if any
        if (job.getAmberTxId() != null) {
            db.recover(job.getAmberTxId());
        }

        for (Long workId : job.getWorks()) {
            describeWork(job, db.findWork(workId));
        }

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(db.suspend());
    }

    @Test
    public void testFixLabel() {
        Work work = db.findWorkByVn(12345L);

        try {
            int count = work.countParts();
            if (count > 0) {
                Page page7 = work.getPage(1);
                if (page7 != null)
                    page7.setTitle("III");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.commit();
    }

    @Test
    public void testCompleteJob() {
        // recover existing transaction if any
        db.recover(job.getAmberTxId());
        db.commit();
    }

    @Test
    public void testCancelJob() {
        db.rollback();
    }

    @Test
    public void testSerializeToJson() throws JsonGenerationException, JsonMappingException,
            IOException {
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
                db.serializeToJson()));
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
                work.getPage(1).setOrder(2);
                work.getPage(2).setOrder(1);
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
