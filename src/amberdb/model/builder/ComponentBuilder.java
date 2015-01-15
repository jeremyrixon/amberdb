package amberdb.model.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.PIUtil;
import amberdb.model.EADWork;
import amberdb.model.Work;
import amberdb.util.DateParser;

public class ComponentBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    static final ObjectMapper mapper = new ObjectMapper();       
    
    /**
     * mergeComponents merges multiple components directly under the parentWork.
     * @param collectionWork
     * @param parentWork
     * @param components
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static List<EADWork> mergeComponents(EADWork collectionWork, EADWork parentWork, JsonNode... components) throws JsonParseException, JsonMappingException, IOException {
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
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static EADWork mergeComponent(EADWork collectionWork, EADWork parentWork, JsonNode component) throws JsonParseException, JsonMappingException, IOException {
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
    
    protected static EADWork updateComponentData(EADWork componentWork, JsonNode component) throws JsonParseException, JsonMappingException, IOException {
        String compJson = mapper.writeValueAsString(component);
        Map<String, Object> fieldsMap = mapper.readValue(compJson, new TypeReference<HashMap<String, Object>>(){});
        mapWorkMD(componentWork, component.get("uuid").getTextValue(), fieldsMap);
        return componentWork;
    }
    
    protected static void mapWorkMD(EADWork componentWork, String uuid, Map<String, Object> fieldsMap) throws JsonParseException, JsonMappingException, IOException {
        componentWork.setSubType("Work");      
        componentWork.setForm("Manuscript");
      
        componentWork.setBibLevel("Item");
        componentWork.setCollection("nla.ms");
        componentWork.setRecordSource("FA");        
        componentWork.setLocalSystemNumber(uuid);
        componentWork.setRdsAcknowledgementType("Sponsor");
        componentWork.setRdsAcknowledgementReceiver("NLA");
        componentWork.setEADUpdateReviewRequired("Y"); 
        componentWork.setAccessConditions("Unrestricted");
        
        Object componentLevel = fieldsMap.get("component-level");
        if (componentLevel != null && !componentLevel.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": componentLevel: " + componentLevel.toString());
            componentWork.setComponentLevel(componentLevel.toString());
            componentWork.setSubUnitType(componentLevel.toString());
            // determine bib level with business rule borrowed from DCM
            String bibLevel = mapBibLevel(componentLevel);
            componentWork.setBibLevel(bibLevel);
        }
        
        Object componentNumber = fieldsMap.get("component-number");
        if (componentNumber != null && !componentNumber.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": componentNumber: " + componentNumber.toString());
            componentWork.setComponentNumber(componentNumber.toString());
            componentWork.setSubUnitNo(componentNumber.toString());
        }
        
        Object unitTitle = fieldsMap.get("title");
        if (unitTitle != null && !unitTitle.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": unit title: " + unitTitle.toString());
            componentWork.setTitle(unitTitle.toString());
            componentWork.setUniformTitle(unitTitle.toString());
        }
        
        Object scopeContent = fieldsMap.get("scope-n-content");
        if (scopeContent != null && !scopeContent.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": scope and content: " + scopeContent.toString());
            componentWork.setScopeContent(scopeContent.toString());
        }
        Object dateRange = fieldsMap.get("date-range");
        if (dateRange != null && !dateRange.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": date range: " + dateRange.toString());
            componentWork.setDateRangeInAS(dateRange.toString());
            List<Date> dateList;
            try {
                dateList = DateParser.parseDateRange(dateRange.toString());
                componentWork.setDateRange(dateList);
            } catch (Exception e) {
                log.info("Failed to parse date range for component work " + componentWork.getObjId());
            }
        }
        
        mapContainer(componentWork, fieldsMap);
    }

    private static String mapBibLevel(Object componentLevel) {
        String bibLevel = "Set";
        if (componentLevel != null) {
            if (componentLevel.toString().equalsIgnoreCase("item")) 
                bibLevel = "Item";
            else if (componentLevel.toString().equalsIgnoreCase("part"))
                bibLevel = "Part";
        }
        return bibLevel;
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
        ObjectNode node = mapper.createObjectNode();
        Map<String, Object> fieldsMap = parser.getFieldsMap(eadElement, elementCfg, parser.getBasePath(parser.getDocument()));        
        if (fieldsMap.get("uuid") == null || fieldsMap.get("uuid").toString().isEmpty())
            throw new EADValidationException("Failed to parse uuid for EAD element " + ((Element) eadElement).getLocalName() + " - " + eadElement.getValue());
        
        String uuid = fieldsMap.get("uuid").toString();
        node.put("uuid", uuid);
        Object componentLevel = fieldsMap.get("component-level");
        if (componentLevel != null) {
            node.put("subUnitType", componentLevel.toString());
            node.put("component-level", componentLevel.toString());
        }
        Object componentNumber = fieldsMap.get("component-number");
        if (componentNumber != null)
            node.put("component-number", componentNumber.toString());
        Object unitTitle = fieldsMap.get("title");
        if (unitTitle != null)
            node.put("title", unitTitle.toString());
        Object scopeContent = fieldsMap.get("scope-n-content");
        if (scopeContent != null)
            node.put("scope-n-content", scopeContent.toString());
        Object dateRange = fieldsMap.get("date-range");
        if (dateRange != null)
            node.put("date-range", dateRange.toString());
        Object containerType = fieldsMap.get("container-type");
        if (containerType != null)
            node.put("container-type", containerType.toString());
        Object containerLabel = fieldsMap.get("container-label");
        if (containerLabel != null)
            node.put("container-label", containerLabel.toString());
        Object containerNumber = fieldsMap.get("container-number");
        if (containerNumber != null)
            node.put("container-number", containerNumber.toString());
        return node;
    }
}
