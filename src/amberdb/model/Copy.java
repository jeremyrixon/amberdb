package amberdb.model;

import java.lang.ProcessBuilder.Redirect;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import amberdb.relation.*;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.util.Jp2Converter;
import amberdb.util.PdfTransformerFop;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import doss.Blob;
import doss.BlobStore;
import doss.NoSuchBlobException;
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
    
    @Property("algorithm")
    public String getAlgorithm();
    
    @Property("algorithm")
    public void setAlgorithm(String algorithm);
        
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
    
    @Property("html")
    public String getHtml();
    
    @Property("html")
    public void setHtml(String html);

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

    @Adjacency(label = IsSourceCopyOf.label, direction=Direction.IN)
    public Iterable<Copy> getDerivatives();
    
    @JavaHandler
    public Iterable<Copy> getDerivatives(CopyRole copyRole);

    @Adjacency(label = IsSourceCopyOf.label, direction=Direction.OUT)
    public void setSourceCopy(Copy sourceCopy);

    @Adjacency(label = IsComasterOf.label, direction=Direction.OUT)
    public Copy getComasterCopy();

    @Adjacency(label = IsComasterOf.label, direction=Direction.OUT)
    public void setComasterCopy(Copy comasterCopy);

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
    
    @Adjacency(label = Represents.label)
    public Iterable<Work> getRepresentedWorks();
    
    @JavaHandler
    Copy deriveJp2ImageCopy(Path jp2Converter, Path imgConverter) throws IllegalStateException, IOException, InterruptedException, Exception;

    @JavaHandler
    Copy derivePdfCopy(Path pdfConverter, Path stylesheet, Path altStylesheet) throws IllegalStateException, NoSuchBlobException, IOException, InterruptedException;
    
    @JavaHandler
    Copy derivePdfCopy(CopyRole copyRole, Reader... stylesheets) throws IOException;
    
    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, Copy {
        static final Logger log = LoggerFactory.getLogger(Copy.class);
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
            } else if (mimeType.startsWith("audio")) {
                file = addSoundFile();
                this.setMaterialType("Sound");
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

        @Override
        public Copy deriveJp2ImageCopy(Path jp2Converter, Path imgConverter) throws IllegalStateException, IOException, InterruptedException, Exception {
            ImageFile imgFile = this.getImageFile();
            if (imgFile == null) {
                // Is not an image
                return null;
            }

            String mimeType = imgFile.getMimeType();

            // Do we need to check?
            if (!(mimeType.equals("image/tiff") || mimeType.equals("image/jpeg"))) {
                throw new IllegalStateException(this.getWork().getObjId() + " master is not a tiff or jpeg. You may not generate a jpeg2000 from anything but a tiff or a jpeg");
            }

            Path stage = null;
            try {
                // create a temporary file processing location for deriving the jpeg2000 from the master/comaster
                stage = Files.createTempDirectory("amberdb-derivative");

                // assume this Copy is a master copy and access the amber file
                Long imgBlobId = (this.getFile() == null)? null: this.getFile().getBlobId();

                // get this copy's blob store.
                BlobStore doss = AmberSession.ownerOf(g()).getBlobStore();

                // generate the derivative.
                Path jp2ImgPath = generateJp2Image(doss, jp2Converter, imgConverter, stage, imgBlobId);

                // Set mimetype based on file extension
                String jp2Filename = (jp2ImgPath == null || jp2ImgPath.getFileName() == null)? "" : jp2ImgPath.getFileName().toString();
                String jp2MimeType = "image/" + jp2Filename.substring(jp2Filename.lastIndexOf('.') + 1);

                // add the derived jp2 image to this Copy's work as an access copy
                Copy ac = null;
                if (jp2ImgPath != null) {
                    Work work = this.getWork();

                    // Replace the derivative for this copy, not all.
                    Iterable<Copy> derivatives = this.getDerivatives();
                    if (Iterables.size(derivatives) == 0) {
                        ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, jp2MimeType);
                        ac.setSourceCopy(this);
                    } else {
                        ac = Iterables.get(derivatives, 0);
                        ac.getImageFile().put(jp2ImgPath);
                    }

                    ImageFile acf = ac.getImageFile();

                    // add image metadata based on the master image metadata
                    // this is used by some nla delivery systems eg: tarkine
                    acf.setImageLength(imgFile.getImageLength());
                    acf.setImageWidth(imgFile.getImageWidth());
                    acf.setResolution(imgFile.getResolution());
                    acf.setFileFormat("jpeg2000");
                    acf.setFileSize(Files.size(jp2ImgPath));
                    acf.setMimeType(jp2MimeType);
                }
                return ac;

            } catch (Exception e) {
                throw e;
            } finally {
                // clean up temporary working space
                if (stage != null) {
                    java.io.File[] files = stage.toFile().listFiles();
                    if (files != null) {
                        for (java.io.File f : files) {
                            f.delete();
                        }
                    }
                    stage.toFile().delete();
                }
            }
        }
        
        @Override
        public Copy derivePdfCopy(CopyRole copyRole, Reader... stylesheets) throws IOException {
            File file = this.getFile();
            if (file == null)
                throw new RuntimeException("Failed to generate pdf copy for work " + getWork().getObjId() + " as no file can be found for this copy " + getObjId());
            if (!file.getMimeType().equals("application/xml")) {
                throw new RuntimeException("Failed to generate pdf copy for work " + getWork().getObjId() + " as this copy " + getObjId() + " is not an xml file.");
            }
            
            Copy pdfCopy = this.getWork().addCopy();
            pdfCopy.setCopyRole(copyRole.code());
            pdfCopy.setSourceCopy(this);
            byte[] pdfContent = PdfTransformerFop.transform(file.openStream(), stylesheets);
            pdfCopy.addFile(Writables.wrap(pdfContent), "application/pdf");
            return pdfCopy;
        }
        
        @Override
        public Copy derivePdfCopy(Path pdfConverter, Path stylesheet, Path altStylesheet) throws IllegalStateException, NoSuchBlobException, IOException, InterruptedException {
            File eadFile = this.getFile();
            if (!eadFile.getMimeType().equals("application/xml")) {
                throw new IllegalStateException("Failed to generate pdf from this copy. " + this.getWork().getObjId() + " " + getCopyRole() + " copy is not a xml file.");
            }
            Path stage = null;
            try {
                // create a temporary file processing location for generating pdf
                stage = Files.createTempDirectory("amberdb-derivative");
                
                // assume this Copy is a FINDING_AID_COPY
                Long blobId = this.getFile().getBlobId();
                
                // get this copy's blob store...
                BlobStore doss = AmberSession.ownerOf(g()).getBlobStore();
                
                // pdf finally...
                Path pdfPath = generatePdf(doss, pdfConverter, stylesheet, altStylesheet, stage, blobId);
                
                // add the derived pdf copy
                Copy pc = null;
                if (pdfPath != null) {
                    EADWork work = this.getWork().asEADWork();
                    pc = work.getCopy(CopyRole.FINDING_AID_PRINT_COPY);
                    if (pc == null) {
                        pc = work.addCopy(pdfPath, CopyRole.FINDING_AID_PRINT_COPY, "application/pdf");
                        pc.setSourceCopy(this);
                    } else {
                        pc.getFile().put(pdfPath);
                    }
                    File pcf = pc.getFile();
                    pcf.setFileFormat("pdf");
                }
                return pc;
            } finally {
                // clean up temporary working space
                if (stage != null) {
                    java.io.File[] files = stage.toFile().listFiles();
                    if (files != null) {
                        for (java.io.File f : files)
                            f.delete();
                    }
                    stage.toFile().delete();
                }
            }
        }

        private Path generatePdf(BlobStore doss, Path pdfConverter, Path stylesheet, Path altStylesheet, Path stage,
                Long blobId) throws NoSuchBlobException, IOException, InterruptedException {
            if (blobId == null)
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.fromString(getCopyRole()));
            
            // prepare file for conversion
            Path eadPath = stage.resolve(blobId + ".xml"); // where to put the ead xml from the amber
            copyBlobToFile(doss.get(blobId), eadPath);
            
            // pdf file
            Path pdfPath = stage.resolve(blobId + ".pdf"); // name the pdf derivative after the original blob id
            
            // Convert to pdf
            convertToPDF(pdfConverter, stylesheet, altStylesheet, eadPath, pdfPath);
            return pdfPath;
        }

        private void convertToPDF(Path pdfConverter, Path stylesheet, Path altStylesheet, Path eadPath, Path pdfPath) throws IOException, InterruptedException {
            try {
                executeCmd(new String[] {
                        pdfConverter.toString(),
                        "-xml",
                        eadPath.toString(),
                        "-xsl",
                        stylesheet.toString(),
                        "-pdf",
                        pdfPath.toString()
                });
            } catch (IOException | InterruptedException e) {
                executeCmd(new String[] {
                        pdfConverter.toString(),
                        "-xml",
                        eadPath.toString(),
                        "-xsl",
                        altStylesheet.toString(),
                        "-pdf",
                        pdfPath.toString()
                });
            }
            
        }
        
        // Execute a command
        private void executeCmd(String[] cmd) throws IOException, InterruptedException {
            // Log command
            log.debug("Run command: ", StringUtils.join(cmd, ' '));

            // Execute command
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT);
            Process p = builder.start();
            p.waitFor();
            int exitVal = p.exitValue();
            if (exitVal > 0) {
                throw new IOException("Error in executeCmd");
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
        public Iterable<Copy> getDerivatives(CopyRole copyRole) {
            Iterable<Copy> copies = getDerivatives();
            List<Copy> limitedCopies = new ArrayList<>(); 
            if (copies != null) {
                for (Copy copy : copies) {
                    if (copy.getCopyRole().equals(copyRole.code())) {
                        limitedCopies.add(copy);
                    }
                }
            }
            return (Iterable<Copy>) limitedCopies;
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
            uncompressPb.redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT);
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
            jp2Pb.redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT);
            Process jp2Process = jp2Pb.start();
            jp2Process.waitFor();
            int convertResult = jp2Process.exitValue(); // really should check it's worked

            // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
            if (!tiffUncompressor.toFile().exists() || !jp2Generator.toFile().exists()) return null;
            return jp2ImgPath;
        }

        private Path generateJp2Image(BlobStore doss, Path jp2Converter, Path imgConverter, Path stage, Long imgBlobId) throws IOException, InterruptedException, NoSuchCopyException, Exception {
            if (imgBlobId == null) {
                throw new NoSuchCopyException(this.getWork().getId(), CopyRole.fromString(this.getCopyRole()));
            }

            // prepare the files for conversion
            Path tmpPath = stage.resolve("" + imgBlobId);  // where to put the source retrieved from the amber blob
            copyBlobToFile(doss.get(imgBlobId), tmpPath);  // get the blob from amber

            // Add the right file extension to filename based on mime type
            // This is to prevent kdu_compress from failing when a tif file is named as .jpg, etc.
            Tika tika = new Tika();
            String mimeType = tika.detect(tmpPath.toFile());
            String fileExtension = null;
            if ("image/tiff".equals(mimeType)) {
                fileExtension = ".tif";
            } else if ("image/jpeg".equals(mimeType)) {
                fileExtension = ".jpg";
            } else {
                // Will add support for other mime types (eg. raw) later
                throw new RuntimeException("Not a tiff or a jpeg file");
            }

            // Rename the file
            String newFilename = "" + imgBlobId + fileExtension;
            Path srcImgPath = tmpPath.resolveSibling(newFilename);
            Files.move(tmpPath, srcImgPath);

            // Convert to jp2
            Jp2Converter jp2c = new Jp2Converter(jp2Converter, imgConverter);

            // To prevent Jp2Converter from re-doing the image metadata extractor step,
            // check 4 properties from ImageFile: compression, samplesPerPixel, bitsPerSample and photometric
            // If they all exist and have proper values, pass them in a Map to Jp2Converter.
            // Otherwise, let Jp2Converter find out from the source file.
            Map<String, String> imgInfoMap = null;
            ImageFile imgFile = this.getImageFile();
            if (imgFile != null && "image/tiff".equals(imgFile.getMimeType())) {
                // Only check image properties for tiff files
                int compression = parseIntFromStr(imgFile.getCompression());
                int samplesPerPixel = parseIntFromStr(imgFile.getSamplesPerPixel());
                int bitsPerSample = parseIntFromStr(imgFile.getBitDepth());
                int photometric = parseIntFromStr(imgFile.getPhotometric());
                if (compression >= 0 && samplesPerPixel >= 0 && bitsPerSample >= 0 && photometric >= 0) {
                    imgInfoMap = new HashMap<String, String>();
                    imgInfoMap.put("mimeType", imgFile.getMimeType());
                    imgInfoMap.put("compression", "" + compression);
                    imgInfoMap.put("samplesPerPixel", "" + samplesPerPixel);
                    imgInfoMap.put("bitsPerSample", "" + bitsPerSample);
                    imgInfoMap.put("photometric", "" + photometric);
                }
            }

            Path jp2ImgPath = null;
            // Try to convert it to .jp2
            try {
                jp2ImgPath = stage.resolve(imgBlobId + ".jp2"); // name the jpeg2000 derivative after the original uncompressed blob
                jp2c.convertFile(srcImgPath, jp2ImgPath, imgInfoMap);
            } catch (Exception e1) {
                // If failed, try to convert it to .jpx
                jp2ImgPath = stage.resolve(imgBlobId + ".jpx");
                jp2c.convertFile(srcImgPath, jp2ImgPath, imgInfoMap);
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
            Description o = getDescription("CameraData");
            return (o == null) ? null : this.g().frame(o.asVertex(), CameraData.class);
        }
        
        @Override
        public ImageFile getImageFile() {
            File o = getSpecializedFile("image");
            return (o == null) ? null : this.g().frame(o.asVertex(), ImageFile.class);
        }
        
        @Override
        public SoundFile getSoundFile() {
            File o = getSpecializedFile("audio");
            return (o == null) ? null : this.g().frame(o.asVertex(), SoundFile.class);
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
