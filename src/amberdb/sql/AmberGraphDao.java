package amberdb.sql;

import java.util.Date;
import java.util.Iterator;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AmberGraphDao {

    /*
     *  DB creation operations (DDL)
     */
    @SqlUpdate(
    		"CREATE TABLE IF NOT EXISTS vertex (" +
    		"id         BIGINT PRIMARY KEY AUTO_INCREMENT, " +
    		"pi         VARCHAR(100), " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
    		"txn_open   TINYINT(1), " +
    		"properties TEXT)")
    void createVertexTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge (" +
    		"id         BIGINT PRIMARY KEY AUTO_INCREMENT, " +
    		"txn_start  BIGINT, " +
    		"txn_end    BIGINT, " +
            "txn_open   TINYINT(1), " +
    		"properties TEXT, " +
    		"v_out      BIGINT, " +
    		"v_in       BIGINT, " +
    		"label      VARCHAR(100), " +
    		"edge_order BIGINT)")
    void createEdgeTable();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge_property_idx (" +
    		"id    BIGINT PRIMARY KEY AUTO_INCREMENT, " +
    		"e_id  BIGINT, " +
    		"name  VARCHAR(100), " +
    		"value BLOB)")
    void createEdgePropertyTable();
    @SqlUpdate(
            "CREATE UNIQUE INDEX e_uni_prop " +
            "ON edge_property_idx(e_id, name)")
    void createEdgePropertyTableIndex();
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS vertex_property_idx (" +
    		"id    BIGINT PRIMARY KEY AUTO_INCREMENT, " +
    		"v_id  BIGINT, " +
    		"name  VARCHAR(100), " +
    		"value BLOB)")
    void createVertexPropertyTable();
    @SqlUpdate(
            "CREATE UNIQUE INDEX v_uni_prop " +
    		"ON vertex_property_idx(v_id, name)")
    void createVertexPropertyTableIndex();

    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS transaction (" +
    		"id     BIGINT PRIMARY KEY AUTO_INCREMENT, " +
    		"commit TIMESTAMP, " +
    		"user   VARCHAR(100))")
    void createTransactionTable();

//    @SqlUpdate("CREATE TABLE IF NOT EXISTS obj_id (id BIGINT PRIMARY KEY AUTO_INCREMENT)")
//    void createObjectIdTable();

    @SqlUpdate("DROP TABLE IF EXISTS vertex, edge, edge_property_idx, vertex_property_idx, transaction")
    void dropTables();
    

    
    /*
     *  Transaction related operations
     */
    
    @SqlUpdate(
            "DELETE " +
    		"FROM transaction " +
    		"WHERE id = :id")
    void removeTxn(@Bind("id") long id);

    
    @SqlUpdate(
            "UPDATE transaction " +
    		"SET commit = :timestamp " +
    		"WHERE id = :id")
    void commitTxn(@Bind("id") long id, @Bind("timestamp") Date timestamp);

    
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO transaction (user) VALUES (:user)")
    long createTxn(@Bind("user") String user);

    
    @SqlQuery(
            "SELECT id, user, commit " +
    		"FROM transaction " +
    		"WHERE id = :id")
    @Mapper(AmberTransactionMapper.class)
    AmberTransaction 
        findTxnById(@Bind("id") long id);

    
    
    /*
     *  Edge related operations
     */
    
    @SqlUpdate(
            "DELETE FROM edge " +
    		"WHERE id = :id")
    void removeEdge(@Bind("id") long id);

    
    @SqlUpdate(
            "UPDATE edge " +
            "SET properties = :properties " +
            "WHERE id = :id")
    void updateEdgeProperties(@Bind("id") long id, @Bind("properties") String properties);

    
    @SqlUpdate(
            "UPDATE edge " +
    		"SET edge_order = :edgeOrder " +
    		"WHERE id = :id")
    void updateEdgeOrder(@Bind("id") long id, @Bind("edgeOrder") Integer edgeOrder);
    
    
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO edge (txn_start, txn_open, properties, v_out, v_in, label, edge_order) " +
    		"VALUES (:txnStart, true, :properties, :outId, :inId, :label, 0)")
    long createEdge(@Bind("txnStart") long txnStart, @Bind("properties") String properties, @Bind("outId") long outId, @Bind("inId") long inId, @Bind("label") String label);

    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
    		"FROM edge " +
    		"WHERE id = :id")
    @Mapper(AmberEdgeMapper.class)
    AmberEdge 
        findEdgeById(@Bind("id") long id);
    
    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE v_out = :vertexId " +
            "ORDER BY edge_order")
    @Mapper(AmberEdgeMapper.class)
    Iterator<AmberEdge> 
        findOutEdgesByVertexId(@Bind("vertexId") long vertexId);

    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
            "FROM edge " +
            "WHERE v_in = :vertexId " +
            "ORDER BY edge_order")
    @Mapper(AmberEdgeMapper.class)
    Iterator<AmberEdge> 
        findInEdgesByVertexId(@Bind("vertexId") long vertexId);

    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
    		"FROM edge " +
    		"WHERE v_out = :vertexId " +
    		"AND label in (:labels) " +
    		"ORDER BY edge_order")
    @Mapper(AmberEdgeMapper.class)
    Iterator<AmberEdge> 
        findOutEdgesByVertexIdAndLabel(@Bind("vertexId") long vertexId, @Bind("labels") String labels);

    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
    		"FROM edge " +
    		"WHERE v_in = :vertexId AND label in (:labels) " +
    		"ORDER BY edge_order")
    @Mapper(AmberEdgeMapper.class)
    Iterator<AmberEdge> 
        findInEdgesByVertexIdAndLabel(@Bind("vertexId") long vertexId, @Bind("labels") String labels);
    
    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
            "FROM edge")
    @Mapper(AmberEdgeMapper.class)
    Iterator<AmberEdge> 
        findAllEdges();
    
    
    /*
     *  Vertex related operations
     */
    
    @SqlUpdate("DELETE FROM vertex WHERE id = :id")
    void removeVertex(@Bind("id") long id);
    
    
    @SqlUpdate("UPDATE vertex SET properties = :properties WHERE id = :id")
    void updateVertex(@Bind("id") long id, @Bind("properties") String properties);

    @SqlUpdate("UPDATE vertex SET properties = :properties WHERE id = :id")
    void updateVertexProperties(@Bind("id") long id, @Bind("properties") String properties);

    
    @GetGeneratedKeys
    @SqlUpdate(
            "INSERT INTO vertex (txn_start, txn_open, properties, pi) " +
    		"VALUES (:txnStart, true, :properties, :pi)")
    long createVertex(@Bind("txnStart") Long txnStart, @Bind("properties") String properties, @Bind("pi") String pi);

    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, pi " +
            "FROM vertex " +
            "WHERE id = :id")
    @Mapper(AmberVertexMapper.class)
    AmberVertex 
        findVertexById(@Bind("id") long id);
    

    // allow multiple of the same vertex to be returned to conform
    // to the Blueprint VertexTestSuite gettingEdgesAndVertices
    @SqlQuery(
            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
            "       v.properties properties, v.pi pi " +
    		"FROM vertex v, edge e " +
    		"WHERE v.id = e.v_in " +
    		"AND e.v_out = :srcVertexId " +
    		"ORDER BY e.edge_order")
    @Mapper(AmberVertexMapper.class)
    Iterator<AmberVertex> 
        findVertexByInEdgeFromVertexId(@Bind("srcVertexId") long vertexId);
    
    
    // allow multiple of the same vertex to be returned to conform
    // to the Blueprint VertexTestSuite gettingEdgesAndVertices
    @SqlQuery(
            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
            "       v.properties properties, v.pi pi " +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND e.v_in = :srcVertexId " +
            "ORDER BY e.edge_order")
    @Mapper(AmberVertexMapper.class)
    Iterator<AmberVertex> 
        findVertexByOutEdgeToVertexId(@Bind("srcVertexId") long vertexId);
    
    
    @SqlQuery(
            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
            "       v.properties properties, v.pi pi " +
            "FROM vertex v, edge e " +
    		"WHERE v.id = e.v_in " +
    		"AND e.v_out = :srcVertexId " +
    		"AND e.label = :label " +
    		"ORDER BY e.edge_order")
    @Mapper(AmberVertexMapper.class)
    Iterator<AmberVertex> 
        findVertexByInEdgeLabelFromVertexId(@Bind("srcVertexId") long vertexId, @Bind("label") String label);
    
    
    @SqlQuery(
            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
            "       v.properties properties, v.pi pi " +
            "FROM vertex v, edge e " +
            "WHERE v.id = e.v_out " +
            "AND e.v_in = :srcVertexId " +
            "AND e.label = :label " +
            "ORDER BY e.edge_order")
    @Mapper(AmberVertexMapper.class)
    Iterator<AmberVertex> 
        findVertexByOutEdgeLabelToVertexId(@Bind("srcVertexId") long vertexId, @Bind("label") String label);

    
    @SqlQuery(
            "SELECT id, txn_start, txn_end, txn_open, properties, pi " +
            "FROM vertex")
    @Mapper(AmberVertexMapper.class)
    Iterator<AmberVertex> 
        findAllVertices();

    
    @SqlQuery(
            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
            "       v.properties properties, v.pi pi " +
    		"FROM vertex v, edge e " +
    		"WHERE e.id = :edgeId " +
    		"AND v.id = e.v_out")
    @Mapper(AmberVertexMapper.class)
    AmberVertex 
        findVertexByOutEdge(@Bind("edgeId") long edgeId);

    
    @SqlQuery(
            "SELECT v.id id, v.txn_start txn_start, v.txn_end txn_end, v.txn_open txn_open, " +
            "       v.properties properties, v.pi pi " +
    		"FROM vertex v, edge e " +
    		"WHERE e.id = :edgeId " +
    		"AND v.id = e.v_in")
    @Mapper(AmberVertexMapper.class)
    AmberVertex 
        findVertexByInEdge(@Bind("edgeId") long edgeId);

    
    
    /*
     * Property index related operations
     */
    
    @GetGeneratedKeys
    @SqlUpdate(
            "REPLACE INTO vertex_property_idx (v_id, name, value) " +
    		"VALUES (:id, :name, :value)")
    long setVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Object value);

