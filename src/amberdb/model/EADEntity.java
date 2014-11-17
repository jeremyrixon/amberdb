package amberdb.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("EADEntity")
public interface EADEntity extends Description{

    @Property("entityType")
    public String getEntityType();
    
    @Property("entityType")
    public void setEntityType(String entityType);
    
    @Property("entityName")
    public String getJSONEntityName();
    
    @Property("entityName")
    public void setJSONEntityName(String partyName);
    
    @Property("correspondenceId")
    public String getJSONCorrespondenceId();
    
    @Property("correspondenceId")
    public void setJSONCorrespondenceId(String correspondenceId);
    
    @Property("correspondenceRef")
    public String getCorrespondencRef();
    
    @Property("correspondenceRef")
    public void setCorrespondenceRef(String correspondenceRef);
    
    @JavaHandler
    public List<String> getEntityName() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setEntityName(List<String> partyName) throws JsonGenerationException, JsonMappingException, IOException;
    
    @JavaHandler
    public List<String> getCorrespondenceId() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setCorrespondenceId(List<String> correspondenceId) throws JsonGenerationException, JsonMappingException, IOException;
    
    abstract class Impl extends Description.Impl implements JavaHandlerContext<Vertex>, EADEntity {
        static ObjectMapper mapper = new ObjectMapper();
        
        @Override
        public List<String> getEntityName() throws JsonParseException, JsonMappingException, IOException {
            String entityName = getJSONEntityName();
            if (entityName == null || entityName.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(entityName, new TypeReference<List<String>>() {});
        }
        
        @Override
        public void setEntityName(List<String> entityName) throws JsonGenerationException, JsonMappingException, IOException {
            setJSONEntityName(mapper.writeValueAsString(entityName));
        }
        
        @Override
        public List<String> getCorrespondenceId() throws JsonParseException, JsonMappingException, IOException {
            String correspondenceId = getJSONCorrespondenceId();
            if (correspondenceId == null || correspondenceId.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(correspondenceId, new TypeReference<List<String>>() {});
        }
        
        @Override
        public void setCorrespondenceId(List<String> correspondenceId) throws JsonGenerationException, JsonMappingException, IOException {
            setJSONCorrespondenceId(mapper.writeValueAsString(correspondenceId));
        }
    }
}
