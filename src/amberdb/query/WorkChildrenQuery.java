package amberdb.query;

import amberdb.AmberSession;
import amberdb.enums.BibLevel;
import amberdb.enums.CopyRole;
import amberdb.enums.SubType;
import amberdb.graph.AmberProperty;
import amberdb.graph.AmberQueryBase;
import amberdb.graph.DataType;
import amberdb.model.Section;
import amberdb.model.Work;

import java.util.*;

import amberdb.model.sort.WorkComparator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.ByteArrayMapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import com.tinkerpop.blueprints.Vertex;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.util.LongMapper;

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
    
    public List<Work> getChildRangeSorted(Long workId, int start, int num, String sortPropertyName, boolean sortForward) {
        if (StringUtils.isBlank(sortPropertyName)){
            return getChildRange(workId, start, num);
        }
        List<Work> children = getChildren(getAddChildrenWorkSortBySql(workId, start, num, sortPropertyName, sortForward));
        Collections.sort(children, new WorkComparator(sortPropertyName, sortForward));    
        return children;
    }
    
    public List<Work> getChildRange(Long workId, int start, int num){
        return getChildren(getAddChildrenWorksSql(workId, start, num));
    }
    
    private List<Work> getChildren(String addChildrenWorksSql) {

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
        s.append(addChildrenWorksSql);
        
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

    private String getAddChildrenWorksSql(Long workId, int start, int num) {
        return "INSERT INTO v1 (id, obj_type, ord) \n" +
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
        " LIMIT "+start+","+num+"; \n";
    }
    
    private String getAddChildrenWorkSortBySql(Long workId, int start, int num, String sortBy, boolean asc) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO v1 (id, obj_type, ord) "
                + " SELECT DISTINCT v.id, 'W' obj_type, e.edge_order FROM vertex v "
                + " INNER JOIN property p1 "
                + " on v.txn_end = 0 AND p1.txn_end = 0 AND p1.id = v.id AND p1.name = 'type' AND p1.value IN " + workNotSectionInList
                + " INNER JOIN edge e on e.txn_end = 0 AND e.v_out = v.id AND e.label = 'isPartOf' "
                + " LEFT JOIN property p2 "
                + " on p2.txn_end = 0 and p2.id = v.id AND p2.name = '"+sortBy+"' "
                + " and p2.value != '[]' " //empty list treated as null (e.g. when sorting by alias)
                + " WHERE e.v_in = "+workId+" ORDER BY ISNULL(p2.value), p2.value "); //null always last whether asc or desc
        if (!asc){
            sb.append(" desc ");
        }
        sb.append(" LIMIT "+start+","+num+"; ");
        return sb.toString();
    }

    public List<Section> getSections(Long workId) {

        StringBuilder s = new StringBuilder();
        List<Section> sections =  new ArrayList<>();
        String tDrop = graph.getTempTableDrop();
        String tEngine = graph.getTempTableEngine();

        s.append(
            "DROP " + tDrop + " TABLE IF EXISTS v1; \n" +
            "CREATE TEMPORARY TABLE v1 (id BIGINT, obj_type CHAR(1), ord BIGINT)" + tEngine + "; \n");

        // add children Sections
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
            " AND p.value = '" + Hex.encodeHexString(AmberProperty.encode("Section")) + "'\n" +
            " ORDER BY e.edge_order; \n");

        List<Vertex> vertices = null;
        try (Handle h = graph.dbi().open()) {
            h.begin();
            h.createStatement(s.toString()).execute();
            h.commit();

            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "v1", "id");
            vertices = getVertices(h, graph, propMaps, "v1", "id", "ord");

            for (Vertex v : vertices) {
                sections.add(sess.getGraph().frame(v, Section.class));
            }
        }
        return sections;
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
    
    public List<CopyRole> getAllChildCopyRoles(Long workId) {
        List<byte[]> copyRoleCodes = new ArrayList<>();
        try (Handle h = graph.dbi().open()) {
            copyRoleCodes = h.createQuery(
                    "SELECT DISTINCT p2.value \n" + 
                    "FROM vertex v, edge e, edge e2, property p, property p2 \n" +
                    "WHERE v.txn_end = 0 AND e.txn_end = 0 AND e2.txn_end = 0 AND p.txn_end = 0 AND p2.txn_end = 0 \n" +
                    " AND e.v_in = :workId \n" +
                    " AND e.v_out = v.id \n" +
                    " AND e.label = 'isPartOf' \n" +
                    " AND p.id = v.id \n" +
                    " AND p.name = 'type' \n" + 
                    " AND p.value IN (:workVal, :pageVal, :eadWorkVal)" +
                    " AND e2.v_in = p.id \n" +
                    " AND e2.v_out = p2.id \n" +
                    " AND e2.label = 'isCopyOf' \n" +
                    " AND p2.name = 'copyRole'; \n")
                    .bind("workId", workId)
                    .bind("workVal", AmberProperty.encode("Work"))
                    .bind("pageVal", AmberProperty.encode("Page"))
                    .bind("eadWorkVal", AmberProperty.encode("EADWork"))
                    .map(ByteArrayMapper.FIRST).list();
        }

        List<CopyRole> copyRoles = new ArrayList<CopyRole>();
        for (byte[] bytes : copyRoleCodes) {
            String code = (String) AmberProperty.decode(bytes, DataType.STR);
            copyRoles.add(CopyRole.fromString(code));
        }
        return copyRoles;
    }

    public List<CopyRole> getAllCopyRoles(List<Long> workIds){
        List<CopyRole> copyRoles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workIds)){
            List<byte[]> copyRoleCodes;
            try (Handle h = graph.dbi().open()) {
                copyRoleCodes = h.createQuery(
                        "select distinct convert(value using utf8) " +
                                "from property p, edge e, vertex v " +
                                "where p.txn_end = 0 and e.txn_end = 0 and v.txn_end = 0 " +
                                "and p.id = e.v_out and v.id = p.id " +
                                "and e.label = 'isCopyOf' and p.name = 'copyRole' and e.v_in in (" + Joiner.on(",").join(workIds) + ")")
                        .map(ByteArrayMapper.FIRST).list();
            }
            for (byte[] bytes : copyRoleCodes) {
                String code = (String) AmberProperty.decode(bytes, DataType.STR);
                CopyRole copyRole = CopyRole.fromString(code);
                if (copyRole != null){
                    copyRoles.add(copyRole);
                }
            }
        }
        return copyRoles;
    }

    /**
     * Retrieve children Ids of a list of parent Ids by the specified bib level and specified sub type
     * @param parentIds a list of parent Ids
     * @param bibLevel bibLevel of the child
     * @param subType subtype of the child, can be null
     * @return a list of children Ids with the specified bib level and specified sub type
     */
    public List<Long> getChildrenIdsByBibLevelSubType(List<Long> parentIds, BibLevel bibLevel, List<String> subTypes){
        if (CollectionUtils.isNotEmpty(parentIds)){
            try (Handle h = graph.dbi().open()) {
                Query query = getChildrenIdsByBibLevelSubTypeQuery(h, parentIds, bibLevel, subTypes);
                return query.list();
            }
        }
        return new ArrayList<>();
    }

    private Query getChildrenIdsByBibLevelSubTypeQuery(Handle h, List<Long> parentIds, BibLevel bibLevel, List<String> subTypes) {
        if (CollectionUtils.isNotEmpty(subTypes)){
            Function<String,String> addQuotes = new Function<String,String>() {
                @Override public String apply(String s) {
                    return new StringBuilder(s.length()+2).append("'").append(s).append("'").toString();
                }
            };
            String subTypeStr = Joiner.on(", ").join(Iterables.transform(subTypes, addQuotes));
            return h.createQuery(
                    "select distinct v.id from property p1, property p2, edge e, vertex v " +
                            "where p1.txn_end = 0 and e.txn_end = 0 and v.txn_end = 0 and p2.txn_end = 0 " +
                            "and p1.id = e.v_out and p2.id = e.v_out and v.id = p1.id and v.id = p2.id " +
                            "and e.label = 'isPartOf' " +
                            "and p1.name = 'bibLevel' and p1.value = :bibLevel " +
                            "and p2.name = 'subType' and p2.value in ("+ subTypeStr + ") " +
                            "and e.v_in in (" + Joiner.on(",").join(parentIds) + ")")
                    .bind("bibLevel", bibLevel.code())
                    .map(LongMapper.FIRST);
        }
        return h.createQuery(
                "select distinct v.id from property p1, edge e, vertex v " +
                        "where p1.txn_end = 0 and e.txn_end = 0 and v.txn_end = 0 " +
                        "and p1.id = e.v_out and v.id = p1.id " +
                        "and e.label = 'isPartOf' " +
                        "and p1.name = 'bibLevel' and p1.value = :bibLevel " +
                        "and e.v_in in (" + Joiner.on(",").join(parentIds) + ")")
                .bind("bibLevel", bibLevel.code())
                .map(LongMapper.FIRST);
    }
}
