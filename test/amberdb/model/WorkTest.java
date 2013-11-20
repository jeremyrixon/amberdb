package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import amberdb.AmberDbFactory;
import amberdb.AmberSession;
import amberdb.NoSuchObjectException;
import amberdb.enums.CopyRole;

public class WorkTest {

    private static Work workCollection;
    private static Work bookBlinkyBill;
    private static Work chapterBlinkyBill;
    private static Page workFrontCover;
    private static Page workTitlePage;
    private static Iterable<Copy> copies;
    private static Map<String, Object> expectedResults = new HashMap<String, Object>();    
    private static int expectedNoOfPages = 0;
    private static int expectedNoOfPagesForSection = 0;
    
    @BeforeClass
    public static void setup() throws IOException, InstantiationException {
        resetTestData();
    }

    
    // @Test
    @Ignore
    public void testGetLeafsForBlinkyBill() {
        resetTestData();
        Iterable<Work> leafs = bookBlinkyBill.getLeafs("page");
        System.out.println("List leafs for book blinky bill: ");
        int noOfPages = 0;
        for (Work leaf : leafs) {
            System.out.println("Leaf: " + leaf.getSubUnitType());
            noOfPages++;
        }
        assertEquals(expectedNoOfPages, noOfPages);
        resetTestData();
    }
    
    @Test
    public void testGetLeafsOfSubTypesForBlinkyBill() {
        resetTestData();
        List<String> subTypes = new ArrayList<String>();
        subTypes.add("page");
        subTypes.add("chapter");
        Iterable<Work> leafs = bookBlinkyBill.getLeafs(subTypes);
        System.out.println("List leafs for book blinky bill of type page and chapter: ");
        int noOfPages = 0;
        for (Work leaf : leafs) {
            System.out.println("Leaf: " + leaf.getSubType() + ", " + leaf.getSubUnitType() + ", " + leaf.getTitle());
            noOfPages++;
        }
        resetTestData();
    }
    
    // @Test
    @Ignore
    public void testGetLeafsForBlinkyBillAssoication() {
        resetTestData();
        Iterable<Section> sections = bookBlinkyBill.getSections("chapter");
        Section theNewArrival = null;
        for (Section section : sections) {
            if (section.getTitle().equalsIgnoreCase("The New Arrival")) 
                theNewArrival = section;
        }
        Iterable<Work> leafs = theNewArrival.getLeafs("page");
        System.out.println("List leafs for chapter the new arrival: ");
        int noOfPages = 0;
        for (Work leaf : leafs) {
            System.out.println("Leaf: " + leaf.getSubType());
            noOfPages++;
        }
        assertEquals(expectedNoOfPagesForSection, noOfPages);
        resetTestData();
    }
    
    @Test
    public void testGetWorkAsSection() {
        resetTestData();
        Section theChapter = chapterBlinkyBill.asSection();
        assertEquals(chapterBlinkyBill.getId(), theChapter.getId());
        
        // Also verify bibId can be returned as a String
        System.out.println("voyager id for blinky bill: " + bookBlinkyBill.getBibId().toString());
        // assertEquals(bookBlinkyBill.getBibId().getClass().getName(), "java.lang.String");
        resetTestData();
    }
    
    @Test
    public void testDeattachPage() {
        AmberSession db = new AmberSession();
        setTestDataInH2(db);
        
        int expectedNoOfPagesBfrRemoval = 3;
        int expectedNoOfPagesAftRemoval = 2;
        
        int actualNoOfPagesBfrRemoval = 0;
        Iterable<Page> pagesBI = bookBlinkyBill.getPages();
        for (Page page : pagesBI) {
            actualNoOfPagesBfrRemoval++;
        }
        assertEquals(expectedNoOfPagesBfrRemoval, actualNoOfPagesBfrRemoval);
        bookBlinkyBill.removePage(workTitlePage);

        int actualNoOfPagesAftRemoval = 0;
        Iterable<Page> pagesAI = bookBlinkyBill.getPages();
        for (Page page : pagesAI) {
            actualNoOfPagesAftRemoval++;
        }
        assertEquals(expectedNoOfPagesAftRemoval, actualNoOfPagesAftRemoval);
        workTitlePage = bookBlinkyBill.addPage();
        workTitlePage.setSubType("page");
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType("page");
        workTitlePage.setSubUnitType("Title Page");
        
        resetTestData();
    }
    
