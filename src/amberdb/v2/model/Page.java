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

    public String getExtent() {
        return extent;
    }

    public String getNotes() {
        return notes;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public Boolean getMaterialFromMultipleSources() {
        return materialFromMultipleSources;
    }

    public Boolean getDisplayTitlePage() {
        return displayTitlePage;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getSubject() {
        return subject;
    }

    public Boolean getSendToIlms() {
        return sendToIlms;
    }

    public String getVendorId() {
        return vendorId;
    }

    public Boolean getAllowOnsiteAccess() {
        return allowOnsiteAccess;
    }

    public String getLanguage() {
        return language;
    }

    public String getSensitiveMaterial() {
        return sensitiveMaterial;
    }

    public String getRepository() {
        return repository;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public String getDcmAltPi() {
        return dcmAltPi;
    }

    public String getWest() {
        return west;
    }

    public Boolean getWorkCreatedDuringMigration() {
        return workCreatedDuringMigration;
    }

    public Date getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public String getCommentsExternal() {
        return commentsExternal;
    }

    public String getFirstPart() {
        return firstPart;
    }

    public String getFindingAidNote() {
        return findingAidNote;
    }

    public String getCollection() {
        return collection;
    }

    public String getDcmWorkPid() {
        return dcmWorkPid;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public String getClassification() {
        return classification;
    }

    public String getLocalSystemno() {
        return localSystemno;
    }

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public String getAcquisitionStatus() {
        return acquisitionStatus;
    }

    public String getImmutable() {
        return immutable;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public Date getIlmsSentDateTime() {
        return ilmsSentDateTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getNextStep() {
        return nextStep;
    }

    public String getSubType() {
        return subType;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getTempHolding() {
        return tempHolding;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public String getTilePosition() {
        return tilePosition;
    }

    public String getSortIndex() {
        return sortIndex;
    }

    public Boolean getAllowHighResdownload() {
        return allowHighResdownload;
    }

    public String getSouth() {
        return south;
    }

    public String getRestrictionsOnAccess() {
        return restrictionsOnAccess;
    }

    public Boolean getMissingPage() {
        return isMissingPage;
    }

    public String getNorth() {
        return north;
    }

    public String getStandardId() {
        return standardId;
    }

    public String getRepresentativeId() {
        return representativeId;
    }

    public String getScopeContent() {
        return scopeContent;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public String getEdition() {
        return edition;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getAcquisitionCategory() {
        return acquisitionCategory;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public String getEadUpdateReviewRequired() {
        return eadUpdateReviewRequired;
    }

    public String getSubUnitNo() {
        return subUnitNo;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public Date getDigitalStatusDate() {
        return digitalStatusDate;
    }

    public String getEast() {
        return east;
    }

    public String getContributor() {
        return contributor;
    }

    public Boolean getMoreIlmsDetailsRequired() {
        return moreIlmsDetailsRequired;
    }

    public String getSubUnitType() {
        return subUnitType;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public String getRights() {
        return rights;
    }

    public String getAlias() {
        return alias;
    }

    public String getRdsAcknowledgementType() {
        return rdsAcknowledgementType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public String getBibId() {
        return bibId;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getSummary() {
        return summary;
    }

    public String getCreator() {
        return creator;
    }

    public String getSensitiveReason() {
        return sensitiveReason;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getCreatorStatement() {
        return creatorStatement;
    }

    public Boolean getInteractiveIndexAvailable() {
        return interactiveIndexAvailable;
    }

    public String getBibLevel() {
        return bibLevel;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public String getForm() {
        return form;
    }

    public String getSeries() {
        return series;
    }

    public String getRdsAcknowledgementReceiver() {
        return rdsAcknowledgementReceiver;
    }

    public String getConstraint1() {
        return constraint1;
    }

    public String getDigitalStatus() {
        return digitalStatus;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public String getDepositType() {
        return depositType;
    }

    public String getParentConstraint() {
        return parentConstraint;
    }
}
