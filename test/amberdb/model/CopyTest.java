package amberdb.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberSession;
import amberdb.TestUtils;

public class CopyTest {
    
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();    
    
    private AmberSession amberDb;
    
    @Before
    public void startup() {
        amberDb = new AmberSession();           
    }
    
    @After
    public void teardown() throws IOException {
        if (amberDb != null) {
            amberDb.close();
        }       
    }

    @Test
    public void shouldCreateAnImageFileWhenMimeTypeStartsWithImage() throws Exception {                     
        Work work = amberDb.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.tiff"), "image/tiff").setOrder(1);

        File file = work.getPage(1).getCopies().iterator().next().getFile();        
        assertTrue(file instanceof ImageFile);        
    }
    
    @Test
    public void shouldCreateAFile() throws Exception {                     
        Work work = amberDb.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.xml"), "text/html").setOrder(1);

        File file = work.getPage(1).getCopies().iterator().next().getFile();        
        assertFalse(file instanceof ImageFile);
        assertTrue(file instanceof File);        
    }

}
