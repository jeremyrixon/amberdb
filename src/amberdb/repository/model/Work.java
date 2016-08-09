package amberdb.repository.model;

import amberdb.repository.dao.WorkDao;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Work extends AmberModel {

    @Column(name="abstract")
    protected String abstractText;
    @Column
    protected String category;
    @Column
    protected String dcmAltPi;
    @Column
    protected String dcmWorkPid;
    @Column
    protected Date dcmDateTimeCreated;
    @Column
    protected Date dcmDateTimeUpdated;
    @Column
    protected String dcmRecordCreator;
    @Column
    protected String dcmRecordUpdater;
    @Column
    protected String subUnitType;
    @Column
    protected String subUnitNo;
    @Column
    protected String subType;
    @Column
    protected Date issueDate;
    @Column
    protected String collection;
    @Column
    protected String depositType;
    @Column
    protected String form;
    @Column
    protected Boolean displayTitlePage;
    @Column
    protected String bibLevel;
    @Column
    protected String digitalStatus;
    @Column
    protected Date digitalStatusDate;
    @Column
    protected String heading;
    @Column
    protected String subHeadings;
    /**
     * Also known as CALLNO
     */
    @Column
    protected String holdingNumber;
    @Column
    protected String holdingId;
    @Column
    protected String issn;
    @Column
    protected String title;
    @Column
    protected String creator;
    @Column
    protected String creatorStatement;
    @Column
    protected String publisher;
    @Column
    protected String copyrightPolicy;
    @Column
    protected String commercialStatus;
    @Column
    protected String edition;
    @Column
    protected String immutable;
    @Column
    protected Date startDate;
    @Column
    protected Date endDate;
    @Column
    protected String extent;
    @Column
    protected String language;
    @Column
    protected String addressee;
    @Column
    protected String childRange;
    @Column
    protected String startChild;
    @Column
    protected String endChild;
    @Column
    protected String encodingLevel;
    @Column
    protected String publicationLevel;
    @Column
    protected String genre;
    @Column
    protected String publicationCategory;
    @Column
    protected Boolean sendToIlms;
    @Column
    protected Long ingestJobId;
    @Column
    protected Boolean moreIlmsDetailsRequired;
    @Column
    protected Boolean allowHighResdownload;
    @Column
    protected Date ilmsSentDateTime;
    @Column
    protected Boolean interactiveIndexAvailable;
    @Column
    protected String html;
    @Column
    protected Boolean isMissingPage;
    @Column
    protected Boolean workCreatedDuringMigration;
    @Column
    protected String additionalSeriesStatement;
    @Column
    protected String sheetName;
    @Column
    protected String sheetCreationDate;
    @Column
    protected String vendorId;
    @Column
    protected String totalDuration;
    @Column
    protected String preservicaType;
    @Column
    protected String preservicaId;
    @Column
    protected Boolean allowOnsiteAccess;
    @Column
    protected String series;
    @Column
    protected String classification;
    @Column
    protected String contributor;
    @Column
    protected String coverage;
    @Column
    protected String occupation;
    @Column
    protected String otherTitle;
    @Column
    protected String standardId;
    @Column
    protected String subject;
    @Column
    protected String scaleEtc;
    @Column
    protected String tilePosition;
    @Column
    protected String workPid;
    @Column
    protected String constraint;
    @Column
    protected String rights;
    @Column
    protected String tempHolding;
    @Column
    protected String sensitiveMaterial;
    @Column
    protected String sensitiveReason;
    @Column
    protected String restrictionsOnAccess;
    @Column
    protected String findingAidNote;
    @Column
    protected String eventNote;
    @Column
    protected String uniformTitle;
    @Column
    protected String alternativeTitle;
    @Column
    protected String summary;
    @Column
    protected String bibId;
    @Column
    protected String publicNotes;
    @Column
    protected Boolean australianContent;
    @Column
    protected Boolean materialFromMultipleSources;
    @Column
    protected String acquisitionStatus;
    @Column
    protected String acquisitionCategory;
    @Column
    protected String additionalTitle;
    @Column
    protected String additionalContributor;
    @Column
    protected String additionalCreator;
    @Column
    protected String additionalSeries;

    protected WorkDao workDao;

    public Work() {
        super();
        workDao = jdbiHelper.getDbi().onDemand(WorkDao.class);
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDcmAltPi(String dcmAltPi) {
        this.dcmAltPi = dcmAltPi;
    }

    public String getDcmWorkPid() {
        return dcmWorkPid;
    }

    public void setDcmWorkPid(String dcmWorkPid) {
        this.dcmWorkPid = dcmWorkPid;
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

    public String getDcmRecordCreator() {
        return dcmRecordCreator;
    }

    public void setDcmRecordCreator(String dcmRecordCreator) {
        this.dcmRecordCreator = dcmRecordCreator;
    }

    public String getDcmRecordUpdater() {
        return dcmRecordUpdater;
    }

    public void setDcmRecordUpdater(String dcmRecordUpdater) {
        this.dcmRecordUpdater = dcmRecordUpdater;
    }

    public String getSubUnitType() {
        return subUnitType;
    }

    public void setSubUnitType(String subUnitType) {
        this.subUnitType = subUnitType;
    }

    public String getSubUnitNo() {
        return subUnitNo;
    }

    public void setSubUnitNo(String subUnitNo) {
        this.subUnitNo = subUnitNo;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Boolean getDisplayTitlePage() {
        return displayTitlePage;
    }

    public void setDisplayTitlePage(Boolean displayTitlePage) {
        this.displayTitlePage = displayTitlePage;
    }

    public String getBibLevel() {
        return bibLevel;
    }

    public void setBibLevel(String bibLevel) {
        this.bibLevel = bibLevel;
    }

    public String getDigitalStatus() {
        return digitalStatus;
    }

    public void setDigitalStatus(String digitalStatus) {
        this.digitalStatus = digitalStatus;
    }

    public Date getDigitalStatusDate() {
        return digitalStatusDate;
    }

    public void setDigitalStatusDate(Date digitalStatusDate) {
        this.digitalStatusDate = digitalStatusDate;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubHeadings() {
        return subHeadings;
    }

    public void setSubHeadings(String subHeadings) {
        this.subHeadings = subHeadings;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCopyrightPolicy() {
        return copyrightPolicy;
    }

    public void setCopyrightPolicy(String copyrightPolicy) {
        this.copyrightPolicy = copyrightPolicy;
    }

    public String getCommercialStatus() {
        return commercialStatus;
    }

    public void setCommercialStatus(String commercialStatus) {
        this.commercialStatus = commercialStatus;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getImmutable() {
        return immutable;
    }

    public void setImmutable(String immutable) {
        this.immutable = immutable;
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

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getChildRange() {
        return childRange;
    }

    public void setChildRange(String childRange) {
        this.childRange = childRange;
    }

    public String getStartChild() {
        return startChild;
    }

    public void setStartChild(String startChild) {
        this.startChild = startChild;
    }

    public String getEndChild() {
        return endChild;
    }

    public void setEndChild(String endChild) {
        this.endChild = endChild;
    }

    public String getEncodingLevel() {
        return encodingLevel;
    }

    public void setEncodingLevel(String encodingLevel) {
        this.encodingLevel = encodingLevel;
    }

    public String getPublicationLevel() {
        return publicationLevel;
    }

    public void setPublicationLevel(String publicationLevel) {
        this.publicationLevel = publicationLevel;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPublicationCategory() {
        return publicationCategory;
    }

    public void setPublicationCategory(String publicationCategory) {
        this.publicationCategory = publicationCategory;
    }

    public Boolean getSendToIlms() {
        return sendToIlms;
    }

    public void setSendToIlms(Boolean sendToIlms) {
        this.sendToIlms = sendToIlms;
    }

    public Long getIngestJobId() {
        return ingestJobId;
    }

    public void setIngestJobId(Long ingestJobId) {
        this.ingestJobId = ingestJobId;
    }

    public Boolean getMoreIlmsDetailsRequired() {
        return moreIlmsDetailsRequired;
    }

    public void setMoreIlmsDetailsRequired(Boolean moreIlmsDetailsRequired) {
        this.moreIlmsDetailsRequired = moreIlmsDetailsRequired;
    }

    public Boolean getAllowHighResdownload() {
        return allowHighResdownload;
    }

    public void setAllowHighResdownload(Boolean allowHighResdownload) {
        this.allowHighResdownload = allowHighResdownload;
    }

    public Date getIlmsSentDateTime() {
        return ilmsSentDateTime;
    }

    public void setIlmsSentDateTime(Date ilmsSentDateTime) {
        this.ilmsSentDateTime = ilmsSentDateTime;
    }

    public Boolean getInteractiveIndexAvailable() {
        return interactiveIndexAvailable;
    }

    public void setInteractiveIndexAvailable(Boolean interactiveIndexAvailable) {
        this.interactiveIndexAvailable = interactiveIndexAvailable;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Boolean getMissingPage() {
        return isMissingPage;
    }

    public void setMissingPage(Boolean missingPage) {
        isMissingPage = missingPage;
    }

    public Boolean getWorkCreatedDuringMigration() {
        return workCreatedDuringMigration;
    }

    public void setWorkCreatedDuringMigration(Boolean workCreatedDuringMigration) {
        this.workCreatedDuringMigration = workCreatedDuringMigration;
    }

    public String getAdditionalSeriesStatement() {
        return additionalSeriesStatement;
    }

    public void setAdditionalSeriesStatement(String additionalSeriesStatement) {
        this.additionalSeriesStatement = additionalSeriesStatement;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSheetCreationDate() {
        return sheetCreationDate;
    }

    public void setSheetCreationDate(String sheetCreationDate) {
        this.sheetCreationDate = sheetCreationDate;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getPreservicaType() {
        return preservicaType;
    }

    public void setPreservicaType(String preservicaType) {
        this.preservicaType = preservicaType;
    }

    public String getPreservicaId() {
        return preservicaId;
    }

    public void setPreservicaId(String preservicaId) {
        this.preservicaId = preservicaId;
    }

    public Boolean getAllowOnsiteAccess() {
        return allowOnsiteAccess;
    }

    public void setAllowOnsiteAccess(Boolean allowOnsiteAccess) {
        this.allowOnsiteAccess = allowOnsiteAccess;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getScaleEtc() {
        return scaleEtc;
    }

    public void setScaleEtc(String scaleEtc) {
        this.scaleEtc = scaleEtc;
    }

    public String getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(String tilePosition) {
        this.tilePosition = tilePosition;
    }

    public String getWorkPid() {
        return workPid;
    }

    public void setWorkPid(String workPid) {
        this.workPid = workPid;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getTempHolding() {
        return tempHolding;
    }

    public void setTempHolding(String tempHolding) {
        this.tempHolding = tempHolding;
    }

    public String getSensitiveMaterial() {
        return sensitiveMaterial;
    }

    public void setSensitiveMaterial(String sensitiveMaterial) {
        this.sensitiveMaterial = sensitiveMaterial;
    }

    public String getSensitiveReason() {
        return sensitiveReason;
    }

    public void setSensitiveReason(String sensitiveReason) {
        this.sensitiveReason = sensitiveReason;
    }

    public String getRestrictionsOnAccess() {
        return restrictionsOnAccess;
    }

    public void setRestrictionsOnAccess(String restrictionsOnAccess) {
        this.restrictionsOnAccess = restrictionsOnAccess;
    }

    public String getFindingAidNote() {
        return findingAidNote;
    }

    public void setFindingAidNote(String findingAidNote) {
        this.findingAidNote = findingAidNote;
    }

    public String getEventNote() {
        return eventNote;
    }

    public void setEventNote(String eventNote) {
        this.eventNote = eventNote;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public void setUniformTitle(String uniformTitle) {
        this.uniformTitle = uniformTitle;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getPublicNotes() {
        return publicNotes;
    }

    public void setPublicNotes(String publicNotes) {
        this.publicNotes = publicNotes;
    }

    public Boolean getAustralianContent() {
        return australianContent;
    }

    public void setAustralianContent(Boolean australianContent) {
        this.australianContent = australianContent;
    }

    public Boolean getMaterialFromMultipleSources() {
        return materialFromMultipleSources;
    }

    public void setMaterialFromMultipleSources(Boolean materialFromMultipleSources) {
        this.materialFromMultipleSources = materialFromMultipleSources;
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

    public String getAdditionalTitle() {
        return additionalTitle;
    }

    public void setAdditionalTitle(String additionalTitle) {
        this.additionalTitle = additionalTitle;
    }

    public String getAdditionalContributor() {
        return additionalContributor;
    }

    public void setAdditionalContributor(String additionalContributor) {
        this.additionalContributor = additionalContributor;
    }

    public String getAdditionalCreator() {
        return additionalCreator;
    }

    public void setAdditionalCreator(String additionalCreator) {
        this.additionalCreator = additionalCreator;
    }

    public String getAdditionalSeries() {
        return additionalSeries;
    }

    public void setAdditionalSeries(String additionalSeries) {
        this.additionalSeries = additionalSeries;
    }

    public WorkDao getWorkDao() {
        return workDao;
    }

    public void setWorkDao(WorkDao workDao) {
        this.workDao = workDao;
    }

    // TODO - Associations. Update below to use DAOs

//    public GeoCoding addGeoCoding() {
//
//    }
//
//    public IPTC addIPTC() {
//
//    }

    // TODO - Update below methods to use DAOs if needed

    public void setDcmAltPi(List<String> list) {

    }

    public List<String> getDcmAltPi() {
        return new ArrayList();
    }
}
