package amberdb.sql.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.util.StringMapper;

public interface StatefulDao extends Transactional<StatefulDao> {

    @SqlUpdate(
            "UPDATE vertex " +
            "SET state = :state " +
            "WHERE id = :id")
    void updateVertexState(
            @Bind("id") long id,
            @Bind("state") int state);

    @SqlUpdate(
            "UPDATE edge " +
            "SET state = :state " +
            "WHERE id = :id")
    void updateEdgeState(
            @Bind("id") long id,
            @Bind("state") int state);

    @SqlQuery(
            "SELECT state " +
            "FROM vertex " +
            "WHERE id = :id")
    int getVertexState(
            @Bind("id") long id);

    @SqlQuery(
            "SELECT state " +
            "FROM edge " +
            "WHERE id = :id")
    int getEdgeState(
            @Bind("id") long id);
    
    @SqlQuery(
            "SHOW TABLES")
    @Mapper(StringMapper.class)
    String showTables();
    
    void close();
}

