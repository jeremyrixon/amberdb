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

    public void setDcmDateTimeCreated(Date dcmDateTimeCreated) {
        this.dcmDateTimeCreated = dcmDateTimeCreated;
    }

    public Date getDcmDateTimeUpdated() {
        return dcmDateTimeUpdated;
    }

    public void setDcmDateTimeUpdated(Date dcmDateTimeUpdated) {
        this.dcmDateTimeUpdated = dcmDateTimeUpdated;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public void setDcmRecordUpdater(String dcmRecordUpdater) {
        this.dcmRecordUpdater = dcmRecordUpdater;
    }

    public String getDcmWorkPid() {
        return dcmWorkPid;
    }

    public void setDcmWorkPid(String dcmWorkPid) {
        this.dcmWorkPid = dcmWorkPid;
    }

    public String getDcmAltPi() {
        return dcmAltPi;
    }

    public void setDcmAltPi(String dcmAltPi) {
        this.dcmAltPi = dcmAltPi;
    }

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public void setDcmRecordCreator(String dcmRecordCreator) {
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public Boolean getDisplayTitlePage() {
        return displayTitlePage;
    }

    public void setDisplayTitlePage(Boolean displayTitlePage) {
        this.displayTitlePage = displayTitlePage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddtionalTitle() {
        return addtionalTitle;
    }

    public void setAddtionalTitle(String addtionalTitle) {
        this.addtionalTitle = addtionalTitle;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public void setUniformTitle(String uniformTitle) {
        this.uniformTitle = uniformTitle;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getAdditionalContributor() {
        return additionalContributor;
    }

    public void setAdditionalContributor(String additionalContributor) {
        this.additionalContributor = additionalContributor;
    }

    public String getLocalSystemNumber() {
        return localSystemNumber;
    }

    public void setLocalSystemNumber(String localSystemNumber) {
        this.localSystemNumber = localSystemNumber;
    }

    public String getLocalSystemNo() {
        return localSystemNo;
    }

    public void setLocalSystemNo(String localSystemNo) {
        this.localSystemNo = localSystemNo;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubUnitNo() {
        return subUnitNo;
    }

    public void setSubUnitNo(String subUnitNo) {
        this.subUnitNo = subUnitNo;
    }

    public String getSubUnitType() {
        return subUnitType;
    }

    public void setSubUnitType(String subUnitType) {
        this.subUnitType = subUnitType;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getDigitalStatusDate() {
        return digitalStatusDate;
    }

    public void setDigitalStatusDate(Date digitalStatusDate) {
        this.digitalStatusDate = digitalStatusDate;
    }

    public String getAcquisitionStatus() {
        return acquisitionStatus;
    }

    public void setAcquisitionStatus(String acquisitionStatus) {
        this.acquisitionStatus = acquisitionStatus;
    }

    public String getAcquisitionCategory() {
        return acquisitionCategory;
    }

    public void setAcquisitionCategory(String acquisitionCategory) {
        this.acquisitionCategory = acquisitionCategory;
    }

    public String getPublicationCategory() {
        return publicationCategory;
    }

    public void setPublicationCategory(String publicationCategory) {
        this.publicationCategory = publicationCategory;
    }

    public String getReorderType() {
        return reorderType;
    }

    public void setReorderType(String reorderType) {
        this.reorderType = reorderType;
    }

    public String getImmutable() {
        return immutable;
    }

    public void setImmutable(String immutable) {
        this.immutable = immutable;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public void setCopyrightPolicy(String copyrightPolicy) {
        this.copyrightPolicy = copyrightPolicy;
    }

    public String getHasRepresentation() {
        return hasRepresentation;
    }

    public void setHasRepresentation(String hasRepresentation) {
        this.hasRepresentation = hasRepresentation;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public void setScaleEtc(String scaleEtc) {
        this.scaleEtc = scaleEtc;
    }

    public String getFirstPart() {
        return firstPart;
    }

    public void setFirstPart(String firstPart) {
        this.firstPart = firstPart;
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

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Date getIlmsSentDateTime() {
        return ilmsSentDateTime;
    }

    public void setIlmsSentDateTime(Date ilmsSentDateTime) {
        this.ilmsSentDateTime = ilmsSentDateTime;
    }

    public Boolean getMoreIlmsDetailsRequired() {
        return moreIlmsDetailsRequired;
    }

    public void setMoreIlmsDetailsRequired(Boolean moreIlmsDetailsRequired) {
        this.moreIlmsDetailsRequired = moreIlmsDetailsRequired;
    }

    public Boolean getSendToIlms() {
        return sendToIlms;
    }

    public void setSendToIlms(Boolean sendToIlms) {
        this.sendToIlms = sendToIlms;
    }

    public Date getSendToIlmsDateTime() {
        return sendToIlmsDateTime;
    }

    public void setSendToIlmsDateTime(Date sendToIlmsDateTime) {
        this.sendToIlmsDateTime = sendToIlmsDateTime;
    }

    public String getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(String tilePosition) {
        this.tilePosition = tilePosition;
    }

    public Boolean getAllowHighResdownload() {
        return allowHighResdownload;
    }

    public void setAllowHighResdownload(Boolean allowHighResdownload) {
        this.allowHighResdownload = allowHighResdownload;
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

    public String getEADUpdateReviewRequired() {
        return EADUpdateReviewRequired;
    }

    public void setEADUpdateReviewRequired(String EADUpdateReviewRequired) {
        this.EADUpdateReviewRequired = EADUpdateReviewRequired;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public void setAustralianContent(Boolean australianContent) {
        this.australianContent = australianContent;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDeliveryUrl() {
        return deliveryUrl;
    }

    public void setDeliveryUrl(String deliveryUrl) {
        this.deliveryUrl = deliveryUrl;
    }

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        this.imageServerUrl = imageServerUrl;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getSheetCreationDate() {
        return sheetCreationDate;
    }

    public void setSheetCreationDate(String sheetCreationDate) {
        this.sheetCreationDate = sheetCreationDate;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorStatement() {
        return creatorStatement;
    }

    public void setCreatorStatement(String creatorStatement) {
        this.creatorStatement = creatorStatement;
    }

    public String getAdditionalCreator() {
        return additionalCreator;
    }

    public void setAdditionalCreator(String additionalCreator) {
        this.additionalCreator = additionalCreator;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getFolderNumber() {
        return folderNumber;
    }

    public void setFolderNumber(String folderNumber) {
        this.folderNumber = folderNumber;
    }

    public String getEventNote() {
        return eventNote;
    }

    public void setEventNote(String eventNote) {
        this.eventNote = eventNote;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPublicNotes() {
        return publicNotes;
    }

    public void setPublicNotes(String publicNotes) {
        this.publicNotes = publicNotes;
    }

    public String getFindingAidNote() {
        return findingAidNote;
    }

    public void setFindingAidNote(String findingAidNote) {
        this.findingAidNote = findingAidNote;
    }

    public Boolean getInteractiveIndexAvailable() {
        return interactiveIndexAvailable;
    }

    public void setInteractiveIndexAvailable(Boolean interactiveIndexAvailable) {
        this.interactiveIndexAvailable = interactiveIndexAvailable;
    }

    public String getStartChild() {
        return startChild;
    }

    public void setStartChild(String startChild) {
        this.startChild = startChild;
    }

    public String getBibLevel() {
        return bibLevel;
    }

    public void setBibLevel(String bibLevel) {
        this.bibLevel = bibLevel;
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

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public String getTempHolding() {
        return tempHolding;
    }

    public void setTempHolding(String tempHolding) {
        this.tempHolding = tempHolding;
    }

    public String getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(String sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Boolean getMissingPage() {
        return isMissingPage;
    }

    public void setMissingPage(Boolean missingPage) {
        isMissingPage = missingPage;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getReorder() {
        return reorder;
    }

    public void setReorder(String reorder) {
        this.reorder = reorder;
    }

    public String getAdditionalSeries() {
        return additionalSeries;
    }

    public void setAdditionalSeries(String additionalSeries) {
        this.additionalSeries = additionalSeries;
    }

    public String getConstraint1() {
        return constraint1;
    }

    public void setConstraint1(String constraint1) {
        this.constraint1 = constraint1;
    }

    public String getParentConstraint() {
        return parentConstraint;
    }

    public void setParentConstraint(String parentConstraint) {
        this.parentConstraint = parentConstraint;
    }

    public String getCatalogueUrl() {
        return catalogueUrl;
    }

    public void setCatalogueUrl(String catalogueUrl) {
        this.catalogueUrl = catalogueUrl;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPreservicaId() {
        return preservicaId;
    }

    public void setPreservicaId(String preservicaId) {
        this.preservicaId = preservicaId;
    }

    public String getRedocworksReason() {
        return redocworksReason;
    }

    public void setRedocworksReason(String redocworksReason) {
        this.redocworksReason = redocworksReason;
    }

    public Boolean getWorkCreatedDuringMigration() {
        return workCreatedDuringMigration;
    }

    public void setWorkCreatedDuringMigration(Boolean workCreatedDuringMigration) {
        this.workCreatedDuringMigration = workCreatedDuringMigration;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public int getIngestJobId() {
        return ingestJobId;
    }

    public void setIngestJobId(int ingestJobId) {
        this.ingestJobId = ingestJobId;
    }

    public String getRdsAcknowledgementType() {
        return rdsAcknowledgementType;
    }

    public void setRdsAcknowledgementType(String rdsAcknowledgementType) {
        this.rdsAcknowledgementType = rdsAcknowledgementType;
    }

    public String getRdsAcknowledgementReceiver() {
        return rdsAcknowledgementReceiver;
    }

    public void setRdsAcknowledgementReceiver(String rdsAcknowledgementReceiver) {
        this.rdsAcknowledgementReceiver = rdsAcknowledgementReceiver;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
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

    public String getSensitiveReason() {
        return sensitiveReason;
    }

    public void setSensitiveReason(String sensitiveReason) {
        this.sensitiveReason = sensitiveReason;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getDigitalStatus() {
        return digitalStatus;
    }

    public void setDigitalStatus(String digitalStatus) {
        this.digitalStatus = digitalStatus;
    }

    public String getSprightlyUrl() {
        return sprightlyUrl;
    }

    public void setSprightlyUrl(String sprightlyUrl) {
        this.sprightlyUrl = sprightlyUrl;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getNorth() {
        return north;
    }

    public void setNorth(String north) {
        this.north = north;
    }

    public String getSouth() {
        return south;
    }

    public void setSouth(String south) {
        this.south = south;
    }

    public String getEast() {
        return east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getWest() {
        return west;
    }

    public void setWest(String west) {
        this.west = west;
    }
}
