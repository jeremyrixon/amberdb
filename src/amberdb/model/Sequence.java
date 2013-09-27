package amberdb.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

public class Sequence {
    final static AtomicLong value = new AtomicLong();

    protected void load(Path file) throws IOException {
        byte[] b = Files.readAllBytes(file);
        value.set(Long.valueOf(new String(b)));
    }

    protected void save(Path file) throws IOException {
        Files.write(file, Long.toString(value.get()).getBytes());
    }

    protected static long next() {
        return value.incrementAndGet();
    }
}
