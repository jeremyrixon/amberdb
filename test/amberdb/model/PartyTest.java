package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Iterables;

import amberdb.AmberDb;
import amberdb.AmberSession;

public class PartyTest {
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
    public void shouldNotAddPartyWithoutName() throws Exception {
        Party party = sess.addParty(null);
        assertNull(party);        
    }

    @Test
    public void shouldAddPartyWithNameOnly() {
        String name = "Indiana Jones";
        Party party = sess.addParty(name);
        assertNotNull(party);
        assertTrue(party instanceof Party);
        assertEquals(party.getName(), name);
        assertEquals(party.getOrgUrl(), null);
        assertEquals(party.getLogoUrl(), null);
    }

    @Test
    public void shouldAddPartyWithOrgAndLogoUrl() {
        String name = "James Bond";
        String orgUrl = "http://www.007.com/";
        String logoUrl = "http://www.007.com/logo.png";
        Party party = sess.addParty(name, orgUrl, logoUrl);
        assertNotNull(party);
        assertTrue(party instanceof Party);
        assertEquals(party.getName(), name);
        assertEquals(party.getOrgUrl(), orgUrl);
        assertEquals(party.getLogoUrl(), logoUrl);
    }
    
    @Test
    public void shouldReturnAllParties() throws Exception{        
        Party addParty1 = sess.addParty("BBC", "http://www.bbc.com/", null);
        Party addParty2 = sess.addParty("ABC", "http://www.abc.net.au/", "http://www.abc.net.au/homepage/2013/styles/img/abc.png");
        Party addParty3 = sess.addParty("Department of Something");
        sess.commit("whoami", "testing getAllparties");
        AmberSession session = db.begin();
        Iterable<Party> parties = session.getAllParties();       
        assertTrue(Iterables.size(parties) >= 3);
        Iterables.contains(parties, addParty1);
        Iterables.contains(parties, addParty2);
        Iterables.contains(parties, addParty3);
    }

    @Test
    public void shouldFindNoParty() {
        Party party = sess.findParty("Department of Jokes");
        assertNull(party);
    }

    @Test
    public void shouldFindAParty() throws Exception {
        String name = "Coding Horror";
        String url = "http://blog.codinghorror.com/";
        
        Party party1 = sess.findParty(name);
        assertNull(party1);
        party1 = sess.addParty(name, url, null);  
        assertNotNull(party1);
        sess.commit("whoami", "testing findParty");
       
        AmberSession session = db.begin();
        Party p2 = session.findModelObjectById(party1.getId(), Party.class);
        assertEquals(p2,party1);
        Party party = session.findParty(name);
        
        assertNotNull(party);
        assertEquals(party.getName(), name);
        assertEquals(party.getOrgUrl(), url);
        assertEquals(party.getLogoUrl(), null);
    }
    
    @Test
    public void shouldReturnPartyById() throws IOException {
        Party party1 = sess.addParty("Unknown"); 
        Long sessId = sess.suspend();
        AmberSession sess2 = db.resume(sessId);
        Party party2 = sess2.findModelObjectById(party1.getId(), Party.class);
        assertEquals(party2, party1);
        sess2.close();
    }
}