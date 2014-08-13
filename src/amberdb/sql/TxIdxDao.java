package amberdb.sql;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;


public interface TxIdxDao extends Transactional<TxIdxDao> {

    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS indexed_txns (txn BIGINT(11))")
    void createIndexedTable();
    
    
    @SqlUpdate(
            "CREATE INDEX indexed_txns_idx ON indexed_txns(txn)")
    void createIndexedTxnsIndex();

    
    @SqlUpdate(
            "INSERT INTO indexed_txns (txn) VALUES (1)")
    void seedIndexedTransactions();
    
    
    @SqlQuery(
            "SELECT MAX(txn) FROM indexed_txns")
    Long getLastIndexedTxnId();

    
    @SqlQuery(
            "SELECT id "
            + "FROM transaction "
            + "WHERE id > :lastIndexedTxnId "
            + "ORDER BY id")
    List<Long> getTxnsSinceIndexed(
            @Bind("lastIndexedTxnId") Long lastIndexedTxnId);
    

    @SqlUpdate(
            "INSERT INTO indexed_txns (txn) VALUES (:txn)")
    void updateIndexedTransactions(
            @Bind("txn") Long txn);

    
    @SqlQuery(
            "SELECT (COUNT(table_name) = 1) "
            + "FROM information_schema.tables " 
            + "WHERE table_name = 'indexed_txns'")
    boolean schemaTablesExist();
}

