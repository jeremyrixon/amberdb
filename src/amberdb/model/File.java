package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * TODO: should use DOSS instead of file location
 */
@TypeValue("File")
public interface File extends Node {
    @Property("fileLocation")
    public String getFileLocation();
    
    @Property("fileLocation")
    public void setFileLocation(String fileLocation);
    
    @Property("fileSize")
    public int getFileSize();
    
    @Property("fileSize")
    public void setFileSize(int fileSize);
    
    @Property("checkSum")
    public String getCheckSum();
    
    @Property("checkSum")
    public void setCheckSum(String checkSum);
    
    @Property("fileLocation")
    public String getLocation();
    
    @Property("fileLocation")
    public void setLocation(String fileLocation);
}
