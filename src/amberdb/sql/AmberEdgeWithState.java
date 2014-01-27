package amberdb.sql;


public class AmberEdgeWithState {

    
    public String state;
    public AmberEdge edge;
    
    
    public AmberEdgeWithState(AmberEdge edge, String state) {
        this.edge = edge;
        this.state = state;
    }
}
