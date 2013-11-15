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
}
