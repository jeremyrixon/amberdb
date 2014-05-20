package amberdb.graph;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AmberPersistenceDaoTest {

    @Before
    public void setup() {    }

    @After
    public void teardown() {    }

    @Test
    public void testDao() throws Exception {

        List<Long> l = new ArrayList<Long>();
        l.add(new Long(5));
        l.add(new Long(2));
        l.add(new Long(5));
        l.add(new Long(3));
        l.add(new Long(0));
        l.add(new Long(5));
        
        s("before" + l.size());
        l.remove(new Long(5));
        s("after" + l.size());
        l.remove(new Long(5));
        s("after" + l.size());
        
    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
