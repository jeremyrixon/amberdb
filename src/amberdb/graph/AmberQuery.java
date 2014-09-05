package amberdb.graph;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;


public class AmberQuery {

    /** starting vertex ids */
    List<Long> head;
    
    /**
     * ordered set of clauses indicating which edges to follow to construct a
     * result sub-graph. The first clause follows edges from the head vertices
     */
    List<QueryClause> clauses = new ArrayList<QueryClause>();

    /**
     * The graph associated with this query
     */
    private AmberGraph graph;


    /**
     * Create a query with single starting vertices. As branches are added the
     * query will return the subgraph covered by traversing the branches
     * specified in order.
     * 
     * @param head
     *            The starting vertex
     * @param graph
     */
    protected AmberQuery(Long head, AmberGraph graph) {
        // guard
        if (head == null) throw new IllegalArgumentException("Query must have starting vertices");
        
        this.head = new ArrayList<Long>();
        this.head.add(head);
        this.graph = graph;
    }


    /**
     * Create a query with a collection of starting vertices. As branches are
     * added the query will return the subgraph covered by traversing the
     * branches specified in order.
     * 
     * @param head
     *            The collection of starting vertices
     * @param graph
     */
    protected AmberQuery(List<Long> head, AmberGraph graph) {
        // guards
        if (head == null)
            throw new IllegalArgumentException("Query must have starting vertices");
        head.removeAll(Collections.singleton(null));
        if (head.size() == 0)
            throw new IllegalArgumentException("Query must have starting vertices");

        this.head = head;
        this.graph = graph;
    }
    
    
    public AmberQuery branch(List<String> labels, Direction direction) {
        clauses.add(new QueryClause(BranchType.BRANCH_FROM_PREVIOUS, labels, direction, null));
        return this;
    }
    

    public AmberQuery branch(BranchType branchType, List<String> labels, Direction direction, List<Integer> branchFrom) {
        clauses.add(new QueryClause(branchType, labels, direction, branchFrom));
        return this;
    }
    
    
    public AmberQuery branch(BranchType branchType, List<String> labels, Direction direction) {
        clauses.add(new QueryClause(branchType, labels, direction, null));
        return this;
    }
    
    
    public AmberQuery branch(String[] labels, Direction direction) {
        clauses.add(new QueryClause(BranchType.BRANCH_FROM_PREVIOUS, Arrays.asList(labels), direction, null));
        return this;
    }
    

    public AmberQuery branch(BranchType branchType, String[] labels, Direction direction, Integer[] branchFrom) {
        clauses.add(new QueryClause(branchType, Arrays.asList(labels), direction, Arrays.asList(branchFrom)));
        return this;
    }
    
    
    public AmberQuery branch(BranchType branchType, String[] labels, Direction direction) {
        clauses.add(new QueryClause(branchType, Arrays.asList(labels), direction, null));
        return this;
    }
    
    
    class QueryClause {
        
        List<String> labels;
        Direction direction;
        List<Integer> branchList;
        BranchType branchType;
        
