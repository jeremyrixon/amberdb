package amberdb.model;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsFileOf;
import amberdb.relation.IsSourceCopyOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import doss.Blob;
import doss.BlobStore;
import doss.Writable;
import doss.core.Writables;
import doss.local.LocalBlobStore;

/**
 * A physical or digital manifestation of a {@link Work}. The library may hold
 * one or more copies of a work, e.g. the original paper version of a piece of
 * sheet music, a microform replica and a set of digital replicas made from
 * either the original or the microform copy.
 * 
 * Copies may be either original or derived manually or automatically from
 * another copy. For the purposes of the digital library if the source of a
 * derivative is unknown we consider it original.
 */
@TypeValue("Copy")
public interface Copy extends Node {
    
    /* DCM Legacy Data */
    @Property("dcmCopyPid")
    public String getDcmCopyPid();

    @Property("dcmCopyPid")
    public void setDcmCopyPid(String dcmCopyPid);
    
    @Property("dcmDateTimeCreated")
    public Date getDcmDateTimeCreated();

    @Property("dcmDateTimeCreated")
    public void setDcmDateTimeCreated(Date dcmDateTimeCreated);
    
    @Property("dcmDateTimeUpdated")
    public Date getDcmDateTimeUpdated();

    @Property("dcmDateTimeUpdated")
    public void setDcmDateTimeUpdated(Date dcmDateTimeUpdated);
    
    @Property("dcmRecordCreator")
    public String getDcmRecordCreator();

    @Property("dcmRecordCreator")
    public void setDcmRecordCreator(String dcmRecordCreator);
    
    @Property("dcmRecordUpdater")
    public String getDcmRecordUpdater();

    @Property("dcmRecordUpdater")
    public void setDcmRecordUpdater(String dcmRecordUpdater);
    /* END DCM Legacy Data */
    
    @Property("currentVersion")
    public String getCurrentVersion();

    @Property("currentVersion")
    public void setCurrentVersion(String currentVersion);
    
    @Property("versionNumber")
    public String getVersionNumber();

    @Property("versionNumber")
    public void setVersionNumber(String versionNumber);
        
    @Property("otherNumber")
    public String getOtherNumber();
        
    @Property("otherNumber")
    public void setOtherNumber(String otherNumber);

    @Property("copyType")
    public String getCopyType();

    @Property("copyType")
    public void setCopyType(String copyType);
    
    @Property("copyRole")
    public String getCopyRole();

    @Property("copyRole")
    public void setCopyRole(String copyRole);

    @Property("carrier")
    public String getCarrier();

    @Property("carrier")
    public void setCarrier(String carrier);
    
    @Property("bestCopy")
    public String getBestCopy();

    @Property("bestCopy")
    public void setBestCopy(String bestCopy);
    
    @Property("recordSource")
    public String getRecordSource();

    @Property("recordSource")
    public void setRecordSource(String recordSource);
    
    @Property("localSystemNumber")
    public String getLocalSystemNumber();

    @Property("localSystemNumber")
    public void setLocalSystemNumber(String localSystemNumber);
    
    @Property("materialType")
    public String getMaterialType();

    @Property("materialType")
    public void setMaterialType(String materialType);

    /**
     * The source copy which this copy was derived from. Null if this copy is
     * original or the source copy is unknown.
     */
    @Adjacency(label = IsSourceCopyOf.label, direction=Direction.OUT)
    public Copy getSourceCopy();

    @Adjacency(label = IsSourceCopyOf.label, direction=Direction.OUT)
    public void setSourceCopy(Copy sourceCopy);

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public Iterable<File> getFiles();

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public File getFile();
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public ImageFile getImageFile();

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public File addFile();
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public ImageFile addImageFile();

    @JavaHandler
    File addFile(Path source, String mimeType) throws IOException;

    @JavaHandler
    File addLegacyDossFile(Path dossPath, String mimeType) throws IOException;

    @JavaHandler
    File addFile(Writable contents, String mimeType) throws IOException;
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    void removeFile(final File file);

    @Adjacency(label = IsCopyOf.label)
    public Work getWork();
    
    @JavaHandler
    Copy deriveImageCopy(Path tiffUnCompressor, Path jp2Generator) throws IllegalStateException, IOException, InterruptedException;


    abstract class Impl implements JavaHandlerContext<Vertex>, Copy {

        @Override
        public File addFile(Path source, String mimeType) throws IOException {
           return addFile(Writables.wrap(source), mimeType);             
        }

        @Override
        public File addLegacyDossFile(Path dossPath, String mimeType) throws IOException {
            File file = (mimeType.startsWith("image"))? addImageFile() : addFile();
            storeLegacyDossFile(file, dossPath, mimeType);
            return file;
        }

