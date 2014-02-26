package amberdb.lookup;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ListLu {

    @Column
    String name;
    @Column
    String value;
    @Column
    String deleted;

    public ListLu(String name, String value) {
        this.name = name;
        this.value = value;
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
    
}
