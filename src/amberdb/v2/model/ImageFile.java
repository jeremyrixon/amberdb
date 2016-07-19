package amberdb.v2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class ImageFile extends Node {

    @Column
    private String extent;
    @Column
    private String fileName;
    @Column
    private String localSystemNumber;
    @Column
    private String software;
    @Column
    private String encodingLevel;
    @Column
    private String language;
    @Column
    private String mimeType;
    @Column
    private String resolution;
    @Column
    private String manufacturerSerialNumber;
    @Column
    private String holdingId;
    @Column
    private String resolutionUnit;
    @Column
    private int imageWidth;
    @Column
    private String manufacturerMake;
    @Column
    private String manufacturerModelName;
    @Column
    private String encoding;
    @Column
    private String deviceSerialNumber;
    @Column
    private int fileSize;
    @Column
    private String bitDepth;
    @Column
    private String publisher;
    @Column
    private String compression;
    @Column
    private String device;
    @Column
    private int imageLength;
    @Column
    private String colourSpace;
    @Column
    private String standardId;
    @Column
    private String title;
    @Column
    private Boolean australianContent;
    @Column
    private String contributor;
    @Column
    private String checksum;
    @Column
    private String recordSource;
    @Column
    private String bibId;
    @Column
    private String coverage;
    @Column
    private String orientation;
    @Column
    private String creator;
    @Column
    private String colourProfile;
    @Column
    private Date checksumGenerationDate;
    @Column
    private String applicationDateCreated;
    @Column
    private String coordinates;
    @Column
    private String creatorStatement;
    @Column
    private String fileFormatVersion;
    @Column
    private String dateDigitised;
    @Column
    private String holdingNumber;
    @Column
    private String application;
    @Column
    private String series;
    @Column
    private int blobId;
    @Column
    private String softwareSerialNumber;
    @Column
    private String checksumType;
    @Column
    private String location;
    @Column
    private String fileFormat;

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
