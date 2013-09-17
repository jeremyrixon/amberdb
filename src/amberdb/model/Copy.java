package amberdb.model;

import java.util.Date;

import amberdb.relation.IsFileOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Copy")
public interface Copy extends Node {
    enum Role {
        ACCESS_COPY("ac"), MASTER_COPY("m"), OCR_JSON_COPY("oc"), OCR_ALTO_COPY("at"), OCR_METS_COPY("mt");

        private String code;

        private Role(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public int idx() {
            return this.ordinal();
        }

        public static Role isCopyRole(String copyRole) {
            for (Role _copyRole : Role.values()) {
                if (_copyRole.name().equalsIgnoreCase(copyRole))
                    return _copyRole;
            }
            return null;
        }
    }
    
    
    @Property("copyRole")
    public String getCopyRole();
    
    @Property("copyRole")
    public void setCopyRole(String copyRole);
    
    @Property("carrier")
    public String getCarrier();
    
    @Property("carrier")
    public void setCarrier(String carrier);
    
    @Property("sourceCopy")
    public String getSourceCopy();
    
    @Property("sourceCopy")
    public void setSourceCopy(String sourceCopy);
    
    @Property("accessConditions")
    public String getAccessConditions();
    
    @Property("accessConditions")
    public void setAccessConditions(String accessConditions);
    
    @Property("expiryDate")
    public Date getExpiryDate();
    
    @Property("expiryDate")
    public void setExpiryDate(Date expiryDate);
      
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public Iterable<File> getFiles();
    
    @Adjacency(label = IsFileOf.label, direction = Direction.IN)
    public IsFileOf addFile(final File file);
        
    abstract class Impl implements JavaHandlerContext<Vertex>, Copy {
    }
}
