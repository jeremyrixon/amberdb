package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;

import amberdb.graph.AmberGraph;
import static amberdb.graph.BranchType.*;

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
        String tempPath = tempFolder.getRoot().getAbsolutePath();
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
        q.branch(Arrays.asList(new String[] {"isPartOf", "belongsTo"}), Direction.BOTH);
        q.branch(Arrays.asList(new String[] {"isCopyOf", "belongsTo"}), Direction.IN);
        q.branch(Arrays.asList(new String[] {"isFileOf", "belongsTo"}), Direction.IN);
        //s(q.generateFullSubGraphQuery());
    }
 
    
    @Test
    public void testExecuteQuery() throws Exception {

        // set up database

        Vertex set = graph.addVertex(null);
        set.setProperty("type", "Set");

        Vertex setPart1 = graph.addVertex(null);
        Vertex setPart2 = graph.addVertex(null);

        setPart1.setProperty("type", "Bit");
        setPart2.setProperty("type", "Bit");
        
        setPart1.addEdge("isPartOf", set);
        setPart2.addEdge("isPartOf", set);
        
        Vertex book1 = makeBook("AA", 50, 10);
        Vertex book2 = makeBook("BB", 50, 0);
        Vertex book3 = makeBook("CC", 30, 10);

        book1.addEdge("isPartOf", set);
        book2.addEdge("isPartOf", set);
        book3.addEdge("isPartOf", set);
        
        graph.commit("bookMaker", "made books");
        
        graph.clear();
        
        List<Long> heads = new ArrayList<Long>();
        heads.add((Long) book1.getId());
        heads.add((Long) book2.getId());
        //heads.add((Long) book3.getId());
        
        AmberQuery q = graph.newQuery(heads);
        q.branch(new String[] {"isPartOf"}, Direction.IN);
        q.branch(new String[] {"isPartOf"}, Direction.IN);
        
        q.branch(BRANCH_FROM_ALL, new String[] {"isCopyOf"}, Direction.IN);
        q.branch(BRANCH_FROM_PREVIOUS, new String[] {"isFileOf"}, Direction.IN);
        q.branch(BRANCH_FROM_PREVIOUS, new String[] {"descriptionOf"}, Direction.IN);
        q.branch(BRANCH_FROM_LISTED, new String[] {"isPartOf"}, Direction.OUT, new Integer[] {0});

        Date then = new Date();
        List<Vertex> results = q.execute(true);
        Date now = new Date();
        //s("MILLIS TO RUN: " + (now.getTime() - then.getTime()));
        
        graph.setLocalMode(true);
        
        
        assertEquals(results.size(), 706);    
        Vertex book = graph.getVertex(book2.getId());
        List<Vertex> pages = (List<Vertex>) book.getVertices(Direction.IN, "isPartOf");
        assertEquals(pages.size(), 51);
    }
    
    
    void s(String s) {
        System.out.println(s);
    }
    
    
    private Vertex makeBook(String title, int numPages, int numProps) {

        Vertex book = graph.addVertex(null);
        book.setProperty("title", title);
        book.setProperty("type", "Work");
        addRandomProps(book, numProps);

        // add a section
        Vertex section = graph.addVertex(null);
        section.setProperty("type", "Section");
        
        for (int i = 0; i < numPages; i++) {
            Vertex page = addPage(book, i, numProps);
            section.addEdge("existsOn", page);
        }

        section.addEdge("isPartOf", book);
        
        return book;
    }
    

    private Vertex addPage(Vertex book, int num, int numProps) {
        Vertex page = graph.addVertex(null);
        page.setProperty("type", "Work");
        page.setProperty("number", num);
        addRandomProps(page, numProps);
        addCopy(page, "Master", num, numProps);
        addCopy(page, "Co-master", num, numProps);
        Edge e = page.addEdge("isPartOf", book);
        e.setProperty("edge-order", num);
        return page;
    }
    
    private Vertex addCopy(Vertex page, String type, int num, int numProps) {
        Vertex copy = graph.addVertex(null);
        copy.setProperty("type", "Copy");
        copy.setProperty("number", num);
        addRandomProps(copy, numProps);
        addFile(copy, num, numProps);
        copy.addEdge("isCopyOf", page);
        return copy;
    }
    
    private Vertex addFile(Vertex copy, int num, int numProps) {
        Vertex file = graph.addVertex(null);
        file.setProperty("type", "File");
        file.setProperty("number", num);
        addRandomProps(file, numProps);
        addDesc(file, num);
        file.addEdge("isFileOf", copy);
        return file;
    }

    private Vertex addDesc(Vertex file, int num) {
        Vertex desc = graph.addVertex(null);
        desc.setProperty("type", "Description");
        desc.setProperty("number", num);
        desc.addEdge("descriptionOf", file);
        return desc;
    }

    
    private void addRandomProps(Vertex v, int numProps) {
        for (int i=0; i<numProps; i++) {
            v.setProperty("prop"+i, UUID.randomUUID().toString());
        }
    }
}
