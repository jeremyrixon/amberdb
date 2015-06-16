package amberdb.sql;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

@Entity
public abstract class LinkLookups {
    @Column
    Long id;
    @Column
    String name;
    @Column
    Long id1;
    @Column 
    Long id2;
    
    public LinkLookups(String name, Long id1, Long id2){
        this.name = name;
        this.id1 = id1;
        this.id2 = id2;
                  
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }
    
    @SqlQuery("select distinct id, name, id1, id2,  from link_lookups where name = :name")
    public abstract List<LinkLookups> findLinkedLookupsByName(@Bind("name") String name);
    
    @SqlQuery("select distinct id, name, id1, id2,  from link_lookups where name = :name and id = :id")
    public abstract List<LinkLookups> findLinkedLookupsByNameAndId(@Bind("name") String name, @Bind("id1")Long id1 );
    
    @SqlUpdate("INSERT INTO link_lookups (name, id1, id2) VALUES"
            + "(:name, :id1, :id2)")
    @GetGeneratedKeys
    public abstract long addLinkLookupData(@Bind("name") String name,
                                          @Bind("id1") long id1,
                                          @Bind("id2") long id2);
    
    @SqlUpdate("UPDATE link_lookups set id2 = :id2 "
            + "where name = :name "
            + "and  id1 = :id1")
    public abstract void updLinkLookupData(@Bind("id2") long id2,
                                       @Bind("name") String name,
                                       @Bind("id1") long id1);
    
    @SqlBatch("DELETE from link_lookups where id1 = :id1 and name = :name")
    protected abstract void deleteLookupData(@Bind("id1") Long id1,
                                             @Bind("name") String name);
    

}
