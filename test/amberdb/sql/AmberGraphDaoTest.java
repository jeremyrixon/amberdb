package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import amberdb.sql.dao.PersistentDao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;


public class AmberGraphDaoTest {

    public static DBI dbi = null;
    public static final String dsUrl = "jdbc:h2:~/h2test2";
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up database");

        DataSource ds = JdbcConnectionPool.create(dsUrl,"dlir","dlir");
        
//        MysqlDataSource ds = new MysqlDataSource();
//        ds.setUser("dlir");
//        ds.setPassword("dlir");
//        ds.setServerName("localhost");
//        ds.setPort(3306);
//        ds.setDatabaseName("dlir");
        
        dbi = new DBI(ds);
        PersistentDao dao = dbi.open(PersistentDao.class);
        
        //dao.dropTables();
        //dao.createIdGeneratorTable();
        //dao.createVertexTable();
        //dao.createEdgeTable();
        //dao.createPropertyTable();
        //dao.createPropertyIndex();
        //dao.createTransactionTable();
        
        dao.close();
    }

    @After
    public void teardown() {
        PersistentDao dao = dbi.open(PersistentDao.class);
        //dao.dropTables();
        dao.close();
        dbi = null;
    }

    // @Test
    @Ignore
    public void testDao() throws Exception {
        //setup();
        
        PersistentDao dao = dbi.onDemand(PersistentDao.class);

        long v1 = dao.insertVertex(12, 0, 0);        
        long v2 = dao.insertVertex(3, 1, 0);
        
        long e1 = dao.insertEdge(4, 1, 0, 12, 3, "test", 0);
//        dao.updateEdgeProperties(e1, "{\"order\" : 12}");
        
        long e2 = dao.insertEdge(2, 1, 0, 3,  12, "backwards", 1);
        dao.removeEdge(e2);

        Iterator<AmberEdge> ije2 = dao.findOutEdges(v1);
        while (ije2.hasNext()) {
            s("it:"+ije2.next().toString());
        }
        
        ije2 = dao.findInEdges(v2);
        while (ije2.hasNext()) {
            s("it2:"+ije2.next().toString());
        }

        ije2 = dao.findEdges();
        while (ije2.hasNext()) {
            s("it3:"+ije2.next().toString());
        }
        s("");
        dao.insertVertex(3, 12, 13);
        
        dao.close();
        //teardown();
    }

    // @Test
    @Ignore
    public void testDaoIndexes() throws Exception {
        //setup();
        
        AmberGraph graph = new AmberGraph(dbi);

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
        //teardown();
    }
    
    // @Test
    @Ignore
    public void testGraph() throws Exception {
        //setup();
        
        AmberGraph graph = new AmberGraph(dbi);
        
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
        
        vp.setProperty("type", "book");
        v1.setProperty("type", "page");
        v2.setProperty("type", "page");
        v3.setProperty("type", "page");
        v4.setProperty("type", "page");
        v5.setProperty("type", "page");
        v6.setProperty("type", "page");
        v7.setProperty("type", "page");
        v8.setProperty("type", "page");
        v9.setProperty("type", "page");
        v10.setProperty("type", "page");
        v11.setProperty("type", "page");
        
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
        
    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
