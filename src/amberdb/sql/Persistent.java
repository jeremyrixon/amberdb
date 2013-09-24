package amberdb.sql;

public class Persistent extends Identifiable {
    
    private Long txnStart = 0l;
    private Long txnEnd = 0l;

    public void txnStart(long txnStart) { this.txnStart = txnStart; }
    public long txnStart()              { return txnStart; }

    public void txnEnd(long txnEnd) { this.txnEnd = txnEnd; }
    public long txnEnd()            { return txnEnd; }
}
