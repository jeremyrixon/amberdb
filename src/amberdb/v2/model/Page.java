package amberdb.v2.model;

import amberdb.v2.model.mapper.MapWith;
import amberdb.v2.model.mapper.PageMapper;

import java.util.Date;

@MapWith(PageMapper.class)
public class Page extends Node {

    private Date dcmDateTimeUpdated;
    private String extent;
    private String notes;
    private String localSystemNumber;
    private String occupation;
    private String encodingLevel;
    private Boolean materialFromMultipleSources;
    private Boolean displayTitlePage;
    private Date endDate;
    private String subject;
    private Boolean sendToIlms;
    private String vendorId;
    private Boolean allowOnsiteAccess;
    private String language;
    private String sensitiveMaterial;
    private String repository;
    private String holdingId;
    private String dcmAltPi;
    private String west;
    private Boolean workCreatedDuringMigration;
    private Date dcmDateTimeCreated;
    private String commentsExternal;
    private String firstPart;
    private String findingAidNote;
    private String collection;
    private String dcmWorkPid;
    private String otherTitle;
    private String classification;
    private String localSystemno;
    private String commentsInternal;
    private String acquisitionStatus;
    private String immutable;
    private String restrictionType;
    private String copyrightPolicy;
    private Date ilmsSentDateTime;
    private String publisher;
    private String nextStep;
    private String subType;
    private String scaleEtc;
    private Date startDate;
    private String tempHolding;
    private String dcmRecordUpdater;
    private String tilePosition;
    private String sortIndex;
    private Boolean allowHighResdownload;
    private String south;
    private String restrictionsOnAccess;
    private Boolean isMissingPage;
    private String north;
    private String standardId;
    private String representativeId;
    private String scopeContent;
    private String accessConditions;
    private String edition;
    private String alternativeTitle;
    private String title;
    private String acquisitionCategory;
    private String internalAccessConditions;
    private String eadUpdateReviewRequired;
    private String subUnitNo;
    private Date expiryDate;
    private Boolean australianContent;
    private Date digitalStatusDate;
    private String east;
    private String contributor;
    private Boolean moreIlmsDetailsRequired;
    private String subUnitType;
    private String uniformTitle;
    private String rights;
    private String alias;
    private String rdsAcknowledgementType;
    private Date issueDate;
    private String recordSource;
    private String bibId;
    private String coverage;
    private String summary;
    private String creator;
    private String sensitiveReason;
    private String coordinates;
    private String creatorStatement;
    private Boolean interactiveIndexAvailable;
    private String bibLevel;
    private String carrier;
    private String holdingNumber;
    private String form;
    private String series;
    private String rdsAcknowledgementReceiver;
    private String constraint1;
    private String digitalStatus;
    private String dcmRecordCreator;
    private String depositType;
    private String parentConstraint;

