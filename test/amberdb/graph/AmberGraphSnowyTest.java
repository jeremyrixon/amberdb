package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.graph.BaseGraph;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import com.google.common.collect.Lists;

public class AmberGraphSnowyTest {

    @Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
    
    //public AmberGraph graph;
    
    public AmberSession session;
    
    //DataSource sessionDs;
    DataSource persistentDs;
    
    @Before
	public void setup() throws MalformedURLException, IOException {
        s("Setting up graph");

        String tempPath = tempFolder.getRoot().getAbsolutePath();
        s("sessions are located here " + tempPath);
        //sessionDs = JdbcConnectionPool.create("jdbc:h2:" + tempPath + "session1", "sess", "sess");

        MysqlDataSource persistentDs = new MysqlDataSource();
        persistentDs.setUser("amberdb");
        persistentDs.setPassword("amberdb");
        persistentDs.setServerName("mysql-devel.nla.gov.au");
        persistentDs.setPort(6446);
        persistentDs.setDatabaseName("amberdb");
        persistentDs.setAllowMultiQueries(true);

        //graph = new AmberGraph(sessionDs, persistentDs, "tester");
        session = new AmberDb(persistentDs, tempFolder.getRoot().toPath()).begin();
        
    }

    @After
	public void teardown() {}

    @Ignore
    @Test
    public void testGraphSpeed() throws Exception {

        Long start = new Date().getTime();
        s("starting now ...");

        Work book = session.findWork(179722129l);
        session.getAmberGraph().setLocalMode(true);
        book.loadPagedWork();

        // Get pages
        List<Page> pages = Lists.newArrayList(book.getPages());
        for (Page p : pages) {
            s("PAGE: " + p);
            if (p.getSubType().equals("page")) {
                Copy c = p.getCopy(CopyRole.MASTER_COPY);
                s("\t" + c);
                File f = c.getFile();
                s("\t" + f);
            }
        }
        
        Long end = new Date().getTime();
        s("Time millis: " + (end-start));

    }
    
    @Ignore
    @Test
    public void testWorkFrame() throws Exception {

	s("starting now ...");

        Work book = session.findWork(179722129l);
        Iterable<Work> pages = book.getChildren();
        for (Work w : pages) {
            s(""+w);
        }
    }

    @Ignore
    @Test
    public void testLoadPagedWork() throws Exception {

        s("starting now ...");
        Long start = new Date().getTime();

        Work book = session.findWork(179722129l);
        book.loadPagedWork();
        List<Work> pages = book.getPartsOf("page");
        for (Work p : pages) {
            s("\n" + p);
            s(p.getSubUnitType());
        }

        s("============================");
        pages = book.getExistsOn("page");
        for (Work p : pages) {
            s("\n" + p);
            s(p.getSubUnitType());
        }

        Long end = new Date().getTime();
        s("Time millis: " + (end-start));
    }

    @Ignore
    @Test
    public void testCommitsLotsaStuff() throws Exception {

        // create a work with 100 pages and commit it
        Work work = session.addWork();
        work.setTitle("Test Work");
        
        for (int i=0; i<100; i++) {
            Page p = work.addPage();
            Copy c = p.addCopy();
            File f = c.addFile();
            
            p.setSubUnitType("Page " + i);
            p.setAbstract("la de da de da");
            p.setAccessConditions("banned");
            p.setBibId("ba");
            p.setCreator("igor");
            p.setForm("over function");
            p.setDigitalStatus("digitised");
            
            c.setCopyRole("m");
            c.setBestCopy("yep");
            c.setCarrier("FedEx");
            c.setLocalSystemNumber("abcde");
            
            f.setDevice("meat bag");
            f.setCompression("yep");
            f.setSoftware("doom");
            f.setNotes("some notes notes notes");
            f.setBlobId(session.getBlobStore().begin().put(new java.io.File("/Users/scoen/Documents/xmaspartyelford/images/p1080013.jpg").toPath()).id());
            s("loopy...");
        }

        s("starting commit now ...");
        Long start = new Date().getTime();

        session.commit();
        
        Long end = new Date().getTime();
        s("Commit end time millis: " + (end-start));
    }

    @Ignore
    @Test
    public void testLoadJP2() throws Exception {

        Work work = session.addWork();
        work.setTitle("Test Image");
        
        Copy c = work.addCopy();
        File f = c.addFile();
            
        c.setCopyRole(CopyRole.ACCESS_COPY.code());
        f.setBlobId(session.getBlobStore().begin().put(new java.io.File("/Users/scoen/temp/eg.jp2").toPath()).id());

        s("BLOB ID is :" + f.getBlobId());
        
        s("starting commit now ...");
        Long start = new Date().getTime();

        session.commit();
        
        Long end = new Date().getTime();
        s("Commit end time millis: " + (end-start));
    }

    
    @Test
    public void testChangesSince() throws Exception {

        Long start = new Date().getTime();
        long hour = 1000 * 60 * 60;
        long since = start - (hour * 24 * 5);
        
        Map<Long, String> changed = session.getModifiedWorkIds(new Date(since));

        for (Long id : changed.keySet()) {
            s("id:" + id + " " + changed.get(id));
        }
        
        Long end = new Date().getTime();
        s("run time millis: " + (end-start));
    }

    
    private void s(String s) {
        System.out.println(s);
    }
}