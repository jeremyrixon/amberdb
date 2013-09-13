package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("ImageFile")
public interface ImageFile extends File {
    @Property("resolution")
    public String getResolution();
    
    @Property("resolution")
    public void setResolution(String resolution);
    
    @Property("imageWidth")
    public int getImageWidth();
    
    @Property("imageWidth")
    public void setImageWidth(String imageWidth);
    
    @Property("imageHeight")
    public int getImageHeight();
    
    @Property("imageHeight")
    public void setImageHeight(int imageHeight);
}
