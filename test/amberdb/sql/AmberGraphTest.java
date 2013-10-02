package amberdb.sql;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;

import com.tinkerpop.blueprints.EdgeTestSuite;
import com.tinkerpop.blueprints.GraphTestSuite;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TestSuite;
import com.tinkerpop.blueprints.VertexTestSuite;

public class AmberGraphTest extends com.tinkerpop.blueprints.impls.GraphTest {

    public AmberGraph graph;
    
    public void setup() throws MalformedURLException, IOException {
        
        System.out.println("Setting up graph");

//        DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:~/h2testSession","sess","sess");
        DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:mem:","sess","sess");
        DataSource persistentDs = JdbcConnectionPool.create("jdbc:h2:~/h2testPersist","persist","persist");
        
//        MysqlDataSource ds = new MysqlDataSource();
//        ds.setUser("dlir");
//        ds.setPassword("dlir");
//        ds.setServerName("localhost");
//        ds.setPort(3306);
//        ds.setDatabaseName("dlir");
        
        graph = new AmberGraph(sessionDs, null, "tester");
    }

    public static void teardown() {}

    public void testVertexTestSuite() throws Exception {
        setup();
        this.stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
        teardown();
    }

    public void testEdgeTestSuite() throws Exception {
        setup();
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
        teardown();
    }

    public void testGraphTestSuite() throws Exception {
        setup();
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
        teardown();
    }

    // public void testKeyIndexableGraphTestSuite() throws Exception {
    // this.stopWatch();
    // doTestSuite(new KeyIndexableGraphTestSuite(this));
    // printTestPerformance("KeyIndexableGraphTestSuite", this.stopWatch());
    // }
    //
    // public void testIndexableGraphTestSuite() throws Exception {
    //     this.stopWatch();
    //     doTestSuite(new IndexableGraphTestSuite(this));
    //     printTestPerformance("IndexableGraphTestSuite", this.stopWatch());
    // }
    //
    //
    // public void testIndexTestSuite() throws Exception {
    // this.stopWatch();
    // doTestSuite(new IndexTestSuite(this));
    // printTestPerformance("IndexTestSuite", this.stopWatch());
    // }
    //
    // public void testGraphMLReaderTestSuite() throws Exception {
    // this.stopWatch();
    // doTestSuite(new GraphMLReaderTestSuite(this));
    // printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
    // }
    //
    // public void testGMLReaderTestSuite() throws Exception {
    // this.stopWatch();
    // doTestSuite(new GMLReaderTestSuite(this));
    // printTestPerformance("GMLReaderTestSuite", this.stopWatch());
    // }
    //
    // public void testGraphSONReaderTestSuite() throws Exception {
    // this.stopWatch();
    // doTestSuite(new GraphSONReaderTestSuite(this));
    // printTestPerformance("GraphSONReaderTestSuite", this.stopWatch());
    // }


    @Override
    public Graph generateGraph(String s) {
        try {
            setup();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Setup failed!");
        }
        return graph;
    }
    @Override
    public Graph generateGraph() {
        return generateGraph("this string's purpose is unknown to me");
    }
    
    public void doTestSuite(final TestSuite testSuite) throws Exception {
        String doTest = System.getProperty("testAmberGraph");
        if (doTest == null || doTest.equals("true")) {
            for (Method method : testSuite.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("test")) {
                    System.out.println("Testing " + method.getName() + "...");
                    method.invoke(testSuite);
                }
            }
        }
    }
    
    public void testVertexQuery() throws Exception {
        setup();
        assertNotNull(graph.addVertex(null).query());
    }
}