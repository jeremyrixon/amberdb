package amberdb.sql;

import java.util.Map;

public class AmberVertex extends BaseVertex {

	Long txnStart;
	Long txnEnd;
	String status;
	
    public AmberVertex(long id, Map<String, Object> properties, 
    		AmberGraph graph, Long txnStart, Long txnEnd, String status) {
    	
        super(id, properties, graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
        this.status = status;
    }
	
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd)
          .append(" status:").append(status);
        return super.toString() + sb.toString();
    }
}

