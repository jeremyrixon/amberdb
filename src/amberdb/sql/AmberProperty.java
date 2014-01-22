package amberdb.sql;


public class AmberProperty {

    private long id;
    private String name; 
    private Object value;
    private DataType type;
    
    // session constructor
    public AmberProperty(long id, String name, DataType type, Object value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
    } 
    
    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public DataType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
