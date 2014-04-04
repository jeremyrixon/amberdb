package amberdb.model;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Page")
public interface Page extends Work {

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
