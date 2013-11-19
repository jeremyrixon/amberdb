package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import amberdb.enums.CopyRole;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsPartOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
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
    public String getDigitalStatusDate();

    @Property("digitalStatusDate")
    public void setDigitalStatusDate(String digitalStatusDate);
    
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
    
    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    public Long getBibId();
    
    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    public void setBibId(Long bibId);

    @Adjacency(label = IsPartOf.label)
    public void setParent(final Work parent);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void addChild(final Work part);

    @Adjacency(label = IsPartOf.label)
    public Work getParent();

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Iterable<Work> getChildren();
    
    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', subType)")
    public Iterable<Work> getLeafs(@GremlinParam("subType") String subType);
    
    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.has('subType', subType)")
    public Iterable<Section> getSections(@GremlinParam("subType") String subType);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public void addCopy(final Copy copy);

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
     * continues to exist as an orphan. Use the deletePage method 
     * to actually delete the page with copies and files from the 
     * graph. 
     * @param page
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void removePage(final Page page);
    
    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public Copy addCopy();

    @JavaHandler
    public Page addPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    public Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    public Iterable<Page> getPages();

    @JavaHandler
    public int countParts();
    
    /**
     * This method detatches the part from this work, but the part
     * continues to exist as an orphan. Use the deletePart method 
     * to actually delete the part and its children from the graph. 
     * @param work
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void removePart(final Work part);

    @JavaHandler
    public Page getPage(int position);
    
    @JavaHandler
    public Work getLeaf(String subType, int position);

    abstract class Impl implements JavaHandlerContext<Vertex>, Work {

        @Override
        public Page addPage(Path sourceFile, String mimeType) throws IOException {
            Page page = addPage();
            page.addCopy(sourceFile, CopyRole.MASTER_COPY, mimeType);
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
        public Work getLeaf(String subType, int position) {
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
    }
}
