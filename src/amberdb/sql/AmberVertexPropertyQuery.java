package amberdb.sql;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Vertex;


public class AmberVertexPropertyQuery {

    // limit size of temp tables and tus the number of results returned
    // note: it is possible for the set of matches to include multiple 
    // copies of the same vertex if a vertex matches more than one 
    // property. This will result in less than 10000 unique vertices
    // being returned.
    static final int MAX_TMP_TAB_ROWS = 10000; 

    List<AmberProperty> properties = new ArrayList<AmberProperty>();
    private AmberGraph graph;


    protected AmberVertexPropertyQuery(AmberGraph graph) {
        this.graph = graph;
    }
    
    
    protected AmberVertexPropertyQuery(String name, Object value, AmberGraph graph) {

        // guards
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Property must have a name");
        if (value == null) throw new IllegalArgumentException("Property must have a non-null value");
        
        AmberProperty ap = new AmberProperty(0, name, value);
        this.properties.add(ap);
        this.graph = graph;
    }

    
    protected AmberVertexPropertyQuery(AmberProperty property, AmberGraph graph) {

        // guard
        if (property == null) throw new IllegalArgumentException("Property query must specify a property");
        
        this.properties.add(property);
        this.graph = graph;
    }
    
    
    protected AmberVertexPropertyQuery(List<AmberProperty> properties, AmberGraph graph) {
        
        // guards
        if (properties == null) throw new IllegalArgumentException("Property query must specify properties");
        properties.removeAll(Collections.singleton(null));
        if (properties.size() == 0) throw new IllegalArgumentException("Property query must specify properties");            
        
        this.properties = properties;
        this.graph = graph;
    }


    protected String generateVertexQuery() {
        
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO vp (id) \n"
                + "SELECT v.id \n"
                + "FROM vertex v \n"
                + "WHERE v.txn_end = 0 \n"
                + "LIMIT " + MAX_TMP_TAB_ROWS);

        return s.toString();
    }
    
    
    protected String generatePropertyQuery() {
        
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO vp (id) \n"
                + "SELECT p.id \n"
                + "FROM property p \n"
                + "WHERE p.txn_end = 0 \n"
                + "AND (");
        
        // add the property match clauses
        for (int i=0; i<properties.size(); i++) {
            s.append("(p.name = :name" + i + " AND p.value = :value"+ i +") OR \n");
        }
        
        s.setLength(s.length()-5);
        s.append(")\nLIMIT " + MAX_TMP_TAB_ROWS);

        return s.toString();
    }

    
    protected String generateQuery() {
        if (properties.size() == 0) {
            return generateVertexQuery();
        }
        return generatePropertyQuery();
    }
    
    
    public List<Vertex> execute() {

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {

            // run the generated query
            h.begin();
            h.execute("DROP TABLE IF EXISTS vp; CREATE TEMPORARY TABLE vp (id BIGINT);");
            Update q = h.createStatement(generateQuery());
            
            for (int i = 0; i < properties.size(); i++) {
                q.bind("name"+i, properties.get(i).getName());
                q.bind("value"+i, AmberProperty.encode(properties.get(i).getValue()));
            }
            q.execute();
            h.commit();

            // and reap the rewards
            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h);
            vertices = getVertices(h, graph, propMaps);
        }
        
        return vertices;
    }
    
    
    private Map<Long, Map<String, Object>> getElementPropertyMaps(Handle h) {
        
        List<AmberProperty> propList = h.createQuery(
                "SELECT p.id, p.name, p.type, p.value "
                + "FROM property p, vp " 
                + "WHERE p.id = vp.id "
                + "AND p.txn_end = 0")
                .map(new PropertyMapper()).list();

        Map<Long, Map<String, Object>> propertyMaps = new HashMap<Long, Map<String, Object>>();
        for (AmberProperty prop : propList) {
            Long id = prop.getId();
            if (propertyMaps.get(id) == null) {
                propertyMaps.put(id, new HashMap<String, Object>());
            }
            propertyMaps.get(id).put(prop.getName(), prop.getValue());
        }
        return propertyMaps;
    }
    
    
    private List<Vertex> getVertices(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<Vertex> vertices = new ArrayList<Vertex>();
        
        List<AmberVertexWithState> wrappedVertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM vertex v, vp "
                + "WHERE v.id = vp.id "
                + "AND v.txn_end = 0")
                .map(new VertexMapper(graph)).list();

        // add them to the graph
        for (AmberVertexWithState wrapper : wrappedVertices) {
            AmberVertex vertex = wrapper.vertex; 

            if (graph.removedVertices.contains(vertex)) {
                continue;
            }
            if (!graph.modifiedVertices.contains(vertex)) {
                vertex.replaceProperties(propMaps.get((Long) vertex.getId()));
                graph.addVertexToGraph(vertex);
            }
            vertices.add(vertex);
        } 
        
        return vertices;
    }
}
