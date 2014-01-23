package amberdb.sql;

import java.util.ArrayList;
import java.util.List;

public class AmberVertexBatch {
	List<Long>   id       = new ArrayList<Long>();
	List<Long>   txnStart = new ArrayList<Long>();
	List<Long>   txnEnd   = new ArrayList<Long>();
	List<String> state   = new ArrayList<String>();

	void add(AmberVertexWithState statefulVertex) {
		AmberVertex vertex = statefulVertex.vertex;
		
		id.add((Long) vertex.getId());
		txnStart.add(vertex.txnStart);
		txnEnd.add(vertex.txnEnd);
		state.add(statefulVertex.state);
	}
}
