package amberdb.v2.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class EADWork extends AmberModel {

    @Column
    private String extent;
    @Column
    private Date dcmDateTimeUpdated;
    @Column
    private String localSystemNumber;
    @Column
    private String occupation;
    @Column
    private Boolean materialFromMultipleSources;
    @Column
    private String encodingLevel;
    @Column
    private Date endDate;
    @Column
    private Boolean displayTitlePage;
    @Column
    private String subject;
    @Column
    private Boolean sendToIlms;
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
    private String arrangement;
    @Column
    private String dcmAltPi;
    @Column
    private String folderNumber;
    @Column
    private String collectionNumber;
    @Column
    private String west;
    @Column
    private String totalDuration;
    @Column
    private Boolean workCreatedDuringMigration;
    @Column
    private String relatedMaterial;
    @Column
    private Date dcmDateTimeCreated;
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
    private String commentsInternal;
    @Column
    private String immutable;
    @Column
    private String folder;
    @Column
    private String copyrightPolicy;
    @Column
    private String nextStep;
    @Column
    private String publisher;
    @Column
    private String subType;
    @Column
    private String copyingPublishing;
    @Column
    private String scaleEtc;
    @Column
    private Date startDate;
    @Column
    private String tempHolding;
    @Column
    private String dcmRecordUpdater;
    @Column
    private String access;
    @Column
    private Boolean allowHighResdownload;
    @Column
    private String south;
    @Column
    private Boolean isMissingPage;
    @Column
    private String restrictionsOnAccess;
    @Column
    private String north;
    @Column
    private String scopeContent;
    @Column
    private String representativeId;
    @Column
    private String standardId;
    @Column
    private String accessConditions;
    @Column
    private String title;
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
    private String bibliography;
    @Column
    private String contributor;
    @Column
    private String provenance;
    @Column
    private Boolean moreIlmsDetailsRequired;
    @Column
    private String subUnitType;
    @Column
    private String rights;
    @Column
    private String uniformTitle;
    @Column
    private String rdsAcknowledgementType;
    @Column
    private String alias;
    @Column
    private String recordSource;
    @Column
    private String dateRangeInAS;
    @Column
    private String coverage;
    @Column
    private String bibId;
    @Column
    private String summary;
    @Column
    private String creator;
    @Column
    private String preferredCitation;
    @Column
    private String coordinates;
    @Column
    private String creatorStatement;
    @Column
    private String folderType;
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
