package amberdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Work;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public class AmberDb implements AutoCloseable {
	final FramedGraph<TinkerGraph> graph;
	final Sequence objectIdSeq = new Sequence();
	final Path dataPath;

	/**
	 * Constructs an in-memory AmberDb for testing with.
	 */
	public AmberDb() {
		graph = openGraph(new TinkerGraph());
		dataPath = null;
	}

	/**
	 * Constructs an AmberDb stored on the local filesystem.
	 */
	public AmberDb(Path dataPath) throws IOException {
		Files.createDirectories(dataPath);
		graph = openGraph(new TinkerGraph(dataPath.resolve("graph").toString(), TinkerGraph.FileType.GML));
		this.dataPath = dataPath;
		try {
			objectIdSeq.load(dataPath.resolve("objectIdSeq"));
		} catch (NoSuchFileException e) {
			// first run
		}
	}

	private static FramedGraph<TinkerGraph> openGraph(TinkerGraph tinker) {
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
	
	/**
	 * Creates a new work with specified metadata
	 */
	public Work addWork(Map<String, String> metadata) {
	    Work item = addWork();
	    updWork(item, metadata);
	    return item;
	}
	
	/**
	 * Update an existing work with specified properties
	 */
	public void updWork(Work item, Map<String, String> metadata) {
	    if (metadata == null)
	        throw new IllegalArgumentException("Cannot update work as input metadata is null.");
	    for (String field : metadata.keySet()) {
	        item.asVertex().setProperty(field, metadata.get(field));
	    }
	}
	
	/**
	 * Create a new groupItem (work) with the specified metadata, and  map a list of files (e.g. masters 
	 * or derivatives) in the structmap of the groupItem,  according to the map strategy specified.  
	 * 
	 * e.g. WorkFileMapStrategy will create a corresponding work item for each of the file, and 
	 *      place each of the created work item as the direct child of the groupItem.
	 *      
	 * @param metadata: the metadata for the grouping Item
	 * @param fileLocations: the file paths of the files to be mapped
	 * @param strategy: the strategy to group and map the list of files into a struct map under the grouped item.
	 * @return The list of corresponding work item created for each of the files from the input fileLocations list.
	 */
	public List<Work> map(Map<String, String> metadata, List<String> fileLocations, MapStrategy strategy) {
	    Work groupItem = addWork();
	    updWork(groupItem, metadata);
	    return map(groupItem, fileLocations, strategy);
	}
	
	/**
	 * Given an existing groupItem, map a list of files (e.g. masters or derivatives) in the structmap of
	 * the groupItem, according to the map strategy specified.
	 * 
	 * @param groupItem: the existing groupItem in the repository to be mapped to.
	 * @param fileLocations: the file paths of the files to be mapped
	 * @param strategy: the strategy to group and map the list of files into a struct map under the grouped item.
	 * @return The corresponding work item created for each of the files from the input fileLocations list.
	 */
	public List<Work> map(Work groupItem, List<String> fileLocations, MapStrategy strategy) {
	    List<Copy> copies = map(fileLocations);
	    strategy.setDAO(this);
	    return strategy.map(groupItem, copies);
	}

	@Override
	public void close() throws IOException {
		graph.shutdown();
		if (dataPath != null) {
			objectIdSeq.save(dataPath.resolve("objectIdSeq"));
		}
	}
	
	protected List<Copy> map(List<String> fileLocations) {
	   if (fileLocations == null)
	       throw new IllegalArgumentException("Cannot map to work as input fileLocations is null.");
	       
	    List<File> files = new ArrayList<File>();
	    List<Copy> copies = new ArrayList<Copy>();
	    for (String fileLocation : fileLocations) {
	       File file = addFile(); 
	       file.setFileLocation(fileLocation);
           files.add(file);
	       
	       Copy copy = addCopy();
	       copy.addFile(file);
	       copies.add(copy);
	    }
	    return copies;
	}
	
	/**
	 * Create a new copy
	 */
	protected Copy addCopy() {
	    return graph.addVertex(objectIdSeq.next(), Copy.class);
	}
	
	/**
     * Create a new file
     */
    protected File addFile() {
        return graph.addVertex(objectIdSeq.next(), File.class);
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
