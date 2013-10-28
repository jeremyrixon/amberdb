package amberdb.sql.map;

import amberdb.sql.State;

/**
 * 
 * Container for a long id and an element state. Really basic.
 *
 */
public class IdState {

    private Long id;
    public Long getId() {
        return id;
    }

    private State state;
    public State getState() {
        return state;
    }
    
    public IdState(long id, String stateStr) {
        this.id = id;
        state = State.valueOf(stateStr);
    }

}
