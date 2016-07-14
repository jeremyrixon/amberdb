package amberdb.v1.graph;


public class AmberEdgeWithState {

    
    public String state;
    public AmberEdge edge;
    
    
    public AmberEdgeWithState(AmberEdge edge, String state) {
        this.edge = edge;
        this.state = state;
    }
}
