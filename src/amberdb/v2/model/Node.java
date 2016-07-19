package amberdb.v2.model;

import javax.persistence.Column;

public class Node {

    @Column
    private int id = 0;

    @Column
    private int txn_start;

    @Column
    private int txn_end;

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
