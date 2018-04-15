package org.needle.di;

import org.needle.di.annotations.Inject;
import org.needle.di.annotations.Service;
import org.needle.di.exceptions.CyclicDependencyException;
import org.needle.di.exceptions.InjectionException;
import org.needle.di.exceptions.NestedInjectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.needle.di.ReflectionUtils.hasOneAnnotation;
import static org.needle.di.exceptions.InjectionException.*;

/**
 * Builder class for building classes instances, resolving and injecting recursively
 *   all dependencies instances.
 * ServiceBuilder<T> class can be use for every Class<T>, provided that 
 *   it has @Service annotation on its definition.
 * ServiceBuilder<T> scans, in this order, all constructors, setters and fields marked by the @Inject
 *   annotation, and tries to build nested dependencies instances.
 * @param <T> The type of the class built by the ServiceBuilder
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
public class ServiceBuilder<T> implements Builder<T, InjectionException> {
	
	/**
	 * The class of the instance we are attempting to build
	 */
	private Class<T> baseClass;
	
	/**
	 * The set of dependencies we have already built
	 */
	private Set<Class<?>> dependencies;

	/**
	 * Returns an instance of a builder for the class baseClass
	 * @param <T> type of the instance to build
	 * @param baseClass Class of the instance to build
	 * @return The brand new builder of T instance
	 */
	public static <T> ServiceBuilder<T> instance(Class<T> baseClass) {
		return new ServiceBuilder<>(baseClass);
	}

	/**
	 * Returns an instance of a builder for the class baseClass
	 * @param <T> type of the instance to build
	 * @param baseClass Class of the instance to build
	 * @param parent The parent ServiceBuilder
	 * @return The ServiceBuilder instance
	 */
	private static <T> ServiceBuilder<T> instance(
			Class<T> baseClass, ServiceBuilder<?> parent) {
		return new ServiceBuilder<>(baseClass, parent);
	}
	
	/**
	 * Create a builder for the class baseClass
	 * @param baseClass Class of the instance to build
	 */
	private ServiceBuilder(Class<T> baseClass) {
		this.baseClass = baseClass;
		this.dependencies = new HashSet<>();
	}
	
	/**
	 * Create a builder for the class baseClass,
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
	 * Returns the builder base class, which is the class on which 
	 *   builder operates.
	 * @return The Builder base class
	 */
	public Class<T> getBaseClass() {
		return baseClass;
	}

	/**
	 * Active method of the builder that examine the class baseClass, scan all
	 *   its fields, setters and constructors and create the instance, with its dependencies resolved if possible.
	 * @throws InjectionException An error has occurred during the instantiation or dependency injection process
	 */
	public T build() throws InjectionException {
		T target = this.injectByConstructor();
		this.injectBySetters(target);		
		this.injectByFields(target);
	
		return target;
	}

	/**
	 * Inject an instance of the type type by instantiating
	 *   a ServiceBuilder on the class that will resolve recursively its dependencies.
	 * @param type The Class that represents the type of service to inject
	 * @return The instance of type
	 * @throws InjectionException An error has occurred during the injection process,
	 *   at this or a nested level.
	 */
	private Object inject(Class<?> type) throws InjectionException {		
		
		if (!type.isAnnotationPresent(Service.class)) {	
			throw new InjectionException(NOT_A_SERVICE, type.getName());
		}
		
		// Class already proceeded, cycle detected
		if (!dependencies.add(type)) {
			throw new CyclicDependencyException(type, dependencies);
		}
		
		return ServiceBuilder
				.instance(type, this)
				.build();
	}

	/**
	 * Find the first eligible injectable constructor, resolve dependencies and
	 *   call it.
	 * @throws InjectionException If an error occurred during the injection process
	 */
	private T injectByConstructor() throws InjectionException {
		T target;

		Constructor<T> constructor = findInjectableConstructor(baseClass);

		try {
			if (constructor != null) {
				List<Object> values = new ArrayList<>();
				
				for (int i = 0, n = constructor.getParameterCount(); i < n; i++) {
					Parameter param = constructor.getParameters()[i];
					
					try {
						Object value = inject(param.getType());
						values.add(value);
					} catch (InjectionException e) {
						// Chaining exception in the upper call of the stack
						throw new NestedInjectionException(i, constructor, e);
					}
				}
				
				// Constructs the instance with the matching injectable constructor
				target = constructor.newInstance(values.toArray());
			} else {
				// Constructs the instance with the empty constructor
				target = baseClass.newInstance();
			}
		} catch (ReflectiveOperationException cause) {
		    throw new InjectionException(cause, INSTANTIATION_FAILED, baseClass.getName());
		}
		return target;
	}
	
	/**
	 * Scan all base class methods, gets setters and realize injection on each eligible ones.
	 * @param target The instance in which to inject by setters
	 * @throws InjectionException An error has occurred during the injection process,
	 *   at this or a nested level.
	 */
	private void injectBySetters(T target) throws InjectionException {
		for (final Method method : baseClass.getDeclaredMethods()) {	
			if (hasOneAnnotation(method, Inject.class)) {
				try {
					method.setAccessible(true);
					
					if (!ReflectionUtils.isSetter(baseClass, method)) {
						throw new InjectionException(NOT_A_SETTER, ReflectionUtils.describeMethod(method));
					}
					
					Class<?> paramType = method.getParameterTypes()[0];
					Object value = inject(paramType);
					method.invoke(target, value);
				} catch (InjectionException e) {
					// Chaining exception in the upper call of the stack
					throw new NestedInjectionException(method, e);
				} catch (ReflectiveOperationException cause) {
				    throw new InjectionException(cause, INJECTION_FAILED,
                            ReflectionUtils.getMemberNameFromSetter(method.getName()));
				} finally {
					method.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Scan all base class fields and realize injection on each eligible ones.
	 * @param target The instance in which to inject by fields
	 * @throws InjectionException An error has occurred during the injection process,
	 *   at this or a nested level.
	 */
	private void injectByFields(T target) throws InjectionException {
		
		for (final Field field : baseClass.getDeclaredFields()) {	
			if (hasOneAnnotation(field, Inject.class)) {
				try {
					field.setAccessible(true);
					field.set(target, inject(field.getType()));
				} catch (InjectionException e) {
					// Chaining exception in the upper call of the stack
					throw new NestedInjectionException(field, e);
				} catch (ReflectiveOperationException cause) {
				    throw new InjectionException(cause, INSTANTIATION_FAILED, field.getName());
				} finally {
					field.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Find the first constructor of the class argument with @Inject annotation.
	 * @return An instance of Constructor<T>, or null if none was found.
	 */
    @SuppressWarnings("unchecked")
	private static <T> Constructor<T> findInjectableConstructor(Class<T> clazz) {
        return (Constructor<T>) Stream.of(clazz.getConstructors())
                .filter(cstr -> cstr.getDeclaringClass().equals(clazz))
                .filter(cstr -> cstr.isAnnotationPresent(Inject.class))
                .findFirst().orElse(null);
    }
}
