package amberdb.model.builder;

import static amberdb.model.builder.XmlDocumentParser.CFG_COLLECTION_ELEMENT;
import static amberdb.model.builder.XmlDocumentParser.CFG_REPEATABLE_ELEMENTS;
import static amberdb.model.builder.XmlDocumentParser.CFG_SUB_ELEMENTS;
import static amberdb.model.builder.XmlDocumentParser.CFG_BASE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Nodes;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.PIUtil;
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
                if (collection.isNewComponent(component.get("uuid").getTextValue())) 
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
            if  (collection.isNewComponent(component.get("uuid").getTextValue()))
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
        String parentUUID = component.get("parentUUID").getTextValue();
        if (collection.getLocalSystemNumber() != null && parentUUID != null 
                && !collection.getLocalSystemNumber().equals(parentUUID)) {
            
        }
        
        return null;
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
}
