package amberdb;

import amberdb.AmberDb;
import amberdb.AmberSession;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public abstract class AbstractDatabaseIntegrationTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    public AmberDb amberDb;
    public AmberSession amberSession;
    public DataSource amberSrc;
    public DataSource banjoSrc;
    public Path tempPath;

    @Before
    public void setupIntegrationTest() {
        if(amberDb == null) {
            tempPath = Paths.get(tempFolder.getRoot().getAbsolutePath());
            amberSrc = JdbcConnectionPool.create("jdbc:h2:mem:amber;DB_CLOSE_DELAY=-1;MVCC=true", "", "");
            amberDb = new AmberDb(amberSrc, tempPath);
            amberSession = amberDb.begin();
        }
    }

    @After
    public void tearDownIntegrationTest() throws SQLException {
        amberSrc.getConnection().prepareStatement("DROP ALL OBJECTS").execute();
    }

}
