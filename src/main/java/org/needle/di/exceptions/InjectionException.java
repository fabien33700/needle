package org.needle.di.exceptions;

/**
 * Represents a base error occurring during dependency injection process.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
public class InjectionException extends Exception {

	/*
	 * Exception error messages constants
	 */
	public final static String INSTANTIATION_FAILED = "Unable to instantiated %s service. Did you declare an empty constructor ?";
	public final static String CYCLIC_DEPENDENCIES  = "Class %s has already been proceeded. Seems there is a cyclic dependency. Dependency graph : %s";
	public final static String INJECTION_FAILED     = "Unable to inject a matching instance in field %s.";
	public final static String NOT_A_SERVICE        = "Could not inject a non-service typed field. Put @Annotation on %s class declaration.";
	public final static String NESTED_EXCEPTION_F   = "Unable to create the dependency instance to inject in field %s.";
	public final static String NESTED_EXCEPTION_A   = "Unable to create the dependency instance to inject in argument %d of the constructor %s.";
	public final static String NESTED_EXCEPTION_S   = "Unable to create the dependency instance to inject with the method %s.";
	public final static String NOT_A_SETTER         = "The method %s must be a setter.";
	public final static String UNRESOLVABLE		    = "The field %s cannot be resolved. No parameter with key %s was found in the configuration.";
	
	/**
	 * Constructs an InjectionException instance.
	 * @param cause The cause of the injection error
	 * @param message The error message
	 * @param args The arguments list for error message
	 */
	public InjectionException(Throwable cause, String message,  Object... args) {
		super(String.format(message, args), cause);
	}

	/**
	 * Constructs an InjectionException instance.
	 * @param message The error message
	 * @param args The arguments list for error message
	 */
	public InjectionException(String message, Object... args) {
		super(String.format(message, args));
	}

}
