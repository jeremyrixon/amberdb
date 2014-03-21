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
     * get the access conditions
     * @return the access conditions, e.g. restricted, unrestricted.
     */
    @Property("accessConditions")
    public String getAccessConditions();

    /**
     * set the access conditions. e.g. restricted and unrestricted
     * @param accessConditions
     */
    @Property("accessConditions")
    public void setAccessConditions(String accessConditions);
    
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
    
	@JavaHandler
	abstract public long getId();
	
	@JavaHandler
	abstract public String getObjId();
	
    /**
     * This property is encoded as a JSON Array - You probably want to use getAliases to get this property
     */
    @Property("aliases")
    public String getJSONAliases();
    
    /**
     * This property is encoded as a JSON Array - You probably want to use setAliases to set this property
     */
    @Property("aliases")
    public void setJSONAliases(String aliases);
    
    /**
     * This method handles the JSON serialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public void setAliases(List<String> aliases) throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * This method handles the JSON deserialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public List<String> getAliases() throws JsonParseException, JsonMappingException, IOException;

    @Property("commentsInternal")
    public String getCommentsInternal();

    @Property("commentsInternal")
    public void setCommentsInternal(String commentsInternal);

    @Property("commentsExternal")
    public String getcommentsExternal();

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
        public List<String> getAliases() throws JsonParseException, JsonMappingException, IOException {
            ObjectMapper mapper = new ObjectMapper();
            String aliases = getJSONAliases();
            if (aliases == null || aliases.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(aliases, new TypeReference<List<String>>() { } );
            
        }
        
        @Override
        public void setAliases( List<String>  aliases) throws JsonParseException, JsonMappingException, IOException {
            ObjectMapper mapper = new ObjectMapper();
            setJSONAliases(mapper.writeValueAsString(aliases));
        }
        
        
    }
}
