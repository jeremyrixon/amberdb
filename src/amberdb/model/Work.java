package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import amberdb.AmberSession;
import amberdb.relation.*;
import amberdb.util.WorkUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import com.google.common.collect.LinkedListMultimap;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import amberdb.InvalidSubtypeException;
import amberdb.enums.CopyRole;
import amberdb.enums.CopyType;
import amberdb.enums.SubType;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQuery;
import amberdb.graph.AmberVertex;
import static amberdb.graph.BranchType.*;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedVertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * Any logical work that is collected or created by the library such as a book,
 * page, map, physical object or sound recording.
 * 
 * A complex digital object may be made up of multiple related works forming a
 * graph. All works in a single digital object belong to a parent-child tree
 * formed by the {@link IsPartOf} relationship.
 * 
 * @see {@link Copy}
 */
@TypeValue("Work")
public interface Work extends Node {
    @Property("abstract")
    String getAbstract();

    @Property("abstract")
    void setAbstract(String aBstract);

    @Property("category")
    String getCategory();

    @Property("category")
    void setCategory(String category);

    /* DCM Legacy Data */

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getDcmAltPi to get this property
     */
    @Property("dcmAltPi")
    String getJSONDcmAltPi();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setDcmAltPi to set this property
     */
    @Property("dcmAltPi")
    void setJSONDcmAltPi(String dcmAltPi);

    /**
     * This method handles the JSON serialisation of the dcmAltPi Property
     */
    @JavaHandler
    void setDcmAltPi(List<String> list) throws IOException;

    /**
     * This method handles the JSON deserialisation of the dcmAltPi Property
     */
    @JavaHandler
    List<String> getDcmAltPi() throws IOException;

    @Property("dcmWorkPid")
    String getDcmWorkPid();

    @Property("dcmWorkPid")
    void setDcmWorkPid(String dcmWorkPid);

    @Property("dcmDateTimeCreated")
    Date getDcmDateTimeCreated();

    @Property("dcmDateTimeCreated")
    void setDcmDateTimeCreated(Date dcmDateTimeCreated);

    @Property("dcmDateTimeUpdated")
    Date getDcmDateTimeUpdated();

    @Property("dcmDateTimeUpdated")
    void setDcmDateTimeUpdated(Date dcmDateTimeUpd);

    @Property("dcmRecordCreator")
    String getDcmRecordCreator();

    @Property("dcmRecordCreator")
    void setDcmRecordCreator(String dcmRecordCreator);

    @Property("dcmRecordUpdater")
    String getDcmRecordUpdater();

    @Property("dcmRecordUpdater")
    void setDcmRecordUpdater(String dcmRecordUpdater);

    /* END DCM Legacy Data */

    @Property("subUnitType")
    String getSubUnitType();

    @Property("subUnitType")
    void setSubUnitType(String subUnitType);

    @Property("subUnitNo")
    String getSubUnitNo();

    @Property("subUnitNo")
    void setSubUnitNo(String subUnitNo);

    @Property("subType")
    String getSubType();

    @Property("subType")
    void setSubType(String subType);

    @Property("issueDate")
    Date getIssueDate();

    @Property("issueDate")
    void setIssueDate(Date issueDate);

    @Property("collection")
    String getCollection();

    @Property("collection")
    void setCollection(String collection);

    @Property("form")
    String getForm();

    @Property("form")
    void setForm(String form);
    
    @Property("displayTitlePage")
    Boolean isDisplayTitlePage();

    @Property("displayTitlePage")
    void setDisplayTitlePage(Boolean displayTitlePage);

    @Property("bibLevel")
    String getBibLevel();

    @Property("bibLevel")
    void setBibLevel(String bibLevel);

    @Property("digitalStatus")
    String getDigitalStatus();

    @Property("digitalStatus")
    void setDigitalStatus(String digitalStatus);

    @Property("digitalStatusDate")
    Date getDigitalStatusDate();

    @Property("digitalStatusDate")
    void setDigitalStatusDate(Date digitalStatusDate);

    @Property("heading")
    String getHeading();

    @Property("heading")
    void setHeading(String heading);

    @Property("subHeadings")
    String getSubHeadings();

    @Property("subHeadings")
    void setSubHeadings(String subHeadings);

    /**
     * Also known as CALLNO
     */
    @Property("holdingNumber")
    String getHoldingNumber();

    /**
     * Also known as CALLNO
     */
    @Property("holdingNumber")
    void setHoldingNumber(String holdingNumber);

    @Property("holdingId")
    String getHoldingId();

    /**
     * Also known as CALLNO
     */
    @Property("holdingId")
    void setHoldingId(String holdingId);

    @JavaHandler
    String getHoldingNumberAndId();

    @JavaHandler
    void setHoldingNumberAndId(String holdNumAndId);

    @Property("issn")
    String getISSN();

    @Property("issn")
    void setISSN(String issn);

    @Property("title")
    String getTitle();

    @Property("title")
    void setTitle(String title);

    @Property("creator")
    String getCreator();

    @Property("creator")
    void setCreator(String creator);

    @Property("creatorStatement")
    String getCreatorStatement();

