package amberdb.v1.query;

import java.util.LinkedHashMap;

public class ModifiedObjectsQueryResponse {
    private LinkedHashMap<Long, String> modifiedObjects;
    private LinkedHashMap<Long, String> reasons;

    private boolean hasMore;
    private long skipForNextBatch;

    public ModifiedObjectsQueryResponse() {
        this(new LinkedHashMap<Long, String>(), false, -1);
    }

    public ModifiedObjectsQueryResponse(LinkedHashMap<Long, String> modifiedObjects, LinkedHashMap< Long, String> reasons, boolean hasMore, long skipForNextBatch) {
        this.modifiedObjects = modifiedObjects;
        this.reasons = reasons;
        this.hasMore = hasMore;
        this.skipForNextBatch = skipForNextBatch;
    }

    public ModifiedObjectsQueryResponse(LinkedHashMap<Long, String> modifiedObjects, boolean hasMore, long skipForNextBatch) {
        this(modifiedObjects, new LinkedHashMap<Long, String>(), hasMore, skipForNextBatch);
    }

    public LinkedHashMap<Long, String> getModifiedObjects() {
        return modifiedObjects;
    }

    public LinkedHashMap<Long, String> getReasons() {
        return reasons;
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