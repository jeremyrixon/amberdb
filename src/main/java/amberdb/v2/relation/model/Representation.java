package amberdb.v2.relation.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Representation {
    @Column(name="copy_id")
    private Long copyId;
    @Column(name="work_id")
    private Long workId;
    @Column(name="edge_order")
    private int edgeOrder;
    @Column(name="work_type")
    private String workType;

    public Long getCopyId() {
        return copyId;
    }

    public void setCopyId(Long copyId) {
        this.copyId = copyId;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public int getEdgeOrder() {
        return edgeOrder;
    }

    public void setEdgeOrder(int edgeOrder) {
        this.edgeOrder = edgeOrder;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
}
