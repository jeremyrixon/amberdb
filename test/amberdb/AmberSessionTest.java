package amberdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.ImageFile;
import amberdb.model.Work;
import org.junit.*;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.tinkerpop.frames.Property;

import amberdb.AmberSession;
import amberdb.TestUtils;
import amberdb.enums.CopyRole;

public class AmberSessionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private AmberSession amberDb;

    @Before
    public void startup() {
        amberDb = new AmberSession();
    }

    @After
    public void teardown() throws IOException {
        if (amberDb != null) {
            amberDb.close();
        }
    }

    @Test
    public void castException() throws Exception {
        Work work = amberDb.addWork();
        work.addPage(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.xml"), "text/html").setOrder(1);

        Copy copy = work.getPage(1).getCopies().iterator().next();

        thrown.expect(ClassCastException.class);
        Work nWork = amberDb.findWork(copy.getId());
    }

    @Test
    public void noCastException() throws Exception {
        Work work = amberDb.addWork();
        Work nWork = amberDb.findWork(work.getId());
    }



}
