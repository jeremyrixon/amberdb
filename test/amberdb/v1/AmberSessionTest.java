package amberdb.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import amberdb.v1.AmberSession;
import amberdb.v1.graph.AmberGraph;
import amberdb.v1.model.AliasItem;
import amberdb.v1.model.Copy;
import amberdb.v1.model.Page;
import amberdb.v1.model.Tag;
import amberdb.v1.model.Work;
import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;

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

        Map<String, Integer> counts = sess.deleteWorksFast(new HashMap<String, Integer>(), bookAgain);
        assertEquals(numVertices(sess.getAmberGraph()), 0);

        assertEquals(new Integer(5), counts.get("File"));
        assertEquals(new Integer(5), counts.get("Copy"));
        assertEquals(new Integer(7), counts.get("Work"));

        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        Work book6 = makeBook();

        book3.addChild(book4);
        book5.setBibLevel("Set");
        book3.addChild(book5);

        // check we have the 4 books
        assertEquals(numVertices(sess.getAmberGraph()), 68);

        counts = sess.deleteWorksFast(new HashMap<String, Integer>(), book3);

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

        sess.deleteWorks(bookAgain);
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

        sess.deleteWorks(book3);

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

        sess.deleteWorks(book4);
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

    @Test
    public void testDeleteParentWithSuspend() throws IOException {

        // create a test work and delete its parent
        Work book = makeBook();

        // check our creation
        assertEquals(19, numEdges(sess.getAmberGraph()));
        assertEquals(17, numVertices(sess.getAmberGraph()));

        // now delete the parent and suspend
        sess.deleteWork(book);
        long sessId = sess.suspend();
        sess.close();

        // next recover the session
        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        sess.recover(sessId);

        // check what's in the resumed session - should be 5 pages with 5 copies and 5 files, and 1 Section
        assertEquals(13, numEdges(sess.getAmberGraph()));
        assertEquals(16, numVertices(sess.getAmberGraph()));
        // now commit it
        sess.commit();
        sess.close();

        // then recover the session
        sess = new AmberSession(LocalBlobStore.open(dossLocation));
        assertEquals(13, numEdges(sess.getAmberGraph()));
        assertEquals(16, numVertices(sess.getAmberGraph()));
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

    @Test
    public void testFindModelByJsonListValue() throws IOException {

        // committed works
        Work w1 = sess.addWork();
        Work w2 = sess.addWork();
        Work w3 = sess.addWork();
        Work w4 = sess.addWork();

        w1.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w2.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma", "wally"));
        w3.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));
        w3.setStandardId(Arrays.asList("1445-2197 (ISSN)"));
        w4.setAlias(Arrays.asList("beta", "delta", "wally", "epsilon", "gamma"));
        w4.setStandardId(Arrays.asList("1445-2197 (ISSN)"));

        sess.commit();

        // uncommitted works
        Work w5 = sess.addWork();
        Work w6 = sess.addWork();

        w5.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w6.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));
        w6.setStandardId(Arrays.asList("1445-2197 (ISSN)"));

        List<Work> works = sess.findModelByValueInJsonList("alias", "wally", Work.class);

        assertEquals(4, works.size());
        for (Work w : works) {
            assertTrue(w.getAlias().contains("wally"));
        }
        
        assertEquals(3, sess.findModelByValueInJsonList("standardId", "1445-2197 (ISSN)", Work.class).size());
    }

    @Test
    public void testFindModelByValue() throws IOException {

        // committed works
        Work w1 = sess.addWork();
        Work w2 = sess.addWork();
        Work w3 = sess.addWork();
        Work w4 = sess.addWork();

        w1.setBibId("harry");
        w2.setBibId("houdini");
        w3.setBibId("harry");
        w4.setBibId("potter");

        Date d1 = new Date();
        Date d2 = new Date();
        d2.setYear(0);

        w1.setDcmDateTimeCreated(d2);
        w2.setDcmDateTimeCreated(d1);
        w3.setDcmDateTimeCreated(d2);
        w4.setDcmDateTimeCreated(d2);

        sess.commit();

        // uncommitted works
        Work w5 = sess.addWork();
        Work w6 = sess.addWork();

        w5.setBibId("harrison");
        w6.setBibId("harry");
        w5.setDcmDateTimeCreated(d2);
        w6.setDcmDateTimeCreated(d1);

        // string find
        List<Work> works = sess.findModelByValue("bibId", "harry", Work.class);
        assertEquals(3, works.size());

        works = sess.findModelByValue("dcmDateTimeCreated", d1, Work.class);
        assertEquals(2, works.size());
    }
    
    @Test
    public void testAddTagForCollection() throws IOException {
        List<Work> records = new ArrayList<>();
        String[] collections = { "nla.aus", "nla.gen", "nla.aus", "nla.aus"};
        String[] alias = {"alias", "altalias"};
        for (int i = 0; i < 4; i++) {
            Work record = sess.addWork();
            records.add(record);
            record.setCollection(collections[i]);
            List<String> recordAlias = new ArrayList<>();
            for (String alia : alias) {
                recordAlias.add(alia + i);
            }
            record.setAlias(recordAlias);
        }        
        sess.commit();
        Tag tag = sess.addTagForCollection("nla.aus", "nla-aus-alias-tag", "alias", true);
        sess.commit();
        
        HashMap<String, List<Long>> map = new ObjectMapper().readValue(tag.getDescription(), new TypeReference<LinkedHashMap<String, List<Long>>>() {});
        for (String alia : map.keySet()) {
            List<Long> recordIds = map.get(alia);
            for (Long recordId : recordIds) {
                Work record = sess.findWork(recordId);
                assertTrue(record.getAlias().contains(alia));
            }
        }
    }
}
