package amberdb.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberPropertyQueryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph;
    
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up graph");
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        s("amber db located here: " + tempPath + "amber");
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"amber;auto_server=true","sess","sess");
        graph = new AmberGraph(src);
    }

    @After
    public void teardown() {}

    @Test
    public void testQueryGeneration() throws Exception {

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
        
        v1.setProperty("PNAME1", "PVALUE1");
        v1.setProperty("PNAMEN", "PVALUEN");

        v2.setProperty("PNAME1", "PVALUE2");

        v3.setProperty("PNAME2", new Boolean(false));
        v4.setProperty("PNAME2", "Something different");
        v5.setProperty("PNAME2", new Boolean(true));
        
        v6.setProperty("PNAME1", "PVALUE1");
        v7.setProperty("PNAME2", "PVALUE1");
        
        v8.setProperty("PNAME3", new Integer(3));
        v9.setProperty("PNAME3", new Integer(0));

        v10.setProperty("PNAME1", "PVALUE1");
        v10.setProperty("PNAME2", new Boolean(true));
        v10.setProperty("PNAME3", "xxxxXXXXxxxx");
        
        graph.commit("tester", "saving some vertices with properties");
        
        AmberProperty p1 = new AmberProperty(0, "PNAME1", "PVALUE1");
        AmberProperty p2 = new AmberProperty(0, "PNAME2", new Boolean(true));
        AmberProperty p3 = new AmberProperty(0, "PNAME3", new Integer(3));
        
        List<AmberProperty> aps = new ArrayList<AmberProperty>();
        aps.add(p1);
        aps.add(p2);
        aps.add(p3);
        
        AmberVertexPropertyQuery avpq = new AmberVertexPropertyQuery(aps, graph);
        
        //s(avpq.generatePropertyQuery());
        
        List<Vertex> results = avpq.execute();
        
        //s(""+results);
        //s("number matched: "+results.size());
        assertEquals(6, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v5));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v8));
        assertTrue(results.remove(v10));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }
    
    void s(String s) {
        System.out.println(s);
    }
}
