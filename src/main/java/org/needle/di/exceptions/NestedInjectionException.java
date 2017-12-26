package org.needle.di.exceptions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * In case of nested ServiceBuilder calls, when an Exception occurs,
 *   it is captured, encapsulated in a NestedInjectionException instance
 *   which is thrown to the top ServiceBuilder call.
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
public class NestedInjectionException extends InjectionException {
	
	private static final long serialVersionUID = -479379668503057842L;

	/**
	 * Constructs a NestedInjectionException, from the setter for which the injection failed
	 * @param setter The method representation class of the setter 
	 * @param nested The source exception
	 */
	public NestedInjectionException(Method setter, InjectionException nested) {
		super(String.format(NESTED_EXCEPTION, getFieldFromSetter(setter), nested), nested);
	}
	
	/**
	 * Constructs a NestedInjectionException, from the field for which the injection failed
	 * @param field The field representation class 
	 * @param nested The source exception
	 */
	public NestedInjectionException(Field field, InjectionException nested) {
		super(String.format(NESTED_EXCEPTION, field.getName(), nested), nested);
	}

	/**
	 * Constructs a NestedInjectionException, from the constructor for which the injection failed.
	 * @param index The index of the constructor's argument for which the injection failed
	 * @param constructor The constructor
	 * @param nested The source exception
	 */
	public NestedInjectionException(
			int index, 
			Constructor<?> constructor, 
			InjectionException nested) {
		super(String.format(NESTED_EXCEPTION_2, 
				index, constructor, nested),nested);
	}

}
