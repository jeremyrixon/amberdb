package amberdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;

import amberdb.model.Work;
import amberdb.sql.AmberGraph;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public class AmberDb implements AutoCloseable {
	//final FramedGraph<TinkerGraph> graph;
	final FramedGraph<AmberGraph> graph;
	final Sequence objectIdSeq = new Sequence();
	final Path dataPath;

	/**
	 * Constructs an in-memory AmberDb for testing with.
	 */
	public AmberDb() {
	    
	    DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:mem:","fish","fish");
        DataSource persistDs = JdbcConnectionPool.create("jdbc:h2:~/h2-test-persist","pers","pers");
	    
	    //graph = openGraph(new TinkerGraph());
        AmberGraph amber = new AmberGraph(sessionDs, persistDs, "test");
        amber.createPersistentDataStore();
		graph = openGraph(amber);
		dataPath = null;
	}

	/**
	 * Constructs an AmberDb stored on the local filesystem.
	 */
	public AmberDb(Path dataPath) throws IOException {
		Files.createDirectories(dataPath);
        DataSource sessionDs = JdbcConnectionPool.create("jdbc:h2:"+dataPath+"-sess","fish","fish");
        DataSource persistDs = JdbcConnectionPool.create("jdbc:h2:"+dataPath+"-pers","pers","pers");

        s("sess db at : "+ dataPath+"-sess");
        s("pers db at : "+ dataPath+"-pers");
        
        //graph = openGraph(new TinkerGraph(dataPath.resolve("graph").toString(), TinkerGraph.FileType.GML));
        AmberGraph amber = new AmberGraph(sessionDs, persistDs, "test");
        amber.createPersistentDataStore();
        graph = openGraph(amber);
		this.dataPath = dataPath;
		try {
			objectIdSeq.load(dataPath.resolve("objectIdSeq"));
		} catch (NoSuchFileException e) {
			// first run
		}
	}

	public void s(String s) {
	    System.out.println(s);
	}
	
 //   private static FramedGraph<TinkerGraph> openGraph(TinkerGraph tinker) {
	private static FramedGraph<AmberGraph> openGraph(AmberGraph tinker) {
		return new FramedGraphFactory(
				new JavaHandlerModule(),
				new GremlinGroovyModule())
		.create(tinker);
	}
	
	/**
	 * Finds a work by id.
	 */
	public Work findWork(long objectId) {
		return graph.getVertex(objectId, Work.class);
	}

	/**
	 * Creates a new work.
	 * 
	 * @return the work
	 */
	public Work addWork() {
		return graph.addVertex(objectIdSeq.next(), Work.class);
	}

	@Override
	public void close() throws IOException {
		graph.shutdown();
		if (dataPath != null) {
			objectIdSeq.save(dataPath.resolve("objectIdSeq"));
		}
	}

	private static class Sequence {
		final AtomicLong value = new AtomicLong();

		void load(Path file) throws IOException {
			byte[] b = Files.readAllBytes(file);
			value.set(Long.valueOf(new String(b)));
		}

		void save(Path file) throws IOException {
			Files.write(file, Long.toString(value.get()).getBytes());
		}

		long next() {
			return value.incrementAndGet();
		}
	}

}
