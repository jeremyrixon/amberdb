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
        @Property("metsId")
        public String getMetsId();
    
        @Property("metsId")
        public void setMetsId();

	@Adjacency(label = ExistsOn.label, direction = Direction.OUT)
	public Iterable<Page> getExistsOnPages();

	@Adjacency(label = ExistsOn.label, direction = Direction.OUT)
	public void addPage(final Page page);
	   
	@GremlinGroovy("it.outE.has('label', 'existsOn').has('relOrder', idx).inV")
	public Page getPage(@GremlinParam("idx") int idx);
	
	@GremlinGroovy("it.outE.has('label', 'existsOn').inV.loop(3){true}{true}.has('subType', subType)")
    public Iterable<Work> getLeafs(@GremlinParam("subType") String subType);
	   
	@GremlinGroovy(value="it.out('existsOn')[p-1].inE.id.toList()[0].toLong()", frame=false)
	public long getExistsOnRef(@GremlinParam("p") int position);
	
	@JavaHandler
	public int countExistsOns();
	
	abstract class Impl implements JavaHandlerContext<Vertex>, Section {
        public int countExistsOns() {
            return (existsOns() == null)? 0 : existsOns().size();
        }
	    
        private List<Edge> existsOns() {
            return (gremlin().outE(ExistsOn.label) == null)? null: gremlin().outE(ExistsOn.label).toList();
        }
	}
}
