package amberdb;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.rules.TemporaryFolder;
import org.skife.jdbi.v2.DBI;

import com.google.common.collect.Lists;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import amberdb.enums.CopyRole;
import amberdb.enums.SubType;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;
import amberdb.sql.AmberProperty;
import amberdb.sql.DataType;
import amberdb.sql.dao.MigrationDao;

public class JellyTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private static Work workCollection;
    private static Work bookBlinkyBill;
    private static Work chapterBlinkyBill;
    private static Work workFrontCover;
    private static Work workTitlePage;
    private static Iterable<Copy> copies;
    private static Map<String, Object> expectedResults = new HashMap<String, Object>();
    
    // private static String dbUrl = "jdbc:mysql://amberserver:3306/dlir?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8";
    private static String dbUrl = "jdbc:mysql://snowy.nla.gov.au:3306/dlir?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8&relaxAutoCommit=true";
    private static String dbUser = "dlir";
    private static String dbPassword = "dlir";

    @BeforeClass
    public static void setup() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {
        String rootPath = ".";
        
        try (AmberSession db = AmberDbFactory.openAmberDb(dbUrl, dbUser, dbPassword, rootPath)) {
            if (AmberDbFactory.h2Test)
                setTestDataInH2(db);
                
            if (workCollection == null) workCollection = db.findWork(179720227L);
            if (bookBlinkyBill == null) bookBlinkyBill = db.findWork(179722129L);
            // chapterBlinkyBill = db.findWork(179);
            if (workFrontCover == null) workFrontCover = db.findWork(179722445L);
            copies = workFrontCover.getCopies();

            if (workTitlePage == null) workTitlePage = db.findWork(179722746L);
            
            if (!AmberDbFactory.h2Test) {
                expectedResults.put("workFrontCover_getSubType", SubType.PAGE.code());
                expectedResults.put("workFrontCover_getSubUnitType", "Front Cover");
                expectedResults.put("copy_carrier", "Online");
                expectedResults.put("workFrontCover_OCR_JSON_COPY_ID", 179722446L);
                expectedResults.put("workFrontCover_MASTER_COPY_ID", 179722448L);
                expectedResults.put("workFrontCover_ACCESS_COPY_ID", 179722450L);
                expectedResults.put("workFrontCover_OCR_JSON_COPY_FILE_ID", 179722447L);
                expectedResults.put("workFrontCover_MASTER_COPY_FILE_ID", 179722449L);
                
                expectedResults.put("workTitlePage_getSubType", SubType.PAGE.code());
                expectedResults.put("workTitlePage_getSubUnitType", "Title Page");
                expectedResults.put("workTitlePage_ACCESS_COPY_ID", 179722751L);
                
                expectedResults.put("workTitlePage_OCR_JSON_COPY_ID", 179722747L);
                expectedResults.put("workTitlePage_MASTER_COPY_ID", 179722749L);
            }
        }
    }
    
    /**
     * Test to demonstrate of converting image width and length from str to int for Jelly migration.
     */
    // @Test
    @Ignore
    public void testEncodeImageWidthAsInt() {
        try {
            MysqlDataSource mds = new MysqlDataSource();
            mds.setURL(dbUrl);
            mds.setUser(dbUser);
            mds.setPassword(dbPassword);
            
            DBI dbi = new DBI(mds);
            MigrationDao dao = dbi.open(MigrationDao.class);

            long blinkyBillId = 179722129L;
            List<AmberProperty> rs = dao.getPropertiesForWorkDetails(blinkyBillId, "imageWidth");
            if (rs != null) {
                for (AmberProperty p : rs) {
                    System.out.println("id:" + p.getId() + ", name:" + p.getName() + ", type:" + p.getType() + ", value:" + p.getValue());
                    Integer width = Integer.parseInt(p.getValue().toString());
                    AmberProperty np = new AmberProperty(p.getId(), p.getName(), DataType.INT, width);
                    dao.updProperty(np);
                }
            }
            
            List<AmberProperty> rs1 = dao.getPropertiesForWorkDetails(blinkyBillId, "imageLength");
            if (rs != null) {
                for (AmberProperty p : rs1) {
                    System.out.println("id:" + p.getId() + ", name:" + p.getName() + ", type:" + p.getType() + ", value:" + p.getValue());
                    Integer length = Integer.parseInt(p.getValue().toString());
                    AmberProperty np = new AmberProperty(p.getId(), p.getName(), DataType.INT, length);
                    dao.updProperty(np);
                }
            }  

            dao.commit();
            dbi.close(dao); 
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    // @Test
    @Ignore
    public void testPortBlinkyProperties() {
        try {
            String amberUrl = "jdbc:mysql://mysql-devel.nla.gov.au:6446/amberdb?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8&relaxAutoCommit=true";
            String amberUser = "amberdb";
            String amberPassword = "amberdb";
            
            MysqlDataSource jellyDS = new MysqlDataSource();
            jellyDS.setURL(dbUrl);
            jellyDS.setUser(dbUser);
            jellyDS.setPassword(dbPassword);
            
            MysqlDataSource amberDS = new MysqlDataSource();
            amberDS.setURL(amberUrl);
            amberDS.setUser(amberUser);
            amberDS.setPassword(amberPassword);
            
            DBI jellyDBI = new DBI(jellyDS);
            DBI amberDBI = new DBI(amberDS);
            
            MigrationDao fromDao = jellyDBI.open(MigrationDao.class);
            MigrationDao toDao = amberDBI.open(MigrationDao.class);
            
            long blinkyBillId = 179722129L;
            List<AmberProperty> rs = fromDao.getPropertiesForWorkDetails(blinkyBillId);
            if (rs != null) {
                for (AmberProperty p : rs) {
                    System.out.println("id:" + p.getId() + ", name:" + p.getName() + ", type:" + p.getType() + ", value:" + p.getValue());
                    AmberProperty np = new AmberProperty(p.getId(), p.getName(), p.getType(), p.getValue());
                    toDao.updProperty(np);
                }
            }
            
            toDao.commit();
            jellyDBI.close(fromDao);
            amberDBI.close(toDao);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    

    // @Test
    @Ignore
    public void testEncodeFileSizeAsLongInAmber() {
        String amberUrl = "jdbc:mysql://mysql-devel.nla.gov.au:6446/amberdb?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8&relaxAutoCommit=true";
        String amberUser = "amberdb";
        String amberPassword = "amberdb";
        
        MysqlDataSource amberDS = new MysqlDataSource();
        amberDS.setURL(amberUrl);
        amberDS.setUser(amberUser);
        amberDS.setPassword(amberPassword);
        DBI amberDBI = new DBI(amberDS);
        encodeFileSizeAsLong(amberDBI);
    }
    
    @Ignore
    public void testEncodeFileSizeAsLongInJelly() {
        MysqlDataSource jellyDS = new MysqlDataSource();
        jellyDS.setURL(dbUrl);
        jellyDS.setUser(dbUser);
        jellyDS.setPassword(dbPassword);
        DBI jellyDBI = new DBI(jellyDS);
        encodeFileSizeAsLong(jellyDBI);
    }
    
    private void encodeFileSizeAsLong(DBI db) {
        MigrationDao dao = db.open(MigrationDao.class);

        List<AmberProperty> rs = dao.getPropertiesOfName("fileSize");
        if (rs != null) {
            for (AmberProperty p : rs) {
                System.out.println("id:" + p.getId() + ", name:" + p.getName() + ", type:" + p.getType() + ", value:" + p.getValue());
                Long width = Long.parseLong(p.getValue().toString());
                AmberProperty np = new AmberProperty(p.getId(), p.getName(), DataType.LNG, width);
                dao.updProperty(np);
            }
        }
        dao.commit();
        db.close(dao);
    }

    // @Test
    @Ignore
    public void testWorkStructure() {
        // Test retrieving pages
        ArrayList<Page> pages = Lists.newArrayList(bookBlinkyBill.getPages());
        assertEquals(3, pages.size());
        
        // Test retrieving copies
        ArrayList<Copy> copies = Lists.newArrayList(workFrontCover.getCopies());
        assertEquals(3, copies.size());
        
        // Test retrieving files
        ArrayList<File> files = Lists.newArrayList(workFrontCover.getCopy(CopyRole.ACCESS_COPY).getFiles());
        assertEquals(1, files.size());
    }

    // @Test
    @Ignore
    public void testWorkProperties() {
        assertEquals(SubType.PAGE.code(), workFrontCover.getSubType());
        assertEquals("Front Cover", workFrontCover.getSubUnitType());

        for (Copy copy : copies) {
            System.out.println("copy id : " + copy.getId());
            assertEquals("Online", copy.getCarrier());
            System.out.println("copy role : " + copy.getCopyRole());
        }

        assertEquals(expectedResults.get("workFrontCover_OCR_JSON_COPY_ID"), workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getId());
        assertEquals(expectedResults.get("workFrontCover_MASTER_COPY_ID"), workFrontCover.getCopy(CopyRole.MASTER_COPY).getId());
        assertEquals(expectedResults.get("workFrontCover_ACCESS_COPY_ID"), workFrontCover.getCopy(CopyRole.ACCESS_COPY).getId());

        assertEquals(expectedResults.get("workFrontCover_OCR_JSON_COPY_FILE_ID"), workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getFile().getId());
        assertEquals(expectedResults.get("workFrontCover_MASTER_COPY_FILE_ID"), workFrontCover.getCopy(CopyRole.MASTER_COPY).getFile().getId());

        assertEquals(expectedResults.get("workTitlePage_getSubType"), workTitlePage.getSubType());
        assertEquals(expectedResults.get("workTitlePage_getSubUnitType"), workTitlePage.getSubUnitType());
        Copy titlePageAc = workTitlePage.getCopy(CopyRole.ACCESS_COPY);
        System.out.println("title page copy ac id: " + titlePageAc.getId());
        assertNotNull(titlePageAc);
        assertEquals(expectedResults.get("workTitlePage_ACCESS_COPY_ID"), titlePageAc.getId());

        Iterable<Copy> titlePageCopies = workTitlePage.getCopies();
        for (Copy _copy : titlePageCopies) {
            assertEquals("Online", _copy.getCarrier());
        }

        assertEquals(expectedResults.get("workTitlePage_OCR_JSON_COPY_ID"), workTitlePage.getCopy(CopyRole.OCR_JSON_COPY).getId());
        assertEquals(expectedResults.get("workTitlePage_MASTER_COPY_ID"), workTitlePage.getCopy(CopyRole.MASTER_COPY).getId());
        assertEquals(expectedResults.get("workTitlePage_ACCESS_COPY_ID"), workTitlePage.getCopy(CopyRole.ACCESS_COPY).getId());
    }

    @AfterClass
    public static void teardown() throws IOException {
        workFrontCover = null;
    }

    private static void setTestDataInH2(AmberSession sess) {
        workCollection = sess.addWork();
        
        workCollection.setSubType("title");
        workCollection.setTitle("nla.books");
        
        bookBlinkyBill = sess.addWork();
        bookBlinkyBill.setSubType(SubType.BOOK.code());
        bookBlinkyBill.setTitle("Blinky Bill");
        workCollection.addChild(bookBlinkyBill);
        
        chapterBlinkyBill = sess.addWork();
        chapterBlinkyBill.setSubType(SubType.CHAPTER.code());
        chapterBlinkyBill.setTitle("Blinky Bill chapter 1");
        bookBlinkyBill.addChild(chapterBlinkyBill);
        
        workFrontCover = bookBlinkyBill.addPage();
        workTitlePage = bookBlinkyBill.addPage();
        workFrontCover.setSubType(SubType.PAGE.code());
        workFrontCover.setTitle("Blinky Bill Page 1");
        workFrontCover.setSubUnitType("Front Cover");
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setTitle("Blinky Bill Page 2");
        workTitlePage.setSubType(SubType.PAGE.code());
        workTitlePage.setSubUnitType("Title Page");
        
        Copy workFrontCoverMasterCopy = workFrontCover.addCopy();
        Copy workFrontCoverOCRJsonCopy = workFrontCover.addCopy();
        Copy workFrontCoverAccessCopy = workFrontCover.addCopy();
        workFrontCoverMasterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        workFrontCoverMasterCopy.setCarrier("Online");
        workFrontCoverOCRJsonCopy.setCopyRole(CopyRole.OCR_JSON_COPY.code());
        workFrontCoverOCRJsonCopy.setCarrier("Online");
        workFrontCoverAccessCopy.setCopyRole(CopyRole.ACCESS_COPY.code());
        workFrontCoverAccessCopy.setCarrier("Online");
        
        Copy workTitlePageMasterCopy = workTitlePage.addCopy();
        Copy workTitlePageOCRJsonCopy = workTitlePage.addCopy();
        Copy workTitlePageAccessCopy = workTitlePage.addCopy();
        workTitlePageMasterCopy.setCopyRole(CopyRole.MASTER_COPY.code());
        workTitlePageMasterCopy.setCarrier("Online");
        workTitlePageOCRJsonCopy.setCopyRole(CopyRole.OCR_JSON_COPY.code());
        workTitlePageOCRJsonCopy.setCarrier("Online");
        workTitlePageAccessCopy.setCopyRole(CopyRole.ACCESS_COPY.code());
        workTitlePageAccessCopy.setCarrier("Online");
        
        File workFrontCoverMasterCopyFile = workFrontCoverMasterCopy.addFile();
        File workFrontCoverOCRJsonCopyFile = workFrontCoverOCRJsonCopy.addFile();
        File workFrontCoverAccessCopyFile = workFrontCoverAccessCopy.addFile();
        
        expectedResults.put("workFrontCover_getSubType", workFrontCover.getSubType());
        expectedResults.put("workFrontCover_getSubUnitType", workFrontCover.getSubUnitType());
        expectedResults.put("copy_carrier", workFrontCoverAccessCopy.getCarrier());
        expectedResults.put("workFrontCover_OCR_JSON_COPY_ID", workFrontCoverOCRJsonCopy.getId());
        expectedResults.put("workFrontCover_MASTER_COPY_ID", workFrontCoverMasterCopy.getId());
        expectedResults.put("workFrontCover_ACCESS_COPY_ID", workFrontCoverAccessCopy.getId());
        expectedResults.put("workFrontCover_OCR_JSON_COPY_FILE_ID", workFrontCoverOCRJsonCopyFile.getId());
        expectedResults.put("workFrontCover_MASTER_COPY_FILE_ID", workFrontCoverMasterCopyFile.getId());
        expectedResults.put("workFrontCover_ACCESS_COPY_FILE_ID", workFrontCoverAccessCopyFile.getId());
        
        expectedResults.put("workTitlePage_getSubType", workTitlePage.getSubType());
        expectedResults.put("workTitlePage_getSubUnitType", workTitlePage.getSubUnitType());
        expectedResults.put("workTitlePage_ACCESS_COPY_ID", workTitlePageAccessCopy.getId());
        
        expectedResults.put("workTitlePage_OCR_JSON_COPY_ID", workTitlePageOCRJsonCopy.getId());
        expectedResults.put("workTitlePage_MASTER_COPY_ID", workTitlePageMasterCopy.getId());
    }
}
