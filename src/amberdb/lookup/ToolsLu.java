package amberdb.lookup;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ToolsLu {
    @Column
    Long id;
    @Column
    String name;
    @Column
    String resolution;
    @Column
    String serialNumber;
    @Column
    String notes;
    @Column
    String materialType;
    @Column
    String toolType;
    @Column
    String toolCategory;
    @Column
    boolean deleted;
    
    public ToolsLu(Long id, String name, String resolution, String serialNumber, String notes,
                   String materialType, String toolType, String toolCategory, String deleted) {
        this.id = id;
        this.name = name;
        this.resolution = resolution;
        this.serialNumber = serialNumber;
        this.notes = notes;
        this.materialType = materialType;
        this.toolType = toolType;
        this.toolCategory = toolCategory;
        this.deleted = (deleted == null)? false : (deleted.equalsIgnoreCase("Y") || deleted.equalsIgnoreCase("D"));
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
    
    public boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
