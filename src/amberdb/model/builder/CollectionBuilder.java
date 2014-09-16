package amberdb.model.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.model.EADWork;
import amberdb.model.Work;

public class CollectionBuilder {
    static final Logger log = LoggerFactory.getLogger(CollectionBuilder.class);
    
    /**
     * createCollection currently parses an ead file with the input field mapping 
     * from collectionCfg, and create the collection work structure under the
     * top level collection work. 
     * 
     * 
     * @param eadFilePath 
     * @param collectCfg
     * @return
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    public static void createCollection(Work collectionWork, Path eadFilePath, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException{
        // map metadata in the top-level work for the collection 
        File inFile = eadFilePath.toFile();
        String collectionName = inFile.getName();
        boolean validateXML = false;
        parser.init(inFile, validateXML);
        mapCollectionMD(collectionWork, eadFilePath, collectionCfg.get("collection"), parser);
        
        // traverse each work component under the top-level work, and map its metadata
        Nodes eadElements = parser.traverse(parser.getDocument());
        traverse(collectionWork.asEADWork(), eadElements, collectionCfg.get("collection"), parser);
    }
    
    public static void generateJson(Work collectionWork) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode structure = mapper.createObjectNode();
        ObjectNode content = mapper.createObjectNode();
        
        // create the document from the work.
        generateStructure(collectionWork, structure);
        generateContent(collectionWork, content);
        
        Document doc = new Document(structure, content);

        // TODO: store the document as a copy to collectionWork.
    }
    
    protected static void traverse(EADWork parentWork, Nodes eadElements, JsonNode elementCfg, XmlDocumentParser parser) {
        for (int i = 0 ; i < eadElements.size(); i++) {
            Node eadElement = eadElements.get(i);
            EADWork workInCollection = parentWork.addEADWork();
            mapWorkMD(workInCollection, eadElement, elementCfg, parser);
            
            Nodes nextLevel = parser.traverse(eadElement);
            if (nextLevel != null)
                traverse(workInCollection, nextLevel, elementCfg, parser);
        }
    }
    
    protected static void mapCollectionMD(Work collectionWork, Path eadFilePath, JsonNode collectionCfg, XmlDocumentParser parser) throws ValidityException, ParsingException, IOException {
        Map<String, Object> fieldsMap = parser.getFieldsMap(parser.getDocument(), collectionCfg);      
        collectionWork.setCollection(fieldsMap.get("collection-name").toString());
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
        Map<String, Object> fieldsMap = parser.getFieldsMap(eadElement, elementCfg);
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
    
    protected static void generateStructure(Work collectionWork, ObjectNode structure) {
        // TODO:
    }
    
    protected static void generateContent(Work collectionWork, ObjectNode content) {
        // TODO:
    }
}
