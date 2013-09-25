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


@BindingAnnotation(BindAmberProperty.AmberPropertyBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindAmberProperty { 

    public static class AmberPropertyBinderFactory implements BinderFactory {
        public Binder build(Annotation annotation) {
            return new Binder<BindAmberProperty, AmberProperty>() {
                public void bind(SQLStatement q, BindAmberProperty bind, AmberProperty p) {
                    q.bind("id", p.id());
                    q.bind("name", p.name);
                    q.bind("type", p.getType());

                    switch (p.getType()) {
                    case "s":
                        q.bind("s_value", (String) p.getValue());
                        break;
                    case "b":
                        q.bind("b_value", (Boolean) p.getValue());
                        break;
                    case "i":
                        q.bind("i_value", (Integer) p.getValue());
                        break;
                    case "d":
                        q.bind("d_value", (Double) p.getValue());
                        break;
                    default:
                        break;
                    }
                }
            };
        }
    }
}