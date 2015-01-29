package amberdb.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

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
     * bibliography: bibliography info for a person.
     */
    @Property("bibliography")
    public String getBibliography();
    
    /**
     * bibliography: bibliography info for a person.
     */
    @Property("bibliography")
    public void setBibliography(String bibliography);
    
    /**
     * arrangement: the arrangement for the collection in hierarchical components
     */
    @Property("arrangement")
    public String getJSONArrangement();
    
    /**
     * arrangement: the arrangement for the collection in hierarchical components
     */
    @Property("arrangement")
    public void setJSONArrangement(String arrangement);
    
    @JavaHandler
    public List<String> getArrangement() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setArrangement(List<String> arrangement) throws JsonParseException, JsonMappingException, IOException;

    
    @Property("access")
    public String getJSONAccess();
    
    @Property("access")
    public void setJSONAccess(String access);
    
    @JavaHandler
    public List<String> getAccess() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setAccess(List<String> access) throws JsonParseException, JsonMappingException, IOException;

    
    @Property("copyingPublishing")
    public String getJSONCopyingPublishing();
    
    @Property("copyingPublishing")
    public void setJSONCopyingPublishing(String copyingPublishing);
    
    @JavaHandler
    public List<String> getCopyingPublishing() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setCopyingPublishing(List<String> copyingPublishing) throws JsonParseException, JsonMappingException, IOException;

    
    @Property("preferredCitation")
    public String getJSONPreferredCitation();
    
    @Property("preferredCitation")
    public void setJSONPreferredCitation(String preferredCitation);
    
    @JavaHandler
    public List<String> getPreferredCitation() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setPreferredCitation(List<String> preferredCitation) throws JsonParseException, JsonMappingException, IOException;

    
    @Property("relatedMaterial")
    public String getJSONRelatedMaterial();
    
    @Property("relatedMaterial")
    public void setJSONRelatedMaterial(String relatedMaterial);
    
    @JavaHandler
    public List<String> getRelatedMaterial() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setRelatedMaterial(List<String> relatedMaterial) throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * adminInfo: administrative info for an origanisation.
     */
    @Property("adminInfo")
    public String getAdminInfo();
    
    /**
     * adminInfo: administrative info for an origanisation.
     */
    @Property("adminInfo")
    public void setAdminInfo(String adminInfo);

    
    /**
     * correspondenceIndex: provide summary of correspondence indexed to entities associated
     * with this EADWork. 
     */
    @Property("correspondenceIndex")
    public String getCorrespondenceIndex();
    
    /**
     * correspondenceIndex: provide summary of correspondence indexed to entities associated
     * with this EADWork.
     */
    @Property("correspondenceIndex")
    public void setCorrespondenceIndex(String correspondenceIndex);
    
    @Property("provenance")
    public String getJSONProvenance();
    
    @Property("provenance")
    public void setJSONProvenance(String provenance);
    
    @JavaHandler
    public List<String> getProvenance() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setProvenance(List<String> provenance) throws JsonParseException, JsonMappingException, IOException;
    
    @Property("altform")
    public String getAltForm();
    
    @Property("altform")
    public void setAltForm(String altform);
        
    @Property("dateRangeInAS")
    public String getDateRangInAS();
    
    @Property("dateRangeInAS")
    public void setDateRangeInAS(String dateRangeInAS);
    
    @JavaHandler
    public String getFmttedDateRange() throws JsonParseException, JsonMappingException, IOException;
    
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
     * This property is encoded as a JSON Array - You probably want to use
     * getFolderType() to get this property.
     */
    @Property("folderType")
    public String getJSONFolderType();
    
    /**
     * This property is encoded as a JSON Array - You probably want to use 
     * setFolderType() to set this property.
     */
    @Property("folderType")
    public void setJSONFolderType(String folderType);
    
    @JavaHandler
    public List<String> getFolderType() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setFolderType(List<String> folderTypes) throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getFolderNumber() to get this property.
     */
    @Property("folderNumber")
    public String getJSONFolderNumber();
    
    /**
     * This property is encoded as a JSON Array - You probably want to use 
     * setFolderNumber() to set this property.
     */
    @Property("folderNumber")
    public void setJSONFolderNumber(String folderNumber);
    
    @JavaHandler
    public List<String> getFolderNumber() throws JsonParseException, JsonMappingException, IOException;
    
    @JavaHandler
    public void setFolderNumber(List<String> folderNumber) throws JsonParseException, JsonMappingException, IOException;
    
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
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        
        @Override
        public String getFmttedDateRange() throws JsonParseException, JsonMappingException, IOException {
            Date from = getStartDate();
            Date to = getEndDate();
            
            SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy");
            String fmttedFrom = (from == null)?"":dateFmt.format(from);
            String fmttedTo = (to == null)? "" : dateFmt.format(to);
                      
            if (fmttedFrom.startsWith("01/01") && (fmttedFrom.endsWith("00:00:00") || fmttedFrom.endsWith("12:00:00"))) {
                fmttedFrom = yearFmt.format(from);
            }

            if (fmttedTo.startsWith("31/12") && (fmttedTo.endsWith("23:59:59") || fmttedTo.endsWith("12:00:00"))) {
                fmttedTo = yearFmt.format(to);
            }
            return fmttedFrom + " - " + fmttedTo;
        }
        
        @Override
        public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolder());
        }
        
        @Override
        public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolder(serialiseToJSON(folder));
        }
        
        @Override
        public List<String> getFolderType() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolderType());
        }
        
        @Override
        public void setFolderType(List<String> folderTypes) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolderType(serialiseToJSON(folderTypes));
        }
        
        @Override
        public List<String> getFolderNumber() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolderNumber());
        }
        
        @Override
        public void setFolderNumber(List<String> folderNumbers) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolderNumber(serialiseToJSON(folderNumbers));
        }
        
        @Override
        public List<String> getArrangement() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONArrangement());
        }

        @Override
        public void setArrangement(List<String> arrangement) throws JsonParseException, JsonMappingException, IOException {
            setJSONArrangement(serialiseToJSON(arrangement));
        }
        
        @Override
        public List<String> getAccess() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONAccess());
        }
        
        @Override
        public void setAccess(List<String> access) throws JsonParseException, JsonMappingException, IOException {
            setJSONAccess(serialiseToJSON(access));
        }
        
        @Override
        public List<String> getCopyingPublishing() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONCopyingPublishing());
        }
        
        @Override
        public void setCopyingPublishing(List<String> copyingPublishing) throws JsonParseException, JsonMappingException, IOException {
            setJSONCopyingPublishing(serialiseToJSON(copyingPublishing));
        }
        
        @Override
        public List<String> getPreferredCitation() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONPreferredCitation());
        }
        
        @Override
        public void setPreferredCitation(List<String> preferredCitation) throws JsonParseException, JsonMappingException, IOException {
            setJSONPreferredCitation(serialiseToJSON(preferredCitation));
        }
        
        @Override
        public List<String> getRelatedMaterial() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONRelatedMaterial());
        }
        
        @Override
        public void setRelatedMaterial(List<String> relatedMaterial) throws JsonParseException, JsonMappingException, IOException {
            setJSONRelatedMaterial(serialiseToJSON(relatedMaterial));
        }
        
        @Override
        public List<String> getProvenance() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONProvenance());
        }
        
        @Override
        public void setProvenance(List<String> provenance) throws JsonParseException, JsonMappingException, IOException {
            setJSONProvenance(serialiseToJSON(provenance));
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

