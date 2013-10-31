package amberdb.sql.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

public interface TransactionDao extends Transactional<TransactionDao> {

    @SqlUpdate(
            "INSERT INTO transaction (id, commit, user, operation)" +
            "VALUES (:id, :commit, :user, :operation)")
    void insertTransaction(
            @Bind("id") long id, 
            @Bind("commit") long commit, 
            @Bind("user") String user,
            @Bind("operation") String operation);

    void close();

    @SqlUpdate(
            "UPDATE transaction " +
            "SET operation = :op " +
            "WHERE id = :id")
    void setOperation(
            @Bind("id") long id,
            @Bind("op") String op);
}

