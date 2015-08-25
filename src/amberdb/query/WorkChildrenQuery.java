package amberdb.query;

import amberdb.AmberSession;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberQueryBase;
import amberdb.model.Work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.IntegerMapper;

import com.tinkerpop.blueprints.Vertex;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

public class WorkChildrenQuery extends AmberQueryBase {

    AmberSession sess;
    
    public WorkChildrenQuery(AmberSession sess) {
        super(sess.getAmberGraph());
        this.sess = sess;
    }

    /* the following static variables are used to provide the hex encoding for the different vertex
     * types required by the child range query. This is required due to the following limitations:
     *  
     *  -- H2 requires blob strings to be encoded as hex strings (mysql does not).
     *  -- JDBI cannot bind variables in multi-statement queries as used here.
     *  
     *  So to support testing on H2 with the multi-statement child range query used, we use the hex 
     *  strings defined directly below. */
    static Map<String, String> hex = new HashMap<>();
    static String[] types = {
        "Work", "Page", "EADWork", "Section", 
        "Copy", 
        "File", "ImageFile", "SoundFile", 
        "Description", "CameraData", "EADEntity", "EADFeature", "GeoCoding", "IPTC"
    };
    static String workNotSectionInList; 
    static String fileInList;
    static String descInList;
    static {
        for (String t : types) {
            hex.put(t, Hex.encodeHexString(AmberProperty.encode(t)));
        }
        workNotSectionInList = "(X'" + StringUtils.join(new String[] {hex.get("Work"), hex.get("Page"), hex.get("EADWork")}, "', X'") + "')";
        fileInList = "(X'" + StringUtils.join(new String[] {hex.get("File"), hex.get("ImageFile"), hex.get("SoundFile")}, "', X'") + "')";
        descInList = "(X'" + StringUtils.join(new String[] {hex.get("Description"), hex.get("CameraData"), hex.get("EADEntity"), 
                hex.get("EADFeature"), hex.get("GeoCoding"), hex.get("IPTC")}, "', X'") + "')";
    }

    
    public List<Work> getChildRange(Long workId, int start, int num) {

        StringBuilder s = new StringBuilder();
        List<Work> children =  new ArrayList<>();
        String tDrop = graph.getTempTableDrop();
        String tEngine = graph.getTempTableEngine();
        
        // create double buffered temp tables because mysql
        // can't open the same temp table twice in a query
        s.append(
            "DROP " + tDrop + " TABLE IF EXISTS v1; \n" +
            "DROP " + tDrop + " TABLE IF EXISTS v2; \n" +
        	"CREATE TEMPORARY TABLE v1 (id BIGINT, obj_type CHAR(1), ord BIGINT)" + tEngine + "; \n" +
            "CREATE TEMPORARY TABLE v2 (id BIGINT, obj_type CHAR(1), ord BIGINT)" + tEngine + "; \n");
        
        // add children Works excluding Sections with the limits specified on the range returned
        s.append(
            "INSERT INTO v1 (id, obj_type, ord) \n" +
            "SELECT DISTINCT v.id, 'W', e.edge_order \n" +
            "FROM vertex v, edge e, property p \n" +
            "WHERE v.txn_end = 0 AND e.txn_end = 0 AND p.txn_end = 0 \n" +
            " AND e.v_in = "+workId+" \n" +
            " AND e.v_out = v.id \n" +
            " AND e.label = 'isPartOf' \n" +
            " AND p.id = v.id \n" +
            " AND p.name = 'type' \n" + 
            " AND p.value IN " + workNotSectionInList + " \n" +
            " ORDER BY e.edge_order \n" +
            " LIMIT "+start+","+num+"; \n");
        
        // get their copies
        s.append(
            "INSERT INTO v2 (id, obj_type) \n" +
            "SELECT DISTINCT v.id, 'C' \n" +
            "FROM vertex v, edge e, property p, v1 \n" +
            "WHERE v.txn_end = 0 AND e.txn_end = 0 AND p.txn_end = 0 \n" +
            " AND e.v_in = v1.id \n" +
            " AND e.v_out = v.id \n" +
            " AND e.label = 'isCopyOf' \n" +
            " AND p.id = v.id \n" +
            " AND p.name = 'type' \n" + 
            " AND p.value = X'" + hex.get("Copy") + "'; \n");
        
        // get files
        s.append(
            "INSERT INTO v1 (id, obj_type) \n" +
            "SELECT DISTINCT v.id, 'F' \n" +
            "FROM vertex v, edge e, property p, v2 \n" +
            "WHERE v.txn_end = 0 AND e.txn_end = 0 AND p.txn_end = 0 \n" +
            " AND e.v_in = v2.id \n" +
            " AND e.v_out = v.id \n" +
            " AND e.label = 'isFileOf' \n" +
            " AND p.id = v.id \n" +
            " AND p.name = 'type' \n" + 
            " AND p.value IN " + fileInList + "; \n");

        // move everything in v2 to v1
        s.append(
            "INSERT INTO v1 (id, obj_type) \n" +
            "SELECT v2.id, v2.obj_type \n" +
            "FROM v2; \n" +
            "DELETE FROM v2; \n");
 
        // get descriptions
        s.append(
            "INSERT INTO v2 (id, obj_type) \n" +
            "SELECT DISTINCT v.id, 'D' \n" +
            "FROM vertex v, edge e, property p, v1 \n" +
            "WHERE v.txn_end = 0 AND e.txn_end = 0 AND p.txn_end = 0 \n" +
            " AND e.v_in = v1.id \n" +
            " AND e.v_out = v.id \n" +
            " AND e.label = 'descriptionOf' \n" +
            " AND p.id = v.id \n" +
            " AND p.name = 'type' \n" + 
            " AND p.value IN " + descInList + "; \n");

        // finally move all the descriptions into v1 also
        s.append(
            "INSERT INTO v1 (id, obj_type) \n" +
            "SELECT v2.id, v2.obj_type \n" +
            "FROM v2; \n");

        List<Vertex> vertices = null;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.createStatement(s.toString())
                    .bind("workVal", AmberProperty.encode("Work"))
                    .bind("pageVal", AmberProperty.encode("Page"))
                    .bind("eadWorkVal", AmberProperty.encode("EADWork"))
                    .execute();
            h.commit();
            
            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "v1", "id");
            vertices = getVertices(h, graph, propMaps, "v1", "id", "ord");
            
