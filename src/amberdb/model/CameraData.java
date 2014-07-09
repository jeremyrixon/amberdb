package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("CameraData")
public interface CameraData extends Description {
    @Property("exposureTime")
    public String getexposureTime();
    
    @Property("exposureTime")
    public void setexposureTime(String exposureTime);
    
    @Property("exposureFNumber")
    public String getexposureFNumber();
    
    @Property("exposureFNumber")
    public void setexposureFNumber(String exposureFNumber);
    
    @Property("exposureMode")
    public String getexposureMode();
    
    @Property("exposureMode")
    public void setexposureMode(String exposureMode);
    
    @Property("exposureProgram")
    public String getexposureProgram();
    
    @Property("exposureProgram")
    public void setexposureProgram(String exposureProgram);
    
    @Property("isoSpeedRating")
    public String getISOSpeedRating();
    
    @Property("isoSpeedRating")
    public void setISOSpeedRating(String isoSpeedRating);
    
    @Property("focalLenth")
    public String getFocalLenth();
    
    @Property("focalLenth")
    public void setFocalLenth(String focalLenth);
    
    @Property("lens")
    public String getLens();
    
    @Property("lens")
    public void setLens(String lens);
    
    @Property("meteringMode")
    public String getMeteringMode();
    
    @Property("meteringMode")
    public void setMeteringMode(String lens);
    
    @Property("whiteBalance")
    public String getWhiteBalance();
    
    @Property("whiteBalance")
    public void setWhiteBalance(String whiteBalance);
    
    @Property("fileSource")
    public String getFileSource();
    
    @Property("fileSource")
    public void setFileSource(String fileSource);
    
}
