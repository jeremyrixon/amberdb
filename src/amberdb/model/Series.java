package amberdb.model;

import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;

public interface Series extends Work {
    @Property("eadUpdateReviewRequired")
    public String getEADUpdateReviewRequired();
    
    @Property("eadUpdateReviewRequired")
    public void setEADUpdateReviewRequired(String eadUpdateReviewRequired);
    
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Folder addFolder();
}
