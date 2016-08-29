package amberdb.v2.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Acknowledgement extends AmberModel {
    @Column(name="ack_type")
    private String type;
    @Column(name="kind_of_support")
    private String kindOfSupport;
    @Column(name="weighting")
    private Double weighting;
    @Column(name="url_to_original")
    private String urlToOriginal;
    @Column
    private Date date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKindOfSupport() {
        return kindOfSupport;
    }

    public void setKindOfSupport(String kindOfSupport) {
        this.kindOfSupport = kindOfSupport;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public String getUrlToOriginal() {
        return urlToOriginal;
    }

    public void setUrlToOriginal(String urlToOriginal) {
        this.urlToOriginal = urlToOriginal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
