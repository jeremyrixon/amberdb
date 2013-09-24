package amberdb.model;

import amberdb.relation.IsCopyOf;
import amberdb.relation.IsPartOf;
import amberdb.relation.Relation;

import com.tinkerpop.blueprints.Direction;
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
public interface Work extends Item {    
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
    public Copy getCopy(@GremlinParam("role") Copy.Role role);
    
    @JavaHandler
    public Section addSection();
    
    @JavaHandler
    public Page addPage();
    
    @JavaHandler
    public Page addPage(java.io.File file);
    
    @JavaHandler
    public void addImageTiffCopy(java.io.File file);
    
    @JavaHandler
    public void addImageJP2Copy(java.io.File file);
    
    @JavaHandler
    public void addOCRMETSCopy(java.io.File file);
    
    @JavaHandler
    public void addOCRALTOCopy(java.io.File file);
    
    @JavaHandler
    public void addOCRJSONCopy(java.io.File file);
    
    @JavaHandler
    public void setGraph(FramedGraph<TinkerGraph>  graph);
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Work {
        FramedGraph<TinkerGraph> graph;
        Order order = new Order();
        
        public void setGraph(FramedGraph<TinkerGraph> graph) {
            this.graph = graph;
            order.setContext(graph);
        }
        
        public Section addSection() {
            Section section = graph.addVertex(Sequence.next(), Section.class);
            section.setSubType("section");
            this.addChild(section);
            order.assign(this, section);
            return section;
        }
        
        public Page addPage() {
            Page page = graph.addVertex(Sequence.next(), Page.class);
            page.setSubType("page");
            this.addChild(page);
            order.assign(this, page);
            return page;
        }
        
        public Page addPage(java.io.File file) {
            Page page = addPage();
            page.addImageTiffCopy(file);
            return page;
        }
        
        public void addImageTiffCopy(java.io.File file) {
            this.addCopy(file, Copy.Role.MASTER_COPY.code());
        }
        
        public void addImageJP2Copy(java.io.File file) {
            this.addCopy(file, Copy.Role.ACCESS_COPY.code());
        }
        
        public void addOCRMETSCopy(java.io.File file) {
            this.addCopy(file, Copy.Role.OCR_METS_COPY.code());
        }
        
        public void addOCRALTOCopy(java.io.File file) {
            this.addCopy(file, Copy.Role.OCR_ALTO_COPY.code());
        }
        
        public void addOCRJSONCopy(java.io.File file) {
            this.addCopy(file, Copy.Role.OCR_JSON_COPY.code());
        }
        
        private void addCopy(java.io.File file, String copyRole) {
            if (file == null)
                throw new IllegalArgumentException("Cannot add copy as input file is null.");

            Copy copy = graph.addVertex(null, Copy.class);
            copy.setCopyRole(copyRole);
            this.addCopy(copy);

            File _file = graph.addVertex(null, File.class);
            copy.addFile(_file);
            _file.setFileLocation(file.getAbsolutePath());            
        }
    }
}
