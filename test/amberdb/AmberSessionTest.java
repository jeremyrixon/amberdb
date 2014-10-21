package amberdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Vertex;

import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;

import amberdb.model.Copy;
import amberdb.model.Page;
import amberdb.model.Work;


public class AmberSessionTest {

    public AmberSession sess;
    Path fileLocation = Paths.get("test/resources/hello.txt");
    
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    
    @Before
    public void setup() throws CorruptBlobStoreException, IOException {
        Path dossLocation = tmpFolder.getRoot().toPath();
        LocalBlobStore.init(dossLocation);
        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        for (Vertex v : sess.getGraph().getVertices()) {
            v.remove();
        }
        sess.commit();
    }

    @After
    public void tearDown() throws IOException {
        if (sess != null) sess.close();
    }
    
    @Test
    public void testDeleteWorkRecursiveItem() throws IOException {

        // create a test work and delete it
        Work book = makeBook();
        
        // check our creation
        int p = 0;
        int c = 0;

        // count pages, copies etc
        for (Work page : book.getChildren()) {
            p++;
            for (Copy copy : page.getCopies()) {
                c++;
            }
        }
        // we expect 5 pages and 1 section = 6
        assertEquals(p, 6);
        // 5 copies (none for the section)
        assertEquals(c, 5);

        sess.commit();
        sess.close();

        Path dossLocation = tmpFolder.getRoot().toPath();
        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        
        Work bookAgain = sess.findWork(book.getId());
        assertNotNull(bookAgain);
        
        sess.deleteWorkRecursive(bookAgain);
        int b = 0;
        for (Vertex v : sess.getAmberGraph().getVertices()) {
            b++;
        }
        assertEquals(b, 0);
        
        // check we don't delete Sets
        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        
        book3.addChild(book4);
        book5.setBibLevel("Set");
        book3.addChild(book5);
        
        // check we have the 3 books
        int vNum = 0;
        for (Vertex v : sess.getAmberGraph().getVertices()) {
            vNum++;
        }
        assertEquals(vNum, 51);
        
        sess.deleteWorkRecursive(book3);
        
        // we should have retained book 5 because it's a Set
        vNum = 0;
        for (Vertex v : sess.getAmberGraph().getVertices()) {
            vNum++;
        }
        assertEquals(vNum, 17);
    }        

    private static void s(String s) {
        System.out.println(s);
    }
    
    private Work makeBook() throws IOException {

        Work book = sess.addWork();
        book.setBibLevel("Item");
        
        Page p1 = book.addPage(fileLocation, "text/plain");
        Page p2 = book.addPage(fileLocation, "text/plain");
        Page p3 = book.addPage(fileLocation, "text/plain");
        Page p4 = book.addPage(fileLocation, "text/plain");
        Page p5 = book.addPage(fileLocation, "text/plain");

        Work chapter = book.addSection();
        chapter.asSection().addPage(p1);
        chapter.asSection().addPage(p2);
        chapter.asSection().addPage(p3);
        
        return book;
    }
}
