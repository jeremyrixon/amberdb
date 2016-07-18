package amberdb.v2.model;

import amberdb.v2.model.mapper.ImageFileMapper;
import amberdb.v2.model.mapper.MapWith;

import java.util.Date;

@MapWith(ImageFileMapper.class)
public class ImageFile extends Node {
    private String extent;
    private String fileName;
    private String localSystemNumber;
    private String software;
    private String encodingLevel;
    private String language;
    private String mimeType;
    private String resolution;
    private String manufacturerSerialNumber;
    private String holdingId;
    private String resolutionUnit;
    private int imageWidth;
    private String manufacturerMake;
    private String manufacturerModelName;
    private String encoding;
    private String deviceSerialNumber;
    private int fileSize;
    private String bitDepth;
    private String publisher;
    private String compression;
    private String device;
    private int imageLength;
    private String colourSpace;
    private String standardId;
    private String title;
    private Boolean australianContent;
    private String contributor;
    private String checksum;
    private String recordSource;
    private String bibId;
    private String coverage;
    private String orientation;
    private String creator;
    private String colourProfile;
    private Date checksumGenerationDate;
    private String applicationDateCreated;
    private String coordinates;
    private String creatorStatement;
    private String fileFormatVersion;
    private String dateDigitised;
    private String holdingNumber;
    private String application;
    private String series;
    private int blobId;
    private String softwareSerialNumber;
    private String checksumType;
    private String location;
    private String fileFormat;

    public ImageFile(int id, int txn_start, int txn_end, String extent, String fileName, String localSystemNumber,
                     String software, String encodingLevel, String language, String mimeType, String resolution,
                     String manufacturerSerialNumber, String holdingId, String resolutionUnit, int imageWidth,
                     String manufacturerMake, String manufacturerModelName, String encoding, String deviceSerialNumber,
                     int fileSize, String bitDepth, String publisher, String compression, String device,
                     int imageLength, String colourSpace, String standardId, String title, Boolean australianContent,
                     String contributor, String checksum, String recordSource, String bibId, String coverage,
                     String orientation, String creator, String colourProfile, Date checksumGenerationDate,
                     String applicationDateCreated, String coordinates, String creatorStatement,
                     String fileFormatVersion, String dateDigitised, String holdingNumber, String application,
                     String series, int blobId, String softwareSerialNumber, String checksumType, String location,
                     String fileFormat) {
        super(id, txn_start, txn_end);
        this.extent = extent;
        this.fileName = fileName;
        this.localSystemNumber = localSystemNumber;
        this.software = software;
        this.encodingLevel = encodingLevel;
        this.language = language;
        this.mimeType = mimeType;
        this.resolution = resolution;
        this.manufacturerSerialNumber = manufacturerSerialNumber;
        this.holdingId = holdingId;
        this.resolutionUnit = resolutionUnit;
        this.imageWidth = imageWidth;
        this.manufacturerMake = manufacturerMake;
        this.manufacturerModelName = manufacturerModelName;
        this.encoding = encoding;
        this.deviceSerialNumber = deviceSerialNumber;
        this.fileSize = fileSize;
        this.bitDepth = bitDepth;
        this.publisher = publisher;
        this.compression = compression;
        this.device = device;
        this.imageLength = imageLength;
        this.colourSpace = colourSpace;
        this.standardId = standardId;
        this.title = title;
        this.australianContent = australianContent;
        this.contributor = contributor;
        this.checksum = checksum;
        this.recordSource = recordSource;
        this.bibId = bibId;
        this.coverage = coverage;
        this.orientation = orientation;
        this.creator = creator;
        this.colourProfile = colourProfile;
        this.checksumGenerationDate = checksumGenerationDate;
        this.applicationDateCreated = applicationDateCreated;
        this.coordinates = coordinates;
        this.creatorStatement = creatorStatement;
        this.fileFormatVersion = fileFormatVersion;
        this.dateDigitised = dateDigitised;
        this.holdingNumber = holdingNumber;
        this.application = application;
        this.series = series;
        this.blobId = blobId;
        this.softwareSerialNumber = softwareSerialNumber;
        this.checksumType = checksumType;
        this.location = location;
        this.fileFormat = fileFormat;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public void setLocalSystemNumber(String localSystemNumber) {
        this.localSystemNumber = localSystemNumber;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public void setEncodingLevel(String encodingLevel) {
        this.encodingLevel = encodingLevel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getResolutionUnit() {
        return resolutionUnit;
    }

    public void setResolutionUnit(String resolutionUnit) {
        this.resolutionUnit = resolutionUnit;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getManufacturerMake() {
        return manufacturerMake;
    }

    public void setManufacturerMake(String manufacturerMake) {
        this.manufacturerMake = manufacturerMake;
    }

    public String getManufacturerModelName() {
        return manufacturerModelName;
    }

    public void setManufacturerModelName(String manufacturerModelName) {
        this.manufacturerModelName = manufacturerModelName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(String bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getImageLength() {
        return imageLength;
    }

    public void setImageLength(int imageLength) {
        this.imageLength = imageLength;
    }

    public String getColourSpace() {
        return colourSpace;
    }

    public void setColourSpace(String colourSpace) {
        this.colourSpace = colourSpace;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public void setAustralianContent(Boolean australianContent) {
        this.australianContent = australianContent;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getColourProfile() {
        return colourProfile;
    }

    public void setColourProfile(String colourProfile) {
        this.colourProfile = colourProfile;
    }

    public Date getChecksumGenerationDate() {
        return checksumGenerationDate;
    }

    public void setChecksumGenerationDate(Date checksumGenerationDate) {
        this.checksumGenerationDate = checksumGenerationDate;
    }

    public String getApplicationDateCreated() {
        return applicationDateCreated;
    }

    public void setApplicationDateCreated(String applicationDateCreated) {
        this.applicationDateCreated = applicationDateCreated;
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

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    public void setFileFormatVersion(String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public String getDateDigitised() {
        return dateDigitised;
    }

    public void setDateDigitised(String dateDigitised) {
        this.dateDigitised = dateDigitised;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public int getBlobId() {
        return blobId;
    }

    public void setBlobId(int blobId) {
        this.blobId = blobId;
    }

    public String getSoftwareSerialNumber() {
        return softwareSerialNumber;
    }

    public void setSoftwareSerialNumber(String softwareSerialNumber) {
        this.softwareSerialNumber = softwareSerialNumber;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
}
