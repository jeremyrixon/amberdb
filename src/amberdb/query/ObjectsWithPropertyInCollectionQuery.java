package amberdb.query;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberQueryBase;


import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Vertex;


public class ObjectsWithPropertyInCollectionQuery extends AmberQueryBase {

    
    public ObjectsWithPropertyInCollectionQuery(AmberGraph graph) {
        super(graph);
    }

    
    public List<Vertex> execute(String name, String collectionName) {

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP " + graph.getTempTableDrop() + " TABLE IF EXISTS vp; CREATE TEMPORARY TABLE vp (id BIGINT) " + graph.getTempTableEngine() + ";");
            Update q = h.createStatement(
                    "INSERT INTO vp (id) \n"
                    + "SELECT DISTINCT p.id \n"
                    + "FROM property p, property cp, property tp \n"
                    + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end = 0 "
                    + " AND cp.id = p.id AND tp.id = p.id "
                    + " AND tp.name= :typeName "
                    + " AND tp.value = :type1 "
                    + " AND cp.name = :collection " 
                    + " AND p.name = :name "
                    + " AND cp.value = :value "
                    + "UNION \n"
                    + "SELECT DISTINCT p.id \n"
                    + "FROM property p, property cp, edge ed, property tp \n"
                    + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end = 0 and ed.txn_end = 0 "
                    + " AND p.id = ed.v_out AND tp.id = p.id and ed.v_in=cp.id "
                    + " AND tp.name= :typeName "
                    + " AND tp.value = :type2 "
                    + " AND cp.name = :collection "
                    + " AND p.name = :name "
                    + " AND cp.value = :value;");

            q.bind("name", name);
            q.bind("typeName", "type");
            q.bind("type1", AmberProperty.encode("Work"));
            q.bind("type2", AmberProperty.encode("Copy"));
            q.bind("collection", "collection");
            q.bind("value", AmberProperty.encode(collectionName));
            q.execute();
            h.commit();

            // and reap the rewards
            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "vp", "id");
            vertices = getVertices(h, graph, propMaps, "vp", "id", "id");
        }
        return vertices;
    }
    
    
    public List<Vertex> getExpiryReport(String expiryYear, String collectionName) {
   
    
        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP "
                    + graph.getTempTableDrop()
                    + " TABLE IF EXISTS er;CREATE TEMPORARY TABLE er (id BIGINT, millis BIGINT) "
                    + graph.getTempTableEngine() + ";");
            Update q = h
                    .createStatement("INSERT INTO er (id) \n"
                            + "SELECT DISTINCT p.id \n"
                            + "FROM property p"
                            + ", property cp, property tp, property iacp \n"
                            + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end=0 AND iacp.txn_end=0"
                            + " AND cp.id = p.id AND tp.id = p.id AND iacp.id = p.id "
                            + " AND tp.name= :typeName "
                            + " AND iacp.name= :internalAccessConditions"
                            + " AND tp.value = :type1 "
                            + " AND iacp.value != :closed "
                            + " AND cp.name = :collection "
                            + " AND p.name = :name "                     
                            + " AND YEAR(DATE_ADD(FROM_UNIXTIME(0), INTERVAL conv(hex(p.value), 16, 10)/1000 SECOND))  = :expiry " 
                            + " AND cp.value = :value ");

            q.bind("expiry", expiryYear);
            q.bind("name", "expiryDate");
            q.bind("internalAccessConditions", "internalAccessConditions");
            q.bind("closed", "Closed");
            q.bind("typeName", "type");
            q.bind("type1", AmberProperty.encode("Work"));
            q.bind("collection", "collection");
            q.bind("value", AmberProperty.encode(collectionName));
            q.execute();
            h.commit();

            // and reap the rewards
            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h,
                    "er", "id");
            vertices = getVertices(h, graph, propMaps, "er", "id", "id");
        }
        return vertices;
    }

    public static long getTimeMillis(java.sql.Timestamp ts) {
        return ts.getTime();
    }
}
