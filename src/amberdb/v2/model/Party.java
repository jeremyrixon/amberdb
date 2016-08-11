package amberdb.v2.model;


import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Party extends AmberModel {

    @Column
    private String name;
    @Column
    private Boolean suppressed;
    @Column
    private String orgUrl;
    @Column
    private String logoUrl;

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
