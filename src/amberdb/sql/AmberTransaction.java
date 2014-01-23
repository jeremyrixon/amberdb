package amberdb.sql;


import java.util.Date;


public class AmberTransaction {
    
    private long id;
    public long getId() {
        return id;
    }
    
    private AmberGraph graph;
    protected void graph(AmberGraph g) { graph = g; }
    protected AmberGraph graph()       { return graph; }  
    
    private long time;
    private String user;
    private String operation;

    
    public AmberTransaction(AmberGraph graph, String user, String operation) {
        graph(graph);
        id = graph.newId();
        time = new Date().getTime();
        this.user = user;
        this.operation = operation;
        graph.dao().insertTransaction(id, time, user, operation);
    }
    
    
    public String toString() {
        return String.format("[%d] %s : %s : %s", id, new Date(time), user, operation);
    }
}
