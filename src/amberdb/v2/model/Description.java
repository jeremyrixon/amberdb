package amberdb.v2.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Description {
    @Column
    private Long id;
    @Column
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
