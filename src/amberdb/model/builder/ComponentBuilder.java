package amberdb.model.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nu.xom.Element;
import nu.xom.Node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.PIUtil;
import amberdb.model.EADWork;
import amberdb.model.Work;

public class ComponentBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    static final ObjectMapper mapper = new ObjectMapper();       
    
    /**
     * mergeComponents merges multiple components directly under the parentWork.
     * @param collectionWork
     * @param parentWork
     * @param components
     * @return
     */
    public static List<EADWork> mergeComponents(EADWork collectionWork, EADWork parentWork, JsonNode... components) {
        List<EADWork> componentWorks = new ArrayList<>();
        for (JsonNode component : components) {
            componentWorks.add(mergeComponent(collectionWork, parentWork, component));
        }

        return componentWorks;
    }
    
    /**
     * mergeComponent checks the component for the type of merge required in order to carry out the merge:
     *  - NEW_COMP, a new component to be added to the collection.
     *  - UPDATED_COMP_DATA, an existing component in the collection with its metadata required to be updated.
     *  - UPDATED_COMP_PATH, an existing component in the collection required to be re-attached to a different
     *                       parent EADwork.
     * 
     * @param parentWork - the top level work of a collection with the new updated EAD finding aid attached as 
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet updated
     *                     from the new updated FINING_AID_COPY.
     *                    
     * an existing FINDING_AID_COPY (not the new
     *                     updated EAD finding aid yet) attached.
     * @param component  - a component from the new updated EAD finding aid.
     * @return the object Id of the updated component EADWork in the collection after the merge. 
     */
    public static EADWork mergeComponent(EADWork collectionWork, EADWork parentWork, JsonNode component) {
        EADWork componentWork;
        
        if (component.get("nlaObjId") == null) {
            // new component
            componentWork = parentWork.addEADWork();
        } else {
            log.debug("The component nla object id is " + component.get("nlaObjId").getTextValue());
            try {
                componentWork = collectionWork.getEADWork(PIUtil.parse(component.get("nlaObjId").getTextValue()));
                if (!parentWork.getObjId().equals(componentWork.getParent().getObjId())) {
                    // Update component path
                    Work fromParent = componentWork.getParent();
                    fromParent.removePart(componentWork);
                    componentWork.setParent(parentWork);
                }
            } catch (Exception e) {
                componentWork = parentWork.addEADWork();
            }
        }
        // Update component data
        updateComponentData(componentWork, component);
        return componentWork;
    }
    
    protected static EADWork updateComponentData(EADWork componentWork, JsonNode component) {
        // TODO: map subUnitType later on.
        String subUnitType = "Series";
        Map<String, Object> fieldsMap = new ConcurrentHashMap<>();
        // TODO: set the component fields scope-n-content and date-range during the reload
        // if (component != null) {
        //    fieldsMap.put("scope-n-content", component.get("scope-n-content"));
        //    fieldsMap.put("date-range", component.get("date-range"));
        // }
        
        mapWorkMD(componentWork, component.get("uuid").getTextValue(), subUnitType, fieldsMap);
        return componentWork;
    }
    
    protected static void mapWorkMD(EADWork componentWork, String uuid, String subUnitType, Map<String, Object> fieldsMap) {
        componentWork.setSubType("Work");      
        componentWork.setSubUnitType(subUnitType);
        componentWork.setForm("Manuscript");
        componentWork.setBibLevel("Item");
        componentWork.setCollection("nla.ms");
        componentWork.setRecordSource("FA");        
        componentWork.setLocalSystemNumber(uuid);
        componentWork.setRdsAcknowledgementType("Sponsor");
        componentWork.setRdsAcknowledgementReceiver("NLA");
        componentWork.setEADUpdateReviewRequired("Y"); 
        componentWork.setAccessConditions("Unrestricted");
        
        Object unitTitle = fieldsMap.get("title");
        if (unitTitle != null && !unitTitle.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": unit title: " + unitTitle.toString());
            componentWork.setTitle(unitTitle.toString());
        }
        Object scopeContent = fieldsMap.get("scope-n-content");
        if (scopeContent != null && !scopeContent.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": scope and content: " + scopeContent.toString());
            componentWork.setScopeContent(scopeContent.toString());
        }
        Object dateRange = fieldsMap.get("date-range");
        if (dateRange != null && !dateRange.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": date range: " + dateRange.toString());
            componentWork.setDateRange(dateRange.toString());
        }
        
        mapContainer(componentWork, fieldsMap);
    }

    protected static void mapContainer(EADWork componentWork, Map<String, Object> fieldsMap) {
        Object containerNumber = fieldsMap.get("container-number");
        Object containerLabel = fieldsMap.get("container-label");        
        Object containerType = fieldsMap.get("container-type");
        if (containerLabel != null) {
            // if a single container is extracted for the component work
            if (containerLabel instanceof String) {
                if (!((String) containerLabel).isEmpty()) {
                    String folder = "";
                    if (containerType != null)
                        folder = containerType.toString() + " " + containerNumber.toString() + ": " + containerLabel.toString();
                    else
                        folder = "container " + containerNumber.toString() + ": " + containerLabel.toString();
                    log.debug("component work " + componentWork.getObjId() + ": container: " + folder);
                    List<String> folders = new ArrayList<>();
                    folders.add(folder);
                    try {
                        componentWork.setFolder(folders);
                    } catch (IOException e) {
                        log.error("Failed to extract container for component work: " + componentWork.getObjId());
                    }
                }
                return;
            }
            
            // if multiple containers are extracted for the component work
            String[] containerLabels = (String[]) containerLabel;
            String[] containerTypes = (String[]) containerType;
            if (containerLabels.length > 0) {
                List<String> folders = new ArrayList<>();
                for (int i = 0; i < containerLabels.length; i++) {
                    if (containerLabels[i] != null && !((String) containerLabels[i]).isEmpty()) {
                        String folder = "";
                        if (containerTypes != null && containerTypes[i] != null) {
                            folder = containerTypes[i] + ": " + containerLabels[i];
                        } else {
                            folder = containerLabels[i];
                        }
                        log.debug("component work " + componentWork.getObjId() + ": container: " + folder);
                        folders.add(folder);
                    }
                }
                try {
                    if (!folders.isEmpty())
                        componentWork.setFolder(folders);
                } catch (IOException e) {
                    log.error("Failed to extract container for component work: " + componentWork.getObjId());
                }
            }
        }
    }
    
    protected static JsonNode makeComponent(Node eadElement, JsonNode elementCfg, XmlDocumentParser parser) {
        ObjectNode node = parser.mapper.createObjectNode();
        Map<String, Object> fieldsMap = parser.getFieldsMap(eadElement, elementCfg, parser.getBasePath(parser.getDocument()));        
        if (fieldsMap.get("uuid") == null || fieldsMap.get("uuid").toString().isEmpty())
            throw new EADValidationException("Failed to parse uuid for EAD element " + ((Element) eadElement).getLocalName() + " - " + eadElement.getValue());
        
        // TODO: map subUnitType later on.
        String subUnitType = "Series";
        String uuid = fieldsMap.get("uuid").toString();
        node.put("uuid", uuid);
        node.put("subUnitType", subUnitType);
        return node;
    }
}
