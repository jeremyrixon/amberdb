package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import amberdb.InvalidSubtypeException;
import amberdb.enums.CopyRole;
import amberdb.enums.SubType;
import amberdb.relation.DescriptionOf;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsPartOf;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQuery;
import amberdb.graph.AmberVertex;

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
    public String getAbstract();

    @Property("abstract")
    public void setAbstract(String aBstract);

    @Property("category")
    public String getCategory();

    @Property("category")
    public void setCategory(String category);

    /* DCM Legacy Data */

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getDcmAltPi to get this property
     */
    @Property("dcmAltPi")
    public String getJSONDcmAltPi();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setDcmAltPi to set this property
     */
    @Property("dcmAltPi")
    public void setJSONDcmAltPi(String dcmAltPi);

    /**
     * This method handles the JSON serialisation of the dcmAltPi Property
     */
    @JavaHandler
    public void setDcmAltPi(List<String> list) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the dcmAltPi Property
     */
    @JavaHandler
    public List<String> getDcmAltPi() throws JsonParseException, JsonMappingException, IOException;

    @Property("dcmWorkPid")
    public String getDcmWorkPid();

    @Property("dcmWorkPid")
    public void setDcmWorkPid(String dcmWorkPid);

    @Property("dcmDateTimeCreated")
    public Date getDcmDateTimeCreated();

    @Property("dcmDateTimeCreated")
    public void setDcmDateTimeCreated(Date dcmDateTimeCreated);

    @Property("dcmDateTimeUpdated")
    public Date getDcmDateTimeUpdated();

    @Property("dcmDateTimeUpdated")
    public void setDcmDateTimeUpdated(Date dcmDateTimeUpd);

    @Property("dcmRecordCreator")
    public String getDcmRecordCreator();

    @Property("dcmRecordCreator")
    public void setDcmRecordCreator(String dcmRecordCreator);

    @Property("dcmRecordUpdater")
    public String getDcmRecordUpdater();

    @Property("dcmRecordUpdater")
    public void setDcmRecordUpdater(String dcmRecordUpdater);

    /* END DCM Legacy Data */

    @Property("subUnitType")
    public String getSubUnitType();

    @Property("subUnitType")
    public void setSubUnitType(String subUnitType);

    @Property("subUnitNo")
    public String getSubUnitNo();

    @Property("subUnitNo")
    public void setSubUnitNo(String subUnitNo);

    @Property("subType")
    public String getSubType();

    @Property("subType")
    public void setSubType(String subType);

    @Property("issueDate")
    public Date getIssueDate();

    @Property("issueDate")
    public void setIssueDate(Date issueDate);

    @Property("collection")
    public String getCollection();

    @Property("collection")
    public void setCollection(String collection);

    @Property("form")
    public String getForm();

    @Property("form")
    public void setForm(String form);

    @Property("bibLevel")
    public String getBibLevel();

    @Property("bibLevel")
    public void setBibLevel(String bibLevel);

    @Property("digitalStatus")
    public String getDigitalStatus();

    @Property("digitalStatus")
    public void setDigitalStatus(String digitalStatus);

    @Property("digitalStatusDate")
    public Date getDigitalStatusDate();

    @Property("digitalStatusDate")
    public void setDigitalStatusDate(Date digitalStatusDate);

    @Property("heading")
    public String getHeading();

    @Property("heading")
    public void setHeading(String heading);

    @Property("subHeadings")
    public String getSubHeadings();

    @Property("subHeadings")
    public void setSubHeadings(String subHeadings);

    /**
     * Also known as CALLNO
     */
    @Property("holdingNumber")
    public String getHoldingNumber();

    /**
     * Also known as CALLNO
     */
    @Property("holdingNumber")
    public void setHoldingNumber(String holdingNumber);

    @Property("issn")
    public String getISSN();

    @Property("issn")
    public void setISSN(String issn);

    @Property("title")
    public String getTitle();

    @Property("title")
    public void setTitle(String title);

    @Property("creator")
    public String getCreator();

    @Property("creator")
    public void setCreator(String creator);

    @Property("creatorStatement")
    public String getCreatorStatement();

    @Property("creatorStatement")
    public void setCreatorStatement(String creatorStatement);

    @Property("publisher")
    public String getPublisher();

    @Property("publisher")
    public void setPublisher(String publisher);

    @Property("copyrightPolicy")
    public String getCopyrightPolicy();

    @Property("copyrightPolicy")
    public void setCopyrightPolicy(String copyrightPolicy);

    @Property("firstPart")
    public String getFirstPart();

    @Property("firstPart")
    public void setFirstPart(String firstPart);

    @Property("sortIndex")
    public String getSortIndex();

    @Property("sortIndex")
    public void setSortIndex(String sortIndex);

    @Property("edition")
    public String getEdition();

    @Property("edition")
    public void setEdition(String edition);

    @Property("immutable")
    public String getImmutable();

    @Property("immutable")
    public void setImmutable(String immutable);

    @Property("startDate")
    public Date getStartDate();

    @Property("startDate")
    public void setStartDate(Date startDate);

    @Property("endDate")
    public Date getEndDate();

    @Property("endDate")
    public void setEndDate(Date endDate);

    @Property("extent")
    public String getExtent();

    @Property("extent")
    public void setExtent(String extent);

    @Property("language")
    public String getLanguage();

    @Property("language")
    public void setLanguage(String language);

    @Property("addressee")
    public String getAddressee();

    @Property("addressee")
    public void setAddressee(String addressee);

    @Property("childRange")
    public String getChildRange();

    @Property("childRange")
    public void setChildRange(String childRange);

    @Property("startChild")
    public String getStartChild();

    @Property("startChild")
    public void setStartChild(String startChild);

    @Property("endChild")
    public String getEndChild();

    @Property("endChild")
    public void setEndChild(String endChild);

    @Property("encodingLevel")
    public String getEncodingLevel();

    @Property("encodingLevel")
    public void setEncodingLevel(String encodingLevel);

    @Property("publicationLevel")
    public String getPublicationLevel();

    @Property("publicationLevel")
    public void setPublicationLevel(String publicationLevel);

    @Property("genre")
    public String getGenre();

    @Property("genre")
    public void setGenre(String genre);

    @Property("publicationCategory")
    public String getPublicationCategory();

    @Property("publicationCategory")
    public void setPublicationCategory(String publicationCategory);

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    public GeoCoding addGeoCoding();

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    public IPTC addIPTC();

    @JavaHandler
    public GeoCoding getGeoCoding();

    @JavaHandler
    public IPTC getIPTC();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getSeries to get this property
     */
    @Property("series")
    public String getJSONSeries();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setSeries to set this property
     */
    @Property("series")
    public void setJSONSeries(String series);

    /**
     * This method handles the JSON serialisation of the series Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setSeries(List<String> series) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the series Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getSeries() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getClassification to get this property
     */
    @Property("classification")
    public String getJSONClassification();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setClassification to set this property
     */
    @Property("classification")
    public void setJSONClassification(String classification);

    /**
     * This method handles the JSON serialisation of the classification Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setClassification(List<String> classification) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the classification
     * Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getClassification() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getContributor to get this property
     */
    @Property("contributor")
    public String getJSONContributor();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setContributor to set this property
     */
    @Property("contributor")
    public void setJSONContributor(String contributor);

    /**
     * This method handles the JSON serialisation of the contributor Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setContributor(List<String> contributor) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the contributor Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getContributor() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getCoverage to get this property
     */
    @Property("coverage")
    public String getJSONCoverage();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setCoverage to set this property
     */
    @Property("coverage")
    public void setJSONCoverage(String coverage);

    /**
     * This method handles the JSON serialisation of the coverage Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setCoverage(List<String> coverage) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the coverage Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getCoverage() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getOccupation to get this property
     */
    @Property("occupation")
    public String getJSONOccupation();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setOccupation to set this property
     */
    @Property("occupation")
    public void setJSONOccupation(String occupation);

    /**
     * This method handles the JSON serialisation of the occupation Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setOccupation(List<String> occupation) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the occupation Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getOccupation() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getOtherTitle to get this property
     */
    @Property("otherTitle")
    public String getJSONOtherTitle();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setOtherTitle to set this property
     */
    @Property("otherTitle")
    public void setJSONOtherTitle(String otherTitle);

    /**
     * This method handles the JSON serialisation of the otherTitle Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setOtherTitle(List<String> otherTitle) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the otherTitle Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getOtherTitle() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getStandardId to get this property
     */
    @Property("standardId")
    public String getJSONStandardId();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setStandardId to set this property
     */
    @Property("standardId")
    public void setJSONStandardId(String standardId);

    /**
     * This method handles the JSON serialisation of the standardId Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setStandardId(List<String> standardId) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the standardId Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getStandardId() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getSubject to get this property
     */
    @Property("subject")
    public String getJSONSubject();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setSubject to set this property
     */
    @Property("subject")
    public void setJSONSubject(String subject);

    /**
     * This method handles the JSON serialisation of the subject Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setSubject(List<String> subject) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the subject Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getSubject() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getScaleEtc to get this property
     */
    @Property("scaleEtc")
    public String getJSONScaleEtc();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setScaleEtc to set this property
     */
    @Property("scaleEtc")
    public void setJSONScaleEtc(String scaleEtc);

    /**
     * This method handles the JSON serialisation of the scaleEtc Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setScaleEtc(List<String> scaleEtc) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the scaleEtc Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getScaleEtc() throws JsonParseException, JsonMappingException, IOException;

    @Property("west")
    public String getWest();

    @Property("west")
    public void setWest(String west);

    @Property("east")
    public String getEast();

    @Property("east")
    public void setEast(String east);

    @Property("north")
    public String getNorth();

    @Property("north")
    public void setNorth(String north);

    @Property("south")
    public String getSouth();

    @Property("south")
    public void setSouth(String south);

    @Property("tilePosition")
    public String getTilePosition();

    @Property("tilePosition")
    public void setTilePosition(String tilePosition);

    @Property("workPid")
    public String getWorkPid();

    @Property("workPid")
    public void setWorkPid(String workPid);

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getConstraint to get this property
     */
    @Property("constraint")
    public String getJSONConstraint();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setConstraint to set this property
     */
    @Property("constraint")
    public void setJSONConstraint(String constraint);

    /**
     * This method handles the JSON serialisation of the constraint Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setConstraint(List<String> constraint) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the constraint Property
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<String> getConstraint() throws JsonParseException, JsonMappingException, IOException;

    @Property("rights")
    public String getRights();

    @Property("rights")
    public void setRights(String rights);

    @Property("tempHolding")
    public String getTempHolding();

    @Property("tempHolding")
    public void setTempHolding(String tempHolding);

    @Property("sensitiveMaterial")
    public String getSensitiveMaterial();

    @Property("sensitiveMaterial")
    public void setSensitiveMaterial(String sensitiveMaterial);

    @Property("sensitiveReason")
    public String getSensitiveReason();

    @Property("sensitiveReason")
    public void setSensitiveReason(String sensitiveReason);

    @Property("uniformTitle")
    public String getUniformTitle();

    @Property("uniformTitle")
    public void setUniformTitle(String uniformTitle);

    @Property("alternativeTitle")
    public String getAlternativeTitle();

    @Property("alternativeTitle")
    public void setAlternativeTitle(String alternativeTitle);

    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    public String getBibId();

    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    public void setBibId(String bibId);

    /**
     * To be published in the catalogue
     */
    @Property("publicNotes")
    public String getPublicNotes();

    /**
     * To be published in the catalogue
     */
    @Property("publicNotes")
    public void setPublicNotes(String publicNotes);

    @Adjacency(label = IsPartOf.label)
    public void setParent(final Work parent);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void addChild(final Work part);

    @Adjacency(label = IsPartOf.label)
    public Work getParent();

    @Incidence(label = IsPartOf.label, direction = Direction.OUT)
    public Iterable<IsPartOf> getParentEdges();

    @JavaHandler
    public IsPartOf getParentEdge();

    @JavaHandler
    public void setOrder(int position);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Iterable<Work> getChildren();

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', subType.code)")
    public Iterable<Work> getLeafs(@GremlinParam("subType") SubType subType);

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', Subtype.fromString(T).in, subTypes)")
    public Iterable<Work> getLeafs(@GremlinParam("subTypes") List<String> subTypes);

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.has('subType', subType.code)")
    public Iterable<Section> getSections(@GremlinParam("subType") SubType subType);

    // TODO: need to test later whether it has any existsOn outE(s)
    @GremlinGroovy("it")
    public Section asSection();

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public void addCopy(final Copy copy);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public void removeCopy(final Copy copy);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public Iterable<Copy> getCopies();

    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code)")
    public Copy getCopy(@GremlinParam("role") CopyRole role);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Section addSection();

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Page addPage();

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
    public void removePage(final Page page);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public Copy addCopy();

    /**
     * Adds a page Work and create a MASTER_COPY Copy Node with a File for it
     */
    @JavaHandler
    public Page addPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    public Page addLegacyDossPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    public Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    public Copy addLegacyDossCopy(Path dossPath, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    public Iterable<Page> getPages();

    @JavaHandler
    public int countParts();

    @JavaHandler
    public int countCopies();

    /**
     * This method detaches the part from this work, but the part continues to
     * exist as an orphan. Use the deletePart method to actually delete the part
     * and its children from the graph.
     * 
     * @param work
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void removePart(final Work part);

    @JavaHandler
    public Page getPage(int position);

    @JavaHandler
    public Work getLeaf(SubType subType, int position);

    @JavaHandler
    public void loadPagedWork() throws InvalidSubtypeException;

    @JavaHandler
    public List<Work> getPartsOf(List<String> subTypes);

    @JavaHandler
    public List<Work> getExistsOn(List<String> subTypes);

    @JavaHandler
    public List<Work> getPartsOf(String subType);

    @JavaHandler
    public List<Work> getExistsOn(String subType);

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
    public void orderRelated(List<Work> relatedNodes, String label, Direction direction);

    /**
     * Orders the parts in the given list by their list order. This is a
     * specialization of orderRelated.
     * 
     * @param parts
     *            The list of parts.
     */
    @JavaHandler
    public void orderParts(List<Work> parts);

    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, Work {
        static ObjectMapper mapper = new ObjectMapper();

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
            List<Page> pages = new ArrayList<Page>();
            Iterable<Work> parts = this.getChildren();
            if (parts != null) {
                Iterator<Work> it = parts.iterator();
                while (it.hasNext()) {
                    Work part = it.next();
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
         * Copies and Files
         */
        public void loadPagedWork() {
            AmberVertex work = this.asAmberVertex();
            AmberGraph g = work.getAmberGraph();

            AmberQuery query = g.newQuery((Long) work.getId());
            query.branch(new String[] { "isPartOf" }, Direction.BOTH);
            query.branch(new String[] { "isCopyOf" }, Direction.IN);
            query.branch(new String[] { "isFileOf" }, Direction.IN);
            query.branch(new String[] { "descriptionOf" }, Direction.IN);
            query.execute(true);
        }

        public List<Work> getPartsOf(List<String> subTypes) {

            AmberVertex work = this.asAmberVertex();

            // just return the pages
            List<Edge> partEdges = Lists.newArrayList(work.getEdges(Direction.IN, "isPartOf"));
            List<Work> works = new ArrayList<Work>();
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
            List<Work> works = new ArrayList<Work>();
            for (Edge e : partEdges) {
                Vertex v = e.getVertex(Direction.IN);
                if (subTypes == null || subTypes.size() == 0 || subTypes.contains(v.getProperty("subType"))) {
                    works.add(this.g().frame(v, Work.class));
                }
            }
            return works;
        }

        public List<Work> getPartsOf(String subType) {
            return getPartsOf(Arrays.asList(new String[] { subType }));
        }

        public List<Work> getExistsOn(String subType) {
            return getExistsOn(Arrays.asList(new String[] { subType }));
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
        public List<String> getConstraint() throws JsonParseException, JsonMappingException, IOException {
            String s = getJSONConstraint();
            return deserialiseJSONString(getJSONConstraint());
        }

        @Override
        public void setConstraint(List<String> constraint) throws JsonParseException, JsonMappingException, IOException {
            String s = serialiseToJSON(constraint);
            setJSONConstraint(serialiseToJSON(constraint));
        }

        @Override
        public List<String> getSeries() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONSeries());
        }

        @Override
        public void setSeries(List<String> series) throws JsonParseException, JsonMappingException, IOException {
            setJSONSeries(serialiseToJSON(series));
        }

        @Override
        public List<String> getClassification() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONClassification());
        }

        @Override
        public void setClassification(List<String> classification) throws JsonParseException, JsonMappingException, IOException {
            setJSONClassification(serialiseToJSON(classification));
        }

        @Override
        public List<String> getContributor() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONContributor());
        }

        @Override
        public void setContributor(List<String> contributor) throws JsonParseException, JsonMappingException, IOException {
            setJSONContributor(serialiseToJSON(contributor));
        }

        @Override
        public List<String> getCoverage() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONCoverage());
        }

        @Override
        public void setCoverage(List<String> coverage) throws JsonParseException, JsonMappingException, IOException {
            setJSONCoverage(serialiseToJSON(coverage));
        }

        @Override
        public List<String> getOccupation() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONOccupation());
        }

        @Override
        public void setOccupation(List<String> occupation) throws JsonParseException, JsonMappingException, IOException {
            setJSONOccupation(serialiseToJSON(occupation));
        }

        @Override
        public List<String> getOtherTitle() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONOtherTitle());
        }

        @Override
        public void setOtherTitle(List<String> otherTitle) throws JsonParseException, JsonMappingException, IOException {
            setJSONOtherTitle(serialiseToJSON(otherTitle));
        }

        @Override
        public List<String> getStandardId() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONStandardId());
        }

        @Override
        public void setStandardId(List<String> standardId) throws JsonParseException, JsonMappingException, IOException {
            setJSONStandardId(serialiseToJSON(standardId));
        }

        @Override
        public List<String> getSubject() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONSubject());
        }

        @Override
        public void setSubject(List<String> subject) throws JsonParseException, JsonMappingException, IOException {
            // ensure each subject entry is unique
            setJSONSubject(serialiseToJSON(new HashSet<String>(subject)));
        }

        @Override
        public List<String> getScaleEtc() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONScaleEtc());
        }

        @Override
        public void setScaleEtc(List<String> scaleEtc) throws JsonParseException, JsonMappingException, IOException {
            setJSONScaleEtc(serialiseToJSON(scaleEtc));
        }

        private List<String> deserialiseJSONString(String json) throws JsonParseException, JsonMappingException, IOException {
            if (json == null || json.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(json, new TypeReference<List<String>>() {
            });
        }

        private String serialiseToJSON(Collection<String> list) throws JsonParseException, JsonMappingException, IOException {
            return mapper.writeValueAsString(list);
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
        public IPTC getIPTC() {
            return (IPTC) getDescription("IPTC");
        }

        @Override
        public List<String> getDcmAltPi() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONDcmAltPi());
        }

        @Override
        public void setDcmAltPi(List<String> list) throws JsonParseException, JsonMappingException, IOException {
            setJSONDcmAltPi(serialiseToJSON(list));
        }
    }
}
