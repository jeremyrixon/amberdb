package amberdb.sql;

import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class BaseEdge extends BaseElement implements Edge {

	
    private String label;
    protected BaseVertex inVertex;
    protected BaseVertex outVertex;
    
    
    public BaseEdge(Long id, String label, BaseVertex inVertex, BaseVertex outVertex, Map<String, Object> properties, BaseGraph graph) {
        super(id, properties, graph);
        this.label = label;
        this.inVertex = inVertex;
        this.outVertex = outVertex;
        inVertex.inEdges.add(this);
        outVertex.outEdges.add(this);
    }

    
    @Override
    public Object getId() {
        return id;
    }

    
    @Override
    public void remove() {
        graph.removeEdge(this);
    }

    
    @Override
    public String getLabel() {
        return label;
    }

    
    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        // argument guard
        if (Direction.BOTH == direction) {  
        	throw new IllegalArgumentException("Can only get a vertex from a single direction"); 
        }
        if (direction == Direction.IN) {
            return (Vertex) inVertex;
        } 
        return (Vertex) outVertex; // direction must be out
    }

 
    @Override
    public void setProperty(String propertyName, Object value) {
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*|label")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        super.setProperty(propertyName, value);
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("edge id:").append(id)
        .append(" label:").append(label)
        .append(" out:").append(outVertex.getId())
        .append(" in:").append(inVertex.getId());
        // properties
        sb.append(" {");
        if (properties.size() > 0) {
            for (String key : properties.keySet()) {
                sb.append(key).append(":").append(properties.get(key)).append(", ");
            }
            sb.setLength(sb.length()-2);
        }
        sb.append("}");
        return sb.toString();
    }
}
