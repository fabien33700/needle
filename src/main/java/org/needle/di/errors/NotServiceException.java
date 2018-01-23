package org.needle.di.errors;

/**
 * An exception thrown when the type of an instance to inject is not 
 *   marked with the <code>@Service</code> annotation.
 *   
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 */
public class NotServiceException extends InjectionException {

	private static final long serialVersionUID = 6708705225811744093L;

	/**
	 * Constructs a NotServiceException instance from the problematic
	 *   type class.
	 * @param clazz The problematic type class
	 */
	public NotServiceException(Class<?> clazz) {
		super(String.format(NOT_A_SERVICE, clazz.getName()));
	}
}
