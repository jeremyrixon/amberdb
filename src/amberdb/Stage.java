package amberdb;

import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import amberdb.model.Work;

public class Stage extends AmberDb implements AutoCloseable {
    private static Stage self;
    protected AmberDb liveDao;
    // final Sequence objectIdSeq = new Sequence();  // do we really need this?
    
    private Stage() {
        super();
    }
    
    public static Stage getInstance() {
        if (self == null)
            self = new Stage();
        return self;
    }
    
    public Work findStagedWork(long objectId) {
        return super.findWork(objectId);
    }
    
    public Work stageWork() {
        return super.addWork();
    }
    
    public Work stageWork(Map<String, String> metadata) {
        return super.addWork(metadata);
    }
    
    public List<Work> stageMap(Map<String, String> metadata, List<String> fileLocations, MapStrategy strategy) {
        return super.map(metadata, fileLocations, strategy);
    }
    
    public JsonNode toJson() {
        // TODO 
        return null;
    }
    
    /**
     * Port item and its structmap from liveDao to stageDao
     * @param item
     * @return
     */
    public Work port(Work item) {
        // TODO
        return null;
    }
    
    /**
     * Commit item and its structmap from stageDao to liveDao
     * @param item
     * @return
     */
    public Work commit(Work item) {
        // TODO
        return null;
    }
    
    public void setLiveDao(AmberDb dao) {
        this.liveDao = liveDao;
    }
}
