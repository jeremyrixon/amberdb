package amberdb.model;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

public interface Page extends Work {

    @JavaHandler
    public void addImageCopy(java.io.File file);
    
    @JavaHandler
    public void addOCRCopy(java.io.File file);
    
    @JavaHandler
    public void rotate(int degree);
    
    @JavaHandler
    public void crop(int startX, int startY, int height, int width);
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Section {
        public void addImageCopy(java.io.File file) {
            
        }
        
        public void addOCRCopy(java.io.File file) {
            
        }
        
        public void rotate(int degree) {
            // TODO
        }
        
        public void crop(int startX, int startY, int height, int width) {
            // TODO
        }
    }
}
