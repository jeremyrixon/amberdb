package amberdb.version;

public class TId implements Comparable<TId> {

    
    Long id;
    Long start = 0l;
    Long end = 0l;
    
    
    public TId(Long id) {
        this.id = id;
    }

    
    public TId(Long id, Long start, Long end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
    
    
    public static TId parse(String idStr) throws InvalidIdentifierException {
        try {
            String[] parts = idStr.split(":", 3);
            return new TId(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        } catch (Exception e) {
            throw new InvalidIdentifierException("Cannot parse as VId: " + idStr, e);
        }
    }
    
    
    @Override
    public String toString() {
        return id.toString() + ":" + start.toString() + ":" + end.toString();
    }
    
    
    public boolean equals(Object o) {
        if (o instanceof TId) {
            TId aid = (TId) o;
            return (id == aid.id && start == aid.start && end == aid.end);
        }
        return false;
    }

    
    @Override
    public int hashCode() {
        return id.hashCode() + (13 *start.hashCode()) + (17 * end.hashCode());
    }
    
    
    public int compareTo(TId o) {
        if (id > o.id) return 1;
        if (id < o.id) return -1;
        if (start > o.start) return 1;
        if (start < o.start) return -1;
        
        if (end < o.end) return 1; // end == 0 is greater than end > 0
        if (end > o.end) return -1; 
        return 0;
    }
}
