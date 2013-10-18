package amberdb;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Work;

public class JellyTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private static Work workCollection;
    private static Work bookBlinkyBill;
    private static Work chapterBlinkyBill;
    private static Work workFrontCover;
    private static Work workTitlePage;
    private static Iterable<Copy> copies;
    private static Map<String, Object> expectedResults = new HashMap<String, Object>();

    @BeforeClass
    public static void setup() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {
        String dbUrl = "jdbc:mysql://amberserver:3306/dlir?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8";
        String dbUser = "dlir";
        String dbPassword = "dlir";
        String rootPath = ".";
        
        try (AmberSession db = AmberDbFactory.openAmberDb(dbUrl, dbUser, dbPassword, rootPath)) {
            if (AmberDbFactory.h2Test)
                setTestDataInH2(db);
                
            if (workCollection == null) workCollection = db.findWork(179720227L);
            if (bookBlinkyBill == null) bookBlinkyBill = db.findWork(179722129L);
            // chapterBlinkyBill = db.findWork(179);
            if (workFrontCover == null) workFrontCover = db.findWork(179722445L);
            copies = workFrontCover.getCopies();

            if (workTitlePage == null) workTitlePage = db.findWork(179722746L);
            
            if (!AmberDbFactory.h2Test) {
                expectedResults.put("workFrontCover_getSubType", "page");
                expectedResults.put("workFrontCover_getSubUnitType", "Front Cover");
                expectedResults.put("copy_carrier", "Online");
                expectedResults.put("workFrontCover_OCR_JSON_COPY_ID", 179722446L);
                expectedResults.put("workFrontCover_MASTER_COPY_ID", 179722448L);
                expectedResults.put("workFrontCover_ACCESS_COPY_ID", 179722450L);
                expectedResults.put("workFrontCover_OCR_JSON_COPY_FILE_ID", 179722447L);
                expectedResults.put("workFrontCover_MASTER_COPY_FILE_ID", 179722449L);
                
                expectedResults.put("workTitlePage_getSubType", "page");
                expectedResults.put("workTitlePage_getSubUnitType", "Title Page");
                expectedResults.put("workTitlePage_ACCESS_COPY_ID", 179722751L);
                
                expectedResults.put("workTitlePage_OCR_JSON_COPY_ID", 179722747L);
                expectedResults.put("workTitlePage_MASTER_COPY_ID", 179722749L);
            }
        }
    }

    @Test
    public void testWorkStructure() {
        // Test retrieving pages
        List pages = Lists.newArrayList(bookBlinkyBill.getPages());
        assertEquals(3, pages.size());
        
        // Test retrieving copies
        List copies = Lists.newArrayList(workFrontCover.getCopies());
        assertEquals(3, copies.size());
        
        // Test retrieving files
        List files = Lists.newArrayList(workFrontCover.getCopy(CopyRole.ACCESS_COPY).getFiles());
        assertEquals(1, files.size());
    }

    @Test
    public void testWorkProperties() {
        assertEquals("page", workFrontCover.getSubType());
        assertEquals("Front Cover", workFrontCover.getSubUnitType());

        for (Copy copy : copies) {
            System.out.println("copy id : " + copy.getId());
            assertEquals("Online", copy.getCarrier());
            System.out.println("copy role : " + copy.getCopyRole());
        }

        assertEquals(expectedResults.get("workFrontCover_OCR_JSON_COPY_ID"), workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getId());
        assertEquals(expectedResults.get("workFrontCover_MASTER_COPY_ID"), workFrontCover.getCopy(CopyRole.MASTER_COPY).getId());
        assertEquals(expectedResults.get("workFrontCover_ACCESS_COPY_ID"), workFrontCover.getCopy(CopyRole.ACCESS_COPY).getId());

        assertEquals(expectedResults.get("workFrontCover_OCR_JSON_COPY_FILE_ID"), workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getFile().getId());
        assertEquals(expectedResults.get("workFrontCover_MASTER_COPY_FILE_ID"), workFrontCover.getCopy(CopyRole.MASTER_COPY).getFile().getId());

        assertEquals(expectedResults.get("workTitlePage_getSubType"), workTitlePage.getSubType());
        assertEquals(expectedResults.get("workTitlePage_getSubUnitType"), workTitlePage.getSubUnitType());
        Copy titlePageAc = workTitlePage.getCopy(CopyRole.ACCESS_COPY);
        System.out.println("title page copy ac id: " + titlePageAc.getId());
        assertNotNull(titlePageAc);
        assertEquals(expectedResults.get("workTitlePage_ACCESS_COPY_ID"), titlePageAc.getId());

        Iterable<Copy> titlePageCopies = workTitlePage.getCopies();
        for (Copy _copy : titlePageCopies) {
            assertEquals("Online", _copy.getCarrier());
        }

        assertEquals(expectedResults.get("workTitlePage_OCR_JSON_COPY_ID"), workTitlePage.getCopy(CopyRole.OCR_JSON_COPY).getId());
        assertEquals(expectedResults.get("workTitlePage_MASTER_COPY_ID"), workTitlePage.getCopy(CopyRole.MASTER_COPY).getId());
        assertEquals(expectedResults.get("workTitlePage_ACCESS_COPY_ID"), workTitlePage.getCopy(CopyRole.ACCESS_COPY).getId());
    }

    @AfterClass
    public static void teardown() throws IOException {
        workFrontCover = null;
    }

    private static void setTestDataInH2(AmberSession sess) {
        workCollection = sess.addWork();
        
        workCollection.setSubType("title");
        workCollection.setTitle("nla.books");
        
        bookBlinkyBill = sess.addWork();
        bookBlinkyBill.setSubType("book");
        bookBlinkyBill.setTitle("Blinky Bill");
        workCollection.addChild(bookBlinkyBill);
        
        chapterBlinkyBill = sess.addWork();
        chapterBlinkyBill.setSubType("chapter");
        chapterBlinkyBill.setTitle("Blinky Bill chapter 1");
        bookBlinkyBill.addChild(chapterBlinkyBill);
        
        workFrontCover = bookBlinkyBill.addPage();
        workTitlePage = bookBlinkyBill.addPage();
        workFrontCover.setSubType("page");
        workFrontCover.setTitle("Blinky Bill Page 1");
        workFrontCover.setSubUnitType("Front Cover");
        workTitlePage.setSubType("page");
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType("page");
        workTitlePage.setSubUnitType("Title Page");
        
        Copy workFrontCoverMasterCopy = workFrontCover.addCopy();
        Copy workFrontCoverOCRJsonCopy = workFrontCover.addCopy();
        Copy workFrontCoverAccessCopy = workFrontCover.addCopy();
        workFrontCoverMasterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        workFrontCoverMasterCopy.setCarrier("Online");
        workFrontCoverOCRJsonCopy.setCopyRole(CopyRole.OCR_JSON_COPY.code());
        workFrontCoverOCRJsonCopy.setCarrier("Online");
        workFrontCoverAccessCopy.setCopyRole(CopyRole.ACCESS_COPY.code());
        workFrontCoverAccessCopy.setCarrier("Online");
        
        Copy workTitlePageMasterCopy = workTitlePage.addCopy();
        Copy workTitlePageOCRJsonCopy = workTitlePage.addCopy();
        Copy workTitlePageAccessCopy = workTitlePage.addCopy();
        workTitlePageMasterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        workTitlePageMasterCopy.setCarrier("Online");
        workTitlePageOCRJsonCopy.setCopyRole(CopyRole.OCR_JSON_COPY.code());
        workTitlePageOCRJsonCopy.setCarrier("Online");
        workTitlePageAccessCopy.setCopyRole(CopyRole.ACCESS_COPY.code());
        workTitlePageAccessCopy.setCarrier("Online");
        
        File workFrontCoverMasterCopyFile = workFrontCoverMasterCopy.addFile();
        File workFrontCoverOCRJsonCopyFile = workFrontCoverOCRJsonCopy.addFile();
        File workFrontCoverAccessCopyFile = workFrontCoverAccessCopy.addFile();
        
        expectedResults.put("workFrontCover_getSubType", workFrontCover.getSubType());
        expectedResults.put("workFrontCover_getSubUnitType", workFrontCover.getSubUnitType());
        expectedResults.put("copy_carrier", workFrontCoverAccessCopy.getCarrier());
        expectedResults.put("workFrontCover_OCR_JSON_COPY_ID", workFrontCoverOCRJsonCopy.getId());
        expectedResults.put("workFrontCover_MASTER_COPY_ID", workFrontCoverMasterCopy.getId());
        expectedResults.put("workFrontCover_ACCESS_COPY_ID", workFrontCoverAccessCopy.getId());
        expectedResults.put("workFrontCover_OCR_JSON_COPY_FILE_ID", workFrontCoverOCRJsonCopyFile.getId());
        expectedResults.put("workFrontCover_MASTER_COPY_FILE_ID", workFrontCoverMasterCopyFile.getId());
        expectedResults.put("workFrontCover_ACCESS_COPY_FILE_ID", workFrontCoverAccessCopyFile.getId());
        
        expectedResults.put("workTitlePage_getSubType", workTitlePage.getSubType());
        expectedResults.put("workTitlePage_getSubUnitType", workTitlePage.getSubUnitType());
        expectedResults.put("workTitlePage_ACCESS_COPY_ID", workTitlePageAccessCopy.getId());
        
        expectedResults.put("workTitlePage_OCR_JSON_COPY_ID", workTitlePageOCRJsonCopy.getId());
        expectedResults.put("workTitlePage_MASTER_COPY_ID", workTitlePageMasterCopy.getId());
    }
}
