package amberdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.relation.Acknowledge;

public class WorkAcknowledgementTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private AmberDb db;
    private AmberSession sess;

    @Before
    public void startup() {
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:;MVCC=TRUE;", "amb", "amb");
        db = new AmberDb(dataSource, Paths.get(folder.getRoot().getPath()));
        sess = db.begin();
    }

    @After
    public void teardown() throws IOException {
        if (sess != null) {
            sess.close();
        }
    }
    
    @Test
    public void shouldAddAnAcknowledgement() throws Exception {
        Party party = sess.addParty("James Bond");
        assertNotNull(party);    
        final String type = "of material";
        final String kindOfSupport = "lender";
        final Double weighting = 1.0;
        final Date date = new Date();
        final String url = "http://www.007.com/";
       
        Work w = sess.addWork();
        Acknowledge ack = w.addAcknowledgement(party, type, kindOfSupport, weighting, date, url);
        assertNotNull(ack);
        assertEquals(ack.getAckType(), type);
        assertEquals(ack.getKindOfSupport(), kindOfSupport);
        assertEquals(ack.getWeighting(), weighting);
        assertEquals(ack.getDate(), date);
        assertEquals(ack.getUrlToOriginial(), url);
    }
    
}
