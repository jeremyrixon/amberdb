package amberdb.repository.dao;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.model.EADWork;
import amberdb.repository.JdbiHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WorkDaoTest extends AbstractDatabaseIntegrationTest {

    private JdbiHelper jdbiHelper;
    private EADWork componentWork;
    private WorkDao workDao;

    @Before
    public void setup() {
        setDataInH2();
        jdbiHelper = new JdbiHelper(amberSrc);
        workDao = jdbiHelper.getDbi().onDemand(WorkDao.class);
    }

    private void setDataInH2() {
        EADWork collectionWork = amberSession.addWork().asEADWork();
        componentWork = collectionWork.addEADWork();
        componentWork.setSubType("series");
        componentWork.setTitle("Papers of Leslie Greener");
        amberSession.commit();
    }
}