    public Page(int id, int txn_start, int txn_end, Date dcmDateTimeUpdated, String extent, String notes,
                String localSystemNumber, String occupation, String encodingLevel, Boolean materialFromMultipleSources,
                Boolean displayTitlePage, Date endDate, String subject, Boolean sendToIlms, String vendorId,
                Boolean allowOnsiteAccess, String language, String sensitiveMaterial, String repository,
                String holdingId, String dcmAltPi, String west, Boolean workCreatedDuringMigration,
                Date dcmDateTimeCreated, String commentsExternal, String firstPart, String findingAidNote,
                String collection, String dcmWorkPid, String otherTitle, String classification, String localSystemno,
                String commentsInternal, String acquisitionStatus, String immutable, String restrictionType,
                String copyrightPolicy, Date ilmsSentDateTime, String publisher, String nextStep, String subType,
                String scaleEtc, Date startDate, String tempHolding, String dcmRecordUpdater, String tilePosition,
                String sortIndex, Boolean allowHighResdownload, String south, String restrictionsOnAccess,
                Boolean isMissingPage, String north, String standardId, String representativeId, String scopeContent,
                String accessConditions, String edition, String alternativeTitle, String title, String acquisitionCategory,
                String internalAccessConditions, String eadUpdateReviewRequired, String subUnitNo, Date expiryDate,
                Boolean australianContent, Date digitalStatusDate, String east, String contributor,
                Boolean moreIlmsDetailsRequired, String subUnitType, String uniformTitle, String rights, String alias,
                String rdsAcknowledgementType, Date issueDate, String recordSource, String bibId, String coverage,
                String summary, String creator, String sensitiveReason, String coordinates, String creatorStatement,
                Boolean interactiveIndexAvailable, String bibLevel, String carrier, String holdingNumber, String form,
                String series, String rdsAcknowledgementReceiver, String constraint1, String digitalStatus,
                String dcmRecordCreator, String depositType, String parentConstraint) {
        super(id, txn_start, txn_end);
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
        this.extent = extent;
        this.notes = notes;
        this.localSystemNumber = localSystemNumber;
        this.occupation = occupation;
        this.encodingLevel = encodingLevel;
        this.materialFromMultipleSources = materialFromMultipleSources;
        this.displayTitlePage = displayTitlePage;
        this.endDate = endDate;
        this.subject = subject;
        this.sendToIlms = sendToIlms;
        this.vendorId = vendorId;
        this.allowOnsiteAccess = allowOnsiteAccess;
        this.language = language;
        this.sensitiveMaterial = sensitiveMaterial;
        this.repository = repository;
        this.holdingId = holdingId;
        this.dcmAltPi = dcmAltPi;
        this.west = west;
        this.workCreatedDuringMigration = workCreatedDuringMigration;
        this.dcmDateTimeCreated = dcmDateTimeCreated;
        this.commentsExternal = commentsExternal;
        this.firstPart = firstPart;
        this.findingAidNote = findingAidNote;
        this.collection = collection;
        this.dcmWorkPid = dcmWorkPid;
        this.otherTitle = otherTitle;
        this.classification = classification;
        this.localSystemno = localSystemno;
        this.commentsInternal = commentsInternal;
        this.acquisitionStatus = acquisitionStatus;
        this.immutable = immutable;
        this.restrictionType = restrictionType;
        this.copyrightPolicy = copyrightPolicy;
        this.ilmsSentDateTime = ilmsSentDateTime;
        this.publisher = publisher;
        this.nextStep = nextStep;
        this.subType = subType;
        this.scaleEtc = scaleEtc;
        this.startDate = startDate;
        this.tempHolding = tempHolding;
        this.dcmRecordUpdater = dcmRecordUpdater;
        this.tilePosition = tilePosition;
        this.sortIndex = sortIndex;
        this.allowHighResdownload = allowHighResdownload;
        this.south = south;
        this.restrictionsOnAccess = restrictionsOnAccess;
        this.isMissingPage = isMissingPage;
        this.north = north;
        this.standardId = standardId;
        this.representativeId = representativeId;
        this.scopeContent = scopeContent;
        this.accessConditions = accessConditions;
        this.edition = edition;
        this.alternativeTitle = alternativeTitle;
        this.title = title;
        this.acquisitionCategory = acquisitionCategory;
        this.internalAccessConditions = internalAccessConditions;
        this.eadUpdateReviewRequired = eadUpdateReviewRequired;
        this.subUnitNo = subUnitNo;
        this.expiryDate = expiryDate;
        this.australianContent = australianContent;
        this.digitalStatusDate = digitalStatusDate;
        this.east = east;
        this.contributor = contributor;
        this.moreIlmsDetailsRequired = moreIlmsDetailsRequired;
        this.subUnitType = subUnitType;
        this.uniformTitle = uniformTitle;
        this.rights = rights;
        this.alias = alias;
        this.rdsAcknowledgementType = rdsAcknowledgementType;
        this.issueDate = issueDate;
        this.recordSource = recordSource;
        this.bibId = bibId;
        this.coverage = coverage;
        this.summary = summary;
        this.creator = creator;
        this.sensitiveReason = sensitiveReason;
        this.coordinates = coordinates;
        this.creatorStatement = creatorStatement;
        this.interactiveIndexAvailable = interactiveIndexAvailable;
        this.bibLevel = bibLevel;
        this.carrier = carrier;
        this.holdingNumber = holdingNumber;
        this.form = form;
        this.series = series;
        this.rdsAcknowledgementReceiver = rdsAcknowledgementReceiver;
        this.constraint1 = constraint1;
        this.digitalStatus = digitalStatus;
        this.dcmRecordCreator = dcmRecordCreator;
        this.depositType = depositType;
        this.parentConstraint = parentConstraint;
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
