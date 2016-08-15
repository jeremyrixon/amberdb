package amberdb.model;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;
import amberdb.InvalidSubtypeException;
import amberdb.NoSuchObjectException;
import amberdb.enums.*;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import org.apache.commons.collections.IteratorUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class WorkTest extends AbstractDatabaseIntegrationTest {

    private Work workCollection;
    private Work bookBlinkyBill;
    private Work chapterBlinkyBill;
    private Page workFrontCover;
    private Page workTitlePage;
    private Map<String, Object> expectedResults = new HashMap<String, Object>();    
    private int expectedNoOfPages = 0;
    private int expectedNoOfPagesForSection = 0;
    
    private Work journalNLAOH;

    @Before
    public void setup() throws IOException, InstantiationException {
        setTestDataInH2(amberSession);
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
        Assert.assertEquals(expectedNoOfPages, noOfPages);
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

        Assert.assertEquals(noOfLeaves, 3);
        
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
        Assert.assertEquals(expectedNoOfPagesForSection, noOfPages);
    }
    
    @Test
    public void testGetWorkAsSection() {
        Section theChapter = chapterBlinkyBill.asSection();
        Assert.assertEquals(chapterBlinkyBill.getId(), theChapter.getId());
        
        // Also verify bibId can be returned as a String
        Assert.assertEquals(bookBlinkyBill.getBibId().getClass().getName(), "java.lang.String");
    }
    

    
    @Test
    public void testGetSetParentEdges() throws IOException {          
            Work work = amberSession.addWork();
            Work parentWork = amberSession.addWork();
            parentWork.addChild(work);
            Assert.assertTrue(null == parentWork.getParentEdge());
            Assert.assertEquals(parentWork, work.getParentEdge().getSource());
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
        Assert.assertEquals(expectedNoOfPagesBfrRemoval, actualNoOfPagesBfrRemoval);
        bookBlinkyBill.removePage(workTitlePage);

        int actualNoOfPagesAftRemoval = 0;
        Iterable<Page> pagesAI = bookBlinkyBill.getPages();
        for (Page page : pagesAI) {
            actualNoOfPagesAftRemoval++;
        }
        Assert.assertEquals(expectedNoOfPagesAftRemoval, actualNoOfPagesAftRemoval);
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
        Assert.assertEquals(expectedNoOfPagesBfrRemoval, actualNoOfPagesBfrRemoval);
        bookBlinkyBill.removePart(bookBlinkyBill.getLeaf(SubType.PAGE, 1));

        int actualNoOfPagesAftRemoval = 0;
        Iterable<Work> pagesAI = bookBlinkyBill.getLeafs(SubType.PAGE);
        for (Work page : pagesAI) {
            actualNoOfPagesAftRemoval++;
        }
        Assert.assertEquals(expectedNoOfPagesAftRemoval, actualNoOfPagesAftRemoval);
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
        Assert.assertEquals(work.countCopies(), 0);

        int counter = 0;
        Iterable<Copy> pageCopies = page.getCopies();
        for (Copy copy : pageCopies) {
            counter++;
        }
        /* we count it the same way the Work class does */
        Assert.assertEquals(page.countCopies(), counter);

        /* Check page _has_ copies. 
           (otherwise this test is a waste of time that proves nothing */
        Assert.assertNotEquals(page.countCopies(), 0);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDeleteWork() {

        Work work = bookBlinkyBill;
        long workVertexId = work.getId();

        amberSession.deleteWork(work);

        /* expects a NoSuchObjectException */
        amberSession.findWork(workVertexId);
    }
    
    @Test
    public void testRepresentWork() {
        Work newWork = amberSession.addWork();
        Copy representativeCopy = workFrontCover.getCopy(CopyRole.MASTER_COPY);
        newWork.addRepresentation(representativeCopy);
        Iterable<Work> representedWorks = representativeCopy.getRepresentedWorks();
        Assert.assertNotNull(representedWorks);

        // assert representedWorks has at least one element
        Iterator<Work> representedIt = representedWorks.iterator();
        Assert.assertTrue(representedIt.hasNext());
        Assert.assertEquals(newWork, representedIt.next());
        
        // assert representedWorks has only one element
        Assert.assertFalse(representedIt.hasNext());
    }

    @Test
    public void testEmptyIterableNotNull() {
        
        Work work = amberSession.addWork();
        Copy c = work.addCopy();

        // test if problem with empty iterable
        for (Work w : c.getRepresentedWorks()) {
            // noop
        }
    }
    
    @Test
    public void testGetWorksRepresentedByCopiesOf() {
        Work newWork = amberSession.addWork();
        Copy representativeCopy = workFrontCover.getCopy(CopyRole.MASTER_COPY);
        newWork.addRepresentation(representativeCopy);
        Map<Long, Long> reps = amberSession.getWorksRepresentedByCopiesOf(workFrontCover);
        Assert.assertEquals((Long) reps.get(newWork.getId()), (Long) representativeCopy.getId());
    }
    
    @Test(expected = NoSuchObjectException.class)
    public void testDeleteWorkShouldNotDeleteCopyFromOtherWork() {
        // create new work and assign its representative copy
        Work newWork = amberSession.addWork();
        String newWorkId = newWork.getObjId();
        String workFrontCoverId = workFrontCover.getObjId();
        Copy representativeCopy = workFrontCover.getCopy(CopyRole.MASTER_COPY);
        newWork.addRepresentation(representativeCopy);
        
        // delete new work
        amberSession.deleteWork(newWork);
        amberSession.commit();
        
        // verify the representative copy still exist in amberSession
        Work foundWorkFrontCover = amberSession.findWork(workFrontCoverId);
        Copy afterDeleteCopy = foundWorkFrontCover.getCopy(CopyRole.MASTER_COPY);
        Assert.assertEquals(representativeCopy, afterDeleteCopy);
        
        // verify the new work is deleted through NoSuchObjectException thrown
        Work afterDeleteNewWork = amberSession.findWork(newWorkId);
    }
    
    @Test(expected = NoSuchObjectException.class)
    public void testDeletePage() {  
       
        Page page = workTitlePage;
        long workVertexId = page.getId();
        Assert.assertEquals(workVertexId, page.asVertex().getId());
        List<Long> copyVertexIds = new ArrayList<Long>();
        List<Long> fileVertexIds = new ArrayList<Long>();
        Iterable<Copy> copies = page.getCopies();
        for (Copy copy : copies) {
            File file = copy.getFile();
            copyVertexIds.add(copy.getId());
            
            if (file != null) {
                fileVertexIds.add(file.getId());
                Assert.assertEquals(file.getId(), file.asVertex().getId());
            }
            Assert.assertEquals(copy.getId(), copy.asVertex().getId());
        }
        
        amberSession.deletePage(page);
        amberSession.findWork(workVertexId);
    }
    

    @Test
    public void testOrderChildren() {
        
        List<Work> pages = new ArrayList<>();
        
        Work book = amberSession.addWork();
        for (int i = 0; i < 20; i++) {
            Page page = book.addPage();
            page.setTitle("page " + i);
            pages.add(page); // pages List will hold the pages in order
        }
        
        // set order
        book.orderParts(pages);
        
        // check ordering worked
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(pages.get(i).getOrder(book, "isPartOf", Direction.OUT), (Integer) (i + 1));
        }
        
        // reverse order
        Collections.reverse(pages);
        book.orderParts(pages);

        // Check reordering worked
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(((Page) pages.get(i)).getTitle(), "page "+(19-i));
        }
    }

    @Test
    public void testAddDeliveryWork() {
        Work work = amberSession.addWork();

        Work deliveryWork = amberSession.addWork();
        work.addDeliveryWork(deliveryWork);

        Collection<Work> list = IteratorUtils.toList(work.getDeliveryWorks().iterator());

        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void testRemoveDeliveryWork() {
        Work work = amberSession.addWork();

        Work deliveryWork = amberSession.addWork();
        work.addDeliveryWork(deliveryWork);

        Collection<Work> list = IteratorUtils.toList(work.getDeliveryWorks().iterator());
        Assert.assertFalse(list.isEmpty());

        work.removeDeliveryWork(deliveryWork);

        list = IteratorUtils.toList(work.getDeliveryWorks().iterator());
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testRetrieveDeliveryWorkInterview() {
        Work work = amberSession.addWork();

        Work deliveryWork = amberSession.addWork();
        deliveryWork.setDeliveryWorkParent(work);
        work.addDeliveryWork(deliveryWork);

        Assert.assertTrue(deliveryWork.getDeliveryWorkParent() != null);
        Assert.assertTrue(deliveryWork.getDeliveryWorkParent().equals(work));
    }
    
    @Test
    public void testVendorIdWasSet() {
        Assert.assertEquals("101", bookBlinkyBill.getVendorId());
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
        bookBlinkyBill.setVendorId("101");
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
        Work work = amberSession.addWork();
        
        Date date = new Date();
        
        work.setDcmDateTimeCreated(date);
        Assert.assertEquals(date, work.getDcmDateTimeCreated());
        
        work.setDcmWorkPid("12345");
        Assert.assertEquals("12345", work.getDcmWorkPid());
        
        work.setDcmDateTimeUpdated(date);
        Assert.assertEquals(date, work.getDcmDateTimeUpdated());
        
        work.setDcmRecordCreator("creator");
        Assert.assertEquals("creator", work.getDcmRecordCreator());
        
        work.setDcmRecordUpdater("updater");
        Assert.assertEquals("updater", work.getDcmRecordUpdater());
        
        List<String> list = new ArrayList<String>();
        list.add("pi-1");
        list.add("pi-2");
        work.setDcmAltPi(list);
        
        Assert.assertEquals(2, work.getDcmAltPi().size());
        Assert.assertEquals(list, work.getDcmAltPi());
    }
    
    @Test
    public void testGetSetConstraint() throws IOException {
        Work work = amberSession.addWork();
        Set<String> constraints = new LinkedHashSet<>();
        Assert.assertEquals(0, work.getConstraint().size());
        Assert.assertFalse(constraints.contains("testingc"));
        constraints.add("testing");
        constraints.add("testinga");
        constraints.add("testingb");
        constraints.add("testingc");
        constraints.add("testingd");
        work.setConstraint(constraints);
        constraints = work.getConstraint();
        Assert.assertEquals(5, constraints.size());
        Assert.assertTrue(constraints.contains("testingc"));
        constraints.add("octopus");
        work.setConstraint(constraints);
        constraints = work.getConstraint();
        Assert.assertEquals(6, constraints.size());
        Assert.assertTrue(constraints.contains("octopus"));
    }
    
    @Test
    public void testSetOrder() {
        Work work = amberSession.addWork();
        work.setOrder(0);  // should not throw a NullPointerException
        
        Work child1 = amberSession.addWork();
        work.addChild(child1);
        Work child2 = amberSession.addWork();
        work.addChild(child2);
        
        child2.setOrder(0);
        child1.setOrder(1);
        
        Iterator<Work> it = work.getChildren().iterator();
        Assert.assertEquals(child2, it.next());
        Assert.assertEquals(child1, it.next());
    }

    @Test
    public void hasCopyRoleList(){
        Work work = amberSession.addWork();
        Copy copy1 = work.addCopy();
        copy1.setCopyRole(CopyRole.ACCESS_COPY.code());
        Assert.assertTrue(work.hasCopyRole(Arrays.asList(CopyRole.MASTER_COPY, CopyRole.ACCESS_COPY)));
        Assert.assertFalse(work.hasCopyRole(Arrays.asList(CopyRole.MASTER_COPY, CopyRole.ORIGINAL_COPY)));
    }

    @Test
    public void getFirstExistingCopy(){
        Work work = amberSession.addWork();
        Copy masterCopy = work.addCopy();
        masterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy copy = work.getFirstExistingCopy(CopyRole.ACCESS_COPY, CopyRole.MASTER_COPY);
        Assert.assertThat(copy.getCopyRole(), CoreMatchers.is(CopyRole.MASTER_COPY.code()));
    }

    @Test
    public void getFirstExistingCopyWorkHasNoCopy(){
        Work work = amberSession.addWork();
        Copy copy = work.getFirstExistingCopy(CopyRole.ACCESS_COPY, CopyRole.MASTER_COPY);
        Assert.assertThat (copy, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void getFirstExistingNoCopy(){
        Work work = amberSession.addWork();
        Copy masterCopy = work.addCopy();
        masterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy copy = work.getFirstExistingCopy(null);
        Assert.assertThat (copy, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void getFirstExistingCopyNoMatchingCopy(){
        Work work = amberSession.addWork();
        Copy masterCopy = work.addCopy();
        masterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy copy = work.getFirstExistingCopy(CopyRole.ACCESS_COPY, CopyRole.ORIGINAL_COPY);
        Assert.assertThat (copy, CoreMatchers.is(CoreMatchers.nullValue()));
    }
    
    @Test
    public void removeCopies(){
        Work work = amberSession.addWork();
        Copy masterCopy = work.addCopy();
        masterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        Copy accessCopy1 = work.addCopy();
        accessCopy1.setCopyRole(CopyRole.ACCESS_COPY.code());
        Copy originalCopy = work.addCopy();
        originalCopy.setCopyRole(CopyRole.ORIGINAL_COPY.code());
        work.removeCopies(Arrays.asList(CopyRole.MASTER_COPY, CopyRole.ACCESS_COPY));
        List<Copy> copies = Lists.newArrayList(work.getCopies().iterator());
        Assert.assertThat (copies.size(), CoreMatchers.is (1));
        Assert.assertThat (copies.get(0).getCopyRole(), CoreMatchers.is(CopyRole.ORIGINAL_COPY.code()));
    }
    
}
