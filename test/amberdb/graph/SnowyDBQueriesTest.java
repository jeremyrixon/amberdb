package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import amberdb.graph.AmberProperty;
import amberdb.graph.DataType;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class SnowyDBQueriesTest {

    MysqlDataSource persistentDs;
    
    @Before
	public void setup() throws MalformedURLException, IOException {

//        persistentDs = new MysqlDataSource();
//        persistentDs.setUser("amberdb");
//        persistentDs.setPassword("amberdb");
//        persistentDs.setServerName("mysql-devel.nla.gov.au");
//        persistentDs.setPort(6446);
//        persistentDs.setDatabaseName("amberdb");
//        persistentDs.setAllowMultiQueries(true);

      persistentDs = new MysqlDataSource();
      persistentDs.setUser("amberdb");
      persistentDs.setPassword("N1cetr33Sap");
      persistentDs.setServerName("mysql-dlir-prod.nla.gov.au");
      persistentDs.setPort(6446);
      persistentDs.setDatabaseName("amberdb");
      persistentDs.setAllowMultiQueries(true);

    }

    @After
	public void teardown() {}

        @Ignore
    @Test
    public void testFindBlobIds() throws Exception {

        Long start = new Date().getTime();
        s("starting now ...");

        DBI dbi = new DBI(persistentDs);
        try (Handle h = dbi.open()) {
        
            List<Map<String,Object>> rs = h.select(
                    "SELECT value, count(value) num " +
                    "FROM property " +
                    "WHERE name = 'blobId' " +
                    "GROUP BY value " +
                    "HAVING count(value) > 1 " +
                    "ORDER BY count(value) DESC " +
                    "LIMIT 200");
        
            for (Map<String, Object> r : rs) {
                
                Long blobId = (Long) AmberProperty.decode((byte[]) r.get("value"), DataType.LNG);
                long num = (long) r.get("num");
                
                s("bid:" + blobId + " num:" + num);
            }

//            List<Long> fileIds = new ArrayList<Long>();
//            List<Map<String,Object>> rs = h.select(
//                    "SELECT id " +
//                    "FROM property " +
//                    "WHERE name = 'blobId' " +
//                    "AND value = ?", AmberProperty.encode(new Long(2)));
//        
//            for (Map<String, Object> r : rs) {
//                StringBuilder sb = new StringBuilder();
//                for  (String col: r.keySet()) {
//                    Object o = r.get(col);
////                    if (o instanceof byte[]) {
////                        o = AmberProperty.decode((byte[]) o, DataType.LNG);
////                    }
//                    sb.append(col + ":" + o + ", ");
//                    fileIds.add((Long) o);
//                }
//                sb.setLength(sb.length()-2);
//                s(sb.toString());
//            }
//        
//            for (Long id: fileIds) {
//                
//            }
            
        }
        s("ended time elapsed: " + (new Date().getTime() - start) + "ms");
    }        
        
    
    private void s(String s) {
        System.out.println(s);
    }
}
