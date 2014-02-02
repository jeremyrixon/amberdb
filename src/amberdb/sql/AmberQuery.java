package amberdb.sql;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberQuery {

    
    List<Long> head;       
    List<QueryClause> clauses = new ArrayList<QueryClause>();
    private AmberGraph graph;

    protected AmberQuery(Long head, AmberGraph graph) {

        // guard
        if (head == null) throw new IllegalArgumentException("Query must have starting vertices");
        
        this.head = new ArrayList<Long>();
        this.head.add(head);
        this.graph = graph;
    }

    
    protected AmberQuery(List<Long> head, AmberGraph graph) {
        
        // guards
        if (head == null) throw new IllegalArgumentException("Query must have starting vertices");
        head.removeAll(Collections.singleton(null));
        if (head.size() == 0) throw new IllegalArgumentException("Query must have starting vertices");            
        
        this.head = head;
        this.graph = graph;
    }

    
    public AmberQuery branch(List<String> labels, Direction direction) {
         clauses.add(new QueryClause(labels, direction));
        return this;
    }
    
    
    class QueryClause {
        List<String> labels;
        Direction direction;
        
        QueryClause(List<String> labels, Direction direction) {
            this.labels = labels;
            this.direction = direction;
        }
    }
    
    // note: still need to add edge-order
    protected String generateFullSubGraphQuery() {

        int step = 0;
        
        StringBuilder s = new StringBuilder();
        s.append("DROP TABLE IF EXISTS v0;\n");
        s.append("DROP TABLE IF EXISTS v1;\n");
        
        s.append("CREATE TEMPORARY TABLE v0 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "ev_in BIGINT, "
                + "ev_out BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT);\n");
        
        s.append("CREATE TEMPORARY TABLE v1 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "ev_in BIGINT, "
                + "ev_out BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT);\n");
        
        // inject head
        s.append(String.format(

        "INSERT INTO v0 (step, vid, eid, ev_in, ev_out, label, edge_order) \n"
        + "SELECT 0, id, 0, id, id, 'root', 0 \n"
        + "FROM vertex \n"
        + "WHERE id IN (%s) \n"
        + "AND txn_end = 0; \n",
        
        LongList2Str(head)));
        
        // add the clauses
        for (QueryClause qc : clauses) {
            
            step++;
            String thisTable = "v" + ( step    % 2);
            String thatTable = "v" + ((step+1) % 2);
            
            String labelsClause = generatelabelsClause(qc.labels);
            String directionClause = generateDirClause(qc.direction, thatTable);

            s.append(String.format(

            "INSERT INTO %1$s (step, vid, eid, ev_in, ev_out, label, edge_order) \n"
            + "SELECT %3$d, v.id, e.id, e.v_in, e.v_out, e.label, e.edge_order  \n"
            + "FROM vertex v, edge e, %2$s \n"
            + "WHERE e.txn_end = 0 \n"
            + " AND v.txn_end = 0 \n"
            + labelsClause
            + directionClause
            + " AND " + thatTable + ".step = " + (step-1) + " ;\n",
            
            thisTable, thatTable, step));
        }

        // result consolidation
        s.append("INSERT INTO v0 (step, vid, eid, ev_in, ev_out, label, edge_order) "
                + "SELECT step, vid, eid, ev_in, ev_out, label, edge_order FROM v1;\n");
        
        return s.toString();
        // Draw from v0 for results
    }

    
    private String LongList2Str(List<Long> longs) {
        StringBuilder s = new StringBuilder();
        for (Long l : longs) {
            s.append(l).append(',');
        }
        s.setLength(s.length()-1);
        return s.toString();
    }


    private String StrList2Str(List<String> strs) {
        StringBuilder s = new StringBuilder();
        for (String str : strs) {
            // dumbass sql injection protection (not real great)
            str.replaceAll("'", "\\'"); 
            s.append("'" + str + "',");
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    private String generatelabelsClause(List<String> labels) {
        if (labels == null || labels.size() == 0) return "";
        return " AND e.label IN (" + StrList2Str(labels) + ") \n"; 
    }
    
    
    private String generateDirClause(Direction direction, String thatTable) {
        
        String inClause  = "";
        String outClause = "";
        
        if (direction == Direction.BOTH || direction == Direction.IN) {
            inClause  = "(e.v_out = v.id AND e.v_in = " + thatTable    + ".vid)";
        }
        if (direction == Direction.BOTH || direction == Direction.OUT) {
            outClause = "(e.v_in = v.id AND e.v_out = " + thatTable    + ".vid)";
        }
        
        if (direction == Direction.BOTH) {
            return " AND (" + inClause + " OR " + outClause + ") \n";
        }
        return " AND " + inClause + outClause + " \n";
    }

    
    public List<Vertex> execute() {

        Handle h = graph.dbi().open();

        // run the generated query
        h.begin();
        h.createStatement(generateFullSubGraphQuery()).execute();
        h.commit();
        
        // and reap the rewards
        Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h);
        List<Vertex> vertices = getVertices(h, graph, propMaps);
        getEdges(h, graph, propMaps);
        
        h.close();
        
        return vertices;
    }
    
    
    private Map<Long, Map<String, Object>> getElementPropertyMaps(Handle h) {
        
        List<AmberProperty> propList = h.createQuery(
                "SELECT p.id, p.name, p.type, p.value "
                + "FROM property p, v0 " 
                + "WHERE p.id = v0.vid "
                + "AND p.txn_end = 0")
                .map(new PropertyMapper()).list();

        Map<Long, Map<String, Object>> propertyMaps = new HashMap<Long, Map<String, Object>>();
        for (AmberProperty prop : propList) {
            Long id = prop.getId();
            if (propertyMaps.get(id) == null) {
                propertyMaps.put(id, new HashMap<String, Object>());
            }
            propertyMaps.get(id).put(prop.getName(), prop.getValue());
        }
        return propertyMaps;
    }
    
    
    private List<Vertex> getVertices(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<Vertex> vertices = new ArrayList<Vertex>();
        
        List<AmberVertexWithState> wrappedVertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM vertex v, v0 "
                + "WHERE v.id = v0.vid "
                + "AND v.txn_end = 0 "
                + "ORDER BY v0.edge_order")
                .map(new VertexMapper(graph)).list();

        // add them to the graph
        for (AmberVertexWithState wrapper : wrappedVertices) {
            AmberVertex vertex = wrapper.vertex; 

            if (graph.removedVertices.contains(vertex)) {
                continue;
            }
            if (!graph.modifiedVertices.contains(vertex)) {
                vertex.replaceProperties(propMaps.get((Long) vertex.getId()));
                graph.addVertexToGraph(vertex);
            }
            vertices.add(vertex);
        } 
        
        return vertices;
    }
    
    
    private void getEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {
        
        List<AmberEdgeWithState> wrappedEdges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, v0 "
                + "WHERE e.id = v0.eid "
                + "AND e.txn_end = 0")
                .map(new EdgeMapper(graph, true)).list();
        
        // add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            AmberEdge edge = wrapper.edge; 

            if (graph.removedEdges.contains(edge) || graph.modifiedEdges.contains(edge)) {
                continue;
            } 
            edge.replaceProperties(propMaps.get((Long) edge.getId()));
            graph.addEdgeToGraph(edge);
        }        
    }
}
