package org.needle.di.exceptions;

import java.lang.reflect.Method;

/**
 * Represents a base error occuring during dependency injection process.
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 * 
 */
public abstract class InjectionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	/*
	 * Exception error messages constants
	 */
	final static String INSTANCIATION_FAILED = "Unable to instanciated %s service. Did you declare an empty constructor ?";
	final static String CYCLIC_DEPENDENCIES  = "Class %s has already been proceeded. Seems there is a cyclic dependency. Dependency graph : %s";
	final static String INJECTION_FAILED     = "Unable to inject a matching instance in field %s.";
	final static String NOT_A_SERVICE        = "Could not inject a non-service typed field. Put @Annotation on %s class declaration.";
	final static String NESTED_EXCEPTION     = "Unable to create the dependency instance to inject in field %s. Nested exception : %s";
	final static String NESTED_EXCEPTION_2   = "Unable to create the dependency instance to inject in argument %d of the constructor %s. Nested exception : %s";
	final static String NOT_A_SETTER         = "@Inject annotated method %s must be a setter.";

	/**
	 * Returns field name from setter method name
	 * @param setter The setter method object
	 * @return The field name, or empty empty String if method is not a setter
	 */
	protected static String getFieldFromSetter(Method setter) {
		if (setter.getName().startsWith("set")) {
			return setter.getName().substring(0, 3) +
					setter.getName().substring(3, 4).toLowerCase() +
					setter.getName().substring(4);
		}
		return "";
	}
	
	/**
	 * Constructs an InjectionException instance
	 * @param message The error message
	 * @param cause The cause of the injection error
	 */
	protected InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an InjectionException instance
	 * @param message The error message
	 */
	protected InjectionException(String message) {
		super(message);
	}

}
