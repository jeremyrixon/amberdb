package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import amberdb.InvalidSubtypeException;
import amberdb.enums.CopyRole;
import amberdb.enums.SubType;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsPartOf;
import amberdb.sql.AmberGraph;
import amberdb.sql.AmberQuery;
import amberdb.sql.AmberVertex;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedVertex;
import com.tinkerpop.frames.Adjacency;
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
 * A complex digital object may be made up of multiple related works forming
 * a graph. All works in a single digital object belong to a parent-child
 * tree formed by the {@link IsPartOf} relationship.
 * 
 * @see {@link Copy}
 */
@TypeValue("Work")
public interface Work extends Node {
    @Property ("abstract")
    public String getAbstract();
    
    @Property ("abstract")
    public void setAbstract(String aBstract);
    
    @Property("category")
    public String getCategory();
    
    @Property("category")
    public void setCategory(String category);
    
    /* DCM Legacy Data */
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
    public void setDcmDateTimeUpdated(Date dcmDateTimeUpdated);
    
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
    
    @Property("publisher")
    public String getPublisher();

    @Property("publisher")
    public void setPublisher(String publisher);
    
    @Property("recordSource")
    public String getRecordSource();

    @Property("recordSource")
    public void setRecordSource(String recordSource);
    
    @Property("copyrightPolicy")
    public String getCopyrightPolicy();

    @Property("copyrightPolicy")
    public void setCopyrightPolicy(String copyrightPolicy);
    
    @Property("firstPart")
    public String getFirstPart();

    @Property("firstPart")
    public void setFirstPart(String firstPart);
    
    @Property("otherNumbers")
    public ArrayList<String> getOtherNumbers();
    
    @Property("otherNumbers")
    public void setOtherNumbers(ArrayList<String> otherNumbers);
    
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
     * This method detatches the page from this work, but the page
     * continues to exist as an orphan.  Use the deletePage method
     * in AmberSessoin to actually delete the page with copies and 
     * files from the graph.
     * @param page
     * 
     * Note: remove is a naming convention used by tinkerpop frames
     *       annotation.
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
    
    /**
     * This method detaches the part from this work, but the part
     * continues to exist as an orphan.  Use the deletePart method
     * to actually delete the part and its children from the graph.
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

    
    abstract class Impl implements JavaHandlerContext<Vertex>, Work {

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
                throw new IllegalArgumentException("Cannot get this page, invalid input position "
                        + position);

            Iterable<Page> pages = this.getPages();
            if (pages == null || countParts() < position)
                throw new IllegalArgumentException("Cannot get this page, page at position "
                        + position + " does not exist.");

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
                throw new IllegalArgumentException("Cannot get this page, invalid input position "
                        + position);
            
            Iterable<Work> leafs = getLeafs(subType);
            if (leafs == null)
                throw new IllegalArgumentException("Cannot get this page, page at position "
                        + position + " does not exist.");
            
            int counter = 1;
            for (Work leaf : leafs) {
                if (counter == position)
                    return leaf;
            }
            return null;
        }
        
        @Override
        public int countParts() {
            return (parts() == null) ? 0 : parts().size();
        }

        private List<Edge> parts() {
            return (gremlin().inE(IsPartOf.label) == null) ? null : gremlin().inE(IsPartOf.label)
                    .toList();
        }

        private AmberVertex asAmberVertex() {
            if (this.asVertex() instanceof WrappedVertex) {
                return (AmberVertex) ((WrappedVertex) this.asVertex()).getBaseVertex();
            } else {
                return (AmberVertex) this.asVertex();
            }
        }
        
        /**
         * Loads all of a work into the session including Pages with their Copies and Files
         */
        public void loadPagedWork() {
            
            AmberVertex work = this.asAmberVertex();
            AmberGraph g = work.getAmberGraph();
            
            AmberQuery query = g.newQuery((Long) work.getId());
            query.branch(Lists.newArrayList(new String[] {"isPartOf"}), Direction.BOTH);
            query.branch(Lists.newArrayList(new String[] {"isCopyOf"}), Direction.IN);
            query.branch(Lists.newArrayList(new String[] {"isFileOf"}), Direction.IN);
            query.execute();
            
            query = g.newQuery((Long) work.getId());
            query.branch(Lists.newArrayList(new String[] {"existsOn"}), Direction.OUT);
            query.execute();
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
            return getPartsOf(Arrays.asList(new String[] {subType}));
        }
        
        public List<Work> getExistsOn(String subType) {
            return getExistsOn(Arrays.asList(new String[] {subType}));
        }
    }
}
