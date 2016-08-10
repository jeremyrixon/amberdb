package amberdb.repository.model;

import amberdb.enums.SubType;
import amberdb.relation.ExistsOn;
import amberdb.relation.IsPartOf;
import amberdb.repository.dao.associations.AckAssociationDao;
import amberdb.repository.dao.WorkDao;
import amberdb.repository.dao.associations.DeliveryWorkAssociationDao;
import amberdb.repository.dao.associations.ParentChildAssociationDao;
import amberdb.repository.dao.associations.PartsAssociationDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;

import javax.persistence.Column;
import java.util.*;

public class Work extends Node {

    @Column(name="abstract")
    protected String abstractText;
    @Column
    protected String category;
    @Column(name="dcmAltPi")
    protected String jsonDcmAltPi;
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
    @Column(name="series")
    protected String jsonSeries;
    @Column(name="classification")
    protected String jsonClassification;
    @Column(name="contributor")
    protected String jsonContributor;
    @Column(name="coverage")
    protected String jsonCoverage;
    @Column(name="occupation")
    protected String jsonOccupation;
    @Column(name="otherTitle")
    protected String jsonOtherTitle;
    @Column(name="standardId")
    protected String jsonStandardId;
    @Column(name="subject")
    protected String jsonSubject;
    @Column(name="scaleEtc")
    protected String jsonScaleEtc;
    @Column
    protected String tilePosition;
    @Column
    protected String workPid;
    @Column(name="scaleEtc")
    protected String jsonConstraint;
    @Column
    protected String rights;
    @Column
    protected String tempHolding;
    @Column
    protected String sensitiveMaterial;
    @Column(name="sensitiveReason")
    protected String jsonSensitiveReason;
    @Column(name="restrictionsOnAccess")
    protected String jsonRestrictionsOnAccess;
    @Column(name="findingAidNote")
    protected String jsonFindingAidNote;
    @Column(name="findingAidNote")
    protected String jsonEventNote;
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
    @Column(name="coordinates")
    protected String jsonCoordinates;
    @Column
    protected String carrier;

    protected WorkDao workDao;
    protected AckAssociationDao ackDao;
    protected DeliveryWorkAssociationDao deliveryWorkDao;
    protected ParentChildAssociationDao parentChildDao;
    protected PartsAssociationDao partsDao;

