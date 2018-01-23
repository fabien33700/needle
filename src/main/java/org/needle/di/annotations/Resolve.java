package org.needle.di.annotations;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>This annotation is used to mark a class as a service, which means
 *   a class that can be instanciated, and whose instances can be injected by
 *   a <em>ServiceBuilder</em> in other instances field, setter or constructor 
 *   (themsleves <code>@Inject</code> annotated). </p>
 * <p>A service may have dependencies that can be resolved by another <em>ServiceBuilder</em></p>.
 * 
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Resolve {
	/**
	 * The key of the property to resolve.
	 */
	String value() default "";
}
