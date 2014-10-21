package amberdb.model.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import doss.core.Writables;

import amberdb.PIUtil;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.EADWork;
import amberdb.model.File;
import amberdb.model.Work;

import static amberdb.model.builder.XmlDocumentParser.CFG_COLLECTION_ELEMENT;
import static amberdb.model.builder.XmlDocumentParser.CFG_SUB_ELEMENTS;
import static amberdb.model.builder.XmlDocumentParser.CFG_BASE;
import static amberdb.model.builder.XmlDocumentParser.CFG_REPEATABLE_ELEMENTS;

public class CollectionBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * createCollection in absence of collection configuration and document parser input parameters, 
     * resolves to default collection JSON configuration and the default EAD parser in order to 
     * create the collection work structure under the top level collection work.
     * 
     * @param collectionWork: the top-level work of a collection with a FINDING_AID_COPY attached.
     * @throws EADValidationException
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void createCollection(Work collectionWork) throws EADValidationException, ValidityException, ParsingException, IOException {
        createCollection(collectionWork, getDefaultCollectionCfg(), getDefaultXmlDocumentParser());
    }
    
    /**
     * getDefaultCollectionCfg returns the default configuration for creating a hierarchy of works under
     * the top level collection work.  The default configuration currently sets:
     *  - validateXML to be false, which means that EAD xml file will not be validated before parsing; maybe
     *    later on, validation of EAD as valid xml can be introduced.  
     *  - storeCopy to be true, which means the derived EAD json and filtered EAD xml which be stored as EAD copies
     *    in amberdb.
     */
    public static JsonNode getDefaultCollectionCfg() {
        return new EADConfiguration().getConfig();
    }
    
    /**
     * getDefaultXmlDocumentParser returns the EADParser as the default xml document parser
     * for the CollectionBuilder.
     */
    public static XmlDocumentParser getDefaultXmlDocumentParser() {
        return new EADParser();
    }
    
    /**
     * createCollection parses the ead file attached to the top-level
     * collection work with the input field mapping from collectionCfg, and 
     * create the collection work structure under the top level collection work. 
     * 
     * Pre-condition: the input collectionWork must already exist, and must already have a EAD file attached as
     *                FINDING_AID_COPY.
     *                
     * @param collectionWork - the top level work of a collection with a FINDING_AID_COPY attached. 
     * @param collectionCfg  - configuration for parsing attached EAD file in order to create
     *                         the collection.
     * @param parser         - the XML document parser for parsing the EAD. 
     * @return
     * @throws EADValidationException
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void createCollection(Work collectionWork, JsonNode collectionCfg, XmlDocumentParser parser) throws EADValidationException, ValidityException, ParsingException, IOException{
        if (collectionWork == null) {
            String errMsg = "Failed to create work collection as the input collection work is null.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        File eadFile = getFindingAIDFile(collectionWork);
        
        if (collectionCfg == null) {
            String warnMsg = "No configuration found for parsing the collection data, switched to use the default parsing configuration.";
            log.info(warnMsg);
            collectionCfg = getDefaultCollectionCfg();
        }
        
        if (parser == null) {
            String warnMsg = "No parser found for parsing the collection data, switched to use the default parser";
            log.info(warnMsg);
            parser = getDefaultXmlDocumentParser();
        }
        
        String collectionName = eadFile.getFileName();
        // initializing the parser
        parser.init(collectionWork.getObjId(), eadFile.openStream(), collectionCfg);
        processCollection(collectionWork, collectionName, eadFile.openStream(), collectionCfg, parser);
    }
    
    /**
     * reloadEADPreChecks checks each EADwork to be purged as whether there're any digitial objects attached to it, if so, EADValidationException is thrown.
     * If no digital object is attached to any EADwork to be purged, a list of nla object ids for the EADworks to be purged is returned.
     *   
     * @param collection - the top level work of a collection with the new updated EAD finding aid attached as
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                     updates from the new updated FINDING_AID_COPY.
     * @return list of nla object ids for the EADworks to be deleted.
     * @throws EADValidationException when a component EADwork (in collection) to be purged has digital objects attach to it.
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static List<String> reloadEADPreChecks(Work collectionWork) throws EADValidationException, JsonParseException, JsonMappingException, IOException, ValidityException, ParsingException {
        return reloadEADPreChecks(collectionWork.asEADWork(), null);
    }
    
    /**
     * reloadEADPreChecks checks each EADwork to be purged as whether there're any digitial objects attached to it, if so, EADValidationException is thrown.
     * If no digital object is attached to any EADwork to be purged, a list of nla object ids for the EADworks to be purged is returned.
     *   
     * @param collection - the top level work of a collection with the new updated EAD finding aid attached as
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                     updates from the new updated FINDING_AID_COPY.
     * @param parser     - the XML document parser configured to parse the updated EAD.
     * @return list of nla object ids for the EADworks to be deleted.
     * @throws EADValidationException when a component EADwork (in collection) to be purged has digital objects attach to it.
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static List<String> reloadEADPreChecks(EADWork collection, XmlDocumentParser parser) throws EADValidationException, JsonParseException, JsonMappingException, IOException, ValidityException, ParsingException {
        if (collection == null) {
            String errMsg = "Failed to perform EAD reload prechecks as the input collection work is null.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        if (parser == null) {
            String warnMsg = "No parser found for parsing the collection data, switch to use the default parser";
            log.info(warnMsg);
            parser = getDefaultXmlDocumentParser();
        }
        
        parser.init(collection.getObjId(), getFindingAIDFile(collection).openStream(), getDefaultCollectionCfg());
        Map<String, String> currentComponents = componentWorksMap(collection); 
        List<String> currentDOs = digitisedItemList(collection);
        List<String> eadUUIDList = parser.listUUIDs();
        List<String> componentsNotInEAD = new ArrayList<String>();
        
        for (String asId : currentComponents.keySet()) {
            if (asId != null && !asId.isEmpty() && !eadUUIDList.contains(asId)) {
                if (currentDOs.contains(asId)) {
                    throw new EADValidationException("Cannot update collection " + parser.collectionObjId + " from EAD as component of Archive Space id " + asId + " contains digitised copies, but is not found in the EAD.");
                } else {
                    componentsNotInEAD.add(currentComponents.get(asId));
                }
            }
        }
        return componentsNotInEAD;
    }
    
    /**
     * componentWorksMap provides a map of uuid to nla object id of each EAD works within collectionWork (including the collectionWork).
     * 
     * @param collectionWork - the top level work of a collection with the new updated EAD finding aid attached as
     *                         the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                         updates from the new updated FINDING_AID_COPY.
     * @return a map of (uuid, nla object id) of each EAD works within collectionWork (including the collectionWork).
     *         
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    protected static Map<String, String> componentWorksMap(Work collectionWork) throws JsonParseException,
            JsonMappingException, IOException {
        JsonNode content = getFindingAIDJsonDocument(collectionWork).getContent();
        Map<String, String> uuidToPIMap = new HashMap<>();

        if (content != null && content.getFieldNames() != null) {
            Iterator<String> fieldNames = content.getFieldNames();
            while (fieldNames.hasNext()) {
                String objId = fieldNames.next();
                if (content.get(objId).get("localSystemNumber") != null) {
                    String uuid = content.get(objId).get("localSystemNumber").getTextValue();
                    uuidToPIMap.put(uuid, objId);
                } 
            }
        }
        return uuidToPIMap;
    }
    
    /**
     * digitisedItemList provides a list of uuid of each EAD works within collectionWork (including the collectionWork)
     * which has any digital object attach to it.
     * 
     * @param collectionWork - the top level work of a collection with the new updated EAD finding aid attached as
     *                         the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                         updates from the new updated FINDING_AID_COPY.
     * @return a list of uuid of each EAD works within collectionWork (including the collectionWork) which has any digital
     *         object attach to it.
     *         
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    protected static List<String> digitisedItemList(Work collectionWork) throws JsonParseException,
            JsonMappingException, IOException {
        // Get a list of EAD component works in the current collection work
        // structure which has digital objects attached
        JsonNode content = getFindingAIDJsonDocument(collectionWork).getContent();
        List<String> uuidList = new ArrayList<>();

        if (content != null && content.getFieldNames() != null) {
            Iterator<String> fieldNames = content.getFieldNames();
            while (fieldNames.hasNext()) {
                String objId = fieldNames.next();
                // find the component work
                if (!objId.equals(collectionWork.getObjId())) {
                    EADWork component = collectionWork.asEADWork().getEADWork(PIUtil.parse(objId));

                    // add entry to digitalObjectsMap if the component has any
                    // copies attached
                    if (component != null
                            && (component.getCopies() != null && component.getCopies().iterator().hasNext())) {
                        if (content.get(objId).get("localSystemNumber") != null) {
                            String uuid = content.get(objId).get("localSystemNumber").getTextValue();
                            uuidList.add(uuid);
                        } 
                    }
                }
            }
        }
        return uuidList;
    }
    
    protected static Document getFindingAIDJsonDocument(Work collectionWork) throws JsonParseException, JsonMappingException, IOException {
        Copy eadJsonCopy = collectionWork.getCopy(CopyRole.FINDING_AID_VIEW_COPY);
        if (eadJsonCopy == null || eadJsonCopy.getFile() == null) {
            String errMsg = "Failed to process work collection as the input collection work " + collectionWork.getObjId() + " does not have a finding aid json copy.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        File eadJsonFile = eadJsonCopy.getFile();
        JsonNode eadJson = mapper.readTree(eadJsonFile.openStream());
        Document doc = new Document(eadJson.get("structure"), eadJson.get("content"));
        return doc;
    }
    
    /**
     * reloadCollection parses the updated EAD file attached to the top-level
     * collection work with the input field mapping from collectionCfg, and
     * update the collection work structure under the top level collection by
     * adding EAD work for each new EAD component; delete EAD work for an EAD
     * component that was previously there but not included in the updated EAD
     * file; update EAD work for an EAD component with modified metadata.
     * 
     * pre-requisite: reloadEADPreChecks() has been called, and all components intended for purging
     *                has been purged.
     *                
     * reloadCollection also call generateJson(...) to regenerate the derivative Json to reflect the updated
     * the mapping of the structure and the content for top level collection metadata and its components and 
     * sub-components.
     * 
     * if any of the EAD work in the current collection work structure has copies and files
     * attached to it, the copies and files will be re-attached to the corresponding
     * node in the recreated collection work structure.
     * 
     * @param collectionWork - the top level work of a collection with the new updated EAD attached as the FINDING_AID_COPY, 
     *                         and the FINDING_AID_VIEW_COPY containing json not yet containing updates from the new updated
     *                         FINDING_AID_COPY.
     * @throws EADValidationException
     * @throws IOException
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void reloadCollection(Work collectionWork) throws EADValidationException, IOException, ValidityException, ParsingException {
        reloadCollection(collectionWork, null, null);
    }
    
    /**
     * reloadCollection parses the updated EAD file attached to the top-level
     * collection work with the input field mapping from collectionCfg, and
     * update the collection work structure under the top level collection by
     * adding EAD work for each new EAD component; delete EAD work for an EAD
     * component that was previously there but not included in the updated EAD
     * file; update EAD work for an EAD component with modified metadata.
     * 
     * pre-requisite: reloadEADPreChecks() has been called, and all components intended for purging
     *                has been purged.
     * 
     * reloadCollection also call generateJson(...) to regenerate the derivative Json to reflect the updated
     * the mapping of the structure and the content for top level collection metadata and its components and 
     * sub-components.
     * 
     * if any of the EAD work in the current collection work structure has copies and files
     * attached to it, the copies and files will be re-attached to the corresponding
     * node in the recreated collection work structure.
     * 
     * @param collectionWork - the top level work of a collection with the new updated EAD attached as the FINDING_AID_COPY, 
     *                         and the FINDING_AID_VIEW_COPY containing json not yet containing updates from the new updated
     *                         FINDING_AID_COPY.
     * @param collectionCfg  - configuration for parsing attached EAD file in order to create the collection.
     * @param parser         - the XML document parser for parsing the EAD.
     * @throws EADValidationException
     * @throws IOException
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void reloadCollection(Work collectionWork, JsonNode collectionCfg, XmlDocumentParser parser) throws EADValidationException, IOException, ValidityException, ParsingException {
        if (collectionWork == null) {
            String errMsg = "Failed to merge work collection as the input collection work is null.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        File eadFile = getFindingAIDFile(collectionWork);
        
        if (collectionCfg == null) {
            String warnMsg = "No configuration found for parsing the collection data, switched to use the default parsing configuration.";
            log.info(warnMsg);
            collectionCfg = getDefaultCollectionCfg();
        }
        
        if (parser == null) {
            String warnMsg = "No parser found for parsing the collection data, switched to use the default parser";
            log.info(warnMsg);
            parser = getDefaultXmlDocumentParser();
        }
        
        String collectionName = eadFile.getFileName();      
        // initializing the parser
        parser.init(collectionWork.getObjId(), eadFile.openStream(), collectionCfg);
        
        // Step 1: process collection from EAD: 
        //          - compare and update the metadata in collectionWork from the updated EAD finding aid header.
        //          - iterate through each component in the updated EAD, and merge the component into the collection of works
        //            under the collectionWork.
        processCollection(collectionWork, collectionName, eadFile.openStream(), collectionCfg, parser);
        
        // Step 2: generate the FINDING_AID_VIEW_COPY json from the updated FINDING_AID_COPY EAD attached to collectionWork
        generateJson(collectionWork, parser.storeCopy);
    }
    
    private static File getFindingAIDFile(Work collectionWork) {
        Copy eadCopy = collectionWork.getCopy(CopyRole.FINDING_AID_COPY);
        if (eadCopy == null || eadCopy.getFile() == null) {
            String errMsg = "Failed to process work collection as the input collection work " + collectionWork.getObjId() + " does not have a finding aid copy.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        File eadFile = eadCopy.getFile();
        return eadFile;
    }
    
    /**
     * generateJson generates a document in json format consist the mapping of the structure and
     * the content for top level collection metadata and its components and sub-components:
     *  - the structure is a json tree with top node at collection level and branches and leaves
     *    of components and sub-components. 
     *  - the content is a json array of items (including the collection and its components and 
     *    sub-components) in a flat structure, and each item within the array contains the required 
     *    metadata for the delivery.
     *    
     * If the storeCopy flag is set to true, the generated Json document will be stored as a finding aid view copy
     * of the top level collection work. 
     * 
     * Store Copy rule: if the collectionWork has already an existing finding aid view copy, and the existing
     *                  copy will be deleted and the newly generated view copy will be stored.
     *                
     * @param collectionWork - the top level work of a collection with a FINDING_AID_COPY attached.
     * @param storeCopy      - flag whether or not to store the generated json as a finding aid
     *                         view copy to the top level work.
     * @return Document      - the newly generated document containing structure and content json
     *                         nodes.
     * @throws IOException
     */
    public static Document generateJson(Work collectionWork, boolean storeCopy) throws IOException {
        if (collectionWork == null) {
            String errMsg = "Failed to generate collection json as the input collection work is null.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        ObjectNode structure = mapper.createObjectNode();
        ObjectNode content = mapper.createObjectNode();
        
        // create the document from the work.
        traverseCollection(collectionWork, structure, content);
        Document doc = new Document(structure, content);
        
        // store the document as a copy to collectionWork.
        if (storeCopy)
            storeEADCopy(collectionWork, CopyRole.FINDING_AID_VIEW_COPY, doc.toJson(), "application/json");
        return doc;
    }
    
    /**
     * filterEAD: filter the input EAD document so that only elements required for delivery are retained in the EAD document.
     * 
     *                    If the storeCopy flag is configured to be true in the parser, the filtered EAD document will be stored 
     *                    as a FINDING_AID__FILTERED_COPY to the top level collection work.
     *                    
     *                    Note: the EAD elements not required for delivery are specified in the "excludes" section of the collectionCfg provided.
     * 
     * @param collectionWork - the top level work of a collection with a FINDING_AID_COPY attached.
     * @param parser         - the XML document parser for parsing the EAD.
     * @return String        - the XML string of the filtered EAD document
     * @throws IOException
     * @throws ParsingException 
     * @throws ValidityException 
     */
    protected static String filterEAD(Work collectionWork, XmlDocumentParser parser) throws IOException, ValidityException, ParsingException {
        if (collectionWork == null) {
            String errMsg = "Failed to find finding aid copy for to filter EAD as the input collection work is null.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        if (parser == null) {
            String warnMsg = "No parser found for parsing the collection data, switched to use the default parser";
            log.info(warnMsg);
            parser = getDefaultXmlDocumentParser();
            parser.init(collectionWork.getObjId(), getFindingAIDFile(collectionWork).openStream(), getDefaultCollectionCfg());
        } else {
            parser.setInputStream(getFindingAIDFile(collectionWork).openStream());
        }
        
        // filter EAD document
        Node rootElement = parser.doc.getRootElement();
        parser.filterEAD(rootElement);
        String filteredEAD = rootElement.getDocument().toXML();
        
        // if storeCopy is set, also attach the filtered EAD xml document as a FINDING_AID__FILTERED_COPY to the collection level work
        if (parser.storeCopy)
            storeEADCopy(collectionWork, CopyRole.FINDING_AID_FILTERED_COPY, filteredEAD, "application/xml");
        return filteredEAD;
    }
    
    /**
     * extractEADComponent: extract the xml segment for the input EAD component.
     * 
     *                      If the storeCopy flag is configured to be true in the parser, the extracted xml segment for the EAD component 
     *                      will be stored as a FINDING_AID__FILTERED_COPY. 
     *  
     *                      Note: in the process of creating collection from EAD, and storing extracted xml segment as FINDING_AID__FILTERED_COPY,
     *                            this method should be called after filterEAD() method is called so that only required EAD elements for
     *                            delivery will be included in the extracted XML segment.
     *                    
     * @param collectionWork  - the top level work of a collection with a FINDING_AID_COPY attached   
     * @param componentASId   - the Archive Space uuid for the EAD component.                
     * @param componentWork   - the corresponding work for the EAD component. 
     * @param node            - the document node for the EAD component
     * @param parser          - the XML document parser for parsing EAD
     * @return String         - the XML segment from EAD file for this component
     * @throws IOException
     * @throws ParsingException 
     * @throws ValidityException 
     */
    protected static String extractEADComponent(Work collectionWork, String componentASId, Work componentWork, Node node, XmlDocumentParser parser) throws IOException, ValidityException, ParsingException {
        if (collectionWork == null)
            throw new IllegalArgumentException("Failed to return EAD segment, must supply the top level collection work.");
            
        if (componentASId == null)
            throw new IllegalArgumentException("Failed to return EAD segment, must supply an valid Archive Space id as the component AS id.");
        
        if (componentWork == null)
            throw new IllegalArgumentException("Failed to return EAD segment for collection: " + collectionWork.getObjId() + ", component: " + componentASId + " - no corresponding work found for this component.");
        
        if (node == null)
            throw new IllegalArgumentException("Failed to return EAD segment for collection: " + collectionWork.getObjId() + ", component: " + componentASId + " - supplied document node is null.");
        
        if (parser == null) {
            String warnMsg = "No parser found for parsing the collection data, switched to use the default parser";
            log.info(warnMsg);
            parser = getDefaultXmlDocumentParser();
            parser.init(collectionWork.getObjId(), getFindingAIDFile(collectionWork).openStream(), getDefaultCollectionCfg());
        }
        
        String componentEAD = node.toXML();
        if (parser.storeCopy) {
            storeEADCopy(componentWork, CopyRole.FINDING_AID_FILTERED_COPY, componentEAD, "application/xml");
        }
        return componentEAD;
    }
       
    private static void storeEADCopy(Work work, CopyRole copyRole, String content, String contentType) throws IOException {
        Copy eadCopy;
        if (work.getCopy(copyRole) == null) {
            // add new EAD copy if none exists
            eadCopy = work.addCopy();
            eadCopy.setCopyRole(copyRole.code());
        } else {
            // check and remove the previous copy
            eadCopy = work.getCopy(copyRole);
            if (eadCopy.getFile() != null)
                eadCopy.removeFile(eadCopy.getFile());
        }
        eadCopy.addFile(Writables.wrap(content), contentType);
    }
    
    protected static void processCollection(Work collectionWork, String collectionName, InputStream in, JsonNode collectionCfg, XmlDocumentParser parser) throws EADValidationException, ValidityException, ParsingException, IOException {
        boolean newCollection = true;
        Map<String, String> componentWorks;
        
        if (collectionWork.getChildren() != null && collectionWork.getChildren().iterator().hasNext()) {
            newCollection = false;
            componentWorks = componentWorksMap(collectionWork);
        } else {
            componentWorks = new ConcurrentHashMap<>();
        }
        
        // update metadata in the collection work.
        collectionWork.setCollection(collectionName);
        mapCollectionMD(collectionWork, collectionCfg.get(CFG_COLLECTION_ELEMENT), parser);
        
        // filter out elements to be excluded during delivery from the EAD, 
        // and by default configuration, the filtered EAD document will be stored
        // as the FINDING_AID__FILTERED_COPY of the top level collection work.
        filterEAD(collectionWork, parser);
        
        // traverse EAD components, and create work for each component under the top-level work, 
        // and map its metadata
        JsonNode subElementsCfg = collectionCfg.get(CFG_COLLECTION_ELEMENT).get(CFG_SUB_ELEMENTS);
        
        if (subElementsCfg != null) {
            String basePath = subElementsCfg.get(CFG_BASE).getTextValue();
            String repeatablePath = subElementsCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
            Nodes eadElements = parser.getElementsByXPath(parser.getDocument(), basePath);
            log.debug("sub elements found: " +  eadElements.size() + " for query " + basePath);
            if (eadElements != null && eadElements.size() > 0) {
                for (int i = 0; i < eadElements.size(); i++) {
                    Nodes eadSubElements = parser.traverse(eadElements.get(i), repeatablePath);
                    log.debug("sub elements found: " +  eadSubElements.size() + " for query repeatable path " + repeatablePath);
                    traverseEAD(collectionWork.asEADWork(), collectionWork.asEADWork(), eadSubElements, subElementsCfg, parser, newCollection, componentWorks);
                }
            }
        }
    }
    
    protected static void traverseEAD(EADWork collectionWork, EADWork parentWork, Nodes eadElements, JsonNode elementCfg, XmlDocumentParser parser, 
            boolean newCollection, Map<String, String> componentWorks) {
        for (int i = 0 ; i < eadElements.size(); i++) {
            Node eadElement = eadElements.get(i);
            EADWork workInCollection;
            if (newCollection) {
               workInCollection = parentWork.addEADWork();
               mapWorkMD(workInCollection, eadElement, elementCfg, parser); 
            } else {
               JsonNode component = ComponentBuilder.makeComponent(eadElement, elementCfg, parser);
               String uuid = component.get("uuid").getTextValue();
               if (componentWorks.get(uuid) != null) {
                   ((ObjectNode) component).put("nlaObjId", componentWorks.get(uuid));
               }
               workInCollection = ComponentBuilder.mergeComponent(collectionWork, parentWork, component);
            }                              
            
            String repeatablePath = elementCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
            Nodes nextLevel = parser.traverse(eadElement, repeatablePath);
            if (nextLevel != null)
                traverseEAD(collectionWork, workInCollection, nextLevel, elementCfg, parser, newCollection, componentWorks);
        }
    }
    
    protected static void mapCollectionMD(Work collectionWork, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException {
        Map<String, Object> fieldsMap = parser.getFieldsMap(parser.getDocument(), collectionCfg, parser.getBasePath(parser.getDocument()));  
        log.debug("collection config: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionCfg));
        log.debug("collection fieldMap: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fieldsMap));

        collectionWork.setCreator(fieldsMap.get("creator").toString());
        collectionWork.setSubType("Work");
        collectionWork.setSubUnitType("Collection");
        collectionWork.setForm("Manuscript");
        collectionWork.setBibLevel("Set");
        collectionWork.setCollection("nla.ms");
        collectionWork.setRecordSource("FA");
        collectionWork.asEADWork().setRdsAcknowledgementType("Sponsor");
        collectionWork.asEADWork().setRdsAcknowledgementReceiver("NLA");
        collectionWork.asEADWork().setEADUpdateReviewRequired("Y");   
        collectionWork.setAccessConditions("Unrestricted");
        collectionWork.setTitle(fieldsMap.get("title").toString());
    }
    
    protected static void mapWorkMD(EADWork workInCollection, Node eadElement, JsonNode elementCfg, XmlDocumentParser parser) throws EADValidationException {
        Map<String, Object> fieldsMap = parser.getFieldsMap(eadElement, elementCfg, parser.getBasePath(parser.getDocument()));        
        if (fieldsMap.get("uuid") == null || fieldsMap.get("uuid").toString().isEmpty())
            throw new EADValidationException("Failed to process collection " + parser.collectionObjId + " as no Archive Space id found for component work " + workInCollection.getObjId());
        
        // TODO: map subUnitType later on.
        String subUnitType = "Series";
        String uuid = fieldsMap.get("uuid").toString();
        ComponentBuilder.mapWorkMD(workInCollection, uuid, subUnitType);
    }
    
    protected static void traverseCollection (Work work, JsonNode structure, JsonNode content) {
        JsonNode workProperties = mapWorkProperties(work);

        String uuid = "";
        if (workProperties.get("localSystemNumber") != null && !workProperties.get("localSystemNumber").getTextValue().isEmpty()) {
            uuid = workProperties.get("localSystemNumber").getTextValue();
        }
        if (uuid.isEmpty()) {
            ((ObjectNode) structure).put(work.getObjId(), work.getCollection());
        } else {   
            ((ObjectNode) structure).put(work.getObjId(), uuid);
        }
        ((ObjectNode) content).put(work.getObjId(), workProperties);
        traverseCollection(work.getChildren(), structure, content);
    }
    
    protected static void traverseCollection (Iterable<Work> works, JsonNode structure, JsonNode content) {
        if (works == null || !works.iterator().hasNext()) return;
        ArrayNode arry = mapper.createArrayNode();
        ((ObjectNode) structure).put(XmlDocumentParser.CFG_SUB_ELEMENTS, arry);
        for (Work work : works) {
            JsonNode item = mapper.createObjectNode();
            arry.add(item);
            traverseCollection(work, item, content);
        }
    }
    
    private static JsonNode mapWorkProperties(Work work) {
        JsonNode workProperties = mapper.createObjectNode();
        String[] fields = { "creator", "title", "subType", "subUnitType", "form", "bibLevel", "collection", "recordSource", "localSystemNumber",
                               "rdsAcknowledgementType", "rdsAcknowledgementReceiver", "eadUpdateReviewRequired", "accessConditions" };
        for (String field : fields) {
            if (work.asVertex().getProperty(field) != null)
                ((ObjectNode) workProperties).put(field, work.asVertex().getProperty(field).toString());
        }
        return workProperties;
    }
}
