package amberdb;

import java.util.List;
import java.util.Map;

import amberdb.model.Work;

public class StageStrategy {
    protected AmberDb stageDao;
    protected AmberDb liveDao;
    
    public Work findStagedWork(long objectId) {
        return stageDao.findWork(objectId);
    }
    
    public Work stageWork() {
        return stageDao.addWork();
    }
    
    public Work stageWork(Map<String, String> metadata) {
        return stageDao.addWork(metadata);
    }
    
    public void updStagedWork(Work item, Map<String, String> metadata) {
        stageDao.updWork(item, metadata);
    }
    
    public List<Work> stageMap(Map<String, String> metadata, List<String> fileLocations, MapStrategy strategy) {
        return stageDao.map(metadata, fileLocations, strategy);
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
    
    public void setStageDAO(AmberDb dao) {
        this.stageDao = stageDao;
    }
    
    public void setLiveDao(AmberDb dao) {
        this.liveDao = liveDao;
    }
}
