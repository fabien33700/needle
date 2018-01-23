package org.needle.di;

/**
 * General behaviour for a builder, which is a class that build
 *   a typed T object, and possibly throws X
 *   exceptions.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 * @param <T> The type of the instance to build
 * @param <X> The type of the Exception that can be thrown during
 * 	the building process
 */
public interface Builder<T, X extends Exception> {
	
	/**
	 * Build an instance of T.
	 * @return The built instance
	 * @throws X The build process has thrown an exception
	 */
	T build() throws X;

}
