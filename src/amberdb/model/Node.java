package amberdb.model;

import amberdb.PIUtil;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;

@TypeField("type")
public interface Node extends VertexFrame {
	
	@JavaHandler
	abstract public long getId();
	
	@JavaHandler
	abstract public String getObjId();
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Node {

		@Override
		public long getId() {
			return toLong(asVertex().getId());
		}
		
		public long toLong(Object x) {
			// tingergraph converts ids to strings
			if (x instanceof String) {
				return Long.parseLong((String) x);
			}
			return (long)x; 
		}
		
		@Override
		public String getObjId() {
		    return PIUtil.format(getId());
		}
    }
}
