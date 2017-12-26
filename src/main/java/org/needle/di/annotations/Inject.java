package org.needle.di.annotations;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>This annotation is used to mark a class field, setter or constructor as injectable
 *   for a ServiceBuilder instance.</p>
 * <p>ServiceBuilder scans its target class representation, all fields, methods and
 *   constructors marked with <code>Inject</code> annotation to resolve its dependencies.</p>
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD, CONSTRUCTOR })
public @interface Inject {

}
