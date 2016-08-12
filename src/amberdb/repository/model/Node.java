package amberdb.repository.model;

import amberdb.DataIntegrityException;
import amberdb.PIUtil;
import amberdb.graph.AmberTransaction;
import amberdb.graph.dao.AmberDao;
import amberdb.repository.JdbiHelper;
import amberdb.repository.dao.EdgeDao;
import amberdb.repository.dao.WorkDao;
import amberdb.repository.dao.associations.DescriptionAssociationDao;
import amberdb.repository.dao.associations.TagAssociationDao;
import amberdb.repository.mappers.AmberDbMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Direction;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Node {
    @Column
    protected long id = 0;
    @Column(name="txn_start")
    protected long txnStart;
    @Column(name="txn_end")
    protected long txnEnd;
    @Column
    protected String accessConditions;
    @Column
    protected String internalAccessConditions;
    @Column
    protected Date expiryDate;
    @Column
    protected String restrictionType;
    @Column
    protected String notes;
    @Column
    protected String type;
    @Column(name="alias")
    protected String jsonAlias;
    @Column
    protected String recordSource;
    @Column
    protected String localSystemNumber;
    @Column
    protected String commentsInternal;
    @Column
    protected String commentsExternal;

    protected JdbiHelper jdbiHelper;
    protected AmberDao amberDao;
    protected WorkDao workDao;
    protected DescriptionAssociationDao descRelationshipDao;
    protected TagAssociationDao tagAssociationDao;
    protected EdgeDao edgeDao;

    public Node() {
        jdbiHelper = new JdbiHelper();
        amberDao = jdbiHelper.getDbi().onDemand(AmberDao.class);
        workDao = jdbiHelper.getDbi().onDemand(WorkDao.class);
        descRelationshipDao = jdbiHelper.getDbi().onDemand(DescriptionAssociationDao.class);
        tagAssociationDao = jdbiHelper.getDbi().onDemand(TagAssociationDao.class);
        edgeDao = jdbiHelper.getDbi().onDemand(EdgeDao.class);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTxnStart() {
        return txnStart;
    }

    public void setTxnStart(long txnStart) {
        this.txnStart = txnStart;
    }

    public long getTxnEnd() {
        return txnEnd;
    }

    public void setTxnEnd(long txnEnd) {
        this.txnEnd = txnEnd;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public void setAccessConditions(String accessConditions) {
        this.accessConditions = accessConditions;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public void setInternalAccessConditions(String internalAccessConditions) {
        this.internalAccessConditions = internalAccessConditions;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJsonAlias() {
        return jsonAlias;
    }

    public void setJsonAlias(String jsonAlias) {
        this.jsonAlias = jsonAlias;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public void setLocalSystemNumber(String localSystemNumber) {
        this.localSystemNumber = localSystemNumber;
    }

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public void setCommentsInternal(String commentsInternal) {
        this.commentsInternal = commentsInternal;
    }

    public String getCommentsExternal() {
        return commentsExternal;
    }

    public void setCommentsExternal(String commentsExternal) {
        this.commentsExternal = commentsExternal;
    }

    public AmberTransaction getFirstTransaction() {
        return amberDao.getFirstTransaction(this.getId(), this.getType());
    }

    public AmberTransaction getLastTransaction() {
        return amberDao.getLastTransaction(this.getId(), this.getType());
    }

    public String getObjId() {
        return PIUtil.format(getId());
    }

    public Iterable<Description> getDescriptions() {
        return descRelationshipDao.getDescriptions(this.getId());
    }

    public Description getDescription(String fmt) {
        Iterable<Description> descriptions = this.getDescriptions();
        if (descriptions != null) {
            Iterator<Description> it = descriptions.iterator();
            while (it.hasNext()) {
                Description next = it.next();
                if (next.getType() != null && next.getType().equals(fmt)) {
                    return next;
                }
            }
        }
        return null;
    }

    public Iterable<Tag> getTags() {
        return tagAssociationDao.getTags(this.getId());
    }

    public void addTag(final Tag tag) {
        // TODO - need txn_start/txn_end values
    }

    public void removeTag(final Tag tag) {
        tagAssociationDao.removeTag(tag.getId(), this.getId());
    }

    public void addCommentsInternal(String comment) {
        if (comment == null || comment.length() == 0) {
            return;
        }

        String currentValue = getCommentsInternal();
        setCommentsInternal(currentValue + "\n" + comment);
    }

    public void addCommentsExternal(String comment) {
        if (comment == null || comment.length() == 0) {
            return;
        }

        String currentValue = getCommentsExternal();
        setCommentsExternal(currentValue + "\n" + comment);
    }

    public List<String> getAlias() {
        String alias = getJsonAlias();
        if (alias == null || alias.isEmpty())
            return new ArrayList<>();

        return deserialiseJSONString(alias);
    }

    public void setAlias(List<String> aliases) throws JsonProcessingException {
        setJsonAlias(serialiseToJSON(aliases));
    }

    public void setOrder(Node adjacent, String label, Direction direction, Integer order) {
        if (Direction.IN.equals(direction)) {
            edgeDao.setOrderIn(adjacent.getId(), label, order);
        } else if (Direction.OUT.equals(direction)) {
            edgeDao.setOrderOut(adjacent.getId(), label, order);
        }
    }

    public Integer getOrder(Node adjacent, String label, Direction direction) {
        if (Direction.IN.equals(direction)) {
            return edgeDao.getOrderIn(adjacent.getId(), label);
        } else if (Direction.OUT.equals(direction)) {
            return edgeDao.getOrderOut(adjacent.getId(), label);
        }

        return 0;
    }

    // TODO - remove? only seems to be used in tests...
    public Set<String> getPropertyKeySet() {
        List<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));

        Set<String> result = new HashSet();
        for (Field f : fields) {
            result.add(f.getName());
        }

        return result;
    }

    protected List<String> deserialiseJSONString(String json) {
        if (json == null || json.isEmpty())
            return new ArrayList<>();
        return deserialiseJSONString(json, new TypeReference<List<String>>() {
        });
    }

    protected <T> T deserialiseJSONString(String json, TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, typeReference);
        }
        catch (IOException e) {
            throw new DataIntegrityException("Could not deserialize property", e);
        }
    }

    protected String serialiseToJSON(Collection<String> list) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if (list == null || list.isEmpty()) return null;
        return mapper.writeValueAsString(list);
    }
}
