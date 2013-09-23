package amberdb.sql;

import amberdb.sql.dao.TransactionDao;

public class AmberTransaction extends Identifiable {
    
    AmberGraph graph;
    protected void graph(AmberGraph g) { graph = g; }
    protected AmberGraph graph()       { return graph; }  
    
    private TransactionDao dao() { return graph().transactionDao(); }

    private long commit;
    private String user;
    private String operation;
    
    public AmberTransaction(AmberGraph graph, String user, String operation) {
        graph(graph);
        id(-graph.newId()); // use a negative number to indicate in-progress txn
        commit = id();
        this.user = user;
        this.operation = operation;
        
        dao().insertTransaction(id(), commit, user, operation);
    }
    
}
