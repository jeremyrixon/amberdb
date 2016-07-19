package amberdb.v2.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class EADFeature extends Node {

    @Column
    private String records;
    @Column
    private String featureType;
    @Column
    private String fields;
    @Column
    private String featureId;

    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
}
