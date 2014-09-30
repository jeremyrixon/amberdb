package amberdb.model.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XmlDocumentParser {
    public static final String CFG_COLLECTION_ELEMENT = "collection";  // collection cfg tag
    public static final String CFG_SUB_ELEMENTS = "sub-elements";      // sub-elements cfg tag
    public static final String CFG_BASE = "base";                      // base cfg tag, use for base path of an repeatable element type
    public static final String CFG_REPEATABLE_ELEMENTS = "repeatable-element"; // cfg tag to specify how to identify repeatable elements.
    public static final String CFG_ATTRIBUTE_PREFIX = "@";
    public static final String CFG_EXCLUDE_ELEMENTS = "excludes";
    public static final String CFG_VALIDATE_XML = "validateXML";
    public static final String CFG_STORE_COPY = "storeCopy";
    
    static final Logger log = LoggerFactory.getLogger(XmlDocumentParser.class);
    protected static ObjectMapper mapper = new ObjectMapper();
    protected JsonNode parsingCfg;
    protected List<String> filters;
    protected boolean validateXML;
    protected boolean storeCopy;
    protected Builder builder;
    protected Document doc;
    protected XPathContext xc;
    protected String qualifiedName;
    protected String namespaceURI;
    
    public void init(InputStream in, JsonNode parsingCfg) throws ValidityException, ParsingException, IOException {
        this.parsingCfg = parsingCfg;
        this.validateXML = validateXML();
        this.storeCopy = storeCopy();
        filters = parseFiltersCfg();
        builder = new Builder(validateXML());
        doc = builder.build(in);
        qualifiedName = doc.getRootElement().getQualifiedName();
        namespaceURI = doc.getRootElement().getNamespaceURI();
        xc = new XPathContext();
        xc.addNamespace(qualifiedName, namespaceURI);
    }
    
    public Nodes traverse(Document doc) {
        Nodes nodes = new Nodes();
        nodes.append(doc.getRootElement());  
        return nodes;
    }
    
    public Nodes traverse(Node node, String repeatablePath) {
        String fmttedRepeatablePath = repeatablePath.replace(qualifiedName + ":", "").toUpperCase();
        Nodes nodes = new Nodes();
        Elements elements = ((Element) node).getChildElements();
        if (elements != null && elements.size() > 0) {
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getLocalName().toUpperCase().startsWith(fmttedRepeatablePath)) {
                    nodes.append(elements.get(i));
                }
            }
        }
        return nodes;
    }
    
    public List<String> getFilters() {
        return filters;
    }
    
    public boolean validateXML() {
        String validateXMLCfg = parsingCfg.get(CFG_COLLECTION_ELEMENT).get(CFG_VALIDATE_XML).getTextValue();
        if (validateXMLCfg != null && (validateXMLCfg.equalsIgnoreCase("yes") 
                || validateXMLCfg.equalsIgnoreCase("y") || validateXMLCfg.equalsIgnoreCase("true")))
                		return true;
        return false;
    }
    
    public boolean storeCopy() {
        String storeCopyCfg = parsingCfg.get(CFG_COLLECTION_ELEMENT).get(CFG_STORE_COPY).getTextValue();
        if (storeCopyCfg != null && (storeCopyCfg.equalsIgnoreCase("yes") 
                || storeCopyCfg.equalsIgnoreCase("y") || storeCopyCfg.equalsIgnoreCase("true")))
                        return true;
        return false;
    }
    
    protected List<String> parseFiltersCfg() {
        JsonNode filtersCfg = parsingCfg.get(CFG_COLLECTION_ELEMENT).get(CFG_EXCLUDE_ELEMENTS);
        filters = new ArrayList<>();
        if (filtersCfg.isArray() && ((ArrayNode) filtersCfg).size() > 0) {
            for (int i = 0; i < ((ArrayNode) filtersCfg).size(); i++)
                filters.add(((ArrayNode) filtersCfg).get(i).getTextValue().toUpperCase());
        }
        return filters;
    }
    
    public void filterEAD(Node node) { 
        Elements elements = ((Element) node).getChildElements();
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                if (filters.contains((element.getLocalName().toUpperCase()))) {
                    ((Element) node).removeChild(element);
                } else {
                    filterEAD(element);
                }
            }
        }
    }
    
    public Nodes getElementsByXPath(Document doc, String xpath) {
        String qualifiedName = doc.getRootElement().getQualifiedName();
        String namespaceURI = doc.getRootElement().getNamespaceURI();
        XPathContext xc = new XPathContext();
        xc.addNamespace(qualifiedName, namespaceURI);
        String qualifiedXPath = getQualifiedXPath(xpath);
        log.debug("qualifiedXPath: " + qualifiedXPath);
        Nodes nodes = doc.query(qualifiedXPath, xc);
        
        if (nodes == null || nodes.size() == 0) {
            String localXPath = qualifiedXPath.replaceAll(qualifiedName + ":", "");
            nodes = doc.query(localXPath, xc);
        }
        return nodes;
    }
    
    public Document getDocument() {
        return doc;
    }
    
    /**
     * Provide mapping of <field name, field value> of the root element of the document.
     * The field value can be of type Integer, Long, String, etc. 
     * 
     * @param doc - the input xml document.
     * @param collectionCfg - the fields config for mapping the top collection work
     * @return field mapping of the root element of the document
     */
    public abstract Map<String, Object> getFieldsMap(Document doc, JsonNode collectionCfg);
    
    /**
     * provide mapping of <field name, field value> of the input element.
     * The field value can be of type Integer, Long, String, etc.
     * 
     * @param node - the input xml node.
     * @param elementCfg - the fields config for mapping the sub work item.
     * @param basePath - the xpath for the input xml node.
     * @return field mapping of the input element.
     */
    public abstract Map<String, Object> getFieldsMap(Node node, JsonNode elementCfg, String basePath);
    
    protected String getQualifiedXPath(String xpath) {
        String qualifiedXPath = xpath.replace("//", "");
        
        if (queryAttribute(xpath)) {
            String attribute = qualifiedXPath.substring(qualifiedXPath.indexOf('@'));
            qualifiedXPath = qualifiedXPath.replace("/" + attribute, "");
        }
        return qualifiedXPath;
    }
    
    protected boolean queryAttribute(String xpath) {
        return xpath.contains("@");
    }
    
    protected Nodes findXmlSubElementFromDoc(Document doc, Node node, String repeatableElementName) {
        String repeatableElementLocalName = getElementLocalName(doc, repeatableElementName);
        Elements elements = ((Element) node).getChildElements();
        Nodes nodes = new Nodes();
        if (elements != null && elements.size() > 0) {
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getLocalName().toUpperCase().startsWith(repeatableElementLocalName.toUpperCase())) {
                    nodes.append(elements.get(i));
                }
            }
        }
        return nodes;
    }
    
    protected abstract void mapFields(Document doc, Node node, String fromPath, Map<String, Object> fldsMap);
    
    protected String constructPath(Document doc, String elementPath, Node node) {
        String qualifiedName = doc.getRootElement().getQualifiedName();
        if (node == null) return getBasePath(doc);
        
        String elementLocalName = ((Element) node).getLocalName();
        System.out.println("elementPath : " + elementPath + ", element local name: " + elementLocalName);
        String newPath = elementPath;
        if (elementPath.lastIndexOf('/') > 0)
            newPath = elementPath.substring(0, elementPath.lastIndexOf('/')) + "/" + qualifiedName + ":" + elementLocalName;
        
        return newPath;
    }
    
    protected String getBasePath(Document doc) {
        String qualifiedName = doc.getRootElement().getQualifiedName();
        return "//" + qualifiedName + ":" + qualifiedName;
    }
    
    protected String getElementLocalName(Document doc, String qualifiedElementName) {
        String qualifiedName = doc.getRootElement().getQualifiedName();
        return qualifiedElementName.replace(qualifiedName + ":", "");
    } 
    
    protected Map<Node, String> getFieldValues(Document doc, String xpath) {
        return getFieldValues(getElementsByXPath(doc, xpath));
    }

    private Map<Node, String> getFieldValues(Nodes nodes) {
        Map<Node, String> map = new ConcurrentHashMap<>();
        if (nodes != null && nodes.size() > 0) {
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                map.put(node, ((Element) node).getValue());
            }
        }
        return map;
    }
}
