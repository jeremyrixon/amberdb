package amberdb;

import java.util.Map;
import java.util.List;

import javax.sql.DataSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.h2.jdbcx.JdbcConnectionPool;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import amberdb.sql.AmberGraph;
import amberdb.sql.AmberProperty;

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

    public static void main(String[] args) {

        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser("dlir");
        ds.setPassword("dlir");
        ds.setServerName("snowy.nla.gov.au");
        ds.setPort(6446);
        ds.setDatabaseName("dlir");
        
        //getPropertiesFromDescription(ds);
        
        DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:mem:sess","sess","sess");
        
        testGraph(sessionDs, ds);
        
    }
    
    public static void testGraph(DataSource sess, MysqlDataSource pers) {
        AmberGraph g = new AmberGraph(sess, pers);
        
        Vertex v1 = g.getVertex(179722129l);
        s(showVertex(v1));
        
        Iterable<Vertex> vs = v1.getVertices(Direction.IN);
        
        for (Vertex v : vs) {
            s(showVertex(v));
        }
    }

    public static String showVertex(Vertex v) {
        StringBuilder sb = new StringBuilder();
        sb.append(v.toString()).append("\n");
        for (String k : v.getPropertyKeys()) {
            sb.append("\t" + k + ": " + v.getProperty(k) + "\n");
        }
        return sb.toString();
    }
    
    public static void getPropertiesFromDescription(MysqlDataSource ds) {
        
        DBI conn = new DBI(ds);
        
        Handle h = conn.open();
        
        List<Map<String,Object>> result = h.select("SELECT id, value FROM property WHERE name = 'description'");
        
        for (Map row : result) {
            Long id = (Long) row.get("id");
            String buff = new String((byte[]) row.get("value"));
            s("" + id + ":" + buff);
            
            try {

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(buff, new TypeReference<Map<String, Object>>() {});

                for (String key: map.keySet()) {
                    s(">>>"+ key);
                    Object val = map.get(key);
                    String type = "STR";
                    if (val instanceof Integer) type = "INT";
                    byte[] v = AmberProperty.encodeBlob(val);
                    
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
        System.out.println(s);
    }
}
