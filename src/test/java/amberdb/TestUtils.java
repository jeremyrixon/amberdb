package amberdb;

import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    
    public static Path newDummyFile(TemporaryFolder folder, String name) throws IOException {
        Path path = folder.newFile(name).toPath();
        Files.write(path, "Hello world\n".getBytes());
        return path;
    }

}
