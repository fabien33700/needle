package org.needle.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to mark a class as a service, which means
 *   a class that can be instantiated, and whose instances can be injected by
 *   a ServiceBuilder in other instances field, setter or constructor
 *   (themselves @Inject annotated).
 * A service may have dependencies that can be resolved by another ServiceBuilder.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Service {

}
