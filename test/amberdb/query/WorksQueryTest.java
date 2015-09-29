package amberdb.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;

import doss.CorruptBlobStoreException;
import amberdb.enums.CopyRole;
import amberdb.graph.AmberQuery;
import amberdb.model.CameraData;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.GeoCoding;
import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.Work;
import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;

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
