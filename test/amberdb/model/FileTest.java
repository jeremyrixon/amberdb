package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import amberdb.AmberSession;

public class FileTest {
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
    public void shouldReturn0InAbsenceOfFileSize() {
        Work work = amberDb.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        assertEquals(0L, file.getFileSize());
        assertEquals(0L, file.getSize());
    }
}
