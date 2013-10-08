package amberdb.model;

import java.util.List;

import amberdb.relation.ExistsOn;
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
public interface Section extends Work {
	@Adjacency(label = ExistsOn.label, direction = Direction.IN)
	public Iterable<Page> getExistsOnPages();

	@Adjacency(label = ExistsOn.label, direction = Direction.IN)
	public void addPage(final Page page);
	   
	@GremlinGroovy("it.inE('existsOn').has('relOrder', idx).outV")
	public Page getPage(@GremlinParam("idx") int idx);
	   
	@GremlinGroovy(value="it.in('existsOn')[p-1].outE.id.toList()[0].toLong()", frame=false)
	public long getExistsOnRef(@GremlinParam("p") int position);
	
	@JavaHandler
	public int countExistsOns();
	
	abstract class Impl implements JavaHandlerContext<Vertex>, Section {
        public int countExistsOns() {
            return (existsOns() == null)? 0 : existsOns().size();
        }
	    
        private List<Edge> existsOns() {
            return (gremlin().inE(ExistsOn.label) == null)? null: gremlin().outE(ExistsOn.label).toList();
        }
	}
}
