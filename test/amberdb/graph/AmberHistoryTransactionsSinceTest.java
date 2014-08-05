package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberHistory;
import amberdb.graph.AmberVertex;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;

import com.google.common.collect.Lists;
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
        System.out.println("Setting up graph");
        tempPath = Paths.get(tempFolder.getRoot().getAbsolutePath());
        s("amber db located here: " + tempPath + "amber");
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath.toString()+"amber;auto_server=true","sess","sess");
        graph = new AmberGraph(src);
    }

    @After
    public void teardown() {}


    @Test
    public void testPersistingVertex() throws Exception {
        
        Date now = new Date();
        s("TS:" + now.getTime());
        
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
        Map<Long, String> vSince = ah.modifiedSince(now);
        assertEquals(100, vSince.size());
        //for (Long key : vSince.keySet()) { s(""+key+":"+vSince.get(key)); }

        now = new Date();
        s("TS:" + now.getTime());
        
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
        assertEquals(0, ah.modifiedSince(now).size());
        
        // don't forget to commit
        graph.commit("test", "modified 80");
        
        vSince = ah.modifiedSince(now);
        
        int vNew = 0;
        int vMod = 0;
        int vDel = 0;
        
        for (String state : vSince.values()) {
            if (state.equals("NEW")) {
                vNew++;
            } 
            if (state.equals("MOD")) {
                vMod++;
            }
            if (state.equals("DEL")) {
                vDel++;
            }
        }

        for (Long key : vSince.keySet()) { s("id:"+key+" state:"+vSince.get(key)); }

        s("NEW:" + vNew);
        s("MOD:" + vMod);
        s("DEL:" + vDel);
        
        assertEquals(10, vNew);
        assertEquals(20, vMod);
        assertEquals(50, vDel);

        now = new Date();
        s("TS:" + now.getTime());
        
        // now add some edges
//        for (int i = 50; i < 60; i++) {
//            graph.addEdge(null, graph.getVertex(vertIds.get(i)), graph.getVertex(vertIds.get(i+10)), "link"+i);
//        }
//        graph.commit("test", "rel test");    
//
//        vSince = ah.modifiedSince(now);
//        for (Long key : vSince.keySet()) { s(""+key+":"+vSince.get(key)); }
//        assertEquals(20, vSince.size());
    }        

    
    @Test
    public void testTransactions() throws Exception {
        
        
        AmberSession sess = new AmberDb(src, tempPath).begin();
        AmberGraph aGraph = sess.getAmberGraph();
        AmberHistory history = new AmberHistory(aGraph);

        Date now = new Date();
        s("TS:" + now.getTime());
        
        // Set up work
        Work w = sess.addWork();
        w.setTitle("Testing for fun and profit");
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

        // Comment this out since this doesn't just retrieve work.
        // Check we get all the bits of the work we want
//        Map<Long, String> changed = sess.getModifiedVertexIds(now);
//        List<Long> ids = Lists.newArrayList(changed.keySet());
//        Collections.sort(ids);
//        for (Long id : ids) {
//            Work wrk = sess.findWork(id);
//            System.out.println("id:" + id + " status:" + changed.get(id) + " work:" + wrk);
//        }
        
        now = new Date();
        s("\nTS:" + now.getTime());

        // Modify work by updating title, adding a page
        w.setTitle("Testing a new title");
        Page p = w.addPage();
        p.setTitle("new page");
        p.setOrder(0);
        
        sess.commit("test", "change title add page");

        aGraph.clear();
        
//        changed = sess.getModifiedVertexIds(now);
//        ids = Lists.newArrayList(changed.keySet());
//        Collections.sort(ids);
//        for (Long id : ids) {
//            Work wrk = sess.findWork(id);
//            System.out.println("id:" + id + " status:" + changed.get(id) + " work:" + wrk);
//        }
        
        
        now = new Date();
        s("\nTS:" + now.getTime());

        // Modify work by deleting 2 pages
        w.setTitle("Testing a new title");

        p = w.getPage(12);
//        System.out.println("deleting page: " + p);
//        System.out.println("\tcopy: " + p.getCopy(CopyRole.ACCESS_COPY));
        sess.deletePage(p);

        p = w.getPage(3);
//        System.out.println("deleting page: " + p);
//        System.out.println("\tcopy: " + p.getCopy(CopyRole.ACCESS_COPY));
        sess.deletePage(p);

        p = w.getPage(5);
//        System.out.println("deleting page: " + p);
//        System.out.println("\tcopy: " + p.getCopy(CopyRole.ACCESS_COPY));
        sess.deletePage(p);
        
        sess.commit("test", "deleted 3 pages");

        aGraph.clear();
        
        
        changed = sess.getModifiedVertexIds(now);
        ids = Lists.newArrayList(changed.keySet());
        Collections.sort(ids);
        for (Long id : ids) {
            Work wrk;
            try {
                wrk = sess.findWork(id);
            } catch (Exception e) {
                wrk = null;
            }
            System.out.println("id:" + id + " status:" + changed.get(id) + " work:" + history.getLastVertex(id));
        }
        assertEquals(16, changed.size()); // 1 for title modification, 3 x 5 per page (1 page, 2 copies and 2 files) deletions

        for (Long id : ids) {
            List<AmberVertex> hist = history.getHistory(id);
            System.out.println("--");
            for (AmberVertex v : hist) {
                System.out.println("" + v);
            }    
        }
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
        
        Edge ab = aGraph.addEdge(null, a, b, "a-b");
        Edge bc = aGraph.addEdge(null, b, c, "b-c");
        Edge cd = aGraph.addEdge(null, c, d, "c-d");
        
        Edge be = aGraph.addEdge(null, b, e, "b-e");
        Edge ef = aGraph.addEdge(null, e, f, "e-f");
        
        aGraph.commit();

        Date now = new Date();
        s("\nTS:" + now.getTime());
        
        s("||"+ a + "\n" + b + "\n" + c + "\n" + d + "\n" + e + "\n" + f + "\n");
        
        Long fId = (Long) f.getId();
        
        a.remove();
        b.remove();
        c.remove();
        d.remove();
        e.remove();
        f.remove();
        
        aGraph.commit();
        
        Vertex newly = history.getLastVertex(fId);
        
        List<Long> deletedParent = history.followLastEdges((Long) newly.getId(), "e-f", Direction.IN);
        Long parentId = deletedParent.get(0);
        Vertex parent = history.getLastVertex(parentId);
        s("parent " + parent);
        assertEquals(parent, e);
        List<Long> deletedGrandParent = history.followLastEdges(parentId, "b-e", Direction.IN);
        Vertex grandParent = history.getLastVertex(deletedGrandParent.get(0));
        assertEquals(grandParent, b);
        s("grand parent " + grandParent);
    }        
    
    
    public static void s(String s) {
        System.out.println(s);
    }
}
