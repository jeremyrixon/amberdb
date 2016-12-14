package amberdb.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import doss.CorruptBlobStoreException;
import amberdb.enums.CopyRole;
import amberdb.model.CameraData;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.GeoCoding;
import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.Work;
import amberdb.AmberSession;

public class WorkChildrenQueryTest {

    public AmberSession sess;
    
    @Before
    public void setup() throws CorruptBlobStoreException, IOException {
        sess = new AmberSession();
    }

    @After
    public void tearDown() throws IOException {
        if (sess != null) sess.close();
    }

    @Test
    public void testGetWorkChildren() throws IOException {

        // create a test work with children
        Work parent = buildWork();
        sess.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(sess);
        List<Work> children = wcq.getChildRange(parent.getId(), 0, 5);
        assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getTitle(),"page " + i);
        }
        
        sess.setLocalMode(true); 
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getCopy(CopyRole.MASTER_COPY)
                    .getFile().getDevice(),"device " + i);
        }
        sess.setLocalMode(false);
        
        children = wcq.getChildRange(parent.getId(), 5, 5);
        assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getTitle(),"page " + (i+5));
        }

        children = wcq.getChildRange(parent.getId(), 25, 35);
        assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getTitle(),"work " + (i+25));
        }

        int numChilds = wcq.getTotalChildCount(parent.getId());
        assertEquals(30, numChilds);

        List<CopyRole> roles = wcq.getAllChildCopyRoles(parent.getId());
        assertEquals(roles.size(), 2);
        assertTrue(roles.contains(CopyRole.MASTER_COPY));
        assertTrue(roles.contains(CopyRole.ACCESS_COPY));

        sess.getAmberGraph().clear();
        children = wcq.getChildRange(parent.getId(), 5, 5);
        sess.setLocalMode(true);
        assertEquals(children.size(), 5);
        Work c1 = children.get(0);
        for (Copy c : c1.getCopies()) {
            assertNotNull(c);
            File f = c.getFile();
            assertNotNull(f);
        }
        sess.setLocalMode(false);

        List<Section> sections = wcq.getSections(parent.getId());
        sess.setLocalMode(true);
        assertEquals(sections.size(), 7);
        sess.setLocalMode(false);
    }

    @Test
    public void testGetChildRangeSorted() throws Exception {
        // create a test work with children with properties to sort by
        Work parent = sess.addWork();

        // add children with properties that are not in the same sort order as the Order property or each other
        Work child1 = sess.addWork();
        child1.setOrder(1);
        child1.setTitle("title5"); 
        child1.setBibId("bibId2");
        parent.addChild(child1);
        
        Work child2 = sess.addWork();
        child2.setOrder(2);
        child2.setTitle("title4");
        child2.setBibId("bibId5");
        parent.addChild(child2);
        
        Work child3 = sess.addWork();
        child3.setOrder(3);
        child3.setTitle("title3");
        child3.setBibId("bibId1");
        parent.addChild(child3);
        
        Work child4 = sess.addWork();
        child4.setOrder(4);
        child4.setTitle("title2");
        child4.setBibId("bibId4");
        parent.addChild(child4);
        
        Work child5 = sess.addWork();
        child5.setOrder(5);
        child5.setTitle("title1");
        child5.setBibId("bibId3");
        child5.setEndDate(new Date());
        child5.setIsMissingPage(true);
        parent.addChild(child5);

        sess.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(sess);

        // sort by Title should be child 5, 4, 3, 2, 1
        List<Work> sortedByTitle = wcq.getChildRangeSorted(parent.getId(), 0, 10, "title", true);
        assertEquals(child5.getId(), sortedByTitle.get(0).getId());
        assertEquals(child4.getId(), sortedByTitle.get(1).getId());
        assertEquals(child3.getId(), sortedByTitle.get(2).getId());
        assertEquals(child2.getId(), sortedByTitle.get(3).getId());
        assertEquals(child1.getId(), sortedByTitle.get(4).getId());

        // sort by bibId should be child 3, 1, 5, 4, 2
        List<Work> sortedByBibId = wcq.getChildRangeSorted(parent.getId(), 0, 10, "bibId", true);
        assertEquals(child3.getId(), sortedByBibId.get(0).getId());
        assertEquals(child1.getId(), sortedByBibId.get(1).getId());
        assertEquals(child5.getId(), sortedByBibId.get(2).getId());
        assertEquals(child4.getId(), sortedByBibId.get(3).getId());
        assertEquals(child2.getId(), sortedByBibId.get(4).getId());
    }

    @Test
    public void testNullShouldAlwaysBeLastInGetChildRangeSorted() throws Exception {
        // create a test work with children with properties to sort by
        Work parent = sess.addWork();

        // add children with properties that are not in the same sort order as the Order property or each other
        Work child1 = sess.addWork();
        child1.setOrder(1);
        child1.setTitle("title5");
        parent.addChild(child1);

        Work child2 = sess.addWork();
        child2.setOrder(2);
        child2.setTitle("title4");
        parent.addChild(child2);

        Work child3 = sess.addWork();
        child3.setOrder(3);
        child3.setTitle(null);
        child3.setBibId("bibId1");
        parent.addChild(child3);

        sess.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(sess);

        List<Work> sortedByTitle = wcq.getChildRangeSorted(parent.getId(), 0, 10, "title", true); // ascending
        assertEquals(child2.getId(), sortedByTitle.get(0).getId());
        assertEquals(child1.getId(), sortedByTitle.get(1).getId());
        assertEquals(child3.getId(), sortedByTitle.get(2).getId()); // null title

        sortedByTitle = wcq.getChildRangeSorted(parent.getId(), 0, 10, "title", false); // descending
        assertEquals(child1.getId(), sortedByTitle.get(0).getId());
        assertEquals(child2.getId(), sortedByTitle.get(1).getId());
        assertEquals(child3.getId(), sortedByTitle.get(2).getId()); // null title
    }

    private Work buildWork() {
        Work parent = sess.addWork();
        
        for (int i = 0; i < 20; i++) {
            
            Page p = parent.addPage();
            p.setOrder(i);
            p.setTitle("page " + i);
            
            GeoCoding gc = p.addGeoCoding();
            gc.setGPSVersion("version " + i);
            
            Copy c = p.addCopy();
            c.setCopyRole(CopyRole.MASTER_COPY.code());
            CameraData cd = c.addCameraData();
            cd.setLens("lens");
            
            File f = c.addFile();
            f.setDevice("device " + i);
        }

        for (int i = 20; i < 30; i++) {
            
            Work w = sess.addWork();
            parent.addChild(w);
            w.setTitle("work " + i);
            w.setOrder(i);
            
            Copy c = w.addCopy();
            c.setCopyRole(CopyRole.ACCESS_COPY.code());
            CameraData cd = c.addCameraData();
            cd.setLens("work lens");
            
            File f = c.addFile();
            f.setDevice("work device " + i);
        }

        for (int i = 0; i < 7; i++) {
            
            Section s = parent.addSection();
            s.setTitle("section " + i);
            s.setOrder(i);

            s.addPage(parent.getPage(i+1));
        }
        
        return parent;
    }

}
