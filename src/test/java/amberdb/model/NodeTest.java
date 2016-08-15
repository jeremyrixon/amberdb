package amberdb.model;

import amberdb.AmberSession;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class NodeTest {

    private AmberSession db;

    @Before
    public void setup() throws IOException, InstantiationException {
        db = new AmberSession();
    }

    @After
    public void teardown() throws IOException {
        if (db != null)
            db.close();
    }

    @Test
    public void propertySetHasAllProperties() {
        Work w = db.addWork();
        w.setBibLevel("bib");
        Assert.assertEquals(w.getPropertyKeySet(), Sets.newHashSet("bibLevel", "type"));
    }
}
