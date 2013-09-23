package amberdb.sql;

public class Identifiable {
    
    private long id;

    public void id(long id) { this.id = id; }
    public long id()        { return id; }
    
    
    // just a debugging convenience passed to all subclasses
    void s(String s) {
        System.out.println(s);
    }
}
