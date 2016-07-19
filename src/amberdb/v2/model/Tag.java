package amberdb.v2.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Tag extends Node {

    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
