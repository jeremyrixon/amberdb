package amberdb.sql;


import java.util.Collections;
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
    
    
    @SuppressWarnings("unchecked")
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        AmberGraph g = (AmberGraph) graph;
        if (!g.inLocalMode()) g.getBranch(this.id, direction, labels);
        List<Edge> edges = (List<Edge>) super.getEdges(direction, labels);
        List<AmberEdge> amberEdges = (List<AmberEdge>) (List<? extends Edge>) edges;
        Collections.sort(amberEdges);

        return edges;
    }


    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        List<Vertex> vertices = (List<Vertex>) super.getVertices(direction, labels);

        return vertices;
    }
    
    
    public AmberGraph getAmberGraph() {
        return (AmberGraph) graph;
    }
}

