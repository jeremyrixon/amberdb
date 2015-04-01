package amberdb.model;

import java.util.List;

import amberdb.relation.ExistsOn;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
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
        public void setMetsId(String metsId);

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

	@Property("abstract")
	public String getAbstract();

	@Property("abstract")
	public void setAbstract(String abstractText);

	@Property("captions")
	public List<String> getCaptions();

	@Property("captions")
	public void setCaptions(List<String> captions);

	@Property("advertising")
	public boolean hasAdvertising();

	@Property("advertising")
	public void setAdvertising(boolean advertising);

	@Property("illustrated")
	public boolean isIllustrated();

	@Property("illustrated")
	public void setIllustrated(boolean illustrated);
	
	abstract class Impl implements JavaHandlerContext<Vertex>, Section {
        public int countExistsOns() {
            return (existsOns() == null)? 0 : existsOns().size();
        }
	    
        private List<Edge> existsOns() {
            return (gremlin().outE(ExistsOn.label) == null)? null: gremlin().outE(ExistsOn.label).toList();
        }
	}
}
