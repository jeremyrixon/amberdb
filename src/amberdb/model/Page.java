package amberdb.model;

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

    abstract class Impl implements JavaHandlerContext<Vertex>, Page {
        public void rotate(int degree) {
            // TODO
        }

        public void crop(int startX, int startY, int height, int width) {
            // TODO
        }
    }
}
