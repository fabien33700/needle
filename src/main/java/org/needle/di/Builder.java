package org.needle.di;

/**
 * <p>General behaviour for a builder, which is a class that build
 *   a typed <code>T</code> object, and possibly throws <code>X</code> 
 *   exceptions.</p>
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 * @param <T> The type of the instance to build
 * @param <X> The type of the <b>Exception</b> that can be thrown during 
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
