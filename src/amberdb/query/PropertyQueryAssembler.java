package amberdb.query;

import org.apache.commons.collections.CollectionUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.LongMapper;

import java.util.List;

public class PropertyQueryAssembler {
    private static String SQL_TEMPLATE = "select v.id from %s vertex v where v.txn_end=0 %s and v.id=p1.id %s %s";

    private List<WorkProperty> workProperties;

    public PropertyQueryAssembler(List<WorkProperty> workProperties){
        if (CollectionUtils.isEmpty(workProperties)){
            throw new IllegalArgumentException("Work property workProperties cannot be empty");
        }
        this.workProperties = workProperties;
    }

    /**
     * @return select clause for property table.
     * e.g. property p1, property p2, property p3
     */
    private String fromClauseForProperty(){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< workProperties.size(); i++){
            sb.append("property p").append(i+1).append(", ");
        }
        return sb.toString();
    }

    /**
     * @return where clause for txn_end=0
     * e.g. and p1.txn_end=0 and p2.txn_end=0 and p3.txn_end=0
     */
    private String whereClauseForTxnEnd(){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< workProperties.size(); i++){
            sb.append("and p").append(i+1).append(".txn_end=0 ");
        }
        return sb.toString();
    }

    /**
     * @return where clause for join Id
     * e.g. and p1.id=p2.id and p2.id=p3.id
     */
    private String whereClauseForJoinId(){
        StringBuilder sb = new StringBuilder();
        for (int i=1; i< workProperties.size(); i++){
            sb.append("and p").append(i).append(".id=p").append(i+1).append(".id ");
        }
        return sb.toString();
    }

    /**
     * @return where clause for name/value pair
     * e.g. and p1.name='title' and p1.value=?
     *      and p2.name='collection' and p2.value=? and
     *      p3.name='recordSource' and p3.value=?
     */
    private String whereClauseForNameAndValuePair(){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< workProperties.size(); i++){
            String name = workProperties.get(i).getName();
            sb.append("and p").append(i+1).append(".name='").append(name).append("' and p").append(i+1).append(".value=? ");
        }
        return sb.toString();
    }

    public String sql(){
        return String.format(SQL_TEMPLATE, fromClauseForProperty(), whereClauseForTxnEnd(), whereClauseForJoinId(), whereClauseForNameAndValuePair());
    }

    public Query query(Handle h){
        Query q = h.createQuery(sql());
        for (int i=0; i< workProperties.size(); i++){
            q.bind(i, workProperties.get(i).getValue());
        }
        return q.map(LongMapper.FIRST);
    }


}
