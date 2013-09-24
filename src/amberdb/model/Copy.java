package amberdb.model;

import java.util.Date;

import amberdb.relation.IsFileOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Copy")
public interface Copy extends Node {        
    @Property("copyRole")
    public String getCopyRole();
    
    @Property("copyRole")
    public void setCopyRole(String copyRole);
    
    @Property("carrier")
    public String getCarrier();
    
    @Property("carrier")
    public void setCarrier(String carrier);
    
    @Property("sourceCopy")
    public String getSourceCopy();
    
    @Property("sourceCopy")
    public void setSourceCopy(String sourceCopy);
    
    @Property("accessConditions")
    public String getAccessConditions();
    
    @Property("accessConditions")
    public void setAccessConditions(String accessConditions);
    
    @Property("expiryDate")
    public Date getExpiryDate();
    
    @Property("expiryDate")
    public void setExpiryDate(Date expiryDate);
      
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public Iterable<File> getFiles();
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public File addFile();
    
    @Incidence(label = IsFileOf.label, direction = Direction.IN)
    public IsFileOf addFile(final File file);
        
    abstract class Impl implements JavaHandlerContext<Vertex>, Copy {
    }
}
