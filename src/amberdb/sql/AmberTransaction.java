package amberdb.sql;

import amberdb.sql.AmberElement.State;
import amberdb.sql.dao.*;
import java.util.Date;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import javax.sql.DataSource;


public class AmberTransaction {

    long id;
    String user;
    Date commit;
    
    private DBI dbi = null;
    private AmberTransactionDao dao = null;
    private static final String dataSourceUrl = "jdbc:h2:mem:";
    
    public AmberTransaction(String user) {
        this.setUser(user);
        initMemoryDb();
    }

    public AmberTransaction(Long id, String user, Date commit) {
        this.setUser(user);
        this.id = id;
        this.setCommit(commit);
        initMemoryDb();
    }
    
    private void initMemoryDb() {
        DataSource ds = JdbcConnectionPool.create(dataSourceUrl, user,"txn");
        dbi = new DBI(ds);
        dao = dbi.onDemand(AmberTransactionDao.class);
    }
    
    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getCommit() {
        return commit;
    }

    public void setCommit(Date commit) {
        this.commit = commit;
    }

    public AmberTransactionDao dao() {
        return dao;
    }
    
    public void addEdge(AmberEdge e) {
//        dao.addEdge(e.id(), e.txnStart(), e.txnEnd(), e.properties(), 
//                e.outVertexId, e.inVertexId, e.label, e.edgeOrder, e.txnState().ordinal());
//        dao.add
    }
    
    public void removeProperty(AmberElement e, String propertyName) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        dao.removeProperty(e.id(), propertyName);
    }

    public void setProperty(AmberElement e, String propertyName, Object value) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        if (value instanceof Integer) {
            dao.setIntegerProperty(e.id(), propertyName, (Integer) value);
        } else if (value instanceof Boolean) {
            dao.setBooleanProperty(e.id(), propertyName, (Boolean) value);
        } else if (value instanceof Double) {
            dao.setDoubleProperty(e.id(), propertyName, (Double) value);
        } else {
            dao.setStringProperty(e.id(), propertyName, (String) value);
        }
    }
    
    private void updateState(AmberElement e, State newState) {
        e.txnState(newState);
        if (e instanceof AmberEdge) {
            dao.updateEdgeState(e.id(), newState.ordinal());
        } else {
            dao.updateVertexState(e.id(), newState.ordinal());
        }
    }
    
    public void updateEdgeOrder(AmberEdge e) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        dao.updateEdgeOrder(e.id(), e.edgeOrder);
    }
    
    public void removeElement(AmberElement e) {
    
        if (e.txnState() == State.DELETED) return;
        
        if (e.txnState() == State.READ || e.txnState() == State.MODIFIED) { 
            updateState(e, State.DELETED);
            if (e instanceof AmberVertex) {
//                for (AmberEdge edge : ((AmberVertex) e).)
//                removeElement((Vertex))
            }
        }
        
        if (e.txnState() == State.NEW) {
            if (e instanceof AmberEdge) {
                dao.removeEdge(e.id());
            } else {
                dao.removeVertex(e.id());
//                removeIncidentEdges(e.id());
            }
        }
        
        dao.removeElementProperties(e.id());
    }
    
    
}
