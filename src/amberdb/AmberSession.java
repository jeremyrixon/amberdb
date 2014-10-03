package amberdb;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.h2.Driver;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

import amberdb.model.CameraData;
import amberdb.model.Copy;
import amberdb.model.Description;
import amberdb.model.EADWork;
import amberdb.model.File;
import amberdb.model.GeoCoding;
import amberdb.model.IPTC;
import amberdb.model.ImageFile;
import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.SoundFile;
import amberdb.model.Work;
import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.sql.LookupsSchema;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberHistory;

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


public class AmberSession implements AutoCloseable {


    private final FramedGraph<TransactionalGraph> graph;
    private final BlobStore blobStore;
    private final TempDirectory tempDir;
    private DBI lookupsDbi;

    private final static FramedGraphFactory framedGraphFactory =
            new FramedGraphFactory(
                new JavaHandlerModule(), 
                new GremlinGroovyModule(), 
                new TypedGraphModuleBuilder()
                .withClass(Copy.class)
                .withClass(File.class)
                .withClass(ImageFile.class)
                .withClass(SoundFile.class)
                .withClass(Page.class)
                .withClass(Section.class)
                .withClass(Work.class)
                .withClass(Description.class)
                .withClass(IPTC.class)
                .withClass(GeoCoding.class)
                .withClass(CameraData.class)
                .withClass(EADWork.class)
                .build());


    /**
     * Constructs an in-memory AmberDb for testing with. Also creates a BlobStore in a temp dir 
     */
    public AmberSession() {
    
        tempDir = new TempDirectory();
        tempDir.deleteOnExit();
        
        // DOSS
        blobStore = AmberDb.openBlobStore(tempDir.getPath());
        
        // Graph
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
        AmberGraph amber = init(dataSource, null);
        graph = openGraph(amber);
    }

    
    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberSession(BlobStore blobStore, Long sessionId) throws IOException {
        
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
        AmberGraph amber = init(dataSource, sessionId);
        tempDir = null;
        
        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }

    
    public AmberSession(BlobStore blobStore) throws IOException {
        
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
        AmberGraph amber = init(dataSource, null);
        tempDir = null;
        
        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }

    
    public AmberSession(DataSource dataSource, BlobStore blobStore, Long sessionId) {
    
        AmberGraph amber = init(dataSource, sessionId);
        tempDir = null;
        
        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }

    
    private AmberGraph init(DataSource dataSource, Long sessionId) {

            // NLA specific lookup table config
            lookupsDbi = new DBI(dataSource);
            LookupsSchema luSchema = lookupsDbi.onDemand(LookupsSchema.class);
            if (!luSchema.schemaTablesExist()) {
                // maybe log something
                luSchema.createLookupsSchema();
                List<ListLu> list = getLookups().findActiveLookups();
                luSchema.setupToolsAssociations(list);
            }
            
            // Graph
            AmberGraph amber = new AmberGraph(dataSource);
            if (sessionId != null) amber.resume(sessionId);
            
            return amber;
    }
    
    
    public AmberGraph getAmberGraph() {
        return ((OwnedGraph) graph.getBaseGraph()).getAmberGraph();
    }


