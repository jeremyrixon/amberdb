package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;

import amberdb.AmberDbFactory;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;

public class MasterImageCopyTest {
    private static Page coverPageFor341935;
    private static Path tiffUnCompressor = Paths.get("/opt/local/bin").resolve("convert");
    private static Path jp2Generator = Paths.get("/usr/bin").resolve("kdu_compress");
    
    @Before
    public void setup() throws IOException, InstantiationException {
        // resetTestData();
    }
    
    // @Test
    @Ignore
    public void testDriveImage() {
        // MasterImageCopy mc = (MasterImageCopy) coverPageFor341935.getCopy(CopyRole.MASTER_COPY);
        Copy mc = coverPageFor341935.getCopy(CopyRole.MASTER_COPY);
        try {
            mc.deriveImageCopy(tiffUnCompressor, jp2Generator);
        } catch (IllegalStateException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void resetTestData() {
        String dbUrl = "jdbc:mysql://mysql-devel.nla.gov.au:6446/amberdb?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8&relaxAutoCommit=true";
        String dbUser = "amberdb";
        String dbPassword = "amberdb";   
        String rootPath = "/doss/staging/interim";
        
        // Note: setting rootPath for local desktop
        if (!Paths.get(rootPath).toFile().exists()) {
            rootPath = "/doss-devel/dlir/doss/docworks";
        }
        
        try (AmberSession db = AmberDbFactory.openAmberDb(dbUrl, dbUser, dbPassword, rootPath) ) {
            coverPageFor341935 = (Page) db.findWork(34222L);
        } catch (Exception e) {
            e.printStackTrace();
            rootPath = ".";
            AmberSession db = new AmberSession();
            setTestDataInH2(db);
        } 
        
    }
    
    private static void setTestDataInH2(AmberSession db) {
        Work book = db.addWork();
        coverPageFor341935 = book.addPage();
        try {
            Copy coverPageMasterCopy = coverPageFor341935.addCopy(Paths.get(".").resolve("test_tiff.tif"), CopyRole.MASTER_COPY, "tif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private static LocalBlobStore openBlobStore(Path root) {
        try {
            return (LocalBlobStore) LocalBlobStore.open(root);
        } catch (CorruptBlobStoreException e) {
            try {
                LocalBlobStore.init(root);
            } catch (IOException e2) {
                throw new RuntimeException("Unable to initialize blobstore: " + e2.getMessage(), e2);
            }
            return (LocalBlobStore) LocalBlobStore.open(root);
        }
    }
}
