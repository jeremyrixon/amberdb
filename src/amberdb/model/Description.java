package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.typedgraph.TypeField;

@TypeField("type")
public interface Description extends VertexFrame {
    @Property("type")
    public String getType();
}