    public Lookups getLookups() {
        return lookupsDbi.onDemand(Lookups.class);
    }
    
    
    public void setLocalMode(boolean localModeOn) {
        getAmberGraph().setLocalMode(localModeOn);
    }
    
    
    /**
     * commit saves everything in the current transaction.
     */
    public void commit() {
        ((TransactionalGraph) graph).commit();
    }

    
    /**
     * commit saves everything in the current session and records who did it and why with the transaction record.
     * 
     * @param who the username to associate with the transaction
     * @param why the operation they were fulfilling by commiting the transaction
     */
    public Long commit(String who, String why) {
        return getAmberGraph().commit(who, why);
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
        return framedGraphFactory.create(g);
    }

    
    /**
     * Finds a work by id.
     */
    public Work findWork(long objectId) {
        return findModelObjectById(objectId, Work.class);
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
     * Finds some object and return it as the supplied model type.
     *
     * @param objectId the ID of the graph vertex you want to fetch
     * @param returnClass The type of the class that you expect the object to be
     * @param <T> The type of the class that you expect the object to be
     * @throws ClassCastException thrown if the specified type is not what the object actually is
     * @return an object of the specified type
     */
    public <T> T findModelObjectById(long objectId, Class<T> returnClass) {
        // TODO This should do some validation that the class is as expected, but that is almost impossible.
        T obj = graph.getVertex(objectId, returnClass);
        if (obj == null) {
            throw new NoSuchObjectException(objectId);
        }
        return obj;
    }

    /**
     * Finds some object and return it as the supplied model type.
     */
    public <T> T findModelObjectById(String objectId, Class<T> returnClass) {
        try {
            return findModelObjectById(Long.parseLong(objectId), returnClass);
        }
        catch (NumberFormatException nfe) {
            return findModelObjectById(PIUtil.parse(objectId), returnClass);
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
    public void deleteWork(final Work work) {
        Work parent = work.getParent();
        if (parent != null) {
            parent.removePart(work);
        }
        Iterable<Copy> copies = work.getCopies();
        if (copies != null) {
            for (Copy copy : copies) {
                deleteCopy(copy);
            }
        }
        graph.removeVertex(work.asVertex());
    }


    /**
     * Delete all the vertices representing the copy, all its files and their descriptions.
     * @param copy
     */
    public void deleteCopy(final Copy copy) {
        Work work = copy.getWork();
        for (File file : copy.getFiles()) {
            for (Description desc : file.getDescriptions()) {
                file.removeDescription(desc);
                graph.removeVertex(desc.asVertex());
            }
            copy.removeFile(file);
            graph.removeVertex(file.asVertex());
        }
        if (work != null) {
            work.removeCopy(copy);
        }
        graph.removeVertex(copy.asVertex());
    }
    
    
    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session.
     * @param work
     */
    public void deletePage(final Page page) {
        deleteWork(page);
    }    
    
    
    @Override
    public void close() throws IOException {
        graph.shutdown();
        if (tempDir != null) {
            tempDir.delete();
        }
        if (blobStore != null) {
            blobStore.close();
        }
    }

    
    /**
     * Suspend suspends the current transaction.
     * 
     * @return the id of the current transaction.
     */
    public long suspend() {
        return ((OwnedGraph) graph.getBaseGraph()).getAmberGraph().suspend();
    }

    
    /**
     * Recover recovers the suspended transaction of the specified txId.
     * 
     * @param txId
     *            the transaction id of the suspended transaction to reover.
     */
    public void recover(Long txId) {
        ((OwnedGraph) graph.getBaseGraph()).getAmberGraph().resume(txId);        
    }

    
    /**
     * getGraph
     * 
     * @return the framed graph, handly for groovy tests.
     */
    public FramedGraph<TransactionalGraph> getGraph() {
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
    
    
    /**
     * Get the ids of objects that have been modified since a given time. If an
     * edge has been modified, then both its connected objects (vertices) are
     * returned
     * 
     * @param when
     *            time of first modifications to be included
     * @return a map of object ids and how they changed
     */
    public Map<Long, String> getModifiedObjectIds(Date when) {
        return getAmberHistory().getModifiedObjectIds(when);
    }
    
    public AmberTransaction getTransaction(long id) {
        return getAmberGraph().getTransaction(id);
    }

    /**
     * Get the ids of works that have been modified since a given time. 
     * 
     * @param when
     *            time of first modifications to be included
     * @return a map of work ids and how the work was changed
     */
    public Map<Long, String> getModifiedWorkIds(Date when) {
        return getAmberHistory().getModifiedWorkIds(when);
    }
    

    public AmberHistory getAmberHistory() {
        return new AmberHistory(getAmberGraph());
    }
    
    
    /**
     * Removes a suspended session from the database.
     * 
     * @param sessId
     *            The id of the session to be removed.
     */
    public void removeSession(Long sessId) {
        getAmberGraph().destroySession(sessId);
    }
}
