package amberdb.repository.model;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Acknowledge extends Node {
    @Column
    public Long v_out;
    @Column
    public Long v_in;
    @Column
    public int edgeOrder;
    @Column
    public String ackType;
    @Column
    public Date date;
    @Column
    public String kindOfSupport;
    @Column
    public Double weighting;
    @Column
    public String urlToOriginal;

    public Long getV_out() {
        return v_out;
    }

    public void setV_out(Long v_out) {
        this.v_out = v_out;
    }

    public Long getV_in() {
        return v_in;
    }

    public void setV_in(Long v_in) {
        this.v_in = v_in;
    }

    public int getEdgeOrder() {
        return edgeOrder;
    }

    public void setEdgeOrder(int edgeOrder) {
        this.edgeOrder = edgeOrder;
    }

    public String getAckType() {
        return ackType;
    }

    public void setAckType(String ackType) {
        this.ackType = ackType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Party getParty() {
        // TODO - get Party
        return null;
    }
}
