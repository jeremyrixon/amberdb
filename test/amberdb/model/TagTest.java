package amberdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.TestUtils;
import amberdb.enums.CopyRole;

public class TagTest {
    
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();    
    
    private AmberDb db;
    private AmberSession sess;
    
    @Before
    public void startup() {
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "amb", "amb");
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
    public void basicTagTests() throws Exception { 
        
        // creation
        Tag tag = sess.addTag();
        Tag tag2 = sess.addTag("tag2");

        assertNotNull(tag);
        assertNotNull(tag2);
        assertTrue(tag instanceof Tag);        
        assertTrue(tag2 instanceof Tag);
        assertEquals(tag.getName(), null);        
        assertEquals(tag2.getName(), "tag2");
        
        // tag persists for session suspend
        Long sessId = sess.suspend();
        AmberSession sess2 = db.resume(sessId);
        Tag tag3 = sess2.findModelObjectById(tag.getId(), Tag.class);
        assertEquals(tag3, tag);
        
        // tag persists for session commit
        sess.commit("tester", "save tag");
        AmberSession sess3 = db.begin();
        Tag tag4 = sess3.findModelObjectById(tag2.getId(), Tag.class);
        assertEquals(tag4, tag2);
        
        // tags can be searched for and found
        Tag tag5 = sess.addTag("t5");
        Tag tag6 = sess.addTag("t6");
        Tag tag7 = sess.addTag("t7");
        sess.commit("tester", "save tags for search");
        
        Iterable<Tag> tagColl1 = sess2.findTags();
        Iterable<Tag> tagCollection2 = sess3.findTags(new String[] {"t5","t6"});
        int i = 0;
        for (Tag t : tagColl1) {
            i++;
        }
        assertEquals(i, 5);
        
        sess2.close();
        sess3.close();
    }
    
    private void s(Object o) {
        System.out.println(o);
    }
}

