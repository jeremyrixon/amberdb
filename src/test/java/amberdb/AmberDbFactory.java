package amberdb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class AmberDbFactory {

    public static boolean h2Test = false;

    public static AmberSession openAmberDb(String dbUrl, String dbUser, String dbPassword, String rootPath) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {
        DataSource ds = null;
        AmberSession sess = null;

        try {
            MysqlDataSource mds = new MysqlDataSource();
            mds.setURL(dbUrl);
            mds.setUser(dbUser);
            mds.setPassword(dbPassword);
            ds = mds;
            sess = openAmberDb(ds, Paths.get(rootPath));
        } catch (Throwable e) {
            h2Test = true;
            // This is for build integration site which does not have
            // direct access to the mysql data source.
            DriverManager.registerDriver(new org.h2.Driver());
            dbUrl = "jdbc:h2:" + Paths.get(".").resolve("graph") + ";MVCC=true;DATABASE_TO_UPPER=false";
            dbUser = "garfield";
            dbPassword = "odde";
            ds = JdbcConnectionPool.create(dbUrl, dbUser, dbPassword);
            sess = openAmberDb(ds, Paths.get(rootPath));
        }

        return sess;
    }

    private static AmberSession openAmberDb(DataSource dataSource, Path rootPath) {
        AmberSession db = new AmberDb(dataSource, rootPath).begin();
        return db;
    }
}
