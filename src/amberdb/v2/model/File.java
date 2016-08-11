package amberdb.v2.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class File extends AmberModel {

    @Column
    protected String extent;
    @Column
    protected String fileName;
    @Column
    protected String localSystemNumber;
    @Column
    protected String software;
    @Column
    protected String encodingLevel;
    @Column
    protected String standardId;
    @Column
    protected String language;
    @Column
    protected String mimeType;
    @Column
    protected String title;
    @Column
    protected String holdingId;
    @Column
    protected Boolean australianContent;
    @Column
    protected String contributor;
    @Column
    protected String checksum;
    @Column
    protected String recordSource;
    @Column
    protected String coverage;
    @Column
    protected String bibId;
    @Column
    protected String creator;
    @Column
    protected Date checksumGenerationDate;
    @Column
    protected String coordinates;
    @Column
    protected String encoding;
    @Column
    protected String holdingNumber;
    @Column
    protected int fileSize;
    @Column
    protected Long blobId;
    @Column
    protected String checksumType;
    @Column
    protected String publisher;
    @Column
    protected String compression;
    @Column
    protected String device;
    @Column
    protected String fileFormat;

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

    public Long getBlobId() {
        return blobId;
    }

    public void setBlobId(Long blobId) {
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
