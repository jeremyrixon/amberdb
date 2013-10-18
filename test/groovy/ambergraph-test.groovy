// ambergraph-test.groovy: 
//
//   This script helps to verify the retrieval of the 
//   vertices from ambergraph.                
//
//   It can also be modified to verify the updates to
//   ambergraph.
//
import com.tinkerpop.gremlin.*
import com.tinkerpop.gremlin.java.*
import com.tinkerpop.gremlin.pipes.filter.*
import com.tinkerpop.gremlin.pipes.sideeffect.*
import com.tinkerpop.gremlin.pipes.transform.*
import com.tinkerpop.blueprints.*
import static com.tinkerpop.blueprints.Direction.*
import static com.tinkerpop.blueprints.TransactionalGraph$Conclusion.*
import static com.tinkerpop.blueprints.Compare.*
import com.tinkerpop.blueprints.impls.*
import com.tinkerpop.blueprints.impls.tg.*
import com.tinkerpop.blueprints.impls.neo4j.*
import com.tinkerpop.blueprints.impls.neo4j.batch.*
import com.tinkerpop.blueprints.impls.orient.*
import com.tinkerpop.blueprints.impls.orient.batch.*
import com.tinkerpop.blueprints.impls.dex.*
import com.tinkerpop.blueprints.impls.rexster.*
import com.tinkerpop.blueprints.impls.sail.*
import com.tinkerpop.blueprints.impls.sail.impls.*
import com.tinkerpop.blueprints.util.*
import com.tinkerpop.blueprints.util.io.*
import com.tinkerpop.blueprints.util.io.gml.*
import com.tinkerpop.blueprints.util.io.graphml.*
import com.tinkerpop.blueprints.util.io.graphson.*
import com.tinkerpop.blueprints.util.wrappers.*
import com.tinkerpop.blueprints.util.wrappers.batch.*
import com.tinkerpop.blueprints.util.wrappers.batch.cache.*
import com.tinkerpop.blueprints.util.wrappers.event.*
import com.tinkerpop.blueprints.util.wrappers.event.listener.*
import com.tinkerpop.blueprints.util.wrappers.id.*
import com.tinkerpop.blueprints.util.wrappers.partition.*
import com.tinkerpop.blueprints.util.wrappers.readonly.*
import com.tinkerpop.blueprints.oupls.sail.*
import com.tinkerpop.blueprints.oupls.sail.pg.*
import com.tinkerpop.blueprints.oupls.jung.*
import com.tinkerpop.pipes.*
import com.tinkerpop.pipes.branch.*
import com.tinkerpop.pipes.filter.*
import com.tinkerpop.pipes.sideeffect.*
import com.tinkerpop.pipes.transform.*
import com.tinkerpop.pipes.util.*
import com.tinkerpop.pipes.util.iterators.*
import com.tinkerpop.pipes.util.structures.*
import org.apache.commons.configuration.*
import com.tinkerpop.gremlin.Tokens.T
import com.tinkerpop.gremlin.groovy.*
import groovy.grape.Grape
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.NoSuchObjectException;
import amberdb.PIUtil;
import amberdb.TempDirectory;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.ImageFile;
import amberdb.model.Node;
import amberdb.model.Page;
import amberdb.model.Section;
import amberdb.model.Work;
import amberdb.relation.ExistsOn;
import amberdb.relation.IsCopyOf;
import amberdb.relation.IsFileOf;
import amberdb.relation.IsPartOf;
import amberdb.relation.IsSourceCopyOf;
import amberdb.relation.Relation;
import amberdb.sql.AmberEdge;
import amberdb.sql.AmberGraph;
import amberdb.sql.AmberProperty;
import amberdb.sql.AmberTransaction;
import amberdb.sql.AmberVertex;
import amberdb.sql.DataType;
import amberdb.sql.InSessionException;
import amberdb.sql.InvalidDataTypeException;
import amberdb.sql.PersistenceException;
import amberdb.sql.State;
import amberdb.sql.TransactionException;
import amberdb.sql.bind.BindAmberEdge;
import amberdb.sql.bind.BindAmberProperty;
import amberdb.sql.bind.BindAmberVertex;
import amberdb.sql.dao.EdgeDao;
import amberdb.sql.dao.PersistentDao;
import amberdb.sql.dao.PersistentDaoH2;
import amberdb.sql.dao.PersistentDaoMYSQL;
import amberdb.sql.dao.SessionDao;
import amberdb.sql.dao.TransactionDao;
import amberdb.sql.dao.VertexDao;
import amberdb.sql.map.LongArrayMapper;
import amberdb.sql.map.PersistentEdgeMapper;
import amberdb.sql.map.PersistentEdgeMapperFactory;
import amberdb.sql.map.PersistentPropertyMapper;
import amberdb.sql.map.PersistentVertexMapper;
import amberdb.sql.map.PersistentVertexMapperFactory;
import amberdb.sql.map.SessionEdgeMapper;
import amberdb.sql.map.SessionEdgeMapperFactory;
import amberdb.sql.map.SessionPropertyMapper;
import amberdb.sql.map.SessionVertexMapper;
import amberdb.sql.map.SessionVertexMapperFactory;
import amberdb.AmberDbTest;
import amberdb.IngestTest;
import amberdb.JellyTest;
import amberdb.JobMockup;
import amberdb.PIUtilTest;
import amberdb.AmberDbFactory;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.DriverManager;
import org.h2.jdbcx.JdbcConnectionPool;
import java.nio.file.Path;
import java.nio.file.Paths;

