package amberdb.model;

import java.util.List;

import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Page")
public interface Page extends Work {
    @Property("device")
    public String getDevice();
    
    @Property("device")
    public void setDevice(String device);
    
    @Property("software")
    public String getSoftware();
    
    @Property("software")
    public void setSoftware(String software);
    
    @JavaHandler
    public void rotate(int degree);

    @JavaHandler
    public void crop(int startX, int startY, int height, int width);
    
    @JavaHandler
    public void setOrder(int position);
    
    @Incidence(label = IsPartOf.label)
    public Iterable<IsPartOf> getParentEdges();

    abstract class Impl implements JavaHandlerContext<Vertex>, Page {
        public void rotate(int degree) {
            // TODO
        }

        public void crop(int startX, int startY, int height, int width) {
            // TODO
        }
        
        @Override
        public void setOrder(int position) {
            getParentEdges().iterator().next().setRelOrder(position);
        }
    }
}
