package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import com.tinkerpop.blueprints.Vertex;


public class AmberGraphPersistenceTest {

    public AmberGraph graph;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        
        System.out.println("Setting up graph");

        DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:~/h2testSession","sess","sess");
        DataSource persistentDs = JdbcConnectionPool.create("jdbc:h2:~/h2testPersist","persist","persist");
      
        graph = new AmberGraph(sessionDs, persistentDs, "tester");
    }

    @After
    public void teardown() {}

    @Test
    public void testPersistVertex() throws Exception {

        // start with a clear persistence db
        graph.createPersistentDataStore();
        
        Vertex v1 = graph.addVertex("nla.obj-12231");
        v1.setProperty("name", "lava");
        v1.setProperty("number", 50);
        v1.setProperty("real", true);
        v1.setProperty("other number", 20.20);
        
        // persist the sucker
        graph.commitToPersistent("persisting v1");

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
