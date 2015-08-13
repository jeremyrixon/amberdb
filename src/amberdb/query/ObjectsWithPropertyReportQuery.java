package amberdb.query;

import amberdb.graph.AmberGraph;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberQueryBase;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

import com.tinkerpop.blueprints.Vertex;


public class ObjectsWithPropertyReportQuery extends AmberQueryBase {

    
    public ObjectsWithPropertyReportQuery(AmberGraph graph) {
        super(graph);
    }

    
    public List<Vertex> generateDuplicateAliasReport(String name, String collectionName) {

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
                    + " AND cp.value = :value "
                    + "UNION \n"
                    + "SELECT DISTINCT p.id \n"
                    + "FROM property p, property cp, property tp \n"
                    + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end = 0 "
                    + " AND cp.id = p.id AND tp.id = p.id "
                    + " AND tp.name= :typeName "
                    + " AND tp.value = :type3 "
                    + " AND cp.name = :collection " 
                    + " AND p.name = :name "
                    + " AND cp.value = :value "
                    + "UNION \n"
                    + "SELECT DISTINCT p.id \n"
                    + "FROM property p, property cp, property tp \n"
                    + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end = 0 "
                    + " AND cp.id = p.id AND tp.id = p.id "
                    + " AND tp.name= :typeName "
                    + " AND tp.value = :type4 "
                    + " AND cp.name = :collection " 
                    + " AND p.name = :name "
                    + " AND cp.value = :value "
                    + "UNION \n"
                    + "SELECT DISTINCT p.id \n"
                    + "FROM property p, property cp, property tp \n"
                    + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end = 0 "
                    + " AND cp.id = p.id AND tp.id = p.id "
                    + " AND tp.name= :typeName "
                    + " AND tp.value = :type5 "
                    + " AND cp.name = :collection " 
                    + " AND p.name = :name "
                    + " AND cp.value = :value ;"
                    );

            q.bind("name", name);
            q.bind("typeName", "type");
            q.bind("type1", AmberProperty.encode("Work"));
            q.bind("type2", AmberProperty.encode("Copy"));
            q.bind("type3", AmberProperty.encode("EADWork"));
            q.bind("type4", AmberProperty.encode("Section"));
            q.bind("type5", AmberProperty.encode("Page"));
            q.bind("collection", "collection");
            q.bind("value", AmberProperty.encode(collectionName));
            q.execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "vp", "id");
            vertices = getVertices(h, graph, propMaps, "vp", "id", "id");
        }
        return vertices;
    }
    
    
    public List<Vertex> generateExpiryReport(Date expiryYear,
            String collectionName) {

        long datetime = expiryYear.getTime();

        String compareStatement = "AND conv(hex(p.value), 16, 10) = :expiry ";

        // compare statement for H2
        if ("".equals(graph.getTempTableDrop())) {
            compareStatement = "AND  CAST(CAST(p.value AS BINARY) AS BIGINT) = :expiry ";
        }

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP "
                    + graph.getTempTableDrop()
                    + " TABLE IF EXISTS er;CREATE TEMPORARY TABLE er (id BIGINT) "
                    + graph.getTempTableEngine() + ";");
            Update q = h
                    .createStatement("INSERT INTO er (id) \n"
                            + "SELECT DISTINCT p.id  \n"
                            + "FROM property p"
                            + ", property cp, property tp, property iacp \n"
                            + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end=0 AND iacp.txn_end=0"
                            + " AND cp.id = p.id AND tp.id = p.id AND iacp.id = p.id "
                            + " AND tp.name= :typeName "
                            + " AND iacp.name= :internalAccessConditions"
                            + " AND tp.value = :type1 "
                            + " AND iacp.value != :closed "
                            + " AND cp.name = :collection "
                            + " AND p.name = :name " + compareStatement
                            + " AND cp.value = :value "
                            + "UNION \n"
                            + "SELECT DISTINCT p.id  \n"
                            + "FROM property p"
                            + ", property cp, property tp, property iacp \n"
                            + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end=0 AND iacp.txn_end=0"
                            + " AND cp.id = p.id AND tp.id = p.id AND iacp.id = p.id "
                            + " AND tp.name= :typeName "
                            + " AND iacp.name= :internalAccessConditions"
                            + " AND tp.value = :type2 "
                            + " AND iacp.value != :closed "
                            + " AND cp.name = :collection "
                            + " AND p.name = :name " + compareStatement
                            + " AND cp.value = :value "
                            + "UNION \n"
                            + "SELECT DISTINCT p.id  \n"
                            + "FROM property p"
                            + ", property cp, property tp, property iacp \n"
                            + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end=0 AND iacp.txn_end=0"
                            + " AND cp.id = p.id AND tp.id = p.id AND iacp.id = p.id "
                            + " AND tp.name= :typeName "
                            + " AND iacp.name= :internalAccessConditions"
                            + " AND tp.value = :type3 "
                            + " AND iacp.value != :closed "
                            + " AND cp.name = :collection "
                            + " AND p.name = :name " + compareStatement
                            + " AND cp.value = :value "
                            + "UNION \n"
                            + "SELECT DISTINCT p.id  \n"
                            + "FROM property p"
                            + ", property cp, property tp, property iacp \n"
                            + "WHERE p.txn_end = 0 AND cp.txn_end = 0 AND tp.txn_end=0 AND iacp.txn_end=0"
                            + " AND cp.id = p.id AND tp.id = p.id AND iacp.id = p.id "
                            + " AND tp.name= :typeName "
                            + " AND iacp.name= :internalAccessConditions"
                            + " AND tp.value = :type4 "
                            + " AND iacp.value != :closed "
                            + " AND cp.name = :collection "
                            + " AND p.name = :name " + compareStatement
                            + " AND cp.value = :value "
                            );

            q.bind("expiry", datetime);
            q.bind("name", "expiryDate");
            q.bind("internalAccessConditions", "internalAccessConditions");
            q.bind("closed", AmberProperty.encode("Closed"));
            q.bind("typeName", "type");
            q.bind("type1", AmberProperty.encode("Work"));
            q.bind("type2", AmberProperty.encode("EADWork"));
            q.bind("type3", AmberProperty.encode("Section"));
            q.bind("type4", AmberProperty.encode("Page"));
            q.bind("collection", "collection");
            q.bind("value", AmberProperty.encode(collectionName));
            q.execute();
            h.commit();

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
