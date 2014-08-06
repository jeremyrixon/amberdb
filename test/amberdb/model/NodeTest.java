package amberdb.model;

import amberdb.AmberSession;
import com.google.common.collect.Sets;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class NodeTest {

    private static AmberSession db;

    @Before
    public void setup() throws IOException, InstantiationException {
        db = new AmberSession();
    }

    @Test
    public void propertySetHasAllProperties() {
        Work w = db.addWork();
        w.setBibLevel("bib");
        assertEquals(w.getPropertyKeySet(), Sets.newHashSet("bibLevel", "type"));
    }
    
    @After
    public void teardown() throws IOException {
        if (db != null)
            db.close();
    }
}
