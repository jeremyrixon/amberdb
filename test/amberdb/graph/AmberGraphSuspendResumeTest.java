package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraphSuspendResumeTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph;
    public AmberGraph graph2;
    
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"amber;auto_server=true","sess","sess");
        graph = new AmberGraph(src);
    }

    @After
    public void teardown() {}

    @Test
    public void testPersistingVertex() throws Exception {
        
        // persist vertex
        Vertex v = graph.addVertex(null);
        v.setProperty("date", new Date());
        
        Object vId = v.getId();
        graph.commit("tester", "testPersistingVertex");
        
        // clear local session
        graph.clear();
        
        // get from persistent data store
        Vertex v2 = graph.getVertex(vId);
        assertEquals(v, v2);
        
        // remove from local session
        v.remove();
        Vertex v3 = graph.getVertex(vId);
        assertNull(v3);
        
        graph.clear();
        
        // get from persistent data store
        Vertex v4 = graph.getVertex(vId);
        assertEquals(v, v4);
        
        // modify and persist
        v4.setProperty("array", new char[] {'1','a'});
        graph.commit("tester", "testModifyAndPersist");
        
        graph.clear();
        
        // get from persistent data store
        Vertex v5 = graph.getVertex(vId);
        assertEquals(v5, v4);
        
        // delete from data store
        v5.remove();
        graph.commit("tester", "removeVertex");

        // get from persistent data store
        Vertex v6 = graph.getVertex(vId);
        assertNull(v6);

    }


    @Test
    public void testAddEdgeToUnchangedVertex() throws Exception {

        // persist vertex
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("date", new Date());

        // persist vertex
        Vertex v2 = graph.addVertex(null);
        v2.setProperty("date", new Date());


        Object vId1 = v1.getId();
        Object vId2 = v2.getId();
        graph.commit("tester", "test");

        // clear local session
        graph.clear();

        // get from persistent data store
        v1 = graph.getVertex(vId1);
        v2 = graph.getVertex(vId2);

        Edge e = graph.addEdge(null, v1, v2, "connects");
        Long eId = (Long) e.getId();
        s(e);

        Long sId = graph.suspend();

        graph.clear();
        graph2 = new AmberGraph(src);
        graph2.resume(sId);

        graph2.setLocalMode(true);

        e = graph2.getEdge(eId);
        assertNotNull(e);
        v1 = e.getVertex(Direction.IN);

        assertNotNull(v1);
        assertNotNull(v2);
    }


    @Test
    public void testSuspendResume() throws Exception {
        
        // persist vertex
        Vertex v = graph.addVertex(null);
        v.setProperty("date", new Date());
        Long vId = (Long) v.getId();
        
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("date", new Date());
        Long v1Id = (Long) v1.getId();

        graph.addEdge(null, v, v1, "link");
        
        Long sessId = graph.suspend();
        
        AmberGraph graph1 = new AmberGraph(src);

        graph1.resume(sessId);
        
        Vertex v2 = graph1.getVertex(vId);
        assertEquals(v2, v); 
        Vertex v3 = graph1.getVertex(v1Id);
        assertEquals(v3, v1); 
    }
    
    
    @Test
    public void testPersistingEdge() throws Exception {
        
        // persist edge
        Vertex v = graph.addVertex(null);
        v.setProperty("name", "ajax");
        v.setProperty("date", new Date());

        Vertex v2 = graph.addVertex(null);
        v2.setProperty("name", "hector");
        v2.setProperty("date", new Date());
        
        Edge e = graph.addEdge(null, v, v2, "foughtWith");
        
        Object eId = e.getId();
        graph.commit("tester", "testPersistingEdge");
        // commit clears the local session
        
        // get from persistent data store
        Edge e2 = graph.getEdge(eId);
        assertEquals(e, e2);
        
        assertEquals(e.getVertex(Direction.OUT), e2.getVertex(Direction.OUT));
        assertEquals(e.getVertex(Direction.IN), e2.getVertex(Direction.IN));
        
        
        // remove from local session
        e2.remove();
        Edge e3 = graph.getEdge(eId);
        assertNull(e3);
        
        graph.clear();
        
        // get from persistent data store
        Edge e4 = graph.getEdge(eId);
        assertEquals(e, e4);
        
        // modify and persist
        e4.setProperty("array", new char[]{'1', 'a'});
        graph.commit("tester", "testModifyAndPersist");
        
        graph.clear(); // double clear :-)
        
        // get from persistent data store
        Edge e5 = graph.getEdge(eId);

        assertEquals(e5, e4);
        
        // delete an incident vertex from data store
        Object remainingVertexId = e5.getVertex(Direction.OUT).getId();
        Object removedVertexId = e5.getVertex(Direction.IN).getId();
        e5.getVertex(Direction.IN).remove();
        graph.commit("tester", "removeVertex");

        // that should have deleted the edge as well, but the other vertex should remain
        assertNull(graph.getEdge(eId));
        assertNull(graph.getVertex(removedVertexId));
        assertNotNull(graph.getVertex(remainingVertexId));
    }

    /*
     * my convenience 
     */
    public void s(Object s) {
        System.out.println(s);
    }
}
