package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;

import amberdb.AmberSession;
import amberdb.InvalidSubtypeException;
import amberdb.NoSuchObjectException;
import amberdb.enums.BibLevel;
import amberdb.enums.CaptureStatus;
import amberdb.enums.ChecksumType;
import amberdb.enums.Compression;
import amberdb.enums.CopyRole;
import amberdb.enums.CopyrightPolicy;
import amberdb.enums.SubType;

public class WorkTest {

    private Work workCollection;
    private Work bookBlinkyBill;
    private Work chapterBlinkyBill;
    private Page workFrontCover;
    private Page workTitlePage;
    private Map<String, Object> expectedResults = new HashMap<String, Object>();    
    private int expectedNoOfPages = 0;
    private int expectedNoOfPagesForSection = 0;
    
    private Work journalNLAOH;

    private AmberSession db;
    
    @Before
    public void setup() throws IOException, InstantiationException {
        db = new AmberSession();
        setTestDataInH2(db);
    }

    @Test
    public void testLoadPagedWork() {
        try {
            journalNLAOH.loadPagedWork();
        } catch (Exception e) {
             System.out.println(e.getCause().getCause().toString());
        }
        
        List<String> subTypes = new ArrayList<String>();
        subTypes.add("page");
        List<Work> pages = journalNLAOH.getPartsOf(subTypes);
        if (pages != null) {
            for (Work page : pages) {
                System.out.println("page: " + page.getId());
            }
        }
        subTypes.remove("page");
        subTypes.add("article");
        List<Work> articles = journalNLAOH.getPartsOf(subTypes);
        if (articles != null) {
            for (Work article : articles) {
                System.out.println("article: " + article.getId());
            }
        }
    }
    
    @Test
    public void testGetLeafsForBlinkyBill() {
        Iterable<Work> leafs = bookBlinkyBill.getLeafs(SubType.PAGE);
        int noOfPages = 0;
        for (Work leaf : leafs) {
            noOfPages++;
        }
        assertEquals(expectedNoOfPages, noOfPages);
    }
    
    @Test
    public void testGetLeafsOfSubTypesForBlinkyBill() {
        
        Long start = new Date().getTime();
        
        // try loading the book first
        try {
            bookBlinkyBill.loadPagedWork();
        } catch (InvalidSubtypeException e) {
            e.printStackTrace();
        }
        
        List<Work> leaves = bookBlinkyBill.getPartsOf(Arrays.asList(new String[] {SubType.PAGE.code(), SubType.CHAPTER.code()}));
        
        int noOfLeaves = 0;
        for (Work leaf : leaves) {
            noOfLeaves++;
        }

        assertEquals(noOfLeaves, 3);
        
        // Check output bits
        leaves.get(0).getExistsOn(SubType.PAGE.code());
    }
    
    @Test
    public void testGetLeafsForBlinkyBillAssoication() {
        Iterable<Section> sections = bookBlinkyBill.getSections(SubType.CHAPTER);
        Section theNewArrival = null;
        for (Section section : sections) {
            if (section.getTitle().equalsIgnoreCase("The New Arrival")) 
                theNewArrival = section;
        }
        Iterable<Work> leafs = theNewArrival.getLeafs(SubType.PAGE.code());
        int noOfPages = 0;
        assertEquals(expectedNoOfPagesForSection, noOfPages);
    }
    
    @Test
    public void testGetWorkAsSection() {
        Section theChapter = chapterBlinkyBill.asSection();
        assertEquals(chapterBlinkyBill.getId(), theChapter.getId());
        
        // Also verify bibId can be returned as a String
        assertEquals(bookBlinkyBill.getBibId().getClass().getName(), "java.lang.String");
    }
    

    
    @Test
    public void testGetSetParentEdges() throws IOException {          
            Work work = db.addWork();
            Work parentWork = db.addWork();
            parentWork.addChild(work);
            assertTrue(null == parentWork.getParentEdge());
            assertEquals(parentWork, work.getParentEdge().getSource());            
    }
    
    @Test
    public void testDeattachPage() {
        
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
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setSubUnitType("Title Page");
    }
    
    @Test
    public void testDeattachLeaf() {
        
        int expectedNoOfPagesBfrRemoval = 2;
        int expectedNoOfPagesAftRemoval = 1;
        
        int actualNoOfPagesBfrRemoval = 0;
        Iterable<Work> pagesBI = bookBlinkyBill.getLeafs(SubType.PAGE);
        for (Work page : pagesBI) {
            actualNoOfPagesBfrRemoval++;
        }
        assertEquals(expectedNoOfPagesBfrRemoval, actualNoOfPagesBfrRemoval);
        bookBlinkyBill.removePart(bookBlinkyBill.getLeaf(SubType.PAGE, 1));

        int actualNoOfPagesAftRemoval = 0;
        Iterable<Work> pagesAI = bookBlinkyBill.getLeafs(SubType.PAGE);
        for (Work page : pagesAI) {
            actualNoOfPagesAftRemoval++;
        }
        assertEquals(expectedNoOfPagesAftRemoval, actualNoOfPagesAftRemoval);
        workTitlePage = bookBlinkyBill.addPage();
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setSubUnitType("Title Page");
    }
    
