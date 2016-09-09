package amberdb.graph;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Edge;


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
        s.append("INSERT INTO ep (id) \n" +
        		"select flatedge.id \n" +
        		"from flatedge \n" +
        		"left join acknowledge on flatedge.id = acknowledge.id \n" +
        		"where \n");
        
        for (int i = 0; i < properties.size(); i++) {
        	String columnName = properties.get(i).getName().toLowerCase();
        	if ("label".equals(columnName)) {
        		columnName = "flatedge.label";  
        	}
        	s.append(columnName + " = :value"+ i + " \n and ");
        }
        s.setLength(s.length()-4);
        return s.toString();
    }

    protected String generateOrQuery() {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ep (id) \n");
        for (int i = 0; i < properties.size(); i++) {
        	String columnName = properties.get(i).getName().toLowerCase();
        	if ("label".equals(columnName)) {
        		columnName = "flatedge.label";  
        	}
            s.append(
            		"select flatedge.id \n" +
            		"from flatedge \n" +
            		"left join acknowledge on flatedge.id = acknowledge.id \n" +
            		"where " + columnName + " = :value"+ i + " \n"
                    + "UNION ALL \n");
        }
        s.setLength(s.length()-13);
        s.append(";\n");
        return s.toString();
    }
    

    protected String generateAllQuery() {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ep (id) \n"
                 + "SELECT e.id \n"
                 + "FROM flatedge e \n;");
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
            h.execute(
                    "DROP " + graph.tempTableDrop + " TABLE IF EXISTS ep; CREATE TEMPORARY TABLE ep (id BIGINT) " + graph.tempTableEngine + ";" +
                    "DROP " + graph.tempTableDrop + " TABLE IF EXISTS vp; CREATE TEMPORARY TABLE vp (id BIGINT) " + graph.tempTableEngine + ";");
            Update q = h.createStatement(generateQuery());
            
            for (int i = 0; i < properties.size(); i++) {
                q.bind("name"+i, properties.get(i).getName());
                q.bind("value"+i, AmberProperty.encode(properties.get(i).getValue()));
            }
            q.execute();
            h.execute(
                    "INSERT INTO vp " +
                    "SELECT v_in FROM flatedge e, ep WHERE e.id = ep.id " +
                    "UNION " +
                    "SELECT v_out FROM flatedge e, ep WHERE e.id = ep.id;");
            h.commit();

            // and reap the rewards
            Map<Long, Map<String, Object>> vPropMaps = getElementPropertyMaps(h, "ep", "id");
            getVertices(h, graph, vPropMaps, "vp", "id", "id");
            Map<Long, Map<String, Object>> ePropMaps = getElementPropertyMaps(h, "ep", "id");
            edges = getEdges(h, graph, ePropMaps, "ep", "id");
        }
        return edges;
    }
}
