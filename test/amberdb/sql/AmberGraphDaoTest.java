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

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;


public class AmberGraphDaoTest {

    public static DBI dbi = null;
    
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up database");

        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser("dlir");
        ds.setPassword("dlir");
        ds.setServerName("localhost");
        ds.setPort(3306);
        ds.setDatabaseName("dlir");
        
        dbi = new DBI(ds);
        AmberGraphDao dao = dbi.open(AmberGraphDao.class);
        
        dao.dropTables();
        dao.createVertexTable();
        dao.createEdgeTable();
        dao.createEdgePropertyTable();
        dao.createEdgePropertyTableIndex();
        dao.createVertexPropertyTable();
        dao.createVertexPropertyTableIndex();
        dao.createTransactionTable();
        
        dao.close();
    }

    
    public static void teardown() {
        AmberGraphDao dao = dbi.open(AmberGraphDao.class);
        dao.dropTables();
        dao.close();
        dbi = null;
    }

    @Test
    public void testDao() throws Exception {
        setup();
        
        AmberGraphDao dao = dbi.onDemand(AmberGraphDao.class);

        long v1 = dao.createVertex(3l, "{}", "nla.obj-12234");        
        long v2 = dao.createVertex(3l, "{}", "nla.obj-12235");
        
        long e1 = dao.createEdge(3l, "{}", v1,  v2, "test");
        dao.updateEdgeProperties(e1, "{\"order\" : 12}");
        
        long e2 = dao.createEdge(3l, "{}", v1,  v2, "test2");
        dao.removeEdge(e2);

        Iterator<AmberEdge> ije2 = dao.findOutEdgesByVertexId(v1);
        while (ije2.hasNext()) {
            s("it:"+ije2.next().toString());
        }
        
        ije2 = dao.findInEdgesByVertexId(v2);
        while (ije2.hasNext()) {
            s("it2:"+ije2.next().toString());
        }

        ije2 = dao.findAllEdges();
        while (ije2.hasNext()) {
            s("it3:"+ije2.next().toString());
        }
        s("");
        dao.createVertex(3l, "{pants : shirt}", "nla.obj-333");
        
        dao.close();
        teardown();
    }

    @Test
    public void testDaoIndexes() throws Exception {
        setup();
        
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
        teardown();
    }
    
    @Test
    public void testGraph() throws Exception {
        setup();
        
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
        }
        
        s("");
        
        List<Vertex> sortedVertices = ((AmberVertex) vp).getVertices(Direction.OUT, "hasPage");

        for (Vertex v : sortedVertices) {
            s(v.toString());
        }

        teardown();

    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
