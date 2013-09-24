package amberdb.sql;

import amberdb.sql.dao.StatefulDao;

public class Stateful extends Persistent {
    
    private StatefulDao dao() { return graph().statefulDao(); }
    
    public enum State {
        
        NEW, MODIFIED, DELETED, READ, BAD;
        
        public static State forOrdinal(int i) {
            if (i == State.NEW.ordinal())      return State.NEW;
            if (i == State.MODIFIED.ordinal()) return State.MODIFIED;
            if (i == State.DELETED.ordinal())  return State.DELETED;
            if (i == State.READ.ordinal())     return State.READ;
            return State.BAD;
        }
    };
    
    public void sessionState(State state) { 
        if (this instanceof AmberVertex) {
            dao().updateVertexState(id(), state.ordinal());
        } else if (this instanceof AmberEdge) {
            dao().updateEdgeState(id(), state.ordinal());
        }
    }
    public State sessionState() { 
        if (this instanceof AmberVertex) {
            return State.forOrdinal(dao().getVertexState(id()));
        } else if (this instanceof AmberEdge) {
            return State.forOrdinal(dao().getEdgeState(id()));
        }
        return State.BAD;
    }
}
