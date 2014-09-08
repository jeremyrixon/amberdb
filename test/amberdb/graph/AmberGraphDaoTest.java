package amberdb.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import amberdb.enums.SubType;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;


public class AmberGraphDaoTest {

    public AmberGraph graph;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up graph");
        graph = new AmberGraph();
    }

    @After
    public void teardown() {}

    @Test
    public void testDaoIndexes() throws Exception {

        Vertex v1 = graph.addVertex("nla.obj-12231");
        Vertex v2 = graph.addVertex("nla.obj-12232");
        Vertex v3 = graph.addVertex("nla.obj-12233");
        Vertex v4 = graph.addVertex("nla.obj-12234");
        Vertex v5 = graph.addVertex("nla.obj-12235");
        
        v1.setProperty("key1", "abcd");
        v2.setProperty("key1", "abcd");
        v3.setProperty("key1", "abcd");
        v4.setProperty("key1", "abcd");
        v5.setProperty("key1", "111abcd");
        v1.setProperty("key2", 14);
        v2.setProperty("key2", 14);
        v3.setProperty("key2", 14);
        v4.setProperty("key2", 14);
        v5.setProperty("key2", 14);
        v1.setProperty("key3", "abcd");

        Edge e1 = v1.addEdge("books", v2);
        Edge e2 = v1.addEdge("books", v3);
        Edge e3 = v1.addEdge("books", v4);
        Edge e4 = v1.addEdge("books", v5);
        
        e1.setProperty("k1", 3.2);
        e2.setProperty("k1", 3.2);
        e3.setProperty("k1", 2.2);
        e4.setProperty("k1", 1.2);
        
        s("------");
        Iterable<Edge> es = graph.getEdges("k1", 3.2);
        for (Edge e: es) {
            s(e.toString());
        }
        s("------");
        Iterable<Vertex> vs = graph.getVertices("key1", "abcd");
        for (Vertex v: vs) {
            s(v.toString());
        }
        
        graph.shutdown();
    }
    
    @Test
    public void testGraph() throws Exception {
        
        Vertex vp = graph.addVertex(null);
        
        Vertex v1 = graph.addVertex(null); 
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        Vertex v4 = graph.addVertex(null);
        Vertex v5 = graph.addVertex(null);
        Vertex v6 = graph.addVertex(null);
        Vertex v7 = graph.addVertex(null);
        Vertex v8 = graph.addVertex(null);
        Vertex v9 = graph.addVertex(null);
        Vertex v10 = graph.addVertex(null);
        Vertex v11 = graph.addVertex(null);
        
        vp.setProperty("type", SubType.BOOK.code());
        v1.setProperty("type", SubType.PAGE.code());
        v2.setProperty("type", SubType.PAGE.code());
        v3.setProperty("type", SubType.PAGE.code());
        v4.setProperty("type", SubType.PAGE.code());
        v5.setProperty("type", SubType.PAGE.code());
        v6.setProperty("type", SubType.PAGE.code());
        v7.setProperty("type", SubType.PAGE.code());
        v8.setProperty("type", SubType.PAGE.code());
        v9.setProperty("type", SubType.PAGE.code());
        v10.setProperty("type", SubType.PAGE.code());
        v11.setProperty("type", SubType.PAGE.code());
        
        Edge e1 = graph.addEdge(null, vp, v1, "hasPage");
        Edge e2 = graph.addEdge(null, vp, v2, "hasPage");
        Edge e3 = graph.addEdge(null, vp, v3, "hasPage");
        Edge e4 = graph.addEdge(null, vp, v4, "hasPage");
        Edge e5 = graph.addEdge(null, vp, v5, "hasPage");
        Edge e6 = graph.addEdge(null, vp, v6, "hasPage");
        Edge e7 = graph.addEdge(null, vp, v7, "hasPage");
        Edge e8 = graph.addEdge(null, vp, v8, "hasPage");
        Edge e9 = graph.addEdge(null, vp, v9, "hasPage");
        Edge e10 = graph.addEdge(null, vp, v10, "hasPage");
        Edge e11 = graph.addEdge(null, vp, v11, "hasPage");
        
        Edge[] edges = new Edge[] {e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11};
        for (int i=0; i < edges.length; i++) {
            edges[i].setProperty("edge-order", 20-i);
        }

        Vertex[] vertices = new Vertex[] {v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11};
        for (int i=0; i < vertices.length; i++) {
            vertices[i].setProperty("page", ""+i);
        }

        for (int i=0; i < edges.length; i++) {
            s(edges[i].toString());
            s(vertices[i].toString());
            s("");
        }
        
        s("");
        
        List<Vertex> sortedVertices = (List<Vertex>) ((AmberVertex) vp).getVertices(Direction.OUT, "hasPage");

        for (Vertex v : sortedVertices) {
            s(v.toString());
        }

        List<Edge> sortedEdges = (List<Edge>) ((AmberVertex) vp).getEdges(Direction.OUT, "hasPage");

        for (Edge e : sortedEdges) {
            s(e.toString());
        }

        Iterable<Vertex> vs = graph.getVertices();
        for (Vertex v: vs) {
            s("----  "+ v);
        }
        
        e7.remove();
        e8.remove();
        e9.remove();
        e10.remove();
    }
    
    @Test
    public void updateEdgeOrder() throws Exception {

        Vertex vp = graph.addVertex(null);
        
        Vertex v1 = graph.addVertex(null); 
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        Vertex v4 = graph.addVertex(null);

        Edge e1 = graph.addEdge(null, v1, vp, "isPartOf");
        Edge e2 = graph.addEdge(null, v2, vp, "isPartOf");
        Edge e3 = graph.addEdge(null, v3, vp, "isPartOf");
        Edge e4 = graph.addEdge(null, v4, vp, "isPartOf");
        
        ((AmberVertex) vp).setEdgeOrder(v1, "isPartOf", Direction.IN, 1);
        ((AmberVertex) vp).setEdgeOrder(v2, "isPartOf", Direction.IN, 2);
        ((AmberVertex) vp).setEdgeOrder(v3, "isPartOf", Direction.IN, 3);
        ((AmberVertex) vp).setEdgeOrder(v4, "isPartOf", Direction.IN, 4);
        
        assertEquals(1, e1.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(2, e2.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(3, e3.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(4, e4.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));

        ((AmberVertex) vp).setEdgeOrder(v1, "isPartOf", Direction.IN, 4);
        ((AmberVertex) vp).setEdgeOrder(v2, "isPartOf", Direction.IN, 3);
        ((AmberVertex) vp).setEdgeOrder(v3, "isPartOf", Direction.IN, 2);
        ((AmberVertex) vp).setEdgeOrder(v4, "isPartOf", Direction.IN, 1);
        
        assertEquals(4, e1.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(3, e2.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(2, e3.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(1, e4.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
