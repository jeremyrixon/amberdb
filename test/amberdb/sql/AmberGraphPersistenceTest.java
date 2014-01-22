package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraphPersistenceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph1;
    public AmberGraph graph2;
    public AmberGraph graph3;
    
    DataSource sessionDs1;
    DataSource sessionDs2;
    DataSource sessionDs3;
    DataSource persistentDs;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up graph");
        
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        s("sessions are located here "+tempPath);
        sessionDs1 = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"session1","sess","sess");
        sessionDs2 = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"session2","sess","sess");
        sessionDs3 = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"session3","sess","sess");
        
        persistentDs = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"persist","per","per");
        
        graph1 = new AmberGraph(sessionDs1);
        graph2 = new AmberGraph(sessionDs2);
        graph3 = new AmberGraph(sessionDs3);

    }

    @After
    public void teardown() {}

//    @Test
//    public void testPersistVertex() throws Exception {
//        this.stopWatch();
//        
//        Vertex v1 = graph1.addVertex(null);
//        v1.setProperty("name", "enter the dragon");
//        v1.setProperty("number", 42);
//        s("v1 before persist: " + v1);
//        assertEquals("enter the dragon", v1.getProperty("name"));
//        assertEquals(42, v1.getProperty("number"));
//        
//        graph1.commitToPersistent("Persisting v1");
//        
//        s("v1 after persist: " + v1);
//        assertEquals("session vertex must retain properties after being persisted", "enter the dragon", v1.getProperty("name"));
//        assertEquals("session vertex must retain properties after being persisted", 42, v1.getProperty("number"));
//
//        assertEquals(graph1.getVertex(v1.getId()), v1);
//        // remove the vertex in the session but don't persist
//        v1.remove();
//        assertNull(graph1.getVertex(v1.getId()));
//        
//        // create 2nd session
//        Vertex v2 = graph2.getVertex(v1.getId());
//        s(""+v2);
//        assertNotNull(v2);
//        assertEquals("enter the dragon", v2.getProperty("name"));
//        
//        v2.setProperty("name", "game of death");
//        Vertex v3 = graph2.addVertex(null);
//        v3.addEdge("connect", v3);
//        v3.setProperty("name", "bruce lee");
//        
//        graph2.commitToPersistent("update v2 and connector");
//        
//        
//        // ============== after here needs fixing ================
//        // no assertions dunno even what's happening
//        
//        v2 = graph1.addVertex(null);
//        v2.setProperty("name", "what else");
//        v2.setProperty("number", 25);
//        v2.setProperty("real", false);
//        v2.setProperty("other number", 40.40);
//        
//        try {
//            graph1.addEdge(null, v1, v2, "the connector");
//        } catch (Exception e) {
//            s("Expected ....." + e.getMessage());
//        }
//        
//        // persist the sucker
//        graph1.commitToPersistent("persisting v1 and v2");
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
//        v3 = graph1.getVertex(v1id);
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
//    }
//    
//    @Test
//    public void testGraphPropertiesValuesRemainConsistent() throws Exception {
//        
//        Vertex v = graph1.addVertex(null);
//        v.setProperty("String", "this is a string");
//        v.setProperty("Boolean", true);
//        v.setProperty("Long", 1234567891011L);
//        v.setProperty("Integer", 1234567);
//        v.setProperty("Float", 123456.123456);
//        v.setProperty("Double", 12345678901232354.0d);
//        graph1.commit();
//
//        Vertex v2 = graph2.getVertex(v.getId());
//        assertEquals(v.getProperty("String"),  v2.getProperty("String"));
//        assertEquals(v.getProperty("Boolean"), v2.getProperty("Boolean"));
//        assertEquals(v.getProperty("Long"),    v2.getProperty("Long"));
//        assertEquals(v.getProperty("Integer"), v2.getProperty("Integer"));
//        assertEquals(v.getProperty("Float"),   v2.getProperty("Float"));
//        assertEquals(v.getProperty("Double"),  v2.getProperty("Double"));
//    }
//    
//    @Test
//    public void testSynchMarking() throws Exception {
//        
//        s("Synch marking ---");
//        
//        // save a graph to persist 
//        Long rootId = buildTestGraph(graph1, "red");
//        graph1.commit();
//        
//        // read it back into a new session
//        graph2.updateSynchMark();
//        s("--- synching from txn " + graph2.getSynchMark());
//        Vertex root = graph2.getVertex(rootId);
//        
//        List<Vertex> vs = readTree(root, 5, "branch");
//        assertEquals(9, vs.size());
//        
//        // modify 2 vertices and an edge in the original session and commit
//        Vertex oRoot = graph1.getVertex(rootId);
//        readTree(oRoot, 5, "branch");
//        Vertex n1 = oRoot.getVertices(Direction.OUT, "branch").iterator().next();
//        n1.setProperty("new prop", "a new value");
//        Vertex n2 = n1.getVertices(Direction.OUT, "branch").iterator().next();
//        n2.setProperty("name", "new name");
//        Edge e1 = oRoot.getEdges(Direction.OUT, "branch").iterator().next();
//        e1.setProperty("name", "link 1");
//        graph1.commit();
//
//        // this graph is up to date - its own commit was the last performed
//        Map<String, List<Long>> mutes = graph1.getSynchLists();
//        assertEquals(0, mutes.get("vertex").size());
//        assertEquals(0, mutes.get("edge").size());
//
//        // this graph is 3 elements behind 
//        Map<String, List<Long>> mutes2 = graph2.getSynchLists();
//        assertEquals(2, mutes2.get("vertex").size());
//        assertEquals(1, mutes2.get("edge").size());
//
//        // this graph hasn't pulled anything from persistence - nothing to synch yet 
//        Map<String, List<Long>> mutes3 = graph3.getSynchLists();
//        assertEquals(0, mutes3.get("vertex").size());
//        assertEquals(0, mutes3.get("edge").size());
//
//        // make sure graph 1 is aware of "Leaf red 5" and all its edges
//        graph1.getVertices("name", "Leaf 5 red").iterator().next();
//        
//        // graph 3 to delete a leaf
//        Vertex del = graph3.getVertices("name", "Leaf 5 red").iterator().next();
//        del.remove();
//        graph3.commit();
//
//        // this graph is 2 updates behind now - removal of a vertex and its edge
//        mutes = graph1.getSynchLists();
//        assertEquals(1, mutes.get("vertex").size());
//        assertEquals(1, mutes.get("edge").size());
//
//        // this graph is now 5 elements behind 
//        mutes2 = graph2.getSynchLists();
//        assertEquals(3, mutes2.get("vertex").size());
//        assertEquals(2, mutes2.get("edge").size());
//
//        // this graph should be up to date 
//        mutes3 = graph3.getSynchLists();
//        assertEquals(0, mutes3.get("vertex").size());
//        assertEquals(0, mutes3.get("edge").size());
//
//    }
//
//    @Test
//    public void testGetEdgesWithProperty() throws Exception {
//        
//        s("Edges with property ---");
//        
//        // save a graph to persist
//        Vertex v = graph1.addVertex(null);
//        
//        Edge e = v.addEdge("e1", graph1.addVertex(null));
//        e.setProperty("string", "value1");
//        e.setProperty("int", 5);
//
//        Edge e2 = v.addEdge("e2", graph1.addVertex(null));
//        e2.setProperty("string", "value1");
//        e2.setProperty("int", 10);
//
//        Edge e3 = v.addEdge("e3", graph1.addVertex(null));
//        e3.setProperty("string", "value2");
//        e3.setProperty("int", 10);
//        
//        graph1.commit();
//
//        // now add some session edges just for fun
//        Edge e4 = v.addEdge("e4", graph1.addVertex(null));
//        e4.setProperty("string", "value1");
//        e4.setProperty("int", 5);
//
//        Edge e5 = v.addEdge("e5", graph1.addVertex(null));
//        e5.setProperty("string", "value3");
//        e5.setProperty("int", 8);
//
//        Edge e6 = v.addEdge("e6", graph1.addVertex(null));
//        e6.setProperty("string", "value2");
//        e6.setProperty("int", 10);
//        
//        // ok let's get testing
//        
//        // find edges with a particular string property 
//        List<Edge> edges = Lists.newArrayList(graph1.getEdges("string", "value1"));
//        s("edges with property 'string' = 'value1'");
//        for (Edge edge : edges) {
//            s("" + edge);
//        }
//        assertEquals(3, edges.size());
//        assertTrue(edges.contains(e));
//        assertTrue(edges.contains(e2));
//        assertTrue(edges.contains(e4));
//        
//        // now try getting by the int properties to be sure
//        edges = Lists.newArrayList(graph1.getEdges("int", 10));
//        s("edges with property 'int' = 10");
//        for (Edge edge : edges) {
//            s("" + edge);
//        }
//        assertEquals(3, edges.size());
//        assertTrue(edges.contains(e2));
//        assertTrue(edges.contains(e3));
//        assertTrue(edges.contains(e6));        
//    }
//
//    @Test
//    public void testGetVerticesWithProperty() throws Exception {
//        
//        s("Vertices with property ---");
//        
//        // save a graph to persist
//        Vertex v0 = graph1.addVertex(null);
//        v0.setProperty("string", "v1");
//        
//        Vertex v1 = graph1.addVertex(null);
//        v1.setProperty("string", "v3");
//
//        Vertex v2 = graph1.addVertex(null);
//        v2.setProperty("string", "v1");
//
//        Vertex v3 = graph1.addVertex(null);
//        v3.setProperty("string", "v1");
//
//        Vertex v4 = graph1.addVertex(null);
//        v4.setProperty("string2", "v1");
//
//        // persist that sucker
//        graph1.commit();
//        
//        // add a couple more
//        Vertex v5 = graph1.addVertex(null);
//        v5.setProperty("string", "v1");
//
//        Vertex v6 = graph1.addVertex(null);
//        v6.setProperty("string2", "v1");
//
//        // ok let's get testing
//        
//        // find vertices with a particular string property 
//        List<Vertex> vertices = Lists.newArrayList(graph1.getVertices("string", "v1"));
//        s("vertices with property 'string' = 'v1'");
//        for (Vertex v : vertices) {
//            s("" + v);
//        }
//        assertEquals(4, vertices.size());
//        assertTrue(vertices.contains(v0));
//        assertTrue(vertices.contains(v2));
//        assertTrue(vertices.contains(v3));
//        assertTrue(vertices.contains(v5));
//        
//        // now try deleting a vertex and a property
//        v5.remove();
//        v2.removeProperty("string");
//        
//        vertices = Lists.newArrayList(graph1.getVertices("string", "v1"));
//        s("vertices with property 'string' = 'v1'");
//        for (Vertex v : vertices) {
//            s("" + v);
//        }
//        assertEquals(2, vertices.size());
//        assertTrue(vertices.contains(v0));
//        assertTrue(vertices.contains(v3));
//    }
//    
//    /*
//     * Following 3 methods ripped directly from tinkerpop blueprint testing framework
//     */
//    double timer = -1.0d;
//    public double stopWatch() {
//        if (this.timer == -1.0d) {
//            this.timer = System.nanoTime() / 1000000.0d;
//            return -1.0d;
//        } else {
//            double temp = (System.nanoTime() / 1000000.0d) - this.timer;
//            this.timer = -1.0d;
//            return temp;
//        }
//    }
//    public void printPerformance(String name, Integer events, String eventName, double timeInMilliseconds) {
//        if (null != events)
//            System.out.println("\t" + name + ": " + events + " " + eventName + " in " + timeInMilliseconds + "ms");
//        else
//            System.out.println("\t" + name + ": " + eventName + " in " + timeInMilliseconds + "ms");
//    }
//    public void printTestPerformance(String testName, double timeInMilliseconds) {
//        System.out.println("*** TOTAL TIME [" + testName + "]: " + timeInMilliseconds + " ***");
//    }
//
    /*
     * my convenience 
     */
    public void s(String s) {
        System.out.println(s);
    }

