package amberdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;

import amberdb.graph.AmberGraph;
import amberdb.model.Copy;
import amberdb.model.Page;
import amberdb.model.Work;


public class AmberSessionTest {

    public AmberSession sess;
    Path fileLocation = Paths.get("test/resources/hello.txt");
    Path dossLocation;
    
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    
    @Before
    public void setup() throws CorruptBlobStoreException, IOException {
        dossLocation = tmpFolder.getRoot().toPath();
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
    public void testDeleteWorkWithAudit() throws IOException {

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

        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        
        Work bookAgain = sess.findWork(book.getId());
        assertNotNull(bookAgain);
        
        Map<String, Integer> counts = sess.deleteWorkWithAudit(new HashMap<String, Integer>(), bookAgain);
        assertEquals(numVertices(sess.getAmberGraph()), 0);
        
        assertEquals(new Integer(5), counts.get("File"));
        assertEquals(new Integer(5), counts.get("Copy"));
        assertEquals(new Integer(7), counts.get("Work"));
        
        // check we don't delete Sets
        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        Work book6 = makeBook();
        
        book3.addChild(book4);
        book5.setBibLevel("Set");
        book3.addChild(book5);
        
        // check we have the 4 books
        assertEquals(numVertices(sess.getAmberGraph()), 68);
        
        counts =  sess.deleteWorkWithAudit(new HashMap<String, Integer>(), book3);
        assertEquals(new Integer(15), counts.get("File"));
        assertEquals(new Integer(15), counts.get("Copy"));
        assertEquals(new Integer(21), counts.get("Work"));

        // we should have retained book6 as it's not in book3 hierarchy
        assertEquals(numVertices(sess.getAmberGraph()), 17);
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

        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        
        Work bookAgain = sess.findWork(book.getId());
        assertNotNull(bookAgain);
        
        sess.deleteWorkRecursive(bookAgain);
        assertEquals(numVertices(sess.getAmberGraph()), 0);
        
        // check we don't delete Sets
        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        Work book6 = makeBook();
        
        book3.addChild(book4);
        book5.setBibLevel("Set");
        book3.addChild(book5);
        
        // check we have the 4 books
        assertEquals(numVertices(sess.getAmberGraph()), 68);
        
        sess.deleteWorkRecursive(book3);
        
        // we should have retained book6 as it's not in book3 hierarchy
        assertEquals(numVertices(sess.getAmberGraph()), 17);
    }        

    @Test
    public void testDeleteWithCycle() throws IOException {

        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        
        book3.addChild(book4);
        book4.addChild(book5);
        book5.addChild(book3);

        sess.deleteWorkRecursive(book4);
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
        book.addPage(fileLocation, "text/plain");
        book.addPage(fileLocation, "text/plain");

        Work chapter = book.addSection();
        chapter.asSection().addPage(p1);
        chapter.asSection().addPage(p2);
        chapter.asSection().addPage(p3);
        
        return book;
    }
    
    @Test
    public void testSuspensionEdgeDeletions() throws IOException {
        
        // create a graph with 1 edge
        AmberGraph g = sess.getAmberGraph();
        
        Vertex v1 = g.addVertex(null);
        Vertex v2 = g.addVertex(null);
        Edge e = g.addEdge(null, v1, v2, "link");
        g.commit();
        assertEquals(numEdges(g), 1);
        g.removeEdge(e);
        assertEquals(numEdges(g), 0);
        Long sId = g.suspend();
        sess.close();
        
        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        AmberGraph g2 = sess.getAmberGraph();
        g2.resume(sId);
        assertEquals(numEdges(g2), 0);
        g2.commit();
        sess.close();
        
        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        AmberGraph g3 = sess.getAmberGraph();
        assertEquals(numEdges(g3), 0);
    }
    
    private int numEdges(Graph g) {
        int i = 0;
        for (Edge e : g.getEdges()) {
            i++;
        }
        return i;
    }

    private int numVertices(Graph g) {
        int i = 0;
        for (Vertex v : g.getVertices()) {
            i++;
        }
        return i;
    }
}
