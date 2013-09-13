package amberdb.model;

import amberdb.relation.ExistsOn;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * A section (article, chapter etc) of a printed work. May exist on multiple
 * pages and may span multiple pages.
 */
@TypeValue("Section")
public interface Section extends Work {

	@Adjacency(label = ExistsOn.label)
	public Iterable<Work> getPages();

	@Adjacency(label = ExistsOn.label)
	public void addPage(final Work page);

	abstract class Impl implements JavaHandlerContext<Vertex>, Section {
	}
}
