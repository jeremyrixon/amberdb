package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraphPersistenceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph1;
    public AmberGraph graph2;

    DataSource sessionDs1;
    DataSource sessionDs2;
    DataSource persistentDs;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up graph");
        
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        sessionDs1 = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"session1","sess","sess");
        sessionDs2 = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"session2","sess","sess");
        
        persistentDs = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"persist","per","per");

        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser("dlir");
        ds.setPassword("dlir");
        ds.setServerName("snowy.nla.gov.au");
        ds.setPort(6446);
        ds.setDatabaseName("dlir");
        
        graph1 = new AmberGraph(sessionDs1, ds, "tester");
        graph2 = new AmberGraph(sessionDs2, ds, "tester2");

        graph1.createPersistentSchema();

    }

    @After
    public void teardown() {}

    @Test
    public void testPersistVertex() throws Exception {
        this.stopWatch();
        
        Vertex v1 = graph1.addVertex(null);
        v1.setProperty("name", "enter the dragon");
        v1.setProperty("number", 42);
        s("v1 before persist: " + v1);
        assertEquals("enter the dragon", v1.getProperty("name"));
        assertEquals(42, v1.getProperty("number"));
        
        graph1.commitToPersistent("Persisting v1");
        
        s("v1 after persist: " + v1);
        assertEquals("session vertex must retain properties after being persisted", "enter the dragon", v1.getProperty("name"));
        assertEquals("session vertex must retain properties after being persisted", 42, v1.getProperty("number"));

        assertEquals(graph1.getVertex(v1.getId()), v1);
        // remove the vertex in the session but don't persist
        v1.remove();
        assertNull(graph1.getVertex(v1.getId()));
        
        // create 2nd session
        Vertex v2 = graph2.getVertex(v1.getId());
        s(""+v2);
        assertNotNull(v2);
        assertEquals("enter the dragon", v2.getProperty("name"));
        
        v2.setProperty("name", "game of death");
        Vertex v3 = graph2.addVertex(null);
        v3.addEdge("connect", v3);
        v3.setProperty("name", "bruce lee");
        
        graph2.commitToPersistent("update v2 and connector");
        
        
        // ============== after here needs fixing ================
        
//        Vertex v2 = graph1.addVertex(null);
//        v2.setProperty("name", "what else");
//        v2.setProperty("number", 25);
//        v2.setProperty("real", false);
//        v2.setProperty("other number", 40.40);
//        
//        Edge e1 = graph1.addEdge(null, v1, v2, "the connector");
//        
//        // persist the sucker
//        graph1.commitToPersistent("persisting v1 and v2");
//
////        s("v1 b==== " + v1); // has been removed already
//        s("v2 b==== " + v2);
//
//        // make some changes
//        v1.setProperty("name", "lava lamp");
//        v1.setProperty("number", 234349);
//        v1.setProperty("real", false);
//        v1.setProperty("other number", 77710.10);
//
//        v2.remove();
//        
//        s("v1 a==== " + v1);
//        s("v2 a==== " + v2);
//        
//        // persist the sucker
//        graph1.commitToPersistent("persisting v1 & v2 a second time");
//
//        // just building on previous persisted 
//        Long v1id = (Long) v1.getId();
//
//        Vertex v3 = graph1.getVertex(v1id);
//        
//        s("v3 is : "+ v3);
//        s("v1 is : "+ v1);
//
//        graph1.shutdown();
//        
//        graph1 = new AmberGraph(sessionDs1, persistentDs, "tester");
//        
//        v3 = graph1.getVertex(v1id);
//        s("v3 is again : "+ v3);
//    
//        Iterable<Vertex> v4 = graph1.getVertices("name", "lava lamp");
//        s("her we go");
//        for (Vertex v : v4) {
//            s("and we found " + v);
//        }
        // some assertions
        //assert
        
        //graph.shutdown();
    }
    
    @Test
    public void testGraph() throws Exception {
        
        s("just waiting for a test yeah");
        
    }
    
    /*
     * Following 3 methods ripped directly from tinkerpop blueprint testing framework
     */
    double timer = -1.0d;
    public double stopWatch() {
        if (this.timer == -1.0d) {
            this.timer = System.nanoTime() / 1000000.0d;
            return -1.0d;
        } else {
            double temp = (System.nanoTime() / 1000000.0d) - this.timer;
            this.timer = -1.0d;
            return temp;
        }
    }
    public void printPerformance(String name, Integer events, String eventName, double timeInMilliseconds) {
        if (null != events)
            System.out.println("\t" + name + ": " + events + " " + eventName + " in " + timeInMilliseconds + "ms");
        else
            System.out.println("\t" + name + ": " + eventName + " in " + timeInMilliseconds + "ms");
    }
    public void printTestPerformance(String testName, double timeInMilliseconds) {
        System.out.println("*** TOTAL TIME [" + testName + "]: " + timeInMilliseconds + " ***");
    }

    /*
     * my convenience 
     */
    public void s(String s) {
        System.out.println(s);
    }
}
