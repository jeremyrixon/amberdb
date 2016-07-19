package amberdb.v2.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Geocoding extends Node {

    @Column
    private String mapDatum;
    @Column
    private String latitude;
    @Column
    private Date timestamp;
    @Column
    private String longitude;

    public String getMapDatum() {
        return mapDatum;
    }

    public void setMapDatum(String mapDatum) {
        this.mapDatum = mapDatum;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
