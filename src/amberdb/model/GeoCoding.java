package amberdb.model;

import java.util.Date;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("GeoCoding")
public interface GeoCoding extends Description {
    @Property("latitude")
    public String getLatitude();
    
    @Property("latitude")
    public void setLatitude(String latitude);
    
    @Property("langitude")
    public String getLangitude();
    
    @Property("langitude")
    public void setLangitude(String langitude);
    
    @Property("timestamp")
    public Date getTimeStamp();
    
    @Property("timestamp")
    public void setTimeStamp(Date timestamp);
    
    @Property("mapDatum")
    public String getMapDatum();
    
    @Property("mapDatum")
    public void setMapDatum(String mapDatum);
}
