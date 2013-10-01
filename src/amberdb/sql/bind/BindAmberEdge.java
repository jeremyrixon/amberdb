package amberdb.sql.bind;

import amberdb.sql.*;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;


@BindingAnnotation(BindAmberEdge.AmberEdgeBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindAmberEdge {

    public static class AmberEdgeBinderFactory implements BinderFactory {
        public Binder build(Annotation annotation) {
            return new Binder<BindAmberEdge, AmberEdge>() {
                public void bind(SQLStatement q, BindAmberEdge bind, AmberEdge e) {
                    q.bind("id", e.getId());
                    q.bind("txn_start", e.getTxnStart());
                    q.bind("v_in", e.getInVertexId());
                    q.bind("v_out", e.getOutVertexId());
                    q.bind("label", e.getLabel());
                    q.bind("edge_order", e.getEdgeOrder());
                    q.bind("state", e.getState().toString());
                }
            };
        }
    }
}