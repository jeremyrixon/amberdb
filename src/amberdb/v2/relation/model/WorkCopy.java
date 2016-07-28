package amberdb.v2.relation.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class WorkCopy {

    @Column(name="work_id")
    private Long workId;
    @Column(name="copy_id")
    private Long copyId;
    @Column(name="edge_order")
    private int edgeOrder;
    @Column(name="work_type")
    private String workType;
    @Column(name="copy_role")
    private String copyRole;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getCopyId() {
        return copyId;
    }

    public void setCopyId(Long copyId) {
        this.copyId = copyId;
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

    public String getCopyRole() {
        return copyRole;
    }

    public void setCopyRole(String copyRole) {
        this.copyRole = copyRole;
    }
}
