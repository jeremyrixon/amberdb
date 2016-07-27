package amberdb.v2.model;

import amberdb.v2.model.mapper.AmberDbMapperFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import java.util.Date;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
public class Work extends AmberModel {

    @Column
    private Date dcmDateTimeCreated;
    @Column
    private Date dcmDateTimeUpdated;
    @Column
    private String dcmRecordUpdater;
    @Column
    private String dcmWorkPid;
    @Column
    private String dcmAltPi;
    @Column
    private String dcmRecordCreator;
    @Column
    private Boolean displayTitlePage;
    @Column
    private String title;
    @Column
    private String additionalTitle;
    @Column
    private String otherTitle;
    @Column
    private String uniformTitle;
    @Column
    private String alias;
    @Column
    private String author;
    @Column
    private String publisher;
    @Column
    private String contributor;
    @Column
    private String additionalContributor;
    @Column
    private String localSystemNumber;
    @Column
    private String localSystemNo;
    @Column
    private String classification;
    @Column
    private String collection;
    @Column
    private String extent;
    @Column
    private String subType;
    @Column
    private String subUnitNo;
    @Column
    private String subUnitType;
    @Column
    private String occupation;
    @Column
    private Date startDate;
    @Column
    private Date endDate;
    @Column
    private Date expiryDate;
    @Column
    private Date digitalStatusDate;
    @Column
    private String acquisitionStatus;
    @Column
    private String acquisitionCategory;
    @Column
    private String publicationCategory;
    @Column
    private String reorderType;
    @Column
    private String immutable;
    @Column
    private String copyrightPolicy;
    @Column
    private String hasRepresentation;
    @Column
    private String totalDuration;
    @Column
    private String scaleEtc;
    @Column
    private String firstPart;
    @Column
    private String commentsInternal;
    @Column
    private String commentsExternal;
    @Column
    private String restrictionType;
    @Column
    private Date ilmsSentDateTime;
    @Column
    private Boolean moreIlmsDetailsRequired;
    @Column
    private Boolean sendToIlms;
    @Column
    private Date sendToIlmsDateTime;
    @Column
    private String tilePosition;
    @Column
    private Boolean allowHighResdownload;
    @Column
    private String accessConditions;
    @Column
    private String internalAccessConditions;
    @Column
    private String eadUpdateReviewRequired;
    @Column
    private Boolean australianContent;
    @Column
    private String rights;
    @Column
    private String genre;
    @Column
    private String deliveryUrl;
    @Column
    private String imageServerUrl;
    @Column
    private String recordSource;
    @Column
    private String sheetCreationDate;
    @Column
    private String sheetName;
    @Column
    private String creator;
    @Column
    private String creatorStatement;
    @Column
    private String additionalCreator;
    @Column
    private String folderType;
    @Column
    private String folderNumber;
    @Column
    private String eventNote;
    @Column
    private String notes;
    @Column
    private String publicNotes;
    @Column
    private String findingAidNote;
    @Column
    private Boolean interactiveIndexAvailable;
    @Column
    private String startChild;
    @Column
    private String bibLevel;
    @Column
    private String standardId;
    @Column
    private String representativeId;
    @Column
    private String holdingId;
    @Column
    private String holdingNumber;
    @Column
    private String tempHolding;
    @Column
    private String sortIndex;
    @Column
    private Boolean isMissingPage;
    @Column
    private String series;
    @Column
    private String edition;
    @Column
    private String reorder;
    @Column
    private String additionalSeries;
    @Column
    private String constraint1;
    @Column
    private String parentConstraint;
    @Column
    private String catalogueUrl;
    @Column
    private String encodingLevel;
    @Column
    private Boolean materialFromMultipleSources;
    @Column
    private String subject;
    @Column
    private String vendorId;
    @Column
    private Boolean allowOnsiteAccess;
    @Column
    private String language;
    @Column
    private String sensitiveMaterial;
    @Column
    private String html;
    @Column
    private String preservicaId;
    @Column
    private String redocworksReason;
    @Column
    private Boolean workCreatedDuringMigration;
    @Column
    private String nextStep;
    @Column
    private int ingestJobId;
    @Column
    private String rdsAcknowledgementType;
    @Column
    private String rdsAcknowledgementReceiver;
    @Column
    private Date issueDate;
    @Column
    private String bibId;
    @Column
    private String coverage;
    @Column
    private String summary;
    @Column
    private String sensitiveReason;
    @Column
    private String carrier;
    @Column
    private String form;
    @Column
    private String digitalStatus;
    @Column
    private String sprightlyUrl;
    @Column
    private String depositType;
    @Column
    private String coordinates;
    @Column
    private String north;
    @Column
    private String south;
    @Column
    private String east;
    @Column
    private String west;
    @Column
    private String restrictionsOnAccess;
    @Column
    private String preservicaType;

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

    public String getAdditionalTitle() {
        return additionalTitle;
    }

    public void setAdditionalTitle(String additionalTitle) {
        this.additionalTitle = additionalTitle;
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

    public String getEadUpdateReviewRequired() {
        return eadUpdateReviewRequired;
    }

    public void setEadUpdateReviewRequired(String eadUpdateReviewRequired) {
        this.eadUpdateReviewRequired = eadUpdateReviewRequired;
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

    public Boolean getIsMissingPage() {
        return isMissingPage;
    }

    public void setIsMissingPage(Boolean missingPage) {
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
    
	public String getRestrictionsOnAccess() {
		return restrictionsOnAccess;
	}

	public void setRestrictionsOnAccess(String restrictionsOnAccess) {
		this.restrictionsOnAccess = restrictionsOnAccess;
	}

	public String getPreservicaType() {
		return preservicaType;
	}

	public void setPreservicaType(String preservicaType) {
		this.preservicaType = preservicaType;
	}

	@Transient
    public EADWork asEADWork() {
    	return this instanceof EADWork ? (EADWork) this : null;
    }


}
