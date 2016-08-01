package amberdb.v2.relation;

import amberdb.v2.AmberSession;

public class AmberQueryBase {

    protected AmberSession session;

    public AmberQueryBase(AmberSession session) {
        this.session = session;
    }

    public AmberSession getSession() {
        return session;
    }

    public void setSession(AmberSession session) {
        this.session = session;
    }
}
