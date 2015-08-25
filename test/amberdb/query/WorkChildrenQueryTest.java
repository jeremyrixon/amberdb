package amberdb.query;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
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
            c.setCopyRole(CopyRole.MASTER_COPY.code());
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
