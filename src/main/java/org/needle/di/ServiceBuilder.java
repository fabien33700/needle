package org.needle.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.needle.di.annotations.Inject;
import org.needle.di.annotations.Resolve;
import org.needle.di.annotations.Service;
import org.needle.di.errors.CyclicDependencyException;
import org.needle.di.errors.InjectionException;
import org.needle.di.errors.InjectionFailedException;
import org.needle.di.errors.InstanciationFailedException;
import org.needle.di.errors.NestedInjectionException;
import org.needle.di.errors.NotServiceException;
import org.needle.di.errors.NotSetterException;
import org.needle.di.errors.UnresolvablePropertyException;
import org.needle.di.utils.ReflectionUtils;


/**
 * <p>Builder class for building classes instances, resolving and injecting recursively 
 *   all dependencies instances.</p>
 * <p><code>ServiceBuilder&lt;T&gt;</code> class can be use for every <code>Class&lt;T&gt;</code>, provided that 
 *   it has <code>@Service</code> annotation on its definition.</p>
 * <p><code>ServiceBuilder&lt;T&gt;</code> scans,
 *   in this order, all constructors, setters and fields marked by the <code>@Inject</code> 
 *   annotation, and tries to build nested dependencies instances.</p>
 *   
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 * 
 * @param T the type of instance to build
 */
public class ServiceBuilder<T> implements Builder<T, InjectionException>, Configurable<String> {		
	
	/**
	 * The class of the instance we are attempting to build
	 */
	private Class<T> baseClass;
	
	/**
	 * The set of dependencies we have already built
	 */
	private Set<Class<?>> dependencies;
	
	/**
	 * The configuration of the injector 
	 */
	private Map<String, Object> configuration;
	
	/**
	 * Returns a Configurator instance for the current builder.
	 * @return The configurator for the ServiceBuilder<T>
	 */
	public Configurator<T> configure() {
		return new Configurator<>(this);
	}
	
	/**
	 * Returns a Configurator instance for the current builder,
	 *   filled with initial configuration provided in a Map.
	 * @param confguration The map that contains configuration
	 * @return The configurator for the ServiceBuilder<T>
	 */
	public Configurator<T> configure(Map<String, ?> configuration) {
		return new Configurator<>(this, configuration);
	}

	/**
	 * Returns an instance of a builder for the class <code>baseClass</code>
	 * @param <T> type of the instance to build
	 * @param baseClass Class of the instance to build
	 * @return The brand new builder of T instance
	 */
	public static <T> ServiceBuilder<T> instance(Class<T> baseClass) {
		return new ServiceBuilder<T>(baseClass);
	}

	/**
	 * Returns an instance of a builder for the class <code>baseClass</code>
	 * @param <T> type of the instance to build
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
		this.configuration = new HashMap<>();
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
		this.configuration = parent.getConfiguration();
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
	 * <p>Active method of the builder that examine the class <code>baseClass</code>, scan all 
	 *   its fields, setters and constructors and create the instance, with its dependencies resolved if possible.</p>
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
	 * <p>Inject an instance of the type <code>type</code> by instanciating
	 *   a ServiceBuilder on the class that will resolve recursively its dependencies.</p>
	 * @param type The Class that represents the type of service to inject
	 * @return The instance of <code>type</code>
	 * @throws InjectionException An error has occured during the injection process,
	 *   at this or a nested level.
	 */
	private Object inject(Class<?> type) throws InjectionException {		
		
		if (!type.isAnnotationPresent(Service.class)) {	
			throw new NotServiceException(type);
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
	 * <p>Try to resolve a property marked with <code>@Resolve</code> annotation
	 *   on a field, with the key contained in it, or with the memberName if 
	 *   no key was provided in the annotation use.</p>
	 * @param target The target object
	 * @param field The field representation 
	 * @return The value of the property to resolve
	 * @throws InjectionException If the injector has no configuration property
	 *   with matching key.
	 */
	private Object resolve(T target, Field field) 
		throws InjectionException 	
	{
		final Resolve resolve = field.getAnnotation(Resolve.class);
		final String key = (!resolve.value().isEmpty()) ?
				resolve.value() : field.getName();

		if (!configuration.containsKey(key)) {
			throw new UnresolvablePropertyException(field.getName(), key);
		}
		
		return configuration.get(key);
	}
	
	/**
	 * <p>Try to resolve a property marked with <code>@Resolve</code> annotation
	 *   on a setter method, with the key contained in it, or with the memberName if 
	 *   no key was provided in the annotation use.</p>
	 * @param target The target object
	 * @param setter The setter method representation 
	 * @return The value of the property to resolve
	 * @throws InjectionException If the injector has no configuration property
	 *   with matching key.
	 */	
	private Object resolve(T target, Method setter) 
			throws InjectionException 	
		{
			final String memberName = ReflectionUtils.getMemberNameFromSetter(setter);
			final Resolve resolve = setter.getAnnotation(Resolve.class);
			final String key = (!resolve.value().isEmpty()) ?
					resolve.value() : memberName;

			if (!configuration.containsKey(key)) {
				throw new UnresolvablePropertyException(memberName, key);
			}
			
			return configuration.get(key);
		}

	/**
	 * <p>Find the first eligible injectable constructor, resolve dependencies and 
	 *   call it.</p>
	 * @param target The instance in which to inject by constructor
	 * @throws InjectionException
	 */
	private T injectByConstructor() throws InjectionException {
		T target = null;
		Constructor<T> constructor = findInjectableConstructor();

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
				target = (T) constructor.newInstance(values.toArray());
			} else {
				// Constructs the instance with the empty constructor
				target = (T) baseClass.newInstance();
			}
		} catch (ReflectiveOperationException e) {
			throw new InstanciationFailedException(baseClass, e);
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
			if (ReflectionUtils.hasAnnotations(method, Inject.class, Resolve.class)) {
				try {
					method.setAccessible(true);
					
					if (!ReflectionUtils.isSetter(baseClass, method)) {
						throw new NotSetterException(method);
					}
					
					Class<?> paramType = method.getParameterTypes()[0];
					Object value = null;
					
					if (ReflectionUtils.hasAnnotations(method, Inject.class)) {
						value = inject(paramType);
					} else
					if (ReflectionUtils.hasAnnotations(method, Resolve.class)) {
						value = resolve(target, method);
					}
					
					method.invoke(target, new Object[] { value });
				} catch (InjectionException e) {
					// Chaining exception in the upper call of the stack
					throw new NestedInjectionException(method, e);
				} catch (ReflectiveOperationException e) {
					throw new InjectionFailedException(method, e);
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
			if (ReflectionUtils.hasAnnotations(field, Inject.class, Resolve.class)) {
				try {
					field.setAccessible(true);
					if (ReflectionUtils.hasAnnotations(field, Inject.class)) {
						field.set(target, inject(field.getType()));
					} else 
					if (ReflectionUtils.hasAnnotations(field, Resolve.class)) {
						field.set(target, resolve(target, field));
					}
				} catch (InjectionException e) {
					// Chaining exception in the upper call of the stack
					throw new NestedInjectionException(field, e);
				} catch (ReflectiveOperationException e) {
					throw new InjectionFailedException(field, e);
				} finally {
					field.setAccessible(false);
				}
			}
		}
	}
	
	/**
	 * <p>Find the first base class constructor with <code>@Inject</code> annotation.</p>
	 * 
	 * @return An instance of <code>Constructor&lt;T&gt;</code>, or null if none was found.
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
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getConfiguration() {
		return configuration;
	}	
	
}
