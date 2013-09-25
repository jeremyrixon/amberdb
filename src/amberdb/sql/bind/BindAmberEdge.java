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
                    q.bind("id", e.id());
                    q.bind("txn_start", e.txnStart());
                    q.bind("v_in", e.inVertexId);
                    q.bind("v_out", e.outVertexId);
                    q.bind("label", e.label);
                    q.bind("edge_order", e.edgeOrder);
                    q.bind("state", e.sessionState().ordinal());
                }
            };
        }
    }
}