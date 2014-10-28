package amberdb.graph;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberEdgeQuery extends AmberQueryBase {

    List<AmberProperty> properties = new ArrayList<AmberProperty>();
    /* whether to combine criteria using 'and' or 'or' */
    private boolean combineWithOr = true; // default to 'or' 
    
    
    public void combineCriteriaWithOr() {
        combineWithOr = true;
    }
    
    
    public void combineCriteriaWithAnd() {
        combineWithOr = false;
    }


    protected AmberEdgeQuery(AmberGraph graph) {
        super(graph);
    }
    

    /**
     * Add criteria of the form (property) name = value to the query. Notes:
     * Criteria with a null value will be ignored. Multi-value, json encoded
     * criteria will only match unreliably and probably shouldn't be used.
     * 
     * @param name
     *            The name of a property
     * @param value
     *            The value the property must be for a vertex to meet the
     *            criteria
     */
    public void addCriteria(String name, Object value) {
        
        // guards
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Property must have a name");
        
        AmberProperty ap = new AmberProperty(0, name, value);
        this.properties.add(ap);
    }

    
    /**
     * Add Property criteria to the query. Notes: Criteria with a null value
     * will be ignored. Multi-value, json encoded criteria will only match
     * unreliably and shouldn't be used for criteria.
     * 
     * @param properties A list of criteria properties
     */
    public void addCriteria(List<AmberProperty> properties) {
        
        // guards
        if (properties == null) throw new IllegalArgumentException("Property query must specify properties");
        properties.removeAll(Collections.singleton(null));
        if (properties.size() == 0) throw new IllegalArgumentException("Property query must specify properties");            
        
        this.properties.addAll(properties);
    }


    /**
     * Add Property criteria to the query. Notes: Criteria with a null value
     * will be ignored. Multi-value, json encoded criteria will only match
     * unreliably and shouldn't be used for criteria.
     * 
     * @param properties An array of criteria properties
     */
    public void addCriteria(AmberProperty... properties) {

        // guards
        if (properties == null) throw new IllegalArgumentException("Property query must specify properties");
        List<AmberProperty> props = Arrays.asList(properties);
        props.removeAll(Collections.singleton(null));
        if (props.size() == 0) throw new IllegalArgumentException("Property query must specify properties");            
        
        this.properties.addAll(props);
    }
    

    /* edge AND queries can't include labels currently */ 
    protected String generateAndQuery() {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ep (id) \n" 
                + "SELECT p0.id \n"
                + "FROM property p0 \n");
        
        for (int i = 1; i < properties.size(); i++) {
            if (properties.get(i).getName().equals("label")) continue;
            s.append(String.format("INNER JOIN property p%1$d ON (p0.id = p%1$d.id AND p0.txn_start = p%1$d.txn_start) \n", i));
        }
        
        s.append("WHERE p0.txn_end = 0 "
                + "AND p0.name = :name0 "
                + "AND p0.value = :value0 \n");
        
        for (int i = 1; i < properties.size(); i++) {
            s.append(String.format("AND p%1$d.txn_end = 0 "
                    + "AND p%1$d.name = :name%1$d "
                    + "AND p%1$d.value = :value%1$d \n",
                    i));
        }
        s.append(";\n");
        return s.toString();
    }
    

    protected String generateOrQuery() {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ep (id) \n");
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getName().equals("label")) {
                s.append("SELECT e.id \n"
                        + "FROM edge e \n"
                        + "WHERE e.txn_end = 0 \n"
                        + "AND e.label = :value"+ i + " \n"
                        + "UNION ALL \n");
            } else {
                s.append("SELECT p.id \n"
                        + "FROM property p \n"
                        + "WHERE p.txn_end = 0 \n"
                        + "AND p.name = :name" + i + " " 
                        + "AND p.value = :value"+ i + " \n"
                        + "UNION ALL \n");
            }
        }
        s.setLength(s.length()-13);
        s.append(";\n");
        return s.toString();
    }
    

    protected String generateAllQuery() {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ep (id) \n"
                 + "SELECT e.id \n"
                 + "FROM edge e \n"
                 + "WHERE e.txn_end = 0 \n;");
        return s.toString();
    }

    
    protected String generateQuery() {
        
        if (properties.size() == 0) {
            return generateAllQuery();
        }
        
        if (combineWithOr) {
            return generateOrQuery();
        } else {
            return generateAndQuery();        
        }
    }
    
    
    public List<Edge> execute() {

        List<Edge> edges;
        try (Handle h = graph.dbi().open()) {

            // run the generated query
            h.begin();
            h.execute("DROP TABLE IF EXISTS ep; CREATE TEMPORARY TABLE ep (id BIGINT);");
            Update q = h.createStatement(generateQuery());
            
            for (int i = 0; i < properties.size(); i++) {
                q.bind("name"+i, properties.get(i).getName());
                q.bind("value"+i, AmberProperty.encode(properties.get(i).getValue()));
            }
            q.execute();
            h.commit();

            // and reap the rewards
            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "ep", "id");
            edges = getEdges(h, graph, propMaps, "ep", "id");
        }
        return edges;
    }
}
