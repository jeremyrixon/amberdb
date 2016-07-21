package amberdb.v2.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Page extends AmberModel {

    @Column
    private Date dcmDateTimeUpdated;

    @Column
    private String extent;
    @Column
    private String notes;
    @Column
    private String localSystemNumber;
    @Column
    private String occupation;
    @Column
    private String encodingLevel;
    @Column
    private Boolean materialFromMultipleSources;
    @Column
    private Boolean displayTitlePage;
    @Column
    private Date endDate;
    @Column
    private String subject;
    @Column
    private Boolean sendToIlms;
    @Column
    private String vendorId;
    @Column
    private Boolean allowOnsiteAccess;
    @Column
    private String language;
    @Column
    private String sensitiveMaterial;
    @Column
    private String repository;
    @Column
    private String holdingId;
    @Column
    private String dcmAltPi;
    @Column
    private String west;
    @Column
    private Boolean workCreatedDuringMigration;
    @Column
    private Date dcmDateTimeCreated;
    @Column
    private String commentsExternal;
    @Column
    private String firstPart;
    @Column
    private String findingAidNote;
    @Column
    private String collection;
    @Column
    private String dcmWorkPid;
    @Column
    private String otherTitle;
    @Column
    private String classification;
    @Column
    private String localSystemno;
    @Column
    private String commentsInternal;
    @Column
    private String acquisitionStatus;
    @Column
    private String immutable;
    @Column
    private String restrictionType;
    @Column
    private String copyrightPolicy;
    @Column
    private Date ilmsSentDateTime;
    @Column
    private String publisher;
    @Column
    private String nextStep;
    @Column
    private String subType;
    @Column
    private String scaleEtc;
    @Column
    private Date startDate;
    @Column
    private String tempHolding;
    @Column
    private String dcmRecordUpdater;
    @Column
    private String tilePosition;
    @Column
    private String sortIndex;
    @Column
    private Boolean allowHighResdownload;
    @Column
    private String south;
    @Column
    private String restrictionsOnAccess;
    @Column
    private Boolean isMissingPage;
    @Column
    private String north;
    @Column
    private String standardId;
    @Column
    private String representativeId;
    @Column
    private String scopeContent;
    @Column
    private String accessConditions;
    @Column
    private String edition;
    @Column
    private String alternativeTitle;
    @Column
    private String title;
    @Column
    private String acquisitionCategory;
    @Column
    private String internalAccessConditions;
    @Column
    private String eadUpdateReviewRequired;
    @Column
    private String subUnitNo;
    @Column
    private Date expiryDate;
    @Column
    private Boolean australianContent;
    @Column
    private Date digitalStatusDate;
    @Column
    private String east;
    @Column
    private String contributor;
    @Column
    private Boolean moreIlmsDetailsRequired;
    @Column
    private String subUnitType;
    @Column
    private String uniformTitle;
    @Column
    private String rights;
    @Column
    private String alias;
    @Column
    private String rdsAcknowledgementType;
    @Column
    private Date issueDate;
    @Column
    private String recordSource;
    @Column
    private String bibId;
    @Column
    private String coverage;
    @Column
    private String summary;
    @Column
    private String creator;
    @Column
    private String sensitiveReason;
    @Column
    private String coordinates;
    @Column
    private String creatorStatement;
    @Column
    private Boolean interactiveIndexAvailable;
    @Column
    private String bibLevel;
    @Column
    private String carrier;
    @Column
    private String holdingNumber;
    @Column
    private String form;
    @Column
    private String series;
    @Column
    private String rdsAcknowledgementReceiver;
    @Column
    private String constraint1;
    @Column
    private String digitalStatus;
    @Column
    private String dcmRecordCreator;
    @Column
    private String depositType;
    @Column
    private String parentConstraint;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public void setLocalSystemNumber(String localSystemNumber) {
        this.localSystemNumber = localSystemNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public void setEncodingLevel(String encodingLevel) {
        this.encodingLevel = encodingLevel;
    }

    public Boolean getMaterialFromMultipleSources() {
        return materialFromMultipleSources;
    }

    public void setMaterialFromMultipleSources(Boolean materialFromMultipleSources) {
        this.materialFromMultipleSources = materialFromMultipleSources;
    }

    public Boolean getDisplayTitlePage() {
        return displayTitlePage;
    }

    public void setDisplayTitlePage(Boolean displayTitlePage) {
        this.displayTitlePage = displayTitlePage;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getSendToIlms() {
        return sendToIlms;
    }

    public void setSendToIlms(Boolean sendToIlms) {
        this.sendToIlms = sendToIlms;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Boolean getAllowOnsiteAccess() {
        return allowOnsiteAccess;
    }

    public void setAllowOnsiteAccess(Boolean allowOnsiteAccess) {
        this.allowOnsiteAccess = allowOnsiteAccess;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSensitiveMaterial() {
        return sensitiveMaterial;
    }

    public void setSensitiveMaterial(String sensitiveMaterial) {
        this.sensitiveMaterial = sensitiveMaterial;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getDcmAltPi() {
        return dcmAltPi;
    }

    public void setDcmAltPi(String dcmAltPi) {
        this.dcmAltPi = dcmAltPi;
    }

    public String getWest() {
        return west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    public Boolean getWorkCreatedDuringMigration() {
        return workCreatedDuringMigration;
    }

    public void setWorkCreatedDuringMigration(Boolean workCreatedDuringMigration) {
        this.workCreatedDuringMigration = workCreatedDuringMigration;
    }

    public Date getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public void setDcmDateTimeCreated(Date dcmDateTimeCreated) {
        this.dcmDateTimeCreated = dcmDateTimeCreated;
    }

    public String getCommentsExternal() {
        return commentsExternal;
    }

    public void setCommentsExternal(String commentsExternal) {
        this.commentsExternal = commentsExternal;
    }

    public String getFirstPart() {
        return firstPart;
    }

    public void setFirstPart(String firstPart) {
        this.firstPart = firstPart;
    }

    public String getFindingAidNote() {
        return findingAidNote;
    }

    public void setFindingAidNote(String findingAidNote) {
        this.findingAidNote = findingAidNote;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDcmWorkPid() {
        return dcmWorkPid;
    }

    public void setDcmWorkPid(String dcmWorkPid) {
        this.dcmWorkPid = dcmWorkPid;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getLocalSystemno() {
        return localSystemno;
    }

    public void setLocalSystemno(String localSystemno) {
        this.localSystemno = localSystemno;
    }

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public void setCommentsInternal(String commentsInternal) {
        this.commentsInternal = commentsInternal;
    }

    public String getAcquisitionStatus() {
        return acquisitionStatus;
    }

    public void setAcquisitionStatus(String acquisitionStatus) {
        this.acquisitionStatus = acquisitionStatus;
    }

    public String getImmutable() {
        return immutable;
    }

    public void setImmutable(String immutable) {
        this.immutable = immutable;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public void setCopyrightPolicy(String copyrightPolicy) {
        this.copyrightPolicy = copyrightPolicy;
    }

    public Date getIlmsSentDateTime() {
        return ilmsSentDateTime;
    }

    public void setIlmsSentDateTime(Date ilmsSentDateTime) {
        this.ilmsSentDateTime = ilmsSentDateTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public void setScaleEtc(String scaleEtc) {
        this.scaleEtc = scaleEtc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTempHolding() {
        return tempHolding;
    }

    public void setTempHolding(String tempHolding) {
        this.tempHolding = tempHolding;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public void setDcmRecordUpdater(String dcmRecordUpdater) {
        this.dcmRecordUpdater = dcmRecordUpdater;
    }

    public String getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(String tilePosition) {
        this.tilePosition = tilePosition;
    }

    public String getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(String sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Boolean getAllowHighResdownload() {
        return allowHighResdownload;
    }

    public void setAllowHighResdownload(Boolean allowHighResdownload) {
        this.allowHighResdownload = allowHighResdownload;
    }

    public String getSouth() {
        return south;
    }

    public void setSouth(String south) {
        this.south = south;
    }

    public String getRestrictionsOnAccess() {
        return restrictionsOnAccess;
    }

    public void setRestrictionsOnAccess(String restrictionsOnAccess) {
        this.restrictionsOnAccess = restrictionsOnAccess;
    }

    public Boolean getMissingPage() {
        return isMissingPage;
    }

    public void setMissingPage(Boolean missingPage) {
        isMissingPage = missingPage;
    }

    public String getNorth() {
        return north;
    }

    public void setNorth(String north) {
        this.north = north;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

    public String getRepresentativeId() {
        return representativeId;
    }

    public void setRepresentativeId(String representativeId) {
        this.representativeId = representativeId;
    }

    public String getScopeContent() {
        return scopeContent;
    }

    public void setScopeContent(String scopeContent) {
        this.scopeContent = scopeContent;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public void setAccessConditions(String accessConditions) {
        this.accessConditions = accessConditions;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAcquisitionCategory() {
        return acquisitionCategory;
    }

    public void setAcquisitionCategory(String acquisitionCategory) {
        this.acquisitionCategory = acquisitionCategory;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public void setInternalAccessConditions(String internalAccessConditions) {
        this.internalAccessConditions = internalAccessConditions;
    }

    public String getEadUpdateReviewRequired() {
        return eadUpdateReviewRequired;
    }

    public void setEadUpdateReviewRequired(String eadUpdateReviewRequired) {
        this.eadUpdateReviewRequired = eadUpdateReviewRequired;
    }

    public String getSubUnitNo() {
        return subUnitNo;
    }

    public void setSubUnitNo(String subUnitNo) {
        this.subUnitNo = subUnitNo;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public void setAustralianContent(Boolean australianContent) {
        this.australianContent = australianContent;
    }

    public Date getDigitalStatusDate() {
        return digitalStatusDate;
    }

    public void setDigitalStatusDate(Date digitalStatusDate) {
        this.digitalStatusDate = digitalStatusDate;
    }

    public String getEast() {
        return east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public Boolean getMoreIlmsDetailsRequired() {
        return moreIlmsDetailsRequired;
    }

    public void setMoreIlmsDetailsRequired(Boolean moreIlmsDetailsRequired) {
        this.moreIlmsDetailsRequired = moreIlmsDetailsRequired;
    }

    public String getSubUnitType() {
        return subUnitType;
    }

    public void setSubUnitType(String subUnitType) {
        this.subUnitType = subUnitType;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public void setUniformTitle(String uniformTitle) {
        this.uniformTitle = uniformTitle;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRdsAcknowledgementType() {
        return rdsAcknowledgementType;
    }

    public void setRdsAcknowledgementType(String rdsAcknowledgementType) {
        this.rdsAcknowledgementType = rdsAcknowledgementType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSensitiveReason() {
        return sensitiveReason;
    }

    public void setSensitiveReason(String sensitiveReason) {
        this.sensitiveReason = sensitiveReason;
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

    public Boolean getInteractiveIndexAvailable() {
        return interactiveIndexAvailable;
    }

    public void setInteractiveIndexAvailable(Boolean interactiveIndexAvailable) {
        this.interactiveIndexAvailable = interactiveIndexAvailable;
    }

    public String getBibLevel() {
        return bibLevel;
    }

    public void setBibLevel(String bibLevel) {
        this.bibLevel = bibLevel;
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

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getRdsAcknowledgementReceiver() {
        return rdsAcknowledgementReceiver;
    }

    public void setRdsAcknowledgementReceiver(String rdsAcknowledgementReceiver) {
        this.rdsAcknowledgementReceiver = rdsAcknowledgementReceiver;
    }

    public String getConstraint1() {
        return constraint1;
    }

    public void setConstraint1(String constraint1) {
        this.constraint1 = constraint1;
    }

    public String getDigitalStatus() {
        return digitalStatus;
    }

    public void setDigitalStatus(String digitalStatus) {
        this.digitalStatus = digitalStatus;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public void setDcmRecordCreator(String dcmRecordCreator) {
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getParentConstraint() {
        return parentConstraint;
    }

    public void setParentConstraint(String parentConstraint) {
        this.parentConstraint = parentConstraint;
    }
}
