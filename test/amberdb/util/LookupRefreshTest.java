package amberdb.util;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.sql.ListLu;

public class LookupRefreshTest {

    @Test
    public void TestRefreshLookupData() throws IOException {
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:amberdb", "amberdb", "amberdb");
        String rootPath = ".";
        
        try (AmberSession db = new AmberDb(ds, Paths.get(rootPath)).begin()) {
            List<ListLu> srcLu = LookupRefresh.synchronizeLookups(db);
            Map<String, ListLu> destMap = LookupRefresh.indexLookups(db.getLookups().findActiveLookups());
            for (ListLu lu : srcLu) {
                String nameCode = lu.getName() + "_" + lu.getCode();
                assertNotNull(destMap.get(nameCode));
            }
        }
    }
}
