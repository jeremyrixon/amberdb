package amberdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import amberdb.model.AliasItem;
import amberdb.AmberSession;

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
        w4.setAlias(Arrays.asList("beta", "delta", "wally", "epsilon", "gamma"));

        sess.commit();

        // uncommitted works
        Work w5 = sess.addWork();
        Work w6 = sess.addWork();

        w5.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w6.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));

        List<Work> works = sess.findModelByValueInJsonList("alias", "wally", Work.class);

        assertEquals(4, works.size());
        for (Work w : works) {
            assertTrue(w.getAlias().contains("wally"));
        }
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
    public void testJsonDuplicateValueQueries() throws Exception {

        AmberGraph graph = sess.getAmberGraph();
  
        
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("alias-list", "[\"abba\",\"beta\",\"delta\",\"snez\",\"gama\"]");
        v1.setProperty("collection", "nla.aus");
        v1.setProperty("type", "Work");
        v1.setProperty("title", "title1");
        
        Vertex v2 = graph.addVertex(null);
        v2.setProperty("alias-list", "[\"babba\",\"beta\",\"baraba\",\"delta\",\"gama\"]");
        v2.setProperty("collection", "nla.aus");
        v2.setProperty("type", "Work");
        v2.setProperty("title", "title2");
        
        Vertex v3 = graph.addVertex(null);
        v3.setProperty("alias-list", "[\"beta\",\"delta\",\"gama\",\"snez\",\"abba\"]");
        v3.setProperty("type", "Copy");
        v3.setProperty("title", "title3");
        Edge ed = graph.addEdge(null, v3, v2, "isCopyOf");
        
        Vertex v4 = graph.addVertex(null);
        v4.setProperty("alias-list", "[\"abba\",\"beta\",\"delta\",\"snez\",\"gama\"]");
        v4.setProperty("collection", "nla.aus");
        v4.setProperty("type", "Page");
        v4.setProperty("title", "title4");
        
        Vertex v5 = graph.addVertex(null);
        v5.setProperty("alias-list", "[\"abba\",\"beta\",\"delta\",\"snez\",\"gama\"]");
        v5.setProperty("collection", "nla.aus");
        v5.setProperty("type", "Section");
        v5.setProperty("title", "title5");
        
        Vertex v6 = graph.addVertex(null);
        v6.setProperty("alias-list", "[\"abba\",\"beta\",\"delta\",\"snez\",\"gama\"]");
        v6.setProperty("collection", "nla.aus");
        v6.setProperty("type", "EADWork");
        v6.setProperty("title", "title6");
        
        graph.commit("tester", "testing duplicates in the json value");
        
        Map<String, Set<AliasItem>> aliasMap = sess.getDuplicateAliases("alias-list", "nla.aus");
        
        assertEquals(5, aliasMap.size());
        assertEquals(6, aliasMap.get("beta").size());
        assertEquals(6, aliasMap.get("delta").size());
        assertEquals(6, aliasMap.get("gama").size());
        assertEquals(5, aliasMap.get("abba").size());
        assertEquals(null,aliasMap.get("babba"));
        assertEquals(null,aliasMap.get("baraba"));
    }
    
  @Test
  public void testExpiryReport() throws Exception {

      AmberGraph graph = sess.getAmberGraph();
      
      DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String dateString = "2016-01-01";
      Date date1 = sdf.parse(dateString); 
      dateString = "2017-01-01";
      Date date2 = sdf.parse(dateString); 
    
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, 2016);
      Date date = calendar.getTime();
      Vertex v1 = graph.addVertex(null);
      v1.setProperty("expiryDate", date1.getTime());
      v1.setProperty("collection", "nla.aus");
      v1.setProperty("type", "Work");
      v1.setProperty("internalAccessConditions", "Open");
      v1.setProperty("title", "title1");
     
      Vertex v2 = graph.addVertex(null);
      v2.setProperty("expiryDate", date1.getTime());
      v2.setProperty("collection", "nla.aus");
      v2.setProperty("internalAccessConditions", "Open");
      v2.setProperty("type", "Work");
      v2.setProperty("title", "title2");
     
      Vertex v3 = graph.addVertex(null);
      calendar.set(Calendar.YEAR, 2017);
      date = calendar.getTime();
      v3.setProperty("expiryDate", date2.getTime());
      v3.setProperty("type", "Copy");
      v3.setProperty("title", "title3");
      v3.setProperty("collection", "nla.aus");
      v3.setProperty("internalAccessConditions", "Open");
      
      Vertex v4 = graph.addVertex(null);
      v4.setProperty("expiryDate", date2.getTime());
      v4.setProperty("type", "Work");
      v4.setProperty("title", "title3");
      v4.setProperty("collection", "nla.aus");
      v4.setProperty("internalAccessConditions", "Open");
      
      Vertex v5 = graph.addVertex(null);
      v5.setProperty("expiryDate", date2.getTime());
      v5.setProperty("type", "Work");
      v5.setProperty("title", "title3");
      v5.setProperty("collection", "nla.oh");
      v5.setProperty("internalAccessConditions", "Closed");
      
      Vertex v6 = graph.addVertex(null);
      v6.setProperty("expiryDate", date2.getTime());
      v6.setProperty("type", "Work");
      v6.setProperty("title", "title3");
      v6.setProperty("collection", "nla.oh");
      v6.setProperty("internalAccessConditions", "Open");
      
      
      Vertex v7 = graph.addVertex(null);
      v7.setProperty("expiryDate", date1.getTime());
      v7.setProperty("type", "Page");
      v7.setProperty("title", "title7");
      v7.setProperty("collection", "nla.oh");
      v7.setProperty("internalAccessConditions", "Open");
      
      
      Vertex v8 = graph.addVertex(null);
      v8.setProperty("expiryDate", date1.getTime());
      v8.setProperty("type", "Section");
      v8.setProperty("title", "title8");
      v8.setProperty("collection", "nla.oh");
      v8.setProperty("internalAccessConditions", "Open");
      
      Vertex v9 = graph.addVertex(null);
      v9.setProperty("expiryDate", date1.getTime());
      v9.setProperty("type", "EADWork");
      v9.setProperty("title", "title8");
      v9.setProperty("collection", "nla.oh");
      v9.setProperty("internalAccessConditions", "Open");
    

     
      graph.commit("tester", "testing expiry report");
  
      List<Work> result =  sess.getExpiryReport(date1, "nla.aus");
      assertEquals(2, result.size());
      result =  sess.getExpiryReport(date1, "nla.oh");
      assertEquals(3, result.size());
      result =  sess.getExpiryReport(date2, "nla.aus");
      assertEquals(1, result.size());
      result =  sess.getExpiryReport(date2, "nla.oh");
      assertEquals(1, result.size());
      
      


  }
  
    

}
