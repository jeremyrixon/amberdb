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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTxn_start() {
        return txn_start;
    }

    public void setTxn_start(int txn_start) {
        this.txn_start = txn_start;
    }

    public int getTxn_end() {
        return txn_end;
    }

    public void setTxn_end(int txn_end) {
        this.txn_end = txn_end;
    }
}
