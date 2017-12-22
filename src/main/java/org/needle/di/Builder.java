package org.needle.di;

/**
 * General behaviour for a builder, that is a class that build
 *   a T object, and possibly raise exceptions.
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 * @param <T> The type of the instance to build
 * @param <X> The type of the Exception that can be raised during the building process
 */
public interface Builder<T, X extends Exception> {
	
	/**
	 * Build an instance of T
	 * @return
	 * @throws X The build process has thrown an exception
	 */
	T build() throws X;

}
