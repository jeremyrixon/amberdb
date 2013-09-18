package amberdb;

import amberdb.model.Node;
import java.util.List;

public abstract class MapStrategy {
    protected AmberDb dao;
    
    /**
     * Give a root entity, create and return a list of sub-entities mapped onto 
     * the root entity's structmap base on supplied metadata.
     * 
     * @param root: the root entity
     * @param metadata: the metadata for the list of sub-entities to be mapped
     *                  onto the root entity's structmap 
     * @return a list of mapped sub-entities.
     */
    public abstract <T extends Node, S extends Node> List<T> map(T root, List<S> subEntities);
    
    public void setDAO(AmberDb dao) {
        this.dao = dao;
    }
}
