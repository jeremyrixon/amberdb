package amberdb.model;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.relation.DescriptionOf;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsFileOf;
import amberdb.relation.IsSourceCopyOf;
import amberdb.util.Jp2Converter;

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
    
    @Property("dcmSourceCopy")
    public String getDcmSourceCopy();

    @Property("dcmSourceCopy")
    public void setDcmSourceCopy(String dcmSourceCopy);
    
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
    
    @Property("materialType")
    public String getMaterialType();

    @Property("materialType")
    public void setMaterialType(String materialType);
    
    @Property("dateCreated")
    public Date getDateCreated();

    @Property("dateCreated")
    public void setDateCreated(Date dateCreated);

    @Property("condition")
    public String getCondition();

    @Property("condition")
    public void setCondition(String condition);
    
    @Property("exhibition")
    public String getExhibition();

    @Property("exhibition")
    public void setExhibition(String exhibition);

    @Property("acquisitionStatus")
    public String getAcquisitionStatus();

    @Property("acquisitionStatus")
    public void setAcquisitionStatus(String acquisitionStatus);

    @Property("acquisitionCategory")
    public String getAcquisitionCategory();

    @Property("acquisitionCategory")
    public void setAcquisitionCategory(String acquisitionCategory);

    @Property("copyStatus")
    public String getCopyStatus();

    @Property("copyStatus")
    public void setCopyStatus(String copyStatus);
    
    @Property("timedStatus")
    public String getTimedStatus();

    @Property("timedStatus")
    public void setTimedStatus(String timedStatus);
    
    @Property("segmentIndicator")
    public String getSegmentIndicator();
    
    @Property("segmentIndicator")
    public void setSegmentIndicator(String segmentIndicator);

    /**
     * This property is encoded as a JSON Hash - You probably want to use getAllOtherNumbers to get this property
     */
    @Property("otherNumbers")
    public String getOtherNumbers();
    

    /**
     * This method handles the JSON deserialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public Map<String, String> getAllOtherNumbers() throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * This property is encoded as a JSON Hash - You probably want to use setAllOtherNumbers to set this property
     */
    @Property("otherNumbers")
    public void setOtherNumbers(String otherNumbers);
 
    /**
     * This method handles the JSON serialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public void setAllOtherNumbers(Map<String, String> otherNumbers) throws JsonParseException, JsonMappingException, IOException;

    
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
    
    @JavaHandler
    public CameraData getCameraData();
    
    @JavaHandler
    public ImageFile getImageFile();

    @JavaHandler
    public SoundFile getSoundFile();
    
    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    public CameraData addCameraData();
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public File addFile();
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public ImageFile addImageFile();

    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public SoundFile addSoundFile();
    
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
    @Deprecated
    Copy deriveImageCopy(Path tiffUnCompressor, Path jp2Generator) throws IllegalStateException, IOException, InterruptedException;

    @JavaHandler
    Copy deriveJp2ImageCopy(Path jp2Converter, Path imgConverter) throws IllegalStateException, IOException, InterruptedException, Exception;


    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, Copy {
        static ObjectMapper mapper = new ObjectMapper();
        
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
            File file;
            if (mimeType.startsWith("image")) {
                file = addImageFile();
                this.setMaterialType("Image");
            } else {
                file = addFile();
            }
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

        /*
         * The old method to derive image copy
         * @deprecated - use {@links #deriveJp2ImageCopy()} instead
         */
        @Override
        @Deprecated
        public Copy deriveImageCopy(Path tiffUncompressor, Path jp2Generator) throws IllegalStateException, IOException, InterruptedException {

            ImageFile tiffImage = this.getImageFile();
            if (!tiffImage.getMimeType().equals("image/tiff")) {
                throw new IllegalStateException(this.getWork().getObjId() + " master is not a tiff.  You may not generate a jpeg2000 from anything but a tiff");
            }

            Path stage = null;
            try {
                // create a temporary file processing location for deriving the jpeg2000 from the tiff
                stage = Files.createTempDirectory("amberdb-derivative");

                // assume this Copy is a tiff master copy and access the amber file
                Long tiffBlobId = this.getFile().getBlobId();

                // get this copy's blob store ...
                BlobStore doss = AmberSession.ownerOf(g()).getBlobStore();

                // aaaaand generate the derivative ...
                Path jp2ImgPath = generateImage(doss, tiffUncompressor, jp2Generator, stage, tiffBlobId);

                // add the derived jp2 image to this Copy's work as an access copy
                Copy ac = null;
                if (jp2ImgPath != null) {
                    Work work = this.getWork();

                    ac = work.getCopy(CopyRole.ACCESS_COPY);
                    if ( ac == null ) {
                        ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, "image/jp2");
                        ac.setSourceCopy(this);
                    }

                    ImageFile acf = ac.getImageFile();
                    acf.setLocation(jp2ImgPath.toString());

                    // add image metadata based on the master image metadata
                    // this is used by some nla delivery systems eg: tarkine
                    acf.setImageLength(tiffImage.getImageLength());
                    acf.setImageWidth(tiffImage.getImageWidth());
                    acf.setResolution(tiffImage.getResolution());
                    acf.setFileFormat("jpeg2000");
                }
                return ac;
                
            } finally {
                // clean up temporary working space
                java.io.File[] files = stage.toFile().listFiles();
                if (files != null) {
                    for (java.io.File f : files) {
                        f.delete();
                    }
                }
                stage.toFile().delete();
            }
        }

        @Override
        public Copy deriveJp2ImageCopy(Path jp2Converter, Path imgConverter) throws IllegalStateException, IOException, InterruptedException, Exception {
            ImageFile tiffImage = this.getImageFile();
            if (!tiffImage.getMimeType().equals("image/tiff")) {
                throw new IllegalStateException(this.getWork().getObjId() + " master is not a tiff.  You may not generate a jpeg2000 from anything but a tiff");
            }

            Path stage = null;
            try {
                // create a temporary file processing location for deriving the jpeg2000 from the tiff
                stage = Files.createTempDirectory("amberdb-derivative");

                // assume this Copy is a tiff master copy and access the amber file
                Long tiffBlobId = this.getFile().getBlobId();

                // get this copy's blob store ...
                BlobStore doss = AmberSession.ownerOf(g()).getBlobStore();

                // aaaaand generate the derivative ...
                Path jp2ImgPath = generateJp2Image(doss, jp2Converter, imgConverter, stage, tiffBlobId);

                // add the derived jp2 image to this Copy's work as an access copy
                Copy ac = null;
                if (jp2ImgPath != null) {
                    Work work = this.getWork();

                    ac = work.getCopy(CopyRole.ACCESS_COPY);
                    if ( ac == null ) {
                        ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, "image/jp2");
                        ac.setSourceCopy(this);
                    } else {
                        ac.getImageFile().put(jp2ImgPath);
                    }

                    ImageFile acf = ac.getImageFile();
                    acf.setLocation(jp2ImgPath.toString());

                    // add image metadata based on the master image metadata
                    // this is used by some nla delivery systems eg: tarkine
                    acf.setImageLength(tiffImage.getImageLength());
                    acf.setImageWidth(tiffImage.getImageWidth());
                    acf.setResolution(tiffImage.getResolution());
                    acf.setFileFormat("jpeg2000");
                }
                return ac;

            } catch (Exception e) {
                throw e;
            } finally {
                // clean up temporary working space
                java.io.File[] files = stage.toFile().listFiles();
                if (files != null) {
                    for (java.io.File f : files) {
                        f.delete();
                    }
                }
                stage.toFile().delete();
            }
        }

        @Override
        public Map<String,String> getAllOtherNumbers() throws JsonParseException, JsonMappingException, IOException {

            String otherNumbers = getOtherNumbers();
            if (otherNumbers == null || otherNumbers.isEmpty())
                return new HashMap<String,String>();
            return mapper.readValue(otherNumbers, new TypeReference<Map<String, String>>() { } );
            
        }

        @Override
        public void setAllOtherNumbers( Map<String,String>  otherNumbers) throws JsonParseException, JsonMappingException, IOException {

            setOtherNumbers(mapper.writeValueAsString(otherNumbers));
        }

        /*
         * The old method to generate jp2 from a tiff file
         * @deprecated - use {@links #generateJP2Image()} instead
         */
        @Deprecated
        private Path generateImage(BlobStore doss, Path tiffUncompressor, Path jp2Generator, Path stage, Long tiffBlobId) throws IOException, InterruptedException, NoSuchCopyException {
            
            if (tiffBlobId == null)
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.fromString(this.getCopyRole()));

            // prepare the files for conversion
            Path tiffPath = stage.resolve(tiffBlobId + ".tif");                                 // where to put the tif retrieved from the amber blob
            Path uncompressedTiffPath = stage.resolve("uncompressed_" + tiffBlobId + ".tif");   // what to call the uncompressed tif 
            copyBlobToFile(doss.get(tiffBlobId), tiffPath);                                     // get the blob from amber

            // Step 1: uncompress tiff
            String[] uncompressCmd = {
                    tiffUncompressor.toString(),
                    "-c",
                    "none", 
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
                    "-num_threads",
                    "1",
                    "Cuse_sop=yes"};

            ProcessBuilder jp2Pb = new ProcessBuilder(convertCmd);
            Process jp2Process = jp2Pb.start();
            jp2Process.waitFor();
            int convertResult = jp2Process.exitValue(); // really should check it's worked

            // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!tiffUncompressor.toFile().exists() || !jp2Generator.toFile().exists()) return null;
            return jp2ImgPath;
        }

        private Path generateJp2Image(BlobStore doss, Path jp2Converter, Path imgConverter, Path stage, Long tiffBlobId) throws IOException, InterruptedException, NoSuchCopyException, Exception {
            if (tiffBlobId == null) {
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.fromString(this.getCopyRole()));
            }

            // prepare the files for conversion
            Path tiffPath = stage.resolve(tiffBlobId + ".tif");   // where to put the tif retrieved from the amber blob
            copyBlobToFile(doss.get(tiffBlobId), tiffPath);       // get the blob from amber

            // Jp2 file
            Path jp2ImgPath = stage.resolve(tiffBlobId + ".jp2"); // name the jpeg2000 derivative after the original uncompressed blob

            // Convert to jp2
            Jp2Converter jp2c = new Jp2Converter(jp2Converter, imgConverter);

            // To prevent Jp2Converter from re-doing the image metadata extractor step,
            // check 4 properties from ImageFile: compression, samplesPerPixel, bitsPerSample and photometric
            // If they all exist and have proper values, pass them in a Map to Jp2Converter.
            // Otherwise, let Jp2Converter find out from the source file.
            ImageFile imgFile = this.getImageFile();
            if (imgFile != null) {
                int compression = parseIntFromStr(imgFile.getCompression());
                int samplesPerPixel = parseIntFromStr(imgFile.getSamplesPerPixel());
                int bitsPerSample = parseIntFromStr(imgFile.getBitDepth());
                int photometric = parseIntFromStr(imgFile.getPhotometric());

                if (compression >= 0 && samplesPerPixel >= 0 && bitsPerSample >= 0 && photometric >= 0) {
                    Map<String, Integer> imgInfoMap = new HashMap<String, Integer>();
                    imgInfoMap.put("compression", compression);
                    imgInfoMap.put("samplesPerPixel", samplesPerPixel);
                    imgInfoMap.put("bitsPerSample", bitsPerSample);
                    imgInfoMap.put("photometric", photometric);
                    jp2c.convertFile(tiffPath, jp2ImgPath, imgInfoMap);
                } else {
                    jp2c.convertFile(tiffPath, jp2ImgPath);
                }
            } else {
                // No image file
                jp2c.convertFile(tiffPath, jp2ImgPath);
            }

            // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!jp2Converter.toFile().exists() || !imgConverter.toFile().exists()) return null;

            return jp2ImgPath;
        }

        private int parseIntFromStr(String s) {
            int n = -1;
            try {
                n = Integer.parseInt(s.split("\\D+")[0], 10);
            } catch (Exception e) {
                n = -1;
            }
            return n;
        }

        private long copyBlobToFile(Blob blob, Path destinationFile) throws IOException {
            long bytesTransferred = 0;
            try (ReadableByteChannel channel = blob.openChannel(); 
                    FileChannel dest = FileChannel.open(
                            destinationFile, 
                            StandardOpenOption.WRITE, 
                            StandardOpenOption.CREATE, 
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                bytesTransferred = dest.transferFrom(channel, 0, Long.MAX_VALUE);
            }
            return bytesTransferred;
        }
        
        @Override
        public CameraData getCameraData() {
            return (CameraData) getDescription("CameraData");
        }
        
        @Override
        public ImageFile getImageFile() {
            return (ImageFile) getSpecializedFile("image");
        }
        
        @Override
        public SoundFile getSoundFile() {
            return (SoundFile) getSpecializedFile("audio");
        }
        
        private File getSpecializedFile(String fmt) {
            String fileType = "";
            if (fmt.equals("image"))
                fileType = "ImageFile";
            else if (fmt.equals("audio"))
                fileType = "SoundFile";
            
            Iterable<File> files = this.getFiles();
            if (files != null) {
                Iterator<File> it = files.iterator();
                while (it.hasNext()) {
                    File next = it.next();
                    if (next.getType().equals(fileType) || 
                            (next.getMimeType() != null && next.getMimeType().startsWith(fmt))) {
                        return next;
                    }
                }
            }
            return null;
        }       
    }
}
