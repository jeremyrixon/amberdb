package amberdb.v2.model;

import amberdb.v2.model.mapper.CopyMapper;
import amberdb.v2.model.mapper.MapWith;

import java.util.Date;

@MapWith(CopyMapper.class)
public class Copy extends Node {

    private Date dcmDateTimeUpdated;
    private String extent;
    private String dcmRecordUpdater;
    private String localSystemNumber;
    private String encodingLevel;
    private String standardId;
    private String language;
    private String title;
    private String holdingId;
    private String internalAccessConditions;
    private Boolean australianContent;
    private Date dateCreated;
    private String contributor;
    private String timedStatus;
    private String copyType;
    private String alias;
    private String copyStatus;
    private String copyRole;
    private String manipulation;
    private String recordSource;
    private String algorithm;
    private String bibId;
    private String creator;
    private String otherNumbers;
    private Date dcmDateTimeCreated;
    private String materialType;
    private String commentsExternal;
    private String coordinates;
    private String creatorStatement;
    private String classification;
    private String currentVersion;
    private String commentsInternal;
    private String bestCopy;
    private String carrier;
    private String holdingNumber;
    private String series;
    private String publisher;
    private String dcmRecordCreator;
    private String dcmCopyPid;

    public Copy(int id, int txn_start, int txn_end, Date dcmDateTimeUpdated, String extent, String dcmRecordUpdater,
                String localSystemNumber, String encodingLevel, String standardId, String language, String title,
                String holdingId, String internalAccessConditions, Boolean australianContent, Date dateCreated,
                String contributor, String timedStatus, String copyType, String alias, String copyStatus,
                String copyRole, String manipulation, String recordSource, String algorithm, String bibId,
                String creator, String otherNumbers, Date dcmDateTimeCreated, String materialType,
                String commentsExternal, String coordinates, String creatorStatement, String classification,
                String currentVersion, String commentsInternal, String bestCopy, String carrier, String holdingNumber,
                String series, String publisher, String dcmRecordCreator, String dcmCopyPid) {
        super(id, txn_start, txn_end);
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
        this.extent = extent;
        this.dcmRecordUpdater = dcmRecordUpdater;
        this.localSystemNumber = localSystemNumber;
        this.encodingLevel = encodingLevel;
        this.standardId = standardId;
        this.language = language;
        this.title = title;
        this.holdingId = holdingId;
        this.internalAccessConditions = internalAccessConditions;
        this.australianContent = australianContent;
        this.dateCreated = dateCreated;
        this.contributor = contributor;
        this.timedStatus = timedStatus;
        this.copyType = copyType;
        this.alias = alias;
        this.copyStatus = copyStatus;
        this.copyRole = copyRole;
        this.manipulation = manipulation;
        this.recordSource = recordSource;
        this.algorithm = algorithm;
        this.bibId = bibId;
        this.creator = creator;
        this.otherNumbers = otherNumbers;
        this.dcmDateTimeCreated = dcmDateTimeCreated;
        this.materialType = materialType;
        this.commentsExternal = commentsExternal;
        this.coordinates = coordinates;
        this.creatorStatement = creatorStatement;
        this.classification = classification;
        this.currentVersion = currentVersion;
        this.commentsInternal = commentsInternal;
        this.bestCopy = bestCopy;
        this.carrier = carrier;
        this.holdingNumber = holdingNumber;
        this.series = series;
        this.publisher = publisher;
        this.dcmRecordCreator = dcmRecordCreator;
        this.dcmCopyPid = dcmCopyPid;
    }

    public String getManipulation() {
        return manipulation;
    }

    public void setManipulation(String manipulation) {
        this.manipulation = manipulation;
    }

    public Date getDcmDateTimeUpdated() {
        return dcmDateTimeUpdated;
    }

    public void setDcmDateTimeUpdated(Date dcmDateTimeUpdated) {
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public void setDcmRecordUpdater(String dcmRecordUpdater) {
        this.dcmRecordUpdater = dcmRecordUpdater;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public void setLocalSystemNumber(String localSystemNumber) {
        this.localSystemNumber = localSystemNumber;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public void setEncodingLevel(String encodingLevel) {
        this.encodingLevel = encodingLevel;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public void setInternalAccessConditions(String internalAccessConditions) {
        this.internalAccessConditions = internalAccessConditions;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public void setAustralianContent(Boolean australianContent) {
        this.australianContent = australianContent;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getTimedStatus() {
        return timedStatus;
    }

    public void setTimedStatus(String timedStatus) {
        this.timedStatus = timedStatus;
    }

    public String getCopyType() {
        return copyType;
    }

    public void setCopyType(String copyType) {
        this.copyType = copyType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCopyStatus() {
        return copyStatus;
    }

    public void setCopyStatus(String copyStatus) {
        this.copyStatus = copyStatus;
    }

    public String getCopyRole() {
        return copyRole;
    }

    public void setCopyRole(String copyRole) {
        this.copyRole = copyRole;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOtherNumbers() {
        return otherNumbers;
    }

    public void setOtherNumbers(String otherNumbers) {
        this.otherNumbers = otherNumbers;
    }

    public Date getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public void setDcmDateTimeCreated(Date dcmDateTimeCreated) {
        this.dcmDateTimeCreated = dcmDateTimeCreated;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCommentsExternal() {
        return commentsExternal;
    }

    public void setCommentsExternal(String commentsExternal) {
        this.commentsExternal = commentsExternal;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getCreatorStatement() {
        return creatorStatement;
    }

    public void setCreatorStatement(String creatorStatement) {
        this.creatorStatement = creatorStatement;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public void setCommentsInternal(String commentsInternal) {
        this.commentsInternal = commentsInternal;
    }

    public String getBestCopy() {
        return bestCopy;
    }

    public void setBestCopy(String bestCopy) {
        this.bestCopy = bestCopy;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public void setDcmRecordCreator(String dcmRecordCreator) {
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public String getDcmCopyPid() {
        return dcmCopyPid;
    }

    public void setDcmCopyPid(String dcmCopyPid) {
        this.dcmCopyPid = dcmCopyPid;
    }
}