    @Property("creatorStatement")
    void setCreatorStatement(String creatorStatement);

    @Property("publisher")
    String getPublisher();

    @Property("publisher")
    void setPublisher(String publisher);

    @Property("copyrightPolicy")
    String getCopyrightPolicy();

    @Property("copyrightPolicy")
    void setCopyrightPolicy(String copyrightPolicy);

    @Property("firstPart")
    @Deprecated
    String getFirstPart();

    @Property("firstPart")
    @Deprecated
    void setFirstPart(String firstPart);

    @Property("sortIndex")
    @Deprecated
    String getSortIndex();

    @Property("sortIndex")
    @Deprecated
    void setSortIndex(String sortIndex);

    @Property("edition")
    String getEdition();

    @Property("edition")
    void setEdition(String edition);

    @Property("immutable")
    String getImmutable();

    @Property("immutable")
    void setImmutable(String immutable);

    @Property("startDate")
    Date getStartDate();

    @Property("startDate")
    void setStartDate(Date startDate);

    @Property("endDate")
    Date getEndDate();

    @Property("endDate")
    void setEndDate(Date endDate);

    @Property("extent")
    String getExtent();

    @Property("extent")
    void setExtent(String extent);

    @Property("language")
    String getLanguage();

    @Property("language")
    void setLanguage(String language);

    @Property("addressee")
    String getAddressee();

    @Property("addressee")
    void setAddressee(String addressee);

    @Property("childRange")
    String getChildRange();

    @Property("childRange")
    void setChildRange(String childRange);

    @Property("startChild")
    String getStartChild();

    @Property("startChild")
    void setStartChild(String startChild);

    @Property("endChild")
    String getEndChild();

    @Property("endChild")
    void setEndChild(String endChild);

    @Property("encodingLevel")
    String getEncodingLevel();

    @Property("encodingLevel")
    void setEncodingLevel(String encodingLevel);

    @Property("publicationLevel")
    String getPublicationLevel();

    @Property("publicationLevel")
    void setPublicationLevel(String publicationLevel);

    @Property("genre")
    String getGenre();

    @Property("genre")
    void setGenre(String genre);

    @Property("publicationCategory")
    String getPublicationCategory();

    @Property("publicationCategory")
    void setPublicationCategory(String publicationCategory);

    @Property("sendToIlms")
    Boolean getSendToIlms();

    @Property("sendToIlms")
    void setSendToIlms(Boolean sendToIlms);

    @Property("ingestJobId")
    Long getIngestJobId();

    @Property("ingestJobId")
    void setIngestJobId(Long ingestJobId);

    @Property("moreIlmsDetailsRequired")
    Boolean getMoreIlmsDetailsRequired();

    @Property("moreIlmsDetailsRequired")
    void setMoreIlmsDetailsRequired(Boolean moreIlmsDetailsRequired);

    @Property("allowHighResdownload")
    Boolean getAllowHighResdownload();

    @Property("allowHighResdownload")
    void setAllowHighResdownload(Boolean allowHRdownload);

    @Property("ilmsSentDateTime")
    Date getIlmsSentDateTime();

    @Property("ilmsSentDateTime")
    void setIlmsSentDateTime(Date dateTime);
    
    @Property("interactiveIndexAvailable")
    Boolean getInteractiveIndexAvailable();
    
    @Property("interactiveIndexAvailable")
    void setInteractiveIndexAvailable(Boolean interactiveIndexAvailable);
    
    @Property("html")
    String getHtml();
    
    @Property("html")
    void setHtml(String html);

    @Property("isMissingPage")
    Boolean getIsMissingPage();

    @Property("isMissingPage")
    void setIsMissingPage(Boolean isMissingPage);
    
    @Property("workCreatedDuringMigration")
    Boolean getWorkCreatedDuringMigration();
    
    @Property("workCreatedDuringMigration")
    void setWorkCreatedDuringMigration(Boolean workCreatedDuringMigration);
    
    @Property("additionalSeriesStatement")
    String getAdditionalSeriesStatement();
    
    @Property("additionalSeriesStatement")
    void setAdditionalSeriesStatement(String additionalSeriesStatement);
    
    @Property("sheetName")
    String getSheetName();
    
    @Property("sheetName")
    void setSheetName(String sheetName);
    
    @Property("sheetCreationDate")
    Date getSheetCreationDate();
    
    @Property("sheetCreationDate")
    void setSheetCreationDate(Date sheetCreationDate);

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    GeoCoding addGeoCoding();

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    IPTC addIPTC();

    @JavaHandler
    GeoCoding getGeoCoding();

    @JavaHandler
    IPTC getIPTC();
    
    @JavaHandler
    boolean isCopy();
    
    @Incidence(label = Acknowledge.label, direction = Direction.OUT)
    Acknowledge addAcknowledgement(final Party party);
    
    @Incidence(label = Acknowledge.label, direction = Direction.OUT)
    void removeAcknowledgement(final Acknowledge ack);
    
    @Incidence(label = Acknowledge.label, direction = Direction.OUT)
    Iterable<Acknowledge> getAcknowledgements();

