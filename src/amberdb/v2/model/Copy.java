package amberdb.v2.model;

import amberdb.AmberSession;
import amberdb.NoSuchCopyException;
import amberdb.enums.CopyRole;
import amberdb.model.*;
import amberdb.model.ImageFile;
import amberdb.util.Jp2Converter;
import amberdb.v2.model.mapper.AmberDbMapperFactory;
import doss.Blob;
import doss.BlobStore;
import org.apache.tika.Tika;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Copy extends AmberModel {

    @Column
    private Date dcmDateTimeUpdated;
    @Column
    private String extent;
    @Column
    private String dcmRecordUpdater;
    @Column
    private String localSystemNumber;
    @Column
    private String encodingLevel;
    @Column
    private String standardId;
    @Column
    private String language;
    @Column
    private String title;
    @Column
    private String holdingId;
    @Column
    private String internalAccessConditions;
    @Column
    private Boolean australianContent;
    @Column
    private Date dateCreated;
    @Column
    private String contributor;
    @Column
    private String timedStatus;
    @Column
    private String copyType;
    @Column
    private String alias;
    @Column
    private String copyStatus;
    @Column
    private String copyRole;
    @Column
    private String manipulation;
    @Column
    private String recordSource;
    @Column
    private String algorithm;
    @Column
    private String bibId;
    @Column
    private String creator;
    @Column
    private String otherNumbers;
    @Column
    private Date dcmDateTimeCreated;
    @Column
    private String materialType;
    @Column
    private String commentsExternal;
    @Column
    private String coordinates;
    @Column
    private String creatorStatement;
    @Column
    private String classification;
    @Column
    private String currentVersion;
    @Column
    private String commentsInternal;
    @Column
    private String bestCopy;
    @Column
    private String carrier;
    @Column
    private String holdingNumber;
    @Column
    private String series;
    @Column
    private String publisher;
    @Column
    private String dcmRecordCreator;
    @Column
    private String dcmCopyPid;

    public String getManipulation() {
        return manipulation;
    }

    public void setManipulation(String manipulation) {
        this.manipulation = manipulation;
    }

    public Date getDcmDateTimeUpdated() {
        return dcmDateTimeUpdated;
    }

    public void setDcmDateTimeUpdated(Date dcmDateTimeUpdated) {
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public void setDcmRecordUpdater(String dcmRecordUpdater) {
        this.dcmRecordUpdater = dcmRecordUpdater;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public void setLocalSystemNumber(String localSystemNumber) {
        this.localSystemNumber = localSystemNumber;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public void setEncodingLevel(String encodingLevel) {
        this.encodingLevel = encodingLevel;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public void setInternalAccessConditions(String internalAccessConditions) {
        this.internalAccessConditions = internalAccessConditions;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public void setAustralianContent(Boolean australianContent) {
        this.australianContent = australianContent;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getTimedStatus() {
        return timedStatus;
    }

    public void setTimedStatus(String timedStatus) {
        this.timedStatus = timedStatus;
    }

    public String getCopyType() {
        return copyType;
    }

    public void setCopyType(String copyType) {
        this.copyType = copyType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCopyStatus() {
        return copyStatus;
    }

    public void setCopyStatus(String copyStatus) {
        this.copyStatus = copyStatus;
    }

    public String getCopyRole() {
        return copyRole;
    }

    public void setCopyRole(String copyRole) {
        this.copyRole = copyRole;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOtherNumbers() {
        return otherNumbers;
    }

    public void setOtherNumbers(String otherNumbers) {
        this.otherNumbers = otherNumbers;
    }

    public Date getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public void setDcmDateTimeCreated(Date dcmDateTimeCreated) {
        this.dcmDateTimeCreated = dcmDateTimeCreated;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCommentsExternal() {
        return commentsExternal;
    }

    public void setCommentsExternal(String commentsExternal) {
        this.commentsExternal = commentsExternal;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getCreatorStatement() {
        return creatorStatement;
    }

    public void setCreatorStatement(String creatorStatement) {
        this.creatorStatement = creatorStatement;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public void setCommentsInternal(String commentsInternal) {
        this.commentsInternal = commentsInternal;
    }

    public String getBestCopy() {
        return bestCopy;
    }

    public void setBestCopy(String bestCopy) {
        this.bestCopy = bestCopy;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public void setDcmRecordCreator(String dcmRecordCreator) {
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public String getDcmCopyPid() {
        return dcmCopyPid;
    }

    public void setDcmCopyPid(String dcmCopyPid) {
        this.dcmCopyPid = dcmCopyPid;
    }

    // TODO - use ImageFileDao to get image
    public ImageFile getImageFile() {
        return null;
    }

    // TODO - use FileDao to get file
    public File getFile() {
        return null;
    }

    public Copy deriveJp2ImageCopy(Path jp2Converter, Path imgConverter) throws IllegalStateException, IOException, InterruptedException, Exception {
        amberdb.model.ImageFile imgFile = this.getImageFile();
        if (imgFile == null) {
            // Is not an image
            return null;
        }

        String mimeType = imgFile.getMimeType();

        // Do we need to check?
        if (!(mimeType.equals("image/tiff") || mimeType.equals("image/jpeg"))) {
            throw new IllegalStateException(this.getId() + " master is not a tiff or jpeg. You may not generate a jpeg2000 from anything but a tiff or a jpeg");
        }

        Path stage = null;
        try {
            // create a temporary file processing location for deriving the jpeg2000 from the master/comaster
            stage = Files.createTempDirectory("amberdb-derivative");

            // assume this Copy is a master copy and access the amber file
            Long imgBlobId = (this.getFile() == null)? null: this.getFile().getBlobId();

            // get this copy's blob store.
            // TODO - get blobstore from session
            BlobStore doss = null;//AmberSession.ownerOf(g()).getBlobStore();

            // generate the derivative.
            Path jp2ImgPath = generateJp2Image(doss, jp2Converter, imgConverter, stage, imgBlobId);

            // Set mimetype based on file extension
            String jp2Filename = (jp2ImgPath == null || jp2ImgPath.getFileName() == null)? "" : jp2ImgPath.getFileName().toString();
            String jp2MimeType = "image/" + jp2Filename.substring(jp2Filename.lastIndexOf('.') + 1);

            // add the derived jp2 image to this Copy's work as an access copy
            Copy ac = null;
            if (jp2ImgPath != null) {
                // TODO - get Work from Copy
                amberdb.model.Work work = null;//this.getWork();

                // Replace the access copy for this work (there's only ever one for images).
                // TODO - use CopyDao to get copy and do Copy stuff
//                ac = work.getCopy(CopyRole.ACCESS_COPY);
//                if (ac == null) {
//                    ac = work.addCopy(jp2ImgPath, CopyRole.ACCESS_COPY, jp2MimeType);
//                    ac.setSourceCopy(this);
//                } else {
//                    ac.getImageFile().put(jp2ImgPath);
//                    Copy sc = ac.getSourceCopy();
//                    if (!this.equals(sc)) {
//                        ac.removeSourceCopy(sc);
//                        ac.setSourceCopy(this);
//                    }
//                }

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

    private Path generateJp2Image(BlobStore doss, Path jp2Converter, Path imgConverter, Path stage, Long imgBlobId) throws IOException, InterruptedException, NoSuchCopyException, Exception {
        if (imgBlobId == null) {
            throw new NoSuchCopyException(this.getId(), CopyRole.fromString(this.getCopyRole()));
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
}
