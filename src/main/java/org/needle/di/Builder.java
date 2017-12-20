package org.needle.di;

public interface Builder<T, X extends Exception> {
	
	T build() throws X;
	
	Class<T> getBaseClass();

}
