package amberdb.sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;
import org.skife.jdbi.v2.Handle;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberQueryTest {

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

    @Ignore
    @Test
    public void testQueryGeneration() throws Exception {

        List<Long> heads = new ArrayList<Long>();
        heads.add(100L);
        
        AmberQuery q = graph.newQuery(heads);

        q.branch(Arrays.asList(new String[] {"partOf", "belongsTo"}),
                Direction.BOTH);
        
        q.branch(Arrays.asList(new String[] {"isCopyOf", "belongsTo"}),
                Direction.IN);

        q.branch(Arrays.asList(new String[] {"isFileOf", "belongsTo"}),
                Direction.OUT);

        s(q.generateFullSubGraphQuery());
    }
 
    
    @Test
    public void testExecuteQuery() throws Exception {

        // set up database
        
        s("making books...");
        s("Book 1");
        Object book1Id = makeBook("AA", 500, 10);
        s("Book 2");
        Object book2Id = makeBook("BB", 1500, 10);
        s("Book 3");
        Object book3Id = makeBook("CC", 1500, 10);

        s("commiting books to amber");
        graph.commit("bookMaker", "made books");
        s("commited");
        
        List<Long> heads = new ArrayList<Long>();
        heads.add((Long) book1Id);
        heads.add((Long) book2Id);
        heads.add((Long) book3Id);
        
        s("Preparing query...");
        AmberQuery q = graph.newQuery(heads);

        q.branch(Arrays.asList(new String[] {"hasPage"}),
                Direction.OUT);
        
        q.branch(Arrays.asList(new String[] {"hasCopy"}),
                Direction.OUT);

        q.branch(Arrays.asList(new String[] {"hasFile"}),
                Direction.OUT);

        s("Executing query");
        Handle h = graph.dbi().open();
        List<Vertex> results = q.execute();
        h.close();
        
        s("Done " + results.size());
    }
    
    
    void s(String s) {
        System.out.println(s);
    }
    
    
    private Object makeBook(String title, int numPages, int numProps) {
        Vertex book = graph.addVertex(null);
        book.setProperty("title", title);
        addRandomProps(book, numProps);
        
        for (int i=0; i<numPages; i++) {
            Vertex page = graph.addVertex(null);
            page.setProperty("type", "Page");
            addRandomProps(page, numProps);
            
            Vertex copy = graph.addVertex(null);
            copy.setProperty("type", "Copy");
            addRandomProps(copy, numProps);

            Vertex file = graph.addVertex(null);
            file.setProperty("type", "File");
            addRandomProps(file, numProps);

            copy.addEdge("hasFile", file);
            page.addEdge("hasCopy", copy);
            book.addEdge("hasPage", page);
        }
        
        //graph.commit("bookMaker", "made book " + book.getId());
        return book.getId();
    }
    
    private void addRandomProps(Vertex v, int numProps) {
        for (int i=0; i<numProps; i++) {
            v.setProperty("prop"+i, UUID.randomUUID().toString());
        }
    }
}
