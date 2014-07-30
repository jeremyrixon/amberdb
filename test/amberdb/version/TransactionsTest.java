package amberdb.version;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;


import org.junit.rules.TemporaryFolder;

import amberdb.graph.AmberEdge;
import amberdb.graph.AmberGraph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class TransactionsTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    public AmberGraph graph;
    public VersionedGraph vGraph;

    Path tempPath;
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up graph");
        tempPath = Paths.get(tempFolder.getRoot().getAbsolutePath());
        s("amber db located here: " + tempPath + "amber");
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath.toString()+"amber;auto_server=true","sess","sess");
        graph = new AmberGraph(src);
        vGraph = new VersionedGraph(src);
    }

    @After
    public void teardown() {}


    @Test
    public void testTxns2() throws Exception {
        
        // create simple graph
        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        
        v1.setProperty("name", "v1");
        v2.setProperty("name", "v2");
        v3.setProperty("name", "v3");
        
        v1.setProperty("value", 1);
        v2.setProperty("value", 2);
        v3.setProperty("value", 3);
        
        v1.addEdge("links", v2);
        v2.addEdge("links", v3);
        v3.addEdge("links", v1);
        
        // commit
        graph.commit("test", "c1");
        
        // do some things
        v1.setProperty("name", "vertex 1");
        v2.setProperty("value", 100);
        Edge e4 = graph.addEdge(null, v1, v2, "ordered");
        
        // commit
        graph.commit("test", "c2");
        
        // do some more things
        v3.remove();
        e4.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, 99);
        
        // commit
        graph.commit("test", "c2");
        
        vGraph.loadTransactionGraph(0L, 100L);

        assertEquals(((List) vGraph.getVertices()).size(), 3);
        assertEquals(((List) vGraph.getEdges()).size(), 4);
    }        
    
    
    @Test
    public void testTxns1() throws Exception {
        
        // lets try some volume stuff
        String title1 = "Blinky kills again";
        long txn1 = createBook(200, title1);
        
        // modify some bits
        Vertex book = graph.getVertices("title", title1).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("value") % 2 == 0)
                page.setProperty("code", page.hashCode());
            if ((Integer) page.getProperty("value") % 3 == 0)
                page.remove();            
        }
        Long txn2 = graph.commit("test", "modified book 1");
       
        // modify some more ...
        // add 3 pages
        createPage(book, 10);
        createPage(book, 20);
        createPage(book, 40);
        Long txn3 = graph.commit("test", "modified book 1 again");
        
        // make another book
        String title2 = "Blinky rises";
        long txn4 = createBook(200, title2);

        // reorder some pages
        book = graph.getVertices("title", title2).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("value") % 20 == 0) {
                Edge e = page.getEdges(Direction.OUT, "isPartOf").iterator().next();
                e.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, 44);
            }
        }
        Long txn5 = graph.commit("test", "modified book 2");
        
        // now lets check the results
        displayChanges(0L, txn1);
        displayChanges(txn1, txn2+1);
        displayChanges(txn1, txn2);
        displayChanges(txn2, txn3);
        displayChanges(txn3, txn4);
        displayChanges(txn4, txn5);
        displayChanges(txn5, 100000L);
    }        
    
    public static void s(String s) {
        System.out.println(s);
    }
    
    
    private Long createBook(int numPages, String title) {
        
        s("creating book...");
        Vertex book = graph.addVertex(null);
        book.setProperty("type", "Work");
        book.setProperty("title", title);
        
        for (int i = 0; i < numPages; i++) {
            createPage(book, i);
        }
        return graph.commit("test", "create book");
    }
    
    
    private Vertex createPage(Vertex book, int pageNum) {
        Vertex page = graph.addVertex(null);
        
        page.setProperty("name", "page " + pageNum);
        page.setProperty("value", pageNum);
        page.setProperty("type", "Page");
        
        Edge rel = graph.addEdge(null, page, book, "isPartOf");
        rel.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, pageNum);
        
        createCopy(page, "Master");
        createCopy(page, "CoMaster");
        
        return page;
    }

    private Vertex createCopy(Vertex page, String subtype) {
        Vertex copy = graph.addVertex(null);
        copy.setProperty("type", "Copy");
        copy.setProperty("subtype", subtype);
        copy.addEdge("isCopyOf", page);
        createFile(copy);
        return copy;
    }
    
    private Vertex createFile(Vertex copy) {
        Vertex file = graph.addVertex(null);
        file.setProperty("type", "File");
        file.setProperty("path", "/fiddle/de/do");
        file.addEdge("isFileOf", copy);
        return file;
    }
    
    
    private void displayChanges(Long txn1, Long txn2) {
        vGraph.clear();
        s("======== TXN:"+txn1+" - TXN:"+txn2+" ========");
        vGraph.loadTransactionGraph(txn1, txn2);
        s("beep");
        for (VersionedVertex v : vGraph.getVertices()) {
            TVertexDiff diff = v.getDiff(txn1, txn2);
            if (diff.transition != TTransition.UNCHANGED)
                s(diff.toString());
        }
        for (VersionedEdge e : vGraph.getEdges()) {
            TEdgeDiff diff = e.getDiff(txn1, txn2);
            if (diff.transition != TTransition.UNCHANGED)
                s(diff.toString());
        }
    }
}
