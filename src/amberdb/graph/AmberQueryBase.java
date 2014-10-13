package amberdb.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Vertex;


public class AmberQueryBase {

    /* Used for getVertices and getVertexPropertyMaps */ 
    protected String VERTEX_ID_TABLE = "v0";
    protected String VERTEX_ID_COLUMN = "vid";
    protected String VERTEX_ORDER_COLUMN = "edge_order";
    
    /* Used for getEdges */
    protected String EDGE_ID_TABLE = "v0";
    protected String EDGE_ID_COLUMN = "eid";

    /* Used for getFillEdges */
    protected String FILL_IN_VERTEX_TABLE = "v0";
    protected String FILL_IN_VERTEX_COLUMN = "vid";
    protected String FILL_OUT_VERTEX_TABLE = "v1";
    protected String FILL_OUT_VERTEX_COLUMN = "vid";
    
    /** The graph associated with this query */
    protected AmberGraph graph;
    
    
    public AmberQueryBase(AmberGraph graph) {
        this.graph = graph;
    }
    
    
    protected Map<Long, Map<String, Object>> getVertexPropertyMaps(Handle h) {
        
        List<AmberProperty> propList = h.createQuery(
                "SELECT p.id, p.name, p.type, p.value "
                + "FROM property p, " + VERTEX_ID_TABLE + " " 
                + "WHERE p.id = " + VERTEX_ID_TABLE + "." + VERTEX_ID_COLUMN + " "
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
    
    
    protected List<Vertex> getVertices(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<Vertex> vertices = new ArrayList<Vertex>();
        List<AmberVertexWithState> wrappedVertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM vertex v, " + VERTEX_ID_TABLE + " "
                + "WHERE v.id = " + VERTEX_ID_TABLE + "." + VERTEX_ID_COLUMN + " "
                + "AND v.txn_end = 0 "
                + "ORDER BY " + VERTEX_ID_TABLE + "." + VERTEX_ORDER_COLUMN)
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
    

    protected void getEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<AmberEdgeWithState> wrappedEdges;
        wrappedEdges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, " + EDGE_ID_TABLE + " "
                + "WHERE e.id = " + EDGE_ID_TABLE + "." + EDGE_ID_COLUMN + " "
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
            //edge.replaceProperties(propMaps.get((Long) edge.getId()));
            graph.addEdgeToGraph(edge);
        }        
    }

    
    protected void getFillEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps) {

        List<AmberEdgeWithState> wrappedEdges;
        wrappedEdges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, " + FILL_IN_VERTEX_TABLE + ", " + FILL_OUT_VERTEX_TABLE + " "
                + "WHERE e.v_in = " + FILL_IN_VERTEX_TABLE + "." + FILL_IN_VERTEX_COLUMN + " "
                + "AND e.v_out = " + FILL_OUT_VERTEX_TABLE + "." + FILL_OUT_VERTEX_COLUMN + " "
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
            //edge.replaceProperties(propMaps.get((Long) edge.getId()));
            graph.addEdgeToGraph(edge);
        }        
    }
}
