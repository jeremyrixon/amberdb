package amberdb.sql;


public class AmberProperty {

	
    private long id;
    private String name; 
    private Object value;
    
    
    // session constructor
    public AmberProperty(long id, String name, Object value) {
        this.id = id;
        this.name = name;
        this.value = value;
    } 
    
    
    public long getId() {
        return id;
    }
    
    
    public String getName() {
        return name;
    }

    
    public Object getValue() {
        return value;
    }
}
