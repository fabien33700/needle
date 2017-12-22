package org.needle.di;

public class InjectionException extends Exception {

	private static final long serialVersionUID = 4701495830786103003L;
	
	public final static String INSTANCIATION_FAILED = "Unable to instanciated %s service. Did you declare an empty constructor ?";
	public final static String CYCLIC_DEPENDENCIES  = "Class %s has already been proceeded. Seems there is a cyclic dependency.";
	public final static String INJECTION_FAILED     = "Unable to inject a matching instance in the field %s.";
	public final static String NESTED_EXCEPTION     = "Unable to create the dependency instance to inject in the field %s. Nested exception : %s";
	public final static String UNAVAILABLE_PARAM    = "The parameter %s could not be resolved with the builder configuration. Had the parameter been declared ?";

	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InjectionException(String message) {
		super(message);
	}
	
	

}