    public Work() {
        super();
        workDao = jdbiHelper.getDbi().onDemand(WorkDao.class);
        ackDao = jdbiHelper.getDbi().onDemand(AckAssociationDao.class);
        deliveryWorkDao = jdbiHelper.getDbi().onDemand(DeliveryWorkAssociationDao.class);
        parentChildDao = jdbiHelper.getDbi().onDemand(ParentChildAssociationDao.class);
        partsDao = jdbiHelper.getDbi().onDemand(PartsAssociationDao.class);
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

    public void setJSONDcmAltPi(String jsonDcmAltPi) {
        this.jsonDcmAltPi = jsonDcmAltPi;
    }

    public String getJSONDcmAltPi() {
        return jsonDcmAltPi;
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

    public String getJSONSeries() {
        return jsonSeries;
    }

    public void setJSONSeries(String jsonSeries) {
        this.jsonSeries = jsonSeries;
    }

    public String getJSONClassification() {
        return jsonClassification;
    }

    public void setJSONClassification(String classification) {
        this.jsonClassification = jsonClassification;
    }

    public String getJSONContributor() {
        return jsonContributor;
    }

    public void setJSONContributor(String jsonContributor) {
        this.jsonContributor = jsonContributor;
    }

    public String getJSONCoverage() {
        return jsonCoverage;
    }

    public void setJSONCoverage(String jsonCoverage) {
        this.jsonCoverage = jsonCoverage;
    }

    public String getJSONOccupation() {
        return jsonOccupation;
    }

    public void setJSONOccupation(String jsonOccupation) {
        this.jsonOccupation = jsonOccupation;
    }

    public String getJSONOtherTitle() {
        return jsonOtherTitle;
    }

    public void setJSONOtherTitle(String jsonOtherTitle) {
        this.jsonOtherTitle = jsonOtherTitle;
    }

    public String getJSONStandardId() {
        return jsonStandardId;
    }

    public void setJSONStandardId(String jsonStandardId) {
        this.jsonStandardId = jsonStandardId;
    }

    public String getJSONSubject() {
        return jsonSubject;
    }

    public void setJSONSubject(String jsonSubject) {
        this.jsonSubject = jsonSubject;
    }

    public String getJSONScaleEtc() {
        return jsonScaleEtc;
    }

    public void setJSONScaleEtc(String jsonScaleEtc) {
        this.jsonScaleEtc = jsonScaleEtc;
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

    public String getJSONConstraint() {
        return jsonConstraint;
    }

    public void setJSONConstraint(String jsonConstraint) {
        this.jsonConstraint = jsonConstraint;
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

    public String getJSONSensitiveReason() {
        return jsonSensitiveReason;
    }

    public void setJSONSensitiveReason(String jsonSensitiveReason) {
        this.jsonSensitiveReason = jsonSensitiveReason;
    }

    public String getJSONRestrictionsOnAccess() {
        return jsonRestrictionsOnAccess;
    }

    public void setJSONRestrictionsOnAccess(String jsonRestrictionsOnAccess) {
        this.jsonRestrictionsOnAccess = jsonRestrictionsOnAccess;
    }

    public String getJSONFindingAidNote() {
        return jsonFindingAidNote;
    }

    public void setJSONFindingAidNote(String jsonFindingAidNote) {
        this.jsonFindingAidNote = jsonFindingAidNote;
    }

    public String getJSONEventNote() {
        return jsonEventNote;
    }

    public void setJSONEventNote(String jsonEventNote) {
        this.jsonEventNote = jsonEventNote;
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

    public String getJSONCoordinates() {
        return jsonCoordinates;
    }

    public void setJSONCoordinates(String jsonCoordinates) {
        this.jsonCoordinates = jsonCoordinates;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    // TODO - Associations
    // Getters only. Add/remove need to be done in controllers due to needing txn_start/txn_end values

    public GeoCoding getGeoCoding() {
        return (GeoCoding) getDescription("GeoCoding");
    }

    public IPTC getIPTC() {
        return (IPTC) getDescription("IPTC");
    }

    Iterable<Acknowledge> getAcknowledgements() {
        return ackDao.getAcknowledgements(this.getId());
    }

    public List<Acknowledge> getOrderedAcknowledgements() {
        return ackDao.getOrderedAcknowledgements(this.getId());
    }

    public Iterable<Work> getDeliveryWorks() {
        return deliveryWorkDao.getDeliveryWorks(this.getId());
    }

    public Work getDeliveryWorkParent() {
        return deliveryWorkDao.getDeliveryWorkParent(this.getId());
    }

    public void setDeliveryWorkOrder(int position) {
        deliveryWorkDao.setDeliveryWorkOrder(this.getId(), position);
    }

    public Work getParent() {
        return parentChildDao.getParent(this.getId());
    }

    public Iterable<Work> getChildren() {
        return parentChildDao.getChildren(this.getId());
    }

    public void setOrder(int position) {
        setOrder(getParent(), IsPartOf.label, Direction.IN, position);
    }

    public Iterable<Work> getLeafs(SubType subType) {
        return partsDao.getLeafs(this.getId(), IsPartOf.label, subType.code());
    }

    public Iterable<Work> getLeafs(List<String> subTypes) {
        String subType = String.join(", ", subTypes);
        return partsDao.getLeafs(this.getId(), IsPartOf.label, subType);
    }

    public Iterable<Section> getSections(SubType subType) {
        return partsDao.getSections(this.getId(), IsPartOf.label, subType.code());
    }

    public Section asSection() {
        return (Section) this;
    }

    public EADWork asEADWork() {
        return (EADWork) this;
    }

    // TODO - Previously implemented using @JavaHandler

    public void setDcmAltPi(List<String> list) throws JsonProcessingException {
        setJSONDcmAltPi(serialiseToJSON(list));
    }

    public List<String> getDcmAltPi() {
        return deserialiseJSONString(getJSONDcmAltPi());
    }

    public String getHoldingNumberAndId() {
        return getHoldingNumber() + (getHoldingId() != null ? ("|:|" + getHoldingId()) : "");
    }

    public void setHoldingNumberAndId(String holdNumAndId) {
        if (holdNumAndId == null || holdNumAndId.isEmpty()) {
            setHoldingNumber(null);
            setHoldingId(null);
        } else {
            List<String> splitted = Lists.newArrayList(Splitter.on("|:|").split(holdNumAndId));
            if (splitted.size() == 2) {
                setHoldingNumber(splitted.get(0));
                setHoldingId(splitted.get(1));
            }
            else if (splitted.size() == 1) {
                setHoldingNumber(splitted.get(0));
            }
        }
    }

    public List<Work> getPartsOf(List<String> subTypes) {
        List<Work> works = partsDao.getSubType(this.getId(), IsPartOf.label);
        List<Work> filteredWorks = new ArrayList();

        for (Work w : works) {
            if (subTypes.contains(w.getSubType())) {
                filteredWorks.add(w);
            }
        }

        return filteredWorks;
    }

    public List<Work> getExistsOn(List<String> subTypes) {
        List<Work> works = partsDao.getSubType(this.getId(), ExistsOn.label);
        List<Work> filteredWorks = new ArrayList();

        for (Work w : works) {
            if (subTypes.contains(w.getSubType())) {
                filteredWorks.add(w);
            }
        }

        return works;
    }

    public List<Work> getPartsOf(String subType) {
        return getPartsOf(Arrays.asList(new String[]{subType}));
    }

    public List<Work> getExistsOn(String subType) {
        return getExistsOn(Arrays.asList(new String[]{subType}));
    }

    public void setSeries(List<String> series) throws JsonProcessingException {
        setJSONSeries(serialiseToJSON(series));
    }

    public List<String> getSeries() {
        return deserialiseJSONString(getJSONSeries());
    }

    public void setClassification(List<String> classification) throws JsonProcessingException {
        setJSONClassification(serialiseToJSON(classification));
    }

    public List<String> getClassification() {
        return deserialiseJSONString(getJSONClassification());
    }

    public void setContributor(List<String> contributor) throws JsonProcessingException {
        setJSONContributor(serialiseToJSON(contributor));
    }

    public List<String> getContributor() {
        return deserialiseJSONString(getJSONContributor());
    }

    public void setCoverage(List<String> coverage) throws JsonProcessingException {
        setJSONCoverage(serialiseToJSON(coverage));
    }

    public List<String> getCoverage() {
        return deserialiseJSONString(getJSONCoverage());
    }

    public void setOccupation(List<String> occupation) throws JsonProcessingException {
        setJSONOccupation(serialiseToJSON(occupation));
    }

    public List<String> getOccupation() {
        return deserialiseJSONString(getJSONOccupation());
    }

    public void setOtherTitle(List<String> otherTitle) throws JsonProcessingException {
        setJSONOtherTitle(serialiseToJSON(otherTitle));
    }

    public List<String> getOtherTitle() {
        return deserialiseJSONString(getJSONOtherTitle());
    }

    public void setStandardId(List<String> standardId) throws JsonProcessingException {
        setJSONStandardId(serialiseToJSON(standardId));
    }

    public List<String> getStandardId() {
        return deserialiseJSONString(getJSONStandardId());
    }

    public void setSubject(List<String> subject) throws JsonProcessingException {
        setJSONSubject(serialiseToJSON(subject));
    }

    public List<String> getSubject() {
        return deserialiseJSONString(getJSONSubject());
    }

    public void setScaleEtc(List<String> scaleEtc) throws JsonProcessingException {
        setJSONScaleEtc(serialiseToJSON(scaleEtc));
    }

    public List<String> getScaleEtc() {
        return deserialiseJSONString(getJSONScaleEtc());
    }

    public void setConstraint(List<String> constraint) throws JsonProcessingException {
        setJSONConstraint(serialiseToJSON(constraint));
    }

    public Set<String> getConstraint() {
        List<String> list = deserialiseJSONString(getJSONConstraint());
        LinkedHashSet<String> constraint = new LinkedHashSet<>();
        constraint.addAll(list);
        return constraint;
    }

    public void setSensitiveReason(List<String> sensitiveReason) throws JsonProcessingException {
        setJSONSensitiveReason(serialiseToJSON(sensitiveReason));
    }

    public List<String> getSensitiveReason() {
        return deserialiseJSONString(getJSONSensitiveReason());
    }

    public void setRestrictionsOnAccess(List<String> restrictionsOnAccess) throws JsonProcessingException {
        setJSONRestrictionsOnAccess(serialiseToJSON(restrictionsOnAccess));
    }

    public List<String> getRestrictionsOnAccess() {
        return deserialiseJSONString(getJSONRestrictionsOnAccess());
    }

    public void setFindingAidNote(List<String> findingAidNote) throws JsonProcessingException {
        setJSONFindingAidNote(serialiseToJSON(findingAidNote));
    }

    public List<String> getFindingAidNote() {
        return deserialiseJSONString(getJSONFindingAidNote());
    }

    public void setEventNote(List<String> eventNote) throws JsonProcessingException {
        setJSONEventNote(serialiseToJSON(eventNote));
    }

    public List<String> getEventNote() {
        return deserialiseJSONString(getJSONEventNote());
    }

    public void setCoordinates(List<String> coordinates) throws JsonProcessingException {
        setJSONCoordinates(serialiseToJSON(coordinates));
    }

    public List<String> getCoordinates() {
        return deserialiseJSONString(getJSONCoordinates());
    }

    public List<String> getDeliveryWorkIds() {
        return null;
    }
}
