package amberdb.sql;


public class AmberVertexWithState {

    
    public String state;
    public AmberVertex vertex;

    
    public AmberVertexWithState(AmberVertex vertex, String state) {
        this.vertex = vertex;
        this.state = state;
    }
}
