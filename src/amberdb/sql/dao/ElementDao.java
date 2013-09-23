package amberdb.sql.dao;

import java.util.List;

import  amberdb.sql.*;
import  amberdb.sql.map.*;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.util.StringMapper;

public interface ElementDao extends Transactional<ElementDao> {

    static final String propertyFields = " id, name, type, b_value, s_value, i_value, d_value ";
    static final String propertyFieldSymbols = " :id, :name, :type, :b_value, :s_value, :i_value, :d_value ";
    
    @SqlQuery(
            "SELECT " + propertyFields +
            "FROM property " +
            "WHERE id = :elementId " +
            "AND name = :name")
    @Mapper(SessionPropertyMapper.class)
    AmberProperty findProperty(
            @Bind("elementId") long elementId, 
            @Bind("name") String propertyName);
    
    @SqlQuery(
            "SELECT name " +
            "FROM property " +
            "WHERE id = :elementId")
    @Mapper(StringMapper.class)
    List<String> getPropertyKeys(
            @Bind("elementId") long elementId);
    
    @SqlUpdate(
            "DELETE FROM property " +
            "WHERE id = :elementId " +
            "AND name = :name")
    void deleteProperty(
            @Bind("elementId") long elementId, 
            @Bind("name") String propertyName);

    @SqlUpdate(
            "DELETE FROM property " +
            "WHERE id = :elementId")
    void deleteProperties(
            @Bind("elementId") long elementId);

    @SqlUpdate(
            "INSERT INTO property (id, name, type, b_value) " +
            "VALUES (:id, :name, 'b', :value)")
    void addBooleanProperty(
            @Bind("id")    long id,
            @Bind("name")  String name,
            @Bind("value") Boolean value);
    @SqlUpdate(
            "INSERT INTO property (id, name, type, s_value) " +
            "VALUES (:id, :name, 's', :value)")
    void addStringProperty(
            @Bind("id")    long id,
            @Bind("name")  String name,
            @Bind("value") String value);
    @SqlUpdate(
            "INSERT INTO property (id, name, type, i_value) " +
            "VALUES (:id, :name, 'i', :value)")
    void addIntProperty(
            @Bind("id")    long id,
            @Bind("name")  String name,
            @Bind("value") Integer value);
    @SqlUpdate(
            "INSERT INTO property (id, name, type, d_value) " +
            "VALUES (:id, :name, 'd', :value)")
    void addDoubleProperty(
            @Bind("id")    long id,
            @Bind("name")  String name,
            @Bind("value") Double value);

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
    
   
    void close();
}

