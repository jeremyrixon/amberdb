package amberdb.sql;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberVertex extends BaseVertex {

	
	Long txnStart;
	Long txnEnd;
	
	
    public AmberVertex(long id, Map<String, Object> properties, 
    		AmberGraph graph, Long txnStart, Long txnEnd) {
    	
        super(id, properties, graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
    }
	
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd);
        return super.toString() + sb.toString();
    }
    
    
//    public Iterable<Edge> getEdges(Direction direction, String... labels) {
//        List<Edge> edges = new ArrayList<>();
//
//        if (direction == Direction.OUT || direction == Direction.BOTH) {
//            edges.addAll(getEdges(outEdges, labels));
//        }
//        if (direction == Direction.IN || direction == Direction.BOTH) {
// X           edges.addAll(getEdges(inEdges, labels));
//        }
//        return edges;
//    }
//
//
//    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
//        List<Vertex> vertices = new ArrayList<Vertex>();
//
//        // get the edges
//        Iterable<Edge> edges = getEdges(direction, labels);
//        
//        for (Edge e : edges) {
//            if (e.getVertex(Direction.IN) == (Vertex) this) {
//                vertices.add(e.getVertex(Direction.OUT));
//            } else {    
//                vertices.add(e.getVertex(Direction.IN));
//X            }    
//        }        
//        return vertices;
//    }
}

