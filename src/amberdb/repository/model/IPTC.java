package amberdb.repository.model;

import javax.persistence.Column;

public class IPTC extends Description {
    @Column
    public String alternativeTitle;
    @Column
    public String subLocation;
    @Column
    public String city;
    @Column
    public String province;
    @Column
    public String country;
    @Column
    public String isoCountryCode;
    @Column
    public String worldRegion;
    @Column
    public String digitalSourceType;
    @Column
    public String event;
    @Column
    public String fileFormat;

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getSubLocation() {
        return subLocation;
    }

    public void setSubLocation(String subLocation) {
        this.subLocation = subLocation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public void setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    public String getWorldRegion() {
        return worldRegion;
    }

    public void setWorldRegion(String worldRegion) {
        this.worldRegion = worldRegion;
    }

    public String getDigitalSourceType() {
        return digitalSourceType;
    }

    public void setDigitalSourceType(String digitalSourceType) {
        this.digitalSourceType = digitalSourceType;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
}
