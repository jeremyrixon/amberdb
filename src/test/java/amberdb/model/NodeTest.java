package amberdb.model;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class NodeTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void propertySetHasAllProperties() {
        Work w = amberSession.addWork();
        w.setBibLevel("bib");
        Assert.assertEquals(w.getPropertyKeySet(), Sets.newHashSet("bibLevel", "type"));
    }
}
