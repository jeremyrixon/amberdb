package amberdb.repository.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class GeoCoding extends Description {
    @Column
    public String gpsVersion;
    @Column
    public String latitude;
    @Column
    public String latitudeRef;
    @Column
    public String longitude;
    @Column
    public String longitudeRef;
    @Column
    public Date timestamp;
    @Column
    public String mapDatum;

    public String getGpsVersion() {
        return gpsVersion;
    }

    public void setGpsVersion(String gpsVersion) {
        this.gpsVersion = gpsVersion;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitudeRef() {
        return latitudeRef;
    }

    public void setLatitudeRef(String latitudeRef) {
        this.latitudeRef = latitudeRef;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitudeRef() {
        return longitudeRef;
    }

    public void setLongitudeRef(String longitudeRef) {
        this.longitudeRef = longitudeRef;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMapDatum() {
        return mapDatum;
    }

    public void setMapDatum(String mapDatum) {
        this.mapDatum = mapDatum;
    }
}
