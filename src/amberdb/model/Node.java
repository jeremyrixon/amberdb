package amberdb.model;

import java.io.IOException;

import java.util.*;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import amberdb.PIUtil;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberTransaction;
import amberdb.graph.AmberVertex;
import amberdb.relation.DescriptionOf;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedVertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;

@TypeField("type")
public interface Node extends VertexFrame {
    
    /**
     * Access Conditions determine whether or not something can be 
     * displayed to the public. Also known as Public Availability
     * Values: Restricted, Unrestricted, Internal access only
     */
    @Property("accessConditions")
    public String getAccessConditions();

    /**
     * Access Conditions determine whether or not something can be 
     * displayed to the public. Also known as Public Availability
     * Values: Restricted, Unrestricted, Internal access only
     */
    @Property("accessConditions")
    public void setAccessConditions(String accessConditions);
    
    /**
     * Internal Access Conditions reflect display and access 
     * for internal content management applications.
     * Values: open, restricted, closed
     */
    @Property("internalAccessConditions")
    public String getInternalAccessConditions();
    
    /**
    * Internal Access Conditions reflect display and access      
    * for internal content management applications.
    * Values: open, restricted, closed
    */
    @Property("internalAccessConditions")
    public void setInternalAccessConditions(String internalAccessConditions);
    
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
    
    @Property("type")
    public String getType();
    
  @JavaHandler
  abstract public long getId();
  
  @JavaHandler
  abstract public String getObjId();
  
    /**
     * This property is encoded as a JSON Array - You probably want to use getAlias to get this property
     */
    @Property("alias")
    public String getJSONAlias();
    
    /**
     * This property is encoded as a JSON Array - You probably want to use setAlias to set this property
     */
    @Property("alias")
    public void setJSONAlias(String alias);
    
    /**
     * This method handles the JSON serialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public void setAlias(List<String> alias) throws JsonParseException, JsonMappingException, IOException;
    
    /**
     * This method handles the JSON deserialisation of the OtherNumbers Property
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @JavaHandler
    public List<String> getAlias() throws JsonParseException, JsonMappingException, IOException;

    @Property("recordSource")
    public String getRecordSource();

    @Property("recordSource")
    public void setRecordSource(String recordSource);

    @Property("localSystemNumber")
    public String getLocalSystemNumber();

    @Property("localSystemNumber")
    public void setLocalSystemNumber(String localSystemNumber);
    
    @Property("commentsInternal")
    public String getCommentsInternal();

    @Property("commentsInternal")
    public void setCommentsInternal(String commentsInternal);

    @Property("commentsExternal")
    public String getCommentsExternal();

    @Property("commentsExternal")
    public void setCommentsExternal(String commentsExternal);
    
    @Adjacency(label = DescriptionOf.label, direction= Direction.IN)
    public Iterable<Description> getDescriptions();
    
    @JavaHandler
    public Description getDescription(String fmt);
        
    @JavaHandler
    public AmberGraph getAmberGraph();
    
    @JavaHandler
    public AmberTransaction getFirstTransaction();
    
    @JavaHandler
    public AmberTransaction getLastTransaction();
    
    /**
     * Set the edge-order attribute on the edge with the given label that
     * connects from this node to the adjacent node.
     * 
     * @param adjacent
     *            The adjacent node
     * @param label
     *            The label of the edge to be modified
     * @param direction
     *            The direction of the edge to be modified (wrt this Node)
     * @param order
     *            The new order value
     */
    @JavaHandler
    public void setOrder(Node adjacent, String label, Direction direction, Integer order);

    /**
     * Get the edge-order attribute on the edge with the given label that
     * connects from this node to the adjacent node.
     * 
     * @param adjacent
     *            The adjacent node
     * @param label
     *            The label of the edge whose edge order will be returned
     * @param direction
     *            The direction of the edge (wrt this Node) whose order will be returned  
     * @return An order value or null if no edges conform to requirements
     */
    @JavaHandler
    public Integer getOrder(Node adjacent, String label, Direction direction);

