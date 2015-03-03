package amberdb.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
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


import com.tinkerpop.blueprints.Vertex;


public class AmberPropertyQueryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph;
    
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
    public void testOrQueryGeneration() throws Exception {

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

        AmberVertexQuery avq = graph.newVertexQuery();
        avq.addCriteria(aps);

        List<Vertex> results = avq.execute();
        
        assertEquals(6, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v5));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v8));
        assertTrue(results.remove(v10));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }

    @Test
    public void testAndQueryGeneration() throws Exception {

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
        v2.setProperty("PNAMEN", "PVALUEN");

        v3.setProperty("PNAME2", new Boolean(false));
        v4.setProperty("PNAME2", "Something different");
        v5.setProperty("PNAME2", new Boolean(true));
        
        v6.setProperty("PNAME1", "PVALUE1");
        v6.setProperty("PNAMEN", "PVALUEN");

        v7.setProperty("PNAME2", "PVALUE1");
        
        v8.setProperty("PNAME3", new Integer(3));
        v9.setProperty("PNAME3", new Integer(0));

        v10.setProperty("PNAME1", "PVALUE1");
        v10.setProperty("PNAMEN", "PVALUEN");
        v10.setProperty("PNAME2", new Boolean(true));
        v10.setProperty("PNAME3", "xxxxXXXXxxxx");
        
        graph.commit("tester", "saving some vertices with properties");
        
        AmberProperty p1 = new AmberProperty(0, "PNAME1", "PVALUE1");
        AmberProperty p2 = new AmberProperty(0, "PNAMEN", "PVALUEN");
        
        List<AmberProperty> aps = new ArrayList<AmberProperty>();
        aps.add(p1);
        aps.add(p2);
        
        AmberVertexQuery avq = graph.newVertexQuery();
        avq.combineCriteriaWithAnd();
        avq.addCriteria(aps);
        
        List<Vertex> results = avq.execute();
        
        assertEquals(3, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }
    
    @Test
    public void testNullValueInCriteriaAreIgnored() throws Exception {

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
        AmberProperty p3 = new AmberProperty(0, "PNAME3", null);
        
        List<AmberProperty> aps = new ArrayList<AmberProperty>();
        aps.add(p1);
        aps.add(p2);
        aps.add(p3);

        AmberVertexQuery avq = graph.newVertexQuery();
        avq.addCriteria(aps);

        List<Vertex> results = avq.execute();
        
        assertEquals(5, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v5));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v10));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }


    @Test
    public void testJsonListValueQueries() throws Exception {

        Vertex v1 = graph.addVertex(null);
        v1.setProperty("json-list", "[\"abba\",\"beta\",\"delta\",\"gama\"]");

        Vertex v2 = graph.addVertex(null);
        v2.setProperty("json-list", "[\"babba\",\"beta\",\"delta\",\"gama\"]");

        Vertex v3 = graph.addVertex(null);
        v3.setProperty("json-list", "[\"beta\",\"delta\",\"gama\", \"abba\"]");

        graph.commit("tester", "saving some vertices with properties");

        AmberVertexQuery avq = graph.newVertexQuery();
        List<Vertex> results = avq.executeJsonValSearch("json-list", "abba");

        assertEquals(2, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v3));
    }

    
    void s(String s) {
        System.out.println(s);
    }
}