//    /* 
//    
//           Root
//            /\
//           /  \
//          /    \
//         /      \
//        n1      n2
//       / \      / \
//      /   \    /   \
//     l1   l2  n3   l3
//             / \
//            /   \
//          l4    l5
//    
//    */
//    
//    private Long buildTestGraph(AmberGraph graph, String name) {
//        
//        Vertex root = graph.addVertex(null);
//        root.setProperty("name", "Root " + name);
//        root.setProperty("type", "tree");
//        root.setProperty("value", 100);
//        
//        Vertex node1 = graph.addVertex(null);
//        node1.setProperty("name", "Node 1 " + name);
//        node1.setProperty("type", "node");
//        node1.setProperty("value", true);
//
//        Vertex node2 = graph.addVertex(null);
//        node2.setProperty("name", "Node 2 " + name);
//        node2.setProperty("type", "node");
//        node2.setProperty("value", false);
//
//        Vertex node3 = graph.addVertex(null);
//        node3.setProperty("name", "Node 3 " + name);
//        node3.setProperty("type", "node");
//        node3.setProperty("value", false);
//
//        Vertex leaf1 = graph.addVertex(null);
//        leaf1.setProperty("name", "Leaf 1 " + name);
//        leaf1.setProperty("type", "leaf");
//        leaf1.setProperty("value", "leafy");
//        
//        Vertex leaf2 = graph.addVertex(null);
//        leaf2.setProperty("name", "Leaf 2 " + name);
//        leaf2.setProperty("type", "leaf");
//        leaf2.setProperty("value", "leafy leafy");
//
//        Vertex leaf3 = graph.addVertex(null);
//        leaf3.setProperty("name", "Leaf 3 " + name);
//        leaf3.setProperty("type", "leaf");
//        leaf3.setProperty("value", "leafy beefy");
//
//        Vertex leaf4 = graph.addVertex(null);
//        leaf4.setProperty("name", "Leaf 4 " + name);
//        leaf4.setProperty("type", "leaf");
//        leaf4.setProperty("value", "leafy lefty");
//
//        Vertex leaf5 = graph.addVertex(null);
//        leaf5.setProperty("name", "Leaf 5 " + name);
//        leaf5.setProperty("type", "leaf");
//        leaf5.setProperty("value", "leafy laughter");
//
//        root.addEdge("branch", node1);
//        root.addEdge("branch", node2);
//        node2.addEdge("branch", node3);
//        node1.addEdge("branch", leaf1);
//        node1.addEdge("branch", leaf2);
//        node2.addEdge("branch", leaf3);
//        node3.addEdge("branch", leaf4);
//        node3.addEdge("branch", leaf5);
//        
//        return (Long) root.getId();
//    }
//    
//    private List<Vertex> readTree(Vertex root, int maxDepth, String label) {
//        List<Vertex> tree = new ArrayList<Vertex>();
//        readTree(root, tree, 0, maxDepth, label);
//        return tree;
//    }
//    private List<Vertex> readTree(Vertex root, List<Vertex> tree, int depth, int maxDepth, String label) {
//        if (depth >= maxDepth) return tree;
//        
//        s("reading tree at depth "+depth);
//        tree.add(root);
//        List<Vertex> vs = (List<Vertex>) root.getVertices(Direction.OUT, label);
//        if (vs.size() > 0) {
//            for (Vertex v : vs) {
//                readTree(v, tree, depth+1, maxDepth, label);
//            }
//        }
//        return tree;
//    }
}
