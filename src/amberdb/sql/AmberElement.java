package amberdb.sql;

import amberdb.sql.dao.ElementDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmberElement extends Stateful {


    private ElementDao dao() { return graph().elementDao(); }   
 
    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propertyName) {
        AmberProperty property = dao().findProperty(id(), propertyName);
        if (property == null) return null;
        return (T) property.getValue();
    }

    public Set<String> getPropertyKeys() {
        
        Set<String> keys = new HashSet<String>();
        keys.addAll(dao().getPropertyKeys(id()));
        return keys;
    }

    public <T> T removeProperty(String propertyName) {
        
        dao().begin();
        AmberProperty property = dao().findProperty(id(), propertyName);
        if (property == null) return null;
        dao().deleteProperty(id(), propertyName);
        dao().commit();

        if (sessionState() == State.READ) {
            sessionState(State.MODIFIED);
        }
        return property.getValue();
    }

    public void setProperty(String propertyName, Object value) {
        
        dao().begin();
        AmberProperty property = dao().findProperty(id(), propertyName);
        if (property != null) {
            dao().deleteProperty(id(), propertyName);
        }
        if (value instanceof String) {
            dao().addStringProperty(id(), propertyName, (String) value);
        } else if (value instanceof Integer) {
            dao().addIntProperty(id(), propertyName, (Integer) value);
        } else if (value instanceof Boolean) {
            dao().addBooleanProperty(id(), propertyName, (Boolean) value);
        } else if (value instanceof Double) {
            dao().addDoubleProperty(id(), propertyName, (Double) value);
        }
        dao().commit();

        if (sessionState() == State.READ) {
            sessionState(State.MODIFIED);
        }
    }
    
    public void remove() {
        dao().deleteProperties(id());
        sessionState(State.DELETED);
        if (graph().autoCommit) graph().commitToPersistent("removeElement");
    }
    
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        for (String key: getPropertyKeys()) {
            properties.put(key, getProperty(key));
        }
        return properties;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (String key: getPropertyKeys()) {
            sb.append("[" + key + ":" + getProperty(key).toString() + "]");
        }
        sb.append("}");
        return sb.toString();
    }
}
