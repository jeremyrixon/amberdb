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
    public int getManufacturerMake();
        
    @Property("manufacturerMake")
    public void setManufacturerMake(String manufacturerMake);
    
    @Property("manufacturerModelName")
    public int getManufacturerModelName();
        
    @Property("manufacturerModelName")
    public void setManufacturerModelName(String manufacturerModelName);
    
    @Property("manufacturerSerialNumber")
    public int getManufacturerSerialNumber();
        
    @Property("manufacturerSerialNumber")
    public void setManufacturerSerialNumber(String manufacturerSerialNumber);
    
    @Property("applicationDateCreated")
    public int getApplicationDateCreated();
        
    @Property("applicationDateCreated")
    public void setApplicationDateCreated(String applicationDateCreated);
    
    @Property("application")
    public int getApplication();
        
    @Property("application")
    public void setApplication(String application);
    
    @Property("dateDigitised")
    public int getDateDigitised();
        
    @Property("dateDigitised")
    public void setDateDigitised(String dateDigitised);
    
    @Property("bitDepth")
    public int getBitDepth();
        
    @Property("bitDepth")
    public void setBitDepth(String bitDepth);
}
