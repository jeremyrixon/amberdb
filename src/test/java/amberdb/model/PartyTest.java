package amberdb.model;

import amberdb.AmberDb;
import amberdb.AmberSession;
import com.google.common.collect.Iterables;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;

public class PartyTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private AmberDb db;
    private AmberSession sess;

    @Before
    public void startup() {
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:;MVCC=TRUE;DATABASE_TO_UPPER=false", "amb", "amb");
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
        Assert.assertNull(party);
    }

    @Test
    public void shouldAddPartyWithNameOnly() {
        String name = "Indiana Jones";
        Party party = sess.addParty(name);
        Assert.assertNotNull(party);
        Assert.assertTrue(party instanceof Party);
        Assert.assertEquals(party.getName(), name);
        Assert.assertEquals(party.getOrgUrl(), null);
        Assert.assertEquals(party.getLogoUrl(), null);
    }

    @Test
    public void shouldAddPartyWithOrgAndLogoUrl() {
        String name = "James Bond";
        String orgUrl = "http://www.007.com/";
        String logoUrl = "http://www.007.com/logo.png";
        Party party = sess.addParty(name, orgUrl, logoUrl);
        Assert.assertNotNull(party);
        Assert.assertTrue(party instanceof Party);
        Assert.assertEquals(party.getName(), name);
        Assert.assertEquals(party.getOrgUrl(), orgUrl);
        Assert.assertEquals(party.getLogoUrl(), logoUrl);
    }

    @Test
    public void shouldReturnAllParties() throws Exception {
        Party addParty1 = sess.addParty("BBC", "http://www.bbc.com/", null);
        Party addParty2 = sess.addParty("ABC", "http://www.abc.net.au/", "http://www.abc.net.au/homepage/2013/styles/img/abc.png");
        Party addParty3 = sess.addParty("Department of Something");
        sess.commit("whoami", "testing getAllparties");
        try (AmberSession session = db.begin()) {
            Iterable<Party> parties = session.getAllParties();
            Assert.assertTrue(Iterables.size(parties) >= 3);
            Iterables.contains(parties, addParty1);
            Iterables.contains(parties, addParty2);
            Iterables.contains(parties, addParty3);
        }
    }

    @Test
    public void shouldFindNoParty() {
        Party party = sess.findParty("Department of Jokes");
        Assert.assertNull(party);
    }

    @Test
    public void shouldFindAParty() throws Exception {
        String name = "Coding Horror";
        String url = "http://blog.codinghorror.com/";

        Party party1 = sess.findParty(name);
        Assert.assertNull(party1);
        party1 = sess.addParty(name, url, null);
        Assert.assertNotNull(party1);
        sess.commit("whoami", "testing findParty");

        try (AmberSession session = db.begin()) {
            Party p2 = session.findModelObjectById(party1.getId(), Party.class);
            Assert.assertEquals(p2, party1);
            Party party = session.findParty(name);

            Assert.assertNotNull(party);
            Assert.assertEquals(party.getName(), name);
            Assert.assertEquals(party.getOrgUrl(), url);
            Assert.assertEquals(party.getLogoUrl(), null);
        }
    }

    @Test
    public void shouldReturnPartyById() throws IOException {
        Party party1 = sess.addParty("Unknown");
        Long sessId = sess.suspend();
        try (AmberSession sess2 = db.resume(sessId)) {
            Party party2 = sess2.findModelObjectById(party1.getId(), Party.class);
            Assert.assertEquals(party2, party1);
        }
    }

    @Test
    public void shouldSuppressAParty() throws IOException {
        Party party1 = sess.addParty("BBC Suppress", "http://www.bbc.com/", null);
        party1.setSuppressed(true);
        sess.commit("whoami", "testing findParty");

        try (AmberSession session = db.begin()) {
            Party p2 = session.findModelObjectById(party1.getId(), Party.class);
            Assert.assertEquals(p2, party1);
            Party party = session.findParty(party1.getName());
            Assert.assertNotNull(party);
            Assert.assertEquals(party.isSuppressed(), true);
        }
    }
}
