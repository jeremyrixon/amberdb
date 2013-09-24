package amberdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import amberdb.model.Item;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.Sequence;
import amberdb.model.Work;
import amberdb.relation.ExistsOn;
import amberdb.relation.IsPartOf;
import amberdb.relation.Relation;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public class AmberDb extends Sequence implements AutoCloseable {
    final FramedGraph<TinkerGraph> graph;
    final Sequence objectIdSeq = new Sequence();
    final Path dataPath;

    /**
     * Constructs an in-memory AmberDb for testing with.
     */
    public AmberDb() {
        graph = openGraph(new TinkerGraph());
        dataPath = null;
    }

    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberDb(Path dataPath) throws IOException {
        Files.createDirectories(dataPath);
        graph = openGraph(new TinkerGraph(dataPath.resolve("graph").toString(), TinkerGraph.FileType.GML));
        this.dataPath = dataPath;
        try {
            load(dataPath.resolve("objectIdSeq"));
        } catch (NoSuchFileException e) {
            // first run
        }
    }

    /**
     * commit saves everything in the current transaction.
     */
    public void commit() {
        // TODO: saves everything in the current transaction.
    }

    /**
     * rollback rollback everything in the current transaction.
     */
    public void rollback() {
        // TODO: saves everything in the current transaction.
    }
    
    public JsonNode serializeToJson() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GraphSONWriter.outputGraph(graph.getBaseGraph(), bos);
        return new ObjectMapper().reader().readTree(bos.toString());     
    }

    protected static FramedGraph<TinkerGraph> openGraph(TinkerGraph tinker) {
        return new FramedGraphFactory(new JavaHandlerModule(), new GremlinGroovyModule()).create(tinker);
    }

    /**
     * Finds a work by id.
     */
    public Work findWork(long objectId) {
        return graph.getVertex(objectId, Work.class);
    }

    /**
     * Finds a section by id.
     */
    public Section findSection(long objectId) {
        return graph.getVertex(objectId, Section.class);
    }

    /**
     * Finds a section by Bib id.
     */
    public Section findSectionByVn(long vnLink) {
        return graph.frame(graph.getVertices("bibId", vnLink).iterator().next(), Section.class);
    }

    /**
     * Creates a new work.
     * 
     * @return the work
     */
    public Work addWork() {
        return graph.addVertex(objectIdSeq.next(), Work.class);
    }

    /**
     * Create a new section.
     */
    public Section addSection() {
        Section section = graph.addVertex(objectIdSeq.next(), Section.class);
        section.setSubType("work");
        return section;
    }

    @Override
    public void close() throws IOException {
        graph.shutdown();
        if (dataPath != null) {
            save(dataPath.resolve("objectIdSeq"));
        }
    }

    /**
     * Suspend suspends the current transaction.
     * 
     * @return the id of the current transaction.
     */
    public int suspend() {
        // TODO
        return -1;
    }

    /**
     * Recover recovers the suspended transaction of the specified txId.
     * 
     * @param txId
     *            the transaction id of the suspended transaction to reover.
     */
    public void recover(int txId) {
        // TODO
    }

    /**
     * getGraph
     * 
     * @return the framed graph, handly for groovy tests.
     */
    protected FramedGraph<TinkerGraph> getGraph() {
        return graph;
    }
}
