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

    
    public List<Vertex> generateDuplicateAliasReport(String collectionName) {

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.execute("DROP " + graph.getTempTableDrop() + " TABLE IF EXISTS vp; CREATE TEMPORARY TABLE vp (id BIGINT) " + graph.getTempTableEngine() + ";");
            Update q = h.createStatement(
                    "INSERT INTO vp (id) \n"
                            + " SELECT DISTINCT id \n"    
                            + " FROM work \n"              
                            + " WHERE \n"                 
                            + " type in ( 'Work', 'EADWork', 'Section', 'Page') \n"
                            + " AND collection = :collection \n"
                            + " AND alias is not null \n"  
                            + " UNION ALL \n"             
                            + " SELECT DISTINCT p.id \n"  
                            + " FROM work p, work cp, flatedge ed \n"
                            + " WHERE \n"                 
                            + " p.type = 'Copy' \n"        
                            + " AND cp.collection = :collection \n"
                            + " AND p.alias is not null"
                            + " AND p.id = ed.v_out"    
                            + " AND ed.v_in=cp.id "     
            );

            q.bind("collection", collectionName);
            q.execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h, "vp", "id");
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
                            + " SELECT DISTINCT p.id  \n"
                            + " FROM work p \n"
                            + " WHERE p.type in ('Work', 'EADWork', 'Section', 'Page') \n"
                            + " AND internalAccessConditions != 'Closed'  \n"
                            + " AND p.collection = :collection \n"
                            + " AND p.expiryDate = :expiry \n");

            q.bind("expiry", datetime);
            q.bind("collection", collectionName);
            q.execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h,
                    "er", "id");
            vertices = getVertices(h, graph, propMaps, "er", "id", "id");

        }
        return vertices;
    }

    public static long getTimeMillis(java.sql.Timestamp ts) {
        return ts.getTime();
    }
    
  
}