//    @GetGeneratedKeys
//    @SqlUpdate(
//            "MERGE INTO vertex_property_idx (v_id, name, value) " +
//            "KEY (v_id, name) " +
//            "VALUES (:id, :name, :value)")
//    long setVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Object value);
    
    @SqlUpdate(
            "DELETE FROM vertex_property_idx " +
    		"WHERE v_id = :id " +
    		"AND name = :name")
    void removeVertexPropertyIndexEntry(@Bind("id") long id, @Bind("name") String name);

    @SqlUpdate(
            "DELETE FROM vertex_property_idx " +
    		"WHERE v_id = :id")
    void removeVertexPropertyIndexEntries(@Bind("id") long id);
    
    @SqlQuery(
            "SELECT v.id id, txn_start, txn_end, txn_open, properties, pi " +
            "FROM vertex v, vertex_property_idx vpi " +
            "WHERE v.id = vpi.v_id " +
            "AND vpi.name = :name " +
            "AND vpi.value = :value")
    @Mapper(AmberVertexMapper.class)
    Iterator<AmberVertex> 
        findVerticesByProperty(@Bind("name") String name, @Bind("value") Object value);

    
    
    @GetGeneratedKeys
    @SqlUpdate(
          "REPLACE INTO edge_property_idx (e_id, name, value) " +
          "VALUES (:id, :name, :value)")
    long setEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Object value);
