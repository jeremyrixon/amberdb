package amberdb;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

import amberdb.model.CameraData;
import amberdb.model.Copy;
import amberdb.model.Description;
import amberdb.model.EADEntity;
import amberdb.model.EADFeature;
import amberdb.model.EADWork;
import amberdb.model.File;
import amberdb.model.GeoCoding;
import amberdb.model.IPTC;
import amberdb.model.ImageFile;
import amberdb.model.Page;
import amberdb.model.Party;
import amberdb.model.Section;
import amberdb.model.SoundFile;
import amberdb.model.Tag;
import amberdb.model.Work;
import amberdb.model.AliasItem;
import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.sql.LookupsSchema;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberHistory;
import amberdb.graph.AmberMultipartQuery;
import amberdb.graph.AmberMultipartQuery.QueryClause;
import amberdb.graph.AmberTransaction;
import amberdb.query.ObjectsWithPropertyReportQuery;
import static amberdb.graph.BranchType.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Direction;
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
                .withClass(EADEntity.class)
                .withClass(EADFeature.class)
                .withClass(EADWork.class)
                .withClass(Tag.class)
                .withClass(Party.class)
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
        initLookupData(dataSource);

        // Graph
        AmberGraph amber = new AmberGraph(dataSource);
        if (sessionId != null)
            amber.resume(sessionId);

        return amber;
    }


    private void initLookupData(DataSource dataSource) {
        lookupsDbi = new DBI(dataSource);
        LookupsSchema luSchema = lookupsDbi.onDemand(LookupsSchema.class);
        Lookups lookups = getLookups();
        if (!luSchema.schemaTablesExist()) {
            luSchema.createLookupsSchema();
            List<ListLu> list = lookups.findActiveLookups();
            luSchema.setupToolsAssociations(list);
        }
        if(!luSchema.carrierAlgorithmTableExist()){
            luSchema.createCarrierAlgorithmTable();
        }
        lookups.migrate();
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
        return new ObjectMapper().reader().readTree(bos.toString("UTF-8"));
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
     * Finds nodes that have a given value in a json string list property.
     * @param propertyName The property to search on
     * @param value The value to search for
     * @param <T> The class of Object to return (eg: Work, Copy, Node)
     */
    public <T> List<T> findModelByValueInJsonList(String propertyName, String value, Class<T> T) {
        List<T> nodes = new ArrayList<>();
        for (Vertex match : getAmberGraph().getVerticesByJsonListValue(propertyName, value)) {
            // add matched vertex from framed graph
            nodes.add(graph.getVertex(match.getId(), T));
        }
        return nodes;
    }
    
 
    /**
     * Finds nodes that have a property containing the given value.
     * @param propertyName The property to search on
     * @param value The value to search for
     * @param <T> The class of Object to return (eg: Work, Copy, Node)
     */
    public <T> List<T> findModelByValue(String propertyName, Object value, Class<T> T) {
        List<T> nodes = new ArrayList<>();
        for (Vertex match : getAmberGraph().getVertices(propertyName, value)) {
            // add matched vertex from framed graph
            nodes.add(graph.getVertex(match.getId(), T));
        }
        return nodes;
    }
    
    
    /**
     * Creates a new work.
     * 
     * @return the work
     */
    public Work addWork() {
        return graph.addVertex(null, Work.class);
    }
    
    
    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session. This method will orphan any child works.
     */
    public void deleteWork(final Work work) {

        // delete copies of work
        Iterable<Copy> copies = work.getCopies();
        if (copies != null) {
            for (Copy copy : copies) {
                deleteCopy(copy);
            }
        }

        // descriptions
        for (Description desc : work.getDescriptions()) {
            graph.removeVertex(desc.asVertex());
        }

        // delete work
        graph.removeVertex(work.asVertex());
    }


    public Map<String, Integer> deleteWorksFast(Map<String, Integer> counts, final Work... works) {

        List<Long> ids = new ArrayList<>();
        for (Work w : works) {
            ids.add(w.getId()); 
        }
        loadMultiLevelWorks(ids);
        
        AmberGraph g = getAmberGraph();
        boolean prevMode = g.inLocalMode();
        g.setLocalMode(true);
        counts = deleteWorks(counts, works);
        g.setLocalMode(prevMode);
        
        return counts;
    }

    
    public void deleteWorksFast(final Work... works) {

        List<Long> ids = new ArrayList<>();
        for (Work w : works) {
            ids.add(w.getId()); 
        }
        loadMultiLevelWorks(ids);
        
        AmberGraph g = getAmberGraph();
        boolean prevMode = g.inLocalMode();
        g.setLocalMode(true);
        deleteWorks(works);
        g.setLocalMode(prevMode);
    }
    
    
    /**
     * Recursively delete a collection of Works and all their children (including Copies, Files
     * and Descriptions). 
     * 
     * @param works The works to be deleted
     */
    public void deleteWorks(final Work... works) {
        
        for (Work work : works) {
            
            /* first, get children works */
            List<Work> children = Lists.newArrayList(work.getChildren());

            /* to avoid cycles, next delete the work (plus all its sub-objects) */

            // copies
            for (Copy copy : work.getCopies()) {
                deleteCopy(copy);
            }
            
            // descriptions
            for (Description desc : work.getDescriptions()) {
                graph.removeVertex(desc.asVertex());
            }
            
            // the work itself 
            graph.removeVertex(work.asVertex());

            /* finally, process the children */ 
            deleteWorks(children.toArray(new Work[children.size()]));
        }  
    }
    
    
    /**
     * Delete all the vertices representing the copy, all its files and their descriptions.
     * @param copy The copy to be deleted
     */
    public void deleteCopy(final Copy copy) {
        for (File file : copy.getFiles()) {
            deleteFile(file);
        }
        graph.removeVertex(copy.asVertex());
    }
    

    /**
     * Delete the vertices representing a file including its descriptions.
     * @param file The file to be deleted
     */
    public void deleteFile(final File file) {
        for (Description desc : file.getDescriptions()) {
            graph.removeVertex(desc.asVertex());
        }
        graph.removeVertex(file.asVertex());
    }    
    
    
    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session.
     * @param page The page to be deleted
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
    
    
    public Tag addTag() {
        return graph.addVertex(null, Tag.class);
    }
    
    public Tag addTag(String name) {
        Tag t = addTag();
        t.setName(name);
        return t;
    }
    
    
    public Tag findTag(String name) {
        for (Tag t : getAllTags()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    
    public Tag getTag(Long id) {
        return findModelObjectById(id, Tag.class);
    }
    
    
    public Iterable<Tag> getAllTags() {
        return graph.getVertices("type", "Tag", Tag.class);
    }

    public void deleteTag(Tag tag) {
        graph.removeVertex(tag.asVertex());
    }
    
    private Party addParty() {
        return graph.addVertex(null, Party.class);
    }

    public Party addParty(String name) {
        return addParty(name, null, null);
    }
    
    public Party addParty(String name, String orgUrl, String logoUrl) {
        if (StringUtils.isEmpty(name)) { //name is mandatory
            return null;
        }
        Party party = addParty();
        
        party.setName(name);
        party.setOrgUrl(orgUrl);
        party.setLogoUrl(logoUrl);
        return party;
    }

    public Iterable<Party> getAllParties() {
        return graph.getVertices("type", "Party", Party.class);
    }

    public Party findParty(String name) {
        for (Party party : getAllParties()) {
            if (party.getName().equals(name)) {
                return party;
            }
        }
        return null;
    }    

    /**
     * Recursively delete a Work and all its children (including Copies, Files
     * and Descriptions). Returns an updated count of the different object types
     * deleted added to an existing map of keys to counters.
     * 
     * @param counts
     *            A map containing counts of the different object types deleted
     * @param works
     *            The works to be deleted
     */
    public Map<String, Integer> deleteWorks(Map<String, Integer> counts, final Work... works) {

        for (Work work : works) {
            
            /* first, get children works */
            List<Work> children = Lists.newArrayList(work.getChildren());

            /* next, to avoid cycles, delete the work (plus all its sub-objects) */
            
            // copies
            for (Copy copy : work.getCopies()) {
                deleteCopy(counts, copy);
            }
            
            // descriptions
            for (Description desc : work.getDescriptions()) {
                graph.removeVertex(desc.asVertex());
                increment(counts, "Description");
            }
            
            // the work itself 
            graph.removeVertex(work.asVertex());
            increment(counts, "Work");

            /* finally, process the children */ 
            deleteWorks(counts, children.toArray(new Work[children.size()]));
        }  

        return counts;
    }
    

    private static final int MAX_DEPTH = 15;
    /**
     * Return a Map of Work ids for Works that are 'represented' by Copies that are 
     * descendants of the given list of Works (ie: Copies of the listed Works or any 
     * of their descendants). The Work id maps to its representing Copy id.
     * 
     * @param representedBy
     *            A map with Work ids as the key mapping to their representing Copy's id
     * @param depth
     *            Used to prevent cycles while traversing the graph. Maximum depth set by MAX_DEPTH 
     * @param works
     *            The Works to be searched for representing Copies
     */
    protected Map<Long, Long> getWorksRepresentedByCopiesOf(Map<Long, Long> representedBy, int depth, final Work... works) {
        if (depth >= MAX_DEPTH) return representedBy; // possibly throw Exception instead ?
        for (Work work : works) {
            // process this work's copies
            for (Copy copy : work.getCopies()) {
                for (Work rep : copy.getRepresentedWorks()) {
                    representedBy.put(rep.getId(), copy.getId());
                }
            }
            // process this work's descendant works 
            getWorksRepresentedByCopiesOf(representedBy, depth+1, Iterables.toArray(work.getChildren(), Work.class));
        }  
        return representedBy;
    }
    public Map<Long, Long> getWorksRepresentedByCopiesOf(final Work... works) {
        return getWorksRepresentedByCopiesOf(new HashMap<Long, Long>(), 0, works);
    }


    /**
     * Delete all the vertices representing the copy, all its files and their
     * descriptions. Returns an updated count of the different object types
     * deleted added to an existing map of keys to counters.
     * 
     * @param counts
     *            A map of object type to number deleted
     * @param copy
     *            The copy to be deleted
     */
    public Map<String, Integer> deleteCopy(Map<String, Integer> counts, final Copy copy) {
        
        for (File file : copy.getFiles()) {
            for (Description desc : file.getDescriptions()) {
                graph.removeVertex(desc.asVertex());
                increment(counts, "Description");
            }
            graph.removeVertex(file.asVertex());
            increment(counts, "File");
        }
        graph.removeVertex(copy.asVertex());
        increment(counts, "Copy");

        return counts;
    }

    
    /**
     * Convenience method to increment a count in a map of keys to counts
     * 
     * @param countMap
     *            The map to update
     * @param key
     *            The key of the count to increment
     */
    private void increment(Map<String, Integer> countMap, String key) {
        Integer count = countMap.get(key);
        if (count == null) {
            count = 1;
        } else {
            count = count + 1;
        }
        countMap.put(key, count);
    }
    
    
    /**
     * Load the given works into memory. IMPORTANT: Please read the return value
     * as it is not an intuitive result.
     * 
     * @param ids
     *            The list of works to load
     * @return Only the vertices related to these works already saved to amber.
     *         Vertices related to these works that have not yet been saved will
     *         NOT appear here. Parents of the work are not included
     */
    public List<Vertex> loadMultiLevelWorks(final Long... ids) {
        
        AmberGraph g = getAmberGraph();
        
        List<Vertex> components;
        try (AmberMultipartQuery q = g.newMultipartQuery(ids)) {

            String numPartsInAmberQuery = 
                    "SELECT COUNT(edge.id) num " 
                    + "FROM edge, v1 " 
                    + "WHERE v1.step = %d " 
                    + "AND v1.vid = edge.v_in " 
                    + "AND edge.label = 'isPartOf' " 
                    + "AND edge.txn_end = 0;";

            QueryClause qc = q.new QueryClause(BRANCH_FROM_PREVIOUS, new String[] { "isPartOf" }, Direction.IN);

            int step;
            q.startQuery();

            boolean moreParts = true;
            while (moreParts) {
                step = q.step + 1; // add 1 because the checkQuery is run after the following step is executed
                List<Map<String, Object>> numPartsInAmberResult = q.continueWithCheck(String.format(numPartsInAmberQuery, step), qc);
                Long numParts = (Long) numPartsInAmberResult.get(0).get("num");
                if (numParts.equals(0L)) {
                    moreParts = false;
                }
            }    

            // get all the copies, files etc
            q.continueWithCheck(null,
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"deliveredOn"}, Direction.IN), // gets delivery parent
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"represents"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"isCopyOf"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"isFileOf"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"descriptionOf"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"tags"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"acknowledge"}, Direction.OUT)
                    );
            components = q.getResults(true);
        }
        return components;
    }
    public List<Vertex> loadMultiLevelWorks(final List<Long> ids) {
        return loadMultiLevelWorks(ids.toArray(new Long[ids.size()]));
    }
    

    
    public Map<String, Set<AliasItem>> getDuplicateAliases(String name, String collection) {

        ObjectsWithPropertyReportQuery avq = new ObjectsWithPropertyReportQuery(getAmberGraph());
        List<Vertex> results = avq.generateDuplicateAliasReport(name, collection);
        Map<String, Set<AliasItem>> aliasMap = new HashMap<>();
        for (Vertex v :results) {
            String values = v.getProperty(name);
            if (values != null && values.length() > 3) {
                Long id = (Long)v.getId();
                String pi = PIUtil.format(id);
                String type = v.getProperty("type");
                type = type == null ? "":type;
                String title =  v.getProperty("title");
                title = title == null ? "":title;
                AliasItem aliasItem = new AliasItem(pi, type, title);
                
                String[] vals = values.substring(2, values.length() -2).split("\",\"");
                addToAliasMap(aliasMap, aliasItem, vals);
            }
        }
        removeSingleAliases(aliasMap);
        return aliasMap;
    }
    
    public List<Work> getExpiryReport(Date expiryYear, String collection) {

        ObjectsWithPropertyReportQuery avq = new ObjectsWithPropertyReportQuery(getAmberGraph());     
        List<Vertex> results = avq.generateExpiryReport(expiryYear, collection);
        List<Work> works = new ArrayList<>();
        for (Vertex v :results) {
 
            Work work = getGraph().frame(v, Work.class);
            works.add(work);
     
        }
     
        return works;
    }
    
    
    
    
    private void addToAliasMap(Map<String, Set<AliasItem>> aliasMap,
                               AliasItem aliasItem, String[] vals) {
        for (int i = 0; i < vals.length; i++) {
            if (aliasMap.containsKey(vals[i])) {
                Set<AliasItem> existingSet = aliasMap.get(vals[i]);
                existingSet.add(aliasItem);
            } else {
                Set<AliasItem> newSet = new HashSet<>();
                newSet.add(aliasItem);
                aliasMap.put(vals[i], newSet);
            }
        }
    }
    
    
    private void removeSingleAliases(Map<String, Set<AliasItem>> aliasMap) {
        Object[] keys = aliasMap.keySet().toArray();
        for (Object key :keys) {
            if (aliasMap.get(key).size() < 2){
                aliasMap.remove(key);
            }
        }
    }

}
