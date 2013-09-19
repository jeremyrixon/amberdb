package amberdb.model;

import amberdb.relation.ExistsOn;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * A section (article, chapter etc) of a printed work. May exist on multiple
 * pages and may span multiple pages.
 */
@TypeValue("Section")
public interface Section extends Work {

	@Adjacency(label = ExistsOn.label)
	public Iterable<Page> getPages();

	@Adjacency(label = ExistsOn.label)
	public void addPage(final Page page);
	
	@GremlinGroovy("it.inE('isPartOf').has('relOrder', idx).outV")
	public Page getPage(@GremlinParam("idx") int idx);
	
	@JavaHandler
	public void swapPages(int page1, int page2);

	abstract class Impl implements JavaHandlerContext<Vertex>, Section {
	    public void swapPages(int page1, int page2) {
	        // TODO
	    }
	}
}
