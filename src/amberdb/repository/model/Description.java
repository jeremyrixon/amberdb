package amberdb.repository.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Description {
    @Column
    protected long id = 0;
    @Column(name="txn_start")
    protected long txnStart;
    @Column(name="txn_end")
    protected long txnEnd;
    @Column
    protected String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTxnStart() {
        return txnStart;
    }

    public void setTxnStart(long txnStart) {
        this.txnStart = txnStart;
    }

    public long getTxnEnd() {
        return txnEnd;
    }

    public void setTxnEnd(long txnEnd) {
        this.txnEnd = txnEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
