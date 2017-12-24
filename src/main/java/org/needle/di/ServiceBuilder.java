package org.needle.di;

import static org.needle.di.InjectionException.CYCLIC_DEPENDENCIES;
import static org.needle.di.InjectionException.INJECTION_FAILED;
import static org.needle.di.InjectionException.INSTANCIATION_FAILED;
import static org.needle.di.InjectionException.NESTED_EXCEPTION;
import static org.needle.di.InjectionException.NOT_A_SERVICE;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Builder class for building classes instances, resolve and inject recursively 
 *   all dependencies instances.<br/>
 * ServiceBuilder<T> class can be use for every <code>Class&lt;T&gt;</code>, provided that 
 *   it has <code>@Service</code> annotation on its definition. ServiceBuilder<T> scans,
 *   in this order, all constructors, setters and fields marked by the <code>@Inject</code> 
 *   annotation, and tries to build nested dependencies instances.
 *   
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 * 
 * @param T the type of instance to build
 */
public class ServiceBuilder<T> implements Builder<T, InjectionException>{	
	
	/**
	 * The class of the instance we are attempting to build
	 */
	private Class<T> baseClass;
	
	/**
	 * The set of dependencies we have already built
	 */
	private Set<Class<?>> dependencies;
	
	/**
	 * Returns an instance of a builder for the class <code>baseClass</code>
	 * @param baseClass Class of the instance to build
	 * @return
	 */
	public static <T> ServiceBuilder<T> instance(Class<T> baseClass) {
		return new ServiceBuilder<T>(baseClass);
	}

	/**
	 * Returns an instance of a builder for the class <code>baseClass</code>
	 * @param baseClass Class of the instance to build
	 * @param parent The parent ServiceBuilder
	 * @return
	 */
	private static <T> ServiceBuilder<T> instance(
			Class<T> baseClass, ServiceBuilder<?> parent) {
		return new ServiceBuilder<T>(baseClass, parent);
	}

	/**
	 * Create a builder for the class <code>baseClass</code>
	 * @param baseClass Class of the instance to build
	 */
	private ServiceBuilder(Class<T> baseClass) {
		this.baseClass = baseClass;
		this.dependencies = new HashSet<>();
	}
	
	/**
	 * Create a builder for the class <code>baseClass</code>,
	 * with the parent configuration and dependencies set.
	 * @param baseClass Class of the instance to build
	 * @param parent The parent ServiceBuilder, that has called this constructor
	 *   for resolving a dependency
	 */
	private ServiceBuilder(Class<T> baseClass, ServiceBuilder<?> parent) {
		this.baseClass = baseClass;
		this.dependencies = parent.dependencies;
	}

	/**
	 * Returns builder base class.
	 * @return
	 */
	public Class<T> getBaseClass() {
		return baseClass;
	}

	/**
	 * Active method of the builder that examine the class <code>baseClass</code>, scan all 
	 *   its fields, setters and constructors and create the instance, with its dependencies resolved if possible.
	 * @param <T> Type of the class <code>baseClass</code> corresponding to the instance to create
	 * @throws InjectionException An error has occured during the instanciation or dependency injection process
	 */
	public T build() throws InjectionException {
		T target = this.injectByConstructor();
		this.injectBySetters(target);		
		this.injectByFields(target);
	
		return target;
	}

	/**
	 * Inject an instance of the type <code>type</code> by instanciating
	 *   a ServiceBuilder on the class that will resolve recursively its dependencies.
	 * @param type The Class that represents the type of service to inject
	 * @return The instance of <code>type</code>
	 * @throws InjectionException An error has occured during the injection process,
	 *   at this or a nested level.
	 */
	private Object inject(Class<?> type) throws InjectionException {
		
		if (!type.isAnnotationPresent(Service.class)) {
			throw new InjectionException(
					String.format(NOT_A_SERVICE, type.getName()));
		}
		
		// Class already proceeded -> cycle detected
		if (!dependencies.add(type)) {
			throw new InjectionException(
					String.format(CYCLIC_DEPENDENCIES, type.getName()));
		}
		
		return ServiceBuilder.instance(type, this).build();
	}

