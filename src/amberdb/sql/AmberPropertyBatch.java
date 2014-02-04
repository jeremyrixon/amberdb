package amberdb.sql;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AmberPropertyBatch {

    
    List<Long>   id       = new ArrayList<Long>();
    List<String> name     = new ArrayList<String>();
    List<String> type     = new ArrayList<String>();
    List<byte[]> value    = new ArrayList<byte[]>();

    
    void add(Long id, Map<String, Object> properties) {
        if (properties == null) return;
        for (String name : properties.keySet()) {
            this.id.add(id);
            this.name.add(name);
            Object value = properties.get(name);
            this.type.add(DataType.forObject(value));
            this.value.add(AmberProperty.encode(value));
        }
    }
}
