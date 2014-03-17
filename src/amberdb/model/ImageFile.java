package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("ImageFile")
public interface ImageFile extends File {
    
    @Property("resolution")
    public String getResolution();
    
    @Property("resolution")
    public void setResolution(String resolution);
    
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
    public int getImageWidth();
    
    /**
     * In pixels
     */
    @Property("imageWidth")
    public void setImageWidth(int imageWidth);
            
    /**
     * In pixels
     */
    @Property("imageLength")
    public int getImageLength();
    
    /**
     * In pixels
     */
    @Property("imageLength")
    public void setImageLength(int imageLength);    
    
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
    public String getApplicationDateCreated();
        
    @Property("applicationDateCreated")
    public void setApplicationDateCreated(String applicationDateCreated);
    
    @Property("application")
    public String getApplication();
        
    @Property("application")
    public void setApplication(String application);
    
    @Property("dateDigitised")
    public String getDateDigitised();
        
    @Property("dateDigitised")
    public void setDateDigitised(String dateDigitised);
    
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
}