    @JavaHandler
    Acknowledge addAcknowledgement(final Party party, final String ackType, final String kindOfSupport,
            final Double weighting, final Date dateOfAck, final String urlToOriginial);

    @JavaHandler
    List<Acknowledge> getOrderedAcknowledgements();
    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getSeries to get this property
     * 
     * NOTE: this property should not be used to retrieve manuscript series
     *       from EAD.  For EAD related work properties, please refer to 
     *       amberdb.model.EADWork class.
     */
    @Property("series")
    String getJSONSeries();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setSeries to set this property
     *      
     * NOTE: this property should not be used to populate manuscript series
     *       from EAD. For EAD related work properties, please refer to 
     *       amberdb.model.EADWork class.
     */
    @Property("series")
    void setJSONSeries(String series);

    /**
     * This method handles the JSON serialisation of the series Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * 
     * NOTE: this property should not be used to populate manuscript series
     *       from EAD. For EAD related work properties, please refer to 
     *       amberdb.model.EADWork class.
     */
    @JavaHandler
    void setSeries(List<String> series) throws IOException;

    /**
     * This method handles the JSON deserialisation of the series Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * 
     * NOTE: this property should not be used to retrieve manuscript series
     *       from EAD.  For EAD related work properties, please refer to 
     *       amberdb.model.EADWork class.
     */
    @JavaHandler
    List<String> getSeries() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getClassification to get this property
     */
    @Property("classification")
    String getJSONClassification();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setClassification to set this property
     */
    @Property("classification")
    void setJSONClassification(String classification);

    /**
     * This method handles the JSON serialisation of the classification Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setClassification(List<String> classification) throws IOException;

    /**
     * This method handles the JSON deserialisation of the classification
     * Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getClassification() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getContributor to get this property
     */
    @Property("contributor")
    String getJSONContributor();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setContributor to set this property
     */
    @Property("contributor")
    void setJSONContributor(String contributor);

    /**
     * This method handles the JSON serialisation of the contributor Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setContributor(List<String> contributor) throws IOException;

    /**
     * This method handles the JSON deserialisation of the contributor Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getContributor() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getCoverage to get this property
     */
    @Property("coverage")
    String getJSONCoverage();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setCoverage to set this property
     */
    @Property("coverage")
    void setJSONCoverage(String coverage);

    /**
     * This method handles the JSON serialisation of the coverage Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setCoverage(List<String> coverage) throws IOException;

    /**
     * This method handles the JSON deserialisation of the coverage Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getCoverage() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getOccupation to get this property
     */
    @Property("occupation")
    String getJSONOccupation();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setOccupation to set this property
     */
    @Property("occupation")
    void setJSONOccupation(String occupation);

    /**
     * This method handles the JSON serialisation of the occupation Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setOccupation(List<String> occupation) throws IOException;

    /**
     * This method handles the JSON deserialisation of the occupation Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getOccupation() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getOtherTitle to get this property
     */
    @Property("otherTitle")
    String getJSONOtherTitle();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setOtherTitle to set this property
     */
    @Property("otherTitle")
    void setJSONOtherTitle(String otherTitle);

    /**
     * This method handles the JSON serialisation of the otherTitle Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setOtherTitle(List<String> otherTitle) throws IOException;

    /**
     * This method handles the JSON deserialisation of the otherTitle Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getOtherTitle() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getStandardId to get this property
     */
    @Property("standardId")
    String getJSONStandardId();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setStandardId to set this property
     */
    @Property("standardId")
    void setJSONStandardId(String standardId);

    /**
     * This method handles the JSON serialisation of the standardId Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setStandardId(List<String> standardId) throws IOException;

    /**
     * This method handles the JSON deserialisation of the standardId Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getStandardId() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getSubject to get this property
     */
    @Property("subject")
    String getJSONSubject();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setSubject to set this property
     */
    @Property("subject")
    void setJSONSubject(String subject);

    /**
     * This method handles the JSON serialisation of the subject Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setSubject(List<String> subject) throws IOException;

    /**
     * This method handles the JSON deserialisation of the subject Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getSubject() throws IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getScaleEtc to get this property
     */
    @Property("scaleEtc")
    String getJSONScaleEtc();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setScaleEtc to set this property
     */
    @Property("scaleEtc")
    void setJSONScaleEtc(String scaleEtc);

    /**
     * This method handles the JSON serialisation of the scaleEtc Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setScaleEtc(List<String> scaleEtc) throws IOException;

    /**
     * This method handles the JSON deserialisation of the scaleEtc Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    List<String> getScaleEtc() throws IOException;

    @Property("west")
    String getWest();

    @Property("west")
    void setWest(String west);

    @Property("east")
    String getEast();

    @Property("east")
    void setEast(String east);

    @Property("north")
    String getNorth();

    @Property("north")
    void setNorth(String north);

    @Property("south")
    String getSouth();

    @Property("south")
    void setSouth(String south);

    @Property("tilePosition")
    String getTilePosition();

    @Property("tilePosition")
    void setTilePosition(String tilePosition);

    @Property("workPid")
    String getWorkPid();

    @Property("workPid")
    void setWorkPid(String workPid);

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getConstraint to get this property
     */
    @Property("constraint")
    String getJSONConstraint();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setConstraint to set this property
     */
    @Property("constraint")
    void setJSONConstraint(String constraint);

