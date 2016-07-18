package amberdb.v2.model;

import amberdb.v2.model.mapper.CameraDataMapper;
import amberdb.v2.model.mapper.MapWith;

@MapWith(CameraDataMapper.class)
public class CameraData extends Node {

    private String extent;

    private String localSystemNumber;

    private String encodingLevel;

    private String standardId;

    private String language;

    private String title;

    private String otherTitle;

    private String holdingId;

    private String holdingNumber;

    private Boolean australianContent;

    private String contributor;

    private String publisher;

    private String recordSource;

    private String coverage;

    private String bibId;

    private String creator;

    private String coordinates;

    private String scaleEtc;

    private String exposureTime;

    private String exposureFNumber;

    private String exposureMode;

    private String exposureProgram;

    private String isoSpeedRating;

    private String focalLength;

    private String lens;

    private String meteringMode;

    private String whiteBalance;

    private String fileSource;

    public CameraData(int id, int txn_start, int txn_end, String extent, String localSystemNumber, String encodingLevel,
                      String standardId, String language, String title, String otherTitle, String holdingId,
                      String holdingNumber, Boolean australianContent, String contributor, String publisher,
                      String recordSource, String coverage, String bibId, String creator, String coordinates,
                      String scaleEtc, String exposureTime, String exposureFNumber, String exposureMode,
                      String exposureProgram, String isoSpeedRating, String focalLength, String lens, String meteringMode,
                      String whiteBalance, String fileSource) {
        super(id, txn_start, txn_end);
        this.extent = extent;
        this.localSystemNumber = localSystemNumber;
        this.encodingLevel = encodingLevel;
        this.standardId = standardId;
        this.language = language;
        this.title = title;
        this.otherTitle = otherTitle;
        this.holdingId = holdingId;
        this.holdingNumber = holdingNumber;
        this.australianContent = australianContent;
        this.contributor = contributor;
        this.publisher = publisher;
        this.recordSource = recordSource;
        this.coverage = coverage;
        this.bibId = bibId;
        this.creator = creator;
        this.coordinates = coordinates;
        this.scaleEtc = scaleEtc;
        this.exposureTime = exposureTime;
        this.exposureFNumber = exposureFNumber;
        this.exposureMode = exposureMode;
        this.exposureProgram = exposureProgram;
        this.isoSpeedRating = isoSpeedRating;
        this.focalLength = focalLength;
        this.lens = lens;
        this.meteringMode = meteringMode;
        this.whiteBalance = whiteBalance;
        this.fileSource = fileSource;
    }

    public String getExtent() {
        return extent;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public String getStandardId() {
        return standardId;
    }

    public String getLanguage() {
        return language;
    }

    public String getTitle() {
        return title;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public String getContributor() {
        return contributor;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getBibId() {
        return bibId;
    }

    public String getCreator() {
        return creator;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public String getExposureFNumber() {
        return exposureFNumber;
    }

    public String getExposureMode() {
        return exposureMode;
    }

    public String getExposureProgram() {
        return exposureProgram;
    }

    public String getIsoSpeedRating() {
        return isoSpeedRating;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public String getLens() {
        return lens;
    }

    public String getMeteringMode() {
        return meteringMode;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public String getFileSource() {
        return fileSource;
    }
}