	/**
	 * Find the first eligible injectable constructor, resolve dependencies and 
	 *   call it
	 * @param target The instance in which to inject by constructor
	 * @throws InjectionException
	 */
	private T injectByConstructor() throws InjectionException {
		T target;
		Constructor<T> constructor = findInjectableConstructor();
		
		// Instanciation
		try {
			if (constructor != null) {
				List<Object> values = new ArrayList<>();
				
				for (Parameter param : constructor.getParameters()) {
					try {
						Object value = this.inject(param.getType());
						values.add(value);
					} catch (InjectionException e) {
						// Chaining exception in the upper call of the stack
						throw new InjectionException(
								String.format(NESTED_EXCEPTION, param.getName(), e), e);
					}
				}
				
				// Constructs the instance with the matching injectable constructor
				target = (T) constructor.newInstance(values.toArray());
			} else {
				// Constructs the instance with the empty constructor
				target = (T) baseClass.newInstance();
			}
		} catch (ReflectiveOperationException e) {
			throw new InjectionException(
					String.format(INSTANCIATION_FAILED, baseClass.getName()), e);
		}
		return target;
	}
	
	/**
	 * Scan all base class methods, gets setters and realize injection on each eligible ones.
	 * @param target The instance in which to inject by setters
	 * @throws InjectionException An error has occured during the injection process,
	 *   at this or a nested level.
	 */
	private void injectBySetters(T target) throws InjectionException {
		for (final Method method : baseClass.getDeclaredMethods()) {	
			if (method.isAnnotationPresent(Inject.class)) {
				try {
					method.setAccessible(true);
					
					if (!isSetter(method)) {
						throw new InjectionException(
							String.format("@Inject annotated method %s is not a setter.", method.getName()));
					}
					
					Class<?> paramType = method.getParameterTypes()[0];
					Object value = this.inject(paramType);
					
					method.invoke(target, new Object[] { value });
				} catch (InjectionException e) {
					// Chaining exception in the upper call of the stack
					throw new InjectionException(
							String.format(NESTED_EXCEPTION, method.getName(), e), e);
				} catch (ReflectiveOperationException e) {
					throw new InjectionException(
							String.format(INJECTION_FAILED, method.getName()), e);
				} finally {
					method.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Scan all base class fields and realize injection on each eligible ones.
	 * @param target The instance in which to inject by fields
	 * @throws InjectionException An error has occured during the injection process,
	 *   at this or a nested level.
	 */
	private void injectByFields(T target) throws InjectionException {
		for (final Field field : baseClass.getDeclaredFields()) {	
			if (field.isAnnotationPresent(Inject.class)) {
				try {
					field.setAccessible(true);
					field.set(target, this.inject(field.getType()));
				} catch (InjectionException e) {
					// Chaining exception in the upper call of the stack
					throw new InjectionException(
							String.format(NESTED_EXCEPTION, field.getName(), e), e);
				} catch (ReflectiveOperationException e) {
					throw new InjectionException(
							String.format(INJECTION_FAILED, field.getName()), e);
				} finally {
					field.setAccessible(false);
				}
			}
		}
	}
	
	/**
	 * Find the first base class constructor with <code>@Inject</code> annotation.
	 * @return A instance of <code>Constructor&lt;T&gt;</code>, or null if none was found.
	 */
	@SuppressWarnings("unchecked")
	private Constructor<T> findInjectableConstructor() {
		
		for (Constructor<?> constructor : baseClass.getConstructors()) {
			if (constructor.getDeclaringClass().equals(baseClass) &&
					constructor.isAnnotationPresent(Inject.class)) {
				return (Constructor<T>) constructor;
				
			}
		}
		return null;
	}

	/**
	 * Indicate whether the given method is a setter.
	 * @param method The method to check
	 * @return
	 */
	private static boolean isSetter(Method method) {
		return method.getReturnType().equals(Void.TYPE) &&
				method.getParameterCount() == 1;
	}	
	
}
