package amberdb.v2.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class CameraData extends AmberModel {

    @Column
    private String extent;
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
    private String otherTitle;
    @Column
    private String holdingId;
    @Column
    private String holdingNumber;
    @Column
    private Boolean australianContent;
    @Column
    private String contributor;
    @Column
    private String publisher;
    @Column
    private String recordSource;
    @Column
    private String coverage;
    @Column
    private String bibId;
    @Column
    private String creator;
    @Column
    private String coordinates;
    @Column
    private String scaleEtc;
    @Column
    private String exposureTime;
    @Column
    private String exposureFNumber;
    @Column
    private String exposureMode;
    @Column
    private String exposureProgram;
    @Column
    private String isoSpeedRating;
    @Column
    private String focalLength;
    @Column
    private String lens;
    @Column
    private String meteringMode;
    @Column
    private String whiteBalance;
    @Column
    private String fileSource;

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
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

    public String getOtherTitle() {
        return otherTitle;
    }

    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public void setScaleEtc(String scaleEtc) {
        this.scaleEtc = scaleEtc;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getExposureFNumber() {
        return exposureFNumber;
    }

    public void setExposureFNumber(String exposureFNumber) {
        this.exposureFNumber = exposureFNumber;
    }

    public String getExposureMode() {
        return exposureMode;
    }

    public void setExposureMode(String exposureMode) {
        this.exposureMode = exposureMode;
    }

    public String getExposureProgram() {
        return exposureProgram;
    }

    public void setExposureProgram(String exposureProgram) {
        this.exposureProgram = exposureProgram;
    }

    public String getIsoSpeedRating() {
        return isoSpeedRating;
    }

    public void setIsoSpeedRating(String isoSpeedRating) {
        this.isoSpeedRating = isoSpeedRating;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getLens() {
        return lens;
    }

    public void setLens(String lens) {
        this.lens = lens;
    }

    public String getMeteringMode() {
        return meteringMode;
    }

    public void setMeteringMode(String meteringMode) {
        this.meteringMode = meteringMode;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public String getFileSource() {
        return fileSource;
    }

    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }
}
