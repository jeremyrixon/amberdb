package amberdb.v2.model;

import amberdb.v2.model.mapper.EADWorkMapper;
import amberdb.v2.model.mapper.MapWith;

import java.util.Date;

@MapWith(EADWorkMapper.class)
public class EADWork extends Node {

    private String extent;
    private Date dcmDateTimeUpdated;
    private String localSystemNumber;
    private String occupation;
    private Boolean materialFromMultipleSources;
    private String encodingLevel;
    private Date endDate;
    private Boolean displayTitlePage;
    private String subject;
    private Boolean sendToIlms;
    private Boolean allowOnsiteAccess;
    private String language;
    private String sensitiveMaterial;
    private String repository;
    private String holdingId;
    private String arrangement;
    private String dcmAltPi;
    private String folderNumber;
    private String collectionNumber;
    private String west;
    private String totalDuration;
    private Boolean workCreatedDuringMigration;
    private String relatedMaterial;
    private Date dcmDateTimeCreated;
    private String findingAidNote;
    private String collection;
    private String dcmWorkPid;
    private String otherTitle;
    private String classification;
    private String commentsInternal;
    private String immutable;
    private String folder;
    private String copyrightPolicy;
    private String nextStep;
    private String publisher;
    private String subType;
    private String copyingPublishing;
    private String scaleEtc;
    private Date startDate;
    private String tempHolding;
    private String dcmRecordUpdater;
    private String access;
    private Boolean allowHighResdownload;
    private String south;
    private Boolean isMissingPage;
    private String restrictionsOnAccess;
    private String north;
    private String scopeContent;
    private String representativeId;
    private String standardId;
    private String accessConditions;
    private String title;
    private String internalAccessConditions;
    private String eadUpdateReviewRequired;
    private String subUnitNo;
    private Date expiryDate;
    private Boolean australianContent;
    private Date digitalStatusDate;
    private String east;
    private String bibliography;
    private String contributor;
    private String provenance;
    private Boolean moreIlmsDetailsRequired;
    private String subUnitType;
    private String rights;
    private String uniformTitle;
    private String rdsAcknowledgementType;
    private String alias;
    private String recordSource;
    private String dateRangeInAS;
    private String coverage;
    private String bibId;
    private String summary;
    private String creator;
    private String preferredCitation;
    private String coordinates;
    private String creatorStatement;
    private String folderType;
    private String bibLevel;
    private String carrier;
    private String holdingNumber;
    private String form;
    private String series;
    private String rdsAcknowledgementReceiver;
    private String constraint1;
    private String digitalStatus;
    private String dcmRecordCreator;

