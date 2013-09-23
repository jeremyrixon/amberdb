package amberdb.sql;

import amberdb.sql.dao.PersistentDao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;




public class AmberPersistenceDaoTest {

    public static DBI dbi = null;
    public static final String dsUrl = "jdbc:h2:mem:";
    
    public void setup() throws MalformedURLException, IOException {
        System.out.println("Setting up database");

        DataSource ds = JdbcConnectionPool.create(dsUrl,"dlir","dlir");
        
        dbi = new DBI(ds);
        PersistentDao dao = dbi.open(PersistentDao.class);
        
        dao.dropTables();

        dao.createVertexTable();
        dao.createVertexIndex();

        dao.createEdgeTable();
        dao.createEdgeIndex();

        dao.createPropertyTable();
        dao.createPropertyIndex();
        
        dao.createIdGeneratorTable();
        dao.createTransactionTable();
        
        dao.close();
    }

    
    public static void teardown() {
        PersistentDao dao = dbi.open(PersistentDao.class);
        dao.dropTables();
        dao.close();
        dbi = null;
    }

    @Test
    public void testDao() throws Exception {
        setup();
        s("testing");
        teardown();
    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
