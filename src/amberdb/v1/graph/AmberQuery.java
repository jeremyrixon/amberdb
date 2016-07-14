package amberdb.v1.graph;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;


public class AmberQuery extends AmberQueryBase {

    /** starting vertex ids */
    List<Long> head;
    
    /**
     * ordered set of clauses indicating which edges to follow to construct a
     * result sub-graph. The first clause follows edges from the head vertices
     */
    List<QueryClause> clauses = new ArrayList<QueryClause>();


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
        super(graph);
        
        // guard
        if (head == null) throw new IllegalArgumentException("Query must have starting vertices");
        
        this.head = new ArrayList<Long>();
        this.head.add(head);
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
        super(graph);
        
        // guards
        if (head == null)
            throw new IllegalArgumentException("Query must have starting vertices");
        head.removeAll(Collections.singleton(null));
        if (head.size() == 0)
            throw new IllegalArgumentException("Query must have starting vertices");

        this.head = head;
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
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v0;\n");
        s.append("CREATE TEMPORARY TABLE v0 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT) " + graph.tempTableEngine + ";\n");

        // double buffer table to get around mysql limitation
        // of not being able to open the same temporary table
        // more than once in a query
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v1;\n");
        s.append("CREATE TEMPORARY TABLE v1 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT) " + graph.tempTableEngine + ";\n");

        
        // inject head
        s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT 0, id, 0, 'root', 0 \n"
                + "FROM vertex \n"
                + "WHERE id IN (%s) \n"
                + "AND txn_end = 0; \n",
                numberListToStr(head)));
        
        s.append("INSERT INTO v1 (step, vid, eid, label, edge_order) \n"
                + "SELECT step, vid, eid, label, edge_order \n"
                + "FROM v0; \n");
        
        // add the clauses
        for (QueryClause qc : clauses) {
            
            step++;
            
            String labelsClause = generateLabelsClause(qc.labels);
            
            if (qc.direction == Direction.BOTH || qc.direction == Direction.IN) {
                s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT %1$d, v.id, e.id, e.label, e.edge_order  \n"
                + "FROM vertex v, edge e, v1 \n"
                + "WHERE e.txn_end = 0 \n"
                + " AND v.txn_end = 0 \n"
                + labelsClause
                + " AND (e.v_out = v.id AND e.v_in = v1.vid)\n",
                step));

                s.append(generateBranchClause(qc, step));
                s.append(";\n");
            }
            
            if (qc.direction == Direction.BOTH || qc.direction == Direction.OUT) {
                s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT %1$d, v.id, e.id, e.label, e.edge_order  \n"
                + "FROM vertex v, edge e, v1 \n"
                + "WHERE e.txn_end = 0 \n"
                + " AND v.txn_end = 0 \n"
                + labelsClause
                + " AND (e.v_in = v.id AND e.v_out = v1.vid) \n",
                step));

                s.append(generateBranchClause(qc, step));
                s.append(";\n");
            }

            // copy results to v1
            s.append(String.format(
            "INSERT INTO v1 (step, vid, eid, label, edge_order) \n"
            + "SELECT step, vid, eid, label, edge_order  \n"
            + "FROM v0 \n"
            + "WHERE v0.step = %1$d ;\n",
            step));
        }

        return s.toString();
        // Draw from v1 for results
    }

    
    private String generateBranchClause(QueryClause qc, int step) {
        String clause;
        switch (qc.branchType) {
        case BRANCH_FROM_PREVIOUS:
            clause = " AND v1.step = " + (step-1) + " \n";
            break;
        case BRANCH_FROM_LISTED:
            clause = " AND v1.step IN (" + numberListToStr(qc.branchList) + ") \n";
            break;
        case BRANCH_FROM_UNLISTED:    
            clause = " AND v1.step NOT IN (" + numberListToStr(qc.branchList) + ") \n";
            break;
        case BRANCH_FROM_ALL:
        default:    
            clause = "";
        }
        return clause;
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

            Map<Long, Map<String, Object>> propMaps = getElementPropertyMaps(h, "v0", "vid");
            vertices = getVertices(h, graph, propMaps, "v0", "vid", "edge_order");

            // Warning: Filled edge properties won't all be populated 
            propMaps = getElementPropertyMaps(h, "v0", "eid");
            if (fillEdges) {
                getFillEdges(h, graph, propMaps, "v0", "vid", "v1", "vid");
            } else {
                getEdges(h, graph, propMaps, "v0", "eid");
            }
        }
        return vertices;
    }
}
