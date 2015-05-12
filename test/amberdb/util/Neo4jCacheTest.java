package amberdb.util;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import com.google.common.collect.Iterables;

public class Neo4jCacheTest {
    private GraphDatabaseService graphDb;
    private static enum RelTypes implements RelationshipType {
        IsPartOf;
    }
    
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void init() throws IOException {
        graphDb = new Neo4jCache(folder.getRoot().getAbsolutePath()).graph();
    }
    
    @After
    public void cleanup() {
        graphDb.shutdown();
    }
    
    @Test
    public void testAddData() {
        add2Records();
        try (Transaction tx = graphDb.beginTx()) {
            Iterable<Node> nodes = GlobalGraphOperations.at(graphDb).getAllNodes();
            assertEquals(2, Iterables.size(nodes));
        }
    }
    
    @Test
    public void testRemoveData() {
        removeAllData();
        try (Transaction tx = graphDb.beginTx()) {
            Iterable<Node> nodes = GlobalGraphOperations.at(graphDb).getAllNodes();
            assertEquals(0, Iterables.size(nodes));
        }
    }
    
    private void add2Records() {
        Node book;
        Node page;
        Relationship relationship;
        
        try (Transaction tx = graphDb.beginTx()) {
            book = graphDb.createNode();
            book.setProperty("title", "Blinky Bill");
            page = graphDb.createNode();
            page.setProperty("title", "New arrivals");
            relationship = book.createRelationshipTo(page, RelTypes.IsPartOf);
            relationship.setProperty("order", 1);
            tx.success();
        }
    }
    
    private void removeAllData() {
        try (Transaction tx = graphDb.beginTx()) {
            Iterable<Node> nodes = GlobalGraphOperations.at(graphDb).getAllNodes();
            for (Node node : nodes) {
                node.delete();
            }
            tx.success();
        }
    }
}
