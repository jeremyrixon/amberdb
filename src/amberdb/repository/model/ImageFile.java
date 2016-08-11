package amberdb.repository.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class ImageFile extends File {
    @Column
    private String resolution;
    @Column
    private String resolutionUnit;
    @Column
    private String colourSpace;
    @Column
    private String orientation;
    @Column
    private String imageWidth;
    @Column
    private String imageLength;
    @Column
    private String manufacturerMake;
    @Column
    private String manufacturerModelName;
    @Column
    private String manufacturerSerialNumber;
    @Column
    private Date applicationDateCreated;
    @Column
    private String application;
    @Column
    private Date dateDigitised;
    @Column
    private String samplesPerPixel;
    @Column
    private String bitDepth;
    @Column
    private String photometric;
    @Column
    private String location;
    @Column
    private String colourProfile;
    @Column
    private String cpLocation;
    @Column
    private String zoomLevel;

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getResolutionUnit() {
        return resolutionUnit;
    }

    public void setResolutionUnit(String resolutionUnit) {
        this.resolutionUnit = resolutionUnit;
    }

    public String getColourSpace() {
        return colourSpace;
    }

    public void setColourSpace(String colourSpace) {
        this.colourSpace = colourSpace;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageLength() {
        return imageLength;
    }

    public void setImageLength(String imageLength) {
        this.imageLength = imageLength;
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

    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }

    public Date getApplicationDateCreated() {
        return applicationDateCreated;
    }

    public void setApplicationDateCreated(Date applicationDateCreated) {
        this.applicationDateCreated = applicationDateCreated;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Date getDateDigitised() {
        return dateDigitised;
    }

    public void setDateDigitised(Date dateDigitised) {
        this.dateDigitised = dateDigitised;
    }

    public String getSamplesPerPixel() {
        return samplesPerPixel;
    }

    public void setSamplesPerPixel(String samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    public String getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(String bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getPhotometric() {
        return photometric;
    }

    public void setPhotometric(String photometric) {
        this.photometric = photometric;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getColourProfile() {
        return colourProfile;
    }

    public void setColourProfile(String colourProfile) {
        this.colourProfile = colourProfile;
    }

    public String getCpLocation() {
        return cpLocation;
    }

    public void setCpLocation(String cpLocation) {
        this.cpLocation = cpLocation;
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(String zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
}
