package amberdb.model;

import java.util.List;

import amberdb.relation.ExistsOn;
import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
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
public interface Section extends CompositeWork {
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Iterable<Page> getAddedPages();

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void addPage(final Page page);
    
	@Adjacency(label = ExistsOn.label, direction = Direction.IN)
	public Iterable<Page> getAttachedPages();

	@Adjacency(label = ExistsOn.label, direction = Direction.IN)
	public void attachPage(final Page page);
	
	@GremlinGroovy("it.inE('isPartOf').has('relOrder', idx).outV")
	public Page getAddedPage(@GremlinParam("idx") int idx);
	   
	@GremlinGroovy("it.inE('existsOn').has('relOrder', idx).outV")
	public Page getAttachedPage(@GremlinParam("idx") int idx);
	   
	@GremlinGroovy(value="it.in('existsOn')[p-1].outE.id.toList()[0].toLong()", frame=false)
	public long getExistsOnRef(@GremlinParam("p") int position);

	@JavaHandler
	public void swapPages(int page1, int page2) throws IllegalArgumentException;

	@JavaHandler
	public void swapPagesAttached(int page1, int page2) throws IllegalArgumentException;
	
	@JavaHandler
	public int countExistsOns();
	
	abstract class Impl implements JavaHandlerContext<Vertex>, Section {
	    public void swapPages(int page1, int page2) throws IllegalArgumentException {
	        // address swap added pages e.g. for books
	        CompositeWork work = frame(this.asVertex(), CompositeWork.class);
	        work.swapParts(page1, page2);
	    }
	    
	    public void swapPagesAttached(int page1, int page2) throws IllegalArgumentException {
            // address swap attached pages e.g. for articles
            ExistsOn on1 = getExistsOn(page1);
            ExistsOn on2 = getExistsOn(page2);
            swapParts(on1, on2);
	    }
	    
        public int countExistsOns() {
            return (existsOns() == null)? 0 : existsOns().size();
        }
        
	    public ExistsOn getExistsOn(int position) {
            if (existsOns() != null && existsOns().size() >= position)
                return frame(existsOns().get(position - 1), ExistsOn.class);
            return null;
	    }
	    
        private List<Edge> existsOns() {
            return (gremlin().inE(ExistsOn.label) == null)? null: gremlin().outE(ExistsOn.label).toList();
        }
	}
}
