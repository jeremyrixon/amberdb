package amberdb.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class BaseElement {

    protected long id;
    protected Map<String, Object> properties;
    protected BaseGraph graph;
    
    BaseElement(long id, Map<String, Object> properties, BaseGraph graph) {
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Object>() : properties;
        this.graph = graph;
    }

    
    public Object getId() {
        return id;
    }

    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propertyName) {
        return (T) properties.get(propertyName);
    }

    
    public Set<String> getPropertyKeys() {
        Set<String> keys = Sets.newHashSet(properties.keySet());
        return keys;
    }

    
    @SuppressWarnings("unchecked")
    public <T> T removeProperty(String propertyName) {
        T value = (T) properties.remove(propertyName);
        return value;
    }

    
    public void setProperty(String propertyName, Object value) {
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        if (!(value instanceof Integer || value instanceof String || 
              value instanceof Boolean || value instanceof Double ||
              value instanceof Long    || value instanceof Float)) {
            throw new IllegalArgumentException("Illegal property type [" + value.getClass() + "].");
        }

        properties.put(propertyName, value);
    }
}
