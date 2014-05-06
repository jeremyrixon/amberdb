package amberdb.lookup;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ListLu {
    @Column
    Long id;
    @Column
    String name;
    @Column
    String value;
    @Column
    String code;
    @Column
    String deleted;

    public ListLu(Long id) {
        this.id = id;
    }
    
    public ListLu(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ListLu(Long id, String name, String value, String code, String deleted) {
        this.id = id;
        this.name = name;
        this.value = value;   
        if (code == null) 
            this.code = this.value;
        else
            this.code = code;
        this.deleted = deleted;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDeleted() {
        return value;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public boolean isDeleted() {
        return (deleted != null && (deleted.equalsIgnoreCase("Y") || deleted.equalsIgnoreCase("D")));
    }
}
