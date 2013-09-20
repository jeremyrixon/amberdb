package amberdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Section;
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
	
	/**
	 * commit saves everything in the current transaction.
	 */
	public void commit() {
	    //TODO: saves everything in the current transaction. 
	}
	
	/**
     * rollback rollback everything in the current transaction.
     */
    public void rollback() {
        //TODO: saves everything in the current transaction. 
    }

	protected static FramedGraph<TinkerGraph> openGraph(TinkerGraph tinker) {
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
     * Finds a section by id.
     */
    public Section findSection(long objectId) {
        return graph.getVertex(objectId, Section.class);
    }
    
    /**
     * Finds a section by Bib id.
     */
    public Section findSectionByVn(long vnLink) {
        // TODO: to be changed later
        return graph.getVertex(vnLink, Section.class);
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
     * Create a new section.
     */
    public Section addSection() {
        return graph.addVertex(objectIdSeq.next(), Section.class);
    }
    
    /**
     * Todo: this need to be removed later.
     * Create a new section.
     */
    public Section addSection(long vnNumber) {
        return graph.addVertex(vnNumber, Section.class);
    }
    
    /**
     * Create a new section within the input work.
     * 
     * @return
     */
    public Section addSectionTo(Work work) {
        Section section = graph.addVertex(objectIdSeq.next(), Section.class);
        work.addChild(section);
        return section;
    }
    
	/**
	 * Create a new page.
	 * 
	 * @return
	 */
	public Page addPageTo(Section section) {
	    Page page = graph.addVertex(objectIdSeq.next(), Page.class);
	    section.addChild(page);
	    return page;
	}
	
	/**
	 * Create a new page item for the input file.
	 * @param file
	 * @return
	 */
	public Page addPageTo(Section section, java.io.File file) {
	    Page page = addPageTo(section);
	    addCopyTo(page, file, Copy.Role.MASTER_COPY.code());
	    return page;
	}

	/**
     * Create a new image tiff master copy
     * 
     * Note: most often the work is a page, but it doesn't have to be a page,
     *       it can be book, or other groupItem as well.
     */
    public void addImageTiffCopyTo(Work work, java.io.File file) {
        addCopyTo(work, file, Copy.Role.MASTER_COPY.code());
    }
    
    /**
     * Create a new image jp2 access copy
     * 
     * Note: most often the work is a page, but it doesn't have to be a page,
     *       it can be book, or other groupItem as well.
     */
    public void addImageJP2CopyTo(Work work, java.io.File file) {
        addCopyTo(work, file, Copy.Role.ACCESS_COPY.code());
    }
    
    /**
     * Create a new image ocr mets master copy
     * 
     * Note: most often the work is a page, but it doesn't have to be a page,
     *       it can be book, or other groupItem as well.
     */
    public void addOCRMETSCopyTo(Work work, java.io.File file) {
        addCopyTo(work, file, Copy.Role.OCR_METS_COPY.code());
    }

    /**
     * Create a new image ocr alto master copy
     * 
     * Note: most often the work is a page, but it doesn't have to be a page,
     *       it can be book, or other groupItem as well.
     */
    public void addOCRALTOCopyTo(Work work, java.io.File file) {
        addCopyTo(work, file, Copy.Role.OCR_ALTO_COPY.code());
    }
    
    /**
     * Create a new image ocr json derivative copy
     * 
     * Note: most often the work is a page, but it doesn't have to be a page,
     *       it can be book, or other groupItem as well.
     */
    public void addOCRJSONCopyTo(Work work, java.io.File file) {
        addCopyTo(work, file, Copy.Role.OCR_JSON_COPY.code());
    }
    
	@Override
	public void close() throws IOException {
		graph.shutdown();
		if (dataPath != null) {
			objectIdSeq.save(dataPath.resolve("objectIdSeq"));
		}
	}
	
	/**
	 * Suspend suspends the current transaction.
	 * @return the id of the current transaction.
	 */
	public int suspend() {
	    // TODO 
	    return -1;
	}
	
	/**
	 * Recover recovers the suspended transaction of the specified txId.
	 * @param txId the transaction id of the suspended transaction to reover.
	 */
	public void recover(int txId) {
	    // TODO
	}	

    /**
     * Create a new copy
     * @param work - the work to add the copy to.
     * @param file - the file to attach to the copy.
     * @param copyRole - the type of the copy.  e.g. master, access copy, etc.
     */
    protected void addCopyTo(Work work, java.io.File file, String copyRole) {
        if (work == null)
            throw new IllegalArgumentException("Cannot add copy as input work is null.");
        
        if (file == null)
            throw new IllegalArgumentException("Cannot add copy as input file is null.");
        
        Copy copy = graph.addVertex(objectIdSeq.next(), Copy.class);
        copy.setCopyRole(copyRole);
        work.addCopy(copy);
        
        File _file = addFileTo(copy);
        _file.setLocation(file.getAbsolutePath());
        copy.addFile(_file);
    }
	
	/**
     * Create a new file
     */
    protected File addFileTo(Copy copy) {
        File file = graph.addVertex(objectIdSeq.next(), File.class);
        copy.addFile(file);
        return file;
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
