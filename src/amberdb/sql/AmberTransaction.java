package amberdb.sql;

import amberdb.sql.dao.TransactionDao;

public class AmberTransaction {
    
    private long id;
    public long getId() {
        return id;
    }
    
    
    AmberGraph graph;
    protected void graph(AmberGraph g) { graph = g; }
    protected AmberGraph graph()       { return graph; }  
    
    private TransactionDao dao() { return graph().transactionDao(); }

    private long commit;
    private String user;
    private String operation;
    
    public AmberTransaction(AmberGraph graph, String user, String operation) {
        graph(graph);
        id = graph.newPersistentId();
        commit = id;
        this.user = user;
        this.operation = operation;
        
        dao().insertTransaction(id, commit, user, operation);
    }
    
    public String toString() {
        return String.format("[%d] %s : %s", id, user, operation);
    }
    
}
