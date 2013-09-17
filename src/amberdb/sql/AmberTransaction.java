package amberdb.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmberTransaction {

    long id;
    String user;
    Date commit;
    
    // All elements in this txn
    Map<Long, AmberEdge> edges = new HashMap<Long, AmberEdge>();
    Map<Long, AmberVertex> vertices = new HashMap<Long, AmberVertex>();
    
    // Elements that have been acted apon
    Map<Long, AmberEdge> newEdges = new HashMap<Long, AmberEdge>();
    Map<Long, AmberEdge> delEdges = new HashMap<Long, AmberEdge>();
    Map<Long, AmberEdge> modEdges = new HashMap<Long, AmberEdge>();
    
    Map<Long, AmberVertex> newVertices = new HashMap<Long, AmberVertex>();
    Map<Long, AmberVertex> delVertices = new HashMap<Long, AmberVertex>();
    Map<Long, AmberVertex> modVertices = new HashMap<Long, AmberVertex>();
    
   
    public AmberTransaction(String user) {
        this.setUser(user);
    }

    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getCommit() {
        return commit;
    }

    public void setCommit(Date commit) {
        this.commit = commit;
    }
    
    
    
    
}

class PropertyIndex {

    // property indexes
    Map<String, Map<Object, Set>> propertyIndex = new HashMap<String, Map<Object, Set>>();
    Map<Object, Set<Set>> objectIndex = new HashMap<Object, Set<Set>>();
    
    public void addProperty(String name, Object value, Object obj) {
        
        Map<Object, Set> propertyValues = propertyIndex.get(name);
        if (propertyValues == null) propertyValues = new HashMap<Object, Set>();

        Set objectsWithProperty = propertyValues.get(value);
        if (objectsWithProperty == null) objectsWithProperty = new HashSet();
        
        objectsWithProperty.add(obj);
        propertyValues.put(value, objectsWithProperty);
        propertyIndex.put(name, propertyValues);
        
        Set<Set> indexRefs = objectIndex.get(obj);
        if (indexRefs == null) indexRefs = new HashSet<Set>();
        
        indexRefs.add(objectsWithProperty);
        objectIndex.put(obj, indexRefs);
    }
    
    public void removeObject(Object obj) {
        
        Set<Set> indexRefs = objectIndex.get(obj);
        if (indexRefs == null) return;
        
        for (Set index : indexRefs) {
            index.remove(obj);
        }
        objectIndex.remove(obj);
   }

    
}