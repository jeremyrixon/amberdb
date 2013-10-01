package amberdb;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.tools.ant.util.WorkerAnt;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.model.Work;

public class AmberDbTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInMemory() throws IOException {
        Work w1, w2;
        try (AmberDb db = new AmberDb()) {
            w1 = db.addWork();
        }
        try (AmberDb db = new AmberDb()) {
            try {
                db.findWork(w1.getId());
                assertTrue("works should not persist", false);
            } catch (NoSuchObjectException e) {
                // ok
            }
            w2 = db.addWork();
        }
        assertEquals("ids should not persist", w1.getId(), w2.getId());
    }

    @Test
    public void testPersistence() throws IOException {
        Work w1, w2;
        try (AmberDb db = new AmberDb(folder.getRoot().toPath())) {
            w1 = db.addWork();
        }
        try (AmberDb db = new AmberDb(folder.getRoot().toPath())) {
            assertNotNull(db.findWork(w1.getId()));
            w2 = db.addWork();
        }
        assertNotEquals(w1.getId(), w2.getId());
    }

}
