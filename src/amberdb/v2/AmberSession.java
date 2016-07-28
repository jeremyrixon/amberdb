package amberdb.v2;

import amberdb.sql.Lookups;
import amberdb.v2.hook.AmberPreCommitHook;
import amberdb.v2.model.*;
import amberdb.v2.model.dao.*;
import amberdb.v2.model.mapper.AmberDbMapperFactory;
import doss.BlobStore;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AmberSession implements AutoCloseable {

    private final BlobStore blobStore;
    private DBI dbi;
    private WorkDao workDao;
    private CopyDao copyDao;
    private ParentChildDao parentChildDao;
    private FileDao fileDao;
    private ImageFileDao imageFileDao;
    private SoundFileDao soundFileDao;
    private MovingImageFileDao movingImageFileDao;
    private SectionDao sectionDao;
    private final TempDirectory tempDir;
    private List<AmberPreCommitHook<? extends AmberModel>> preCommitHooks = new ArrayList<>();

    /**
     * Constructs an in-memory AmberDb for testing with. Also creates a BlobStore in a temp dir
     */
    public AmberSession() {
        tempDir = new TempDirectory();
        tempDir.deleteOnExit();

        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
        init(dataSource, null);

        // DOSS
        blobStore = AmberDb.openBlobStore(tempDir.getPath());
    }

    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberSession(BlobStore blobStore) throws IOException {
        tempDir = null;

        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
        init(dataSource, null);

        // DOSS
        this.blobStore = blobStore;
    }

    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberSession(BlobStore blobStore, Long sessionId) throws IOException {
        tempDir = null;

        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
        init(dataSource, sessionId);

        // DOSS
        this.blobStore = blobStore;
    }

    public AmberSession(DataSource dataSource, BlobStore blobStore, Long sessionId) {
        init(dataSource, sessionId);
        tempDir = null;

        // DOSS
        this.blobStore = blobStore;
    }

    public AmberSession(DataSource dataSource, BlobStore blobStore, Long sessionId, List<AmberPreCommitHook<? extends AmberModel>> preCommitHooks) {
        init(dataSource, sessionId);

        this.preCommitHooks = preCommitHooks;
        tempDir = null;

        // DOSS
        this.blobStore = blobStore;
    }

    private void init(DataSource dataSource, Long sessionId) {
        // Lookups dbi
        dbi = new DBI(dataSource);
        dbi.registerMapper(new AmberDbMapperFactory());

        workDao = dbi.onDemand(WorkDao.class);
        copyDao = dbi.onDemand(CopyDao.class);
        parentChildDao = dbi.onDemand(ParentChildDao.class);
        fileDao = dbi.onDemand(FileDao.class);
        imageFileDao = dbi.onDemand(ImageFileDao.class);
        soundFileDao = dbi.onDemand(SoundFileDao.class);
        movingImageFileDao = dbi.onDemand(MovingImageFileDao.class);
        sectionDao = dbi.onDemand(SectionDao.class);
    }

    @Override
    public void close() {
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
    // TODO - implement this
    public long suspend() {
        return 0L;
    }

    /**
     * Recover recovers the suspended transaction of the specified txId.
     *
     * @param txId
     *            the transaction id of the suspended transaction to reover.
     */
    public void recover(Long txId) {
        // TODO - implement this
    }

    public Lookups getLookups() {
        return dbi.onDemand(Lookups.class);
    }

    public void setLocalMode(boolean localModeOn) {
        // TODO - lol wut?
    }

    public BlobStore getBlobStore() {
        return blobStore;
    }

    public DBI getDbi() {
        return dbi;
    }

    /**
     * commit saves everything in the current transaction.
     */
    public void commit() {
        runPreCommitHooks();
    }

    private void runPreCommitHooks() {
        for (AmberPreCommitHook preCommitHook: preCommitHooks) {
            // TODO - rewrite precommit hooks to not use vertices and edges
        }
    }

    /**
     * commit saves everything in the current session and records who did it and why with the transaction record.
     *
     * @param who the username to associate with the transaction
     * @param why the operation they were fulfilling by commiting the transaction
     */
    public Long commit(String who, String why) {
        runPreCommitHooks();
        return 0L;
    }

    /**
     * Finds a work by id.
     */
    public Work findWork(long objectId) {
        return workDao.get(objectId);
    }

    /**
     * Finds a work by id or alias.
     */
    public Work findWork(String idOrAlias) {
        // TODO - aliases
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
        T obj = null;
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

    // TODO - find work by Voyager number? (Can only find this used in a test...)

    /**
     * Finds nodes that have a property containing the given value.
     * @param propertyName The property to search on
     * @param value The value to search for
     * @param <T> The class of Object to return (eg: Work, Copy, Node)
     */
    public <T> List<T> findModelByValue(String propertyName, Object value, Class<T> T) {
        List<T> objects = null;
        // TODO - Write DAO to return a model with a given property.
        return objects;
    }

    public <T> List<T> findVersionedVertex(long objectId, Class<T> T) {
        List<T> history = null;
        // TODO - Write DAO to return history for a type.
        return history;
    }

    /**
     * Creates a new work.
     *
     * @return the work
     */
    public Work addWork() {
        return new Work();
    }

    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session. This method will orphan any child works.
     */
    public void deleteWork(final Work work) {

        // delete copies of work
        // TODO - use CopyDAO to get files to delete
        Iterable<Copy> copies = null;//work.getCopies();
        if (copies != null) {
            for (Copy copy : copies) {
                deleteCopy(copy);
            }
        }

        // descriptions
        // TODO - WTF is this?!
//        for (Description desc : work.getDescriptions()) {
//            graph.removeVertex(desc.asVertex());
//        }

        // delete work
        // TODO - use the WorkDao to delete work
    }

    /**
     * Delete all the vertices representing the copy, all its files and their descriptions.
     * @param copy The copy to be deleted
     */
    public void deleteCopy(final Copy copy) {
        // TODO use ImageFileDao to get files to delete
//        for (File file : copy.getFiles()) {
//            deleteFile(file);
//        }

        // delete copy
        // TODO - use CopyDao to delete work
    }

    /**
     * Delete the vertices representing a file including its descriptions.
     * @param file The file to be deleted
     */
    public void deleteFile(final File file) {
        // TODO - I have no idea what this is...
//        for (Description desc : file.getDescriptions()) {
//            graph.removeVertex(desc.asVertex());
//        }

        // delete file
        // TODO - use the ImageFileDao to delete work
    }

    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session.
     * @param page The page to be deleted
     */
    public void deletePage(final Page page) {
        deleteWork(page);
    }

    // TODO - Create a new AmberHistory object that manages historical objects
    public <T> List<T> getAmberHistory() {
        return null;
    }

    // TODO - Add other convenience methods...blah blah blah...

    public WorkDao getWorkDao() {
        return workDao;
    }

    public CopyDao getCopyDao() {
        return copyDao;
    }

    public ParentChildDao getParentChildDao() {
        return parentChildDao;
    }

    public FileDao getFileDao() {
        return fileDao;
    }

    public ImageFileDao getImageFileDao() {
        return imageFileDao;
    }

    public SoundFileDao getSoundFileDao() {
        return soundFileDao;
    }

    public MovingImageFileDao getMovingImageFileDao() {
        return movingImageFileDao;
    }

    public SectionDao getSectionDao() {
        return sectionDao;
    }
}
