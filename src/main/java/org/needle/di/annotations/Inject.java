package org.needle.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to mark a class field, setter or constructor as injectable
 *   for a ServiceBuilder instance.
 * ServiceBuilder scans its target class representation, all fields, methods and
 *   constructors marked with Inject annotation to resolve its dependencies.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD, CONSTRUCTOR })
public @interface Inject {

}
