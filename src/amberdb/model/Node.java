package amberdb.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import amberdb.PIUtil;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;

@TypeField("type")
public interface Node extends VertexFrame {
    
    /**
     * Access Conditions determine whether or not something can be 
     * displayed to the public. Also known as Public Availability
     * Values: Restricted, Unrestricted, Internal access only
     */
    @Property("accessConditions")
    public String getAccessConditions();

    /**
     * Access Conditions determine whether or not something can be 
     * displayed to the public. Also known as Public Availability
     * Values: Restricted, Unrestricted, Internal access only
     */
    @Property("accessConditions")
    public void setAccessConditions(String accessConditions);
    
    /**
     * Internal Access Conditions reflect display and access 
     * for internal content management applications.
     * Values: open, restricted, closed
     */
    @Property("internalAccessConditions")
    public String getInternalAccessConditions();
    
    /**
    * Internal Access Conditions reflect display and access      
    * for internal content management applications.
    * Values: open, restricted, closed
    */
    @Property("internalAccessConditions")
    public void setInternalAccessConditions(String internalAccessConditions);
    
    /**
     * get the expiry date of the access conditions.
     * @return the expiry date.  
     * Note: the expiry date is currently for display only, not enforced
     *       for the checking of the access conditions. 
     */
    @Property("expiryDate")
    public Date getExpiryDate();

    /**
     * set the expiry date on the access conditions.
     * @param expiryDate
     * Note: the expiry date is currently for display only, not enforced for
     *       the checking of the access conditions.
     */
    @Property("expiryDate")
    public void setExpiryDate(Date expiryDate);
    
    @Property("restrictionType")
    public String getRestrictionType();

    @Property("restrictionType")
    public void setRestrictionType(String restrictionType);
    
    @Property("notes")
    public String getNotes();

    @Property("notes")
    public void setNotes(String notes);
    
    @Property("type")
    public String getType();
    
  @JavaHandler
  abstract public long getId();
  
  @JavaHandler
  abstract public String getObjId();
  
    /**
     * This property is encoded as a JSON Array - You probably want to use getAlias to get this property
     */
    @Property("alias")
    public String getJSONAlias();
    
    /**
     * This property is encoded as a JSON Array - You probably want to use setAlias to set this property
     */
    @Property("alias")
    public void setJSONAlias(String alias);
    
    /**
     * This method handles the JSON serialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public void setAlias(List<String> alias) throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * This method handles the JSON deserialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public List<String> getAlias() throws JsonParseException, JsonMappingException, IOException;

    @Property("commentsInternal")
    public String getCommentsInternal();

    @Property("commentsInternal")
    public void setCommentsInternal(String commentsInternal);

    @Property("commentsExternal")
    public String getCommentsExternal();

    @Property("commentsExternal")
    public void setCommentsExternal(String commentsExternal);
        
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Node {

    @Override
    public long getId() {
      return toLong(asVertex().getId());
    }
    
    public long toLong(Object x) {
      // tingergraph converts ids to strings
      if (x instanceof String) {
        return Long.parseLong((String) x);
      }
      return (long)x; 
    }
    
    @Override
    public String getObjId() {
        return PIUtil.format(getId());
    }
       
        @Override
        public List<String> getAlias() throws JsonParseException, JsonMappingException, IOException {
            ObjectMapper mapper = new ObjectMapper();
            String alias = getJSONAlias();
            if (alias == null || alias.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(alias, new TypeReference<List<String>>() { } );
            
        }
        
        @Override
        public void setAlias( List<String>  alias) throws JsonParseException, JsonMappingException, IOException {
            ObjectMapper mapper = new ObjectMapper();
            setJSONAlias(mapper.writeValueAsString(alias));
        }
        
        
    }
}
