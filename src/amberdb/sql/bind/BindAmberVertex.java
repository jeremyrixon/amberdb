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


@BindingAnnotation(BindAmberVertex.AmberVertexBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindAmberVertex {

    public static class AmberVertexBinderFactory implements BinderFactory {
        public Binder build(Annotation annotation) {
            return new Binder<BindAmberVertex, AmberVertex>() {
                public void bind(SQLStatement q, BindAmberVertex bind, AmberVertex v) {
                    q.bind("id", v.getId());
                    q.bind("txn_start", v.getTxnStart());
                    q.bind("state", v.getState().toString());
                }
            };
        }
    }
}