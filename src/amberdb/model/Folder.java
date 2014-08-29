package amberdb.model;

import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
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
    
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Folder addSubFolder();
}
