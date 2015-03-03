package amberdb.model;

import amberdb.AmberSession;
import amberdb.AmberDb;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        assertEquals(w.getPropertyKeySet(), Sets.newHashSet("bibLevel", "type"));
    }

    @Test
    public void testFindNodeByJsonListValue() throws IOException {

        // committed works
        Work w1 = db.addWork();
        Work w2 = db.addWork();
        Work w3 = db.addWork();
        Work w4 = db.addWork();

        w1.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w2.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma", "wally"));
        w3.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));
        w4.setAlias(Arrays.asList("beta", "delta", "wally", "epsilon", "gamma"));

        db.commit("blue", "carrot");

        // uncommitted works
        Work w5 = db.addWork();
        Work w6 = db.addWork();

        w5.setAlias(Arrays.asList("wally", "beta", "delta", "epsilon", "gamma"));
        w6.setAlias(Arrays.asList("beta", "delta", "epsilon", "gamma"));

        List<Work> works = db.findModelByValueInJsonList("alias", "wally", Work.class);

        assertEquals(4, works.size());
        for (Work w : works) {
            assertTrue(w.getAlias().contains("wally"));
        }
    }
}
