package amberdb.model.builder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EADParser extends XmlDocumentParser {
    static final Logger log = LoggerFactory.getLogger(EADParser.class);
    static ObjectMapper mapper = new ObjectMapper();
    
    public Map<String, Object> getFieldsMap(Document doc, JsonNode collectionCfg) {       
        boolean isRootElement = true;
        return mapFields(doc.getRootElement(), getFldsMapCfg(collectionCfg), getBasePath(doc), isRootElement);
    }
       
    public Map<String, Object> getFieldsMap(Node node, JsonNode elementCfg, String basePath) {
        boolean isRootElement = false;
        return mapFields(node, getFldsMapCfg(elementCfg), basePath, isRootElement);
    }
    
    /**
     * Get a list of field mapping configurations.  The configuration will allow fields to be grouped 
     * into sections.
     * 
     * @param elementCfg - the JsonNode to extract the field mapping configuration from.
     * @return field mapping configuration.
     */
    protected Map<String, Object> getFldsMapCfg(JsonNode elementCfg) {
            JsonNode fields = elementCfg.get("fields");
            boolean recurse = true;
            return getFldsMapCfg(fields, recurse);
    }
    
    protected Map<String, Object> getFldsMapCfg(JsonNode fields, boolean recurse) {
        Map<String, Object> fldsCfg = new ConcurrentHashMap<>();
        Iterator<String> fldsNames = fields.getFieldNames();
        if (fldsNames != null) {
            while (fldsNames.hasNext()) {
                String fldName = fldsNames.next();
                JsonNode field = fields.get(fldName);
                if (field.isTextual()) {
                    fldsCfg.put(fldName, field.getTextValue());
                } else {
                    fldsCfg.putAll(getFldsMapCfg(field, recurse));                  
                }
            }
        }
        return fldsCfg;
    }
    
    protected Map<String, Object> mapFields(Node node, Map<String, Object> fldsCfg, String basePath, boolean rootElement) {
        Map<String, Object> fldsMap = new ConcurrentHashMap<>();
        if (fldsCfg == null) return fldsMap;
        for (String fldName : fldsCfg.keySet()) {
            // create a map entry for the field
            fldsMap.put(fldName, "");
            
            // set the value for the field
            String fldCfg = fldsCfg.get(fldName).toString();
            if (fldCfg != null && !fldCfg.isEmpty()) {
                System.out.println("fldName : " + fldName + ", xpath: " + fldCfg);
                if (queryAttribute(fldCfg)) {
                    fldsMap.put(fldName, getAttribute(node, fldCfg));
                } else {
                    Nodes nodes = node.query(fldCfg, xc);
                    if (nodes != null && nodes.size() > 0)
                        fldsMap.put(fldName, nodes.get(0).getValue());
                }
            } 
        }
        try {
            System.out.println("map from base path: " + basePath + ": " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fldsMap));
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return fldsMap;
    }

    @Override
    protected void mapFields(Document doc, Node node, String fromPath, Map<String, Object> fldsMap) {
        if (fldsMap != null) {
            String qualifiedName = doc.getRootElement().getQualifiedName();
            String namespaceURI = doc.getRootElement().getNamespaceURI();
            XPathContext xc = new XPathContext();
            xc.addNamespace(qualifiedName, namespaceURI);
            fldsMap.put("xpath", constructPath(doc, fromPath, node));
            for (String key : fldsMap.keySet()) {
                String xpath = fldsMap.get(key).toString();
                mapField(doc, node, xc, key, xpath, fldsMap);
            }
        }
    }
    
    private String getAttribute(Node node, String xpath) {
        log.debug("getAttribute: xpath: " + xpath);
        if (node == null) return "";
        if (xpath.indexOf('@') >= 0) {
            String attribute = xpath.substring(xpath.indexOf('@') + 1);
            log.debug("getAttribute: attribute: " + attribute);
            log.debug("getAttribute: node: " + ((Element) node).getValue());
            return ((Element) node).getAttributeValue(attribute);
        }
        return "";
    }

    private void mapField(Document doc, Node node, XPathContext xc, String key, String xpath, Map<String, Object> fldsMap) {
        try {
            if (queryAttribute(xpath)) {
                if (xpath.startsWith("@")) {
                    fldsMap.put(key, getAttribute(node, xpath));
                    return;
                }

                String basePath = getQualifiedXPath(xpath);
                Nodes nodes;
                if (node == null || xpath.startsWith("//"))
                    nodes = doc.query(basePath, xc);
                else
                    nodes = node.query(basePath, xc);
                if (nodes != null && nodes.size() > 0)
                    fldsMap.put(key, getAttribute(nodes.get(0), xpath));
            } else {
                Nodes nodes;
                if (node == null || xpath.startsWith("//"))
                    nodes = doc.query(xpath, xc);
                else
                    nodes = node.query(xpath, xc);
                if (nodes != null && nodes.size() > 0)
                    fldsMap.put(key, ((Element) nodes.get(0)).getValue().trim());
            }
        } catch (nu.xom.XPathException e) {
            log.error("error in mapping config for field " + key + ": invalid xpath expression - " + xpath);
        }
    }
}
