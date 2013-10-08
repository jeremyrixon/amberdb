package amberdb.sql.map;

import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;

@SuppressWarnings("rawtypes")
public class SessionVertexMapperFactory implements ResultSetMapperFactory {

    private AmberGraph graph;
    
    @Override
    public boolean accepts(Class type, StatementContext ctx) {
        return AmberVertex.class.isAssignableFrom(type);
    }

    @Override
    public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
        return new SessionVertexMapper(graph);
    }
    
    public void setGraph(AmberGraph graph) {
        this.graph = graph;
    }
}