package amberdb.repository;

import amberdb.repository.mappers.AmberDbMapperFactory;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class JdbiHelper {
    private DBI dbi;

    private static final Logger log = LoggerFactory.getLogger(JdbiHelper.class);

    public JdbiHelper() {
    }

    /**
     * Use with Spring.
     * @param dbi DBI
     */
    public JdbiHelper(DBI dbi) {
        this.dbi = dbi;
        this.dbi.registerMapper(new AmberDbMapperFactory());
    }

    /**
     * Use with Spring.
     * @param dataSource JDBC Connection Pool datasource
     */
    public JdbiHelper(DataSource dataSource) {
        dbi = new DBI(dataSource);
        dbi.registerMapper(new AmberDbMapperFactory());
    }

    public DBI getDbi() {
        if (dbi != null) return dbi;

        // Try to read the database connection details from System properties
        if (System.getProperty("amber.url") != null) {
            log.debug("Reading database connection properties from environment variables");
            JdbcConnectionPool jdbcConnectionPool =
                    JdbcConnectionPool.create(
                            System.getProperty("amber.url"),
                            System.getProperty("amber.user"),
                            System.getProperty("amber.pass"));
            dbi = new DBI(jdbcConnectionPool);
        } else {
            // Otherwise, read it from a properties file
            log.debug("Reading database connection properties from db.properties file");
            JdbcConnectionPool jdbcConnectionPool =
                    JdbcConnectionPool.create(
                            DbProperties.DB_URL.val(),
                            DbProperties.DB_USERNAME.val(),
                            DbProperties.DB_PASSWORD.val());
            dbi = new DBI(jdbcConnectionPool);
        }

        dbi.registerMapper(new AmberDbMapperFactory());
        return dbi;
    }
}
