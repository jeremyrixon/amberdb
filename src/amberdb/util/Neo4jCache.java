package amberdb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jCache {
    private final GraphDatabaseService graphDb;
    
    public Neo4jCache(String dbPath) throws IOException {
        deleteFileOrDirectory(Paths.get(dbPath).toFile());
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
        registerShutdownHook(graphDb);
    }
    
    public GraphDatabaseService graph() {
        return graphDb;
    }
    
    private void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
    
    private void deleteFileOrDirectory(File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteFileOrDirectory(child);
            }
        }
        Files.deleteIfExists(file.toPath());
    }
}
