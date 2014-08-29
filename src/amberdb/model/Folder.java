package amberdb.model;

import com.tinkerpop.frames.Property;

public interface Folder extends Work {

    @Property("folderType")
    public String getFolderType();
    
    @Property("folderType")
    public void setFolderType(String folderType);
    
    @Property("folderNo")
    public String getFolderNo();
    
    @Property("folderNo")
    public void setFolderNo(String folderNo);
}
