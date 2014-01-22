package amberdb.sql;

import java.util.Map;

public class AmberEdge extends BaseEdge {

	
	Long txnStart;
	Long txnEnd;
	Long order;
	String status;

	
	public AmberEdge(Long id, String label, AmberVertex inVertex, 
    		AmberVertex outVertex, Map<String, Object> properties, 
    		AmberGraph graph, Long txnStart, Long txnEnd, Long order,
    		String status) {
    	
        super(id, label, (BaseVertex) inVertex, (BaseVertex) outVertex, properties, (BaseGraph) graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
        this.order = order;
        this.status = status;
    }

	
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd)
          .append(" order:" ).append(order)
          .append(" status:").append(status);
        return super.toString() + sb.toString();
    }
}
