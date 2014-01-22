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


public class AmberGraphSuspendResumeTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph;
    
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        
    	System.out.println("Setting up graph");
        
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        s("amber db located here: " + tempPath);
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"amber","sess","sess");
        graph = new AmberGraph(src);

    }

    @After
    public void teardown() {}

	@Test
	public void testPersistVertex() throws Exception {

		Vertex v1 = graph.addVertex(null);
		v1.setProperty("name", "enter the dragon");
		v1.setProperty("number", 42);
		s("v1 is: " + v1);
		
		Vertex v2 = graph.addVertex(null);
		v2.setProperty("name", "game of death");
		v2.setProperty("number", 99);
		s("v2 is: " + v2);
		
		Edge e = graph.addEdge(null, v1, v2, "similar");
		s("e is: " + e);
		
		Long sessId = graph.suspend();

		s("session id is: " + sessId);
	}

    /*
     * my convenience 
     */
    public void s(String s) {
        System.out.println(s);
    }

    /* 
    
           Root
            /\
           /  \
          /    \
         /      \
        n1      n2
       / \      / \
      /   \    /   \
     l1   l2  n3   l3
             / \
            /   \
          l4    l5
    
    */
    
    private Long buildTestGraph(AmberGraph graph, String name) {
        
        Vertex root = graph.addVertex(null);
        root.setProperty("name", "Root " + name);
        root.setProperty("type", "tree");
        root.setProperty("value", 100);
        
        Vertex node1 = graph.addVertex(null);
        node1.setProperty("name", "Node 1 " + name);
        node1.setProperty("type", "node");
        node1.setProperty("value", true);

        Vertex node2 = graph.addVertex(null);
        node2.setProperty("name", "Node 2 " + name);
        node2.setProperty("type", "node");
        node2.setProperty("value", false);

        Vertex node3 = graph.addVertex(null);
        node3.setProperty("name", "Node 3 " + name);
        node3.setProperty("type", "node");
        node3.setProperty("value", false);

        Vertex leaf1 = graph.addVertex(null);
        leaf1.setProperty("name", "Leaf 1 " + name);
        leaf1.setProperty("type", "leaf");
        leaf1.setProperty("value", "leafy");
        
        Vertex leaf2 = graph.addVertex(null);
        leaf2.setProperty("name", "Leaf 2 " + name);
        leaf2.setProperty("type", "leaf");
        leaf2.setProperty("value", "leafy leafy");

        Vertex leaf3 = graph.addVertex(null);
        leaf3.setProperty("name", "Leaf 3 " + name);
        leaf3.setProperty("type", "leaf");
        leaf3.setProperty("value", "leafy beefy");

        Vertex leaf4 = graph.addVertex(null);
        leaf4.setProperty("name", "Leaf 4 " + name);
        leaf4.setProperty("type", "leaf");
        leaf4.setProperty("value", "leafy lefty");

        Vertex leaf5 = graph.addVertex(null);
        leaf5.setProperty("name", "Leaf 5 " + name);
        leaf5.setProperty("type", "leaf");
        leaf5.setProperty("value", "leafy laughter");

        root.addEdge("branch", node1);
        root.addEdge("branch", node2);
        node2.addEdge("branch", node3);
        node1.addEdge("branch", leaf1);
        node1.addEdge("branch", leaf2);
        node2.addEdge("branch", leaf3);
        node3.addEdge("branch", leaf4);
        node3.addEdge("branch", leaf5);
        
        return (Long) root.getId();
    }
    
    private List<Vertex> readTree(Vertex root, int maxDepth, String label) {
        List<Vertex> tree = new ArrayList<Vertex>();
        readTree(root, tree, 0, maxDepth, label);
        return tree;
    }
    private List<Vertex> readTree(Vertex root, List<Vertex> tree, int depth, int maxDepth, String label) {
        if (depth >= maxDepth) return tree;
        
        s("reading tree at depth "+depth);
        tree.add(root);
        List<Vertex> vs = (List<Vertex>) root.getVertices(Direction.OUT, label);
        if (vs.size() > 0) {
            for (Vertex v : vs) {
                readTree(v, tree, depth+1, maxDepth, label);
            }
        }
        return tree;
    }
}
