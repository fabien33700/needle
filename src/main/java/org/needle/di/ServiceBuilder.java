package org.needle.di;

import java.lang.reflect.Field;

public class ServiceBuilder<T> implements Builder<T, ReflectiveOperationException> {

	private Class<T> baseClass;

	public static <T> ServiceBuilder<T> instance(Class<T> baseClass) {
		return new ServiceBuilder<>(baseClass);
	}

	private ServiceBuilder(Class<T> baseClass) {
		this.baseClass = baseClass;
	}

	@Override
	public T build() throws ReflectiveOperationException {
		T target = (T) baseClass.newInstance();

		// Pour chaque champ de la classe de base
		for (final Field field : baseClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Inject.class) && 
					field.getType().isAnnotationPresent(Service.class)) {
				try {
					field.setAccessible(true);

					ServiceBuilder<?> nested = ServiceBuilder.instance(field.getType());
					Object value = nested.build();

					field.set(target, value);
				} catch (ReflectiveOperationException e) {
					// On propage l'exception au niveau supérieur de la
					// pile d'appel récursif
					throw e;
				} finally {
					// On rétablit l'état nominal de la représentation du champ
					// dans tous les cas
					field.setAccessible(false);
				}
			}
		}

		return target;
	}

	public Class<T> getBaseClass() {
		return baseClass;
	}

}
