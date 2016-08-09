package amberdb.repository.model;

import javax.persistence.Column;

public class CameraData extends Description {
    @Column
    public String exposureTime;
    @Column
    public String exposureFNumber;
    @Column
    public String exposureMode;
    @Column
    public String exposureProgram;
    @Column
    public String isoSpeedRating;
    @Column
    public String focalLength;
    @Column
    public String lens;
    @Column
    public String meteringMode;
    @Column
    public String whiteBalance;
    @Column
    public String fileSource;

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
