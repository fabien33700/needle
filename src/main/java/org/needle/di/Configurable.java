package org.needle.di;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>General behaviour for a class that provide 
 * a key-value based configuration.</p>
 *
 * @author fabien33700 <code>&lt;fabien.lehouedec@gmail.com&gt;</code>
 * @param <K> The type of the key used by the property store
 */
public interface Configurable<K extends Serializable> {
	/**
	 * Returns the configuration map of the configurable object.
	 * @return The configuration map
	 */
	Map<K, ?> getConfiguration();
}
