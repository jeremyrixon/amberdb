package amberdb;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sql.DataSource;

public class AmberDb {
    final private DataSource dataSource;
    final private Path rootPath;
    final private Path sessionsPath;
    
    
    public AmberDb(DataSource dataSource, Path rootPath) {
        this.dataSource = dataSource;
        this.rootPath = rootPath;
        sessionsPath = rootPath.resolve("sessions");
        try {
            Files.createDirectories(sessionsPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    AmberSession begin() {        
        for (long sessionId = 0;; sessionId++) {
            Path sessionPath = sessionsPath.resolve(Long.toString(sessionId));
            try {
                Files.createDirectory(sessionPath);
            } catch (FileAlreadyExistsException e) {
                // try again
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new AmberSession(dataSource, rootPath, sessionPath, sessionId);
        }        
    }
    
    AmberSession resume(long sessionId) {
        Path sessionPath = sessionsPath.resolve(Long.toString(sessionId));
        return new AmberSession(dataSource, rootPath, sessionPath, sessionId);
    }
}