        @Override
        public File addFile(Writable contents, String mimeType) throws IOException {
            File file = (mimeType.startsWith("image"))? addImageFile() : addFile();
            storeFile(file, contents, mimeType);
            return file;
        }
        
        private void storeFile(File file, Writable contents, String mimeType) throws IOException {
            file.put(contents);
            file.setMimeType(mimeType);
        }
                
        private void storeLegacyDossFile(File file, Path dossPath, String mimeType) throws IOException {
            file.putLegacyDoss(dossPath);
            file.setMimeType(mimeType);
        }
        
        @Override
        public Copy deriveImageCopy(Path tiffUncompressor, Path jp2Generator) throws IllegalStateException, IOException, InterruptedException {
            
            // create a temporary file processing location for deriving the jpeg2000 from the tiff
            Path stage = Files.createTempDirectory("amberdb-derivative");
            
            // assume this Copy is a tiff master copy and access the amber file
            Long tiffBlobId = this.getFile().getBlobId();
            
            // let us also assume we are using a local blob store ... 
            LocalBlobStore doss = (LocalBlobStore) AmberSession.ownerOf(g()).getBlobStore();
            
            // aaaaand generate the derivative ...
            Path jp2ImgPath = generateImage(doss, tiffUncompressor, jp2Generator, stage, tiffBlobId);

            // add the derived jp2 image to this Copy's work as an access copy
            if (jp2ImgPath != null) {
                Work work = this.getWork();
                Copy ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, "jp2");
                ImageFile acf = ac.getImageFile();
                acf.setLocation(jp2ImgPath.toString());
                System.out.println("generated ac file with " +
                		"file id : " + ac.getFile().getId() + 
                		", blob id: " + ac.getFile().getBlobId() + 
                		", location: " + ac.getImageFile().getLocation());
                return ac;
            }
            
            return null; // this is what happens when assumptions don't pan out :-(
        }
        
        private Path generateImage(BlobStore doss, Path tiffUncompressor, Path jp2Generator, Path stage, Long tiffBlobId) throws IOException, InterruptedException, NoSuchCopyException {
            
            if (tiffBlobId == null)
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.MASTER_COPY);

            // prepare the files for conversion
            Path tiffPath = stage.resolve(tiffBlobId + ".tif");                                 // where to put the tif retrieved from the amber blob
            Path uncompressedTiffPath = stage.resolve("uncompressed_" + tiffBlobId + ".tif");   // what to call the uncompressed tif 
            copyBlobToFile(doss.get(tiffBlobId), tiffPath);                                     // get the blob from amber

            // Step 1: uncompress tiff
            String[] uncompressCmd = {
                    tiffUncompressor.toString(),
                    "-c none", 
                    tiffPath.toString(), 
                    uncompressedTiffPath.toString()};

            ProcessBuilder uncompressPb = new ProcessBuilder(uncompressCmd);
            Process uncompressProcess = uncompressPb.start();
            uncompressProcess.waitFor();
            int uncompressResult = uncompressProcess.exitValue();
            
            // Step 2: generate and store JP2 image on DOSS
            Path jp2ImgPath = stage.resolve(tiffBlobId + ".jp2"); // name the jpeg2000 derivative after the original uncompressed blob
            
            // This can be shifted out to config down the track
            String[] convertCmd = {
                    jp2Generator.toString(),
                    "-i",
                    uncompressedTiffPath.toString(),
                    "-o",
                    jp2ImgPath.toString(),
                    "-rate",
                    "0.5",
                    "Clayers=1",
                    "Clevels=7",
                    "Cprecincts={256,256},{256,256},{256,256},{128,128},{128,128},{64,64},{64,64},{32,32},{16,16}",
                    "Corder=RPCL",
                    "ORGgen_plt=yes",
                    "Cblk={32,32}",
                    "Cuse_sop=yes"};

            ProcessBuilder jp2Pb = new ProcessBuilder(convertCmd);
            Process jp2Process = jp2Pb.start();
            jp2Process.waitFor();
            int convertResult = jp2Process.exitValue(); // really should check it's worked
            
            // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!tiffUncompressor.toFile().exists() || !jp2Generator.toFile().exists()) return null;
            return jp2ImgPath;
        }
        
        private long copyBlobToFile(Blob blob, Path destinationFile) throws IOException {
            ReadableByteChannel channel = blob.openChannel();
            FileChannel dest = FileChannel.open(
                    destinationFile,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            long bytesTransferred = dest.transferFrom(channel, 0, Long.MAX_VALUE);
            return bytesTransferred;
        }
    }
}
