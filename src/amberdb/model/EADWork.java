package amberdb.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import amberdb.NoSuchObjectException;
import amberdb.model.Work;
import amberdb.relation.DescriptionOf;
import amberdb.relation.IsPartOf;

@TypeValue("EADWork")
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
     * scopeContent: scope of content (aka: abstract) for this EAD work.
     */
    @Property("scopeContent")
    public String getScopeContent();
    
    /**
     * scopeContent: scope of content (aka: abstract) for this EAD work.
     */
    @Property("scopeContent")
    public void setScopeContent(String scopeContent);
    
    /**
     * dateRange: the time period covered in this EAD work.
     */
    @Property("dateRange")
    public String getDateRange();
    
    /**
     * dateRange: the time period covered in this EAD work.
     */
    @Property("dateRange")
    public void setDateRange(String dateRange);
    
    /**
     * repository: the repository that holds the collection this EADWork belongs to.
     */
    @Property("repository")
    public String getRepository();
    
    /**
     * repository: the repository that holds the collection this EADWork belongs to.
     */
    @Property("repository")
    public void setRepository(String repository);
    
    @Property("collectionNumber")
    public String getCollectionNumber();
    
    @Property("collectionNumber")
    public void setCollectionNumber(String collectionNumber);
    
    /**
     * componentLevel: the level within the collection for this EAD work.
     * Example component levels include series, subseries and file
     */
    @Property("componentLevel")
    public String getComponentLevel();
    
    /**
     * componentLevel: the level within the collection for this EAD work.
     * Example component levels include series, subseries and file
     */
    @Property("componentLevel")
    public void setComponentLevel(String componentLevel);
    
    /**
     * componentNumber: components are numbered within each level e.g. 1, and the
     *                  numbering may include linkage to parent e.g. 1.1
     */
    @Property("componentNumber")
    public String getComponentNumber();
    
    /**
     * componentNumber: components are numbered within each level e.g. 1, and the
     *                  numbering may include linkage to parent e.g. 1.1
     */
    @Property("componentNumber")
    public void setComponentNumber(String componentNumber);
    
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
    
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public EADWork addEADWork();
    
    @JavaHandler
    public EADWork getEADWork(long objectId);
    
    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    public EADEntity addEADEntity();
    
    @JavaHandler
    public List<EADEntity> getEADEntities();
    
    @JavaHandler
    public EADEntity getEADEntity(long objectId);
    
    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    public EADFeature addEADFeature();
    
    @JavaHandler
    public List<EADFeature> getEADFeatures();
    
    @JavaHandler
    public EADFeature getEADFeature(long objectId);
    
    abstract class Impl extends Work.Impl implements JavaHandlerContext<Vertex>, EADWork {
        @Override
        public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolder());
        }
        
        @Override
        public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolder(serialiseToJSON(folder));
        }
        
        @Override
        public EADWork getEADWork(long objectId) {
            EADWork component = this.g().getVertex(objectId, EADWork.class);
            if (component == null) {
                throw new NoSuchObjectException(objectId);
            }
            return component;
        }
        
        @Override
        public List<EADEntity> getEADEntities() {
            return this.getDescriptions(EADEntity.class);
        }
        
        @Override
        public EADEntity getEADEntity(long objectId) {
            return this.g().getVertex(objectId, EADEntity.class);
        }
        
        @Override
        public List<EADFeature> getEADFeatures() {
            return this.getDescriptions(EADFeature.class);
        }
        
        @Override
        public EADFeature getEADFeature(long objectId) {
            return this.g().getVertex(objectId, EADFeature.class);
        }
    }
}
