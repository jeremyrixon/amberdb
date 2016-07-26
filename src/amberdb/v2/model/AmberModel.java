package amberdb.v2.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class AmberModel {

    @Column
    protected long id = 0;

    @Column
    protected long txn_start;

    @Column
    protected long txn_end;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTxn_start() {
        return txn_start;
    }

    public void setTxn_start(long txn_start) {
        this.txn_start = txn_start;
    }

    public long getTxn_end() {
        return txn_end;
    }

    public void setTxn_end(long txn_end) {
        this.txn_end = txn_end;
    }
}
