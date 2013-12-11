package amberdb.model;

import com.tinkerpop.frames.Property;

public interface AccessImageFile extends ImageFile {
    @Property("location")
    String getLocation();
    
    @Property("location")
    void setLocation(String location);
}
