package org.needle.di;

import java.util.Map;

/**
 * <p>Class that allows the developper to fill in the <code>ServiceBuilder</code>
 * configuration in a chained way.</p>
 * 
 * <p>To configure a <code>ServiceBuilder</code>, use the method <code>configure()</code> 
 * that returns the corresponding <code>Configurator</code> instance. The put() method 
 * allows to add/change parameters in configuration. The method <code>done()</code> returns
 * a reference on the associated <code>ServiceBuilder</code>.</p>
 *  
 * @see ServiceBuilder#configure()
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 *
 * @param <U> The type of the <code>ServiceBuilder</code> currently in configuration
 */
public class Configurator<U> {
	
	/**
	 * The builder currently in configuration.
	 */
	private ServiceBuilder<U> builder;
	
	/**
	 * <p>Create a Configurator instance for the given builder.
	 * This method is internal and should not be called directly.</p>
	 * 
	 * @param builder The builder instance to configure
	 * @see ServiceBuilder#configure()
	 */
	Configurator(ServiceBuilder<U> builder) {
		this.builder = builder;
	}
	
	/**
	 * <p>Create a Configurator instance for the given builder,
	 * with the configuration parameters contained in the provided map
	 * This method is internal and should not be called directly.</p>
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
	 * @return The associated <code>ServiceBuilder</code> instance
	 */
	public ServiceBuilder<U> done() {
		return builder;
	}
}