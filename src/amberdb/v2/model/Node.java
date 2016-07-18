package amberdb.v2.model;

public class Node {

    private int id = 0;

    private int txn_start;

    private int txn_end;

    public Node(final int id) {
        this.id = id;
    }

    public Node(final int id, final int txn_start, final int txn_end) {
        this(id);
        this.txn_start = txn_start;
        this.txn_end = txn_end;
    }
}
