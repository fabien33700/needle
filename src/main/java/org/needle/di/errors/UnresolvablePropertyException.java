package org.needle.di.errors;

/**
 * An exception thrown when a field marked with <code>@Resolve</code> 
 *   
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 */
public class UnresolvablePropertyException extends InjectionException {

	private static final long serialVersionUID = -5937336155684153809L;

	/**
	 * Constructs an UnresolvablePropertyException instance from the 
	 * problematic method reflective representation.
	 * @param name The name of the problematic member (field, setter, parameter)
	 * @param key 
	 */
	public UnresolvablePropertyException(String name, String key) {
		super(String.format(UNRESOLVABLE_F, name, key));
	}

}
