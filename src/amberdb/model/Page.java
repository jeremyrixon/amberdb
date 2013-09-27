package amberdb.model;

import java.util.List;

import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

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
    public void setOrderInWork(Work work, int position);

    abstract class Impl implements JavaHandlerContext<Vertex>, Page {
        public void rotate(int degree) {
            // TODO
        }

        public void crop(int startX, int startY, int height, int width) {
            // TODO
        }
        
        public void setOrderInWork(Work work, int position) {
            List<Edge> parents = parents();
            for (Edge parent : parents) {
                IsPartOf edge = frame(parent, IsPartOf.class);
                if (edge.getSource() == work) {
                    edge.setRelOrder(position);
                }
            }
        }
        
        private List<Edge> parents() {
            return (gremlin().outE(IsPartOf.label) == null)? null: gremlin().inE(IsPartOf.label).toList();            
        }
    }
}
