package org.needle.di;

import static org.needle.di.InjectionException.INJECTION_FAILED;
import static org.needle.di.InjectionException.INSTANCIATION_FAILED;
import static org.needle.di.InjectionException.NESTED_EXCEPTION;

import java.lang.reflect.Field;

/**
 * Classe permettant de réaliser l'injection de dépendances et d'instancier un objet de la classe
 * passée en paramètre.
 * On considère ici que seuls les champs sont injectables (<code>@Inject</code> autorisé sur <code>ElementType.FIELD</code>)
 * et que l'on ne gère pas les constructeurs avec paramètres.
 * @author Fabien
 *
 */
public class ServiceBuilder<T> implements Builder<T, InjectionException>{	
	
	private Class<T> baseClass;
	
	/**
	 * Créé un builder pour la classe <code>baseClass</code>
	 * @param baseClass La classe de l'objet à instancier
	 */
	private ServiceBuilder(Class<T> baseClass) {
		this.baseClass = baseClass;
	}

	/**
	 * Créer une instance d'un builder pour la classe <code>baseClass</code>
	 * @param baseClass La classe de l'objet à instancier
	 * @return
	 */
	public static <T> ServiceBuilder<T> instance(Class<T> baseClass) {
		return new ServiceBuilder<T>(baseClass);
	}
	
	/**
	 * Examine la classe <code>baseClass</code>, construit le graphe de dépendances
	 *   et retourne une instance du type de <code>baseClass</code> avec les dépendances
	 *   résolues dans la mesure du possible.
	 * @param <T> Le type représenté par la classe <code>baseClass</code> correspondant au type de l'objet retourné
	 * @throws InjectionException Une erreur lors du processus de construction de l'objet ou d'injection de dépendances a eu lieu
	 */
	public T build() throws InjectionException {
		T target;
		// On tente d'instancier la classe
		try {
			target = (T) baseClass.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new InjectionException(
					String.format(INSTANCIATION_FAILED, baseClass.getName()), e);
		}
		
		// Pour chaque champ de la classe de base
		for (final Field field : baseClass.getDeclaredFields()) {	
			if (isInjectable(field)) {
				try {
					field.setAccessible(true);
					
					ServiceBuilder<?> nested = ServiceBuilder.instance(field.getType());
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
	 * Indique si le champ est injectable (<code>@Inject</code> sur le champ
	 *   et <code>@Service</code> sur la classe représentant le type du champ).
	 * @param field Le champ à analyser
	 * @return
	 */
	private static <T> boolean isInjectable(Field field) {
		return field.getType().isAnnotationPresent(Service.class) &&
				field.isAnnotationPresent(Inject.class);		
	}
	
	/**
	 * Retourne la classe de base du builder
	 * @return
	 */
	@Override
	public Class<T> getBaseClass() {
		return baseClass;
	}	
	
}
