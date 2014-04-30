package amberdb.lookup;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ToolsLu {
    @Column
    Long id;
    @Column
    String name;
    @Column
    String code;
    @Column
    String resolution;
    @Column
    String serialNumber;
    @Column
    String notes;
    @Column
    String materialType;
    @Column 
    Long toolTypeId;
    @Column
    String toolType;
    @Column
    String toolCategory;
    @Column
    Long commitTime;
    @Column
    String commitUser;
    @Column
    boolean deleted;
    
    public ToolsLu(Long id, String name, String resolution, String serialNumber, String notes,
                   String materialType, Long toolTypeId, String toolType, String toolCategory, 
                   Long commitTime, String commitUser, String deleted) {
        this.id = id;
        this.code = id.toString();
        this.name = name;
        this.resolution = resolution;
        this.serialNumber = serialNumber;
        this.notes = notes;
        this.materialType = materialType;
        this.toolTypeId = toolTypeId;
        this.toolType = toolType;
        this.toolCategory = toolCategory;
        this.commitTime = commitTime;
        this.commitUser = commitUser;
        this.deleted = (deleted == null)? false : (deleted.equalsIgnoreCase("Y") || deleted.equalsIgnoreCase("D"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getToolTypeId() {
        return toolTypeId;
    }
    
    public void setToolTypeId(Long toolTypeId) {
        this.toolTypeId = toolTypeId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getToolCategory() {
        return toolCategory;
    }

    public void setToolCategory(String toolCategory) {
        this.toolCategory = toolCategory;
    } 
    
    public Long getCommitTime() {
        return commitTime;
    }
    
    public String getCommitUser() {
        return commitUser;
    }
    
    public boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