    // TODO amuller This may need to be implemented to return the set of properties that cannot be set.
    // I haven't done this because I couldn't think of a reasonable way of doing this that did not involve
    // reflection and messyness.
    /**
     * Get the set of keys for properties that are set on this object.
     *
     * This does not return the set of properties that can be set on this model.
     *
     * @return the keyset of properties currently set on this object.
     */
    @JavaHandler
    public Set<String> getPropertyKeySet();

    public abstract class Impl implements JavaHandlerContext<Vertex>, Node {
        static ObjectMapper mapper = new ObjectMapper();

        @Override
        public AmberGraph getAmberGraph() {
            return this.asAmberVertex().getAmberGraph();
        }

        private AmberVertex asAmberVertex() {
            if (this.asVertex() instanceof WrappedVertex) {
                return (AmberVertex) ((WrappedVertex) this.asVertex()).getBaseVertex();
            } else {
                return (AmberVertex) this.asVertex();
            }
        }

        @Override
        public AmberTransaction getFirstTransaction() {
            return this.asAmberVertex().getFirstTransaction();
        }

        @Override
        public AmberTransaction getLastTransaction() {
            return this.asAmberVertex().getLastTransaction();
        }

        @Override
        public long getId() {
            return toLong(asVertex().getId());
        }

        public long toLong(Object x) {
            // tinkergraph converts ids to strings
            if (x instanceof String) {
                return Long.parseLong((String) x);
            }
            return (long) x;
        }

        @Override
        public String getObjId() {
            return PIUtil.format(getId());
        }

        @Override
        public Description getDescription(String fmt) {
            Iterable<Description> descriptions = this.getDescriptions();
            if (descriptions != null) {
                Iterator<Description> it = descriptions.iterator();
                while (it.hasNext()) {
                    Description next = it.next();
                    if (next.getType() != null && next.getType().equals(fmt)) {
                        return next;
                    }
                }
            }
            return null;
        }

        @Override
        public List<String> getAlias() throws JsonParseException, JsonMappingException, IOException {
            String alias = getJSONAlias();
            if (alias == null || alias.isEmpty())
                return new ArrayList<String>();
            return mapper.readValue(alias, new TypeReference<List<String>>() {});
        }

        @Override
        public void setAlias(List<String> alias) throws JsonParseException, JsonMappingException, IOException {
            setJSONAlias(mapper.writeValueAsString(alias));
        }

        @Override
        public void setOrder(Node adjacent, String label, Direction direction, Integer order) {
            // argument guards
            if (adjacent == null) {
                throw new IllegalArgumentException("adjacent node not supplied");
            }
            if (label == null || label.isEmpty()) {
                throw new IllegalArgumentException("edge label not supplied");
            }
            if (direction == null || direction == Direction.BOTH) {
                throw new IllegalArgumentException("direction cannot be null or BOTH");
            }

            this.asAmberVertex().setEdgeOrder(adjacent.asVertex(), label, direction, order);
        }

        @Override
        public Integer getOrder(Node adjacent, String label, Direction direction) {
            // argument guards
            if (adjacent == null) {
                throw new IllegalArgumentException("adjacent not supplied");
            }
            if (label == null || label.isEmpty()) {
                throw new IllegalArgumentException("edge label not supplied");
            }
            if (direction == null || direction == Direction.BOTH) {
                throw new IllegalArgumentException("direction cannot be null or BOTH");
            }

            List<Integer> orderValues = this.asAmberVertex().getEdgeOrder(adjacent.asVertex(), label, direction);
            return orderValues.size() > 0 ? orderValues.get(0) : null;
        }

        @Override
        public Set<String> getPropertyKeySet() {
            return this.asVertex().getPropertyKeys();
        }
    }
}