            for (Vertex v : vertices) {
                String type = v.getProperty("type");
                if (type.matches("(Work|Page|EADWork)")) {
                    children.add(sess.getGraph().frame(v, Work.class));
                }
            }
        }
        return children;
    }
    
    
    public Integer getTotalChildCount(Long workId) {
        Integer numChildren = new Integer(0);
        try (Handle h = graph.dbi().open()) {
            numChildren = h.createQuery(
                    "SELECT COUNT(v_out) \n" + 
                    "FROM vertex v, edge e, property p \n" +
                    "WHERE v.txn_end = 0 AND e.txn_end = 0 AND p.txn_end = 0 \n" +
                    " AND e.v_in = :workId \n" +
                    " AND e.v_out = v.id \n" +
                    " AND e.label = 'isPartOf' \n" +
                    " AND p.id = v.id \n" +
                    " AND p.name = 'type' \n" + 
                    " AND p.value IN (:workVal, :pageVal, :eadWorkVal); \n")
                    .bind("workId", workId)
                    .bind("workVal", AmberProperty.encode("Work"))
                    .bind("pageVal", AmberProperty.encode("Page"))
                    .bind("eadWorkVal", AmberProperty.encode("EADWork"))
                    .map(IntegerMapper.FIRST).first();
        }
        return numChildren;
    }
}