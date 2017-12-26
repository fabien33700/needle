package org.needle.di.exceptions;

import java.lang.reflect.Method;

/**
 * An exception thrown when a method <code>@Inject</code> annotated method 
 *   in the target class is not a correct setter.
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
public class NotSetterException extends InjectionException {

	private static final long serialVersionUID = 1280698776135366950L;

	/**
	 * Constructs an NotSetterException instance from the problematic method 
	 *   reflection representation object
	 * @param method The problematic method
	 */
	public NotSetterException(Method method) {
		super(String.format(NOT_A_SETTER, method.getName()));
	}

}
