package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberHistoryTransactionsSinceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph;
    public AmberGraph graph2;

    Path tempPath;
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        tempPath = Paths.get(tempFolder.getRoot().getAbsolutePath());
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath.toString()+"amber;auto_server=true","sess","sess");
        graph = new AmberGraph(src);
    }

    @After
    public void teardown() {}


    @Test
    public void testPersistingVertex() throws Exception {
        
        Date now = new Date();
        
        // make 100 other vertices
        List<Long> vertIds = new ArrayList<Long>();
        for (int i=0; i < 100; i++) {
            Vertex v = graph.addVertex(null);
            v.setProperty("name", "v"+i);
            vertIds.add((Long) v.getId());
        }
        // save 'em
        graph.commit("test", "save 100");
        
        // check we get them all back again
        AmberHistory ah = new AmberHistory(graph);
        Map<Long, String> vSince = ah.getModifiedObjectIds(now);
        assertEquals(100, vSince.size());

        now = new Date();
        
        // lets make some changes

        // expect 50 deletes
        for (int i = 0; i < 50; i++) {
            graph.getVertex(vertIds.get(i)).remove();
        }
        
        // expect 20 mods
        for (int i = 50; i < 70; i++) {
            graph.getVertex(vertIds.get(i)).setProperty("age", i);
        }
        
        // expect 10 new
        for (int i = 0; i < 10; i++) {
            Vertex v = graph.addVertex(null);
            v.setProperty("name", "v"+(100+i));
            vertIds.add((Long) v.getId());
        }

        // check for no change before commit
        assertEquals(0, ah.getModifiedObjectIds(now).size());
        
        // don't forget to commit
        graph.commit("test", "modified 80");
        
        vSince = ah.getModifiedObjectIds(now);
        
        int vNew = 0;
        int vMod = 0;
        int vDel = 0;
        
        for (String state : vSince.values()) {
            if (state.equals("NEW")) {
                vNew++;
            } 
            if (state.equals("MODIFIED")) {
                vMod++;
            }
            if (state.equals("DELETED")) {
                vDel++;
            }
        }
        
        assertEquals(10, vNew);
        assertEquals(20, vMod);
        assertEquals(50, vDel);

        now = new Date();
        
        // now add some edges
        for (int i = 50; i < 60; i++) {
            graph.addEdge(null, graph.getVertex(vertIds.get(i)), graph.getVertex(vertIds.get(i+10)), "link"+i);
        }
        graph.commit("test", "rel test");    

        vSince = ah.getModifiedObjectIds(now);
        assertEquals(20, vSince.size());
    }        

    
    @Test
    public void testTransactions() throws Exception {
        
        
        AmberSession sess = new AmberDb(src, tempPath).begin();
        AmberGraph aGraph = sess.getAmberGraph();

        Date now = new Date();
        
        // Set up work
        Work w = sess.addWork();
        w.setTitle("Add 100 modifications.");
        for (long i=0; i<20; i++) {
            Page p = w.addPage();

            Copy c1 = p.addCopy();
            File f1 = c1.addFile();
            
            Copy c2 = p.addCopy();
            File f2 = c2.addFile();
            
            p.setOrder((int) i);
            p.setTitle("page " + (i+1));
            
            c1.setCopyRole(CopyRole.ACCESS_COPY.code());
            f1.setBlobId(i);

            c2.setCopyRole(CopyRole.OCR_JSON_COPY.code());
            f2.setBlobId(i+100);
        }
        aGraph.commit("test", "commit book");

        // Check we get all the bits of the work we want
        Map<Long, String> changed = sess.getModifiedObjectIds(now);
        assertThat(changed.size(), is (101));
        
        now = new Date();

        // Modify work by updating title, adding a page
        w.setTitle("Testing a new title");
        Page p = w.addPage();
        p.setTitle("new page");
        p.setOrder(0);
        
        sess.commit("test", "change title add page");

        aGraph.clear();
        
        changed = sess.getModifiedObjectIds(now);
        assertThat(changed.size(), is (2));
        
        now = new Date();

        // Modify work by deleting 2 pages
        w.setTitle("Testing a new title");

        p = w.getPage(12);
        sess.deletePage(p);

        p = w.getPage(3);
        sess.deletePage(p);

        p = w.getPage(5);
        sess.deletePage(p);
        
        sess.commit("test", "deleted 3 pages");

        aGraph.clear();
        
        
        changed = sess.getModifiedObjectIds(now);
        assertEquals(16, changed.size()); // 1 for title modification, 3 x 5 per page (1 page, 2 copies and 2 files) deletions
    }
    
    @Test
    public void testFollowEdges() throws Exception {

        AmberSession sess = new AmberDb(src, tempPath).begin();
        AmberGraph aGraph = sess.getAmberGraph();
        AmberHistory history = new AmberHistory(aGraph);
        
        // try following deleted vertice's edges
        Vertex a = aGraph.addVertex(null);
        Vertex b = aGraph.addVertex(null);
        Vertex c = aGraph.addVertex(null);
        Vertex d = aGraph.addVertex(null);
        Vertex e = aGraph.addVertex(null);
        Vertex f = aGraph.addVertex(null);
        
        a.setProperty("name", "a");
        b.setProperty("name", "b");
        c.setProperty("name", "c");
        d.setProperty("name", "d");
        e.setProperty("name", "e");
        f.setProperty("name", "f");
        
        aGraph.addEdge(null, a, b, "a-b");
        aGraph.addEdge(null, b, c, "b-c");
        aGraph.addEdge(null, c, d, "c-d");
        
        aGraph.addEdge(null, b, e, "b-e");
        aGraph.addEdge(null, e, f, "e-f");
        
        aGraph.commit();

        Long fId = (Long) f.getId();
        
        a.remove();
        b.remove();
        c.remove();
        d.remove();
        e.remove();
        f.remove();
        
        aGraph.commit();
        
        VersionedGraph vg = history.getVersionedGraph();
        
        VersionedVertex newly = vg.getVertex(fId);
        
        List<VersionedVertex> deletedParent = (List<VersionedVertex>) newly.getVertices(Direction.IN, "e-f");
        VersionedVertex parent = deletedParent.get(0);
        assertEquals(parent.getId(), e.getId());
        List<VersionedVertex> deletedGrandParent = (List<VersionedVertex>) parent.getVertices(Direction.IN, "b-e");

        VersionedVertex grandParent = deletedGrandParent.get(0);
        assertEquals(grandParent.getId(), b.getId());
    }

    @Test
    public void getTransactionsAndElements() throws Exception {

        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        v1.setProperty("title", "t1");
        v2.setProperty("title", "t2");

        Edge e1 = graph.addEdge(null, v1, v2, "connect");

        graph.commit();

        v1.setProperty("title", "t1.1");
        v2.setProperty("title", "t2.1");

        graph.commit();

        v1.setProperty("title", "t1.2");

        graph.commit();

        v2.setProperty("title", "t2.2"); // not committed, so should not appear

        List<AmberTransaction> txns = graph.getTransactionsByVertexId((Long) v1.getId());
        assertEquals(3, txns.size());

        txns = graph.getTransactionsByVertexId((Long) v2.getId());
        assertEquals(2, txns.size());

        txns = graph.getTransactionsByEdgeId((Long) e1.getId());
        assertEquals(1, txns.size());

        List<AmberVertex> vertices = graph.getVerticesByTransactionId(txns.get(0).getId());
        assertEquals(2, vertices.size());

        List<AmberEdge> edges = graph.getEdgesByTransactionId(txns.get(0).getId());
        assertEquals(1, edges.size());
    }

}
