package org.needle.di.exceptions;

/**
 * An exception thrown when a the instanciation of the target class
 *   had failed. 
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
public class InstanciationFailedException extends InjectionException {

	private static final long serialVersionUID = -4084001445200361342L;

	/**
	 * Constructs an InstanciationFailedException
	 * @param clazz The class which is not instanciable
	 * @param cause The cause of the problem
	 */
	public InstanciationFailedException(Class<?> clazz, ReflectiveOperationException cause) {
		super(String.format(INSTANCIATION_FAILED, clazz.getName()), cause);
	}

}
