package amberdb.sql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AmberElement {

    private AmberGraph graph;
    private long id;
    private String properties;
    
    // transaction details
    private long txnStart;
    private long txnEnd;
    private boolean txnOpen;
    
    private static final ObjectMapper json = new ObjectMapper();
    static {
        json.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        json.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    }    
    
    
    public void graph(AmberGraph graph) {
        this.graph = graph;
    }
    public AmberGraph graph() {
        return graph;
    }

    public void id(long id) {
        this.id = id;
    }
    public long id() {
        return id;
    }

    public void properties(String properties) {
        this.properties = properties;
    }

    public String properties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propertyName) {
        try {
            JsonNode root = json.readTree(properties);
            JsonNode val = root.path(propertyName);
            
            if (val.isInt()) {
                return (T) (Integer) val.asInt();
            } else if (val.isBoolean()) {
                return (T) (Boolean) val.asBoolean();
            } else if (val.isDouble()) {
                return (T) (Double) val.asDouble();
            } else {
                return (T) val.textValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getPropertyKeys() {
        Set<String> propertyKeys = new HashSet<String>();
        try {
            JsonNode root = json.readTree(properties);
            Iterator<String> propertyNames = root.fieldNames(); 
            while (propertyNames.hasNext()) {
                propertyKeys.add(propertyNames.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propertyKeys;
    }

    @SuppressWarnings("unchecked")
    public <T> T removeProperty(String propertyName) {
        Object prop;
        try {
            JsonNode root = json.readTree(properties);
            JsonNode val = root.path(propertyName);
            
            if (val.isInt()) {
                prop = (T) (Integer) val.asInt();
            } else if (val.isBoolean()) {    
                prop = (T) (Boolean) val.asBoolean();
            } else if (val.isDouble()) {    
                prop = (T) (Double) val.asDouble();
            } else {
                prop = (T) val.textValue();
            }
            if (prop == null)
                return null;
            ((ObjectNode) root).remove(propertyName);
            properties = json.writeValueAsString(root);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return (T) prop;
    }

    public void setProperty(String propertyName, Object value) {
        try {
            JsonNode root = json.readTree(properties);
            ObjectNode on = (ObjectNode) root;
            
            if (value instanceof Integer) {
                on.put(propertyName, (Integer) value);
            } else if (value instanceof Boolean) {   
                on.put(propertyName, (Boolean) value);
            } else if (value instanceof Double) {   
                on.put(propertyName, (Double) value);
            } else {
                on.put(propertyName, (String) value);
            }
            properties = json.writeValueAsString(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    
    /*
     * The transaction stuff might actually happen behind the scenes, but for now
     * access to these properties go here.
     */
    
    public void txnStart(long txnStart) {
        this.txnStart = txnStart;
    }
    public long txnStart() {
        return txnStart;
    }

    public void txnEnd(long txnEnd) {
        this.txnEnd = txnEnd;
    }
    public long txnEnd() {
        return txnEnd;
    }

    public void txnOpen(boolean txnOpen) {
        this.txnOpen = txnOpen;
    }
    public boolean txnOpen() {
        return txnOpen;
    }

}
