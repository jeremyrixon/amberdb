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

import antlr.StringUtils;

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
    
    /**
     * Get a list of field mapping configurations.  
     * 
     * @param fields     - the JsonNode to extract the field mapping configuration from.
     * @param recurse    - this option allow the method to get fields mapping grouped into
     *                     sections like in the following example:
     *                     
     *                     "fields": {
     *                        "collection-name":  "fileName",
     *                        "summary": {
     *                            "eadid":  "//ead:ead/ead:eadheader/ead:eadid",
     *                            ...
     *                        },
     *                        "introduction": {
     *                            "scope-n-content":    "//ead:ead/ead:archdesc/ead:scopecontent/ead:p",
     *                            ...
     *                        }
     *                     }
     *                     
     * note: As @scoen pointed out, the flattening of the config fields allows config overwriting of fields
     *       with the same name elsewhere in the config tree/hirearchy.  It's best to keep fields name 
     *       unique within a "fields" config segment.                    
     * @return field mapping configuration.
     */
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
                    // If the field is not a text field, then recurse down
                    // to get the mapping of fields attached this node.
                    // In this way, it allows the fields to be grouped 
                    // into sections in the configuration.
                    fldsCfg.putAll(getFldsMapCfg(field, recurse));                  
                }
            }
        }
        return fldsCfg;
    }
    
    protected Map<String, Object> mapFields(Node node, Map<String, Object> fldsCfg, String basePath, boolean rootElement) {
        Map<String, Object> fldsMap = new ConcurrentHashMap<>();
        if (fldsCfg == null) {
            String errMsg = "Failed to map element " + ((Element) node).getLocalName() + "under path " + basePath + ", cannot read fields mapping configuration.";
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        for (String fldName : fldsCfg.keySet()) {
            // create a map entry for the field
            fldsMap.put(fldName, "");
            
            // set the value for the field
            String fldCfg = fldsCfg.get(fldName).toString();
            if (fldCfg != null && !fldCfg.isEmpty()) {
                log.debug("fldName : " + fldName + ", xpath: " + fldCfg);
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
            log.debug("map from base path: " + basePath + ": " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fldsMap));
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return fldsMap;
    }
    
    private Object getAttribute(Node node, String xpath) {
        log.debug("getAttribute: xpath: " + xpath);
        if (node == null) return "";
        if (xpath.indexOf('@') >= 0) {
            String attribute = xpath.substring(xpath.indexOf('@') + 1);
            if (xpath.indexOf('@') == 0) {
                log.debug("getAttribute: attribute: " + attribute);
                log.debug("getAttribute: node: " + ((Element) node).getValue());
                return ((Element) node).getAttributeValue(attribute);
            } else {
                String subpath = xpath.substring(0, xpath.indexOf('@') - 1);
                Nodes nodes = node.query(subpath, this.xc);
                if (nodes != null && nodes.size() > 1) {
                    String[] values = new String[nodes.size()];
                    for (int i = 0; i < nodes.size(); i++) {
                        values[i] = ((Element) nodes.get(i)).getAttributeValue(attribute);
                    }
                    return values;
                } else if (nodes != null && nodes.size() == 1) {
                    return ((Element) nodes.get(0)).getAttributeValue(attribute);
                } else {
                    return "";
                }
            }
        }
        return "";
    }
}
