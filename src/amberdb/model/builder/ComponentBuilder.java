package amberdb.model.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.PIUtil;
import amberdb.enums.AccessCondition;
import amberdb.enums.BibLevel;
import amberdb.enums.CopyrightPolicy;
import amberdb.enums.DigitalStatus;
import amberdb.enums.SubUnitType;
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
        Map<String, String> fieldsMap = mapper.readValue(compJson, new TypeReference<HashMap<String, String>>(){});
        mapWorkMD(componentWork, component.get("uuid").getTextValue(), fieldsMap);
        return componentWork;
    }
    
    protected static void mapWorkMD(EADWork componentWork, String uuid, Map<String, String> fieldsMap) throws JsonParseException, JsonMappingException, IOException {
        componentWork.setSubType("Work");      
        componentWork.setForm("Manuscript");
      
        componentWork.setBibLevel(BibLevel.SET.code());
        
        String collection = componentWork.getParent().getCollection();
        componentWork.setCollection(collection);
        componentWork.setRecordSource("FA");        
        componentWork.setLocalSystemNumber(uuid);
        componentWork.setRdsAcknowledgementType("Sponsor");
        if (fieldsMap.get("sponsor") != null)
            componentWork.asEADWork().setRdsAcknowledgementReceiver(fieldsMap.get("sponsor"));
        else    
            componentWork.asEADWork().setRdsAcknowledgementReceiver("NLA");
        
        // eadUpdateReviewRequired appears on the child levels only
        if (componentWork.getEADUpdateReviewRequired() == null)
            componentWork.setEADUpdateReviewRequired("N"); 
        
        String accessConditions = componentWork.getParent().getAccessConditions();
        if (accessConditions != null && !accessConditions.isEmpty())
            componentWork.setAccessConditions(accessConditions);
        else
            componentWork.setAccessConditions(AccessCondition.RESTRICTED.code());
        componentWork.setDigitalStatus(DigitalStatus.NOT_CAPTURED.code());
        
        List<String> constraints = componentWork.getParent().getConstraint();
        componentWork.setConstraint(constraints);
        
        Date expiryDate = componentWork.getParent().getExpiryDate();
        componentWork.setExpiryDate(expiryDate);
        
        String internalAccessConditions = AccessCondition.OPEN.code();
        String copyrightPolicy = CopyrightPolicy.OUTOFCOPYRIGHT.code();
        if (componentWork.getCollection() != null && componentWork.getCollection().equalsIgnoreCase("nla.ms")) {
            internalAccessConditions = AccessCondition.RESTRICTED.code();
            copyrightPolicy = CopyrightPolicy.PERPETUAL.code();
        }
        componentWork.setInternalAccessConditions(internalAccessConditions);
        componentWork.setCopyrightPolicy(copyrightPolicy);
        componentWork.setSensitiveMaterial("No");
              
        String componentLevel = fieldsMap.get("component-level");
        if (componentLevel != null && !componentLevel.isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": componentLevel: " + componentLevel.toString());
            
            SubUnitType subUnitType = SubUnitType.fromString(componentLevel.toString());
            if (subUnitType == null)
                throw new IllegalArgumentException("Invalid subunit type " + componentLevel.toString() + " for work " + componentWork.getObjId() + ".");
            componentWork.setSubUnitType(subUnitType.code());
            // determine bib level with business rule borrowed from DCM
            String bibLevel = mapBibLevel(componentLevel);
            componentWork.setBibLevel(bibLevel);
        }
        
        String componentNumber = fieldsMap.get("component-number");
        if (componentNumber != null && !componentNumber.isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": componentNumber: " + componentNumber.toString());
            componentWork.setSubUnitNo(componentNumber.toString());
            componentWork.setOrder(Integer.parseInt(componentNumber.toString()));
        }
        
        Object unitTitle = fieldsMap.get("title");
        if (unitTitle != null && !unitTitle.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": unit title: " + unitTitle.toString());
            componentWork.setTitle(unitTitle.toString());
        }
        
        Object creator = fieldsMap.get("creator");
        if (creator != null && !creator.toString().isEmpty()) {
            log.debug("component work " + componentWork.getObjId() + ": creator: " + creator.toString());
            componentWork.setCreator(creator.toString());
        }
        
        Object extent = fieldsMap.get("extent");
        if (extent != null && extent instanceof String) {
            if (!extent.toString().isEmpty())
                componentWork.setExtent(extent.toString());
        } else if (extent != null) {
            List<String> extentList = (List<String>) extent;
            componentWork.setExtent(StringUtils.join(extentList, "; "));
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
                if (dateList != null && dateList.size() > 0) {
                    componentWork.setStartDate(dateList.get(0));
                    if (dateList.size() > 1)
                        componentWork.setEndDate(dateList.get(1));
                }
            } catch (Exception e) {
                log.info("Failed to parse date range for component work " + componentWork.getObjId());
            }
        }
        
        Object dcmWorkPID = fieldsMap.get("dcmpi");
        if (dcmWorkPID != null)
            componentWork.setDcmWorkPid(dcmWorkPID.toString());

        mapContainer(componentWork, fieldsMap);
    }

    private static String mapBibLevel(Object componentLevel) {
        String bibLevel = BibLevel.SET.code();
        if (componentLevel != null) {
            if (componentLevel.toString().equalsIgnoreCase("item")) 
                bibLevel = BibLevel.ITEM.code();
            else if (componentLevel.toString().equalsIgnoreCase("otherlevel"))
                bibLevel = BibLevel.PART.code();
        }
        return bibLevel;
    }

    protected static void mapContainer(EADWork componentWork, Map<String, String> fieldsMap) {
        String containerId = fieldsMap.get("container-uuid");
        String containerParent = fieldsMap.get("container-parent");
        String containerNumber = fieldsMap.get("container-number");
        String containerLabel = fieldsMap.get("container-label");        
        String containerType = fieldsMap.get("container-type");

        if (containerId != null) {
            if (containerId instanceof String && !((String) containerId).isEmpty()) {
                List<String> folders = new ArrayList<>();
                List<String> folderTypes = new ArrayList<>();
                List<String> folderNumbers = new ArrayList<>();
                if (((String) containerId).length() == 39) {
                    String folderType = (containerType == null || containerType.toString().isEmpty()) ? "" : containerType.toString();
                    String folderNumber = (containerNumber == null) ? "" : containerNumber.toString();
                    String folder = "container " + folderType + " " + folderNumber + "(id:" + containerId.toString() + ")";
                    folder += (containerLabel == null || containerLabel.toString().isEmpty()) ? "" : ":" + containerLabel.toString();
                    folder += (containerParent == null || containerParent.toString().isEmpty()) ? "" : "(parent: " + containerParent.toString() + ")";
                    folders.add(folder);
                    folderTypes.add(folderType);
                    folderNumbers.add(folderNumber);
                } else {
                    try {
                        String[] containerIds = mapper.readValue(containerId, new TypeReference<String[]>() {});
                        String[] containerParents = null;
                        if (containerParent != null) containerParents = mapper.readValue(containerParent, new TypeReference<String[]>() {});
                        String[] containerNumbers = null;
                        if (containerNumber != null) {
                            containerNumbers = mapper.readValue(containerNumber, new TypeReference<String[]>() {});
                            folderNumbers = Arrays.asList(containerNumbers);
                        }
                        String[] containerLabels = null;
                        if (containerLabel != null) containerLabels = mapper.readValue(containerLabel, new TypeReference<String[]>() {});
                        String[] containerTypes = null;
                        if (containerType != null) {
                            containerTypes = mapper.readValue(containerType, new TypeReference<String[]>() {});
                            folderTypes = Arrays.asList(containerTypes);
                        }
                        for (int i = 0; i < containerIds.length; i++) {
                            String folder = "container " + ((containerTypes[i] == null || containerTypes[i].toString().isEmpty()) ? "" : containerTypes[i].toString()) + " "
                                    + ((containerNumbers[i] == null) ? "" : containerNumbers[i].toString()) + "(id:"
                                    + containerIds[i].toString() + ")";
                            folder += (containerLabels[i] == null || containerLabels[i].toString().isEmpty()) ? "" : ":" + containerLabels[i].toString();
                            folder += (containerParents[i] == null || containerParents[i].toString().isEmpty()) ? "" : "(parent: " + containerParents[i].toString() + ")";
                            folders.add(folder);
                        }
                    } catch (IOException e) {
                        log.error("unable to map containers for work " + componentWork.getObjId());
                    }
                }
                try {
                    componentWork.setFolderType(folderTypes);
                    componentWork.setFolderNumber(folderNumbers);
                    componentWork.setFolder(folders);
                } catch (IOException e) {
                    log.error("Failed to extract container for component work: " + componentWork.getObjId());
                }
            }
        }
    }
    
    protected static JsonNode makeComponent(Node eadElement, JsonNode elementCfg, XmlDocumentParser parser) {
        ObjectNode node = mapper.createObjectNode();
        Map<String, String> fieldsMap = parser.getFieldsMap(eadElement, elementCfg, parser.getBasePath(parser.getDocument()));        
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
        Object dcmWorkPID = fieldsMap.get("dcmpi");
        if (dcmWorkPID != null)
            node.put("dcmWorkPid", dcmWorkPID.toString());
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
