package amberdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.relation.Acknowledge;

public class WorkAcknowledgementTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private AmberDb db;
    private AmberSession sess;
    private Party party;

    @Before
    public void startup() {
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:;MVCC=TRUE;", "amb", "amb");
        db = new AmberDb(dataSource, Paths.get(folder.getRoot().getPath()));
        sess = db.begin();
        party = sess.addParty("James Bond");
    }

    @After
    public void teardown() throws IOException {
        if (sess != null) {
            sess.close();
        }
    }

    @Test
    public void shouldAddAnAcknowledgement() throws Exception {
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

    @Test
    public void shouldAddMultipleAcknowledgementToSameParty() {
        final String type = "of arrangement & description";
        final String kindOfSupport = "lender";
        final Double weighting = 1.1;
        final Date date = new Date();
        final String url = "http://www.web.com/";

        final String type1 = "of digitisation";
        final String kindOfSupport1 = "sponsor";
        final Double weighting1 = 2.0;
        final Date date1 = new Date();
        final String url1 = "http://www.abc.com/";

        Work w = sess.addWork();
        Acknowledge ack = w.addAcknowledgement(party, type, kindOfSupport, weighting, date, url);
        assertNotNull(ack);
        assertEquals(ack.getAckType(), type);
        assertEquals(ack.getKindOfSupport(), kindOfSupport);
        assertEquals(ack.getWeighting(), weighting);
        assertEquals(ack.getDate(), date);
        assertEquals(ack.getUrlToOriginial(), url);

        Acknowledge ack1 = w.addAcknowledgement(party, type1, kindOfSupport1, weighting1, date1, url1);
        assertNotNull(ack1);
        assertEquals(ack1.getAckType(), type1);
        assertEquals(ack1.getKindOfSupport(), kindOfSupport1);
        assertEquals(ack1.getWeighting(), weighting1);
        assertEquals(ack1.getDate(), date1);
        assertEquals(ack1.getUrlToOriginial(), url1);

        // check edge id and directions
        Edge edge = ack.asEdge();
        Edge edge1 = ack1.asEdge();
        assertEquals(edge.getVertex(Direction.IN), edge1.getVertex(Direction.IN));
        assertEquals(edge.getVertex(Direction.OUT), edge1.getVertex(Direction.OUT));
        assertThat(edge.getId(), not(edge1.getId()));
    }

    @Test
    public void shouldMapSamePartyToDifferentWork() {
        final String type1 = "of arrangement & description";
        final String kindOfSupport1 = "lender";
        final Double weighting1 = 1.1;
        final Date date1 = new Date();
        final String url1 = "http://www.web.com/";

        final String type2 = "of digitisation";
        final String kindOfSupport2 = "sponsor";
        final Double weighting2 = 2.0;
        final Date date2 = new Date();
        final String url2 = "http://www.abc.com/";
        Work work1 = sess.addWork();
        Work work2 = sess.addWork();
        Acknowledge ack1 = work1.addAcknowledgement(party, type1, kindOfSupport1, weighting1, date1, url1);
        Acknowledge ack2 = work2.addAcknowledgement(party, type2, kindOfSupport2, weighting2, date2, url2);

        Edge edge1 = ack1.asEdge();
        Edge edge2 = ack2.asEdge();
        assertThat(ack1.asEdge().getId(), not(ack2.asEdge().getId()));
        assertEquals(edge1.getVertex(Direction.IN), edge2.getVertex(Direction.IN));
        assertThat(edge1.getVertex(Direction.OUT), not(edge2.getVertex(Direction.OUT)));
    }

    @Test
    public void shouldAdd12AcknowledgementsToAWork() {
        Work work = sess.addWork();
        final String type = "of arrangement & description";
        final String kindOfSupport = "lender";
        final Date date = new Date();
        final String url = "http://www.web.com/";
        final int count = 12;
        Set<Party> parties = new HashSet<>(count);

        for (int i = 1; i < count + 1; i++) {
            Party party = sess.addParty("Party_" + i);
            parties.add(party);
            work.addAcknowledgement(party, type, kindOfSupport, new Double(i), date, url);
        }

        assertEquals(parties.size(), count);
        assertEquals(Iterables.size(work.getAcknowledgements()), count);

        for (Acknowledge ack : work.getAcknowledgements()) {
            parties.remove(ack.getParty());
        }

        assertEquals(parties.size(), 0);
    }

    @Test
    public void shouldRemoveOneAcknowledgementFromAWork() {
        Work work = sess.addWork();
        work.addAcknowledgement(party, "of arrangement & description", "lender", 1.0, new Date(), "http://www.web.com/");
        assertEquals(Iterables.size(work.getAcknowledgements()), 1);
        work.removeAcknowledgement(work.getAcknowledgements().iterator().next());
        assertEquals(Iterables.size(work.getAcknowledgements()), 0);
    }

    @Test
    public void shouldRemoveMultipleAcknowledgementsFromAWork() {
        Work work = sess.addWork();
        Acknowledge ack1 = work.addAcknowledgement(party, "of arrangement & description", "lender", 1.0, new Date(), "http://www.web.com/");
        Acknowledge ack2 = work.addAcknowledgement(party, "of creation of finding aids", "sponsor", 1.2, new Date(), "http://www.nla.gov.au/");
        Party party2 = sess.addParty("Shrek", "https://en.wikipedia.org/wiki/Shrek", "shrek's logo");
        Acknowledge ack3 = work.addAcknowledgement(party2, "of donation of digitised copy", "donor", 2.0, new Date(), "http://ourweb.nla.gov.au");
        assertEquals(Iterables.size(work.getAcknowledgements()), 3);
        work.removeAcknowledgement(ack2);
        work.removeAcknowledgement(ack3);
        assertEquals(Iterables.size(work.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work.getAcknowledgements(), ack1));
        assertFalse(Iterables.contains(work.getAcknowledgements(), ack2));
        assertFalse(Iterables.contains(work.getAcknowledgements(), ack3));
    }
    
    
    @Test
    public void shouldRemoveOnlyOneAcknowledgement() {
        Work work1 = sess.addWork();
        Work work2 = sess.addWork();
        Acknowledge ack1 = work1.addAcknowledgement(party, "of arrangement & description", "lender", 1.0, new Date(), "http://www.web.com/");
        Acknowledge ack2 = work2.addAcknowledgement(party, "of creation of finding aids", "sponsor", 1.2, new Date(), "http://www.nla.gov.au/");
        
        assertEquals(Iterables.size(work1.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work1.getAcknowledgements(), ack1));
        assertEquals(Iterables.size(work2.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work2.getAcknowledgements(), ack2));
        
        work2.removeAcknowledgement(ack2);
       
        assertEquals(Iterables.size(work1.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work1.getAcknowledgements(), ack1));
        assertEquals(Iterables.size(work2.getAcknowledgements()), 0);
        assertFalse(Iterables.contains(work2.getAcknowledgements(), ack2));
    }

}