    /**
     * This method handles the JSON serialisation of the constraint Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    void setConstraint(Set<String> constraint) throws IOException;

    /**
     * This method handles the JSON deserialisation of the constraint Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    Set<String> getConstraint() throws IOException;

    @Property("rights")
    String getRights();

    @Property("rights")
    void setRights(String rights);

    @Property("tempHolding")
    String getTempHolding();

    @Property("tempHolding")
    void setTempHolding(String tempHolding);

    @Property("sensitiveMaterial")
    String getSensitiveMaterial();

    @Property("sensitiveMaterial")
    void setSensitiveMaterial(String sensitiveMaterial);

    @Property("sensitiveReason")
    String getJSONSensitiveReason();

    @Property("sensitiveReason")
    void setJSONSensitiveReason(String sensitiveReason);
    
    @JavaHandler
    void setSensitiveReason(List<String> sensitiveReason) throws IOException;
    
    @JavaHandler
    List<String> getSensitiveReason() throws IOException;
    
    @Property("restrictionsOnAccess")
    String getJSONRestrictionsOnAccess();

    @Property("restrictionsOnAccess")
    void setJSONRestrictionsOnAccess(String restrictionsOnAccess);
    
    @JavaHandler
    void setRestrictionsOnAccess(List<String> restrictionsOnAccess) throws IOException;
    
    @JavaHandler
    List<String> getRestrictionsOnAccess() throws IOException;

    @Property("findingAidNote")
    String getJSONFindingAidNote();

    @Property("findingAidNote")
    void setJSONFindingAidNote(String findingAidNote);
    
    @JavaHandler
    void setFindingAidNote(List<String> findingAidNote) throws IOException;
    
    @JavaHandler
    List<String> getFindingAidNote() throws IOException;

    @Property("uniformTitle")
    String getUniformTitle();

    @Property("uniformTitle")
    void setUniformTitle(String uniformTitle);

    @Property("alternativeTitle")
    String getAlternativeTitle();

    @Property("alternativeTitle")
    void setAlternativeTitle(String alternativeTitle);
    
    /**
     * summary of scope of work, description of image
     */
    @Property("summary")
    String getSummary();

    @Property("summary")
    void setSummary(String summary);


    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    String getBibId();

    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    void setBibId(String bibId);

    /**
     * To be published in the catalogue
     */
    @Property("publicNotes")
    String getPublicNotes();

    /**
     * To be published in the catalogue
     */
    @Property("publicNotes")
    void setPublicNotes(String publicNotes);

    @Property("australianContent")
    Boolean isAustralianContent();

    @Property("australianContent")
    void setAustralianContent(Boolean australianContent);

    @Property("materialFromMultipleSources")
    void setMaterialFromMultipleSources(Boolean materialFromMultipleSources);

    @Property("materialFromMultipleSources")
    Boolean getMaterialFromMultipleSources();
    
    @Property("acquisitionStatus")
    String getAcquisitionStatus();

    @Property("acquisitionStatus")
    void setAcquisitionStatus(String acquisitionStatus);
    
    @Property("acquisitionCategory")
    String getAcquisitionCategory();

    @Property("acquisitionCategory")
    void setAcquisitionCategory(String acquisitionCategory);

    @Adjacency(label = DeliveredOn.label, direction = Direction.OUT)
    Iterable<Work> getDeliveryWorks();

    @Adjacency(label = DeliveredOn.label, direction = Direction.OUT)
    void addDeliveryWork(Work deliveryWork);

    @Adjacency(label = DeliveredOn.label, direction = Direction.OUT)
    void removeDeliveryWork(Work deliveryWork);

    @JavaHandler
    List<String> getDeliveryWorkIds();

    @JavaHandler
    void removeDeliveryWorks();

    @Adjacency(label = DeliveredOn.label, direction = Direction.IN)
    void setDeliveryWorkParent(final Work interview);

    @Adjacency(label = DeliveredOn.label, direction = Direction.IN)
    Work getDeliveryWorkParent();

    @Adjacency(label = DeliveredOn.label, direction = Direction.IN)
    void removeDeliveryWorkParent(final Work interview);

    @Incidence(label = DeliveredOn.label, direction = Direction.IN)
    Iterable<ExistsOn> getDeliveryWorkParentEdges();

    @JavaHandler
    ExistsOn getDeliveryWorkParentEdge();

    @JavaHandler
    void setDeliveryWorkOrder(int position);

    @Adjacency(label = IsPartOf.label)
    void setParent(final Work parent);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    void addChild(final Work part);

    @Adjacency(label = IsPartOf.label)
    Work getParent();

    @Incidence(label = IsPartOf.label, direction = Direction.OUT)
    Iterable<IsPartOf> getParentEdges();

    @JavaHandler
    IsPartOf getParentEdge();

    @JavaHandler
    void setOrder(int position);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    Iterable<Work> getChildren();

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', subType.code)")
    Iterable<Work> getLeafs(@GremlinParam("subType") SubType subType);

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', Subtype.fromString(T).in, subTypes)")
    Iterable<Work> getLeafs(@GremlinParam("subTypes") List<String> subTypes);

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.has('subType', subType.code)")
    Iterable<Section> getSections(@GremlinParam("subType") SubType subType);

