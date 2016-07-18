package amberdb.v2.model;

import amberdb.v2.model.mapper.EADFeatureMapper;
import amberdb.v2.model.mapper.MapWith;

@MapWith(EADFeatureMapper.class)
public class EADFeature extends Node {

    private String records;
    private String featureType;
    private String fields;
    private String featureId;

    public EADFeature(int id, int txn_start, int txn_end, String records, String featureType, String fields, String featureId) {
        super(id, txn_start, txn_end);
        this.records = records;
        this.featureType = featureType;
        this.fields = fields;
        this.featureId = featureId;
    }

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
