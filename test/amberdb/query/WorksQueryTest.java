package amberdb.query;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import amberdb.model.Work;
import amberdb.AbstractDatabaseIntegrationTest;

public class WorksQueryTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void worksQueryTest() throws IOException {

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
}
