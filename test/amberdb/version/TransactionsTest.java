package amberdb.version;

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
    public void testTransactions() throws Exception {
        
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
        //e4.setProperty("some other property", 99);
        
        // commit
        graph.commit("test", "c2");
        
        vGraph.loadTransactionGraph(0L, 100L);
        
        for (VersionedVertex v : vGraph.getVertices()) s(""+v);
        for (VersionedEdge e : vGraph.getEdges()) s(""+e);
        
        // lets try some volume stuff
        Vertex book = graph.addVertex(null);
        List<Vertex> pages = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Vertex page = graph.addVertex(null);
            page.setProperty("name", "page " + i);
            page.setProperty("value", i);
            Edge rel = graph.addEdge(null, page, book, "isPartOf");
            rel.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, i);
            pages.add(page);
        }
        Long txnCommit = graph.commit("test", "commited book");
        s("Commit Book Txn " + txnCommit);
        
        // modify some bits
        for (Vertex page : pages) {
            if ((Integer) page.getProperty("value") % 2 == 0)
                page.setProperty("type", page.hashCode());
            if ((Integer) page.getProperty("value") % 10 == 0)
                page.remove();
        }
        Long txnModify = graph.commit("test", "modified book");
        s("Modify Book Txn " + txnModify);
       
        
        vGraph.clear();
        s("======== COMMIT ==============");
        vGraph.loadTransactionGraph(txnCommit);
        
        for (VersionedVertex v : vGraph.getVertices()) s(""+v);
        for (VersionedEdge e : vGraph.getEdges()) s(""+e);
        
        vGraph.clear();
        s("======== MODIFY ==============");
        vGraph.loadTransactionGraph(txnModify);
        
        for (VersionedVertex v : vGraph.getVertices()) s(""+v);
        for (VersionedEdge e : vGraph.getEdges()) s(""+e);
        
    }        
    
    
    public static void s(String s) {
        System.out.println(s);
    }
}
