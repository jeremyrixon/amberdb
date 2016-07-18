package amberdb.v2.model;


import amberdb.v2.model.mapper.GeocodingMapper;
import amberdb.v2.model.mapper.MapWith;

import java.util.Date;

@MapWith(GeocodingMapper.class)
public class Geocoding extends Node {

    private String mapDatum;
    private String latitude;
    private Date timestamp;
    private String longitude;

    public Geocoding(int id, int txn_start, int txn_end, String mapDatum, String latitude, Date timestamp, String longitude) {
        super(id, txn_start, txn_end);
        this.mapDatum = mapDatum;
        this.latitude = latitude;
        this.timestamp = timestamp;
        this.longitude = longitude;
    }

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
