package amberdb.graph;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Vertex;


public class AmberQueryGetVertices extends AmberQueryBase {

    // limit size of temp tables and the number of results returned
    // note: it is possible for the set of matches to include multiple 
    // copies of the same vertex if a vertex matches more than one 
    // property. This will result in less than 10000 unique vertices
    // being returned.
    static final int MAX_TMP_TAB_ROWS = 10000; 

    List<AmberProperty> properties = new ArrayList<AmberProperty>();


    protected AmberQueryGetVertices(AmberGraph graph) {
        super(graph);
    }
    

    protected String generateQuery() {
        
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO vp (id) \n"
                + "SELECT v.id \n"
                + "FROM vertex v \n"
                + "WHERE v.txn_end = 0 \n"
                + "LIMIT " + MAX_TMP_TAB_ROWS);

        return s.toString();
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
            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "vp", "id");
            vertices = getVertices(h, graph, propMaps, "vp", "id", "id");
        }
        
        return vertices;
    }
}
