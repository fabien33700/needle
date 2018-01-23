package org.needle.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to mark a field or a setter method, indicating thus
 *   to the ServiceBuilder to try resolving the member value with a value registered in
 *   its configuration.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Resolve {
	/**
	 * The key of the property to resolve.
	 */
	String value() default "";
}
