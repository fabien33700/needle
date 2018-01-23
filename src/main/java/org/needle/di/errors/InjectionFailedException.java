package org.needle.di.errors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.needle.di.utils.ReflectionUtils;

/**
 * An exception thrown when a field could not be injected with an instance.
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
public class InjectionFailedException extends InjectionException {

	private static final long serialVersionUID = -4340908866729540699L;

	/**
	 * Constructs an InjectionFailedException.
	 * @param setter The setter method representing object
	 * @param cause The source cause of the error
	 */
	public InjectionFailedException(Method setter, ReflectiveOperationException cause) {
		super(String.format(INJECTION_FAILED, 
				ReflectionUtils.getMemberNameFromSetter(setter)), cause);
	}
	
	/**
	 * Constructs an InjectionFailedException.
	 * @param field The field that could not be injected
	 * @param cause The source cause of the error
	 */
	public InjectionFailedException(Field field, ReflectiveOperationException cause) {
		super(String.format(INJECTION_FAILED, field.getName()), cause);
	}

}
