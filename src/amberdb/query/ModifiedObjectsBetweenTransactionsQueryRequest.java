package amberdb.query;

import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;

import amberdb.version.VersionedVertex;

public class ModifiedObjectsBetweenTransactionsQueryRequest extends ModifiedObjectsQueryRequest {
    
    private Long fromTxn = null;
    private Long toTxn = null;
    
    public ModifiedObjectsBetweenTransactionsQueryRequest(List<Long> txns) {
        super();

        this.fromTxn = txns.get(0);
        this.toTxn = txns.get(txns.size() - 1);
    }
    
    public ModifiedObjectsBetweenTransactionsQueryRequest(List<Long> txns, Predicate<VersionedVertex> filterPredicate, List<WorkProperty> propertyFilters, boolean onlyPropertiesWithinTransactionRange, int skip, int take) {
        super(null, null, filterPredicate, propertyFilters, onlyPropertiesWithinTransactionRange, skip, take);

        this.fromTxn = txns.get(0);
        this.toTxn = txns.get(txns.size() - 1);
    }
    
    public ModifiedObjectsBetweenTransactionsQueryRequest(ModifiedObjectsQueryRequest request, TransactionsBetweenFinder transactionsFinder) {
        super(request);
        
        List<Long> txns = transactionsFinder.getTransactionsBetween(request.getFrom(), request.getTo());
        this.fromTxn = txns.get(0);
        this.toTxn = txns.get(txns.size() - 1);
    }

    public long getFromTxn() {
        return fromTxn;
    }

    public long getToTxn() {
        return toTxn;
    }
    

    public interface TransactionsBetweenFinder {
        public List<Long> getTransactionsBetween(Date startTime, Date endTime);
    }
}