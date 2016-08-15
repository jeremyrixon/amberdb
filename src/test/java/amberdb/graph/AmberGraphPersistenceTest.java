package amberdb.graph;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;


public class AmberGraphPersistenceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph1;
    public AmberGraph graph2;
    public AmberGraph graph3;
    
    DataSource ds;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        ds = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"persist","per","per");
        
        graph1 = new AmberGraph(ds);
        graph2 = new AmberGraph(ds);
        graph3 = new AmberGraph(ds);

    }

    @After
    public void teardown() {}

    @Test
    public void testPersistVertex() throws Exception {
        this.stopWatch();
        
        Vertex v1 = graph1.addVertex(null);
        v1.setProperty("name", "enter the dragon");
        v1.setProperty("number", 42);
        Assert.assertEquals("enter the dragon", v1.getProperty("name"));
        Assert.assertEquals(42, v1.getProperty("number"));
        
        graph1.commit("tester", "Persisting v1");
        
        Assert.assertEquals("session vertex must retain properties after being persisted", "enter the dragon", v1.getProperty("name"));
        Assert.assertEquals("session vertex must retain properties after being persisted", 42, v1.getProperty("number"));

        Assert.assertEquals(graph1.getVertex(v1.getId()), v1);

        v1.remove();
        Assert.assertNull(graph1.getVertex(v1.getId()));
        
        // create 2nd session
        Vertex v2 = graph2.getVertex(v1.getId());
        Assert.assertNotNull(v2);
        Assert.assertEquals("enter the dragon", v2.getProperty("name"));
        
        v2.setProperty("name", "game of death");

        Vertex v3 = graph2.addVertex(null);
        v3.addEdge("connect", v3);
        v3.setProperty("name", "bruce lee");
        
        graph2.commit("tester","update v2 and connector");
        
        Vertex v4 = graph1.getVertex(v3.getId());
        Assert.assertEquals(v4, v3);

        Vertex v5 = graph1.getVertex(v2.getId());
        Assert.assertNull(v5); // v1 has been removed in graph1, and v2 has same id as v1

        Vertex v6 = graph2.getVertex(v1.getId());
        Assert.assertNotNull(v6);
        
        graph1.shutdown();
        
        graph3 = new AmberGraph(ds);
        
        v3 = graph3.getVertex(v1.getId());
        Assert.assertNotNull(v6);
    }
    
    @Test
    public void testGraphPropertiesValuesRemainConsistent() throws Exception {
        
        Vertex v = graph1.addVertex(null);
        v.setProperty("String", "this is a string");
        v.setProperty("Boolean", true);
        v.setProperty("Long", 1234567891011L);
        v.setProperty("Integer", 1234567);
        v.setProperty("Float", 123456.123456);
        v.setProperty("Double", 12345678901232354.0d);
        graph1.commit();

        Vertex v2 = graph1.getVertex(v.getId());
        Assert.assertEquals(v.getProperty("String"),  v2.getProperty("String"));
        Assert.assertEquals(v.getProperty("Boolean"), v2.getProperty("Boolean"));
        Assert.assertEquals(v.getProperty("Long"),    v2.getProperty("Long"));
        Assert.assertEquals(v.getProperty("Integer"), v2.getProperty("Integer"));
        Assert.assertEquals(v.getProperty("Float"),   v2.getProperty("Float"));
        Assert.assertEquals(v.getProperty("Double"),  v2.getProperty("Double"));
    }

    @Test
    public void testGetEdgesWithProperty() throws Exception {
        
        // save a graph to persist
        Vertex v = graph1.addVertex(null);
        
        Edge e = v.addEdge("e1", graph1.addVertex(null));
        e.setProperty("string", "value1");
        e.setProperty("int", 5);

        Edge e2 = v.addEdge("e2", graph1.addVertex(null));
        e2.setProperty("string", "value1");
        e2.setProperty("int", 10);

        Edge e3 = v.addEdge("e3", graph1.addVertex(null));
        e3.setProperty("string", "value2");
        e3.setProperty("int", 10);
        
        graph1.commit();

        // now add some session edges just for fun
        Edge e4 = v.addEdge("e4", graph1.addVertex(null));
        e4.setProperty("string", "value1");
        e4.setProperty("int", 5);

        Edge e5 = v.addEdge("e5", graph1.addVertex(null));
        e5.setProperty("string", "value3");
        e5.setProperty("int", 8);

        Edge e6 = v.addEdge("e6", graph1.addVertex(null));
        e6.setProperty("string", "value2");
        e6.setProperty("int", 10);
        
        // ok let's get testing
        
        // find edges with a particular string property 
        List<Edge> edges = Lists.newArrayList(graph1.getEdges("string", "value1"));
        Assert.assertEquals(3, edges.size());
        Assert.assertTrue(edges.contains(e));
        Assert.assertTrue(edges.contains(e2));
        Assert.assertTrue(edges.contains(e4));
        
        // now try getting by the int properties to be sure
        edges = Lists.newArrayList(graph1.getEdges("int", 10));
        Assert.assertEquals(3, edges.size());
        Assert.assertTrue(edges.contains(e2));
        Assert.assertTrue(edges.contains(e3));
        Assert.assertTrue(edges.contains(e6));
    }

    @Test
    public void testGetVerticesWithProperty() throws Exception {
        
        // save a graph to persist
        Vertex v0 = graph1.addVertex(null);
        v0.setProperty("string", "v1");
        
        Vertex v1 = graph1.addVertex(null);
        v1.setProperty("string", "v3");

        Vertex v2 = graph1.addVertex(null);
        v2.setProperty("string", "v1");

        Vertex v3 = graph1.addVertex(null);
        v3.setProperty("string", "v1");

        Vertex v4 = graph1.addVertex(null);
        v4.setProperty("string2", "v1");

        // persist that sucker
        graph1.commit();
        
        // add a couple more
        Vertex v5 = graph1.addVertex(null);
        v5.setProperty("string", "v1");

        Vertex v6 = graph1.addVertex(null);
        v6.setProperty("string2", "v1");

        // ok let's get testing
        
        // find vertices with a particular string property 
        List<Vertex> vertices = Lists.newArrayList(graph1.getVertices("string", "v1"));
        Assert.assertEquals(4, vertices.size());
        Assert.assertTrue(vertices.contains(v0));
        Assert.assertTrue(vertices.contains(v2));
        Assert.assertTrue(vertices.contains(v3));
        Assert.assertTrue(vertices.contains(v5));
        
        // now try deleting a vertex and a property
        v5.remove();
        v2.removeProperty("string");
        
        vertices = Lists.newArrayList(graph1.getVertices("string", "v1"));
        Assert.assertEquals(2, vertices.size());
        Assert.assertTrue(vertices.contains(v0));
        Assert.assertTrue(vertices.contains(v3));
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
            s("\t" + name + ": " + events + " " + eventName + " in " + timeInMilliseconds + "ms");
        else
            s("\t" + name + ": " + eventName + " in " + timeInMilliseconds + "ms");
    }
    public void printTestPerformance(String testName, double timeInMilliseconds) {
        s("*** TOTAL TIME [" + testName + "]: " + timeInMilliseconds + " ***");
    }

    /*
     * my convenience 
     */
    public void s(String s) {
        System.out.println(s);
    }
}