    @Test
    public void testDeattachLeaf() {
        AmberSession db = new AmberSession();
        setTestDataInH2(db);
        
        int expectedNoOfPagesBfrRemoval = 2;
        int expectedNoOfPagesAftRemoval = 1;
        
        int actualNoOfPagesBfrRemoval = 0;
        Iterable<Work> pagesBI = bookBlinkyBill.getLeafs("page");
        for (Work page : pagesBI) {
            actualNoOfPagesBfrRemoval++;
        }
        assertEquals(expectedNoOfPagesBfrRemoval, actualNoOfPagesBfrRemoval);
        bookBlinkyBill.removePart(bookBlinkyBill.getLeaf("page", 1));

        int actualNoOfPagesAftRemoval = 0;
        Iterable<Work> pagesAI = bookBlinkyBill.getLeafs("page");
        for (Work page : pagesAI) {
            actualNoOfPagesAftRemoval++;
        }
        assertEquals(expectedNoOfPagesAftRemoval, actualNoOfPagesAftRemoval);
        workTitlePage = bookBlinkyBill.addPage();
        workTitlePage.setSubType("page");
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType("page");
        workTitlePage.setSubUnitType("Title Page");
        
        resetTestData();
    }
    
    @Test(expected = NoSuchObjectException.class)
    public void testDeleteWork() {
        AmberSession db = new AmberSession();
        setTestDataInH2(db);
        
        Page work = workTitlePage;
        long workVertexId = work.getId();
        assertEquals(workVertexId, work.asVertex().getId());
        List<Long> copyVertexIds = new ArrayList<Long>();
        List<Long> fileVertexIds = new ArrayList<Long>();
        Iterable<Copy> copies = work.getCopies();
        for (Copy copy : copies) {
            File file = copy.getFile();
            copyVertexIds.add(copy.getId());
            
            if (file != null) {
                fileVertexIds.add(file.getId());
                assertEquals(file.getId(), file.asVertex().getId());
            }
            assertEquals(copy.getId(), copy.asVertex().getId());
        }
        
        db.deletePage(work);
        db.findWork(workVertexId);

        resetTestData();
    }
    
    private static void resetTestData() {
        String dbUrl = "jdbc:mysql://snowy.nla.gov.au:3306/dlir?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8";
        String dbUser = "dlir";
        String dbPassword = "dlir";   
        String rootPath = ".";
        
        try (AmberSession db = AmberDbFactory.openAmberDb(dbUrl, dbUser, dbPassword, rootPath) ) {
            bookBlinkyBill = db.findWork(179722129L);
            chapterBlinkyBill = db.findWork(179763338);
            expectedNoOfPages = 95;
            expectedNoOfPagesForSection = 13;
        } catch (Exception e) {
            e.printStackTrace();
            AmberSession db = new AmberSession();
            setTestDataInH2(db);
        }        
    }
    
    private static void setTestDataInH2(AmberSession sess) {
        workCollection = sess.addWork();
        
        workCollection.setSubType("title");
        workCollection.setTitle("nla.books");
        
        bookBlinkyBill = sess.addWork();
        bookBlinkyBill.setSubType("book");
        bookBlinkyBill.setTitle("Blinky Bill");
        bookBlinkyBill.setBibId("73");
        workCollection.addChild(bookBlinkyBill);
        
        chapterBlinkyBill = sess.addWork();
        chapterBlinkyBill.setSubType("chapter");
        chapterBlinkyBill.setTitle("The New Arrival");
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
        
        expectedNoOfPages = 2;
        expectedNoOfPagesForSection = 0;
    }
}
