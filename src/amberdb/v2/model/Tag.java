package amberdb.v2.model;

import amberdb.v2.model.mapper.MapWith;
import amberdb.v2.model.mapper.TagMapper;

@MapWith(TagMapper.class)
public class Tag extends Node {

    private String name;

    public Tag(int id, int txn_start, int txn_end, String name) {
        super(id, txn_start, txn_end);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
