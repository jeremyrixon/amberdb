package amberdb.sql.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.sql.AmberProperty;
import amberdb.sql.bind.BindAmberProperty;
import amberdb.sql.map.PersistentPropertyMapper;

public interface MigrationDao extends Transactional<MigrationDao> {
    @SqlQuery("select p.id, p.name, p.type, p.value " +
            "from edge e, copy_of c, file_of f, property p " +
            "where v_in = :workId " +
            "and label = 'isPartOf' " +
            "and e.v_out = c.src_id " +
            "and c.id = f.src_id " +
            "and p.id = f.id " +
            "and p.txn_end is null")
  @Mapper(PersistentPropertyMapper.class)
  List<AmberProperty> getPropertiesForWorkDetails(@Bind("workId") long workId);
    
    @SqlQuery("select p.id, p.name, p.type, p.value " +
              "from edge e, copy_of c, file_of f, property p " +
              "where v_in = :workId " +
              "and label = 'isPartOf' " +
              "and e.v_out = c.src_id " +
              "and c.id = f.src_id " +
              "and p.id = f.id " +
              "and name = :name " +
              "and p.txn_end is null")
    @Mapper(PersistentPropertyMapper.class)
    List<AmberProperty> getPropertiesForWorkDetails(@Bind("workId") long workId, @Bind("name") String name);
    

    @SqlQuery("select p.* " +
            "from property p " +
            "where name = :name")
    @Mapper(PersistentPropertyMapper.class)
    List<AmberProperty> getPropertiesOfName(@Bind("name") String name);
    
    @SqlUpdate("update property set value = :value, type= :type where id = :id and name = :name")
    int updProperty(@BindAmberProperty AmberProperty property);
}
