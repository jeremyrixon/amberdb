package amberdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import amberdb.model.Section;
import amberdb.model.Work;


import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public class AmberDb implements AutoCloseable {
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
            objectIdSeq.load(dataPath.resolve("objectIdSeq"));
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
     * Finds a work by voyager number.
     */
    public Section findWorkByVn(long vnLink) {
        return graph.frame(graph.getVertices("bibId", vnLink).iterator().next(), Section.class);
    }

    /**
     * Creates a new work.
     * 
     * @return the work
     */
    public Work addWork() {
        return graph.addVertex(null, Work.class);
    }

    @Override
    public void close() throws IOException {
        graph.shutdown();
        if (dataPath != null) {
            objectIdSeq.save(dataPath.resolve("objectIdSeq"));
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
    
    private static class Sequence {
        final AtomicLong value = new AtomicLong();

        void load(Path file) throws IOException {
            byte[] b = Files.readAllBytes(file);
            value.set(Long.valueOf(new String(b)));
        }

        void save(Path file) throws IOException {
            Files.write(file, Long.toString(value.get()).getBytes());
        }

        long next() {
            return value.incrementAndGet();
        }
    }
}
