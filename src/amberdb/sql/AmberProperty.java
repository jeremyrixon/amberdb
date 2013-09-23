package amberdb.sql;

public class AmberProperty extends Persistent {

    public String name; 
    private Object value;
    private String type;
    
    // session constructor
    public AmberProperty(long id, String name, String type, 
            Boolean bVal, String sVal, Integer iVal, Double dVal) {
        
        id(id);
        this.name = name;
        this.type = type;
        if (type.equals("s")) { this.value = sVal; return; }
        if (type.equals("b")) { this.value = bVal; return; }
        if (type.equals("d")) { this.value = dVal; return; }
        if (type.equals("i")) { this.value = iVal; return; }
    } 
    
    // persistent constructor
    public AmberProperty(long id, long txnStart, long txnEnd, String name, String type, 
            Boolean bVal, String sVal, Integer iVal, Double dVal) {
        
        id(id);
        this.name = name;
        this.type = type;
        this.txnStart(txnStart);
        this.txnEnd(txnEnd);
        
        if (type.equals("s")) { this.value = sVal; return; }
        if (type.equals("b")) { this.value = bVal; return; }
        if (type.equals("d")) { this.value = dVal; return; }
        if (type.equals("i")) { this.value = iVal; return; }
    } 
    
    public AmberProperty(long id, String name, Object value) {
        this.name = name;
        this.value = value;
    } 
    
    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        
        if (type.equals("i")) {
            return (T) (Integer) value;
        } else if (type.equals("b")) {
            return (T) (Boolean) value;
        } else if (type.equals("d")) {
            return (T) (Double) value;
        } else {
            return (T) (String) value;
        }
    }
    
    protected String getType() {
        return type;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("property [")
        .append("id:").append(id()).append(", ").append(name)
        .append(": ").append(value).append("]");
        return sb.toString();
    }
}
