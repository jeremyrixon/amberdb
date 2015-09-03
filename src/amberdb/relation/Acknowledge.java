package amberdb.relation;

import java.util.Date;

import com.tinkerpop.frames.Property;

/**
 * 
 * @author bsingh
 *
 */

public interface Acknowledge extends Relation {
    public static final String label = "acknowledge";

    @Property("ackType")
    public String getAckType();

    @Property("ackType")
    public void setAckType(String ackType);

    @Property("kindOfSupport")
    public String getKindOfSupport();

    @Property("kindOfSupport")
    public void setKindOfSupport(String kindOfSupport);

    @Property("weighting")
    public Double getWeighting();

    @Property("weighting")
    public void setWeighting(Double weighting);

    @Property("urlToOriginial")
    public String getUrlToOriginial();

    @Property("urlToOriginial")
    public void setUrlToOriginial(String urlToOriginial);

    // date of acknowledgement
    @Property("date")
    public Date getDate();

    // date of acknowledgement
    @Property("date")
    public void setDate(Date date);
}