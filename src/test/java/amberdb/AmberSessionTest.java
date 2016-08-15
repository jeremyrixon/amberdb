package amberdb;

import amberdb.graph.AmberGraph;
import amberdb.model.Copy;
import amberdb.model.Page;
import amberdb.model.Tag;
import amberdb.model.Work;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class AmberSessionTest extends AbstractDatabaseIntegrationTest {

    Path fileLocation = Paths.get("src/test/resources/hello.txt");

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
        Assert.assertEquals(p, 6);
        // 5 copies (none for the section)
        Assert.assertEquals(c, 5);

        amberSession.commit();
        amberSession.close();

        amberSession = amberDb.begin();

        Work bookAgain = amberSession.findWork(book.getId());
        Assert.assertNotNull(bookAgain);

        Map<String, Integer> counts = amberSession.deleteWorksFast(new HashMap<String, Integer>(), bookAgain);
        Assert.assertEquals(numVertices(amberSession.getAmberGraph()), 0);

        Assert.assertEquals(new Integer(5), counts.get("File"));
        Assert.assertEquals(new Integer(5), counts.get("Copy"));
        Assert.assertEquals(new Integer(7), counts.get("Work"));

        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        Work book6 = makeBook();

        book3.addChild(book4);
        book5.setBibLevel("Set");
        book3.addChild(book5);

        // check we have the 4 books
        Assert.assertEquals(numVertices(amberSession.getAmberGraph()), 68);

        counts = amberSession.deleteWorksFast(new HashMap<String, Integer>(), book3);

        Assert.assertEquals(new Integer(15), counts.get("File"));
        Assert.assertEquals(new Integer(15), counts.get("Copy"));
        Assert.assertEquals(new Integer(21), counts.get("Work"));

        // we should have retained book6 as it's not in book3 hierarchy
        Assert.assertEquals(numVertices(amberSession.getAmberGraph()), 17);
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
        Assert.assertEquals(p, 6);
        // 5 copies (none for the section)
        Assert.assertEquals(c, 5);

        amberSession.commit();
        amberSession.close();

        amberSession = amberDb.begin();

        Work bookAgain = amberSession.findWork(book.getId());
        Assert.assertNotNull(bookAgain);

        amberSession.deleteWorks(bookAgain);
        Assert.assertEquals(numVertices(amberSession.getAmberGraph()), 0);

        // check we don't delete Sets
        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();
        Work book6 = makeBook();

        book3.addChild(book4);
        book5.setBibLevel("Set");
        book3.addChild(book5);

        // check we have the 4 books
        Assert.assertEquals(numVertices(amberSession.getAmberGraph()), 68);

        amberSession.deleteWorks(book3);

        // we should have retained book6 as it's not in book3 hierarchy
        Assert.assertEquals(numVertices(amberSession.getAmberGraph()), 17);
    }

    @Test
    public void testDeleteWithCycle() throws IOException {

        Work book3 = makeBook();
        Work book4 = makeBook();
        Work book5 = makeBook();

        book3.addChild(book4);
        book4.addChild(book5);
        book5.addChild(book3);

        amberSession.deleteWorks(book4);
    }

    private static void s(String s) {
        System.out.println(s);
    }

    private Work makeBook() throws IOException {

        Work book = amberSession.addWork();
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
        AmberGraph g = amberSession.getAmberGraph();

        Vertex v1 = g.addVertex(null);
        Vertex v2 = g.addVertex(null);
        Edge e = g.addEdge(null, v1, v2, "link");
        g.commit();
        Assert.assertEquals(numEdges(g), 1);
        g.removeEdge(e);
        Assert.assertEquals(numEdges(g), 0);
        Long sId = g.suspend();
        amberSession.close();

        amberSession = amberDb.begin();
        AmberGraph g2 = amberSession.getAmberGraph();
        g2.resume(sId);
        Assert.assertEquals(numEdges(g2), 0);
        g2.commit();
        amberSession.close();

        amberSession = amberDb.begin();
        AmberGraph g3 = amberSession.getAmberGraph();
        Assert.assertEquals(numEdges(g3), 0);
    }

    @Test
    public void testDeleteParentWithSuspend() throws IOException {

        // create a test work and delete its parent
        Work book = makeBook();

        // check our creation
        Assert.assertEquals(19, numEdges(amberSession.getAmberGraph()));
        Assert.assertEquals(17, numVertices(amberSession.getAmberGraph()));

        // now delete the parent and suspend
        amberSession.deleteWork(book);
        long sessId = amberSession.suspend();
        amberSession.close();

        // next recover the session
        amberSession = amberDb.begin();
        amberSession.recover(sessId);

        // check what's in the resumed session - should be 5 pages with 5 copies and 5 files, and 1 Section
        Assert.assertEquals(13, numEdges(amberSession.getAmberGraph()));
        Assert.assertEquals(16, numVertices(amberSession.getAmberGraph()));
        // now commit it
        amberSession.commit();
        amberSession.close();

        // then recover the session
        amberSession = amberDb.begin();
        Assert.assertEquals(13, numEdges(amberSession.getAmberGraph()));
        Assert.assertEquals(16, numVertices(amberSession.getAmberGraph()));
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
        Work w1 = amberSession.addWork();
        Work w2 = amberSession.addWork();
        Work w3 = amberSession.addWork();
        Work w4 = amberSession.addWork();

        w1.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w2.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma", "wally"));
        w3.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));
        w3.setStandardId(Arrays.asList("1445-2197 (ISSN)"));
        w4.setAlias(Arrays.asList("beta", "delta", "wally", "epsilon", "gamma"));
        w4.setStandardId(Arrays.asList("1445-2197 (ISSN)"));

        amberSession.commit();

        // uncommitted works
        Work w5 = amberSession.addWork();
        Work w6 = amberSession.addWork();

        w5.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w6.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));
        w6.setStandardId(Arrays.asList("1445-2197 (ISSN)"));

        List<Work> works = amberSession.findModelByValueInJsonList("alias", "wally", Work.class);

        Assert.assertEquals(4, works.size());
        for (Work w : works) {
            Assert.assertTrue(w.getAlias().contains("wally"));
        }
        
        Assert.assertEquals(3, amberSession.findModelByValueInJsonList("standardId", "1445-2197 (ISSN)", Work.class).size());
    }

    @Test
    public void testFindModelByValue() throws IOException {

        // committed works
        Work w1 = amberSession.addWork();
        Work w2 = amberSession.addWork();
        Work w3 = amberSession.addWork();
        Work w4 = amberSession.addWork();

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

        amberSession.commit();

        // uncommitted works
        Work w5 = amberSession.addWork();
        Work w6 = amberSession.addWork();

        w5.setBibId("harrison");
        w6.setBibId("harry");
        w5.setDcmDateTimeCreated(d2);
        w6.setDcmDateTimeCreated(d1);

        // string find
        List<Work> works = amberSession.findModelByValue("bibId", "harry", Work.class);
        Assert.assertEquals(3, works.size());

        works = amberSession.findModelByValue("dcmDateTimeCreated", d1, Work.class);
        Assert.assertEquals(2, works.size());
    }
    
    @Test
    public void testAddTagForCollection() throws IOException {
        List<Work> records = new ArrayList<>();
        String[] collections = { "nla.aus", "nla.gen", "nla.aus", "nla.aus"};
        String[] alias = {"alias", "altalias"};
        for (int i = 0; i < 4; i++) {
            Work record = amberSession.addWork();
            records.add(record);
            record.setCollection(collections[i]);
            List<String> recordAlias = new ArrayList<>();
            for (String alia : alias) {
                recordAlias.add(alia + i);
            }
            record.setAlias(recordAlias);
        }
        amberSession.commit();
        Tag tag = amberSession.addTagForCollection("nla.aus", "nla-aus-alias-tag", "alias", true);
        amberSession.commit();
        
        HashMap<String, List<Long>> map = new ObjectMapper().readValue(tag.getDescription(), new TypeReference<LinkedHashMap<String, List<Long>>>() {});
        for (String alia : map.keySet()) {
            List<Long> recordIds = map.get(alia);
            for (Long recordId : recordIds) {
                Work record = amberSession.findWork(recordId);
                Assert.assertTrue(record.getAlias().contains(alia));
            }
        }
    }

    @Test
    public void testFindModelObjectById() {
        // committed works
        Work w1 = amberSession.addWork();
        Work w2 = amberSession.addWork();
        Work w3 = amberSession.addWork();
        Work w4 = amberSession.addWork();

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

        amberSession.commit();

        amberdb.repository.model.Work work = amberSession.findModelObjectById(w1.getId(), amberdb.repository.model.Work.class);
        Assert.assertNotNull(work);
        assertEquals(w1.getId(), work.getId());
        Assert.assertTrue(StringUtils.equals(w1.getBibId(), work.getBibId()));
        assertEquals(w1.getDcmDateTimeCreated(), work.getDcmDateTimeCreated());
    }
}
