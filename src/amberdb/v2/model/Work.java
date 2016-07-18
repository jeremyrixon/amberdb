package amberdb.v2.model;

import amberdb.v2.model.mapper.MapWith;
import amberdb.v2.model.mapper.WorkMapper;

import java.util.Date;

@MapWith(WorkMapper.class)
public class Work extends Node {

    private Date dcmDateTimeCreated;

    private Date dcmDateTimeUpdated;

    private String dcmRecordUpdater;

    private String dcmWorkPid;

    private String dcmAltPi;

    private String dcmRecordCreator;

    private Boolean displayTitlePage;

    private String title;

    private String addtionalTitle;

    private String otherTitle;

    private String uniformTitle;

    private String alias;

    private String author;

    private String publisher;

    private String contributor;

    private String additionalContributor;

    private String localSystemNumber;

    private String localSystemNo;

    private String classification;

    private String collection;

    private String extent;

    private String subType;

    private String subUnitNo;

    private String subUnitType;

    private String occupation;

    private Date startDate;

    private Date endDate;

    private Date expiryDate;

    private Date digitalStatusDate;

    private String acquisitionStatus;

    private String acquisitionCategory;

    private String publicationCategory;

    private String reorderType;

    private String immutable;

    private String copyrightPolicy;

    private String hasRepresentation;

    private String totalDuration;

    private String scaleEtc;

    private String firstPart;

    private String commentsInternal;

    private String commentsExternal;

    private String restrictionType;

    private Date ilmsSentDateTime;

    private Boolean moreIlmsDetailsRequired;

    private Boolean sendToIlms;

    private Date sendToIlmsDateTime;

    private String tilePosition;

    private Boolean allowHighResdownload;

    private String accessConditions;

    private String internalAccessConditions;

    private String EADUpdateReviewRequired;

    private Boolean australianContent;

    private String rights;

    private String genre;

    private String deliveryUrl;

    private String imageServerUrl;

    private String recordSource;

    private String sheetCreationDate;

    private String sheetName;

    private String creator;

    private String creatorStatement;

    private String additionalCreator;

    private String folderType;

    private String folderNumber;

    private String eventNote;

    private String notes;

    private String publicNotes;

    private String findingAidNote;

    private Boolean interactiveIndexAvailable;

    private String startChild;

    private String bibLevel;

    private String standardId;

    private String representativeId;

    private String holdingId;

    private String holdingNumber;

    private String tempHolding;

    private String sortIndex;

    private Boolean isMissingPage;

    private String series;

    private String edition;

    private String reorder;

    private String additionalSeries;

    private String constraint1;

    private String parentConstraint;

    private String catalogueUrl;

    private String encodingLevel;

    private Boolean materialFromMultipleSources;

    private String subject;

    private String vendorId;

    private Boolean allowOnsiteAccess;

    private String language;

    private String sensitiveMaterial;

    private String html;

    private String preservicaId;

    private String redocworksReason;

    private Boolean workCreatedDuringMigration;

    private String nextStep;

    private int ingestJobId;

    private String rdsAcknowledgementType;

    private String rdsAcknowledgementReceiver;

    private Date issueDate;

    private String bibId;

    private String coverage;

    private String summary;

    private String sensitiveReason;

    private String carrier;

    private String form;

    private String digitalStatus;

    private String sprightlyUrl;

    private String depositType;

    private String coordinates;

    private String north;

    private String south;

    private String east;

    private String west;

