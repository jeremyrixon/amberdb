package amberdb.model;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsFileOf;
import amberdb.relation.IsSourceCopyOf;
import amberdb.utils.OSProcessBuilder;
import amberdb.utils.images.ImgConvertBuilder;
import amberdb.utils.images.JP2EchoBuilder;
import amberdb.utils.images.KduCompressBuilder;
import amberdb.utils.images.TiffCPBuilder;
import amberdb.utils.images.TiffEchoBuilder;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import doss.Blob;
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
    Copy deriveImageCopy(LocalBlobStore doss, Path tiffUnCompressor, Path jp2Generator, Path stagingPath) throws IllegalStateException, IOException, InterruptedException;


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
        public Copy deriveImageCopy(LocalBlobStore doss, Path tiffUnCompressor, Path jp2Generator, Path stagingPath) throws IllegalStateException, IOException, InterruptedException {
            File masterImage = this.getFile();
            if (masterImage == null || masterImage.getBlobId() == null) 
                throw new NoSuchCopyException(getWork().getId(), CopyRole.MASTER_COPY);
            
            // set the masterImage file to have file extension of .tif
            System.out.println("Copy.deriveImageCopy:  about to locating local blob store...");
            // LocalBlobStore doss = (LocalBlobStore) AmberSession.ownerOf(g()).getBlobStore();
            try (LocalBlobStore.Tx tx = (LocalBlobStore.Tx) doss.begin()) {
                System.out.println("master image blob id: " + masterImage.getBlobId());
                tx.setExtension(masterImage.getBlobId(), ".tif");    
                tx.commit();
                
                Path jp2ImgPath = generateImage(doss, tiffUnCompressor, jp2Generator, stagingPath, masterImage);
               
                if (jp2ImgPath != null) {
                    Work work = this.getWork();
                    Copy ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, "jp2");
                    ImageFile acf = ac.getImageFile();
                    acf.setLocation(jp2ImgPath.toString());
                    System.out.println("generated ac file with file id : " + ac.getFile().getId() + ", blob id: " + ac.getFile().getBlobId() + ", location: " + ac.getImageFile().getLocation());
                    return ac;
                }
                return null;
            }
        }
        
        private Path generateImage(LocalBlobStore doss, Path tiffUnCompressor, Path jp2Generator, Path stagingPath, File masterImage) throws IOException, InterruptedException, NoSuchCopyException {
            if (masterImage == null || masterImage.getBlobId() == null)
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.MASTER_COPY);
            
            // Step 1: uncompress Tiff
            OSProcessBuilder tiffUtil = getTiffProcessBuilder(tiffUnCompressor);
            
            // NOTE: setting staging path to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!stagingPath.toFile().exists())
                stagingPath = Paths.get(".");
            
            Path tiffInput = stagingPath.resolve(masterImage.getBlobId().toString() + ".tif"); 
            Path tiffOutput = stagingPath.resolve("uncompressed_" + masterImage.getBlobId().toString() + ".tif");
            stageTiffInput(tiffInput, doss.get(masterImage.getBlobId()));
            ProcessBuilder tiffPb = tiffUtil.setCmdPath(tiffUnCompressor).setInputPath(tiffInput).setOutputPath(tiffOutput).assemble();
            Process tiffProcess = tiffPb.start();
            tiffProcess.waitFor();
            tiffProcess.exitValue();
            
            // Step 2: generate and store JP2 image on DOSS
            OSProcessBuilder jp2Util = getJP2ProcessBuilder(jp2Generator);
            Path jp2ImgPath = stagingPath.resolve(masterImage.getBlobId().toString() + ".jp2");  
            String jp2Cmd = "./img.sh";
            if (!Paths.get(jp2Cmd).toFile().exists()) {
                jp2Cmd = stagingPath.toString() + "/img.sh";
            }
            String[] options = { jp2Cmd, jp2Generator.toString(), tiffOutput.toString(), jp2ImgPath.toString() };
            ProcessBuilder jp2Pb = new ProcessBuilder(options);
            Process jp2Process = jp2Pb.start();
            jp2Process.waitFor();
            jp2Process.exitValue();
            
            // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!tiffUnCompressor.toFile().exists() || !jp2Generator.toFile().exists()) return null;
            return jp2ImgPath;
        }
        
        private OSProcessBuilder getTiffProcessBuilder(Path tiffUnCompressor) {
            OSProcessBuilder tiffUtil = null;
            if (!tiffUnCompressor.toFile().exists()) {
                tiffUtil = new TiffEchoBuilder();
            } else {
                String tiffUncompressorPath = tiffUnCompressor.toString();
                if (tiffUncompressorPath.contains("tiffcp")) {
                    tiffUtil = new TiffCPBuilder();
                } else {
                    tiffUtil = new ImgConvertBuilder();
                }
            }
            return tiffUtil;
        }
        
        private OSProcessBuilder getJP2ProcessBuilder(Path jp2Generator) {
            OSProcessBuilder jp2Util = null;
            if (!jp2Generator.toFile().exists()) {
                jp2Util = new JP2EchoBuilder();
            } else {
                jp2Util = new KduCompressBuilder();
            }
            return jp2Util;
        }
        
        private void stageTiffInput(Path tiffInput, Blob tiff) throws IOException {
            ReadableByteChannel channel = tiff.openChannel();
            FileChannel dest = FileChannel.open(tiffInput,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            long bytesTransferred = dest.transferFrom(channel, 0,
                    Long.MAX_VALUE);
        }
    }

}
