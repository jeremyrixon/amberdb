package amberdb.sql;

import java.util.Map;

public class AmberVertex extends BaseVertex {

	Long txnStart;
	Long txnEnd;
	
    public AmberVertex(long id, Map<String, Object> properties, 
    		AmberGraph graph, Long txnStart, Long txnEnd) {
    	
        super(id, properties, graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
    }
	
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd);
        return super.toString() + sb.toString();
    }
    
    
    public boolean equals(Object o) {
    	if ((o == null) || !(o instanceof AmberVertex)) return false;
    	AmberVertex e = (AmberVertex) o;
    	if (getId() != e.getId()) return false;
    	return true;
    }
}

