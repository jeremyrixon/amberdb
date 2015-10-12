package amberdb;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberProperty;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

/**
 * 
 * A class to migrate jelly properties to amber db properties and check some results.
 * Can likely be thrown away in the near future. 
 *
 */
public class JellyAmberPropertyMigrater {
    static final Logger log = LoggerFactory.getLogger(JellyAmberPropertyMigrater.class);
    public static void main(String[] args) {

        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser("dlir");
        ds.setPassword("dlir");
        ds.setServerName("snowy.nla.gov.au");
        ds.setPort(6446);
        ds.setDatabaseName("dlir");
        
        //getPropertiesFromDescription(ds);
        
        testGraph(ds);
        
    }
    
    public static void testGraph(MysqlDataSource pers) {
        AmberGraph g = new AmberGraph(pers);
        
        Vertex v1 = g.getVertex(179722129L);
        s(showVertex(v1));
        
        Iterable<Vertex> vs = v1.getVertices(Direction.IN);
        
        for (Vertex v : vs) {
            s(showVertex(v));
        }
    }

    public static String showVertex(Vertex v) {
        StringBuilder sb = new StringBuilder();
        sb.append(v.toString()).append('\n');
        for (String k : v.getPropertyKeys()) {
            sb.append("\t" + k + ": " + v.getProperty(k) + "\n");
        }
        return sb.toString();
    }
    
    public static void getPropertiesFromDescription(MysqlDataSource ds) throws UnsupportedEncodingException {
        
        DBI conn = new DBI(ds);
        
        Handle h = conn.open();
        
        List<Map<String,Object>> result = h.select("SELECT id, value FROM property WHERE name = 'description'");
        
        for (Map row : result) {
            Long id = (Long) row.get("id");
            String buff = new String((byte[]) row.get("value"), "UTF-8");
            s("" + id + ":" + buff);
            
            try {

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(buff, new TypeReference<Map<String, Object>>() {});

                for (String key: map.keySet()) {
                    s(">>>"+ key);
                    Object val = map.get(key);
                    String type = "STR";
                    if (val instanceof Integer) type = "INT";
                    byte[] v = AmberProperty.encode(val);
                    
                    h.createStatement("INSERT INTO property (id, txn_start, txn_end, type, name, value) VALUES (:id, 111, 0, :type, :name, :value)")
                        .bind("id", id)
                        .bind("type", type)
                        .bind("name", key)
                        .bind("value", v)
                        .execute();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        h.close();        
        
    }
    
    public static void s(String s) {
        log.info(s);
    }
}
