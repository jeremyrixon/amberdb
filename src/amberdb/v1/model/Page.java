package amberdb.v1.model;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import amberdb.v1.relation.ExistsOn;

@TypeValue("Page")
public interface Page extends Work {

    @Adjacency(label = ExistsOn.label, direction = Direction.IN)
    public Iterable<Section> getSections();
}
