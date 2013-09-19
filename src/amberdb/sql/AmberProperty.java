package amberdb.sql;

public class AmberProperty  {

    public String name; 
    public Object value;

    public AmberProperty(String name, Boolean bVal, Double dVal, String sVal, Integer iVal) {
        this.name = name;
        if (bVal != null) this.value = bVal;
        if (dVal != null) this.value = dVal;
        if (sVal != null) this.value = sVal;
        if (iVal != null) this.value = iVal;
    }    
}
