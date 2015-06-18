package amberdb.model.builder;

import amberdb.PIUtil;
import amberdb.enums.AccessCondition;
import amberdb.enums.CopyRole;
import amberdb.enums.DigitalStatus;
import amberdb.model.*;
import com.google.common.base.Joiner;
import doss.core.Writables;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static amberdb.model.builder.XmlDocumentParser.*;

public class CollectionBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    static final ObjectMapper mapper = new ObjectMapper();
    
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
    protected static XmlDocumentParser getDefaultXmlDocumentParser() {
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
        
        // initializing the parser
        try (InputStream fileStream = eadFile.openStream()) {
            parser.init(collectionWork.getObjId(), fileStream, collectionCfg);
        }
        processCollection(collectionWork, collectionCfg, parser);
    }

    public static void createCollection(Work collectionWork, JsonNode collectCfg) throws ParsingException, IOException {
        createCollection(collectionWork, collectCfg, null);
    }

    /**
     * reloadEADPreChecks checks each EADwork within the collectionWork and returns a list of EADwork object id
     * if these EADwork does not exist in the new EAD file for reload.  
     *   
     * @param collectionWork - the top level work of a collection with the new updated EAD finding aid attached as
     *                     the FINDING_AID_COPY not yet containing
     *                     updates from the new updated FINDING_AID_COPY.
     * @return list of nla object ids of the EADworks requiring EAD update review.
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static Set<String> reloadEADPreChecks(Work collectionWork) throws ValidityException, ParsingException, IOException {
        return reloadEADPreChecks(collectionWork.asEADWork(), null);
    }
    
    /**
     * reloadEADPreChecks checks each EADwork within the collectionWork and returns a list of EADwork object id
     * if these EADwork does not exist in the new EAD file for reload.
     *   
     * @param collection - the top level work of a collection with the new updated EAD finding aid attached as
     *                     the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                     updates from the new updated FINDING_AID_COPY.
     * @param parser     - the XML document parser configured to parse the updated EAD.
     * @return list of nla object ids of the EADworks requiring EAD update review.
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static Set<String> reloadEADPreChecks(EADWork collection, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException {
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
        Set<String> eadUUIDList = parser.listUUIDs(currentComponents.size());
        Set<String> componentsNotInEAD = Collections.synchronizedSet(new HashSet<String>());
        
        for (String asId : currentComponents.keySet()) {
            if (asId != null && !asId.isEmpty() && !eadUUIDList.contains(asId)) {
                componentsNotInEAD.add(currentComponents.get(asId));
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
     * @throws IOException
     */
    protected static Map<String, String> componentWorksMap(Work collectionWork) throws IOException {
        List<Work> works = getObjIdsInTree(collectionWork);
        Map<String, String> uuidToPIMap = new HashMap<>();

        for (Work work : works) {
            if (work.getLocalSystemNumber() != null) {
                String uuid = work.getLocalSystemNumber();
                uuidToPIMap.put(uuid, work.getObjId());
            }
        }
        return uuidToPIMap;
    }


    
    /**
     * digitisedItemList provides a list of objId of each EAD works within collectionWork (including the collectionWork)
     * which has any digital object attach to it.
     * 
     * @param collectionWork - the top level work of a collection with the new updated EAD finding aid attached as
     *                         the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing 
     *                         updates from the new updated FINDING_AID_COPY.
     * @return a list of objId of each EAD works within collectionWork (including the collectionWork) which has any digital
     *         object attach to it.
     *         
     * @throws IOException
     */
    protected static Set<String> digitisedItemList(Work collectionWork) throws IOException {
        // Get a list of EAD component works in the current collection work
        // structure which has digital objects attached
        List<Work> works = getObjIdsInTree(collectionWork);
        Set<String> objIdList = new HashSet<String>();
        for (Work work : works) {
            String objId = work.getObjId();
            // find the component work
            if (!objId.equals(collectionWork.getObjId())) {
                EADWork component = work.asEADWork();

                // add entry to digitalObjectsMap if the component has any
                // copies attached
                if (component != null
                        && (component.getCopies() != null && component.getCopies().iterator().hasNext())) {
                    objIdList.add(objId);
                }
            }
        }
        return objIdList;
    }

    public static List<String> getWorksRequiringReview(Work collectionWork) {
        List<Work> works = getObjIdsInTree(collectionWork);
        List<String> worksForReview = new ArrayList<>();
        for (Work work : works) {
            EADWork eadWork = work.asEADWork();
            if ("Y".equals(eadWork.getEADUpdateReviewRequired())) {
                worksForReview.add(work.getObjId());
            }
        }
        return worksForReview;
    }

    private static List<Work> getObjIdsInTree(Work collectionWork) {
        List<Work> works = new ArrayList<>();
        getObjsInTreeR(collectionWork, works);
        return works;
    }

    private static void getObjsInTreeR(Work collectionWork, List<Work> populateList) {
        for (Work child : collectionWork.getChildren()) {
            populateList.add(child);
            getObjsInTreeR(child, populateList);
        }
    }
    
    /**
     * reloadCollection parses the updated EAD file attached to the top-level
     * collection work with the input field mapping from collectionCfg, and
     * update the collection work structure under the top level collection by
     * adding EAD work for each new EAD component; mark EAD work for an EAD
     * component that was previously there but not included in the updated EAD
     * file as EADUpdateReviewRequired; update EAD work for an EAD component 
     * with modified metadata.
     * 
     * pre-requisite: reloadEADPreChecks() has been called, and a list of components requiring EAD update
     *                review has been returned.
     *                
     * reloadCollection also call generateJson(...) to regenerate the derivative Json to reflect the updated
     * the mapping of the structure and the content for top level collection metadata and its components and 
     * sub-components.
     * 
     * if any of the EAD work in the current collection work structure has copies and files
     * attached to it, the copies and files will be retained.
     * 
     * @param collectionWork - input collectionWork is the top level work of a collection with the new updated EAD attached 
     *                         as the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing updates 
     *                         from the new updated FINDING_AID_COPY.
     * @throws EADValidationException
     * @throws IOException
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void reloadCollection(Work collectionWork) throws EADValidationException, IOException, ValidityException, ParsingException {
        reloadCollection(collectionWork, null, null);
    }

    public static void reloadCollection(Work collectionWork, JsonNode collectCfg) throws ParsingException, IOException {
        reloadCollection(collectionWork, collectCfg, null);
    }
    
    /**
     * reloadCollection parses the updated EAD file attached to the top-level
     * collection work with the input field mapping from collectionCfg, and
     * update the collection work structure under the top level collection by
     * adding EAD work for each new EAD component; mark EAD work for an EAD
     * component that was previously there but not included in the updated EAD
     * file as EADUpdateReviewRequired; update EAD work for an EAD component 
     * with modified metadata.
     * 
     * pre-requisite: reloadEADPreChecks() has been called, and a list of components requiring EAD update
     *                review has been returned.
     * 
     * reloadCollection also call generateJson(...) to regenerate the derivative Json to reflect the updated
     * the mapping of the structure and the content for top level collection metadata and its components and 
     * sub-components.
     * 
     * if any of the EAD work in the current collection work structure has copies and files
     * attached to it, the copies and files will be retained.
     * 
     * @param collectionWork - input collectionWork is the top level work of a collection with the new updated EAD attached 
     *                         as the FINDING_AID_COPY, and the FINDING_AID_VIEW_COPY containing json not yet containing updates 
     *                         from the new updated FINDING_AID_COPY.
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
        
        String collectionLevel = collectionWork.getBibLevel();
        // precheck
        Set<String> list = CollectionBuilder.reloadEADPreChecks(collectionWork);
        Set<String> currentDOs = digitisedItemList(collectionWork);
        
        // initializing the parser
        parser.init(collectionWork.getObjId(), eadFile.openStream(), collectionCfg);
        
        // Step 1: process collection from EAD: 
        //          - compare and update the metadata in collectionWork from the updated EAD finding aid header.
        //          - iterate through each component in the updated EAD, and merge the component into the collection of works
        //            under the collectionWork.
        processCollection(collectionWork, collectionCfg, parser);
             
        // mark the list of EAD works which requires review
        for (String objId : list) {
            EADWork eadWork = collectionWork.asEADWork().getEADWork(PIUtil.parse(objId));
            eadWork.setEADUpdateReviewRequired("Y");
        }
        
        // reset the digital status of digitised items
        for (String objId : currentDOs) {
            EADWork eadWork = collectionWork.asEADWork().getEADWork(PIUtil.parse(objId));
            eadWork.setDigitalStatus(DigitalStatus.DIGITISED.code());
        }
        // retain the original bib level for top level work
        collectionWork.setBibLevel(collectionLevel);
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
        eadCopy.setCopyType("b");
        eadCopy.setCarrier("Online");
        eadCopy.setSourceCopy(work.getCopy(CopyRole.FINDING_AID_COPY));
    }
    
    protected static void processCollection(Work collectionWork, JsonNode eadCfg, XmlDocumentParser parser) throws EADValidationException, ValidityException, ParsingException, IOException {
        boolean newCollection = true;
        Map<String, String> componentWorks;

        Set<String> dupUuids = parser.listDuplicateUUIDs();
        if (dupUuids.size() != 0) {
            throw new EADValidationException("EAD file contained duplicate IDs: " + Joiner.on(", ").join(dupUuids));
        }

        if (collectionWork.getChildren() != null && collectionWork.getChildren().iterator().hasNext()) {
            newCollection = false;
            componentWorks = componentWorksMap(collectionWork);
        } else {
            componentWorks = new ConcurrentHashMap<>();
        }
        
        JsonNode collectionCfg = eadCfg.get(CFG_COLLECTION_ELEMENT);
        // update metadata in the collection work.
        mapCollectionMD(collectionWork, collectionCfg, parser);
        
        // extract features like container list
        JsonNode featuresCfg = collectionCfg.get(CFG_FEATURE_ELEMENTS);
        if (featuresCfg != null)
            extractFeatures(collectionWork.asEADWork(), featuresCfg, parser);
        
        // extract entities like correspondence index
        JsonNode entitiesCfg = collectionCfg.get(CFG_ENTITY_ELEMENTS);
        if (entitiesCfg != null)
            extractEntities(collectionWork.asEADWork(), entitiesCfg, parser);
        
        // getNodeChildren EAD components, and create work for each component under the top-level work,
        // and map its metadata
        JsonNode subElementsCfg = collectionCfg.get(CFG_SUB_ELEMENTS);
        
        if (subElementsCfg != null) {
            String basePath = subElementsCfg.get(CFG_BASE).getTextValue();
            String repeatablePath = subElementsCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
            Nodes eadElements = parser.getElementsByXPath(parser.getDocument(), basePath);
            log.debug("sub elements found: " +  ((eadElements == null)? 0 : eadElements.size()) + " for query " + basePath);
            if (eadElements != null && eadElements.size() > 0) {
                for (int i = 0; i < eadElements.size(); i++) {
                    Nodes eadSubElements = parser.getNodeChildren(eadElements.get(i), repeatablePath);
                    log.debug("sub elements found: " +  eadSubElements.size() + " for query repeatable path " + repeatablePath);
                    traverseEAD(collectionWork.asEADWork(), collectionWork.asEADWork(), eadSubElements, subElementsCfg, parser, newCollection, componentWorks);
                }
            }
        }

        if (!collectionWork.getChildren().iterator().hasNext()) {
            throw new EADValidationException("FAILED_TO_CREATE_CHILD_WORK", collectionWork.getObjId(), collectionWork.getObjId());
        }
    }
    
    protected static void extractFeatures(EADWork collectionWork, JsonNode featuresCfg, XmlDocumentParser parser) {
        String basePath = featuresCfg.get(CFG_BASE).getTextValue();
        Nodes nodes = parser.getElementsByXPath(parser.getDocument(), basePath);
        for (int i = 0; i < nodes.size(); i++) {
            Map<String, String> mapping = parser.getFieldsMap(nodes.get(i), featuresCfg, basePath);
            String featureType = mapping.get("odd-type");
            try {
                if (featureType != null && !featureType.isEmpty()) {
                    EADFeature feature = collectionWork.addEADFeature();
                    feature.setFeatureType(featureType);
                    feature.setFeatureId(mapping.get("id"));
                    List<String> featurePara = toList(mapping.get("odd-paragraph"));
                    List<String> featureFields;
                    if (featurePara != null) {
                        featureFields = Arrays.asList(featureType);
                        feature.setFields(featureFields);
                        feature.setRecords(fmtFeatureData(featureFields, featurePara));
                        continue;
                    }
                    featureFields = toList(mapping.get("odd-fields"));
                    if (featureFields == null) {
                        throw new EADValidationException("FAILED_EXTRACT_FEATURE", featureType + " as there's no fields specified for " + featureType, collectionWork.getObjId());
                    }
                    feature.setFields(featureFields);
                    List<String> featureData = toList(mapping.get("odd-record-data"));
                    if (featureData == null || featureData.size() == 0) {
                        throw new EADValidationException("FAILED_EXTRACT_FEATURE", featureType + " as there's no records specified for " + featureType, collectionWork.getObjId());
                    }
                    feature.setRecords(fmtFeatureData(featureFields, featureData));
                } else {
                    throw new EADValidationException("FAILED_EXTRACT_FEATURE", "feature as it is missing feature type", collectionWork.getObjId());
                }
            } catch (IOException e) {
                log.error("Failed to extract feature " + featureType + " for work " + collectionWork.getObjId() + " due to " + e.getMessage() + ".");
                throw new EADValidationException("FAILED_EXTRACT_FEATURE", e, featureType, collectionWork.getObjId());
            }
        }
    }

    private static List<List<String>> fmtFeatureData(List<String> featureFields, List<String> featureData) {
        List<List<String>> featureRecords = new ArrayList<>();
        int dataSize = featureData.size();
        int noOfFields = featureFields.size();
        int i = 0;
        while (i < dataSize) {
            List<String> record = new ArrayList<>();
            for (int j = 0; j < noOfFields; j++) {
                record.add(featureData.get(i + j));
            }
            featureRecords.add(record);
            i = i + noOfFields;
        }
        return featureRecords;
    }
    
    protected static void extractEntities(EADWork collectionWork, JsonNode entitiesCfg, XmlDocumentParser parser) {
        String basePath = entitiesCfg.get(CFG_BASE).getTextValue();
        String repeatablePath = entitiesCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
        Nodes eadEntities = parser.getElementsByXPath(parser.getDocument(), basePath);
        
        if (eadEntities != null && eadEntities.size() > 0) {
            Map<String, String> mapping = parser.getFieldsMap(eadEntities.get(0), entitiesCfg, basePath);
            collectionWork.setCorrespondenceId(mapping.get("id"));
            collectionWork.setCorrespondenceHeader(mapping.get("header"));

            try {
                if (eadEntities.size() > 0) {
                    for (int i = 0; i < eadEntities.size(); i++) {
                        Nodes eadEntityEntries = eadEntities.get(i).query(repeatablePath, parser.xc);
                        log.debug("entity found: " + eadEntityEntries.size() + " for query repeatable path "
                                + repeatablePath);
                        
                        if (eadEntityEntries.size() == 0) {
                            throw new EADValidationException("FAILED_EXTRACT_ENTITIES", "" + (i+1), collectionWork.getObjId());
                        }
                        for (int j = 0; j < eadEntityEntries.size(); j++) {
                            Map<String, String> entityData = parser.getFieldsMap(eadEntityEntries.get(j), entitiesCfg,
                                    repeatablePath);
                            String corpName = entityData.get("corpname");
                            String famName = entityData.get("famname");
                            String persName = entityData.get("persname");
                            String ref = entityData.get("ref");

                            if (!isEmpty(corpName) || !isEmpty(famName) || !isEmpty(persName) || !isEmpty(ref)) {
                                EADEntity entity = collectionWork.addEADEntity();
                                List<String> entityName = new ArrayList<>();

                                if (!isEmpty(corpName)) {
                                    entityName.add(corpName);
                                    entity.setEntityType("corpname");
                                }
                                if (!isEmpty(persName)) {
                                    entityName.add(persName);
                                    entity.setEntityType("persname");
                                }
                                if (!isEmpty(famName)) {
                                    entityName.add(famName);
                                    entity.setEntityType("famname");
                                }
                                entity.setEntityName(entityName);
                                if (!isEmpty(ref)) {
                                    entity.setCorrespondenceRef(ref);
                                }
                            }

                        }
                    }
                }
            } catch (IOException e) {
                log.error("Failed to extract entities for work " + collectionWork.getObjId() + " due to " + e.getMessage() + ".");
                throw new EADValidationException("FAILED_EXTRACT_ENTITIES", e, "any", collectionWork.getObjId());
            }
        }
    }
    
    private static boolean isEmpty(String value) {
        return (value == null || value.isEmpty());
    }
    
    protected static void traverseEAD(EADWork collectionWork, EADWork parentWork, Nodes eadElements, JsonNode elementCfg, XmlDocumentParser parser, 
            boolean newCollection, Map<String, String> componentWorks) throws JsonParseException, JsonMappingException, IOException {
        for (int i = 0 ; i < eadElements.size(); i++) {
            Node eadElement = eadElements.get(i);
            
            EADWork workInCollection;
            if (newCollection) {
               workInCollection = mapWorkMD(parentWork, eadElement, elementCfg, parser, i); 
               if (collectionWork.getRepository() != null)
                   workInCollection.setRepository(collectionWork.getRepository());
               // inherit access conditions from the top-level collection work
               workInCollection.setAccessConditions(collectionWork.getAccessConditions());
            } else {
               JsonNode component = ComponentBuilder.makeComponent(eadElement, elementCfg, parser);
               String uuid = component.get("uuid").getTextValue();
               if (componentWorks.get(uuid) != null) {
                   ((ObjectNode) component).put("nlaObjId", componentWorks.get(uuid));
               }
               workInCollection = ComponentBuilder.mergeComponent(collectionWork, parentWork, component);
               if (collectionWork.getRepository() != null)
                   workInCollection.setRepository(collectionWork.getRepository());
            }      
            workInCollection.getParentEdge().setRelOrder(i + 1);
            
            String repeatablePath = elementCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
            Nodes nextLevel = parser.getNodeChildren(eadElement, repeatablePath);
            if (nextLevel != null)
                traverseEAD(collectionWork, workInCollection, nextLevel, elementCfg, parser, newCollection, componentWorks);
        }
    }
    
    protected static void mapCollectionMD(Work collectionWork, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException {
        if (!(collectionWork instanceof EADWork)) {
            collectionWork.asVertex().setProperty("type", EADWork.class.getSimpleName());
        }
        Map<String, String> fieldsMap = parser.getFieldsMap(parser.getDocument(), collectionCfg, parser.getBasePath(parser.getDocument()));  
        log.debug("collection config: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionCfg));
        log.debug("collection fieldMap: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fieldsMap));

        if (fieldsMap.get("repository") != null)
            collectionWork.asEADWork().setRepository(fieldsMap.get("repository"));
        
        if (fieldsMap.get("collection-number") != null)
            collectionWork.asEADWork().setCollectionNumber(fieldsMap.get("collection-number"));
        
        Object extent = fieldsMap.get("extent");
        if (extent != null && extent instanceof String) {
            if (!extent.toString().isEmpty()) {
                collectionWork.setExtent(extent.toString().replace("\"", "").replace("[", "").replace("]", ""));
            }  
        } else if (extent != null) {
            List<String> extentList = (List<String>) extent;
            String extentValue = StringUtils.join(extentList, ";");
            collectionWork.setExtent(extentValue.replace("\"", "").replace("[", "").replace("]", ""));
        }

        String creator = fieldsMap.get("creator").toString();
        if (creator != null && !creator.isEmpty()) {
            collectionWork.setCreator(creator.replace("\",\"", ";").replace("\"", "").replace("[", "").replace("]", ""));
        }
        collectionWork.setSubType("Work");
        collectionWork.setForm("Manuscript");
        
        // retain the original biblevel for the top level work
        if (collectionWork.getBibLevel() == null)
            collectionWork.setBibLevel("Set");

        collectionWork.asEADWork().setRdsAcknowledgementType("Sponsor");
        
        if (fieldsMap.get("sponsor") != null)
            collectionWork.asEADWork().setRdsAcknowledgementReceiver(fieldsMap.get("sponsor"));
        else    
            collectionWork.asEADWork().setRdsAcknowledgementReceiver("NLA"); 
        
        // default access conditions to Restricted if not set 
        if (collectionWork.getAccessConditions() == null || collectionWork.getAccessConditions().isEmpty()) 
            collectionWork.setAccessConditions(AccessCondition.RESTRICTED.code());
        
        // setting the dcm work pid
        String dcmPI = fieldsMap.get("dcmpi");
        if (dcmPI != null)
            collectionWork.setDcmWorkPid(dcmPI);
        
        collectionWork.setTitle(fieldsMap.get("title"));
        
        // setting the admin info
        mapAdminInfo(collectionWork, collectionCfg, fieldsMap);
        
        // setting the bibiography
        mapBibliography(collectionWork, fieldsMap);
        
        String scopeContent = fieldsMap.get("scope-n-content");
        if (scopeContent != null && !scopeContent.isEmpty()) {
            log.debug("collection work " + collectionWork.getObjId() + ": scope and content: " + scopeContent);
            collectionWork.asEADWork().setScopeContent(scopeContent);
        }
        
        try{
            String dateRange = fieldsMap.get("normal-date-range");
            ComponentBuilder.extractDateRange(collectionWork.asEADWork(), null, dateRange);
        } catch (EADValidationException e) {
            String dateRange = fieldsMap.get("date-range");
            ComponentBuilder.extractDateRange(collectionWork.asEADWork(), null, dateRange);
        }
        
        // setting Arrangement
        List<String> arrangement = toList(fieldsMap.get("arrangement"));
        if (arrangement != null && !arrangement.isEmpty()) {
            collectionWork.asEADWork().setArrangement(arrangement); 
        }
        
        // setting Provenance
        List<String> provenance = toList(fieldsMap.get("provenance"));
        if (provenance != null && !provenance.isEmpty()) {
            collectionWork.asEADWork().setProvenance(provenance);
        }
        
        // setting Copying Publishing
        List<String> copyingPublishing = toList(fieldsMap.get("copying-publishing"));
        if (copyingPublishing != null && !copyingPublishing.isEmpty()) {
            collectionWork.asEADWork().setCopyingPublishing(copyingPublishing);
        }
        
        // setting Preferred Citation
        List<String> preferredCitation = toList(fieldsMap.get("preferred-citation"));
        if (preferredCitation != null && !preferredCitation.isEmpty()) {
            collectionWork.asEADWork().setPreferredCitation(preferredCitation);
        }
        
        // setting Related Material
        List<String> relatedMaterial = toList(fieldsMap.get("related-material"));
        if (relatedMaterial != null && !relatedMaterial.isEmpty()) {
            collectionWork.asEADWork().setRelatedMaterial(relatedMaterial);
        } else {
            List<String> separatedMaterial = toList(fieldsMap.get("separated-material"));
            if (separatedMaterial != null && !separatedMaterial.isEmpty()) {
                collectionWork.asEADWork().setRelatedMaterial(separatedMaterial);
            }
        }
        
        // setting Access
        List<String> access = toList(fieldsMap.get("access"));
        if (access != null && !access.isEmpty()) {
            collectionWork.asEADWork().setAccess(access);
        }
    }
    
    private static List<String> toList(String extract) {
        if (extract != null && !extract.isEmpty()) {
            if (extract.startsWith("[")) {
                String[] array = extract.replace("[", "").replace("]", "").replace("\",", "\n").replace("\"", "").split("\n"); 
                return Arrays.asList(array);
            } 
            List<String> list = new ArrayList<String>();
            list.add(extract);
            return list;
        }
        return null;
    }

    private static void mapAdminInfo(Work collectionWork, JsonNode collectionCfg, Map<String, String> fieldsMap) {
        String adminInfo = "";
        Iterator<String>  fields = collectionCfg.getFieldNames();
        while (fields.hasNext()) {
            String fldName = fields.next();
            Object value = fieldsMap.get(fldName);
            if (value != null) {
                adminInfo += fldName + ": " + value;
            }
        }
        collectionWork.asEADWork().setAdminInfo(adminInfo);
    }
    
    private static void mapBibliography(Work collectionWork, Map<String, String> fieldsMap) throws EADValidationException {
        try {
            String bibliography = fieldsMap.get("bibliography");
            if (bibliography != null && !bibliography.isEmpty()) {
                List<String> biblioList = new ArrayList<String>();
                biblioList.add(bibliography);
                collectionWork.asEADWork().setBibliography(biblioList);
            } else {
                List<String> biographicalNote = toList(fieldsMap.get("biographical-note"));
                if (biographicalNote != null) {
                    collectionWork.asEADWork().setBibliography(biographicalNote);
                }
            }
        } catch (IOException e) {
            log.error("Failed to map bibliography for collection work " + collectionWork.getObjId() + " due to " + e.getMessage() + ".");
            throw new EADValidationException("FAILED_EXTRACT_BIBLIOGRAPHY", e, collectionWork.getObjId());
        }
    }
    
    protected static EADWork mapWorkMD(EADWork collectionWork, Node eadElement, JsonNode elementCfg, XmlDocumentParser parser, int ord) throws EADValidationException, JsonParseException, JsonMappingException, IOException {
        EADWork workInCollection = null;
        Map<String, String> fieldsMap = parser.getFieldsMap(eadElement, elementCfg, parser.getBasePath(parser.getDocument()));        
        if (fieldsMap.get("uuid") == null || fieldsMap.get("uuid").isEmpty()) {
           String collectionWorkUUID = (collectionWork.getLocalSystemNumber() == null)? "" : collectionWork.getLocalSystemNumber();  
           throw new EADValidationException("NO_UUID_FOR_CHILD_WORK", "" + (ord + 1), collectionWork.getObjId(), collectionWorkUUID);
        }
        String uuid = fieldsMap.get("uuid");
        workInCollection = collectionWork.checkEADWorkInCollectionByLocalSystemNumber(uuid);
        if (workInCollection == null) {
            workInCollection = collectionWork.addEADWork();
        }

        log.debug("fieldsMap: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fieldsMap));
        ComponentBuilder.mapWorkMD(workInCollection, uuid, fieldsMap);
        return workInCollection;
    }
}
