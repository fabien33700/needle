package org.needle.di;

import static org.needle.di.InjectionException.CYCLIC_DEPENDENCIES;
import static org.needle.di.InjectionException.INJECTION_FAILED;
import static org.needle.di.InjectionException.INSTANCIATION_FAILED;
import static org.needle.di.InjectionException.NESTED_EXCEPTION;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


/**
 * Builder class for building classes instances, resolve and inject recursively 
 *   all dependencies instances.<br/>
 * ServiceBuilder<T> class can be use for every <code>Class&lt;T&gt;</code>, provided that it has <code>@Service</code> annotation on
 *   its definition. ServiceBuilder<T> scan for all fields marked by the <code>@Inject</code> annotation,
 *   and tries to build nested dependencies instances.
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
	 * Active method of the builder that examine the class <code>baseClass</code>, scan all 
	 *   its fields and create the instance, with its dependencies resolved if possible.
	 * @param <T> Type of the class <code>baseClass</code> corresponding to the instance to create
	 * @throws InjectionException An error has occured during the instanciation or dependency injection process
	 */
	public T build() throws InjectionException {
		T target;
		try {
			target = (T) baseClass.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new InjectionException(
					String.format(INSTANCIATION_FAILED, baseClass.getName()), e);
		}
		
		for (final Field field : baseClass.getDeclaredFields()) {	
			if (isInjectable(field)) {
				try {
					field.setAccessible(true);
					
					// La classe du champ courant a déjà été examiné -> cycle détecté
					if (!dependencies.add(field.getType())) {
						throw new InjectionException(
								String.format(CYCLIC_DEPENDENCIES, field.getType().getName()));
					}
					
					ServiceBuilder<?> nested = ServiceBuilder.instance(field.getType(), this);
					Object value = nested.build();
				
					field.set(target, value);
					
				} catch (ReflectiveOperationException e) {
					throw new InjectionException(
							String.format(INJECTION_FAILED, field.getName()), e);
				} catch (InjectionException e) {
					// On encapsule l'éventuelle exception de l'appel récursif 
					// dans une nouvelle exception et on la relance (chaîne d'exceptions)
					throw new InjectionException(
							String.format(NESTED_EXCEPTION, field.getName(), e), e);
				}
				finally {
					// On rétablit l'état nominal de la représentation du champ dans tous les cas
					field.setAccessible(false);
				}
			}
			
		}
	

		return target;
	}	

	/**
	 * Indicate if the given field is injectable (<code>@Inject</code> annotation on the field
	 *   and <code>@Service</code> one on the field type representing class).
	 * @param field Field to check
	 * @return
	 */
	private static <T> boolean isInjectable(Field field) {
		return field.getType().isAnnotationPresent(Service.class) &&
				field.isAnnotationPresent(Inject.class);		
	}
	
	/**
	 * Returns builder base class.
	 * @return
	 */
	public Class<T> getBaseClass() {
		return baseClass;
	}	
	
}
