package amberdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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

    @Test
    public void testGetSetAllOtherNumbers() throws IOException {
        Copy copy = amberDb.addWork().addCopy();
        Map<String, String> otherNumbers = new HashMap<>();
        otherNumbers.put("Voyager", "voyagerNumber");
        otherNumbers.put("State Library of Victoria", "slvNumber");
        otherNumbers.put("Jon's Cookbook", "1");
        copy.setAllOtherNumbers(otherNumbers);
        ObjectMapper mapper = new ObjectMapper();
        String otherNumbesrs = copy.getOtherNumbers();
        mapper.readValue(otherNumbesrs, new TypeReference<Map<String, String>>() { } );
        otherNumbers = copy.getAllOtherNumbers();
        assertEquals(otherNumbers.size(), 3);
        assertEquals(otherNumbers.get("State Library of Victoria"), "slvNumber");
        otherNumbers.put("fruitNumber", "23");
        copy.setAllOtherNumbers(otherNumbers);
        otherNumbers = copy.getAllOtherNumbers();
        assertEquals(otherNumbers.size(), 4);
        assertEquals(otherNumbers.get("fruitNumber"), "23");
    }
}