//    @GetGeneratedKeys
//    @SqlUpdate(
//            "MERGE INTO edge_property_idx (e_id, name, value) " +
//            "KEY (e_id, name) " +
//            "VALUES (:id, :name, :value)")
//    long setEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name, @Bind("value") Object value);
    
    @SqlUpdate(
            "DELETE FROM edge_property_idx " +
            "WHERE e_id = :id " +
            "AND name = :name")
    void removeEdgePropertyIndexEntry(@Bind("id") long id, @Bind("name") String name);

    @SqlUpdate(
            "DELETE FROM edge_property_idx " +
    		"WHERE e_id = :id")
    void removeEdgePropertyIndexEntries(@Bind("id") long id);
    
    @SqlQuery(
            "SELECT e.id id, txn_start, txn_end, txn_open, properties, v_out, v_in, label, edge_order " +
            "FROM edge e, edge_property_idx epi " +
            "WHERE e.id = epi.e_id " +
            "AND epi.name = :name " +
            "AND epi.value = :value " +
            "ORDER BY edge_order")
    @Mapper(AmberEdgeMapper.class)
    Iterator<AmberEdge> 
        findEdgesByProperty(@Bind("name") String name, @Bind("value") Object value);

    
    /*
     *  Transaction related operations
     */
    
    //@SqlUpdate("DELETE FROM transaction WHERE id = :id")
    //void removeTxn(@Bind("id") long id);
    // Note: will we record failed transactions ?
    
    @SqlUpdate(
            "UPDATE transaction " +
    		"SET commit = :commit " +
    		"WHERE id = :id")
    void updateTxn(@Bind("id") long id, @Bind("commit") Date commit);

    
    void close();
}