    public Work(int id, int txn_start, int txn_end,
                Date dcmDateTimeCreated, Date dcmDateTimeUpdated, String dcmRecordUpdater, String dcmWorkPid,
                String dcmAltPi, String dcmRecordCreator, Boolean displayTitlePage, String title, String addtionalTitle,
                String otherTitle, String uniformTitle, String alias, String author, String publisher, String contributor,
                String additionalContributor, String localSystemNumber, String localSystemNo, String classification,
                String collection, String extent, String subType, String subUnitNo, String subUnitType, String occupation,
                Date startDate, Date endDate, Date expiryDate, Date digitalStatusDate, String acquisitionStatus,
                String acquisitionCategory, String publicationCategory, String reorderType, String immutable,
                String copyrightPolicy, String hasRepresentation, String totalDuration, String scaleEtc, String firstPart,
                String commentsInternal, String commentsExternal, String restrictionType, Date ilmsSentDateTime,
                Boolean moreIlmsDetailsRequired, Boolean sendToIlms, Date sendToIlmsDateTime, String tilePosition,
                Boolean allowHighResdownload, String accessConditions, String internalAccessConditions,
                String EADUpdateReviewRequired, Boolean australianContent, String rights, String genre,
                String deliveryUrl, String imageServerUrl, String recordSource, String sheetCreationDate,
                String sheetName, String creator, String creatorStatement, String additionalCreator, String folderType,
                String folderNumber, String eventNote, String notes, String publicNotes, String findingAidNote,
                Boolean interactiveIndexAvailable, String startChild, String bibLevel, String standardId,
                String representativeId, String holdingId, String holdingNumber, String tempHolding, String sortIndex,
                Boolean isMissingPage, String series, String edition, String reorder, String additionalSeries,
                String constraint1, String parentConstraint, String catalogueUrl, String encodingLevel,
                Boolean materialFromMultipleSources, String subject, String vendorId, Boolean allowOnsiteAccess,
                String language, String sensitiveMaterial, String html, String preservicaId, String redocworksReason,
                Boolean workCreatedDuringMigration, String nextStep, int ingestJobId, String rdsAcknowledgementType,
                String rdsAcknowledgementReceiver, Date issueDate, String bibId, String coverage, String summary,
                String sensitiveReason, String carrier, String form, String digitalStatus, String sprightlyUrl,
                String depositType, String coordinates, String north, String south, String east, String west) {

        super(id, txn_start, txn_end);
        this.dcmDateTimeCreated = dcmDateTimeCreated;
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
        this.dcmRecordUpdater = dcmRecordUpdater;
        this.dcmWorkPid = dcmWorkPid;
        this.dcmAltPi = dcmAltPi;
        this.dcmRecordCreator = dcmRecordCreator;
        this.displayTitlePage = displayTitlePage;
        this.title = title;
        this.addtionalTitle = addtionalTitle;
        this.otherTitle = otherTitle;
        this.uniformTitle = uniformTitle;
        this.alias = alias;
        this.author = author;
        this.publisher = publisher;
        this.contributor = contributor;
        this.additionalContributor = additionalContributor;
        this.localSystemNumber = localSystemNumber;
        this.localSystemNo = localSystemNo;
        this.classification = classification;
        this.collection = collection;
        this.extent = extent;
        this.subType = subType;
        this.subUnitNo = subUnitNo;
        this.subUnitType = subUnitType;
        this.occupation = occupation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.digitalStatusDate = digitalStatusDate;
        this.acquisitionStatus = acquisitionStatus;
        this.acquisitionCategory = acquisitionCategory;
        this.publicationCategory = publicationCategory;
        this.reorderType = reorderType;
        this.immutable = immutable;
        this.copyrightPolicy = copyrightPolicy;
        this.hasRepresentation = hasRepresentation;
        this.totalDuration = totalDuration;
        this.scaleEtc = scaleEtc;
        this.firstPart = firstPart;
        this.commentsInternal = commentsInternal;
        this.commentsExternal = commentsExternal;
        this.restrictionType = restrictionType;
        this.ilmsSentDateTime = ilmsSentDateTime;
        this.moreIlmsDetailsRequired = moreIlmsDetailsRequired;
        this.sendToIlms = sendToIlms;
        this.sendToIlmsDateTime = sendToIlmsDateTime;
        this.tilePosition = tilePosition;
        this.allowHighResdownload = allowHighResdownload;
        this.accessConditions = accessConditions;
        this.internalAccessConditions = internalAccessConditions;
        this.EADUpdateReviewRequired = EADUpdateReviewRequired;
        this.australianContent = australianContent;
        this.rights = rights;
        this.genre = genre;
        this.deliveryUrl = deliveryUrl;
        this.imageServerUrl = imageServerUrl;
        this.recordSource = recordSource;
        this.sheetCreationDate = sheetCreationDate;
        this.sheetName = sheetName;
        this.creator = creator;
        this.creatorStatement = creatorStatement;
        this.additionalCreator = additionalCreator;
        this.folderType = folderType;
        this.folderNumber = folderNumber;
        this.eventNote = eventNote;
        this.notes = notes;
        this.publicNotes = publicNotes;
        this.findingAidNote = findingAidNote;
        this.interactiveIndexAvailable = interactiveIndexAvailable;
        this.startChild = startChild;
        this.bibLevel = bibLevel;
        this.standardId = standardId;
        this.representativeId = representativeId;
        this.holdingId = holdingId;
        this.holdingNumber = holdingNumber;
        this.tempHolding = tempHolding;
        this.sortIndex = sortIndex;
        this.isMissingPage = isMissingPage;
        this.series = series;
        this.edition = edition;
        this.reorder = reorder;
        this.additionalSeries = additionalSeries;
        this.constraint1 = constraint1;
        this.parentConstraint = parentConstraint;
        this.catalogueUrl = catalogueUrl;
        this.encodingLevel = encodingLevel;
        this.materialFromMultipleSources = materialFromMultipleSources;
        this.subject = subject;
        this.vendorId = vendorId;
        this.allowOnsiteAccess = allowOnsiteAccess;
        this.language = language;
        this.sensitiveMaterial = sensitiveMaterial;
        this.html = html;
        this.preservicaId = preservicaId;
        this.redocworksReason = redocworksReason;
        this.workCreatedDuringMigration = workCreatedDuringMigration;
        this.nextStep = nextStep;
        this.ingestJobId = ingestJobId;
        this.rdsAcknowledgementType = rdsAcknowledgementType;
        this.rdsAcknowledgementReceiver = rdsAcknowledgementReceiver;
        this.issueDate = issueDate;
        this.bibId = bibId;
        this.coverage = coverage;
        this.summary = summary;
        this.sensitiveReason = sensitiveReason;
        this.carrier = carrier;
        this.form = form;
        this.digitalStatus = digitalStatus;
        this.sprightlyUrl = sprightlyUrl;
        this.depositType = depositType;
        this.coordinates = coordinates;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;

    }

