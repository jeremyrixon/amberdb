package amberdb.query;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.model.*;
import doss.CorruptBlobStoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class WorkChildrenQueryTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void testGetWorkChildren() throws IOException {

        // create a test work with children
        Work parent = buildWork();
        amberSession.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(amberSession);
        List<Work> children = wcq.getChildRange(parent.getId(), 0, 5);
        Assert.assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(children.get(i).getTitle(),"page " + i);
        }

        amberSession.setLocalMode(true);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(children.get(i).getCopy(CopyRole.MASTER_COPY)
                    .getFile().getDevice(),"device " + i);
        }
        amberSession.setLocalMode(false);
        
        children = wcq.getChildRange(parent.getId(), 5, 5);
        Assert.assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(children.get(i).getTitle(),"page " + (i+5));
        }

        children = wcq.getChildRange(parent.getId(), 25, 35);
        Assert.assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(children.get(i).getTitle(),"work " + (i+25));
        }

        int numChilds = wcq.getTotalChildCount(parent.getId());
        Assert.assertEquals(30, numChilds);

        List<CopyRole> roles = wcq.getAllChildCopyRoles(parent.getId());
        Assert.assertEquals(roles.size(), 2);
        Assert.assertTrue(roles.contains(CopyRole.MASTER_COPY));
        Assert.assertTrue(roles.contains(CopyRole.ACCESS_COPY));

        amberSession.getAmberGraph().clear();
        children = wcq.getChildRange(parent.getId(), 5, 5);
        amberSession.setLocalMode(true);
        Assert.assertEquals(children.size(), 5);
        Work c1 = children.get(0);
        for (Copy c : c1.getCopies()) {
            Assert.assertNotNull(c);
            File f = c.getFile();
            Assert.assertNotNull(f);
        }
        amberSession.setLocalMode(false);

        List<Section> sections = wcq.getSections(parent.getId());
        amberSession.setLocalMode(true);
        Assert.assertEquals(sections.size(), 7);
        amberSession.setLocalMode(false);
    }        
    

    private Work buildWork() {
        Work parent = amberSession.addWork();
        
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
            
            Work w = amberSession.addWork();
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
