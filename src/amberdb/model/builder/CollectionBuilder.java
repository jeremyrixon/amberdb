package amberdb.model.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import doss.core.Writables;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.EADWork;
import amberdb.model.File;
import amberdb.model.Work;

import static amberdb.model.builder.XmlDocumentParser.CFG_COLLECTION_ELEMENT;
import static amberdb.model.builder.XmlDocumentParser.CFG_SUB_ELEMENTS;
import static amberdb.model.builder.XmlDocumentParser.CFG_BASE;
import static amberdb.model.builder.XmlDocumentParser.CFG_REPEATABLE_ELEMENTS;
import static amberdb.model.builder.XmlDocumentParser.CFG_ATTRIBUTE_PREFIX;

public class CollectionBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * createCollection in absence of collection configuration and document parser input parameters, 
     * resolves to default collection JSON configuration and the default EAD parser in order to 
     * create the collection work structure under the top level collection work.
     * 
     * @param collectionWork: the top-level collection work with EAD file attached.
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void createCollection(Work collectionWork) throws ValidityException, ParsingException, IOException {
        createCollection(collectionWork, getDefaultCollectionCfg(), getDefaultXmlDocumentParser());
    }
    
    /**
     * getDefaultCollectionCfg returns the default configuration for creating a hierarchy of works under
     * the top level collection work.  The default configuration sets validateXML to be true and 
     * storeCopy to be true.
     */
    public static JsonNode getDefaultCollectionCfg() {
        return new EADConfiguration().getConfig();
    }
    
    /**
     * getDefaultXmlDocumentParser returns the EADParser as the default xml document parser
     * for the CollectionBuilder.
     */
    public static XmlDocumentParser getDefaultXmlDocumentParser() {
        // return the default document parser;
        return new EADParser();
    }
    
    /**
     * createCollection parses the ead file attached to the top-level
     * collection work with the input field mapping from collectionCfg, and 
     * create the collection work structure under the top level collection work. 
     * 
     * @param collectionWork - the top level work to attach a collection of works to. 
     * @param collectionCfg  - configuration for parsing attached EAD file for creating
     *                         the collection.
     * @param parser         - the XML document parser for parsing the EAD. 
     * @param validateXML    - flag whether or not to validate the input EAD as valid XML.
     * @return
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void createCollection(Work collectionWork, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException{
        // map metadata in the top-level work for the collection 
        if (collectionWork == null) {
            String errMsg = "Failed to create work collection as the input collection work is null.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        Copy eadCopy = collectionWork.getCopy(CopyRole.FINDING_AID_COPY);
        if (eadCopy == null || eadCopy.getFile() == null) {
            String errMsg = "Failed to create work collection as the input collection work " + collectionWork.getObjId() + " does not have a finding aid copy.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        File eadFile = eadCopy.getFile();
        
        String collectionName = eadFile.getFileName();
        createCollection(collectionWork, collectionName, eadFile.openStream(), collectionCfg, parser);
    }
    
    /**
     * generateJson generates a document in json format consist the mapping of the structure and
     * an array of items with each item contains the detailed content required for the delivery,
     * and if the storeCopy flag is set to true, stores the document as a finding aid view copy. 
     * 
     * Pre-condition: if the collectionWork has already an existing finding aid view copy, and the 
     *                copy must be deleted and the storeCopy must be set to true before the newly
     *                generated view copy can be stored.
     *                
     * @param collectionWork - the top level work to attach a collection of works to.
     * @param storeCopy    - flag whether or not to store the generated json as a finding aid
     *                         view copy to the top level work.
     * @return Document      - the newly generated document containing structure and content json
     *                         nodes.
     * @throws IOException
     */
    public static Document generateJson(Work collectionWork, boolean storeCopy) throws IOException {
        ObjectNode structure = mapper.createObjectNode();
        ObjectNode content = mapper.createObjectNode();
        
        // create the document from the work.
        traverseCollection(collectionWork, structure, content);
        Document doc = new Document(structure, content);
        
        // store the document as a copy to collectionWork.
        if (storeCopy)
            storeEADCopy(collectionWork, CopyRole.FINDING_AID__VIEW_COPY, doc.toJson(), "application/json");
        return doc;
    }
    
    /**
     * attachFilteredEAD: filter the input EAD document, and attach the filtered EAD document as a FINDING_AID__FILTERED_COPY 
     *                    to the top level collection work.
     *                    
     *                    The input EAD document will be filtered by all the EAD elements specified in the "excludes" section of
     *                    the collectionCfg provided.
     * 
     * @param collectionWork
     * @param doc
     * @param parser
     * @param storeCopy
     * @return
     * @throws IOException
     */
    protected static String filterEAD(Work collectionWork, XmlDocumentParser parser) throws IOException {
        // filter EAD document
        Node rootElement = parser.doc.getRootElement();
        parser.filterEAD(rootElement);
        String filteredEAD = rootElement.getDocument().toXML();
        
        // if storeCopy is set, also attach the filtered EAD xml document as a FINDING_AID__FILTERED_COPY to the collection level work
        if (parser.storeCopy)
            storeEADCopy(collectionWork, CopyRole.FINDING_AID__FILTERED_COPY, filteredEAD, "application/xml");
        return filteredEAD;
    }
    
    /**
     * attachEADComponent: filter the input EAD xml node, and attach the (filtered) EAD node as a FINDING_AID__FILTERED_COPY to the
     *                    input work within a top level collection which corresponds to an EAD component.
     * 
     *                    The input EAD xml node will be filtered by all the EAD elements specified in the "excludes" section of 
     *                    the collectionCfg provided.
     *                    
     * Pre-condition: the method createCollection() and attachFilteredEAD() need to be called before (to create component works)
     *                before attachFilteredEAD() method can be called to attach the component XML segment
     *                from EAD to the specified componentWork as a FINDING_AID__FILTERED_COPY.
     * @param componentWork
     * @param storeCopy
     * @return
     * @throws IOException
     */
    protected static String extractEADComponent(Work componentWork, Node node, XmlDocumentParser parser) throws IOException {
        // NOTE: as the EAD node has been filtered by the attachFilteredEAD() in the pre-condition
        //       there's no need to filter this again.
        String componentEAD = node.toXML();
        if (parser.storeCopy) {
            storeEADCopy(componentWork, CopyRole.FINDING_AID__FILTERED_COPY, componentEAD, "application/xml");
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
    
    protected static void createCollection(Work collectionWork, String collectionName, InputStream in, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException {
        // initializing the parser
        parser.init(in, collectionCfg);
        
        // update metadata in the collection work.
        collectionWork.setCollection(collectionName);
        mapCollectionMD(collectionWork, collectionCfg.get(CFG_COLLECTION_ELEMENT), parser);
        
        // filter out elements to be excluded during delivery from the EAD, and update the parser to cache the filtered EAD xml document.
        filterEAD(collectionWork, parser);
        
        // traverse each work component under the top-level work, and map its metadata
        JsonNode subElementsCfg = collectionCfg.get(CFG_COLLECTION_ELEMENT).get(CFG_SUB_ELEMENTS);
        
        if (subElementsCfg != null) {
            String basePath = subElementsCfg.get(CFG_BASE).getTextValue();
            String repeatablePath = subElementsCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
            Nodes eadElements = parser.getElementsByXPath(parser.getDocument(), basePath);
            System.out.println("sub elements found: " +  eadElements.size() + " for query " + basePath);
            if (eadElements != null && eadElements.size() > 0) {
                for (int i = 0; i < eadElements.size(); i++) {
                    Nodes eadSubElements = parser.traverse(eadElements.get(i), repeatablePath);
                    System.out.println("sub elements found: " +  eadSubElements.size() + " for query repeatable path " + repeatablePath);
                    traverseEAD(collectionWork.asEADWork(), eadSubElements, subElementsCfg, parser);
                }
            }
        }
    }
    
    protected static void traverseEAD(EADWork parentWork, Nodes eadElements, JsonNode elementCfg, XmlDocumentParser parser) {
        for (int i = 0 ; i < eadElements.size(); i++) {
            Node eadElement = eadElements.get(i);
            EADWork workInCollection = parentWork.addEADWork();
            mapWorkMD(workInCollection, eadElement, elementCfg, parser);
            
            String repeatablePath = elementCfg.get(CFG_REPEATABLE_ELEMENTS).getTextValue();
            Nodes nextLevel = parser.traverse(eadElement, repeatablePath);
            if (nextLevel != null)
                traverseEAD(workInCollection, nextLevel, elementCfg, parser);
        }
    }
    
    protected static void mapCollectionMD(Work collectionWork, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException {
        Map<String, Object> fieldsMap = parser.getFieldsMap(parser.getDocument(), collectionCfg, parser.getBasePath(parser.getDocument()));  
        // System.out.println("collection config: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionCfg));
        // System.out.println("collection fieldMap: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fieldsMap));

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
    
    protected static void mapWorkMD(EADWork workInCollection, Node eadElement, JsonNode elementCfg, XmlDocumentParser parser) {
        Map<String, Object> fieldsMap = parser.getFieldsMap(eadElement, elementCfg, parser.getBasePath(parser.getDocument()));
        // workInCollection.
        workInCollection.setSubType("Work");      
        workInCollection.setSubUnitType("Series");
        workInCollection.setForm("Manuscript");
        workInCollection.setBibLevel("Item");
        workInCollection.setCollection("nla.ms");
        workInCollection.setRecordSource("FA");
        workInCollection.setLocalSystemNumber(fieldsMap.get("uuid").toString());
        workInCollection.setRdsAcknowledgementType("Sponsor");
        workInCollection.setRdsAcknowledgementReceiver("NLA");
        workInCollection.setEADUpdateReviewRequired("Y"); 
        workInCollection.setAccessConditions("Unrestricted");
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
        ((ObjectNode) structure).put("sub-element", arry);
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
