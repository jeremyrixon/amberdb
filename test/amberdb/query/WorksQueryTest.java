package amberdb.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amberdb.model.Copy;
import org.junit.Test;

import amberdb.model.Work;
import amberdb.AbstractDatabaseIntegrationTest;

public class WorksQueryTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void getWorksQueryTest() throws IOException {

        List<Long> ids = new ArrayList<>(); 
        for (int i = 0; i < 100; i++) {
            Work w = amberSession.addWork();
            if (i % 2 == 0) {
                ids.add(w.getId());
            }
        }
        amberSession.commit();
        amberSession.getAmberGraph().clear();
        List<Work> works = WorksQuery.getWorks(amberSession, ids);
        assertEquals(works.size(), 50);
    }

    @Test
    public void getCopiesWithWorksTest() throws IOException {

        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Work w = amberSession.addWork();
            Copy c = w.addCopy();
            if (i % 2 == 0) {
                ids.add(c.getId());
            }
        }
        amberSession.commit();
        amberSession.getAmberGraph().clear();
        amberSession.getAmberGraph().setLocalMode(true);
        List<Copy> copies = WorksQuery.getCopiesWithWorks(amberSession, ids);
        assertEquals(copies.size(), 50);
        for (Copy c : copies) {
            assertNotNull(c.getWork());
        }
    }
}
