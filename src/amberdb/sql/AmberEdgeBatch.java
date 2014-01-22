package amberdb.sql;

import java.util.ArrayList;
import java.util.List;

public class AmberEdgeBatch {
	List<Long>   id        = new ArrayList<Long>();
	List<Long>   txnStart  = new ArrayList<Long>();
	List<Long>   txnEnd    = new ArrayList<Long>();
	List<Long>   vertexOut = new ArrayList<Long>();
	List<Long>   vertexIn  = new ArrayList<Long>();
	List<String> label     = new ArrayList<String>();
	List<Long>   order     = new ArrayList<Long>();
	List<String> status    = new ArrayList<String>();
	
	void add(AmberEdge edge) {
		id.add((Long) edge.getId());
		txnStart.add(edge.txnStart);
		txnEnd.add(edge.txnEnd);
		vertexOut.add((Long) edge.outVertex.getId());
		vertexIn.add((Long) edge.inVertex.getId());
		label.add(edge.getLabel());
		order.add(edge.order);
		status.add(edge.status);
	}
}