dbUrl = "jdbc:mysql://snowy.nla.gov.au:3306/dlir?zeroDateTimeBehavior=convertToNull&useUnicode=yes&characterEncoding=UTF-8"
dbUser = "dlir"
dbPassword = "dlir"
rootPath = "."

mds = new MysqlDataSource()
mds.setURL(dbUrl)
mds.setUser(dbUser)
mds.setPassword(dbPassword)

DriverManager.registerDriver(new org.h2.Driver())
sessionDs = JdbcConnectionPool.create("jdbc:h2:" + Paths.get(rootPath).resolve("gremgraph") + ";MVCC=true", "grem", "grem")
amber = new AmberGraph(sessionDs, mds, "amberdb")

collection = amber.v(179720227L)
bookBBill = amber.v(179722129L)
workFrontCover = amber.v(179722445L)
workTitlePage = amber.v(179722746L)


// workFrontCover.getSubType()
workFrontCover.getProperty("subType")

// workFrontCover.getSubUnitType()
workFrontCover.getProperty("subUnitType")

// workFrontCover.getCopies()
workFrontCover.in('isCopyOf')

// workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getId()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.OCR_JSON_COPY.code())

// workFrontCover.getCopy(CopyRole.MASTER_COPY).getId()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.MASTER_COPY.code())

// workFrontCover.getCopy(CopyRole.ACCESS_COPY).getId()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.ACCESS_COPY.code())

// workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getCarrier()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.OCR_JSON_COPY.code()).property("carrier")

// workFrontCover.getCopy(CopyRole.MASTER_COPY).getCarrier()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.MASTER_COPY.code()).property("carrier")

// workFrontCover.getCopy(CopyRole.ACCESS_COPY).getCarrier()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.ACCESS_COPY.code()).property("carrier")

// workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getCopyRole()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.OCR_JSON_COPY.code()).property("copyRole")

// workFrontCover.getCopy(CopyRole.MASTER_COPY).getCopyRole()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.MASTER_COPY.code()).property("copyRole")

// workFrontCover.getCopy(CopyRole.ACCESS_COPY).getCopyRole()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.ACCESS_COPY.code()).property("copyRole")

// workFrontCover.getCopy(CopyRole.OCR_JSON_COPY).getFile().getId()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.OCR_JSON_COPY.code()).in('isFileOf')

// workFrontCover.getCopy(CopyRole.MASTER_COPY).getFile().getId()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.MASTER_COPY.code()).in('isFileOf')

// workFrontCover.getCopy(CopyRole.ACCESS_COPY).getFile().getId()
workFrontCover.in('isCopyOf').has('copyRole', CopyRole.ACCESS_COPY.code()).in('isFileOf')


