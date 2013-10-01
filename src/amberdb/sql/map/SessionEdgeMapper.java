package amberdb.sql.map;

import amberdb.sql.AmberEdge;
import amberdb.sql.AmberGraph;
import amberdb.sql.AmberVertex;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class SessionEdgeMapper implements ResultSetMapper<AmberEdge> {

    public static AmberGraph graph;

    public AmberEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        return new AmberEdge(graph, rs.getLong("id"));

    }
}
