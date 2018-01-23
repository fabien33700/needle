package org.needle.di.errors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.needle.di.utils.ReflectionUtils;

/**
 * In case of nested ServiceBuilder calls, when an Exception occurs,
 *   it is captured, encapsulated in a NestedInjectionException instance
 *   which is thrown to the top ServiceBuilder call.
 *   
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 */
public class NestedInjectionException extends InjectionException {
	
	private static final long serialVersionUID = -479379668503057842L;

	/**
	 * Constructs a NestedInjectionException, from the setter for which the injection failed.
	 * @param method The method representation class 
	 * @param nested The source exception
	 */
	public NestedInjectionException(Method method, InjectionException nested) {
		super(String.format(NESTED_EXCEPTION_S, 
				ReflectionUtils.describeMethod(method)), nested);
	}
	
	/**
	 * Constructs a NestedInjectionException, from the field for which the injection failed.
	 * @param field The field representation class 
	 * @param nested The source exception
	 */
	public NestedInjectionException(Field field, InjectionException nested) {
		super(String.format(NESTED_EXCEPTION_F, field.getName()), nested);
	}

	/**
	 * Constructs a NestedInjectionException, from the constructor
	 *  for which the injection failed.
	 * @param index The index of the constructor's argument for which the injection failed
	 * @param constructor The constructor
	 * @param nested The source exception
	 */
	public NestedInjectionException(
			int index, 
			Constructor<?> constructor, 
			InjectionException nested) {
		super(String.format(NESTED_EXCEPTION_A, 
				index, constructor),nested);
	}

}
