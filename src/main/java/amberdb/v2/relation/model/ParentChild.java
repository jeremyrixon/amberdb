package amberdb.v2.relation.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class ParentChild {
    @Column(name="parent_id")
    private Long parentId;
    @Column(name="child_id")
    private Long childId;
    @Column(name="child_biblevel")
    private String childBibLevel;
    @Column(name="child_subtype")
    private String childSubType;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getChildBibLevel() {
        return childBibLevel;
    }

    public void setChildBibLevel(String childBibLevel) {
        this.childBibLevel = childBibLevel;
    }

    public String getChildSubType() {
        return childSubType;
    }

    public void setChildSubType(String childSubType) {
        this.childSubType = childSubType;
    }
}
