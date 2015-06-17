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
public abstract class CarrierAlgorithm {
    @Column
    Long linkId;
    @Column
    String name;
    @Column
    Long carrierId;
    @Column 
    Long algorithmId;
    
    public CarrierAlgorithm(String name, Long carrierId, Long algorithmId){
        this.name = name;
        this.carrierId = carrierId;
        this.algorithmId = algorithmId;
                  
    }

 

    public Long getLinkId() {
        return linkId;
    }



    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  
    
    public Long getCarrierId() {
        return carrierId;
    }



    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }



    public Long getAlgorithmId() {
        return algorithmId;
    }



    public void setAlgorithmId(Long algorithmId) {
        this.carrierId = algorithmId;
    }



    @SqlQuery("select distinct linkId, name, carrierId, algorithmId,  from carrier_algorithm where name = :name")
    public abstract List<CarrierAlgorithm> findCarrierAlgorithmsByName(@Bind("name") String name);
    
    @SqlQuery("select distinct linkId, name, carrierId, algorithmId  from carrier_algorithm where name = :name and carrierId = :carrierId")
    public abstract List<CarrierAlgorithm> findCarrierAlgorithmByNameAndId(@Bind("name") String name, @Bind("carrierId")Long carrierId );
    
    @SqlUpdate("INSERT INTO carrier_algorithm (name, carrierId, algorithmId) VALUES"
            + "(:name, :carrierId, :algorithmId)")
    @GetGeneratedKeys
    public abstract long addCarrierAlgorithmData(@Bind("name") String name,
                                          @Bind("carrierId") long carrierId,
                                          @Bind("algorithmId") long algorithmId);
    
    @SqlUpdate("UPDATE carrier_algorithm set algorithmId = :algorithmId "
            + "where name = :name "
            + "and  carrierId = :carrierId")
    public abstract void updCarrierAlgorithm(@Bind("algorithmId") long algorithmId,
                                       @Bind("name") String name,
                                       @Bind("carrierId") long carrierId);
    
    @SqlBatch("DELETE from link_lookups where carrierId = :carrierId and name = :name")
    protected abstract void deleteCarrierAlgorithm(@Bind("carrierId") Long carrierId,
                                             @Bind("name") String name);
    

}
