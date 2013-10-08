package amberdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.enums.CopyRole;
import amberdb.model.Work;

public class AmberDbTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInMemory() throws IOException {
        Work w1, w2;
        try (AmberSession db = new AmberSession()) {
            w1 = db.addWork();
        }
        try (AmberSession db = new AmberSession()) {
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
        try (AmberSession db = new AmberSession(folder.getRoot().toPath())) {
            w1 = db.addWork();
        }
        try (AmberSession db = new AmberSession(folder.getRoot().toPath())) {
            assertNotNull(db.findWork(w1.getId()));
            w2 = db.addWork();
        }
        assertNotEquals(w1.getId(), w2.getId());
    }

    @Test
    public void testIngestBook() throws IOException {
        Path tmpFile = folder.newFile().toPath();
        Files.write(tmpFile, "Hello world".getBytes());
        try (AmberSession db = new AmberSession()) {
            Work book = db.addWork();
            book.setTitle("Test book");
            for (int i = 0; i < 10; i++) {
                book.addPage().addCopy(tmpFile, CopyRole.MASTER_COPY, "image/tiff");
            }
        }
    }
}
