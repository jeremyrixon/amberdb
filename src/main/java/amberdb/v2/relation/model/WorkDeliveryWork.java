package amberdb.v2.relation.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class WorkDeliveryWork {

    @Column(name="work_id")
    private Long workId;
    @Column(name="deliverywork_id")
    private Long deliveryWorkId;
    @Column(name="edge_order")
    private int edgeOrder;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getDeliveryWorkId() {
        return deliveryWorkId;
    }

    public void setDeliveryWorkId(Long deliveryWorkId) {
        this.deliveryWorkId = deliveryWorkId;
    }

    public int getEdgeOrder() {
        return edgeOrder;
    }

    public void setEdgeOrder(int edgeOrder) {
        this.edgeOrder = edgeOrder;
    }
}
