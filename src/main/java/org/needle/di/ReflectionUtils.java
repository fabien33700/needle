package org.needle.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An utility class for Reflection test or manipulation.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
public class ReflectionUtils {
	
	/**
	 * Returns member name from setter method.
	 * @param methodName The method name to check
	 * @return The field name, or empty empty String if method is not a setter
	 */
	public static String getMemberNameFromSetter(String methodName) {
		if (methodName != null && methodName.startsWith("set")) {
			return methodName.substring(3, 4).toLowerCase() +
					methodName.substring(4);
		}
		return "";
	}
	
	/**
	 * Indicate whether the given method is a setter.
	 * @param method The method to check
	 * @return true if the method is a setter of given class, false else.
	 */
	public static boolean isSetter(Class clazz, Method method) {
		try {
			final String fieldName = getMemberNameFromSetter(method.getName());
			final Field field = clazz.getDeclaredField(fieldName);
				
			return method.getReturnType().equals(Void.TYPE) &&
					method.getParameterCount() == 1 &&
					method.getParameterTypes()[0].isAssignableFrom(
							field.getType());
			
		} catch (NoSuchFieldException e) {
			return false;
		}
	}
	
	
	/**
	 * Indicates whether the element is annotated by at least
	 *   one of the annotations type provided in arguments
	 * @param element The element to check
	 * @param annotations The array of Annotation classes to match
	 * @return true if the element is annotated by one of the annotation classes
	 */
	@SafeVarargs
	public static boolean hasOneAnnotation(AnnotatedElement element,
		   Class<? extends Annotation>... annotations) {
		return Stream.of(annotations)
				.anyMatch(element::isAnnotationPresent);
	}
	
	/**
	 * Describe a method from its representation, giving its
	 *   name and the list of the arguments types.
	 * @param method The method reflect representation
	 * @return The method description
	 */
	public static String describeMethod(Method method) {
		return method.getName() + "(" +
				Stream.of(method.getParameterTypes())
					.map(Class::getSimpleName)
					.collect(Collectors.joining(", "))
				+ ") : " +
				method.getReturnType().getSimpleName();
	}

}
