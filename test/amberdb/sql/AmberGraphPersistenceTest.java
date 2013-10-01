package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraphPersistenceTest {

    public AmberGraph graph;
    DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:~/h2tests","sess","sess");
    DataSource persistentDs = JdbcConnectionPool.create("jdbc:h2:~/h2testp","per","per");
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        
        System.out.println("Setting up graph");

        graph = new AmberGraph(sessionDs, persistentDs, "tester");
    }

    @After
    public void teardown() {}

    @Test
    public void testPersistVertex() throws Exception {

        // start with a clear persistence db
        graph.createPersistentDataStore();
        
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("name", "enter the dragon");
        v1.setProperty("number", 50);
        v1.setProperty("real", true);
        v1.setProperty("other number", 20.20);
        
        Vertex v2 = graph.addVertex(null);
        v2.setProperty("name", "what else");
        v2.setProperty("number", 25);
        v2.setProperty("real", false);
        v2.setProperty("other number", 40.40);
        
        Edge e1 = graph.addEdge(null, v1, v2, "the connector");
        
        // persist the sucker
        graph.commitToPersistent("persisting v1 and v2");

        s("v1 b==== " + v1);
        s("v2 b==== " + v2);

        // make some changes
        v1.setProperty("name", "lava lamp");
        v1.setProperty("number", 234349);
        v1.setProperty("real", false);
        v1.setProperty("other number", 77710.10);

        v2.remove();
        
        s("v1 a==== " + v1);
        s("v2 a==== " + v2);
        
        // persist the sucker
        graph.commitToPersistent("persisting v1 & v2 a second time");

        // just building on previous persisted 
        Long v1id = (Long) v1.getId();

        Vertex v3 = graph.getVertex(v1id);
        
        s("v3 is : "+ v3);
        s("v1 is : "+ v1);

        graph.shutdown();
        
        graph = new AmberGraph(sessionDs, persistentDs, "tester");
        
        v3 = graph.getVertex(v1id);
        s("v3 is again : "+ v3);
    
        Iterable<Vertex> v4 = graph.getVertices("name", "lava lamp");
        s("her we go");
        for (Vertex v : v4) {
            s("and we found " + v);
        }
        // some assertions
        //assert
        
        //graph.shutdown();
    }
    
    @Test
    public void testGraph() throws Exception {
        
        s("just waiting for a test yeah");
        
    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
