package amberdb.repository.model;

import amberdb.DataIntegrityException;
import amberdb.PIUtil;
import amberdb.graph.AmberTransaction;
import amberdb.graph.dao.AmberDao;
import amberdb.repository.JdbiHelper;
import amberdb.repository.dao.WorkDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public class AmberModel {
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

    public AmberModel() {
        jdbiHelper = new JdbiHelper();
        amberDao = jdbiHelper.getDbi().onDemand(AmberDao.class);
        workDao = jdbiHelper.getDbi().onDemand(WorkDao.class);
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

    public String getObjId() {
        return PIUtil.format(getId());
    }

    public String getJsonAlias() {
        return jsonAlias;
    }

    public void setJsonAlias(String jsonAlias) {
        this.jsonAlias = jsonAlias;
    }

    public AmberTransaction getFirstTransaction() {
        return amberDao.getFirstTransaction(this.getId(), this.getType());
    }

    public AmberTransaction getLastTransaction() {
        return amberDao.getLastTransaction(this.getId(), this.getType());
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
