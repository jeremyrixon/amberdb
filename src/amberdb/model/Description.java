package amberdb.model;

import amberdb.PIUtil;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberTransaction;
import amberdb.graph.AmberVertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedVertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@TypeField("type")
@TypeValue("Description")
public interface Description extends VertexFrame{
    @Property("type")
    public String getType();

    // TODO amuller This may need to be implemented to return the set of properties that cannot be set.
    // I haven't done this because I couldn't think of a reasonable way of doing this that did not involve
    // reflection and messyness.
    /**
     * Get the set of keys for properties that are set on this object.
     *
     * This does not return the set of properties that can be set on this model.
     *
     * @return the keyset of properties currently set on this object.
     */
    @JavaHandler
    public Set<String> getPropertyKeySet();

    public abstract class Impl implements JavaHandlerContext<Vertex>, Description {
        @Override
        public Set<String> getPropertyKeySet() {
            return this.asVertex().getPropertyKeys();
        }
    }
}
