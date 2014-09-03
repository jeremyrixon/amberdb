package amberdb.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

import amberdb.model.Work;

public interface EADWork extends Work {
    @Property("rdsAcknowledgementType")
    public String getRdsAcknowledgementType();
    
    @Property("rdsAcknowledgementType")
    public void setRdsAcknowledgementType(String rdsAcknowledgementType);
    
    @Property("rdsAcknowledgementReceiver")
    public String getRdsAcknowledgementReceiver();
    
    @Property("rdsAcknowledgementReceiver")
    public void setRdsAcknowledgementReceiver(String rdsAcknowledgementReceiver);
    
    @Property("eadUpdateReviewRequired")
    public String getEADUpdateReviewRequired();
    
    @Property("eadUpdateReviewRequired")
    public void setEADUpdateReviewRequired(String eadUpdateReviewRequired);
    
    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getFolder() to get this property.
     */
    @Property("folder")
    public String getJSONFolder();
    
    /**
     * This property is encoded as a JSON Array - You probably want to use 
     * setFolder() to set this property.
     */
    @Property("folder")
    public void setJSONFolder(String folder);
    
    /**
     * This method handles the JSON deserialisation of the folder property.
     * Each folder entry is returned as <folder type>-<folder number>
     */
    @JavaHandler
    public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * This method handles the JSON serialisation of the folder property.
     * Each folder input entry should be formatted as <folder type>-<folder number>
     */
    @JavaHandler
    public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException;
    
    abstract class Impl extends Work.Impl implements JavaHandlerContext<Vertex>, EADWork {
        @Override
        public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolder());
        }
        
        @Override
        public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolder(serialiseToJSON(folder));
        }
        
        private List<String> deserialiseJSONString(String json) throws JsonParseException, JsonMappingException, IOException {
            if (json == null || json.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(json, new TypeReference<List<String>>() {
            });
        }

        private String serialiseToJSON(Collection<String> list) throws JsonParseException, JsonMappingException, IOException {
            return mapper.writeValueAsString(list);
        }
    }
}
