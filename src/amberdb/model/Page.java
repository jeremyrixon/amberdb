package amberdb.model;

import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Page")
public interface Page extends Work {

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
