package amberdb.model;

import java.util.List;
import amberdb.relation.IsPartOf;
import amberdb.relation.Relation;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

public interface CompositeWork extends Work {    
    @GremlinGroovy(value="it.inE('isPartOf')[p-1].id.toList()[0].toLong()", frame=false)
    public long getIsPartOfRef(@GremlinParam("p") int position);
    
    @JavaHandler
    public void swapParts(int part1, int part2) throws IllegalArgumentException;
    
    @JavaHandler
    public void swapParts(Relation part1Ref, Relation part2Ref);
    
    @JavaHandler
    public int countParts();

    abstract class Impl implements JavaHandlerContext<Vertex>, CompositeWork {
        public void swapParts(int part1, int part2) throws IllegalArgumentException {
            IsPartOf of1 = getPartOf(part1);
            IsPartOf of2 = getPartOf(part2);
            swapParts(of1, of2);
        }
        
        public void swapParts(Relation part1Ref, Relation part2Ref) {
            if (part1Ref == null || part2Ref == null) 
                throw new IllegalArgumentException("cannot swap parts as one or both parts do not belong to the work.");
            
            int tmp = part1Ref.getRelOrder();
            part1Ref.setRelOrder(part2Ref.getRelOrder());
            part2Ref.setRelOrder(tmp);
        }
        
        public int countParts() {
            return (parts() == null)? 0 : parts().size();
        }
        
        private IsPartOf getPartOf(int position) {
            if (parts() != null && parts().size() >= position)
                return frame(parts().get(position - 1), IsPartOf.class);
            return null;
        }
        
        private List<Edge> parts() {
            return (gremlin().inE(IsPartOf.label) == null)? null: gremlin().inE(IsPartOf.label).toList();
        }
     }
}
