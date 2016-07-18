package amberdb.v2.model;

import amberdb.v2.model.mapper.FileMapper;
import amberdb.v2.model.mapper.MapWith;

import java.util.Date;

@MapWith(FileMapper.class)
public class File extends Node {

    private String extent;
    private String fileName;
    private String localSystemNumber;
    private String software;
    private String encodingLevel;
    private String standardId;
    private String language;
    private String mimeType;
    private String title;
    private String holdingId;
    private Boolean australianContent;
    private String contributor;
    private String checksum;
    private String recordSource;
    private String coverage;
    private String bibId;
    private String creator;
    private Date checksumGenerationDate;
    private String coordinates;
    private String encoding;
    private String holdingNumber;
    private int fileSize;
    private int blobId;
    private String checksumType;
    private String publisher;
    private String compression;
    private String device;
    private String fileFormat;

    public File(int id, int txn_start, int txn_end, String extent, String fileName, String localSystemNumber,
                String software, String encodingLevel, String standardId, String language, String mimeType,
                String title, String holdingId, Boolean australianContent, String contributor, String checksum,
                String recordSource, String coverage, String bibId, String creator, Date checksumGenerationDate,
                String coordinates, String encoding, String holdingNumber, int fileSize, int blobId,
                String checksumType, String publisher, String compression, String device, String fileFormat) {
        super(id, txn_start, txn_end);
        this.extent = extent;
        this.fileName = fileName;
        this.localSystemNumber = localSystemNumber;
        this.software = software;
        this.encodingLevel = encodingLevel;
        this.standardId = standardId;
        this.language = language;
        this.mimeType = mimeType;
        this.title = title;
        this.holdingId = holdingId;
        this.australianContent = australianContent;
        this.contributor = contributor;
        this.checksum = checksum;
        this.recordSource = recordSource;
        this.coverage = coverage;
        this.bibId = bibId;
        this.creator = creator;
        this.checksumGenerationDate = checksumGenerationDate;
        this.coordinates = coordinates;
        this.encoding = encoding;
        this.holdingNumber = holdingNumber;
        this.fileSize = fileSize;
        this.blobId = blobId;
        this.checksumType = checksumType;
        this.publisher = publisher;
        this.compression = compression;
        this.device = device;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
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

    public Date getChecksumGenerationDate() {
        return checksumGenerationDate;
    }

    public void setChecksumGenerationDate(Date checksumGenerationDate) {
        this.checksumGenerationDate = checksumGenerationDate;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getBlobId() {
        return blobId;
    }

    public void setBlobId(int blobId) {
        this.blobId = blobId;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
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

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
}
