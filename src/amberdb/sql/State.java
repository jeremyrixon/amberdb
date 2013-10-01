package amberdb.sql;

public enum State {

    NEW, MOD, DEL, AMB, BAD;
    
    public static State forCode(String s) {
        if (s == State.NEW.toString()) return State.NEW;
        if (s == State.MOD.toString()) return State.MOD;
        if (s == State.DEL.toString()) return State.DEL;
        if (s == State.AMB.toString()) return State.AMB;
        return State.BAD;
    }
}
