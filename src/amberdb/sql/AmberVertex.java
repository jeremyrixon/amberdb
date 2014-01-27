package amberdb.sql;


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
    
    
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        
        ((AmberGraph) graph).getBranch(this.id, Direction.BOTH, labels);
        List<Edge> edges = (List<Edge>) super.getEdges(direction, labels);
        
        return edges;
    }


    public Iterable<Vertex> getVertices(Direction direction, String... labels) {

        ((AmberGraph) graph).getBranch(this.id, Direction.BOTH, labels);
        List<Vertex> vertices = (List<Vertex>) super.getVertices(direction, labels);
        
        return vertices;
    }
}

