package amberdb;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.h2.Driver;
import org.h2.jdbcx.JdbcConnectionPool;

import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.ImageFile;
import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.Work;
import amberdb.sql.AmberGraph;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;
import com.tinkerpop.frames.modules.typedgraph.TypedGraphModuleBuilder;

import doss.BlobStore;
import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;


public class AmberSession implements AutoCloseable {
    private final FramedGraph<TransactionalGraph> graph;
    private final BlobStore blobStore;
    private final TempDirectory tempDir;
    private long sessionId;

    /**
     * Constructs an in-memory AmberDb for testing with.
     */
    public AmberSession() {
        tempDir = new TempDirectory();
        tempDir.deleteOnExit();
        
        // DOSS
        blobStore = openBlobStore(tempDir.getPath());
        
        // Graph
        DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:mem:", "fish", "fish");
        DataSource persistDs = JdbcConnectionPool.create("jdbc:h2:" + tempDir.getPath().resolve("graph"), "pers", "pers");
        AmberGraph amber = new AmberGraph(sessionDs, persistDs, "amberdb");
        amber.createPersistentSchema();
        graph = openGraph(amber);
    }

    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberSession(Path dataPath) throws IOException {
        DataSource persistDs = JdbcConnectionPool.create("jdbc:h2:" + dataPath.resolve("graph"), "pers", "pers");
        AmberGraph amber = init(persistDs, "pers", "pers", dataPath, dataPath.resolve("session"), sessionId);
        tempDir = null;
        
        // DOSS
        blobStore = openBlobStore(dataPath);

        // Graph
        graph = openGraph(amber);
    }
    
    public AmberSession(DataSource dataSource, Path dataPath, Path sessionPath, long sessionId) {
        AmberGraph amber = init(dataSource, "fish", "fish", dataPath, sessionPath, sessionId);
        tempDir = null;
        
        // DOSS
        blobStore = openBlobStore(dataPath);

        // Graph
        graph = openGraph(amber);     
    }
    
    private AmberGraph init(DataSource dataSource, String user, String password, Path dataPath, Path sessionPath, long sessionId) {
        try {
            Files.createDirectories(dataPath);

            DriverManager.registerDriver(new Driver());

            // Graph
            DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:" + sessionPath, user, password);
            AmberGraph amber = new AmberGraph(sessionDs, dataSource, "amberdb");
            amber.createPersistentSchema();

            this.sessionId = sessionId;
            return amber;
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }        
    }

    public AmberGraph getAmberGraph() {
        return ((OwnedGraph) graph.getBaseGraph()).getAmberGraph();
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
        ((TransactionalGraph) graph).commit();
    }

    /**
     * rollback rollback everything in the current transaction.
     */
    public void rollback() {
        ((TransactionalGraph) graph).rollback();
    }

    public JsonNode serializeToJson() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GraphSONWriter.outputGraph(graph.getBaseGraph(), bos);
        return new ObjectMapper().reader().readTree(bos.toString());
    }

    protected FramedGraph<TransactionalGraph> openGraph(TransactionalGraph graph) {
        TransactionalGraph g = new OwnedGraph(graph);
        return new FramedGraphFactory(new JavaHandlerModule(), new GremlinGroovyModule(),
                new TypedGraphModuleBuilder()
            .withClass(Copy.class)
            .withClass(File.class)
            .withClass(ImageFile.class)
            .withClass(Page.class)
            .withClass(Section.class)
            .withClass(Work.class).build()).create(g);
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
    public Section findWorkByVn(String vnLink) {
        return graph.frame(graph.getVertices("bibId", vnLink).iterator().next(), Section.class);
    }
    
    /**
     * Finds a work by voyager number.
     */
    public Section findWorkByVn(long vnLink) {
        return graph.frame(graph.getVertices("bibId", Long.toString(vnLink)).iterator().next(), Section.class);
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
    
    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session.
     * @param work
     */
    public void deletePage(final Page page) {
        page.getParent().removePart(page);
        Iterable<Copy> copies = page.getCopies();
        if (copies != null) {
            for (Copy copy : copies) {
                File file = copy.getFile();
                if (file != null) {
                    copy.removeFile(file);
                    graph.removeVertex(file.asVertex());
                }
                page.removeCopy(copy);
                graph.removeVertex(copy.asVertex());
            }
            graph.removeVertex(page.asVertex());
        }
    }

    @Override
    public void close() throws IOException {
        graph.shutdown();
        if (tempDir != null) {
            tempDir.delete();
        }
    }

    /**
     * Suspend suspends the current transaction.
     * 
     * @return the id of the current transaction.
     */
    public long suspend() {
        return sessionId;
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
    protected FramedGraph<TransactionalGraph> getGraph() {
        return graph;
    }
    
    /**
     * Wrapper around the graph that stores a reference to the AmberDb which
     * owns it. See {@link AmberSession#ownerOf(Graph)}.
     */
    private class OwnedGraph extends WrappedGraph<TransactionalGraph> implements TransactionalGraph  {

        public OwnedGraph(TransactionalGraph baseGraph) {
            super(baseGraph);
        }
        
        public AmberSession getOwner() {
            return AmberSession.this;
        }

        public AmberGraph getAmberGraph() {
            return (AmberGraph) baseGraph;
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

        @Override
        public void commit() {
            baseGraph.commit();   
        }

        @Override
        public void rollback() {
            baseGraph.rollback();           
        }

        @Override
        @Deprecated
        public void stopTransaction(Conclusion arg0) {}
    }
    
    /**
     * Returns the AmberDb instance that owns the given graph.
     */
    public static AmberSession ownerOf(Graph graph) {
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
