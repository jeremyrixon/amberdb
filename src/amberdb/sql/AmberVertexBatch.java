package amberdb.sql;

import java.util.ArrayList;
import java.util.List;

public class AmberVertexBatch {
	List<Long>   id       = new ArrayList<Long>();
	List<Long>   txnStart = new ArrayList<Long>();
	List<Long>   txnEnd   = new ArrayList<Long>();
	List<String> status   = new ArrayList<String>();

	void add(AmberVertex vertex) {
		id.add((Long) vertex.getId());
		txnStart.add(vertex.txnStart);
		txnEnd.add(vertex.txnEnd);
		status.add(vertex.status);
	}
}
