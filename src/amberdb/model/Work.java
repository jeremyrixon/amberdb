package amberdb.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import amberdb.enums.CopyRole;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsPartOf;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Work")
public interface Work extends Node {    
    @Property("subType")
    public String getSubType();
    
    @Property("subType")
    public void setSubType(String subType);
    
    @Property("title")
    public String getTitle();
    
    @Property("title")
    public void setTitle(String title);
    
    @Property("creator")
    public String getCreator();
    
    @Property("creator")
    public void setCreator(String creator);
    
    @Property("subUnitType")
    public String getSubUnitType();
    
    @Property("subUnitType")
    public void setSubUnitType(String subUnitType);
    
    @Property("bibLevel")
    public String getBibLevel();
    
    @Property("bibLevel")
    public void setBibLevel(String bibLevel);
    
    @Property("bibId")
    public String getBibId();
    
    @Property("bibId")
    public void setBibId(long bibId);

    @Property("digitalStatus")
    public String getDigitalStatus();
    
    @Property("digitalStatus")
    public void setDigitalStatus(String digitalStatus);
      
    @Adjacency(label = IsPartOf.label)
    public void setParent(final Work parent);
    
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public void addChild(final Work part);
       
    @Adjacency(label = IsPartOf.label)
    public Work getParent(); 
    
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Iterable<Work> getChildren();
    
    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public void addCopy(final Copy copy);
      
    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    public Iterable<Copy> getCopies();  
    
    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code)")
    public Copy getCopy(@GremlinParam("role") CopyRole role);
    
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public Section addSection();
    
    // NOTE: Currently frames-2.4.0 and frames-2.5.0 has a bug with 
    // adjacency add op which has no arguments, although the source
    // on github is fixed.  The bug occurs when more than one page
    // is added to the work, which cause an exception, and fail to
    // add the page.
    //
    // I've deployed a version of frames (v2.5.0) to nla mvn repo
    // however, travis cannot access it.  So need to fall back to
    // Java Handler to create these objects for now.
    //
    // @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    // public Page addPage();  
    // @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    // public Copy addCopy();
    
    @JavaHandler
    public Page addPage(java.io.File file);
    
    @JavaHandler
    public List<Page> addPages(List<java.io.File> files);
    
    @JavaHandler
    public Copy addCopy(java.io.File file, CopyRole copyRole);
    
    @JavaHandler
    public Iterable<Page> getPages();
    
    @JavaHandler
    public Page addPage();
    
    @JavaHandler
    public Copy addCopy();
    
    @JavaHandler
    public int countParts();
    
    @JavaHandler
    public Page getPage(int position);
    
    @JavaHandler
    public void setGraph(FramedGraph<TinkerGraph> graph);
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Work {
        private static FramedGraph<TinkerGraph> graph;
        
        public void setGraph(FramedGraph<TinkerGraph> graph) {
            this.graph = graph;
        }
        
        public Page addPage() {
            Page page = graph.addVertex(null, Page.class);
            this.addChild(page);
            return page;
        }
        
        public Copy addCopy() {
            Copy copy = graph.addVertex(null, Copy.class);
            this.addCopy(copy);
            return copy;
        }
        
        public List<Page> addPages(List<java.io.File> files) {
            List<Page> pages = new ArrayList<Page>();
            if (files == null)
                throw new IllegalArgumentException("Cann not add pages as the input files is null.");
            for (java.io.File file : files) {
                pages.add(addPage(file));
            }
            return pages;
        }
        
        public Page addPage(java.io.File file) {
            Page page = graph.addVertex(null, Page.class);
            this.addChild(page);
            page.addCopy(file, CopyRole.MASTER_COPY);
            return page;
        }
        
        public Copy addCopy(java.io.File file, CopyRole copyRole) {
            if (file == null)
                throw new IllegalArgumentException("Cannot add copy as input file is null.");

            Copy copy = addCopy();
            copy.setCopyRole(copyRole.code());

            File _file = copy.addFile();
            _file.setFileLocation(file.getAbsolutePath());         
            
            return copy;
        }
        
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
        
        public int countParts() {
            return (parts() == null)? 0 : parts().size();
        }
        
        private List<Edge> parts() {
            return (gremlin().inE(IsPartOf.label) == null)? null: gremlin().inE(IsPartOf.label).toList();
        } 
    }
}
