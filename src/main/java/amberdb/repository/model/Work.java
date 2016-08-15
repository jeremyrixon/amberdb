package amberdb.repository.model;

import amberdb.AmberSession;
import amberdb.model.Coordinates;
import amberdb.enums.CopyRole;
import amberdb.enums.CopyType;
import amberdb.enums.SubType;
import amberdb.relation.ExistsOn;
import amberdb.relation.IsPartOf;
import amberdb.repository.dao.associations.*;
import amberdb.repository.dao.WorkDao;
import amberdb.repository.dao.associations.*;
import amberdb.repository.mappers.AmberDbMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import doss.BlobStore;
import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Entity
@RegisterMapperFactory(AmberDbMapperFactory.class)
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
    protected CopyAssociationDao copyDao;

    static ObjectMapper mapper = new ObjectMapper();

    public Work() {
        super();
        workDao = jdbiHelper.getDbi().onDemand(WorkDao.class);
        ackDao = jdbiHelper.getDbi().onDemand(AckAssociationDao.class);
        deliveryWorkDao = jdbiHelper.getDbi().onDemand(DeliveryWorkAssociationDao.class);
        parentChildDao = jdbiHelper.getDbi().onDemand(ParentChildAssociationDao.class);
        partsDao = jdbiHelper.getDbi().onDemand(PartsAssociationDao.class);
        copyDao = jdbiHelper.getDbi().onDemand(CopyAssociationDao.class);
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

    public GeoCoding addGeoCoding() {
        // TODO
        return null;
    }

    public GeoCoding getGeoCoding() {
        return (GeoCoding) getDescription("GeoCoding");
    }

    public IPTC addIPTC() {
        // TODO
        return null;
    }

    public IPTC getIPTC() {
        return (IPTC) getDescription("IPTC");
    }

    public Iterable<Work> getDeliveryWorks() {
        return deliveryWorkDao.getDeliveryWorks(this.getId());
    }

    public void addDeliveryWork(Work deliveryWork) {
        // TODO
    }

    public void removeDeliveryWork(Work deliveryWork) {
        // TODO
    }

    public Work getDeliveryWorkParent() {
        return deliveryWorkDao.getDeliveryWorkParent(this.getId());
    }

    public void setDeliveryWorkParent(final Work interview) {
        // TODO
    }

    void removeDeliveryWorkParent(final Work interview) {
        // TODO
    }

    public void setDeliveryWorkOrder(int position) {
        deliveryWorkDao.setDeliveryWorkOrder(this.getId(), position);
    }

    public List<String> getDeliveryWorkIds() {
        return null;
    }

    public void removeDeliveryWorks() {
        Iterable<Work> deliveryWorks = getDeliveryWorks();
        for (Work dw : deliveryWorks) {
            dw.removeDeliveryWorkParent(this);
        }
    }

    public void setParent(final Work parent) {
        // TODO
    }

    public void addChild(final Work part) {
        // TODO
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

    public Iterable<Copy> getCopies() {
        return getOrderedCopies();
    }

    public Iterable<Copy> getOrderedCopies() {
        return copyDao.getCopies(this.getId());
    }

    public Iterable<Copy> getOrderedCopies(CopyRole role) {
        return copyDao.getCopies(this.getId(), role.code());
    }

    public Iterable<Copy> getCopies(CopyRole role) {
        return getOrderedCopies(role);
    }

    public Copy getCopy(CopyRole role) {
        return copyDao.getCopy(this.getId(), role.code());
    }

    public Copy addCopy() {
        // TODO
        return null;
    }

    public void addCopy(final Copy copy) {
        // TODO
    }

    public void removeCopy(final Copy copy) {
        // TODO
    }

    public Section addSection() {
        // TODO
        return null;
    }

    public Page addPage() {
        // TODO
        return null;
    }

    public void removePage(final Page page) {
        // TODO
    }

    public void addRepresentative(final Copy copy) {
        // TODO
    }

    public void removeRepresentative(final Copy copy) {
        // TODO
    }

    public Iterable<Copy> getRepresentations() {
        return copyDao.getRepresentations(this.getId());
    }

    public void removePart(final Work part) {
        // TODO
    }

    public Acknowledge addAcknowledgement(final Party party) {
        // TODO
        return null;
    }

    public void removeAcknowledgement(final Acknowledge ack) {
        // TODO
    }

    Iterable<Acknowledge> getAcknowledgements() {
        return ackDao.getAcknowledgements(this.getId());
    }

    public List<Acknowledge> getOrderedAcknowledgements() {
        return ackDao.getOrderedAcknowledgements(this.getId());
    }

    public Acknowledge addAcknowledgement(final Party party, final String ackType, final String kindOfSupport,
                                          final Double weighting, final Date dateOfAck, final String urlToOriginal) {
        Acknowledge ack = addAcknowledgement(party);
        ack.setAckType(ackType);
        ack.setKindOfSupport(kindOfSupport);
        ack.setWeighting(weighting);
        ack.setUrlToOriginal(urlToOriginal);
        ack.setDate(dateOfAck);
        return ack;
    }

    public Page addPage(Path sourceFile, String mimeType, BlobStore blobStore) throws IOException {
        Page page = addPage();
        page.addCopy(sourceFile, CopyRole.MASTER_COPY, mimeType, blobStore);
        return page;
    }

    public Page addLegacyDossPage(Path dossPath, String mimeType, BlobStore blobStore) throws IOException {
        Page page = addPage();
        page.addLegacyDossCopy(dossPath, CopyRole.MASTER_COPY, mimeType, blobStore);
        return page;
    }

    public Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType, BlobStore blobStore) throws IOException {
        Copy copy = addCopy();
        copy.setCopyRole(copyRole.code());
        copy.addFile(sourceFile, mimeType, blobStore);
        return copy;
    }

    public Copy addLegacyDossCopy(Path dossPath, CopyRole copyRole, String mimeType, BlobStore blobStore) throws IOException {
        Copy copy = addCopy();
        copy.setCopyRole(copyRole.code());
        copy.addLegacyDossFile(dossPath, mimeType, blobStore);
        return copy;
    }

    public List<Page> getPages() {
        List<Page> pages = new ArrayList<>();
        Iterable<Work> parts = this.getChildren();
        if (parts != null) {
            for (Work part : parts) {
                pages.add((Page) part);
            }
        }
        return pages;
    }

    public Page getPage(int position) {
        if (position <= 0)
            throw new IllegalArgumentException("Cannot get this page, invalid input position " + position);

        Iterable<Page> pages = this.getPages();
        if (pages == null || countParts() < position)
            throw new IllegalArgumentException("Cannot get this page, page at position " + position + " does not exist.");

        Iterator<Page> pagesIt = pages.iterator();
        int counter = 1;
        Page page = null;
        while (pagesIt.hasNext()) {
            page = pagesIt.next();
            if (counter == position)
                return page;
            counter++;
        }
        return page;
    }

    public Work getLeaf(SubType subType, int position) {
        if (position <= 0)
            throw new IllegalArgumentException("Cannot get this page, invalid input position " + position);

        Iterable<Work> leafs = getLeafs(subType);
        if (leafs == null)
            throw new IllegalArgumentException("Cannot get this page, page at position " + position + " does not exist.");

        int counter = 1;
        for (Work leaf : leafs) {
            if (counter == position)
                return leaf;
        }
        return null;
    }

    public int countCopies() {
        return Lists.newArrayList(this.getCopies()).size();
    }

    public int countParts() {
        return partsDao.countParts(this.getId());
    }

    public Section asSection() {
        return (Section) this;
    }

    public EADWork asEADWork() {
        return (EADWork) this;
    }

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

    public boolean isRepresented() {
        Iterable<Copy> representations = getRepresentations();
        return representations != null && Iterables.size(representations) != 0;
    }

    public void removeRepresentation(final Copy copy) {
        removeRepresentative(copy);
    }

    public void addRepresentation(final Copy copy) {
        addRepresentative(copy);
    }

    public Work getRepresentativeImageWork() {
        // TODO - WTF?
        return null;
    }

    public boolean hasBornDigitalCopy() {
        Copy origCopy = getCopy(CopyRole.ORIGINAL_COPY);
        return (origCopy != null) && CopyType.BORN_DIGITAL.code().equals(origCopy.getCopyType());
    }

    public boolean hasMasterCopy() {
        return hasCopyRole(CopyRole.MASTER_COPY);
    }

    public boolean hasCopyRole(CopyRole role) {
        return getCopy(role) != null;
    }

    public boolean hasCopyRole(List<CopyRole> copyRoles){
        if (copyRoles != null){
            for (CopyRole copyRole: copyRoles){
                if (hasCopyRole(copyRole)){
                    return true;
                }
            }
        }
        return false;
    }

    public Copy getOrCreateCopy(CopyRole role) {
        Copy copy = getCopy(role);
        if (copy == null) {
            copy = addCopy();
            copy.setCopyRole(role.code());
        }
        return copy;
    }

    public Copy getFirstExistingCopy(CopyRole... roles){
        if (roles != null){
            for (CopyRole copyRole : roles){
                Copy copy = getCopy(copyRole);
                if (copy != null){
                    return copy;
                }
            }
        }
        return null;
    }

    public boolean hasUniqueAlias(AmberSession session) {
        List<String> aliases = getAlias();
        if (aliases == null || aliases.size() == 0 || aliases.size() > 1) {
            return false;
        } else {
            String alias = aliases.get(0);
            List<Work> works = session.findModelByValueInJsonList("alias", alias, Work.class);
            if (works.size() > 1) {
                // Has more than 1 work with the same alias
                return false;
            }
        }

        return true;
    }

    public Map<String, Collection<Copy>> getOrderedCopyMap() {
        LinkedListMultimap<String, Copy> orderedCopyMap = LinkedListMultimap.create();
        for (Copy copy : getOrderedCopies()) {
            orderedCopyMap.put(copy.getCopyRole(), copy);
        }
        return orderedCopyMap.asMap();
    }

    public boolean hasImageAccessCopy(){
        Copy accessCopy = getCopy(CopyRole.ACCESS_COPY);
        return accessCopy != null && accessCopy.getImageFile() != null;
    }

    public Copy getCopy(CopyRole role, int index) {
        List<Copy> orderedCopies = Lists.newArrayList(getOrderedCopies(role));
        if (orderedCopies == null) {
            return null;
        }

        if (orderedCopies.size() -1 < index) {
            return null;
        }

        return orderedCopies.get(index);
    }

    public Coordinates getCoordinates(int index) {
        List<Coordinates> allCoordinates = getCoordinates();
        return allCoordinates.get(index);
    }

    public void addCoordinates(Coordinates coordinates) throws JsonProcessingException {
        List<Coordinates> allCoordinates = getCoordinates();
        allCoordinates.add(coordinates);
        setCoordinates(allCoordinates);
    }

    public void setCoordinates(List<Coordinates> coordinatesList) throws JsonProcessingException {

        setJSONCoordinates(mapper.writeValueAsString(coordinatesList));
    }

    public List<Coordinates> getCoordinates() {
        String json = getJSONCoordinates();
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return deserialiseJSONString(json, new TypeReference<List<Coordinates>>() {});
    }

    public Integer getOrder() {
        final Work currWork = this;
        final long currId = currWork.getId();
        Work parent = getParent();
        if (parent == null) {
            return null;
        }
        Iterable<Page> pages = parent.getPages();
        int order = Iterables.indexOf(pages, new Predicate<Work>() {
            @Override
            public boolean apply(Work work) {
                return currId == work.getId();
            }
        });
        return order+1;
    }

    public boolean isVoyagerRecord() {
        return StringUtils.isNotBlank(getBibId()) && StringUtils.equalsIgnoreCase(getRecordSource(), "voyager");
    }

    public void removeCopies(List<CopyRole> copyRoles) {
        for (CopyRole copyRole : copyRoles){
            Iterator<Copy> copies = getCopies(copyRole).iterator();
            while (copies.hasNext()) {
                removeCopy(copies.next());
            }
        }
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

}
