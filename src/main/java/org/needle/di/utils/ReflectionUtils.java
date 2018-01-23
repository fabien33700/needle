package org.needle.di.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>An utility class for Reflection test or manipulation.</p>
 * 
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 */
public class ReflectionUtils {
	
	/**
	 * Returns member name from setter method.
	 * @param setter The setter method object
	 * @return The field name, or empty empty String if method is not a setter
	 */
	public static String getMemberNameFromSetter(Method setter) {
		if (setter.getName().startsWith("set")) {
			final String name = setter.getName();
			return name.substring(3, 4).toLowerCase() +
					name.substring(4);
		}
		return "";
	}
	
	/**
	 * Indicate whether the given method is a setter.
	 * @param method The method to check
	 * @return
	 */
	public static boolean isSetter(Class<?> clazz, Method method) {
		try {
			final String fieldName = getMemberNameFromSetter(method);
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
	 * @return
	 */
	@SafeVarargs
	public static boolean hasAnnotations(AnnotatedElement element, 
			Class<? extends Annotation>... annotations) {
		return Stream.of(annotations)
				.anyMatch(element::isAnnotationPresent);
	}
	
	/**
	 * <p>Describe a method from its representation, giving its
	 *   name and the list of the arguments types.</p>
	 * @param method The method reflect representation
	 * @return The method description
	 */
	public static String describeMethod(Method method) {
		return method.getName() + "(" +
				Stream.of(method.getParameterTypes())
					.map(Class::getSimpleName)
					.collect(Collectors.joining(", "))
				+ ")";
	}

}
