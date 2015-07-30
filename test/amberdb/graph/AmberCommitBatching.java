package amberdb.graph;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import amberdb.graph.AmberGraph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberCommitBatching {

    public AmberGraph graph;
    DataSource src;
    
    @Before
    public void setup() throws Exception {
        src = JdbcConnectionPool.create("jdbc:h2:mem:","sess","sess");
        graph = new AmberGraph(src);
    }

    @After
    public void teardown() {}

    /* Ignore following test in regular testing as it takes a long time */     
    @Ignore
    @Test
    public void testCommitBatching() throws Exception {

        initTimer();
        
        // set up database
        Vertex[] v = new Vertex[3000];
        
        for (int i = 0; i < 3000; i++) {
            v[i] = graph.addVertex(null);
            addRandomProps(v[i], 5);
            v[i].setProperty("name", ((Integer) i).toString());
        }
        mark("created 3000 vertices");
        
        for (int i = 0; i < 2999; i++) {
            v[i].addEdge("connect", v[i+1]);
        }
        mark("joined vertices");
        graph.commit();
        mark("committed graph");

        graph.clear();
        mark("cleared graph");
        
    }
    
    Date then, now;
    private void initTimer() { then = new Date(); }
    private void mark(String msg) {
        Date now = new Date();
        System.out.println("-- " + msg + " : " + (now.getTime() - then.getTime()) + "ms");
        then = now;
    }
    
    private void addRandomProps(Vertex v, int numProps) {
        for (int i=0; i<numProps; i++) {
            v.setProperty("prop"+i, UUID.randomUUID().toString());
        }
    }
}
