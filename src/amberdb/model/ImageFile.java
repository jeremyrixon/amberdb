package amberdb.model;

import java.util.Date;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("ImageFile")
public interface ImageFile extends File {
    
    @Property("resolution")
    public String getResolution();

    @Property("resolution")
    public void setResolution(String resolution);
    
    // examples:
    //   inch
    //   cm
    @Property("resolutionUnit")
    public String getResolutionUnit();
    
    @Property("resolutionUnit")
    public void setResolutionUnit(String resolutionUnit);
    
    @Property("colourSpace")
    public String getColourSpace();
    
    @Property("colourSpace")
    public void setColourSpace(String colourSpace);
    
    @Property("orientation")
    public String getOrientation();
    
    @Property("orientation")
    public void setOrientation(String orientation);
    
    /**
     * In pixels
     */
    @Property("imageWidth")
    public Integer getImageWidth();
    
    /**
     * In pixels
     */
    @Property("imageWidth")
    public void setImageWidth(Integer imageWidth);
            
    /**
     * In pixels
     */
    @Property("imageLength")
    public Integer getImageLength();
    
    /**
     * In pixels
     */
    @Property("imageLength")
    public void setImageLength(Integer imageLength);    
    
    @Property("manufacturerMake")
    public String getManufacturerMake();
        
    @Property("manufacturerMake")
    public void setManufacturerMake(String manufacturerMake);
    
    @Property("manufacturerModelName")
    public String getManufacturerModelName();
        
    @Property("manufacturerModelName")
    public void setManufacturerModelName(String manufacturerModelName);
    
    @Property("manufacturerSerialNumber")
    public String getManufacturerSerialNumber();
        
    @Property("manufacturerSerialNumber")
    public void setManufacturerSerialNumber(String manufacturerSerialNumber);
    
    @Property("applicationDateCreated")
    public Date getApplicationDateCreated();
        
    @Property("applicationDateCreated")
    public void setApplicationDateCreated(Date applicationDateCreated);
    
    @Property("application")
    public String getApplication();
        
    @Property("application")
    public void setApplication(String application);
    
    @Property("dateDigitised")
    public Date getDateDigitised();
        
    @Property("dateDigitised")
    public void setDateDigitised(Date dateDigitised);
    
    @Property("bitDepth")
    public String getBitDepth();
        
    @Property("bitDepth")
    public void setBitDepth(String bitDepth);
    
    @Property("location")
    public String getLocation();
    
    @Property("location")
    public void setLocation(String location);
    
    @Property("colourProfile")
    public String getColourProfile();    
    
    @Property("colourProfile")
    public void setColourProfile(String colourProfile);

    @Property("cpLocation")
    public String getCpLocation();    
    
    @Property("cpLocation")
    public void setCpLocation(String cpLocation);
    
    @Property("zoomLevel")
    public String getZoomLevel();
    
    @Property("zoomLevel")
    public void setZoomLevel(String zoomLevel);
    
    
    @Property("exposureTime")
    public String getExposureTime();
    
    @Property("exposureTime")
    public void setExposureTime(String exposureTime);
    
    @Property("exposureFNumber")
    public String getExposureFNumber();
    
    @Property("exposureFNumber")
    public void setExposureFNumber(String exposureFNumber);
    
    @Property("exposureMode")
    public String getExposureMode();
    
    @Property("exposureMode")
    public void setExposureMode(String exposureMode);
    
    @Property("exposureProgram")
    public String getExposureProgram();
    
    @Property("exposureProgram")
    public void setExposureProgram(String exposureProgram);
    
    @Property("isoSpeedRating")
    public String getISOSpeedRating();
    
    @Property("isoSpeedRating")
    public void setISOSpeedRating(String isoSpeedRating);
    
    @Property("focalLength")
    public String getFocalLength();
    
    @Property("focalLength")
    public void setFocalLength(String focalLength);
    
    @Property("lens")
    public String getLens();
    
    @Property("lens")
    public void setLens(String lens);
    
    @Property("meteringMode")
    public String getMeteringMode();
    
    @Property("meteringMode")
    public void setMeteringMode(String meteringMode);
    
    @Property("whiteBalance")
    public String getWhiteBalance();
    
    @Property("whiteBalance")
    public void setWhiteBalance(String whiteBalance);
    
    @Property("fileSource")
    public String getFileSource();
    
    @Property("fileSource")
    public void setFileSource(String fileSource);
}
