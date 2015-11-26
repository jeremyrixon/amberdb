package amberdb;

import java.nio.file.Path;
import java.util.List;

import javax.sql.DataSource;

import org.skife.jdbi.v2.DBI;

import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.sql.LookupsSchema;

import java.io.IOException;

import doss.BlobStore;
import doss.local.LocalBlobStore;
import doss.CorruptBlobStoreException;

public class AmberDb {
    final private DataSource dataSource;
    final private Path rootPath;
    
    public AmberDb(DataSource dataSource, Path rootPath) {
        this.dataSource = dataSource;
        this.rootPath = rootPath;
        initLookupData(dataSource);
    }

    public AmberSession begin() {        
        return new AmberSession(dataSource, openBlobStore(rootPath), null);
    }
    
    public AmberSession resume(long sessionId) {
        AmberSession as = new AmberSession(dataSource, openBlobStore(rootPath), sessionId);
        return as;
    }

    public void close() {
    }

    private void initLookupData(DataSource dataSource) {
        // NLA specific lookup table config
        DBI lookupsDbi = new DBI(dataSource);
        LookupsSchema luSchema = lookupsDbi.onDemand(LookupsSchema.class);
        Lookups lookups = lookupsDbi.onDemand(Lookups.class);
        if (!luSchema.schemaTablesExist()) {
            luSchema.createLookupsSchema();
            List<ListLu> list = lookups.findActiveLookups();
            luSchema.setupToolsAssociations(list);
        }
        if(!luSchema.carrierAlgorithmTableExist()){
            luSchema.createCarrierAlgorithmTable();
        }
        lookups.migrate();
        lookupsDbi.close(luSchema);
        lookupsDbi.close(lookups);
        lookupsDbi = null;
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
