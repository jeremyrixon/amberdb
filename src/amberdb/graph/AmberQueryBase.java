package amberdb.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberQueryBase {

    
    /** The graph associated with this query */
    protected AmberGraph graph;
    
    
    public AmberQueryBase(AmberGraph graph) {
        this.graph = graph;
    }
    
    
    protected Map<Long, Map<String, Object>> getElementPropertyMaps(Handle h, String elementIdTable, String elementIdColumn) {
        
        List<AmberProperty> propList = h.createQuery(String.format(
                "SELECT p.id, p.name, p.type, p.value "
                + "FROM property p, %1$s " 
                + "WHERE p.id = %1$s.%2$s "
                + "AND p.txn_end = 0", 
                elementIdTable, elementIdColumn))
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
    
        
    protected List<Vertex> getVertices(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps,
            String vertexIdTable, String vertexIdColumn, String vertexOrderColumn) {

        List<Vertex> vertices = new ArrayList<>();
        List<AmberVertexWithState> wrappedVertices = h.createQuery(String.format(
                "SELECT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM vertex v, %1$s "
                + "WHERE v.id = %1$s.%2$s "
                + "AND v.txn_end = 0 "
                + "ORDER BY %1$s.%3$s", 
                vertexIdTable, vertexIdColumn, vertexOrderColumn))
                .map(new VertexMapper(graph)).list();

        // add them to the graph
        for (AmberVertexWithState wrapper : wrappedVertices) {
            AmberVertex vertex = wrapper.vertex; 

            if (graph.removedVertices.containsKey(vertex.getId())) {
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
    

    protected List<Edge> getEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps, 
            String edgeIdTable, String edgeIdColumn) {

        List<Edge> edges = new ArrayList<>();
        List<AmberEdgeWithState> wrappedEdges = h.createQuery(String.format(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, %1$s "
                + "WHERE e.id = %1$s.%2$s "
                + "AND e.txn_end = 0",
                edgeIdTable, edgeIdColumn))
                .map(new EdgeMapper(graph, true)).list();
        
        // don't add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            if (wrapper == null) { // if either vertex doesn't exist 
                continue;
            }
            AmberEdge edge = wrapper.edge; 
            Long edgeId = (Long) edge.getId();
            
            if (graph.graphEdges.containsKey(edgeId) || graph.removedEdges.containsKey(edgeId)) {
                continue;
            } 
            edge.replaceProperties(propMaps.get(edgeId));
            graph.addEdgeToGraph(edge);
            edges.add(edge);
        }        
        return edges;
    }

    
    protected void getFillEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps,
            String inVertexIdTable, String inVertexIdColumn, String outVertexIdTable, String outVertexIdColumn) {

        List<AmberEdgeWithState> wrappedEdges = h.createQuery(String.format(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + "FROM edge e, %1$s, %3$s "
                + "WHERE e.v_in = %1$s.%2$s "
                + "AND e.v_out = %3$s.%4$s "
                + "AND e.txn_end = 0",
                inVertexIdTable, inVertexIdColumn, outVertexIdTable, outVertexIdColumn))
                .map(new EdgeMapper(graph, true)).list();
        
        // add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            if (wrapper == null) { // if either vertex doesn't exist 
                continue;
            }
            AmberEdge edge = wrapper.edge; 
            Long edgeId = (Long) edge.getId();
            
            if (graph.graphEdges.containsKey(edgeId) || graph.removedEdges.containsKey(edgeId)) {
                continue;
            } 
            edge.replaceProperties(propMaps.get(edgeId));
            graph.addEdgeToGraph(edge);
        }        
    }
    
    
    protected <T> String numberListToStr(List<T> numbers) {
        StringBuilder s = new StringBuilder();
        for (T n : numbers) {
            s.append(n).append(',');
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    protected String strListToStr(List<String> strs) {
        StringBuilder s = new StringBuilder();
        for (String str : strs) {
            // dumbass sql injection protection (not real great)
            s.append("'" + str.replaceAll("'", "\\'") + "',");
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    protected String generateLabelsClause(List<String> labels) {
        if (labels == null || labels.size() == 0) return "";
        return " AND e.label IN (" + strListToStr(labels) + ") \n"; 
    }
}
