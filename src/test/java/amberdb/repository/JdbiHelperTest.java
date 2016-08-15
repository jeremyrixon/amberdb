package amberdb.repository;

import amberdb.sql.LookupsSchema;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class JdbiHelperTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private JdbiHelper jdbiHelper;
    private DataSource ds;

    @Before
    public void setup() {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:helper;DB_CLOSE_DELAY=-1;MVCC=TRUE;","amb","amb");
    }

    @After
    public void cleanup() {
        Handle h = jdbiHelper.getDbi().open();
        h.createStatement("DROP ALL OBJECTS").execute();
    }

    @Test
    public void createsHelperFromPropsFile() {
        jdbiHelper = new JdbiHelper();
        Assert.assertNotNull(jdbiHelper);
        Assert.assertNotNull(jdbiHelper.getDbi());

        LookupsSchema lookups = jdbiHelper.getDbi().onDemand(LookupsSchema.class);
        Assert.assertFalse(lookups.schemaTablesExist());
    }

    @Test
    public void createsHelperFromDataSource() {
        jdbiHelper = new JdbiHelper(ds);
        Assert.assertNotNull(jdbiHelper);
        Assert.assertNotNull(jdbiHelper.getDbi());

        LookupsSchema lookups = jdbiHelper.getDbi().onDemand(LookupsSchema.class);
        Assert.assertFalse(lookups.schemaTablesExist());
    }

    @Test
    public void createsHelperFromSystemProps() {
        System.setProperty("amber.url", "jdbc:h2:mem:helper;DB_CLOSE_DELAY=-1;MVCC=TRUE;");
        System.setProperty("amber.user", "amb");
        System.setProperty("amber.pass", "amb");

        jdbiHelper = new JdbiHelper();
        Assert.assertNotNull(jdbiHelper);
        Assert.assertNotNull(jdbiHelper.getDbi());

        LookupsSchema lookups = jdbiHelper.getDbi().onDemand(LookupsSchema.class);
        Assert.assertFalse(lookups.schemaTablesExist());
    }

    @Test
    public void performsModificationsOnDB() {
        jdbiHelper = new JdbiHelper();

        Handle h = jdbiHelper.getDbi().open();
        h.createStatement("CREATE TABLE test (greeting VARCHAR(20) NULL)").execute();
        h.createStatement("INSERT INTO test (greeting) VALUES ('hello world')").execute();
        List<Map<String, Object>> result = h.createQuery("SELECT greeting FROM test").list();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);

        for (Map<String, Object> res : result) {
            String greeting = (String) res.get("greeting");
            Assert.assertTrue("hello world".equalsIgnoreCase(greeting));
        }
    }
}
