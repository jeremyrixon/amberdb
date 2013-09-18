package amberdb.sql;

import java.util.Date;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;

public class AmberTransaction {

    long id;
    String user;
    Date commit;
    
    private DBI dbi = null;
    private static final String dataSourceUrl = "jdbc:h2:mem:";
    
    public AmberTransaction(String user) {
        this.setUser(user);
        DataSource ds = JdbcConnectionPool.create(dataSourceUrl, user,"txn");
        dbi = new DBI(ds);
        AmberTransactionDao dao = dbi.open(AmberTransactionDao.class);
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
