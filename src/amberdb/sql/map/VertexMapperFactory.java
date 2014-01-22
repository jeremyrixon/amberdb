package amberdb.sql.map;


import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.sql.AmberEdge;
import amberdb.sql.AmberGraph;

@SuppressWarnings("rawtypes")
public class VertexMapperFactory implements ResultSetMapperFactory {

    private AmberGraph graph;
    
    @Override
    public boolean accepts(Class type, StatementContext ctx) {
        return AmberEdge.class.isAssignableFrom(type);
    }

    @Override
    public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
        return new VertexMapper(graph);
    }
    
    public void setGraph(AmberGraph graph) {
        this.graph = graph;
    }
}