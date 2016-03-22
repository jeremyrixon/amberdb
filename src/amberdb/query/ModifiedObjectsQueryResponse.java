package amberdb.query;

import java.util.LinkedHashMap;

public class ModifiedObjectsQueryResponse {
    private LinkedHashMap<Long, String> modifiedObjects;
    private boolean hasMore;
    private long skipForNextBatch;

    public ModifiedObjectsQueryResponse() {
        this(new LinkedHashMap<Long, String>(), false, -1);
    }

    public ModifiedObjectsQueryResponse(LinkedHashMap<Long, String> modifiedObjects, boolean hasMore, long skipForNextBatch) {
        this.modifiedObjects = modifiedObjects;
        this.hasMore = hasMore;
        this.skipForNextBatch = skipForNextBatch;
    }

    public LinkedHashMap<Long, String> getModifiedObjects() {
        return modifiedObjects;
    }
    
    public long getResultSize() {
        return modifiedObjects.size();
    }
    
    public boolean hasMore() {
        return hasMore;
    }

    public long getSkipForNextBatch() {
        return skipForNextBatch;
    }
}