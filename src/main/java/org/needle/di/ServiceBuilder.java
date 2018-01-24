package org.needle.di;

import org.needle.di.annotations.Inject;
import org.needle.di.annotations.Resolve;
import org.needle.di.annotations.Service;
import org.needle.di.exceptions.CyclicDependencyException;
import org.needle.di.exceptions.InjectionException;
import org.needle.di.exceptions.NestedInjectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

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
@SuppressWarnings("unused")
public class ServiceBuilder<T> implements Builder<T, InjectionException> {

    /**
     * Class that allows the developer to fill in the ServiceBuilder
     * configuration in a chained way.
     *
     * To configure a ServiceBuilder, use the method configure()
     * that returns the corresponding Configurator instance. The put() method
     * allows to add/change parameters in configuration. The method done() returns
     * a reference on the associated ServiceBuilder.
     * @see ServiceBuilder#configure()
     * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
     * @param <U> The type of the ServiceBuilder currently in configuration
     */
    static class Configurator<U> {

        /**
         * The builder currently in configuration.
         */
        private ServiceBuilder<U> builder;

        /**
         * Create a Configurator instance for the given builder.
         * This method is internal and should not be called directly.
         *
         * @param builder The builder instance to configure
         * @see ServiceBuilder#configure()
         */
        Configurator(ServiceBuilder<U> builder) {
            this.builder = builder;
        }

        /**
         * Create a Configurator instance for the given builder,
         * with the configuration parameters contained in the provided map
         * This method is internal and should not be called directly.
         *
         * @param builder The builder instance to configure
         * @param configuration The map that contains initial configuration.
         * @see ServiceBuilder#configure()
         */
        Configurator(ServiceBuilder<U> builder, Map<String, ?> configuration) {
            this(builder);
            builder.getConfiguration().putAll(configuration);
        }

        /**
         * Put a property in the configuration.
         * @param key The property key
         * @param value The property value
         * @return The current configurator
         */
        public Configurator<U> put(String key, Object value) {
            builder.getConfiguration().put(key, value);
            return this;
        }

        /**
         * Returns the builder that we are configuring.
         * @return The associated ServiceBuilder instance
         */
        public ServiceBuilder<U> done() {
            return builder;
        }
    }
	
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
	 * @param configuration The map that contains configuration
	 * @return The configurator for the ServiceBuilder<T>
	 */
	public Configurator<T> configure(Map<String, ?> configuration) {
		return new Configurator<>(this, configuration);
	}

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
		this.configuration = new HashMap<>();
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
	 * Try to resolve a property marked with @Resolve annotation
	 *   on a field, with the key contained in it, or with the memberName if 
	 *   no key was provided in the annotation use.
	 * @param field The field representation 
	 * @return The value of the property to resolve
	 * @throws InjectionException If the injector has no configuration property
	 *   with matching key.
	 */
	private Object resolve(Field field)
		throws InjectionException 	
	{
		final Resolve resolve = field.getAnnotation(Resolve.class);
		final String key = (!resolve.value().isEmpty()) ?
				resolve.value() : field.getName();

		if (!configuration.containsKey(key)) {
			throw new InjectionException(UNRESOLVABLE, field.getName(), key);
		}
		
		return configuration.get(key);
	}
	
	/**
	 * Try to resolve a property marked with @Resolve annotation
	 *   on a setter method, with the key contained in it, or with the memberName if 
	 *   no key was provided in the annotation use.
	 * @param setter The setter method representation 
	 * @return The value of the property to resolve
	 * @throws InjectionException If the injector has no configuration property
	 *   with matching key.
	 */	
	private Object resolve(Method setter)
			throws InjectionException 	
		{
			final String memberName = ReflectionUtils.getMemberNameFromSetter(setter.getName());
			final Resolve resolve = setter.getAnnotation(Resolve.class);
			final String key = (!resolve.value().isEmpty()) ?
					resolve.value() : memberName;

			if (!configuration.containsKey(key)) {
				throw new InjectionException(UNRESOLVABLE, memberName, key);
			}
			
			return configuration.get(key);
		}

	/**
	 * Find the first eligible injectable constructor, resolve dependencies and
	 *   call it.
	 * @throws InjectionException If an error occurred during the injection process
	 */
	private T injectByConstructor() throws InjectionException {
		T target;
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
			if (ReflectionUtils.hasOneAnnotation(method, Inject.class, Resolve.class)) {
				try {
					method.setAccessible(true);
					
					if (!ReflectionUtils.isSetter(baseClass, method)) {
						throw new InjectionException(NOT_A_SETTER, ReflectionUtils.describeMethod(method));
					}
					
					Class<?> paramType = method.getParameterTypes()[0];
					Object value = null;
					
					if (ReflectionUtils.hasOneAnnotation(method, Inject.class)) {
						value = inject(paramType);
					} else
					if (ReflectionUtils.hasOneAnnotation(method, Resolve.class)) {
						value = resolve(method);
					}
					
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
			if (ReflectionUtils.hasOneAnnotation(field, Inject.class, Resolve.class)) {
				try {
					field.setAccessible(true);
					if (ReflectionUtils.hasOneAnnotation(field, Inject.class)) {
						field.set(target, inject(field.getType()));
					} else 
					if (ReflectionUtils.hasOneAnnotation(field, Resolve.class)) {
						field.set(target, resolve(field));
					}
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
	 * Find the first base class constructor with @Inject annotation.
	 * 
	 * @return An instance of Constructor<T>, or null if none was found.
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
	 * Returns the ServiceBuilder configuration
	 * @return The configuration, contained in a Map.
	 */
	public Map<String, Object> getConfiguration() {
		return configuration;
	}	
	
}
