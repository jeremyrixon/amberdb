package amberdb.sql.dao;

import java.util.Iterator;

import  amberdb.sql.*;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

public interface TransactionDao extends Transactional<TransactionDao> {

    static final String txnFields = " id, commit, user, operation ";
    static final String txnFieldSymbols = " :id, :commit, :user, :operation ";
    
//    @SqlUpdate(
//            "CREATE TABLE IF NOT EXISTS transaction (" +
//            "id       BIGINT UNIQUE, " +
//            "commit   TIMESTAMP, " +
//            "user     VARCHAR(100), " +
//            "operaton TEXT)")
//    void createTransactionTable();
//
//    @SqlUpdate("DROP TABLE IF EXISTS transaction")
//    void dropTransactionTable();


    

    @SqlUpdate(
            "INSERT INTO transaction ("+ txnFields + ")" +
            "VALUES (" + txnFieldSymbols + ")")
    void insertTransaction(
            @Bind("id") long id, 
            @Bind("commit") long commit, 
            @Bind("user") String user,
            @Bind("operation") String operation);

    void close();
}

