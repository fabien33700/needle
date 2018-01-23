package org.needle.di.errors;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An exception thrown when a cycle has been detected in the
 *   chain of detected dependencies for the target type.
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
public class CyclicDependencyException extends InjectionException {

	private static final long serialVersionUID = 1477073793173902135L;
	
	/**
	 * Returns a string representing the list of all detected and proceeded 
	 *   dependencies.
	 * @param dependencies A collection that contains all dependencies representing class objects
	 * @return
	 */
	private static String getDependencyList(Collection<Class<?>> dependencies) {
		return dependencies.stream()
				.map(Class::getName)
				.collect(Collectors.joining(", "));
	}

	/**
	 * Constructs a CyclicDependencyException instance.
	 * @param cycle The type class that closed the cycle 
	 * @param dependencies The list of already proceeded dependencies classes
	 */
	public CyclicDependencyException(Class<?> cycle, Collection<Class<?>> dependencies) {
		super(String.format(CYCLIC_DEPENDENCIES, 
				cycle.getName(), getDependencyList(dependencies)));
	}

}
