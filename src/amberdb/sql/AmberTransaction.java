package amberdb.sql;

import java.util.Date;

public class AmberTransaction {

    long id;
    String user;
    Date commit;
    
    public AmberTransaction(long id, String user, Date commit) {
        this.id = id;
        this.setUser(user);
        this.setCommit(commit);
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
    
    
}
