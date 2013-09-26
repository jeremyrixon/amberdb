package amberdb.sql;

import amberdb.sql.dao.*;
import amberdb.sql.map.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import com.tinkerpop.blueprints.EdgeTestSuite;
import com.tinkerpop.blueprints.GraphTestSuite;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TestSuite;
import com.tinkerpop.blueprints.VertexTestSuite;

public class AmberGraphTest extends com.tinkerpop.blueprints.impls.GraphTest {

    public static DBI dbi = null;
    public static final String dsUrl = "jdbc:h2:~/h2test";
    
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up database");

        DataSource ds = JdbcConnectionPool.create(dsUrl,"fish","finger");
        
//        MysqlDataSource ds = new MysqlDataSource();
//        ds.setUser("dlir");
//        ds.setPassword("dlir");
//        ds.setServerName("localhost");
//        ds.setPort(3306);
//        ds.setDatabaseName("dlir");
        
        dbi = new DBI(ds);
        PersistentDao dao = dbi.open(PersistentDao.class);
        
        dao.dropTables();
        dao.createVertexTable();
        dao.createEdgeTable();
        dao.createPropertyTable();
        //dao.createPropertyIndex();
        dao.createIdGeneratorTable();
        dao.createTransactionTable();

        dao.close();
    }

    public static void teardown() {
        PersistentDao dao = dbi.open(PersistentDao.class);
        dao.dropTables();
        dao.close();
        dbi = null;
    }

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
        return new AmberGraph(dbi);
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
}