    @Test
    public void testCountCopies() {
        
        Work work = bookBlinkyBill;
        Page page = workTitlePage;
        
        /* parent work has no copies */
        assertEquals(work.countCopies(), 0);

        int counter = 0;
        Iterable<Copy> pageCopies = page.getCopies();
        for (Copy copy : pageCopies) {
            counter++;
        }
        /* we count it the same way the Work class does */
        assertEquals(page.countCopies(), counter);

        /* Check page _has_ copies. 
           (otherwise this test is a waste of time that proves nothing */
        assertNotEquals(page.countCopies(), 0);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDeleteWork() {

        Work work = bookBlinkyBill;
        long workVertexId = work.getId();

        db.deleteWork(work);

        /* expects a NoSuchObjectException */
        db.findWork(workVertexId);
    }
    
    @Test
    public void testRepresentWork() {
        Work newWork = db.addWork();
        Copy representativeCopy = workFrontCover.getCopy(CopyRole.MASTER_COPY);
        newWork.addRepresentation(representativeCopy);
        Iterable<Work> representedWorks = representativeCopy.getRepresentedWorks();
        assertNotNull(representedWorks);

        // assert representedWorks has at least one element
        Iterator<Work> representedIt = representedWorks.iterator();
        assertTrue(representedIt.hasNext());
        assertEquals(newWork, representedIt.next());
        
        // assert representedWorks has only one element
        assertFalse(representedIt.hasNext());
    }

    @Test
    public void testEmptyIterableNotNull() {
        
        Work work = db.addWork();
        Copy c = work.addCopy();

        // test if problem with empty iterable
        for (Work w : c.getRepresentedWorks()) {
            // noop
        }
    }
    
    @Test
    public void testGetWorksRepresentedByCopiesOf() {
        Work newWork = db.addWork();
        Copy representativeCopy = workFrontCover.getCopy(CopyRole.MASTER_COPY);
        newWork.addRepresentation(representativeCopy);
        Map<Long, Long> reps = db.getWorksRepresentedByCopiesOf(workFrontCover);
        assertEquals((Long) reps.get(newWork.getId()), (Long) representativeCopy.getId());
    }
    
    @Test(expected = NoSuchObjectException.class)
    public void testDeleteWorkShouldNotDeleteCopyFromOtherWork() {
        // create new work and assign its representative copy
        Work newWork = db.addWork();
        String newWorkId = newWork.getObjId();
        String workFrontCoverId = workFrontCover.getObjId();
        Copy representativeCopy = workFrontCover.getCopy(CopyRole.MASTER_COPY);
        newWork.addRepresentation(representativeCopy);
        
        // delete new work
        db.deleteWork(newWork);
        db.commit();
        
        // verify the representative copy still exist in db
        Work foundWorkFrontCover = db.findWork(workFrontCoverId);
        Copy afterDeleteCopy = foundWorkFrontCover.getCopy(CopyRole.MASTER_COPY);
        assertEquals(representativeCopy, afterDeleteCopy);
        
        // verify the new work is deleted through NoSuchObjectException thrown
        Work afterDeleteNewWork = db.findWork(newWorkId);
    }
    
    @Test(expected = NoSuchObjectException.class)
    public void testDeletePage() {  
       
        Page page = workTitlePage;
        long workVertexId = page.getId();
        assertEquals(workVertexId, page.asVertex().getId());
        List<Long> copyVertexIds = new ArrayList<Long>();
        List<Long> fileVertexIds = new ArrayList<Long>();
        Iterable<Copy> copies = page.getCopies();
        for (Copy copy : copies) {
            File file = copy.getFile();
            copyVertexIds.add(copy.getId());
            
            if (file != null) {
                fileVertexIds.add(file.getId());
                assertEquals(file.getId(), file.asVertex().getId());
            }
            assertEquals(copy.getId(), copy.asVertex().getId());
        }
        
        db.deletePage(page);
        db.findWork(workVertexId);
    }
    

    @Test
    public void testOrderChildren() {
        
        List<Work> pages = new ArrayList<>();
        
        Work book = db.addWork();
        for (int i = 0; i < 20; i++) {
            Page page = book.addPage();
            page.setTitle("page " + i);
            pages.add(page); // pages List will hold the pages in order
        }
        
        // set order
        book.orderParts(pages);
        
        // check ordering worked
        for (int i = 0; i < 20; i++) {
            assertEquals(pages.get(i).getOrder(book, "isPartOf", Direction.OUT), (Integer) (i + 1));
        }
        
        // reverse order
        Collections.reverse(pages);
        book.orderParts(pages);

        // Check reordering worked
        for (int i = 0; i < 20; i++) {
            assertEquals(((Page) pages.get(i)).getTitle(), "page "+(19-i));
        }
    }

    @Test
    public void testAddDeliveryWork() {
        Work work = db.addWork();

        Work deliveryWork = db.addWork();
        work.addDeliveryWork(deliveryWork);

        Collection<Work> list = IteratorUtils.toList(work.getDeliveryWorks().iterator());

        assertFalse(list.isEmpty());
    }

    @Test
    public void testRemoveDeliveryWork() {
        Work work = db.addWork();

        Work deliveryWork = db.addWork();
        work.addDeliveryWork(deliveryWork);

        Collection<Work> list = IteratorUtils.toList(work.getDeliveryWorks().iterator());
        assertFalse(list.isEmpty());

        work.removeDeliveryWork(deliveryWork);

        list = IteratorUtils.toList(work.getDeliveryWorks().iterator());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testRetrieveDeliveryWorkInterview() {
        Work work = db.addWork();

        Work deliveryWork = db.addWork();
        deliveryWork.setDeliveryWorkParent(work);
        work.addDeliveryWork(deliveryWork);

        assertTrue(deliveryWork.getDeliveryWorkParent() != null);
        assertTrue(deliveryWork.getDeliveryWorkParent().equals(work));
    }
    
    private void setTestDataInH2(AmberSession sess) {
        workCollection = sess.addWork();
        
        workCollection.setSubType("title");
        workCollection.setTitle("nla.books");
        
        journalNLAOH = sess.addWork();
        bookBlinkyBill = sess.addWork();
        bookBlinkyBill.setSubType(SubType.BOOK.code());
        bookBlinkyBill.setTitle("Blinky Bill");
        bookBlinkyBill.setBibId("73");
        bookBlinkyBill.setBibLevel(BibLevel.ITEM.code());
        bookBlinkyBill.setCopyrightPolicy(CopyrightPolicy.OUTOFCOPYRIGHT.code());
        bookBlinkyBill.setDigitalStatus(CaptureStatus.CAPTURED.code());
        workCollection.addChild(bookBlinkyBill);
        
        chapterBlinkyBill = sess.addWork();
        chapterBlinkyBill.setSubType(SubType.CHAPTER.code());
        chapterBlinkyBill.setTitle("The New Arrival");
        bookBlinkyBill.addChild(chapterBlinkyBill);
        
        workFrontCover = bookBlinkyBill.addPage();
        workTitlePage = bookBlinkyBill.addPage();
        workFrontCover.setSubType(SubType.PAGE.code());
        workFrontCover.setTitle("Blinky Bill Page 1");
        workFrontCover.setSubUnitType("Front Cover");
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType(SubType.PAGE.code());
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
        workFrontCoverMasterCopyFile.setChecksumType(ChecksumType.SHA_1.code());
        workFrontCoverMasterCopyFile.setCompression(Compression.NONE.code());
        
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
    
    @Test
    public void testToEnsureDCMLegacyDataFieldsExist() throws IOException {
        Work work = db.addWork();
        
        Date date = new Date();
        
        work.setDcmDateTimeCreated(date);
        assertEquals(date, work.getDcmDateTimeCreated());
        
        work.setDcmWorkPid("12345");
        assertEquals("12345", work.getDcmWorkPid());
        
        work.setDcmDateTimeUpdated(date);
        assertEquals(date, work.getDcmDateTimeUpdated());
        
        work.setDcmRecordCreator("creator");
        assertEquals("creator", work.getDcmRecordCreator());
        
        work.setDcmRecordUpdater("updater");
        assertEquals("updater", work.getDcmRecordUpdater()); 
        
        List<String> list = new ArrayList<String>();
        list.add("pi-1");
        list.add("pi-2");
        work.setDcmAltPi(list);
        
        assertEquals(2, work.getDcmAltPi().size());
        assertEquals(list, work.getDcmAltPi());
    }
    
    @Test
    public void testGetSetConstraint() throws IOException {
        Work work = db.addWork();
        Set<String> constraints = new LinkedHashSet<>();
        assertEquals(0, work.getConstraint().size());
        assertFalse(constraints.contains("testingc"));
        constraints.add("testing");
        constraints.add("testinga");
        constraints.add("testingb");
        constraints.add("testingc");
        constraints.add("testingd");
        work.setConstraint(constraints);
        constraints = work.getConstraint();
        assertEquals(5,constraints.size());
        assertTrue(constraints.contains("testingc"));
        constraints.add("octopus");
        work.setConstraint(constraints);
        constraints = work.getConstraint();
        assertEquals(6, constraints.size());
        assertTrue(constraints.contains("octopus"));
    }
    
    @After
    public void teardown() throws IOException {
        if (db != null)
            db.close();
    }
}
