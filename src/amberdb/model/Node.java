package amberdb.model;

import java.util.Date;

import amberdb.PIUtil;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;

@TypeField("type")
public interface Node extends VertexFrame {
    /**
     * get the access conditions
     * @return the access conditions, e.g. restricted, unrestricted.
     */
    @Property("accessConditions")
    public String getAccessConditions();

    /**
     * set the access conditions. e.g. restricted and unrestricted
     * @param accessConditions
     */
    @Property("accessConditions")
    public void setAccessConditions(String accessConditions);
    
    /**
     * get the expiry date of the access conditions.
     * @return the expiry date.  
     * Note: the expiry date is currently for display only, not enforced
     *       for the checking of the access conditions. 
     */
    @Property("expiryDate")
    public Date getExpiryDate();

    /**
     * set the expiry date on the access conditions.
     * @param expiryDate
     * Note: the expiry date is currently for display only, not enforced for
     *       the checking of the access conditions.
     */
    @Property("expiryDate")
    public void setExpiryDate(Date expiryDate);
    
    @Property("restrictionType")
    public String getRestrictionType();

    @Property("restrictionType")
    public void setRestrictionType(String restrictionType);
    
    @Property("notes")
    public String getNotes();

    @Property("notes")
    public void setNotes(String notes);
    
	@JavaHandler
	abstract public long getId();
	
	@JavaHandler
	abstract public String getObjId();
    
    abstract class Impl implements JavaHandlerContext<Vertex>, Node {

		@Override
		public long getId() {
			return toLong(asVertex().getId());
		}
		
		public long toLong(Object x) {
			// tingergraph converts ids to strings
			if (x instanceof String) {
				return Long.parseLong((String) x);
			}
			return (long)x; 
		}
		
		@Override
		public String getObjId() {
		    return PIUtil.format(getId());
		}
    }
}
