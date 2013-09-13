package amberdb.model;

import amberdb.relation.IsCopyOf;
import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Work")
public interface Work extends Node {    
    
    @Property("title")
    public String getTitle();
    
    @Property("title")
    public void setTitle(String title);
    
    @Property("creator")
    public String getCreator();
    
    @Property("creator")
    public void setCreator();
    
    @Property("subUnitType")
    public String getSubUnitType();
    
    @Property("subUnitType")
    public void setSubUnitType(String subUnitType);
    
    @Property("bibLevel")
    public String getBibLevel();
    
    @Property("bibLevel")
    public void setBibLevel(String bibLevel);

    @Property("digitalStatus")
    public String getDigitalStatus();
    
    @Property("digitalStatus")
    public void setDigitalStatus(String digitalStatus);
      
    @Adjacency(label = IsPartOf.label)
    public void setParent(final Work parent);
    
    @Adjacency(label = IsPartOf.label, direction = Direction.OUT)
    public void addChild(final Work part);
       
    @Adjacency(label = IsPartOf.label)
    public Work getParent(); 
    
    @Adjacency(label = IsPartOf.label, direction = Direction.OUT)
    public Iterable<Work> getChildren();
    
    @Adjacency(label = IsCopyOf.label)
    public IsCopyOf addCopy(final Copy copy);
      
    @Adjacency(label = IsCopyOf.label)
    public Iterable<Copy> getCopies();  
    
    @GremlinGroovy("it.out('isCopyOf').has('copyRole',role.code)")
    public Copy getCopy(@GremlinParam("role") Copy.Role role);
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Work {
    	
    }
}
