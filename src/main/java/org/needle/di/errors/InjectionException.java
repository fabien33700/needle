package org.needle.di.errors;

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
	final static String NESTED_EXCEPTION_F   = "Unable to create the dependency instance to inject in field %s.";
	final static String NESTED_EXCEPTION_A   = "Unable to create the dependency instance to inject in argument %d of the constructor %s.";
	final static String NESTED_EXCEPTION_S   = "Unable to create the dependency instance to inject with the method %s.";
	final static String NOT_A_SETTER         = "The method %s must be a setter.";
	final static String UNRESOLVABLE_F		  = "The field %s cannot be resolved. No parameter with key %s was found in the configuration.";
	
	/**
	 * Constructs an InjectionException instance.
	 * @param message The error message
	 * @param cause The cause of the injection error
	 */
	protected InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an InjectionException instance.
	 * @param message The error message
	 */
	protected InjectionException(String message) {
		super(message);
	}

}
