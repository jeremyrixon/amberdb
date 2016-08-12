package amberdb.repository.model;

import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.enums.MaterialType;
import amberdb.repository.dao.associations.CopyAssociationDao;
import amberdb.repository.mappers.AmberDbMapperFactory;
import amberdb.util.EPubConverter;
import amberdb.util.Jp2Converter;
import amberdb.util.PdfTransformerFop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import doss.Blob;
import doss.BlobStore;
import doss.NoSuchBlobException;
import doss.Writable;
import doss.core.Writables;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.io.Reader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Copy extends Node {
    @Column
    private String dcmCopyPid;
    @Column
    private String dcmSourceCopy;
    @Column
    private String dcmDateTimeCreated;
    @Column
    private String dcmDateTimeUpdated;
    @Column
    private String dcmRecordCreator;
    @Column
    private String dcmRecordUpdater;
    @Column
    private String currentVersion;
    @Column
    private String versionNumber;
    @Column
    private String copyType;
    @Column
    private String copyRole;
    @Column
    private String carrier;
    @Column
    private String algorithm;
    @Column
    private String bestCopy;
    @Column
    private String materialType;
    @Column
    private String dateCreated;
    @Column
    private String condition;
    @Column
    private String exhibition;
    @Column
    private String copyStatus;
    @Column
    private String timedStatus;
    @Column
    private String segmentIndicator;
    @Column(name="manipulation")
    private String jsonManipulation;
    @Column
    private String otherNumbers;

    static final Logger log = LoggerFactory.getLogger(Copy.class);
    static ObjectMapper mapper = new ObjectMapper();
    protected CopyAssociationDao copyDao;

    public Copy() {
        super();
        copyDao = jdbiHelper.getDbi().onDemand(CopyAssociationDao.class);
    }

    // Adjacencies

    public Copy getSourceCopy() {
        return copyDao.getSourceCopy(this.getId());
    }

    public Iterable<Copy> getDerivatives() {
        return copyDao.getDerivatives(this.getId());
    }

    public Iterable<Copy> getDerivatives(CopyRole copyRole) {
        return copyDao.getDerivatives(this.getId(), copyRole.code());
    }

    public void setSourceCopy(Copy sourceCopy) {
        // TODO - implement this
        //copyDao.setSourceCopy(this.getId(), sourceCopy, txnStart);
    }

    public void removeSourceCopy(final Copy sourceCopy) {
        // TODO - implement this
    }

    public Copy getComasterCopy() {
        return copyDao.getComaster(this.getId());
    }

    public void setComasterCopy(Copy comasterCopy) {
        // TODO - implement this
    }

    public Iterable<File> getFiles() {
        return copyDao.getFiles(this.getId());
    }

    public File getFile() {
        List<File> files = copyDao.getFiles(this.getId());
        return files != null && !files.isEmpty() ? files.get(0) : null;
    }

    public CameraData addCameraData() {
        // TODO - impelment this
        return null;
    }

    public File addFile() {
        // TODO
        return null;
    }

    public ImageFile addImageFile() {
        // TODO
        return null;
    }

    public SoundFile addSoundFile() {
        // TODO
        return null;
    }

    public MovingImageFile addMovingImageFile() {
        // TODO
        return null;
    }

    void removeFile(final File file) {
        // TODO
    }

    public Work getWork() {
        return copyDao.getWork(this.getId());
    }

    public Iterable<Work> getRepresentedWorks() {
        return copyDao.getRepresentedWorks(this.getId());
    }

    // Implementation

    public File addFile(Path source, String mimeType, BlobStore blobStore) throws IOException {
        return addFile(Writables.wrap(source), mimeType, blobStore);
    }

    public File addLegacyDossFile(Path dossPath, String mimeType, BlobStore blobStore) throws IOException {
        File file = (mimeType.startsWith("image")) ? addImageFile() : addFile();
        storeLegacyDossFile(file, dossPath, mimeType, blobStore);
        return file;
    }

    public File addFile(Writable contents, String mimeType, BlobStore blobStore) throws IOException {
        File file = null;
        MaterialType mt = MaterialType.fromMimeType(mimeType);
        if (mt != null && (mt == MaterialType.IMAGE || mt == MaterialType.SOUND || mt == MaterialType.MOVINGIMAGE)) {
            if (mt == MaterialType.IMAGE) {
                file = addImageFile();
            } else if (mt == MaterialType.SOUND) {
                file = addSoundFile();
            } else if (mt == MaterialType.MOVINGIMAGE) {
                file = addMovingImageFile();
            }
            this.setMaterialType(mt.code());
        } else {
            file = addFile();
        }
        storeFile(file, contents, mimeType, blobStore);
        return file;
    }

    private void storeFile(File file, Writable contents, String mimeType, BlobStore blobStore) throws IOException {
        file.put(contents, blobStore);
        file.setMimeType(mimeType);
    }

    private void storeLegacyDossFile(File file, Path dossPath, String mimeType, BlobStore blobStore) throws IOException {
        file.putLegacyDoss(dossPath, blobStore);
        file.setMimeType(mimeType);
    }

    public Copy deriveEPubCopy(Path epubConverterPath, BlobStore blobStore) throws Exception {
        File file = this.getFile();
        if (file == null) {
            return null;
        }

        // Check that the file is something that we can process
        String mimeType = file.getMimeType();
        if (!(mimeType.matches("application/.*"))) {
            throw new IllegalStateException(this.getWork().getObjId() + " is not a application/* type file. Unable to convert to EPub");
        }

        Path stage = null;
        try {
            // create a temporary file processing location for deriving the EPub file
            stage = Files.createTempDirectory("amberdb-derivative");

            // assume this Copy is a master copy and access the amber file
            Long blobId = (this.getFile() == null)? null: this.getFile().getBlobId();

            // generate the derivative.
            Path epubPath = generateEPubFile(blobStore, epubConverterPath, stage, blobId);

            // Set mimetype based on tika detection
            String epubMimeType = new Tika().detect(epubPath.toFile());

            // add the derived EPub to this Copy's work as an access copy
            Copy ac = null;
            if (epubPath != null) {
                Work work = this.getWork();

                // Replace the access copy for this work (there's only ever one for ebooks).
                ac = work.getCopy(CopyRole.ACCESS_COPY);
                if (ac == null) {
                    ac = work.addCopy(epubPath, CopyRole.ACCESS_COPY, epubMimeType, blobStore);
                    ac.setSourceCopy(this);
                } else {
                    ac.getFile().put(epubPath, blobStore);
                    Copy sc = ac.getSourceCopy();
                    if (!this.equals(sc)) {
                        ac.removeSourceCopy(sc);
                        ac.setSourceCopy(this);
                    }
                }

                File acf = ac.getFile();
                acf.setFileSize(Files.size(epubPath));
                acf.setMimeType(epubMimeType);
            }

            return ac;
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

    public Copy deriveJp2ImageCopy(Path jp2Converter, Path imgConverter, BlobStore blobStore) throws IllegalStateException, IOException, InterruptedException, Exception {
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

            // generate the derivative.
            Path jp2ImgPath = generateJp2Image(blobStore, jp2Converter, imgConverter, stage, imgBlobId);

            // Set mimetype based on file extension
            String jp2Filename = (jp2ImgPath == null || jp2ImgPath.getFileName() == null)? "" : jp2ImgPath.getFileName().toString();
            String jp2MimeType = "image/" + jp2Filename.substring(jp2Filename.lastIndexOf('.') + 1);

            // add the derived jp2 image to this Copy's work as an access copy
            Copy ac = null;
            if (jp2ImgPath != null) {
                Work work = this.getWork();

                // Replace the access copy for this work (there's only ever one for images).
                ac = work.getCopy(CopyRole.ACCESS_COPY);
                if (ac == null) {
                    ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, jp2MimeType, blobStore);
                    ac.setSourceCopy(this);
                } else {
                    ac.getImageFile().put(jp2ImgPath, blobStore);
                    Copy sc = ac.getSourceCopy();
                    if (!this.equals(sc)) {
                        ac.removeSourceCopy(sc);
                        ac.setSourceCopy(this);
                    }
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

    public Copy derivePdfCopy(CopyRole copyRole, BlobStore blobStore, Reader... stylesheets) throws IOException {
        File file = this.getFile();
        if (file == null)
            throw new RuntimeException("Failed to generate pdf copy for work " + getWork().getObjId() + " as no file can be found for this copy " + getObjId());
        if (!file.getMimeType().equals("application/xml")) {
            throw new RuntimeException("Failed to generate pdf copy for work " + getWork().getObjId() + " as this copy " + getObjId() + " is not an xml file.");
        }

        Copy pdfCopy = this.getWork().addCopy();
        pdfCopy.setCopyRole(copyRole.code());
        pdfCopy.setSourceCopy(this);
        byte[] pdfContent = PdfTransformerFop.transform(file.openStream(blobStore), stylesheets);
        pdfCopy.addFile(Writables.wrap(pdfContent), "application/pdf", blobStore);
        return pdfCopy;
    }

    public Copy derivePdfCopy(Path pdfConverter, Path stylesheet, Path altStylesheet, BlobStore blobStore) throws IllegalStateException, NoSuchBlobException, IOException, InterruptedException {
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

            // pdf finally...
            Path pdfPath = generatePdf(blobStore, pdfConverter, stylesheet, altStylesheet, stage, blobId);

            // add the derived pdf copy
            Copy pc = null;
            if (pdfPath != null) {
                EADWork work = this.getWork().asEADWork();
                pc = work.getCopy(CopyRole.FINDING_AID_PRINT_COPY);
                if (pc == null) {
                    pc = work.addCopy(pdfPath, CopyRole.FINDING_AID_PRINT_COPY, "application/pdf", blobStore);
                    pc.setSourceCopy(this);
                } else {
                    pc.getFile().put(pdfPath, blobStore);
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

    private void executeCmd(String[] cmd) throws IOException, InterruptedException {
        // Log command
        log.debug("Run command: ", StringUtils.join(cmd, ' '));

        // Execute command
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process p = builder.start();
        p.waitFor();
        int exitVal = p.exitValue();
        if (exitVal > 0) {
            throw new IOException("Error in executeCmd");
        }
    }

    public Map<String,String> getAllOtherNumbers() {

        String otherNumbers = getOtherNumbers();
        if (otherNumbers == null || otherNumbers.isEmpty())
            return new HashMap<String,String>();
        return deserialiseJSONString(otherNumbers, new TypeReference<Map<String, String>>() { } );

    }

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

    private Path generateEPubFile(BlobStore doss, Path epubConverterPath, Path stage, Long blobId) throws Exception {
        if (blobId == null) {
            throw new NoSuchCopyException(this.getWork().getId(), CopyRole.fromString(this.getCopyRole()));
        }

        // prepare the files for conversion
        Path tmpPath = stage.resolve(Long.toString(blobId));  // where to put the source retrieved from the amber blob
        copyBlobToFile(doss.get(blobId), tmpPath);  // get the blob from amber

        // Add the right file extension to filename based on the original file name
        String fileExtension = null;
        String filename = getFile().getFileName();
        if (filename != null && filename.contains(".")) {
            fileExtension = filename.substring(filename.indexOf("."));
        } else {
            // couldn't use the file name so try the mime type
            String mimetype = new Tika().detect(tmpPath.toFile());
            switch (mimetype) {
                case "application/x-mobipocket-ebook": fileExtension = ".mobi"; break;
                case "application/vnd.amazon.ebook": fileExtension = ".azw"; break;
                default: throw new RuntimeException(mimetype + ": Not a file that can be converted to epub");
            }
        }

        // Rename the file
        String newFilename = Long.toString(blobId) + fileExtension;
        Path srcPath = tmpPath.resolveSibling(newFilename);
        Files.move(tmpPath, srcPath);

        // Convert to EPub
        EPubConverter epubConverter = new EPubConverter(epubConverterPath);

        Path epubPath = stage.resolve(blobId + ".epub"); // name the epub derivative after the original uncompressed blob
        epubConverter.convertFile(srcPath, epubPath);

        // NOTE: to return null at this point to cater for TiffEcho and JP2Echo test cases running in Travis env.
        if (!epubConverterPath.toFile().exists()) return null;

        return epubPath;
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

    public CameraData getCameraData() {
        Description o = getDescription("CameraData");
        return (o == null) ? null : (CameraData) o;
    }

    public ImageFile getImageFile() {
        File o = getSpecializedFile("image");
        return (o == null) ? null : (ImageFile) o;
    }

    public SoundFile getSoundFile() {
        File o = getSpecializedFile("audio");
        return (o == null) ? null : (SoundFile) o;
    }

    public MovingImageFile getMovingImageFile() {
        File o = getSpecializedFile("video");
        if (o == null) {
            o = getSpecializedFile("application/mxf");
        }
        return (o == null) ? null : (MovingImageFile) o;
    }

    private File getSpecializedFile(String fmt) {
        String fileType = "";
        if (fmt.equals("image"))
            fileType = "ImageFile";
        else if (fmt.equals("audio"))
            fileType = "SoundFile";
        else if (fmt.equals("video") || fmt.equals("application/mxf"))
            fileType = "MovingImageFile";

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

    public int getCurrentIndex() {
        final Copy copy = this;
        int ind = Iterables.indexOf(getWork().getOrderedCopies(CopyRole.fromString(getCopyRole())),
                new Predicate<Copy>() {
                    @Override
                    public boolean apply(Copy iCopy) {
                        return copy.getId() == iCopy.getId();
                    }
                });
        return ind +1;
    }

    public void removeFileIfExists(){
        File file = getFile();
        if (file != null) {
            removeFile(file);
        }
    }

    public void setAllOtherNumbers( Map<String,String>  otherNumbers) throws JsonProcessingException {

        setOtherNumbers(mapper.writeValueAsString(otherNumbers));
    }

    public String getDcmCopyPid() {
        return dcmCopyPid;
    }

    public void setDcmCopyPid(String dcmCopyPid) {
        this.dcmCopyPid = dcmCopyPid;
    }

    public String getDcmSourceCopy() {
        return dcmSourceCopy;
    }

    public void setDcmSourceCopy(String dcmSourceCopy) {
        this.dcmSourceCopy = dcmSourceCopy;
    }

    public String getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public void setDcmDateTimeCreated(String dcmDateTimeCreated) {
        this.dcmDateTimeCreated = dcmDateTimeCreated;
    }

    public String getDcmDateTimeUpdated() {
        return dcmDateTimeUpdated;
    }

    public void setDcmDateTimeUpdated(String dcmDateTimeUpdated) {
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public void setDcmRecordCreator(String dcmRecordCreator) {
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public void setDcmRecordUpdater(String dcmRecordUpdater) {
        this.dcmRecordUpdater = dcmRecordUpdater;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getCopyType() {
        return copyType;
    }

    public void setCopyType(String copyType) {
        this.copyType = copyType;
    }

    public String getCopyRole() {
        return copyRole;
    }

    public void setCopyRole(String copyRole) {
        this.copyRole = copyRole;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getBestCopy() {
        return bestCopy;
    }

    public void setBestCopy(String bestCopy) {
        this.bestCopy = bestCopy;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getExhibition() {
        return exhibition;
    }

    public void setExhibition(String exhibition) {
        this.exhibition = exhibition;
    }

    public String getCopyStatus() {
        return copyStatus;
    }

    public void setCopyStatus(String copyStatus) {
        this.copyStatus = copyStatus;
    }

    public String getTimedStatus() {
        return timedStatus;
    }

    public void setTimedStatus(String timedStatus) {
        this.timedStatus = timedStatus;
    }

    public String getSegmentIndicator() {
        return segmentIndicator;
    }

    public void setSegmentIndicator(String segmentIndicator) {
        this.segmentIndicator = segmentIndicator;
    }

    public String getJSONManipulation() {
        return jsonManipulation;
    }

    public void setJSONManipulation(String jsonManipulation) {
        this.jsonManipulation = jsonManipulation;
    }

    public String getOtherNumbers() {
        return otherNumbers;
    }

    public void setOtherNumbers(String otherNumbers) {
        this.otherNumbers = otherNumbers;
    }

    public void setManipulation(List<String> manipulation) throws JsonProcessingException {
        setJSONManipulation(serialiseToJSON(manipulation));
    }

    public List<String> getManipulation() {
        String manipulation = getJSONManipulation();
        if (manipulation == null || manipulation.isEmpty())
            return new ArrayList<String>();
        return deserialiseJSONString(manipulation, new TypeReference<List<String>>() {});
    }
}
