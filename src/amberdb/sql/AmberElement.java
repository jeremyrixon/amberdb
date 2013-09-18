package amberdb.sql;

import amberdb.sql.dao.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AmberElement {

    private long id;
    private AmberGraph graph;
    public static final String EMPTY_PROPERTIES = "{}"; 
    
    private Map<String, Object> properties = new HashMap<String, Object>(); 
    
    // transaction details
    private long txnStart;
    private long txnEnd;

    protected enum State {NEW, MODIFIED, DELETED, READ};
    private State txnState;
    
    public void graph(AmberGraph graph) {
        this.graph = graph;
    }
    public AmberGraph graph() {
        return graph;
    }

    public AmberGraphDao dao() {
        return graph.getDao();
    }
    
    public void id(long id) {
        this.id = id;
    }
    public long id() {
        return id;
    }

    public void properties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> properties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propertyName) {
        Object val = properties.get(propertyName);

        if (val instanceof Integer) {
            return (T) (Integer) val;
        } else if (val instanceof Boolean) {
            return (T) (Boolean) val;
        } else if (val instanceof Double) {
            return (T) (Double) val;
        } else {
            return (T) (String) val;
        }
    }

    public Set<String> getPropertyKeys() {
        return properties.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T removeProperty(String propertyName) {
        graph.currentTxn().removeProperty(this, propertyName);
        return (T) properties.remove(propertyName);
    }

    public void setProperty(String propertyName, Object value) {
        graph.currentTxn().setProperty(this, propertyName, value);
        properties.put(propertyName, value);
    }
    
    public void remove() {
        graph.currentTxn().removeElement(this);
        
    }
    
    /*
     * The transaction stuff might actually happen behind the scenes, but for now
     * access to these properties go here.
     */
    
    public void txnStart(long txnStart) { this.txnStart = txnStart; }
    public long txnStart() { return txnStart; }

    public void txnEnd(long txnEnd) { this.txnEnd = txnEnd; }
    public long txnEnd() { return txnEnd; }
    
    public void txnState(State state) { this.txnState = state; }
    public State txnState() { return txnState; }
    
}