    @JavaHandler
    Section asSection();
    
    @JavaHandler
    EADWork asEADWork();

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    void addCopy(final Copy copy);
    
    /**
     * This method is intended for internal amberdb use, to be called by the
     * removeRepresentation() method.  You probably want to use removeRepresentation()
     * method to remove a representative image.
     * @param copy The representative copy
     */
    @Adjacency(label = Represents.label, direction = Direction.IN)
    void removeRepresentative(final Copy copy);
    
    /**
     * This method calls removeRepresentative() to remove a representative image,
     * and update the hasRepresentation flag which is a shortcut for delivery.
     * @param copy The representative copy
     */
    @JavaHandler
    void removeRepresentation(final Copy copy);

    @JavaHandler
    Map<String, Collection<Copy>> getOrderedCopyMap();

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    void removeCopy(final Copy copy);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    Iterable<Copy> getCopies();

    @GremlinGroovy("it.in('isCopyOf').order{it.a.id <=> it.b.id}")
    Iterable<Copy> getOrderedCopies();

    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code).order{it.a.id <=> it.b.id}")
    Iterable<Copy> getOrderedCopies(@GremlinParam("role") CopyRole role);
    
    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code)")
    Iterable<Copy> getCopies(@GremlinParam("role") CopyRole role);

    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code)")
    Copy getCopy(@GremlinParam("role") CopyRole role);

    @JavaHandler
    Copy getCopy(CopyRole role, int index);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    Section addSection();

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    Page addPage();

    /**
     * This method detatches the page from this work, but the page continues to
     * exist as an orphan. Use the deletePage method in AmberSession to actually
     * delete the page with copies and files from the graph.
     * 
     * @param page
     * 
     *            Note: remove is a naming convention used by tinkerpop frames
     *            annotation.
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    void removePage(final Page page);
    
    /**
     * This method is intended for internal amberdb use, to be called by the 
     * addRepresentation() method.  You probably want to use addRepresentation()
     * method to add a representative image.
     * @param copy The representative copy
     */
    @Adjacency(label = Represents.label, direction = Direction.IN)
    void addRepresentative(final Copy copy);
    
    /**
     * This method calls addRepresentative() to add a representative image,
     * and update the hasRepresentation flag which is a shortcut for delivery.
     * @param copy The representative copy
     */
    @JavaHandler
    void addRepresentation(final Copy copy);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    Copy addCopy();

    @Adjacency(label = Represents.label, direction = Direction.IN)
    Iterable<Copy> getRepresentations();
    
    @JavaHandler
    boolean isRepresented();
    
    /**
     * Adds a page Work and create a MASTER_COPY Copy Node with a File for it
     */
    @JavaHandler
    Page addPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    Page addLegacyDossPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    Copy addLegacyDossCopy(Path dossPath, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    Iterable<Page> getPages();

    @JavaHandler
    int countParts();

    @JavaHandler
    int countCopies();

    /**
     * This method detaches the part from this work, but the part continues to
     * exist as an orphan. Use the deletePart method to actually delete the part
     * and its children from the graph.
     * 
     * @param part The part ot be removed
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    void removePart(final Work part);

    @JavaHandler
    Page getPage(int position);

    @JavaHandler
    Work getLeaf(SubType subType, int position);

    @JavaHandler
    void loadPagedWork() throws InvalidSubtypeException;

    @JavaHandler
    List<Work> getPartsOf(List<String> subTypes);

    @JavaHandler
    List<Work> getExistsOn(List<String> subTypes);

    @JavaHandler
    List<Work> getPartsOf(String subType);

    @JavaHandler
    List<Work> getExistsOn(String subType);

    /**
     * This method sets the edge order of the related Nodes in the list to be
     * their index in the list. The related Nodes and their edge association to
     * this object must already exist.
     * 
     * @param relatedNodes
     *            A list of related Nodes whose edges will have their edge
     *            ordering updated.
     * @param label
     *            The label or type of edge to be updated (eg: 'isPartOf',
     *            'existsOn')
     * @param direction
     *            The direction of the edge from this object
     */
    @JavaHandler
    void orderRelated(List<Work> relatedNodes, String label, Direction direction);

    /**
     * Orders the parts in the given list by their list order. This is a
     * specialization of orderRelated.
     * 
     * @param parts
     *            The list of parts.
     */
    @JavaHandler
    void orderParts(List<Work> parts);

    /**
     *
     * Returns the work that contains the access copy to be used for this work's representative image.
     *
     * @return The work with the image copy that should be used to represent this work, or null if no image is
     * specified.
     *
     */
    @JavaHandler
    Work getRepresentativeImageWork();
    
    @JavaHandler
    List<String> getJsonList(String propertyName) throws IOException;
    
    @JavaHandler
    boolean hasBornDigitalCopy();

    @JavaHandler
    boolean hasMasterCopy();

    @JavaHandler
    boolean hasCopyRole(CopyRole role);

    @JavaHandler
    boolean hasUniqueAlias(AmberSession session) throws IOException;
    
    @JavaHandler
    boolean hasImageAccessCopy();

    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, Work {
        static ObjectMapper mapper = new ObjectMapper();

        @Override
        public Acknowledge addAcknowledgement(final Party party, final String ackType, final String kindOfSupport, 
                final Double weighting, final Date dateOfAck, final String urlToOriginial) {           
            Acknowledge ack = addAcknowledgement(party);
            ack.setAckType(ackType);
            ack.setKindOfSupport(kindOfSupport);
            ack.setWeighting(weighting);
            ack.setUrlToOriginial(urlToOriginial);
            ack.setDate(dateOfAck);
            return ack;
        }
        
        @Override
        public List<Acknowledge> getOrderedAcknowledgements() {
            List<Acknowledge> list = Lists.newArrayList(getAcknowledgements());

            Collections.sort(list, new Comparator<Acknowledge>() {
                public int compare(final Acknowledge object1, final Acknowledge object2) {
                    return object1.getWeighting().compareTo(object2.getWeighting());
                }
            });

            return list;
        }

        @Override
        public Page addPage(Path sourceFile, String mimeType) throws IOException {
            Page page = addPage();
            page.addCopy(sourceFile, CopyRole.MASTER_COPY, mimeType);
            return page;
        }

        @Override
        public Page addLegacyDossPage(Path dossPath, String mimeType) throws IOException {
            Page page = addPage();
            page.addLegacyDossCopy(dossPath, CopyRole.MASTER_COPY, mimeType);
            return page;
        }

        @Override
        public Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType) throws IOException {
            Copy copy = addCopy();
            copy.setCopyRole(copyRole.code());
            copy.addFile(sourceFile, mimeType);
            return copy;
        }

        @Override
        public Copy addLegacyDossCopy(Path dossPath, CopyRole copyRole, String mimeType) throws IOException {
            Copy copy = addCopy();
            copy.setCopyRole(copyRole.code());
            copy.addLegacyDossFile(dossPath, mimeType);
            return copy;
        }

        @Override
        public List<Page> getPages() {
            List<Page> pages = new ArrayList<>();
            Iterable<Work> parts = this.getChildren();
            if (parts != null) {
                for (Work part : parts) {
                    pages.add(frame(part.asVertex(), Page.class));
                }
            }
            return pages;
        }

        @Override
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

        @Override
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

        /**
         * Count the number of copies this work has.
         */
        @Override
        public int countCopies() {
            return Lists.newArrayList(this.getCopies()).size();
        }

        @Override
        public int countParts() {
            return (parts() == null) ? 0 : parts().size();
        }

        @Override
        public Section asSection() {
            return frame(this.asVertex(), Section.class);
        }
        
        @Override
        public EADWork asEADWork() {
            return frame(this.asVertex(), EADWork.class);
        }

        @Override
        public String getHoldingNumberAndId() {
            return getHoldingNumber() + (getHoldingId() != null ? ("|:|" + getHoldingId()) : "");
        }

        @Override
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
        
        private List<Edge> parts() {
            return (gremlin().inE(IsPartOf.label) == null) ? null : gremlin().inE(IsPartOf.label).toList();
        }

        private AmberVertex asAmberVertex() {
            if (this.asVertex() instanceof WrappedVertex) {
                return (AmberVertex) ((WrappedVertex) this.asVertex()).getBaseVertex();
            } else {
                return (AmberVertex) this.asVertex();
            }
        }

        /**
         * Loads all of a work into the session including Pages with their
         * Copies and Files but not the work's representative Copy
         */
        public void loadPagedWork() {
            loadPagedWork(false);
        }

        /**
         * Loads all of a work into the session including Pages with their
         * Copies and Files including its representative Copy (if it exists)
         */
        public void loadPagedWork(boolean includeRepresentativeCopies) {
            AmberVertex work = this.asAmberVertex();
            AmberGraph g = work.getAmberGraph();
            AmberQuery query = g.newQuery((Long) work.getId());
            query.branch(new String[] {"isPartOf"}, Direction.BOTH);

            if (includeRepresentativeCopies) {
                query.branch(BRANCH_FROM_ALL, new String[] {"represents"}, Direction.IN);
            }

            query.branch(BRANCH_FROM_ALL, new String[] {"deliveredOn"}, Direction.IN) // gets delivery parent
                 .branch(BRANCH_FROM_ALL, new String[] {"isCopyOf"}, Direction.IN)
                 .branch(BRANCH_FROM_PREVIOUS, new String[] {"isFileOf"}, Direction.IN)
                 .branch(BRANCH_FROM_ALL, new String[] {"descriptionOf"}, Direction.IN)
                 .branch(BRANCH_FROM_ALL, new String[] {"tags"}, Direction.IN)
                 .branch(BRANCH_FROM_ALL, new String[] {"acknowledge"}, Direction.OUT)
                 .execute(true);
        }

        public List<Work> getPartsOf(List<String> subTypes) {

            AmberVertex work = this.asAmberVertex();

            // just return the pages
            List<Edge> partEdges = Lists.newArrayList(work.getEdges(Direction.IN, "isPartOf"));
            List<Work> works = new ArrayList<>();
            for (Edge e : partEdges) {
                Vertex v = e.getVertex(Direction.OUT);
                if (subTypes == null || subTypes.size() == 0 || subTypes.contains(v.getProperty("subType"))) {
                    works.add(this.g().frame(v, Work.class));
                }
            }
            return works;
        }

        public List<Work> getExistsOn(List<String> subTypes) {

            AmberVertex work = this.asAmberVertex();

            // just return the pages
            List<Edge> partEdges = Lists.newArrayList(work.getEdges(Direction.OUT, "existsOn"));
            List<Work> works = new ArrayList<>();
            for (Edge e : partEdges) {
                Vertex v = e.getVertex(Direction.IN);
                if (subTypes == null || subTypes.size() == 0 || subTypes.contains(v.getProperty("subType"))) {
                    works.add(this.g().frame(v, Work.class));
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

        @Override
        public void setOrder(int position) {
            getParentEdge().setRelOrder(position);
        }

        @Override
        public IsPartOf getParentEdge() {
            Iterator<IsPartOf> iterator = getParentEdges().iterator();
            return (iterator != null && iterator.hasNext()) ? iterator.next() : null;
        }

        @Override
        public Set<String> getConstraint() throws IOException {
            List<String> list = deserialiseJSONString(getJSONConstraint());
            LinkedHashSet<String> constraint = new LinkedHashSet<>();
            constraint.addAll(list);
            return constraint;
        }

        @Override
        public void setConstraint(Set<String> constraint) throws IOException {
            setJSONConstraint(serialiseToJSON(constraint));
        }
        
        @Override
        public List<String> getSensitiveReason() throws IOException {
            return deserialiseJSONString(getJSONSensitiveReason());
        }

        @Override
        public void setSensitiveReason(List<String> sensitiveReason) throws IOException {
            setJSONSensitiveReason(serialiseToJSON(sensitiveReason));
        }
        
        @Override
        public List<String> getRestrictionsOnAccess() throws IOException {
            return deserialiseJSONString(getJSONRestrictionsOnAccess());
        }

        @Override
        public void setRestrictionsOnAccess(List<String> restrictionsOnAccess) throws IOException {
            setJSONRestrictionsOnAccess(serialiseToJSON(restrictionsOnAccess));
        }
        
        @Override
        public List<String> getFindingAidNote() throws IOException {
            return deserialiseJSONString(getJSONFindingAidNote());
        }

        @Override
        public void setFindingAidNote(List<String> findingAidNote) throws IOException {
            setJSONFindingAidNote(serialiseToJSON(findingAidNote));
        }

        @Override
        public List<String> getSeries() throws IOException {
            return deserialiseJSONString(getJSONSeries());
        }

        @Override
        public void setSeries(List<String> series) throws IOException {
            setJSONSeries(serialiseToJSON(series));
        }

        @Override
        public List<String> getClassification() throws IOException {
            return deserialiseJSONString(getJSONClassification());
        }

        @Override
        public void setClassification(List<String> classification) throws IOException {
            setJSONClassification(serialiseToJSON(classification));
        }

        @Override
        public List<String> getContributor() throws IOException {
            return deserialiseJSONString(getJSONContributor());
        }

        @Override
        public void setContributor(List<String> contributor) throws IOException {
            setJSONContributor(serialiseToJSON(contributor));
        }

        @Override
        public List<String> getCoverage() throws IOException {
            return deserialiseJSONString(getJSONCoverage());
        }

        @Override
        public void setCoverage(List<String> coverage) throws IOException {
            setJSONCoverage(serialiseToJSON(coverage));
        }

        @Override
        public List<String> getOccupation() throws IOException {
            return deserialiseJSONString(getJSONOccupation());
        }

        @Override
        public void setOccupation(List<String> occupation) throws IOException {
            setJSONOccupation(serialiseToJSON(occupation));
        }

        @Override
        public List<String> getOtherTitle() throws IOException {
            return deserialiseJSONString(getJSONOtherTitle());
        }

        @Override
        public void setOtherTitle(List<String> otherTitle) throws IOException {
            setJSONOtherTitle(serialiseToJSON(otherTitle));
        }

        @Override
        public List<String> getStandardId() throws IOException {
            return deserialiseJSONString(getJSONStandardId());
        }

        @Override
        public void setStandardId(List<String> standardId) throws IOException {
            setJSONStandardId(serialiseToJSON(standardId));
        }

        @Override
        public List<String> getSubject() throws IOException {
            return deserialiseJSONString(getJSONSubject());
        }

        @Override
        public void setSubject(List<String> subject) throws IOException {
            // ensure each subject entry is unique
            setJSONSubject((null == subject)? null : serialiseToJSON(new HashSet<>(subject)));
        }

        @Override
        public List<String> getScaleEtc() throws IOException {
            return deserialiseJSONString(getJSONScaleEtc());
        }

        @Override
        public void setScaleEtc(List<String> scaleEtc) throws IOException {
            setJSONScaleEtc(serialiseToJSON(scaleEtc));
        }

        protected List<String> deserialiseJSONString(String json) throws IOException {
            if (json == null || json.isEmpty())
                return new ArrayList<>();
            return mapper.readValue(json, new TypeReference<List<String>>() {
            });
        }

        protected String serialiseToJSON(Collection<String> list) throws IOException {
            if (list == null || list.isEmpty()) return null;    
            return mapper.writeValueAsString(list);
        }

        @Override
        public List<String> getJsonList(String propertyName) throws IOException {
            return deserialiseJSONString((String) this.asVertex().getProperty(propertyName));
        }
        
        @Override
        public void orderRelated(List<Work> relatedNodes, String label, Direction direction) {
            for (int i = 0; i < relatedNodes.size(); i++) {
                Work node = relatedNodes.get(i);
                node.setOrder(this, label, direction, i+1);
            }
        }

        @Override
        public void orderParts(List<Work> parts) {
            orderRelated(parts, "isPartOf", Direction.OUT);
        }

        @Override
        public GeoCoding getGeoCoding() {
            return (GeoCoding) getDescription("GeoCoding");
        }
        
        @Override
        public boolean isCopy() {
            return this.asVertex().getProperty("type").equals("Copy");
        }

        @Override
        public IPTC getIPTC() {
            return (IPTC) getDescription("IPTC");
        }

        @Override
        public List<String> getDcmAltPi() throws IOException {
            return deserialiseJSONString(getJSONDcmAltPi());
        }

        @Override
        public void setDcmAltPi(List<String> list) throws IOException {
            setJSONDcmAltPi(serialiseToJSON(list));
        }
        
        @Override
        public boolean isRepresented() {
            Iterable<Copy> representations = getRepresentations();
            return representations != null && Iterables.size(representations) != 0;
        }
        
        @Override
        public void removeRepresentation(final Copy copy) {
            removeRepresentative(copy);
        }
        
        @Override
        public void addRepresentation(final Copy copy) {
            addRepresentative(copy);
        }

        @Override
        public Work getRepresentativeImageWork() {

            Work repImageOrAccessCopy = getRepImageOrAccessCopy(this);
            if (repImageOrAccessCopy != null) {
                return repImageOrAccessCopy;
            }

            Iterable<Work> children = getChildren();
            if (Iterables.size(children) == 0) {
                return null;
            }
            Work child = Iterables.get(children, 0);
            if (WorkUtils.checkCanReturnRepImage(child)) {
                return getRepImageOrAccessCopy(child);
            }
            return null;
        }

        private static Work getRepImageOrAccessCopy(Work work) {
            Iterator<Copy> representations = work.getRepresentations().iterator();
            if (representations.hasNext()) {
                Work repWork = representations.next().getWork();
                if (!WorkUtils.checkCanReturnRepImage(repWork)) {
                    return null;
                }
                return repWork;
            }
            Copy accessCopy = work.getCopy(CopyRole.ACCESS_COPY);
            if (accessCopy != null && accessCopy.getImageFile() != null) {
                return work;
            }
            return null;
        }

        @Override
        public List<String> getDeliveryWorkIds() {
            Iterable<Work> deliveryWorks = getDeliveryWorks();

            List<String> ids = new ArrayList<>();
            for (Work work : deliveryWorks) {
                ids.add(work.getObjId());
            }

            return ids;
        }

        @Override
        public void removeDeliveryWorks() {
            Iterable<Work> deliveryWorks = getDeliveryWorks();
            for (Work dw : deliveryWorks) {
                dw.removeDeliveryWorkParent(this);
            }
        }

        @Override
        public void setDeliveryWorkOrder(int position) {
            getDeliveryWorkParentEdge().setRelOrder(position);
        }

        @Override
        public ExistsOn getDeliveryWorkParentEdge() {
            Iterator<ExistsOn> iterator = getDeliveryWorkParentEdges().iterator();
            return (iterator != null && iterator.hasNext()) ? iterator.next() : null;
        }

        @Override
        public boolean hasBornDigitalCopy() {
            Copy origCopy = getCopy(CopyRole.ORIGINAL_COPY);
            return (origCopy != null) && CopyType.BORN_DIGITAL.code().equals(origCopy.getCopyType());
        }

        @Override
        public boolean hasMasterCopy() {
            return hasCopyRole(CopyRole.MASTER_COPY);
        }

        @Override
        public boolean hasCopyRole(CopyRole role) {
            return getCopy(role) != null;
        }

        @Override
        public boolean hasUniqueAlias(AmberSession session) throws IOException {
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

        @Override
        public Map<String, Collection<Copy>> getOrderedCopyMap() {
            LinkedListMultimap<String, Copy> orderedCopyMap = LinkedListMultimap.create();
            for (Copy copy : getOrderedCopies()) {
                orderedCopyMap.put(copy.getCopyRole(), copy);
            }
            return orderedCopyMap.asMap();
        }
        
        @Override
        public boolean hasImageAccessCopy(){
            Copy accessCopy = getCopy(CopyRole.ACCESS_COPY);
            return accessCopy != null && accessCopy.getImageFile() != null;
        }

        @Override
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
    }
}
