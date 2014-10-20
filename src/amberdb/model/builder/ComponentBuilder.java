package amberdb.model.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.codehaus.jackson.JsonNode;
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
            componentWork = collectionWork.getComponentWork(PIUtil.parse(component.get("nlaObjId").getTextValue()));
            if (!parentWork.getObjId().equals(componentWork.getParent().getObjId())) {
                // Update component path
                Work fromParent = componentWork.getParent();
                fromParent.removePart(componentWork);
                componentWork.setParent(parentWork);
            }
        }
        // Update component data
        updateComponentData(componentWork, component);
        return componentWork;
    }
    
    protected static EADWork updateComponentData(EADWork componentWork, JsonNode component) {
        // TODO: map subUnitType later on.
        String subUnitType = "Series";
        mapWorkMD(componentWork, component.get("uuid").getTextValue(), subUnitType);
        return componentWork;
    }
    
    protected static void mapWorkMD(EADWork componentWork, String uuid, String subUnitType) {
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
