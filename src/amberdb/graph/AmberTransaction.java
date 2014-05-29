package amberdb.graph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class AmberTransaction {
    private TxInfo txInfo;
    
    public AmberTransaction(AmberGraph graph) {
        try (Handle h = graph.dbi().open()) {
            Long id = graph.suspend();
            graph.resume(id);
            txInfo = h.createQuery("select user, time from transaction where id = :id").
                    bind("id", id).map(new AmberTransactionMapper()).first();
        }
    }
    
    public String getRecordUpdator() {
        return (txInfo == null)? null: txInfo.recordUpdator;
    }
    
    public Date getDateTimeUpdated() {
        return (txInfo == null)? null:txInfo.dateTimeUpdated;
    }
    
    static class AmberTransactionMapper implements ResultSetMapper<TxInfo> {        
        @Override
        public TxInfo map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            String recordUpdator = r.getString("user");
            Long time = r.getLong("time");
            Date dateTimeUpdated = (time == null)? null : new Date(time);
            return new TxInfo(recordUpdator, dateTimeUpdated);
        }
    }
}

class TxInfo {
    String recordUpdator;
    Date dateTimeUpdated;
    
    public TxInfo(String recordUpdator, Date dateTimeUpdated) {
        this.recordUpdator = recordUpdator;
        this.dateTimeUpdated = dateTimeUpdated;
    }
}
