package amberdb.repository.model;

import javax.persistence.Column;

public class Tag extends Node {
    @Column
    public String name;
    @Column
    public String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addNode(final Node node) {
        // TODO - need txn_start/txn_end values to insert into edge table
        tagRelationshipDao.addNode(this.getId(), node.getId());
    }

    public void removeNode(final Node node) {
        tagRelationshipDao.removeNode(this.getId(), node.getId());
    }

    public Iterable<Node> getTaggedObjects() {
        return tagRelationshipDao.getTaggedObjects(this.getId());
    }
}
