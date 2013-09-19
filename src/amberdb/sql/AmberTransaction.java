package amberdb.sql;

import amberdb.sql.AmberElement.State;
import amberdb.sql.dao.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    
    private AmberGraph graph;
    
    public AmberTransaction(String user, AmberGraph graph) {
        this.setUser(user);
        this.graph(graph);
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
        dao.createEdge(e.id(), e.txnStart(), e.txnEnd(),
                e.outVertexId, e.inVertexId, e.label, 
                e.edgeOrder, e.txnState().ordinal());
        // dao.add props ?
    }
  
    public void addVertex(AmberVertex e) {
        dao.createVertex(e.id(), e.txnStart(), e.txnEnd(), e.txnState().ordinal());
        // dao.add props ?
    }
  
    public void removeProperty(AmberElement e, String propertyName) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        dao.removeProperty(e.id(), propertyName);
    }

    public void setProperty(AmberElement e, String propertyName, Object value) {
      
        if (e.properties().containsKey(propertyName)) {
            updateProperty(e, propertyName, value);
        } else {
            createProperty(e, propertyName, value);
        }
    }
    
    public void updateProperty(AmberElement e, String propertyName, Object value) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        if (value instanceof Integer) {
            dao.updateIntegerProperty(e.id(), propertyName, (Integer) value);
        } else if (value instanceof Boolean) {
            dao.updateBooleanProperty(e.id(), propertyName, (Boolean) value);
        } else if (value instanceof Double) {
            dao.updateDoubleProperty(e.id(), propertyName, (Double) value);
        } else {
            dao.updateStringProperty(e.id(), propertyName, (String) value);
        }
    }
    
    public void createProperty(AmberElement e, String propertyName, Object value) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        if (value instanceof Integer) {
            dao.createIntegerProperty(e.id(), propertyName, (Integer) value);
        } else if (value instanceof Boolean) {
            dao.createBooleanProperty(e.id(), propertyName, (Boolean) value);
        } else if (value instanceof Double) {
            dao.createDoubleProperty(e.id(), propertyName, (Double) value);
        } else {
            dao.createStringProperty(e.id(), propertyName, (String) value);
        }
    }
    
    private void updateState(AmberElement e, State newState) {
        e.txnState(newState);
        if (e instanceof AmberEdge) {
            dao.updateEdgeState(e.id(), newState.ordinal());
        } else { // must be a Vertex
            dao.updateVertexState(e.id(), newState.ordinal());
        }
    }
    
    public void updateEdgeOrder(AmberEdge e) {
        if (e.txnState() == State.READ) updateState(e, State.MODIFIED);
        dao.updateEdgeOrder(e.id(), e.edgeOrder);
    }
    
    public void removeElement(AmberElement e) {
    
        if (e.txnState() == State.DELETED) return; // should be gone already
        
        if (e.txnState() == State.READ || e.txnState() == State.MODIFIED) { 
            markElementDeleted(e);
        }
        
        if (e.txnState() == State.NEW) {
            destroyElement(e);
        }
    }
    
    private void destroyElement(AmberElement e) {
        long id = e.id();
        if (e instanceof AmberEdge) {
            dao.removeEdge(id);
        } else {  // must be a Vertex
            dao.removeIncidentEdgeProperties(id);
            dao.removeIncidentEdges(id);
            dao.removeVertex(id);
        }
        dao.removeElementProperties(id);
    }
    
    private void markElementDeleted(AmberElement e) {
        long id = e.id();
        updateState(e, State.DELETED);
        if (e instanceof AmberVertex) {
            dao.removeIncidentEdgeProperties(id);
            dao.updateIncidentEdgeStates(id, State.DELETED.ordinal());
        }
        dao.removeElementProperties(id);
    }
    
    // can return vertex in DELETED state 
    public AmberVertex getVertex(long id) {
        AmberVertex vertex = dao.findVertexById(id);
        if (vertex != null) {
            vertex.graph(graph);
            loadElementProperties(vertex);
        }
        return vertex;
    }
    
    // can return edge in DELETED state
    public AmberEdge getEdge(long id) {
        AmberEdge edge = dao.findEdgeById(id);
        if (edge != null) {
            edge.graph(graph);
            loadElementProperties(edge);
        }
        return edge;
    }
    
    private void loadElementProperties(AmberElement e) {
        Iterator<AmberProperty> propertyIter = dao.findPropertiesByElementId(e.id());
        Map<String, Object> properties = new HashMap<String, Object>();
        while (propertyIter.hasNext()) {
            AmberProperty p = propertyIter.next();
            properties.put(p.name, p.value);
        }
        e.loadProperties(properties);
    }
    
    protected void graph(AmberGraph graph) {
        this.graph = graph;
    }
    
    protected AmberGraph graph() {
        return graph;
    }
}
