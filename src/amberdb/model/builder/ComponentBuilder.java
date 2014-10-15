package amberdb.model.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.EADWork;
import amberdb.model.File;
import amberdb.model.Work;

public class ComponentBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    static final ObjectMapper mapper = new ObjectMapper();       
    
    public static List<EADWork> mergeComponents(EADWork collection, JsonNode... components) {
        List<EADWork> componentWorks = new ArrayList<>();
        if (collection.getChildren() == null) {
            for (JsonNode component : components) {
                EADWork componentWork = collection.addEADWork();
                updateComponentData(componentWork, component);
                componentWorks.add(componentWork);
            }
        } else {
            for (JsonNode component : components) {
                EADWork componentWork;
                if (collection.isNewComponent(component)) 
                    componentWork = collection.addEADWork();
                else
                    componentWork = updateComponentPath(collection, component);
                updateComponentData(componentWork, component);
                componentWorks.add(componentWork);
            }
        }
        return componentWorks;
    }
    
    /**
     * mergeComponent checks the component for the type of merge required:
     *  - NEW_COMP, a new component to be added to the collection.
     *  - UPDATED_COMP_DATA, an existing component in the collection with its metadata required to be updated.
     *  - UPDATED_COMP_PATH, an existing component in the collection required to be re-attached to a different
     *                       parent EADwork.
     * 
     * @param collection - the top level work of a collection with the new updated EAD finding aid attached as 
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet updated
     *                     from the new updated FINING_AID_COPY.
     *                    
     * an existing FINDING_AID_COPY (not the new
     *                     updated EAD finding aid yet) attached.
     * @param component  - a component from the new updated EAD finding aid.
     * @return the object Id of the updated component EADWork in the collection after the merge. 
     */
    public static EADWork mergeComponent(EADWork collection, JsonNode component) {
        EADWork componentWork;
        if (collection.getChildren() == null) {
            componentWork = collection.addEADWork();
        } else {
            if  (collection.isNewComponent(component))
                componentWork = collection.addEADWork();
            else 
                componentWork = updateComponentPath(collection, component);
        }
        
        // TODO: the updateComponentData is currently done in CollectionBuilder,
        //       check whether it's better here.
        updateComponentData(componentWork, component);
        return componentWork;
    }
    
    protected static EADWork updateComponentPath(EADWork collection, JsonNode component) {
        // TODO: check and update path to the component within the collection graph if changed.
        return null;
    }
    
    protected static EADWork updateComponentData(EADWork componentWork, JsonNode component) {
        // TODO: Step 1: clearing any work properties populated from the EAD fields in the component json node.
        
        // TODO: Step 2: set corresp. work properties from the EAD fields in the component json node.
        return componentWork;
    }
    
    /**
     * purgeComponents deletes the EADworks that no longer corresponds to any component
     * in the updated eadStream, and records the details of the deleted EADworks as an
     * array in json.
     * 
     * @param collection - the top level work of a collection with the new updated EAD finding aid attached as
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing
     *                     updates from the new updated FINDING_AID_COPY.
     * @param parser     - the XML document parser configured to parse the updated EAD.
     * @return the json array containing details of the deleted EADworks.
     * @throws EADReloadException when a component EADwork (in collection) to be purged has digital objects attach to it.
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static ArrayNode purgeComponents(EADWork collection, XmlDocumentParser parser) throws EADReloadException, JsonParseException, JsonMappingException, IOException{
        ArrayNode purgeList = purgePreChecks(collection, parser);
        
        // TODO: delete works in the purgeList from collection 
        
        return purgeList;
    }
    
    /**
     * purgePreChecks checks each EADwork to be purged as whether there're any digitial objects attached to it, if so, EADReloadException is throw.
     * If no digital object is attached to any EADwork to be purged, a json array of the EADworks to be purged is returned.
     *   
     * @param collection - the top level work of a collection with the new updated EAD finding aid attached as
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                     updates from the new updated FINDING_AID_COPY.
     * @param parser     - the XML document parser configured to parse the updated EAD.
     * @return the json array containing details of the EADworks to be deleted.
     * @throws EADReloadException when a component EADwork (in collection) to be purged has digital objects attach to it.
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    protected static ArrayNode purgePreChecks(EADWork collection, XmlDocumentParser parser) throws EADReloadException, JsonParseException, JsonMappingException, IOException {
        Map<String, String> currentDOs = digitalObjectsMap(collection);
        // TODO:
        return null;
    }
    
    protected static Map<String, String> digitalObjectsMap(Work collectionWork) throws JsonParseException, JsonMappingException, IOException {
        // Get a list of EAD component works in the current collection work structure which has digital objects attached
        JsonNode content = getFindingAIDJsonDocument(collectionWork).getContent();
        Map<String, String> uuidToPIMap = new HashMap<>();
        
        if (content != null && content.getFieldNames() != null) {
            while (content.getFieldNames().hasNext()) {
                String objId = content.getFieldNames().next();
                String uuid = content.get(objId).get("localSystemNumber").getTextValue();
                uuidToPIMap.put(uuid, objId);
            }
        }
        return uuidToPIMap;
    }
    
    protected static Document getFindingAIDJsonDocument(Work collectionWork) throws JsonParseException, JsonMappingException, IOException {
        Copy eadJsonCopy = collectionWork.getCopy(CopyRole.FINDING_AID_VIEW_COPY);
        if (eadJsonCopy == null || eadJsonCopy.getFile() == null) {
            String errMsg = "Failed to process work collection as the input collection work " + collectionWork.getObjId() + " does not have a finding aid json copy.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        File eadJsonFile = eadJsonCopy.getFile();
        Document doc = mapper.readValue(eadJsonFile.openStream(), Document.class);
        return doc;
    }
}