    public Date getDcmDateTimeCreated() {
        return dcmDateTimeCreated;
    }

    public Date getDcmDateTimeUpdated() {
        return dcmDateTimeUpdated;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public String getDcmWorkPid() {
        return dcmWorkPid;
    }

    public String getDcmAltPi() {
        return dcmAltPi;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public Boolean getDisplayTitlePage() {
        return displayTitlePage;
    }

    public String getTitle() {
        return title;
    }

    public String getAddtionalTitle() {
        return addtionalTitle;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public String getAlias() {
        return alias;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getContributor() {
        return contributor;
    }

    public String getAdditionalContributor() {
        return additionalContributor;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public String getLocalSystemNo() {
        return localSystemNo;
    }

    public String getClassification() {
        return classification;
    }

    public String getCollection() {
        return collection;
    }

    public String getExtent() {
        return extent;
    }

    public String getSubType() {
        return subType;
    }

    public String getSubUnitNo() {
        return subUnitNo;
    }

    public String getSubUnitType() {
        return subUnitType;
    }

    public String getOccupation() {
        return occupation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Date getDigitalStatusDate() {
        return digitalStatusDate;
    }

    public String getAcquisitionStatus() {
        return acquisitionStatus;
    }

    public String getAcquisitionCategory() {
        return acquisitionCategory;
    }

    public String getPublicationCategory() {
        return publicationCategory;
    }

    public String getReorderType() {
        return reorderType;
    }

    public String getImmutable() {
        return immutable;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public String getHasRepresentation() {
        return hasRepresentation;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public String getFirstPart() {
        return firstPart;
    }

    public String getCommentsInternal() {
        return commentsInternal;
    }

    public String getCommentsExternal() {
        return commentsExternal;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public Date getIlmsSentDateTime() {
        return ilmsSentDateTime;
    }

    public Boolean getMoreIlmsDetailsRequired() {
        return moreIlmsDetailsRequired;
    }

    public Boolean getSendToIlms() {
        return sendToIlms;
    }

    public Date getSendToIlmsDateTime() {
        return sendToIlmsDateTime;
    }

    public String getTilePosition() {
        return tilePosition;
    }

    public Boolean getAllowHighResdownload() {
        return allowHighResdownload;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public String getInternalAccessConditions() {
        return internalAccessConditions;
    }

    public String getEADUpdateReviewRequired() {
        return EADUpdateReviewRequired;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public String getRights() {
        return rights;
    }

    public String getGenre() {
        return genre;
    }

    public String getDeliveryUrl() {
        return deliveryUrl;
    }

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public String getSheetCreationDate() {
        return sheetCreationDate;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getCreator() {
        return creator;
    }

    public String getCreatorStatement() {
        return creatorStatement;
    }

    public String getAdditionalCreator() {
        return additionalCreator;
    }

    public String getFolderType() {
        return folderType;
    }

    public String getFolderNumber() {
        return folderNumber;
    }

    public String getEventNote() {
        return eventNote;
    }

    public String getNotes() {
        return notes;
    }

    public String getPublicNotes() {
        return publicNotes;
    }

    public String getFindingAidNote() {
        return findingAidNote;
    }

    public Boolean getInteractiveIndexAvailable() {
        return interactiveIndexAvailable;
    }

    public String getStartChild() {
        return startChild;
    }

    public String getBibLevel() {
        return bibLevel;
    }

    public String getStandardId() {
        return standardId;
    }

    public String getRepresentativeId() {
        return representativeId;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public String getTempHolding() {
        return tempHolding;
    }

    public String getSortIndex() {
        return sortIndex;
    }

    public Boolean getMissingPage() {
        return isMissingPage;
    }

    public String getSeries() {
        return series;
    }

    public String getEdition() {
        return edition;
    }

    public String getReorder() {
        return reorder;
    }

    public String getAdditionalSeries() {
        return additionalSeries;
    }

    public String getConstraint1() {
        return constraint1;
    }

    public String getParentConstraint() {
        return parentConstraint;
    }

    public String getCatalogueUrl() {
        return catalogueUrl;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public Boolean getMaterialFromMultipleSources() {
        return materialFromMultipleSources;
    }

    public String getSubject() {
        return subject;
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

    public String getHtml() {
        return html;
    }

    public String getPreservicaId() {
        return preservicaId;
    }

    public String getRedocworksReason() {
        return redocworksReason;
    }

    public Boolean getWorkCreatedDuringMigration() {
        return workCreatedDuringMigration;
    }

    public String getNextStep() {
        return nextStep;
    }

    public int getIngestJobId() {
        return ingestJobId;
    }

    public String getRdsAcknowledgementType() {
        return rdsAcknowledgementType;
    }

    public String getRdsAcknowledgementReceiver() {
        return rdsAcknowledgementReceiver;
    }

    public Date getIssueDate() {
        return issueDate;
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

    public String getSensitiveReason() {
        return sensitiveReason;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getForm() {
        return form;
    }

    public String getDigitalStatus() {
        return digitalStatus;
    }

    public String getSprightlyUrl() {
        return sprightlyUrl;
    }

    public String getDepositType() {
        return depositType;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getNorth() {
        return north;
    }

    public String getSouth() {
        return south;
    }

    public String getEast() {
        return east;
    }

    public String getWest() {
        return west;
    }


}