        QueryClause(BranchType branchType, List<String> labels, Direction direction, List<Integer> branchList) {
            this.branchType = branchType;
            this.labels = labels;
            this.direction = direction;
            this.branchList = branchList;
        }
    }

    
    // note: still need to add edge-order
    protected String generateFullSubGraphQuery() {

        int step = 0;
        
        StringBuilder s = new StringBuilder();
        s.append("DROP TABLE IF EXISTS v0;\n");
        s.append("CREATE TEMPORARY TABLE v0 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT);\n");
        
        // inject head
        s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT 0, id, 0, 'root', 0 \n"
                + "FROM vertex \n"
                + "WHERE id IN (%s) \n"
                + "AND txn_end = 0; \n",
                numberListToStr(head)));
        
        // add the clauses
        for (QueryClause qc : clauses) {
            
            step++;
            
            String labelsClause = generateLabelsClause(qc.labels);
            
            if (qc.direction == Direction.BOTH || qc.direction == Direction.IN) {
                s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT %1$d, v.id, e.id, e.label, e.edge_order  \n"
                + "FROM vertex v, edge e, v0 \n"
                + "WHERE e.txn_end = 0 \n"
                + " AND v.txn_end = 0 \n"
                + labelsClause
                + " AND (e.v_out = v.id AND e.v_in = v0.vid)\n",
                step));

                s.append(generateBranchClause(qc, step));
                s.append(";\n");
            }
            
            if (qc.direction == Direction.BOTH || qc.direction == Direction.OUT) {
                s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT %1$d, v.id, e.id, e.label, e.edge_order  \n"
                + "FROM vertex v, edge e, v0 \n"
                + "WHERE e.txn_end = 0 \n"
                + " AND v.txn_end = 0 \n"
                + labelsClause
                + " AND (e.v_in = v.id AND e.v_out = v0.vid) \n",
                step));

                s.append(generateBranchClause(qc, step));
                s.append(";\n");
            }
        }

        return s.toString();
        // Draw from v0 for results
    }

    
    private String generateBranchClause(QueryClause qc, int step) {
        String clause;
        switch (qc.branchType) {
        case BRANCH_FROM_PREVIOUS:
            clause = " AND v0.step = " + (step-1) + " \n";
            break;
        case BRANCH_FROM_LISTED:
            clause = " AND v0.step IN (" + numberListToStr(qc.branchList) + ") \n";
            break;
        case BRANCH_FROM_UNLISTED:    
            clause = " AND v0.step NOT IN (" + numberListToStr(qc.branchList) + ") \n";
            break;
        case BRANCH_FROM_ALL:
        default:    
            clause = "";
        }
        return clause;
    }
    
    
    private <T> String numberListToStr(List<T> numbers) {
        StringBuilder s = new StringBuilder();
        for (T n : numbers) {
            s.append(n).append(',');
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    private String strListToStr(List<String> strs) {
        StringBuilder s = new StringBuilder();
        for (String str : strs) {
            // dumbass sql injection protection (not real great)
            s.append("'" + str.replaceAll("'", "\\'") + "',");
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    private String generateLabelsClause(List<String> labels) {
        if (labels == null || labels.size() == 0) return "";
        return " AND e.label IN (" + strListToStr(labels) + ") \n"; 
    }
    

    /**
     * Execute the query
     * 
     * @return A list of the vertices found during the query. The significant
     *         side effect of execution is that the result subgraph is pulled
     *         into memory including the edges traversed to create the result.
     */
    public List<Vertex> execute() {
        return execute(false);
    }
    
    
    /**
     * Execute the query
     * 
     * @param fillEdges
     *            Fill in internal edges of the resultant sub-graph regardless
     *            of whether they were traversed to create the sub-graph. If
     *            false, only traversed edges are returned.
     * @return A list of the vertices found during the query. The significant
     *         side effect of execution is that the result subgraph is pulled
     *         into memory including edges based on the fillEdges setting.
     */
    public List<Vertex> execute(boolean fillEdges) {

        List<Vertex> vertices;
        try (Handle h = graph.dbi().open()) {

            // run the generated query
            h.begin();
            h.createStatement(generateFullSubGraphQuery()).execute();
            h.commit();

            /*
             * ... and reap the rewards
             * 
             * IMPORTANT NOTE: Currently only Vertex properties are retrieved -
             * not edges. This is ok at the moment because we don't currently
             * populate edge properties.
             */
            Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h);
            vertices = getVertices(h, graph, propMaps);
            
            if (fillEdges) {
                getFillEdges(h, graph, propMaps);
            } else {
                getEdges(h, graph, propMaps);
            }
        }
        return vertices;
    }
    
    
    private Map<Long, Map<String, Object>> getVertexPropertyMaps(Handle h) {
        
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
            if (graph.graphVertices.containsKey(vertex.getId())) {
                vertices.add(graph.graphVertices.get(vertex.getId()));
            } else {
                vertex.replaceProperties(propMaps.get((Long) vertex.getId()));
                graph.addVertexToGraph(vertex);
                vertices.add(vertex);
            }
        } 
        
        return vertices;
    }
    
    
    private void getEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<AmberEdgeWithState> wrappedEdges;
        wrappedEdges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, v0 "
                + "WHERE e.id = v0.eid "
                + "AND e.txn_end = 0")
                .map(new EdgeMapper(graph, true)).list();
        
        // add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            if (wrapper == null) { // if either vertex doesn't exist 
                continue;
            }
            AmberEdge edge = wrapper.edge; 
            
            if (graph.graphEdges.containsKey(edge.getId())) {
                continue;
            } 
            edge.replaceProperties(propMaps.get((Long) edge.getId()));
            graph.addEdgeToGraph(edge);
        }        
    }

    
    private void getFillEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<AmberEdgeWithState> wrappedEdges;
            
        wrappedEdges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, v0 vin, v0 vout "
                + "WHERE e.v_in = vin.vid "
                + "AND e.v_out = vout.vid "
                + "AND e.txn_end = 0")
                .map(new EdgeMapper(graph, true)).list();
        
        
        // add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            if (wrapper == null) { // if either vertex doesn't exist 
                continue;
            }
            AmberEdge edge = wrapper.edge; 
            
            if (graph.graphEdges.containsKey(edge.getId())) {
                continue;
            } 
            edge.replaceProperties(propMaps.get((Long) edge.getId()));
            graph.addEdgeToGraph(edge);
        }        
    }
}