    public EADWork(int id, int txn_start, int txn_end, String extent, Date dcmDateTimeUpdated, String localSystemNumber,
                   String occupation, Boolean materialFromMultipleSources, String encodingLevel, Date endDate,
                   Boolean displayTitlePage, String subject, Boolean sendToIlms, Boolean allowOnsiteAccess,
                   String language, String sensitiveMaterial, String repository, String holdingId, String arrangement,
                   String dcmAltPi, String folderNumber, String collectionNumber, String west, String totalDuration,
                   Boolean workCreatedDuringMigration, String relatedMaterial, Date dcmDateTimeCreated,
                   String findingAidNote, String collection, String dcmWorkPid, String otherTitle,
                   String classification, String commentsInternal, String immutable, String folder,
                   String copyrightPolicy, String nextStep, String publisher, String subType, String copyingPublishing,
                   String scaleEtc, Date startDate, String tempHolding, String dcmRecordUpdater, String access,
                   Boolean allowHighResdownload, String south, Boolean isMissingPage, String restrictionsOnAccess,
                   String north, String scopeContent, String representativeId, String standardId,
                   String accessConditions, String title, String internalAccessConditions,
                   String eadUpdateReviewRequired, String subUnitNo, Date expiryDate, Boolean australianContent,
                   Date digitalStatusDate, String east, String bibliography, String contributor, String provenance,
                   Boolean moreIlmsDetailsRequired, String subUnitType, String rights, String uniformTitle,
                   String rdsAcknowledgementType, String alias, String recordSource, String dateRangeInAS,
                   String coverage, String bibId, String summary, String creator, String preferredCitation,
                   String coordinates, String creatorStatement, String folderType, String bibLevel, String carrier,
                   String holdingNumber, String form, String series, String rdsAcknowledgementReceiver,
                   String constraint1, String digitalStatus, String dcmRecordCreator) {
        super(id, txn_start, txn_end);
        this.extent = extent;
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
        this.localSystemNumber = localSystemNumber;
        this.occupation = occupation;
        this.materialFromMultipleSources = materialFromMultipleSources;
        this.encodingLevel = encodingLevel;
        this.endDate = endDate;
        this.displayTitlePage = displayTitlePage;
        this.subject = subject;
        this.sendToIlms = sendToIlms;
        this.allowOnsiteAccess = allowOnsiteAccess;
        this.language = language;
        this.sensitiveMaterial = sensitiveMaterial;
        this.repository = repository;
        this.holdingId = holdingId;
        this.arrangement = arrangement;
        this.dcmAltPi = dcmAltPi;
        this.folderNumber = folderNumber;
        this.collectionNumber = collectionNumber;
        this.west = west;
        this.totalDuration = totalDuration;
        this.workCreatedDuringMigration = workCreatedDuringMigration;
        this.relatedMaterial = relatedMaterial;
        this.dcmDateTimeCreated = dcmDateTimeCreated;
        this.findingAidNote = findingAidNote;
        this.collection = collection;
        this.dcmWorkPid = dcmWorkPid;
        this.otherTitle = otherTitle;
        this.classification = classification;
        this.commentsInternal = commentsInternal;
        this.immutable = immutable;
        this.folder = folder;
        this.copyrightPolicy = copyrightPolicy;
        this.nextStep = nextStep;
        this.publisher = publisher;
        this.subType = subType;
        this.copyingPublishing = copyingPublishing;
        this.scaleEtc = scaleEtc;
        this.startDate = startDate;
        this.tempHolding = tempHolding;
        this.dcmRecordUpdater = dcmRecordUpdater;
        this.access = access;
        this.allowHighResdownload = allowHighResdownload;
        this.south = south;
        this.isMissingPage = isMissingPage;
        this.restrictionsOnAccess = restrictionsOnAccess;
        this.north = north;
        this.scopeContent = scopeContent;
        this.representativeId = representativeId;
        this.standardId = standardId;
        this.accessConditions = accessConditions;
        this.title = title;
        this.internalAccessConditions = internalAccessConditions;
        this.eadUpdateReviewRequired = eadUpdateReviewRequired;
        this.subUnitNo = subUnitNo;
        this.expiryDate = expiryDate;
        this.australianContent = australianContent;
        this.digitalStatusDate = digitalStatusDate;
        this.east = east;
        this.bibliography = bibliography;
        this.contributor = contributor;
        this.provenance = provenance;
        this.moreIlmsDetailsRequired = moreIlmsDetailsRequired;
        this.subUnitType = subUnitType;
        this.rights = rights;
        this.uniformTitle = uniformTitle;
        this.rdsAcknowledgementType = rdsAcknowledgementType;
        this.alias = alias;
        this.recordSource = recordSource;
        this.dateRangeInAS = dateRangeInAS;
        this.coverage = coverage;
        this.bibId = bibId;
        this.summary = summary;
        this.creator = creator;
        this.preferredCitation = preferredCitation;
        this.coordinates = coordinates;
        this.creatorStatement = creatorStatement;
        this.folderType = folderType;
        this.bibLevel = bibLevel;
        this.carrier = carrier;
        this.holdingNumber = holdingNumber;
        this.form = form;
        this.series = series;
        this.rdsAcknowledgementReceiver = rdsAcknowledgementReceiver;
        this.constraint1 = constraint1;
        this.digitalStatus = digitalStatus;
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public Date getDcmDateTimeUpdated() {
        return dcmDateTimeUpdated;
    }

    public void setDcmDateTimeUpdated(Date dcmDateTimeUpdated) {
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
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

    public Boolean getMaterialFromMultipleSources() {
        return materialFromMultipleSources;
    }

    public void setMaterialFromMultipleSources(Boolean materialFromMultipleSources) {
        this.materialFromMultipleSources = materialFromMultipleSources;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public void setEncodingLevel(String encodingLevel) {
        this.encodingLevel = encodingLevel;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getDisplayTitlePage() {
        return displayTitlePage;
    }

    public void setDisplayTitlePage(Boolean displayTitlePage) {
        this.displayTitlePage = displayTitlePage;
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

    public String getArrangement() {
        return arrangement;
    }

    public void setArrangement(String arrangement) {
        this.arrangement = arrangement;
    }

    public String getDcmAltPi() {
        return dcmAltPi;
    }

    public void setDcmAltPi(String dcmAltPi) {
        this.dcmAltPi = dcmAltPi;
    }

    public String getFolderNumber() {
        return folderNumber;
    }

    public void setFolderNumber(String folderNumber) {
        this.folderNumber = folderNumber;
    }

    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public String getWest() {
        return west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Boolean getWorkCreatedDuringMigration() {
        return workCreatedDuringMigration;
    }

    public void setWorkCreatedDuringMigration(Boolean workCreatedDuringMigration) {
        this.workCreatedDuringMigration = workCreatedDuringMigration;
    }

    public String getRelatedMaterial() {
        return relatedMaterial;
    }

    public void setRelatedMaterial(String relatedMaterial) {
        this.relatedMaterial = relatedMaterial;
    }

    public Date getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public void setDcmDateTimeCreated(Date dcmDateTimeCreated) {
        this.dcmDateTimeCreated = dcmDateTimeCreated;
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

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public void setCommentsInternal(String commentsInternal) {
        this.commentsInternal = commentsInternal;
    }

    public String getImmutable() {
        return immutable;
    }

    public void setImmutable(String immutable) {
        this.immutable = immutable;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public void setCopyrightPolicy(String copyrightPolicy) {
        this.copyrightPolicy = copyrightPolicy;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getCopyingPublishing() {
        return copyingPublishing;
    }

    public void setCopyingPublishing(String copyingPublishing) {
        this.copyingPublishing = copyingPublishing;
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

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
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

    public Boolean getMissingPage() {
        return isMissingPage;
    }

    public void setMissingPage(Boolean missingPage) {
        isMissingPage = missingPage;
    }

    public String getRestrictionsOnAccess() {
        return restrictionsOnAccess;
    }

    public void setRestrictionsOnAccess(String restrictionsOnAccess) {
        this.restrictionsOnAccess = restrictionsOnAccess;
    }

    public String getNorth() {
        return north;
    }

    public void setNorth(String north) {
        this.north = north;
    }

    public String getScopeContent() {
        return scopeContent;
    }

    public void setScopeContent(String scopeContent) {
        this.scopeContent = scopeContent;
    }

    public String getRepresentativeId() {
        return representativeId;
    }

    public void setRepresentativeId(String representativeId) {
        this.representativeId = representativeId;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public void setAccessConditions(String accessConditions) {
        this.accessConditions = accessConditions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
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

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public void setUniformTitle(String uniformTitle) {
        this.uniformTitle = uniformTitle;
    }

    public String getRdsAcknowledgementType() {
        return rdsAcknowledgementType;
    }

    public void setRdsAcknowledgementType(String rdsAcknowledgementType) {
        this.rdsAcknowledgementType = rdsAcknowledgementType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getDateRangeInAS() {
        return dateRangeInAS;
    }

    public void setDateRangeInAS(String dateRangeInAS) {
        this.dateRangeInAS = dateRangeInAS;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
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

    public String getPreferredCitation() {
        return preferredCitation;
    }

    public void setPreferredCitation(String preferredCitation) {
        this.preferredCitation = preferredCitation;
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

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
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
}
