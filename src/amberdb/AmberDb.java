package amberdb;

import java.nio.file.Path;

import javax.sql.DataSource;

import java.io.IOException;

import doss.BlobStore;
import doss.local.LocalBlobStore;
import doss.CorruptBlobStoreException;

public class AmberDb {
    final private DataSource dataSource;
    final private Path rootPath;
    final private BlobStore blobStore;

    public AmberDb(DataSource dataSource, Path rootPath) {
        this.dataSource = dataSource;
        this.rootPath = rootPath;
        this.blobStore = AmberDb.openBlobStore(rootPath);
    }

    public AmberSession begin() {        
        return new AmberSession(dataSource, blobStore, null);
    }
    
    public AmberSession resume(long sessionId) {
        AmberSession as = new AmberSession(dataSource, blobStore, sessionId);
        return as;
    }

    public void close() {
        if (blobStore != null) {
            blobStore.close();
        }
    }

    static BlobStore openBlobStore(Path root) {
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

}
