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

@TypeValue("EADFeature")
public interface EADFeature extends Description {

    @Property("featureType")
    public String getFeatureType();
    
    @Property("featureType")
    public void setFeatureType(String featureType);
    
    @Property("featureId")
    public String getFeatureId();
    
    @Property("featureId")
    public void setFeatureId(String featureId);
    
    @Property("fields")
    public String getJSONFields();
    
    @Property("fields")
    public void setJSONFields(String fields);
    
    @Property("records")
    public String getJSONRecords();
    
    @Property("records")
    public void setJSONRecords(String records);
    
    @JavaHandler
    public List<String> getFields() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setFields(List<String> fields) throws JsonGenerationException, JsonMappingException, IOException;
    
    @JavaHandler
    public List<List<String>> getRecords() throws JsonParseException, JsonMappingException, IOException;;
    
    @JavaHandler
    public void setRecords(List<List<String>> records) throws JsonGenerationException, JsonMappingException, IOException;
    
    abstract class Impl extends Description.Impl implements JavaHandlerContext<Vertex>, EADFeature {
        static ObjectMapper mapper = new ObjectMapper();
        
        @Override
        public List<String> getFields() throws JsonParseException, JsonMappingException, IOException {
            String fields = getJSONFields();
            if (fields == null || fields.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(fields, new TypeReference<List<String>>() {});
        }
        
        @Override
        public void setFields(List<String> fields) throws JsonGenerationException, JsonMappingException, IOException {
            setJSONFields(mapper.writeValueAsString(fields));
        }
        
        @Override
        public List<List<String>> getRecords() throws JsonParseException, JsonMappingException, IOException {
            String records = getJSONRecords();
            if (records == null || records.isEmpty())
                return new ArrayList<List<String>>();
            return mapper.readValue(records, new TypeReference<List<List<String>>>() {});
        }
        
        @Override
        public void setRecords(List<List<String>> records) throws JsonGenerationException, JsonMappingException, IOException {
            setJSONRecords(mapper.writeValueAsString(records));
        }
    }
}
