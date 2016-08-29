package amberdb.v2.relation.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class WorkAcknowledgement {
    @Column(name="work_id")
    private Long workId;
    @Column(name="acknowledgement_id")
    private Long acknowledgementId;
    @Column
    private Double weighting;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getAcknowledgementId() {
        return acknowledgementId;
    }

    public void setAcknowledgementId(Long acknowledgementId) {
        this.acknowledgementId = acknowledgementId;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }
}
