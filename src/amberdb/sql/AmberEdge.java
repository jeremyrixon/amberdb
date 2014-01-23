package amberdb.sql;

import java.util.Map;

public class AmberEdge extends BaseEdge {

	
	Long txnStart;
	Long txnEnd;
	Long order;

	
	public AmberEdge(Long id, String label, AmberVertex inVertex, 
    		AmberVertex outVertex, Map<String, Object> properties, 
    		AmberGraph graph, Long txnStart, Long txnEnd, Long order) {
    	
        super(id, label, (BaseVertex) inVertex, (BaseVertex) outVertex, properties, (BaseGraph) graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
        this.order = order;
    }

	
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd)
          .append(" order:" ).append(order);
        return super.toString() + sb.toString();
    }
}
