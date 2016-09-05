package amberdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;

public class AmberDbTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Test
    public void testPersistence() throws IOException {
        Work w1, w2;
        Long sessId;
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().toPath()))) {
            w1 = db.addWork();
            sessId = db.suspend();
        }
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().toPath()), sessId)) {
            assertNotNull(db.findWork(w1.getId()));
            w2 = db.addWork();
            db.commit();
            db.close();
        }
        assertNotEquals(w1.getId(), w2.getId());
    }

    @Test
    public void testIngestBook() throws IOException {
        
        Path tmpFile = folder.newFile().toPath();
        Files.write(tmpFile, "Hello world".getBytes());

        
        AmberDb adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().toPath());
        
        Long sessId;
        Long bookId;
        try (AmberSession db = adb.begin()) {
            Work book = db.addWork();
            bookId = book.getId();
            book.setTitle("Test book");
            for (int i = 0; i < 10; i++) {
                book.addPage().addCopy(tmpFile, CopyRole.MASTER_COPY, "image/tiff");
            }
            sessId = db.suspend();
        }
        
        String line;
        try (AmberSession db = adb.resume(sessId)) {
            
            // now, can we retrieve the files ?
            Work book2 = db.findWork(bookId);
            
            Page p1 = book2.getPage(1);
            Copy c1 = p1.getCopy(CopyRole.MASTER_COPY);
            File f1 = c1.getFile();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(f1.openStream()));
            line = br.readLine();
            
            db.commit();
            db.close();
        }
        // next, persist the session (by closing it) open a new one and get the contents

        adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().toPath());
        try (AmberSession db = adb.begin()) {

            Work book2 = db.findWork(bookId);
            
            Page p1 = book2.getPage(1);
            Copy c1 = p1.getCopy(CopyRole.MASTER_COPY);
            File f1 = c1.getFile();

            BufferedReader br = new BufferedReader(new InputStreamReader(f1.openStream()));
            assertEquals(line, br.readLine());
            db.close();
        }
    }
 
    
    @Test
    public void testSuspendResume() throws IOException {
        
        AmberDb adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().toPath());
        
        Long sessId;
        Long bookId;
        
        Work book;
        
        try (AmberSession db = adb.begin()) {
            book = db.addWork();
            bookId = book.getId();
            book.setTitle("Test book");
            sessId = db.suspend();
        }

        AmberDb adb2 = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().toPath());
        try (AmberSession db = adb2.resume(sessId)) {
            
            // now, can we retrieve the files ?
            Work book2 = db.findWork(bookId);
            assertEquals(book, book2);
            db.close();
        }
    }
    
    
    void s(String s) {
    	System.out.println(s);
    }
}
