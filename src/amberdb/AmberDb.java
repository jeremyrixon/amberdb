package amberdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import amberdb.model.Section;
import amberdb.model.Work;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

import doss.BlobStore;
import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;

public class AmberDb implements AutoCloseable {
    final FramedGraph<Graph> graph;
    final BlobStore blobStore;
    final Sequence objectIdSeq = new Sequence();
    final Path dataPath;
    final TempDirectory tempDir;

    /**
     * Constructs an in-memory AmberDb for testing with.
     */
    public AmberDb() {
        graph = openGraph(new TinkerGraph());
        dataPath = null;
        tempDir = new TempDirectory();
        tempDir.deleteOnExit();
        blobStore = openBlobStore(tempDir.getPath());
    }

    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberDb(Path dataPath) throws IOException {
        Files.createDirectories(dataPath);
        graph = openGraph(new TinkerGraph(dataPath.resolve("graph").toString(), TinkerGraph.FileType.GML));
        this.dataPath = dataPath;
        tempDir = null;
        blobStore = openBlobStore(dataPath);
        try {
            objectIdSeq.load(dataPath.resolve("objectIdSeq"));
        } catch (NoSuchFileException e) {
            // first run
        }
    }

    private BlobStore openBlobStore(Path root) {
        try {
            return LocalBlobStore.open(root);
        } catch (CorruptBlobStoreException e) {
            try {
                LocalBlobStore.init(root);
            } catch (IOException e2) {
                throw new RuntimeException("Unable to initialize blobstore: " + e2.getMessage(), e2);
            }
            return LocalBlobStore.open(root);
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

    protected FramedGraph<Graph> openGraph(Graph graph) {
        Graph g = new OwnedGraph(graph);
        return new FramedGraphFactory(new JavaHandlerModule(), 
                new GremlinGroovyModule()).create(g);
    }

    /**
     * Finds a work by id.
     */
    public Work findWork(long objectId) {
        Work work = graph.getVertex(objectId, Work.class);
        if (work == null) {
            throw new NoSuchObjectException(objectId);
        }
        return work;
    }

    /**
     * Finds a work by id or alias.
     */
    public Work findWork(String idOrAlias) {
        // @todo aliases
        try {
            return findWork(Long.parseLong(idOrAlias));
        } catch (NumberFormatException e) {
            return findWork(PIUtil.parse(idOrAlias));
        }
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
        Work work = graph.addVertex(null, Work.class);
        return work;
    }

    @Override
    public void close() throws IOException {
        graph.shutdown();
        if (dataPath != null) {
            objectIdSeq.save(dataPath.resolve("objectIdSeq"));
        }
        if (tempDir != null) {
            tempDir.delete();
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
    protected FramedGraph<Graph> getGraph() {
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
    
    /**
     * Wrapper around the graph that stores a reference to the AmberDb which
     * owns it. See {@link AmberDb#ownerOf(Graph)}.
     */
    private class OwnedGraph extends WrappedGraph<Graph> {

        public OwnedGraph(Graph baseGraph) {
            super(baseGraph);
        }
        
        public AmberDb getOwner() {
            return AmberDb.this;
        }

        @Override
        public Vertex addVertex(Object id) {
            /* Workaround a bug in AdjcancyAnnotationHandler in Frames 2.4.0.
             * See https://github.com/tinkerpop/frames/commit/b139067d576
             * Once Frames 2.4.1 is released we may remove this method.
             */
            if (id instanceof Class) {
                id = null;
            }
            return super.addVertex(id);
        }
    }
    
    /**
     * Returns the AmberDb instance that owns the given graph.
     */
    public static AmberDb ownerOf(Graph graph) {
        if (graph instanceof OwnedGraph) {
            return ((OwnedGraph)graph).getOwner();
        } else if (graph instanceof WrapperGraph<?>) {
            return ownerOf(((WrapperGraph<?>)graph).getBaseGraph());
        } else {
            throw new RuntimeException("Not an AmberDb graph: " + graph);
        }
    }
    
    /**
     * Returns the DOSS BlobStore for this AmberDb.
     */
    public BlobStore getBlobStore() {
        return blobStore;
    }
}
