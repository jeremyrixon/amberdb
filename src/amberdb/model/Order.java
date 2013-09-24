package amberdb.model;

import java.util.HashMap;
import java.util.Map;

import amberdb.relation.ExistsOn;
import amberdb.relation.IsPartOf;
import amberdb.relation.Relation;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.frames.FramedGraph;

public class Order {
    private FramedGraph<TinkerGraph> graph;
    private Map<Work, Integer> order = new HashMap<Work, Integer>();

    public void setContext(FramedGraph<TinkerGraph> graph) {
        this.graph = graph;
    }

    public void assign(Work parent, Work child) {
        int position = position(child, parent);
        Item _parent = graph.getVertex(parent.getId(), Item.class);
        IsPartOf of = graph.getEdge(_parent.getIsPartOfRef(position), IsPartOf.class);
        assign(position, child, of, parent);
    }

    public void assignAttached(Section parent, Page child) {
        int position = position(child, parent);
        ExistsOn on = graph.getEdge(parent.getExistsOnRef(position), ExistsOn.class);
        assign(position, child, on, parent);
    }

    public void assign(int position, Work child, Relation of, Work parent) {
        if (of == null)
            throw new IllegalArgumentException("Input work " + child.getObjId() + " is not part of input work "
                    + parent.getObjId());
        of.setRelOrder(position);
    }

    public int position(Work child, Work parent) {
        int nthChild = 1;
        if (order.get(parent) != null) {
            nthChild = order.get(parent) + 1;
        }
        order.put(parent, nthChild);
        return nthChild;
    }
}
