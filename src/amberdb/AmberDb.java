package amberdb;

import java.nio.file.Path;

import javax.sql.DataSource;

public class AmberDb {
    final private DataSource dataSource;
    final private Path rootPath;

    public AmberDb(DataSource dataSource, Path rootPath) {
        this.dataSource = dataSource;
        this.rootPath = rootPath;
    }

    public AmberSession begin() {        
        return new AmberSession(dataSource, rootPath, null);
    }
    
    public AmberSession resume(long sessionId) {
        AmberSession as = new AmberSession(dataSource, rootPath, sessionId);
        return as;
    }
}
