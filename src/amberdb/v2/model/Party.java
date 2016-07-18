package amberdb.v2.model;


import amberdb.v2.model.mapper.MapWith;
import amberdb.v2.model.mapper.PartyMapper;

@MapWith(PartyMapper.class)
public class Party extends Node {

    private String name;
    private Boolean suppressed;
    private String orgUrl;
    private String logoUrl;

    public Party(int id, int txn_start, int txn_end, String name, Boolean suppressed, String orgUrl, String logoUrl) {
        super(id, txn_start, txn_end);
        this.name = name;
        this.suppressed = suppressed;
        this.orgUrl = orgUrl;
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSuppressed() {
        return suppressed;
    }

    public void setSuppressed(Boolean suppressed) {
        this.suppressed = suppressed;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
